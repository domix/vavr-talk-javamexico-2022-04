# Persistence module
## Description 
This module is in charge of handle all the data layer access operations. 

## Tech Details
Implemented using jOOQ + VAVR. It uses `flyway` migrations to either database versioning and generation of the jOOQ's records.

## Setup 

### Local development database

```shell
docker run --name investing-db --rm -p 5434:5432 -e POSTGRES_USER=admin -e POSTGRES_PASSWORD=password -e POSTGRES_DB=investing -d postgres:14.2-alpine 
```
Will create docker container with a PostgreSQL ready to used. 

### Initialization

To create either the investingUser and the database itself it necessary to run the following command: 

```shell
scripts/init.sh
```
Remember to give execution permissions to the script first

```shell
chmod +x scripts/init.sh
```
