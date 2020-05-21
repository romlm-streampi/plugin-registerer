package demo.root;

import fr.streampi.plugin.factories.PluginRegistererFactory;
import fr.streampi.plugin.registerers.contract.AnnotatedPluginRegisterer;

import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.io.File;
import java.lang.reflect.InvocationTargetException;

public class MainApp {

	public static void main(String... args) {

		// this file should be a jar repo for your plugins or just a jar file
		File rootFolder = new File("any path you want");
		testAnnotatedPluginClass(rootFolder);
		testPluginInterface(rootFolder);
	}

	public static void testAnnotatedPluginClass(File rootFolder) {
		AnnotatedPluginRegisterer<? extends PluginClass, PluginAnnotation> registerer = PluginRegistererFactory.getAnnotatedRegisterer(PluginClass.class, PluginAnnotation.class, paths)

		try {
			Map<PluginAnnotation, ? extends PluginClass> objects = registerer.getMappedObjects();
			objects.values().forEach(obj -> obj.executeAny());
			// outputs "executed PluginClassImpl plugin"
			
		} catch(InstantiationException e) {
			e.printStackTrace();
		} catch(IllegalAccessException e) {
			e.printStackTrace();
		} catch(IllegalArgumentException e) {
			e.printStackTrace();
		} catch(InvocationTargetException e) {
			e.printStackTrace();
		} catch(NoSuchMethodException e) {
			e.printStackTrace();
		} catch(SecurityException e) {
			e.printStackTrace();
		}
	}

	public static void testPluginInterface(File rootFolder) {
		PluginRegisterer<? extends PluginInterface> registerer = PluginRegistererFactory.getRegisterer(PluginInterface.class, rootFolder.toPath());

		try {
			List<? extends PluginClass> objects = new ArrayList<>(registerer.getMappedObjects());
			objects.forEach(obj -> obj.executeAny());
			// outputs "overrode plugin interface"
			
		} catch(InstantiationException e) {
			e.printStackTrace();
		} catch(IllegalAccessException e) {
			e.printStackTrace();
		} catch(IllegalArgumentException e) {
			e.printStackTrace();
		} catch(InvocationTargetException e) {
			e.printStackTrace();
		} catch(NoSuchMethodException e) {
			e.printStackTrace();
		} catch(SecurityException e) {
			e.printStackTrace();
		}
	}
}