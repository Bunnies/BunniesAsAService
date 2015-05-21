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

## License
The source of this project is provided, subject to the BSD 2-clause license.

Copyright © 2015, Sky Welch  
All rights reserved.

Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.

2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
