# Template for Java Spring Microservice project

[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=pagopa_pagopa-taxonomy&metric=alert_status)](https://sonarcloud.io/dashboard?id=pagopa_pagopa-taxonomy)
[![Integration Tests](https://github.com/pagopa/<TODO>/actions/workflows/integration_test.yml/badge.svg?branch=main)](https://github.com/pagopa/<TODO>/actions/workflows/integration_test.yml)

## Description

An application for managing the taxonomy of payments. Can generate version-specific JSONs from a csv file on a Google Drive.

---

## Api Documentation 📖

See the [OpenApi 3 here.](https://editor.swagger.io/?url=https://raw.githubusercontent.com/pagopa/pagopa-taxonomy/main/openapi/openapi.json)

---

## Technology Stack

- Java 11
- spring-boot-devtools
- spring-boot-configuration-processor
- caffeine
- springdoc-openapi-ui
- h2
- modelmapper
- mapstruct
- lombok
- junit
- spring-boot-maven-plugin
- mapstruct-processor


---

## Start Project Locally 🚀

### Prerequisites

- docker

### Run docker container

from `./docker` directory

`sh ./run_docker.sh dev`

ℹ️ Note: for PagoPa ACR is required the login `az acr login -n <acr-name>`

---

## Develop Locally 💻

### Prerequisites

- git
- maven
- jdk-11

### Run the project

Start the springboot application with this command:

`mvn spring-boot:run -Dspring-boot.run.profiles=local`

### Spring Profiles

- **local**: to develop locally.
- _default (no profile set)_: The application gets the properties from the environment (for Azure).

### Testing 🧪

#### Unit testing

To run the **Junit** tests:

`mvn clean verify`

#### Integration testing

From `./integration-test/src`

1. `yarn install`
2. `yarn test`

#### Performance testing

install [k6](https://k6.io/) and then from `./performance-test/src`

1. `k6 run --env VARS=local.environment.json --env TEST_TYPE=./test-types/load.json main_scenario.js`

---

## Contributors 👥

Made with ❤️ by PagoPa S.p.A.

### Mainteiners

See `CODEOWNERS` file
