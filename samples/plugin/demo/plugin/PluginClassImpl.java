package demo.plugin;

import demo.root.PluginAnnotation;
import demo.root.PluginClass;

@PluginAnnotation("plugin class impl is my name")
public class PluginClassImpl extends PluginClass {
	@Override
	public void executeAny() {
		System.out.println("executed PluginClassImpl plugin");
	}
}