package fr.streampi.plugin;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.module.Configuration;
import java.lang.module.ModuleFinder;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

/**
 * 
 * this class provides an easy way to create plugins in any projects <br>
 * this class just needs a list of {@link Path} where the jars are stored.
 * <p>
 * these paths could either be folders where jars are stored or jar files
 * <p>
 * in these jar file the only thing required is to have objects extending or
 * implementing <T> and described with annotation <A> so the registerer can
 * automatically find them <br>
 * once loaded, you can retrieve all the loaded classes with
 * {@link PluginRegisterer#getRegisteredObjects(boolean)} or
 * {@link PluginRegisterer#getRegisteredObjects()}
 * 
 * @author Romain LM
 *
 * @param <T> the {@link PluginRegisterer#classType} the plugin objects will be
 *            extending
 * @param <A> the {@link PluginRegisterer#annotationType} the plugin objects
 *            will possess this should contain infos about the type <T>
 * 
 * 
 */
public class PluginRegisterer<T, A extends Annotation> {

	/**
	 * the paths the jar will be fetched. It can either be a jar file or a folder
	 * containing jars
	 */
	private Path[] paths = new Path[0];
	private ModuleLayer parentLayer;

	/**
	 * the class or interface that will be extended in plugin
	 */
	private Class<T> classType;

	/**
	 * the annotation that describes the plugin <T> object
	 */
	private Class<A> annotationType;

	/**
	 * 
	 * @param <T>            the {@link PluginRegisterer#classType} to set
	 * @param <A>            the {@link PluginRegisterer#annotationType} to set
	 * @param classType      the {@link PluginRegisterer#classType} to set
	 * @param annotationType the {@link PluginRegisterer#annotationType} to set
	 * @param paths          the {@link PluginRegisterer#paths} to set
	 * @return new instance of {@link PluginRegisterer} through constructor
	 *         {@link PluginRegisterer#PluginRegisterer(classType, annotationType, paths)}
	 * 
	 */
	public static <T, A extends Annotation> PluginRegisterer<T, A> getPluginRegisterer(Class<T> classType,
			Class<A> annotationType, Path... paths) {
		PluginRegisterer<T, A> pr = new PluginRegisterer<>(classType, annotationType, paths);
		return pr;
	}

	/**
	 * 
	 * @param <T>            the {@link PluginRegisterer#classType} to set
	 * @param <A>            the {@link PluginRegisterer#annotationType} to set
	 * @param classType      the {@link PluginRegisterer#classType} to set
	 * @param annotationType the {@link PluginRegisterer#annotationType} to set
	 * @return new instance of {@link PluginRegisterer} through static method
	 *         {@link PluginRegisterer#getPluginRegisterer(classType,
	 *         annotationType, new {@link java.nio.Path}[0])
	 * 
	 */
	public static <T, A extends Annotation> PluginRegisterer<T, A> getPluginRegisterer(Class<T> classType,
			Class<A> annotationType) {
		return getPluginRegisterer(classType, annotationType, new Path[0]);
	}

	/**
	 * 
	 * @param classType      the {@link PluginRegisterer#classType} to set
	 * @param annotationType the {@link PluginRegisterer#annotationType} to set
	 * @param paths          the {@link PluginRegisterer#paths} to set
	 */
	private PluginRegisterer(Class<T> classType, Class<A> annotationType, Path... paths) {
		this.paths = paths;
	}

	/**
	 * 
	 * @param paths the {@link PluginRegisterer#paths} to set
	 * 
	 */
	public void setPaths(Path... paths) {
		this.paths = paths;
	}

	/**
	 * 
	 * @param inheritance if {@code true} then any object whose super class is
	 *                    extending {@linkplain PluginRegisterer#classType} will be
	 *                    registered <br>
	 *                    if {@code false} then only classes directly extending or
	 *                    implementing {@linkplain PluginRegisterer#classType} will
	 *                    be registered
	 * 
	 * @return all the registered objects
	 */
	public Map<A, Class<? extends T>> getRegisteredObjects(boolean inheritance) {
		parentLayer = ModuleLayer.boot();
		Map<A, Class<? extends T>> result = new HashMap<>();
		for (Path path : paths) {
			for (Entry<File, JarFile> e : listJars(path.toFile()).entrySet()) {
				result.putAll(loadModule(e.getKey(), e.getValue(), inheritance));
			}
		}

		return result;
	}

	/**
	 * 
	 * @return the registered objects with inheritance set to {@code true}
	 *         {@link PluginRegisterer#getRegisteredObjects(inheritance)}
	 */
	public Map<A, Class<? extends T>> getRegisteredObjects() {
		return getRegisteredObjects(true);
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

	/**
	 * 
	 * @param file        the jar system file
	 * @param jarFile     a jar image of {@linkplain file}
	 * @param inheritance if {@code true} then any object whose super class is
	 *                    extending {@linkplain PluginRegisterer#classType} will be
	 *                    registered <br>
	 *                    if {@code false} then only classes directly extending or
	 *                    implementing {@linkplain PluginRegisterer#classType} will
	 *                    be registered
	 * @return all registered objects in modules contained in jar file
	 */
	@SuppressWarnings("unchecked")
	private Map<A, Class<? extends T>> loadModule(File file, JarFile jarFile, boolean inheritance) {
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
								if (Arrays.asList(clazz.getInterfaces()).contains(classType)) {
									return clazz.getAnnotation(annotationType) != null;
								} else {
									return false;
								}
							} else if (inheritance) {
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
										&& clazz.getAnnotation(annotationType) != null;
							}
						} catch (ClassNotFoundException e) {
							e.printStackTrace();
							return false;
						}
					}).map(className -> {
						try {
							return (Class<? extends T>) loader.loadClass(className);
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

}
