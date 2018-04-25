Travis [![Build Status](https://travis-ci.org/puzzle/marina-backend.svg?branch=master)](https://travis-ci.org/puzzle/marina-backend)

# Marina Backend

This Springboot application provides the backend to the marina gui application

## Start Application with Docker pure

First start a Postgresql container with attached volume, run in the project root

```
docker run -d --name postgresql-container -v$(pwd)/datastore-postgresql:/var/lib/pgsql/data -e POSTGRESQL_USER=marina -e POSTGRESQL_PASSWORD=marina -e POSTGRESQL_DATABASE=marina centos/postgresql-96-centos7
```

Then start the backend application and link it to the database container

```
docker run -d -e SECURITY_OAUTH2_CLIENT_ACCESSTOKENURI=[url] -e SECURITY_OAUTH2_CLIENT_CLIENTID=[clientid] -e SECURITY_OAUTH2_CLIENT_CLIENTSECRET=[secret] -e SECURITY_OAUTH2_CLIENT_USERAUTHORIZATIONURI=[url] -e SECURITY_OAUTH2_RESOURCE_USERINFOURI=[url] -e SPRING_DATASOURCE_URL=jdbc:postgresql://postgresql-container:5432/marina -e SPRING_DATASOURCE_PASSWORD=marina -e SPRING_DATASOURCE_USERNAME=marina -p 8080:8080 --link postgresql-container puzzle/marina-backend
```

or use `docker-compose up` to do all together with docker-compose

## Database changes

We use Lliquibase to manage the database scheme and changes

## Dev Environment

### Postgresql DB

We use the docker image `centos/postgresql-96-centos7`

Start Postgresql DB without volume attached:

```
docker run -d -e POSTGRESQL_USER=marina -e POSTGRESQL_PASSWORD=marina -e POSTGRESQL_DATABASE=marina -p 5432:5432 centos/postgresql-96-centos7
```

persistent Volume attached
```
docker run -d -e POSTGRESQL_USER=marina -e POSTGRESQL_PASSWORD=marina -e POSTGRESQL_DATABASE=marina -p 5432:5432 -v /host/db/path:/var/lib/pgsql/data centos/postgresql-96-centos7
```

or run the pre-configured docker-compose command:

```bash
docker-compose up -d postgresql-localdev
```

### OAuth

configure secret values to your oauth integration via Environment variables

```
SECURITY_OAUTH2_CLIENT_CLIENTID
SECURITY_OAUTH2_CLIENT_CLIENTSECRET
SECURITY_OAUTH2_CLIENT_ACCESSTOKENURI
SECURITY_OAUTH2_CLIENT_USERAUTHORIZATIONURI
SECURITY_OAUTH2_CLIENT_TOKENNAME: access_token
SECURITY_OAUTH2_CLIENT_AUTHENTICATIONSCHEME: header
SECURITY_OAUTH2_CLIENT_CLIENTAUTHENTICATIONSCHEME: header
SECURITY_OAUTH2_RESOURCE_USERINFOURI
```

or, if you use the IntelliJ Run Configuration, create a file named `src/main/resources/application-local.yml`
and set the following values:

```text
security:
  oauth2:
    client:
      access-token-uri: 
      client-id: 
      client-secret: 
      user-authorization-uri: 
    resource:
      user-info-uri: 

spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/marina

cors:
  enable: true
  allow-origin: http://localhost:3000
```

## Deploy to OpenShift

### Infrastructure setup

create a build project

We're going to set up the following infrastructure
* build project, to build the images, images from build project will be promoted to the stage
* dev
* test
* prod

create the projects

* `oc new-project marina-build`
* `oc new-project marina-dev`
* `oc new-project marina-test`
* `oc new-project marina-prod`

give the puller serviceaccount from the stages access to the build project

```
oc policy add-role-to-group system:image-puller system:serviceaccounts:marina-dev -n marina-build
oc policy add-role-to-group system:image-puller system:serviceaccounts:marina-test -n marina-build
oc policy add-role-to-group system:image-puller system:serviceaccounts:marina-prod -n marina-build
```

#### Setup build project

* add docker build: `oc new-build https://github.com/puzzle/marina-backend.git --strategy=docker --name=marina-backend`

#### Setup Backend Dev
* Create backend: `oc new-app marina-build/marina-backend:test`

#### Setup Backend with postgresql db

* create Project
* add persistent Postgresql database, and configure dc
* Create Backend app
  * dev `oc new-app marina-build/marina-backend -n marina-dev`
  * test `oc new-app marina-build/marina-backend:test -n marina-test` 
  * prod `oc new-app marina-build/marina-backend:prod -n marina-prod` 
* configure backend
  * OAuth, Database, set resource limits
  * add healthchecks url /api/actuator/health, readyness initial delay 150, delay 5, liveness initial delay 200, delay 5
  * disable image change trigger, so that we can trigger the deployment from the pipeline
* add route to expose the backend
  * path /api
  * secure with redirect of unsecure traffic

### Tagging images and promoting to stage

tag images in the build project accordingly, dev is always on latest.

deploy the latest image to test:
`oc tag marina-build/marina-backend:latest marina-build/marina-backend:test`

deploy the latest test image to prod:
`oc tag marina-build/marina-backend:test marina-build/marina-backend:prod`

