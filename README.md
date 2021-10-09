# OpenAPI Contract-First with Vert.x and jOOQ

## Overview

The purpose of this project is to demonstrate how you could create a Contract-First API
for [Vert.x](https://vertx.io) using [OpenAPI Generator](https://openapi-generator.tech) and [jOOQ](https://www.jooq.org/) or [Hibernate Reactive](http://hibernate.org/reactive/)

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
