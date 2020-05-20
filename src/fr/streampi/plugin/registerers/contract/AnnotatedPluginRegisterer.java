package fr.streampi.plugin.registerers.contract;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

public interface AnnotatedPluginRegisterer<T, A extends Annotation> {

	public Map<A, T> getMappedObjects(Object... params) throws InstantiationException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException;

	public Map<A, Class<T>> getMappedClasses();

}
