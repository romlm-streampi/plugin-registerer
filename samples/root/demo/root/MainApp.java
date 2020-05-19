package demo.root;

import fr.streampi.plugin.PluginRegisterer;
import java.util.Map;
import java.io.File;

public class MainApp {

	public static void main(String... args) {

		// this file should be a jar repo for your plugins
		File rootFolder = new File("any path you want");
		testPluginClass(rootFolder);
		testPluginInterface(rootFolder);
	}

	public static void testPluginClass(File rootFolder) {
		PluginRegisterer<? extends PluginClass, PluginAnnotation> registerer = PluginRegisterer.getPluginRegisterer(PluginClass.class, PluginAnnotation.class, rootFolder.toPath());

		Map<PluginAnnotation, ? extends PluginClass> pluginClassObjects = registerer.getRegisteredObjects();
		pluginClassObjects.forEach((annotation, plugin) -> {
			System.out.println("found object annotated "+ann.value());
			System.out.println("executing plugin : ");
			plugin.getDeclaredConstructor().newInstance().executeAny();
			// does whatever you asked your plugin to do
		});
	}

	public static void testPluginInterface(File rootFolder) {
		PluginRegisterer<? extends PluginInterface, PluginAnnotation> registerer = PluginRegisterer.getPluginRegisterer(PluginInterface.class, PluginAnnotation.class, rootFolder.toPath());

		Map<PluginAnnotation, ? extends PluginInterface> pluginInterfaceObjects = registerer.getRegisteredObjects();
		pluginInterfaceObjects.forEach((annotation, plugin) -> {
			System.out.println("found object annotated "+ann.value());
			System.out.println("executing plugin : ");
			plugin.getDeclaredConstructor().newInstance().executeAny();
			// does whatever you asked your plugin to do
		});
	}
}