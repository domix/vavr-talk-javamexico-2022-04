package vavr.talk.javamexico.persistence.test

import org.apache.commons.dbcp2.BasicDataSourceFactory
import org.flywaydb.core.Flyway
import org.flywaydb.core.api.Location
import org.flywaydb.core.api.configuration.ClassicConfiguration
import org.testcontainers.spock.Testcontainers
import spock.lang.Specification
import vavr.talk.javamexico.validation.BeanValidator
import vavr.talk.javamexico.validation.jakarta.JakartaBeanValidation

import javax.sql.DataSource

@Testcontainers
class DbRepositorySpecification extends Specification {

    static Properties properties = new Properties()
    static DataSource dataSource
    static BeanValidator<?> beanValidator = JakartaBeanValidation.ofDefaults()

    static {
        properties.setProperty('url', 'jdbc:postgresql://localhost:5434/investing')
        properties.setProperty('username', 'admin')
        properties.setProperty('username', 'password')
    }

    def setupSpec() {
        dataSource = BasicDataSourceFactory.createDataSource(properties)
        def configuration = new ClassicConfiguration()
        configuration.setDataSource(dataSource)
        configuration.setLocations(new Location("classpath:db/migration"))

        def flyway = new Flyway(configuration)
        flyway.migrate()
    }

}

