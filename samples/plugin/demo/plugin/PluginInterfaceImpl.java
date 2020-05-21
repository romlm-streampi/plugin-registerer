package demo.plugin;

import demo.root.PluginAnnotation;
import demo.root.PluginInterface;


public class PluginInterfaceImpl implements PluginInterface {
	@Override
	public void exxecuteAny() {
		System.out.println("overrode PluginInterface");
	}

}