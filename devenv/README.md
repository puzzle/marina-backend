# Marina Development Environment

The Marina Development Environment simplifies running the Marina backend within a development environment / IDE.

All services can be started by running `docker-compose up` in the project's root directory.
The devenv is meant to be used with the default configuration of Marina backend.
This means you should not need to change any configuration in order to start development!

However, you may configure different bind ports for each service using a `.env` file.
But keep in mind that this will require you to update the Marina backend configuration accordingly.
To do so, you may use environment variables or create an `applicyation-local.yml` within the [resources directory](../src/main/resources).

## Keycloak
The devenv Keycloak is completely pre-configured with a `devenv` realm and `marina` client.
Moreover, the following users are available:

**username**|**password**|**Marina role**
:----------:|:----------:|:--------------:
admin       | admin      | ADMIN
user        | user       | USER

Keycloak is available on `localhost:8090` per default. 
In order to override the bind port, you may set `KEYCLOAK_PORT` in your `.env` file accordingly. 

## Database
The development environment ships with a database listening on `localhost:5432` per default.
In order to override the bind port, you may set `DB_PORT` in your `.env` file accordingly.
