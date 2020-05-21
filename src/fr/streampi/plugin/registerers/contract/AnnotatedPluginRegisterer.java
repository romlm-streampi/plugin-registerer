package fr.streampi.plugin.registerers.contract;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

public interface AnnotatedPluginRegisterer<T, A extends Annotation> {

	/**
	 * 
	 * @param params the params that will be passed to the constructor of the class.
	 * if <T> parameter type is an interface then leave params empty. 
	 * @return instance Map for every found class and the <A> annotation as key for each instance
	 * @throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException when instantiating all the Plugin Objects
	 */
	public Map<A, ? extends T> getMappedObjects(Object... params) throws InstantiationException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException;

	/**
	 * 
	 * @return all the classes for found objects as values and <A> annotation instance as the key
	 */
	public Map<A, Class<? extends T>> getMappedClasses();

}
