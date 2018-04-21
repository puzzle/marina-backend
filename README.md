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

* create Project
* add persistent Postgresql database
* add to Project "Deploy Image" puzzle/marina-backend and configure the dc
* add route to expose the backend
