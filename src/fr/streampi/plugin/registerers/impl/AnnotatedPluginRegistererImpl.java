package fr.streampi.plugin.registerers.impl;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.module.Configuration;
import java.lang.module.ModuleFinder;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

import fr.streampi.plugin.registerers.contract.AnnotatedPluginRegisterer;

public class AnnotatedPluginRegistererImpl<T, A extends Annotation> implements AnnotatedPluginRegisterer<T, A> {

	private Class<T> classType;
	private Class<A> annotationType;
	private Path[] paths;
	private ModuleLayer parentLayer;
	private boolean inherit = true;

	public AnnotatedPluginRegistererImpl(Class<T> classType, Class<A> annotationType, Path... paths) {
		this(classType, annotationType, true, ModuleLayer.boot(), paths);
	}

	public AnnotatedPluginRegistererImpl(Class<T> classType, Class<A> annotationType, boolean inherit,
			ModuleLayer parentLayer, Path... paths) {
		this.classType = classType;
		this.annotationType = annotationType;
		this.parentLayer = parentLayer;
		this.paths = paths;
		this.inherit = inherit;
	}

	public void setPaths(Path... paths) {
		this.paths = paths;
	}

	public void setInherit(boolean inherit) {
		this.inherit = inherit;
	}

	private Map<File, JarFile> listJars(File file) {
		Map<File, JarFile> result = new HashMap<>();
		if (!file.exists()) {
			return result;
		}
		if (file.isDirectory()) {
			for (File f : file.listFiles((FileFilter) (path) -> path.getName().endsWith(".jar"))) {
				try {
					result.put(f, new JarFile(f));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} else if (file.getName().endsWith(".jar")) {
			try {
				result.put(file, new JarFile(file));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return result;
	}

	@SuppressWarnings("unchecked")
	private Map<A, Class<? extends T>> loadModule(File file, JarFile jarFile) {
		ModuleFinder finder = ModuleFinder.of(file.toPath());
		List<String> names = finder.findAll().stream().map(x -> x.descriptor().name()).collect(Collectors.toList());

		Configuration cf = parentLayer.configuration().resolve(finder, ModuleFinder.of(), names);

		ModuleLayer layer = parentLayer.defineModulesWithOneLoader(cf, ClassLoader.getSystemClassLoader());
		Map<A, Class<? extends T>> result = new HashMap<>();
		for (String name : names) {
			ClassLoader loader = layer.findLoader(name);
			result.putAll(jarFile.stream().filter(entry -> {
				if (!entry.getName().endsWith(".class")) {
					return false;
				}
				if (entry.getName().contains("module-info"))
					return false;
				return true;
			}).map(entry -> entry.getName().subSequence(0, entry.getName().lastIndexOf('.')).toString().replaceAll("/",
					".")).filter(className -> {
						try {
							Class<?> clazz = loader.loadClass(className);
							if (classType.isInterface()) {
								return clazz.isAnnotationPresent(annotationType)
										&& Arrays.asList(clazz.getInterfaces()).contains(classType);
							} else if (this.inherit) {
								boolean isInstance = false;
								Class<? extends Object> currentClass = clazz;

								while (!(currentClass = currentClass.getSuperclass()).equals(Object.class)) {
									if (currentClass.equals(classType)) {
										isInstance = true;
										break;
									}

								}

								return isInstance && clazz.getAnnotation(annotationType) != null;
							} else {
								return clazz.getSuperclass().equals(classType)
										&& clazz.isAnnotationPresent(annotationType);
							}
						} catch (ClassNotFoundException e) {
							e.printStackTrace();
							return false;
						}
					}).map(className -> {
						try {
							return (Class<T>) loader.loadClass(className);
						} catch (ClassNotFoundException e) {
							e.printStackTrace();
						}
						return null;
					}).collect(Collectors.toMap((clazz -> {
						return (A) ((Class<? extends T>) clazz).getAnnotation(annotationType);
					}), Function.identity())));
		}

		return result;
	}

	@Override
	public Map<A, ? extends T> getMappedObjects(Object... params) throws InstantiationException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		Map<A, T> result = new HashMap<>();

		if (params.length > 0) {
			for (Path path : paths) {
				for (Entry<File, JarFile> e : listJars(path.toFile()).entrySet()) {
					for (Entry<A, Class<? extends T>> entry : loadModule(e.getKey(), e.getValue()).entrySet()) {
						result.put(entry.getKey(),
								entry.getValue()
										.getDeclaredConstructor(Arrays.asList(params).stream().map(x -> x.getClass())
												.collect(Collectors.toList()).toArray((Class<?>[]) new Class[0]))
										.newInstance(params));
					}
				}
			}
		} else {
			for (Path path : paths) {
				for (Entry<File, JarFile> e : listJars(path.toFile()).entrySet()) {
					for (Entry<A, Class<? extends T>> entry : loadModule(e.getKey(), e.getValue()).entrySet()) {
						result.put(entry.getKey(), entry.getValue().getDeclaredConstructor().newInstance());
					}
				}
			}
		}

		return result;
	}

	@Override
	public Map<A, Class<? extends T>> getMappedClasses() {
		Map<A, Class<? extends T>> result = new HashMap<>();
		for (Path path : paths) {
			for (Entry<File, JarFile> e : listJars(path.toFile()).entrySet()) {
				result.putAll(loadModule(e.getKey(), e.getValue()));
			}
		}

		return result;
	}

}
