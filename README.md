# plugin-registerer

this project is a tool to help people creating plugins within their java apps.
this project uses modularity.

steps to have a working plugin project

1. add this project as a dependency to your root project
1. create a base object that will be extended in the plugins in the root project
2. create an annotation that will describe your object in the plugin in the root project
3. create your plugin project and set up your module-info.java as shown in `samples/plugin/module-info.java`
4. in your main project retrieve all your plugins in jars added to the plugin registerer paths
5. you are ready to build any plugin and add it to a lib folder so you can use it in any app that uses this plugin registerer
