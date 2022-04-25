package vavr.talk.javamexico.business.interest.impl

import com.github.javafaker.Faker
import org.apache.commons.dbcp2.BasicDataSourceFactory
import org.flywaydb.core.api.Location
import org.flywaydb.core.api.configuration.ClassicConfiguration
import spock.lang.Specification
import vavr.talk.javamexico.persistence.db.repository.InvestingAccountDbRepository
import vavr.talk.javamexico.persistence.db.repository.InvestingContractDbRepository
import vavr.talk.javamexico.persistence.db.repository.InvestingContractMovementDbRepository
import vavr.talk.javamexico.persistence.db.repository.InvestingUserDbRepository
import vavr.talk.javamexico.repository.InvestingAccountRepository
import vavr.talk.javamexico.repository.InvestingContractMovementRepository
import vavr.talk.javamexico.repository.InvestingContractRepository
import vavr.talk.javamexico.repository.InvestingUserRepository
import vavr.talk.javamexico.validation.BeanValidator
import vavr.talk.javamexico.validation.jakarta.JakartaBeanValidation

import javax.sql.DataSource

class BaseDBIntegrationSpec extends Specification {
  static Properties properties = new Properties()
  static DataSource dataSource
  static BeanValidator<?> beanValidator = JakartaBeanValidation.ofDefaults()
  static Faker faker = Faker.instance()
  static InvestingUserRepository userRepository
  static InvestingAccountRepository accountRepository
  static InvestingContractRepository contractRepository
  static InvestingContractMovementRepository movementRepository

  static {
    properties.setProperty('url', 'jdbc:postgresql://localhost:5434/investing')
    properties.setProperty('username', 'admin')
    properties.setProperty('password', 'password')
    properties.setProperty('maxIdle', '5')
  }

  def setupSpec() {
    dataSource = BasicDataSourceFactory.createDataSource(properties)
    userRepository = InvestingUserDbRepository.create(dataSource, beanValidator)
    accountRepository = InvestingAccountDbRepository.create(dataSource, beanValidator)
    contractRepository = InvestingContractDbRepository.create(dataSource, beanValidator)
    movementRepository = InvestingContractMovementDbRepository.create(dataSource, beanValidator)
  }

  static def initFlyway() {
    def configuration = new ClassicConfiguration()
    configuration.setDataSource(dataSource)
    configuration.setLocations(new Location("classpath:db/migration"))
  }
}
