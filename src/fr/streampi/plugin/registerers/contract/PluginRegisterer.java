package fr.streampi.plugin.registerers.contract;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;

public interface PluginRegisterer<T> {

	/**
	 * 
	 * @return all the classes for found objects
	 */
	public Collection<Class<? extends T>> getMappedClasses();

	/**
	 * 
	 * @param params the params that will be passed to the constructor of the class.
	 * if <T> parameter type is an interface then leave params empty. 
	 * @return instance collection for every found class
	 * @throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException when instantiating all the Plugin Objects
	 */
	public Collection<? extends T> getMappedObjects(Object... params) throws InstantiationException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException;

}
