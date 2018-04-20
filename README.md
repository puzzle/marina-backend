# Marina Backend

This Springboot application provides the backend to the marina gui application

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