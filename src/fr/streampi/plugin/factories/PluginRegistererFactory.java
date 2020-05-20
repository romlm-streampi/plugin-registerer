package fr.streampi.plugin.factories;

import java.lang.annotation.Annotation;
import java.nio.file.Path;

import fr.streampi.plugin.registerers.contract.AnnotatedPluginRegisterer;
import fr.streampi.plugin.registerers.contract.PluginRegisterer;
import fr.streampi.plugin.registerers.impl.AnnotatedPluginRegistererImpl;
import fr.streampi.plugin.registerers.impl.PluginRegistererImpl;

public final class PluginRegistererFactory {

	public static <T> PluginRegisterer<T> getRegisterer(Class<T> classType, boolean inherit, ModuleLayer parentLayer,
			Path... paths) {
		return new PluginRegistererImpl<>(classType, inherit, parentLayer, paths);
	}

	public static <T> PluginRegisterer<T> getRegisterer(Class<T> classType, Path... paths) {
		return new PluginRegistererImpl<>(classType, paths);
	}

	public static <T, A extends Annotation> AnnotatedPluginRegisterer<T, A> getAnnotatedRegisterer(Class<T> classType,
			Class<A> annotationType, boolean inherit, ModuleLayer parentLayer, Path... paths) {
		return new AnnotatedPluginRegistererImpl<>(classType, annotationType, inherit, parentLayer, paths);

	}

	public static <T, A extends Annotation> AnnotatedPluginRegisterer<T, A> getAnnotatedRegisterer(Class<T> classType,
			Class<A> annotationType, Path... paths) {
		return new AnnotatedPluginRegistererImpl<>(classType, annotationType, paths);

	}

}
