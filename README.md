# Deprecated
**There are better alternatives nowadays. Have a look at e.g. Keycloak. This project is not maintained anymore. **










# UAA-Service
**User Authentication and Authorization Service**

[![Build Status](https://travis-ci.org/JanLoebel/uaa-service.svg?branch=master)](https://travis-ci.org/JanLoebel/uaa-service)

[![Issue Count](https://codeclimate.com/github/JanLoebel/uaa-service/badges/issue_count.svg)](https://codeclimate.com/github/JanLoebel/uaa-service)

This project should offer a service to authenticate and manage user and clients together with their rights. 

Features:
 - Authentication via JWT (Json Web Token)
 	- OAuth2 (Get token / Refresh token)
 - Self User-Management (Register, Reset password, Update information)
 - Administration of all entities
 	- Authorities (create, read, delete)
	- Users (create, read, update, delete) 
	- Clients (create, read, updated, delete)
 - Highly configurable (see Configuration)
 - Bootstrapping (provide default user/clients)
 - Docker (available as docker image)
 - Based on Spring Boot & Spring Security (easy extendible & high security)

## TODOs
Following points should be addressed in the future:
 - Improve by user feedback
 - Try to replace Tomcat by Undertow to improve performance and memory consumption
 - Write demo client

## How to start

1. Make sure you have **Maven** installed
1. Make sure you have **Java 1.8** installed
1. Checkout the source code `git clone https://github.com/JanLoebel/uaa-service.git`
1. Run `mvn clean install`

### Start the UAA-Service

1. Inside _uaa-Service_ execute `mvn spring-boot:run`, this will start the UAA-Service with the _Development (DEV)_-Profile.
To start the UUA-Service in the production mode, just append `mvn spring-boot:run -Dspring.profiles.active=prod`.

### Start the UAA-Service with docker

1. Build the docker image by executing `mvn clean package docker:build`.
1. Start instance with MySQL by executing `docker-compose -f src/main/docker/docker-compose.yml up -d`.
1. To check the logs use `docker-compose -f src/main/docker/docker-compose.yml logs`.
1. Stop instance by by executing `docker-compose -f src/main/docker/docker-compose.yml down`.

## Configuration
You can change all important configurations of the UAA-Service in the **application-uaa.yml** file.
```
- 'Service.port' - Port on which the UAA-Service should start [required, number]

- 'uaa' - UAA-Service related configurations
-- 'baseUrl' - Url where the UAA-Service is available, will be used when sending email [required, string]
-- 'bootstrapFile' - Path to the json-file to bootstrap when starting the uaa-Service [optional, string]
-- templatePath - Directory path to overwrite templates [optional, string]

-- 'jwt' - JWT related properties
--- 'keystore' - JWT Keystore properties
---- 'path' - Filepath to the Keystore [required, string]
---- 'password' - Password for the provided Keystore [required, string]
---- 'keypair' - Name of the KeyPair to use [required, string]

-- 'client_default' - Defaults are used to define when creating a new client without this information
--- 'accessTokenValiditySeconds' - Default seconds how long a access token for this client should be valid [optional, number]
--- 'authorizedGrantTypes' - Default grant types for a client [optional, string separated by ',']
--- 'autoApprove' - Does a client need to approve itself (e.g. by the client_secret) [optional, boolean]

-- 'mail.from' - Given address will be used as sender for emails [optional, email]

-- 'account' - Defaults are used to define when creating a new account
--- 'verification' - Is verification of the email account needed [optional, boolean]
--- 'passwordResetCodeValidityMinutes' - How long should a generated reset code for the password be valid [optional, number]
--- 'defaultLocale' - Locale to use for new created accounts when the information is not given by the client [optional, locale]

--- 'password' - Password restriction related properties
---- 'minLength' - Minimal length of a valid password [optional, number]
---- 'maxLength' - Maximal length of a valid password [optional, number]
---- 'regex' - Regex which will be checked for a valid password [optional, regex]

--- 'username' - Username restriction related properties
---- 'minLength' - Minimal length of a valid username [optional, number]
---- 'maxLength' - Maximal length of a valid username [optional, number]
---- 'regex' - Regex which will be checked for a valid username [optional, regex]

--- 'email' - Email restriction related properties
---- 'minLength' - Minimal length of a valid email [optional, number]
---- 'maxLength' - Maximal length of a valid email [optional, number]


- 'spring'
-- 'mail' - Mail Service settings for Spring
--- 'host' - Adress of mail Service [required, string]
--- 'port' - Port of the mail Service [required, number]
--- 'username' - Username to authenticate at the mail Service [optional, string]
--- 'password' - Password to authenticate at the mail Service [optional, string]

-- 'datasource' - Database Service settings for Spring
--- 'url' - JDBC-Url to the database Service [required, string]
--- 'username' - Username to authenticate at the database Service [required, string]
--- 'password' - Password to authenticate at the database Service [optional, string]
--- 'driver-class-name' - Driver class name depending on the used database Service [required, string]

-- 'jpa' - JPA related settings
--- 'hibernate.ddl-auto' - Update strategy for database changes [required, string]
--- 'properties.hibernate.dialect' - Hibernate dialect to use to generate database Service specific queries [required, string]
```

## API documentation
The API documentation can be found by starting executing 

1. Inside _uaa-Service_ execute `mvn clean package` and then `mvn spring-boot:run`, this will first compile the application, run tests and documentation tasks and then start the UAA-Service with the _Development (DEV)_-Profile.
1. Afterwards go to [http://localhost:8080/docs/index.html](http://localhost:8080/docs/index.html) to see the documentation.
1. Note: The documentation is currently only allowed to be accessed in the DEV-profile, to grant the access in the PROD-profile, add _doc_ as profile.


## Bootstrap
The UAA offers the possibility to bootstrap user and clients when starting the service. Therefore everything you have to do is provide a bootstrap-json-file which contains users and clients which should be created. Example-Content:
```json
{
	"clients": [
		{
			"client_id": "client_id",
			"client_secret": "client_secret",
			"auto_approve": false
		}
	],
	"users": [
		{
			"username": "admin",
			"password": "mySecretPassword",
			"email": "admin@localhost.me",
			"authorities": [ "USER", "ADMIN" ]
		}
	]
}
```

You can define the location of this file with a simple property variable `mvn spring-boot:run -Duaa.bootstrapFile=/tmp/bootstrap.json`.
