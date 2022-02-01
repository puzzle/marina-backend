Travis [![Build Status](https://travis-ci.org/puzzle/marina-backend.svg?branch=master)](https://travis-ci.org/puzzle/marina-backend)

# Marina Backend

This Springboot application provides the backend to the [marina gui application](https://github.com/puzzle/marina-gui)

## Development Environment

To start the application in local development mode:

```bash
docker-compose up -d
SPRING_PROFILES_ACTIVE=local ./gradlew bootRun
```

For further details on the development environment, please see the [corresponding documentation](devenv).

Run tests:
```shell
./gradlew clean test
```

## Start Application with Docker pure

First start a Postgresql container with attached volume, run in the project root

```
docker run -d --name postgresql-container -v$(pwd)/datastore-postgresql:/var/lib/pgsql/data -e POSTGRESQL_USER=marina -e POSTGRESQL_PASSWORD=marina -e POSTGRESQL_DATABASE=marina centos/postgresql-96-centos7
```

Then start the backend application and link it to the database container

```
docker run -d -e SECURITY_OAUTH2_CLIENT_ACCESSTOKENURI=[url] -e SECURITY_OAUTH2_CLIENT_CLIENTID=[clientid] -e SECURITY_OAUTH2_CLIENT_CLIENTSECRET=[secret] -e SECURITY_OAUTH2_CLIENT_USERAUTHORIZATIONURI=[url] -e SECURITY_OAUTH2_RESOURCE_USERINFOURI=[url] -e SPRING_DATASOURCE_URL=jdbc:postgresql://postgresql-container:5432/marina -e SPRING_DATASOURCE_PASSWORD=marina -e SPRING_DATASOURCE_USERNAME=marina -p 8080:8080 --link postgresql-container puzzle/marina-backend
```

## Database changes

We use Liquibase to manage the database scheme and changes

## Roles

The following user roles are defined
* ROLE_ADMIN
* ROLE_USER (default role)

To provide the roles via OAuthprovider the userinfo JSON needs to be altered with

* roles or
* client_roles property

if the roles are available in the userinfo response, the client_roles are ignored

In Keycloak we need then to define mappers on the client
"User Realm Role" Mapper for the Realm Roles or the "User Client Role" Mapper for the client Roles in the Keycloak Client:
 * Realm Role prefix: empty
 * Multivalued: ON
 * Token Claim Name: roles|client_roles
 * Claim JSON Type: String
 * Add to ID token: ON
 * Add to access token: ON
 * Add to userinfo: ON 

## Deploy to OpenShift

### Infrastructure setup

We're going to set up the following infrastructure
* build project, to build the images, images from build project will be promoted to the stage
* dev
* test
* prod

Create the projects:

* `oc new-project marina-build`
* `oc new-project marina-dev`
* `oc new-project marina-test`
* `oc new-project marina-prod`

Give the puller serviceaccount from the stages access to the build project:

```
oc policy add-role-to-group system:image-puller system:serviceaccounts:marina-dev -n marina-build
oc policy add-role-to-group system:image-puller system:serviceaccounts:marina-test -n marina-build
oc policy add-role-to-group system:image-puller system:serviceaccounts:marina-prod -n marina-build
```

Setup build project:

* add docker build: `oc new-build https://github.com/puzzle/marina-backend.git --strategy=docker --name=marina-backend`

### Application setup

* create Project
* add persistent Postgresql database, and configure dc
* Create Backend app
  * dev `oc new-app marina-build/marina-backend:dev -n marina-dev`
  * test `oc new-app marina-build/marina-backend:test -n marina-test` 
  * prod `oc new-app marina-build/marina-backend:prod -n marina-prod` 
* configure backend
  * OAuth, Database, set resource limits
  * add healthchecks url /api/actuator/health, readyness initial delay 150, delay 5, liveness initial delay 200, delay 5
  * disable image change trigger, so that we can trigger the deployment from the pipeline
* add route to expose the backend
  * path /api
  * secure with redirect of unsecure traffic

#### OAuth

OAuth can be configured using environment variables. See the following example for a Keycloak configuration:

```
SPRING_SECURITY_OAUTH2_CLIENT_REGISTRATION_KEYCLOAK_CLIENTID=<CLIENT_ID>
SPRING_SECURITY_OAUTH2_CLIENT_REGISTRATION_KEYCLOAK_CLIENTSECRET=<CLIENT_SECRET>
SPRING_SECURITY_OAUTH2_CLIENT_PROVIDER_KEYCLOAK_TOKENURI=https://<HOST>/auth/realms/<REALM>/protocol/openid-connect/token
SPRING_SECURITY_OAUTH2_CLIENT_PROVIDER_KEYCLOAK_AUTHORIZATIONURI=https://<HOST>/auth/realms/<REALM>/protocol/openid-connect/auth
SPRING_SECURITY_OAUTH2_CLIENT_PROVIDER_KEYCLOAK_USERINFOURI=https://<HOST>/auth/realms/<REALM>/protocol/openid-connect/userinfo
```
Please note that *\<CLIENT_ID>*, *\<CLIENT_SECRET>*, *\<HOST>* and *\<REALM>* have to be replaced corresponding to your Keycloak setup.

#### Backup

To create a backup cronjob on OpenSphift run the following command
```
oc process -f openshift/database-dump-persistent.yml -pPGUSER=user -pPGPASSWORD=12345 -pPGHOST=host -pPGDATABASE=database | oc create -f -
```

### Tagging images and promoting to stage

Tag images in the build project accordingly, dev is always on latest.

Deploy the latest image to test:
`oc tag marina-build/marina-backend:latest marina-build/marina-backend:test`

Deploy the latest test image to prod:
`oc tag marina-build/marina-backend:test marina-build/marina-backend:prod`

