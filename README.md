# OpenAPI Contract-First with Vert.x and jOOQ

## Overview

The purpose of this project is to demonstrate how you could create a Contract-First API
for [Vert.x](https://vertx.io) using [OpenAPI Generator](https://openapi-generator.tech) and [jOOQ](https://www.jooq.org/)

## Prerequisites

* Java JDK 17
* Docker (or Podman >=3.3 and docker socket configured)
* Docker Compose

## Build Application

```bash
mvn clean verify
```

## Run application locally

```bash
docker-compose up -d
mvn clean install
mvn -pl modules/api clean compile vertx:run
```

## Swagger UI

Swagger UI has been integrated and is expose on port 8080 at the path `/swagger/`. When
running locally, this would be http://localhost:8080/swagger/

## WHAT IS GOING ON HERE?!?!?!

Great question! At first glance, it seems somewhat complicated doesn't it?!?! Once
you get past that, it's really not so bad. Here's how it works:

1. You start with an OpenAPI Specification file (the contract) [HERE](modules/api/src/main/resources/openapi.yml)
2. Using that contract, the `modules/data-access` module uses the [OpenAPI Generator](https://openapigenerator.tech) to create SQL DDL for the schema classes
3. The [jOOQ Maven Plugin](https://www.jooq.org/doc/latest/manual/code-generation/) then generates the jOOQ code needed to wire our data types to the database using the [Vert.x jOOQ](https://github.com/jklingsporn/vertx-jooq) custom generator.
4. The DAOs created by the jOOQ generator are then used to implement our Service Classes in the `modules/api` module

The `modules/converters` module is purely to allow for the creation/compilation of the jOOQ custom type converters, and if you are not using UUIDs as your primary key you shouldn't need to worry about it.

**TL;DR** - Ignore everything except the `modules/api` module. Everything that we will be doing as developers will 
happen in that module alone. Everything else is tooling/automation so that we get a bunch of stuff done for us for
free.