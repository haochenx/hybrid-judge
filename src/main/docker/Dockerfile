# Use phusion/baseimage as base image
FROM phusion/baseimage:latest

# invoke the baseimage-docker init system
CMD ["/sbin/my_init"]

# configure APT
RUN sed -i s/archive/jp.archive/g /etc/apt/sources.list

# configure building environment for LLVM and emscripten
RUN apt-get update && apt-get install -y \
    emscripten

# warm up emscripten
COPY sample.c /tmp/emscripten/sample.c
WORKDIR /tmp/emscripten
RUN emcc -v
RUN emcc -v
RUN emcc -O2 -oprog.js sample.c
RUN rm -rf /tmp/emscripten
WORKDIR /

# clean up APT
RUN apt-get clean && rm -rf /var/lib/apt/lists/* /tmp/* /var/tmp/*
