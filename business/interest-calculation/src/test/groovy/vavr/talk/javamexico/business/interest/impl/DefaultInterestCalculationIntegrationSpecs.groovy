package vavr.talk.javamexico.business.interest.impl

import com.github.javafaker.Faker
import io.vavr.control.Either
import org.apache.commons.dbcp2.BasicDataSourceFactory
import org.flywaydb.core.api.Location
import org.flywaydb.core.api.configuration.ClassicConfiguration
import spock.lang.Specification
import vavr.talk.javamexico.business.interest.InterestCalculationContext
import vavr.talk.javamexico.investing.InvestingAccount
import vavr.talk.javamexico.investing.InvestingUser
import vavr.talk.javamexico.persistence.db.repository.InvestingAccountDbRepository
import vavr.talk.javamexico.persistence.db.repository.InvestingContractDbRepository
import vavr.talk.javamexico.persistence.db.repository.InvestingContractMovementDbRepository
import vavr.talk.javamexico.persistence.db.repository.InvestingUserDbRepository
import vavr.talk.javamexico.validation.BeanValidator
import vavr.talk.javamexico.validation.jakarta.JakartaBeanValidation

import javax.sql.DataSource

class DefaultInterestCalculationIntegrationSpecs extends Specification {
  static Properties properties = new Properties()
  static DataSource dataSource
  static BeanValidator<?> beanValidator = JakartaBeanValidation.ofDefaults()
  static Faker faker = Faker.instance()

  static {
    properties.setProperty('url', 'jdbc:postgresql://localhost:5434/investing')
    properties.setProperty('username', 'admin')
    properties.setProperty('password', 'password')
    properties.setProperty('maxIdle', '5')
  }

  def setupSpec() {
    dataSource = BasicDataSourceFactory.createDataSource(properties)
  }

  static def initFlyway() {
    def configuration = new ClassicConfiguration()
    configuration.setDataSource(dataSource)
    configuration.setLocations(new Location("classpath:db/migration"))
  }

  def foo2() {
    given:
      def userRepository = InvestingUserDbRepository.create(dataSource, beanValidator)
      def accountRepository = InvestingAccountDbRepository.create(dataSource, beanValidator)
      def contractRepository = InvestingContractDbRepository.create(dataSource, beanValidator)
      def movementRepository = InvestingContractMovementDbRepository.create(dataSource, beanValidator)

      userRepository.find(_ as Long) >> Either.right(InvestingUser.builder().id(1l).build())
      accountRepository.findAllActiveAccounts(_ as Long) >> Either.right([])
      def underTest = new DefaultInterestCalculation(accountRepository, movementRepository)

      def calculationContext = InterestCalculationContext.builder()
        .contracts(contractRepository.findAll().get())
        .build()
    and:
      def user = InvestingUser.builder()
        .firstName(faker.name().firstName())
        .lastName(faker.name().lastName())
        .email(faker.internet().emailAddress())
        .build()
      def savedUser = userRepository.save(user).get()
      def contract = contractRepository.findAll().get().get(0)
      def account = InvestingAccount.builder()
        .userId(savedUser.id)
        .contractId(contract.id)
        .startBalance(20_000)
        .currentBalance(20_000)
        .build()
      def savedAccount = accountRepository.save(account)
        .get()
      accountRepository.save(account)
    when:
      def result = underTest.process(calculationContext, savedUser)
    then:
      result.empty
  }
}
