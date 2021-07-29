#!/bin/sh

sh /opt/jboss/keycloak/bin/add-user-keycloak.sh -r devenv -u admin -p admin --roles ADMIN || true
sh /opt/jboss/keycloak/bin/add-user-keycloak.sh -r devenv -u user -p user --roles USER || true
