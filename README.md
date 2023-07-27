# pagoPA Taxonomy Function 

## Run locally with Docker
`cd docker`

`docker compose up`

### Test
`curl http://localhost:8999/generate`

## Run locally with Maven

`mvn clean package`

`mvn azure-functions:run`

### Test
`curl http://localhost:7071/generate` 

---

## TODO
Once cloned the repo, you should:
- to deploy on standard Azure service:
  - rename `deploy-pipelines-standard.yml` to `deploy-pipelines.yml`
  - remove `helm` folder
- to deploy on Kubernetes:
  - rename `deploy-pipelines-aks.yml` to `deploy-pipelines.yml`
  - customize `helm` configuration
- configure the following GitHub action in `.github` folder: 
  - `deploy.yml`
  - `sonar_analysis.yml`

Configure the SonarCloud project :point_right: [guide](https://pagopa.atlassian.net/wiki/spaces/DEVOPS/pages/147193860/SonarCloud+experimental).