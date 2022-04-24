package vavr.talk.javamexico.persistence.test

import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.spock.Testcontainers
import spock.lang.Specification

@Testcontainers
class DbRepositorySpecification extends Specification {

    static PostgreSQLContainer DB_CONTAINER = new PostgreSQLContainer('postgres:14.2-alpine')
        .withDatabaseName('investing')
        .withUsername('admin')
        .withPassword('password')

    def setupSpec() {
        //TODO: Move out these hardcoded values to something more scalable
        DB_CONTAINER.start()
    }

    def cleanupSpec() {
        DB_CONTAINER.stop()
    }

}

