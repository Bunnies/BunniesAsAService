# :rabbit: As A Service

## Introduction
A provider of bunnies, through a REST service.

## Thoughts

* Use Flask, Flask-RESTful
* Keep it simple
* Gifs first using existing stuff from bunnies.io
* SSL only
* Major version in the URL (`/v1/bunny/gif/random`)
* Swagger for REST documentation
* JSON only responses
* Camel case only
* Pretty print output
* Gzip output
* Rate limiting (429 Too Many Requests) - (include functional limits in response somewhere, maybe headers?)
* Make sure it's cacheable

This is quite a nice source of 'good' (ie: what I agree with at face value) REST practices: http://www.vinaysahni.com/best-practices-for-a-pragmatic-restful-api

## License
The source of this project is provided, subject to the BSD 2-clause license.

Copyright (c) 2015, Sky Welch
All rights reserved.

Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.

2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
