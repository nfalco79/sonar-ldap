**This plugin is compatible with SonarQube 8.9.5 and later.**


SonarQube LDAP Plugin
=====================
[![Quality Gate Status](https://next.sonarqube.com/sonarqube/api/project_badges/measure?project=org.sonarsource.ldap%3Asonar-ldap&metric=alert_status)](https://next.sonarqube.com/sonarqube/dashboard?id=org.sonarsource.ldap%3Asonar-ldap)

For more, see [the docs](http://docs.sonarqube.org/display/PLUG/LDAP+Plugin)


## Example

You can check this plugin in action using Docker as described below.

Build plugin:

    mvn clean package

Generate certificates:

    ./docker/gen-certs.sh

Build containers (SonarQube and OpenLDAP servers):

    docker-compose build

Start containers:

    docker-compose up

To access SonarQube use LDAP user `tester` with password `test`.

### License

Copyright 2009-2021 SonarSource.

Licensed under the [GNU Lesser General Public License, Version 3.0](http://www.gnu.org/licenses/lgpl.txt)
