# pagoPA Functions taxonomy

Taxonomy Azure Function.
This function has the role of converting a CSV file to JSON and then to retrieve it from a blob storage whenever needed.

[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=pagopa_pagopa-taxonomy&metric=alert_status)](https://sonarcloud.io/project/overview?id=pagopa_pagopa-taxonomy)

---

## Api Documentation 📖

Check out the [OpenApi 3 here](https://editor.swagger.io/?url=https://raw.githubusercontent.com/pagopa/pagopa-taxonomy/main/openapi/openapi.json)

---

## Run locally with Docker
`docker build -t pagopa-functions-taxonomy .`

`docker run -it -rm -p 8999:80 pagopa-functions-taxonomy`

### Test
`curl http://localhost:8999/example`

## Run locally with Maven

`mvn clean package`

`mvn azure-functions:run`

### Test
`curl http://localhost:7071/example`

---

### Per dev
`docker run -p 10000:10000 -p 10001:10001 -p 10002:10002 mcr.microsoft.com/azure-storage/azurite`
`./mvnw clean package && ./mvnw azure-functions:run`