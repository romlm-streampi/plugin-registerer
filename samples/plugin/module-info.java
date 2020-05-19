module demo.plugin {

	requires demo.root;
	uses demo.root.PluginInterface;
	uses demo.root.PluginClass;

	exports demo.plugin;
}