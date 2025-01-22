# Docker compose
This Compose file defines two services: web and redis.

The web service uses an image that's built from the Dockerfile in the current directory.
It then binds the container and the host machine to the exposed port, 8000.
This example service uses the default port for the Flask web server, 5000.

The redis service uses a public Redis
image pulled from the Docker Hub registry.


## Services

Computing components of an application are defined as **services**. A service is an abstract concept implemented on platforms by running the same container image, and configuration, one or more times.

Services communicate with each other through **networks**. In the Compose Specification, a network is a platform capability abstraction to establish an IP route between containers within services connected together.

## Volumes

Services store and share persistent data into **volumes**. The Specification describes such persistent data as a high-level filesystem mount with global options.

## Configurations

Some services require configuration data that is dependent on the runtime or platform. For this, the Specification defines a dedicated **configs** concept. From a service container point of view, configs are comparable to volumes, in that they are files mounted into the container. However, the actual definition involves distinct platform resources and services, which are abstracted by this type.

## Secrets

A **secret** is a specific flavor of configuration data for sensitive information that should not be exposed without security considerations. Secrets are made available to services as files mounted into their containers, but the platform-specific resources to provide sensitive data are specific enough to deserve a distinct concept and definition within the Compose specification.

> **Note**  
> With volumes, configs, and secrets, you can have a simple declaration at the top-level and then add more platform-specific information at the service level.

## Projects

A **project** is an individual deployment of an application specification on a platform. A project's name, set with the top-level `name` attribute, is used to group resources together and isolate them from other applications or other installations of the same Compose-specified application with distinct parameters. If you are creating resources on a platform, you must prefix resource names by the project name and set the label `com.docker.compose.project`.

Compose offers a way for you to set a custom project name and override this name, so that the same `compose.yaml` file can be deployed twice on the same infrastructure, without changes, by just passing a distinct name.


### Dockerfile
- Build an image starting with the Python 3.10 image.
- Set the working directory to /code.
- Set environment variables used by the flask command.
- Install gcc and other dependencies
- Copy requirements.txt and install the Python dependencies.
- Add metadata to the image to describe that the container is listening on port 5000
- Copy the current directory . in the project to the workdir . in the image.
- Set the default command for the container to flask run --debug.
