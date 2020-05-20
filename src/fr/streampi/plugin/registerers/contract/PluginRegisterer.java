package fr.streampi.plugin.registerers.contract;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;

public interface PluginRegisterer<T> {

	public Collection<Class<T>> getMappedClasses();

	public Collection<T> getMappedObjects(Object... params) throws InstantiationException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException;

}
