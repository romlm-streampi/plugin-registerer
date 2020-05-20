package fr.streampi.plugin.registerers.impl;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.lang.module.Configuration;
import java.lang.module.ModuleFinder;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

import fr.streampi.plugin.registerers.contract.PluginRegisterer;

public class PluginRegistererImpl<T> implements PluginRegisterer<T> {

	private ModuleLayer parentLayer;
	private Class<T> classType;
	private boolean inherit = true;
	private Path[] paths;

	public PluginRegistererImpl(Class<T> classType, Path... paths) {
		this(classType, true, ModuleLayer.boot(), paths);
	}

	public PluginRegistererImpl(Class<T> classType, boolean inherit, ModuleLayer parentLayer, Path... paths) {
		this.classType = classType;
		this.parentLayer = parentLayer;
		this.paths = paths;
		this.inherit = inherit;
	}

	@SuppressWarnings("unchecked")
	private Collection<Class<T>> loadModule(File file, JarFile jarFile) {
		ModuleFinder finder = ModuleFinder.of(file.toPath());
		List<String> names = finder.findAll().stream().map(x -> x.descriptor().name()).collect(Collectors.toList());

		Configuration cf = parentLayer.configuration().resolve(finder, ModuleFinder.of(), names);

		ModuleLayer layer = parentLayer.defineModulesWithOneLoader(cf, ClassLoader.getSystemClassLoader());
		Collection<Class<T>> result = new ArrayList<>();
		for (String name : names) {
			ClassLoader loader = layer.findLoader(name);
			result.addAll(jarFile.stream().filter(entry -> {
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
								return Arrays.asList(clazz.getInterfaces()).contains(classType);
							} else if (this.inherit) {
								Class<? extends Object> currentClass = clazz;

								while (!(currentClass = currentClass.getSuperclass()).equals(Object.class)) {
									if (currentClass.equals(classType)) {
										return true;
									}

								}
								return false;
							} else {
								return clazz.getSuperclass().equals(classType);
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
					}).collect(Collectors.toList()));
		}

		return result;
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

	@Override
	public Collection<Class<T>> getMappedClasses() {
		List<Class<T>> result = new ArrayList<>();
		for (Path path : paths) {
			for (Entry<File, JarFile> e : listJars(path.toFile()).entrySet()) {
				result.addAll(loadModule(e.getKey(), e.getValue()));
			}
		}

		return result;
	}

	@Override
	public Collection<T> getMappedObjects(Object... params) throws InstantiationException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		List<T> result = new ArrayList<>();

		if (params.length > 0) {
			for (Path path : paths) {
				for (Entry<File, JarFile> e : listJars(path.toFile()).entrySet()) {
					for (Class<T> clazz : loadModule(e.getKey(), e.getValue())) {
						result.add(clazz
								.getDeclaredConstructor(Arrays.asList(params).stream().map(x -> x.getClass())
										.collect(Collectors.toList()).toArray((Class<?>[]) new Class[0]))
								.newInstance(params));
					}
				}
			}
		} else {
			for (Path path : paths) {
				for (Entry<File, JarFile> e : listJars(path.toFile()).entrySet()) {
					for (Class<T> clazz : loadModule(e.getKey(), e.getValue())) {
						result.add(clazz.getDeclaredConstructor().newInstance());
					}
				}
			}
		}

		return result;
	}

}
