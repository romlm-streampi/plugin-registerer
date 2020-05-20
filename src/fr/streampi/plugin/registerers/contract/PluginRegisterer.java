package fr.streampi.plugin.registerers.contract;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;

public interface PluginRegisterer<T> {

	public Collection<Class<? extends T>> getMappedClasses();

	public Collection<? extends T> getMappedObjects(Object... params) throws InstantiationException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException;

}
