package demo.plugin;

import demo.root.PluginAnnotation;
import demo.root.PluginInterface;

@PluginAnnotation("hello world")
public class PluginInterfaceImpl implements PluginInterface {
	@Override
	public void exxecuteAny() {
		System.out.println("overrided PluginInterface");
	}

}