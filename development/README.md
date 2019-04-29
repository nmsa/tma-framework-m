# TMA-Monitor Development

This folder contains the software developed for the `TMA Monitor` component.

The complete monitoring platform was designed to run in a scalable fashion on top of a solution like `kubernetes`.


Below we detail the key points relative to processes followed and to the structure of the folder.

## Development Process
Following we describe the specific rules that apply to the development process of `TMA Monitor` components.
General Processes and Principles followed by the entire `WP3` for the development of the solution for the [TMA Framework](https://github.com/eubr-atmosphere/tma-framework) will be available in the [framework wiki](https://github.com/eubr-atmosphere/tma-framework/wiki).
 
### Components development
All the components should be ready to deploy in `docker` containers.

When applicable, `kubernetes` scripts should be prepared for the deployment of the containers in the kubernetes infrastructure.

All images will be available in Docker Hub soon.


### Development of `Java` components
Java components should be developed in `maven` projects.
All projects should follow the `maven` archetype available at [tma-archetype](https://github.com/eubr-atmosphere/tma-framework/tree/master/common/tma-archetype). 
Details on usage are available in the folder.

A `docker` image with the Java dependencies of the project is available at the [libraries](libraries) folder.
This image can be used to build and deploy the `java` applications, in case `maven` is not available in your system.

To build the image, use the following command.

```sh
docker build --no-cache -f Dockerfile -t tma-monitor/libs:0.1 .
```

## Contents Structure

This folder is organized in the following sub-folders: 

* [dependency](dependency) -- contains the essencial dependencies for development, including the [Atmosphere Maven Archetype](dependency/atmosphere-tmaf-archetype) and the base `python` image used.
* [libraries](libraries) -- The key libraries to be used during development. At the moment only containing `java` libraries. Make use of [dependency](dependency).
* [probes](probes) -- Implementation of concrete probes, including a [probe-demo](probes/probe-demo). Make use of [dependency](dependency) and [libraries](libraries).
* [server](server) -- Includes the implementation and scripts of the server component. It will include specific documentation. Make use of [dependency](dependency) and [libraries](libraries).
* [test](test) -- Includes scripts and code for testing activities.  
   
   

   
## Authors
* Nuno Antunes
   



 
