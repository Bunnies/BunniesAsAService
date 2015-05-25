# :rabbit: As A Service

## Introduction
A provider of bunnies, through a REST service.

You can see the results at https://bunnies.io

## Thoughts

* Keep it simple
* SSL only
* Think about v2 - resource based
* Swagger for REST documentation
* JSON only responses
* Need to think about adding metadata to each resource
 * Searching

This is quite a nice source of 'good' (ie: what I agree with at face value) REST practices: http://www.vinaysahni.com/best-practices-for-a-pragmatic-restful-api

## Setup
* Clone the repository,
* Go in to the `api` folder,
* Run `./gradlew clean build shadowJar`,
* Modify `baas.yaml` to run on the ports you want,
* Run `java -jar build/libs/BunniesAsAServiceJava-<version>-<commit>-all.jar server baas.yaml`.

An Upstart script is included, in `meta/upstart`, if you want to run BunniesAsAService as a service.

## Code License
The source code of this project is licensed under the terms of the ISC license, listed in the [LICENSE](LICENSE.md) file. A concise summary of the ISC license is available at [choosealicense.org](http://choosealicense.com/licenses/isc/).
