package fr.streampi.plugin.factories;

import java.lang.annotation.Annotation;
import java.nio.file.Path;

import fr.streampi.plugin.registerers.contract.AnnotatedPluginRegisterer;
import fr.streampi.plugin.registerers.contract.PluginRegisterer;
import fr.streampi.plugin.registerers.impl.AnnotatedPluginRegistererImpl;
import fr.streampi.plugin.registerers.impl.PluginRegistererImpl;


/**
 * this class is an implementation provider for PluginRegisterer project
 * 
 * @author R LM
 *
 */
public final class PluginRegistererFactory {

	/**
	 * 
	 * @param <T> the classType
	 * @param classType the classType of the {@link PluginRegisterer}
	 * @param inherit the inherit of the {@link PluginRegisterer}
	 * <br> if true, and <code>classType</code> is not an interface then it will add every class that extends <T> <code>classType</code> directly or indirectly
	 * <br> if false, it will only select classes directly extending <T> <code>classType</code>
	 * <br> default value is set to <code>true</code>  
	 * @param parentLayer the base {@link ModuleLayer}
	 * <br> default value is set to <code>ModuleLayer.boot();</code> 
	 * @param paths the paths where jars or jar folders may be parsed
	 * @return {@linkplain PluginRegisterer} implementation
	 */
	public static <T> PluginRegisterer<T> getRegisterer(Class<T> classType, boolean inherit, ModuleLayer parentLayer,
			Path... paths) {
		return new PluginRegistererImpl<>(classType, inherit, parentLayer, paths);
	}

	/**
	 * @param <T> the classType
	 * @param classType the classType of the {@link PluginRegisterer}
	 * @param paths the paths where jars or jar folders may be parsed
	 * 
	 * @return {@linkplain PluginRegistererFactory#getRegisterer(<code>classType</code>, <code>true</code>, <code>ModuleLayer.boot()</code>, <code>paths</code>)}
	 */
	public static <T> PluginRegisterer<T> getRegisterer(Class<T> classType, Path... paths) {
		return new PluginRegistererImpl<>(classType, paths);
	}

	/**
	 * 
	 * @param <T> the classType
	 * @param <A> the annotationType
	 * @param classType the classType of the {@link PluginRegisterer}
	 * @param annotationType the annotationType of the {@link PluginRegisterer}
	 * @param inherit the inherit of the {@link PluginRegisterer}
	 * <br> if true, and <code>classType</code> is not an interface then it will add every class that extends <T> <code>classType</code> directly or indirectly
	 * <br> if false, it will only select classes directly extending <T> <code>classType</code>
	 * <br> default value is set to <code>true</code>  
	 * @param parentLayer the base {@link ModuleLayer}
	 * <br> default value is set to <code>ModuleLayer.boot();</code> 
	 * @param paths the paths where jars or jar folders may be parsed
	 * @return {@linkplain AnnotatedPluginRegisterer} implementation
	 */
	public static <T, A extends Annotation> AnnotatedPluginRegisterer<T, A> getAnnotatedRegisterer(Class<T> classType,
			Class<A> annotationType, boolean inherit, ModuleLayer parentLayer, Path... paths) {
		return new AnnotatedPluginRegistererImpl<>(classType, annotationType, inherit, parentLayer, paths);

	}

	/**
	 * 
	 * @param <T> the classType
	 * @param <A> the annotationType
	 * @param classType the classType of the {@link PluginRegisterer}
	 * @param annotationType the annotationType of the {@link PluginRegisterer}
	 * @param paths the paths where jars or jar folders may be parsed
	 * @return {@linkplain PluginRegistererFactory#getAnotatedRegisterer(<code>classType</code>, <code>annotationType</code>, <code>true</code>, <code>ModuleLayer.boot()</code>, <code>paths</code>)}
	 */
	public static <T, A extends Annotation> AnnotatedPluginRegisterer<T, A> getAnnotatedRegisterer(Class<T> classType,
			Class<A> annotationType, Path... paths) {
		return new AnnotatedPluginRegistererImpl<>(classType, annotationType, paths);

	}

}
