package vavr.talk.javamexico.business.interest.impl

import org.jooq.RecordMapper
import spock.lang.Specification
import vavr.talk.javamexico.investing.InvestingUser
import vavr.talk.javamexico.business.interest.InterestCalculation
import vavr.talk.javamexico.jooq.api.JooqStreamOperations
import vavr.talk.javamexico.jooq.stream.JooqReadStreamOperations
import vavr.talk.javamexico.persistence.db.repository.InvestingAccountDbRepository
import vavr.talk.javamexico.persistence.db.repository.InvestingContractDbRepository
import vavr.talk.javamexico.persistence.jooq.tables.records.InvestingUserRecord
import vavr.talk.javamexico.persistence.mapper.InvestingRecordMapper
import vavr.talk.javamexico.persistence.test.DbRepositorySpec
import vavr.talk.javamexico.repository.InvestingAccountRepository
import vavr.talk.javamexico.repository.InvestingContractRepository

import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

import static vavr.talk.javamexico.persistence.jooq.tables.InvestingUser.INVESTING_USER

class VavrInterestCalculationSpecs extends Specification {

  static final DbRepositorySpec db = new DbRepositorySpec()
  static final RecordMapper<InvestingUserRecord, InvestingUser> recordMapper =
    InvestingRecordMapper.INSTANCE::to

  JooqStreamOperations streamOperations
  InvestingAccountRepository accountRepository
  InvestingContractRepository contractRepository
  ExecutorService executorService = Executors.newScheduledThreadPool(5)

  InterestCalculation interestCalculation

  def setupSpec() {
    db.init()
  }

  def setup() {
    streamOperations = JooqReadStreamOperations.create(db.dataSource, 'investing')
    accountRepository = InvestingAccountDbRepository.create(db.dataSource, db.beanValidator)
    contractRepository = InvestingContractDbRepository.create(db.dataSource, db.beanValidator)
    interestCalculation = new VavrInterestCalculation(accountRepository, contractRepository, executorService)
  }

  def 'test calculation of interest'() {
    given:
      streamOperations.streamAll(INVESTING_USER, recordMapper)
        .get()
        .forEach { InvestingUser user ->
          def interest = interestCalculation.interestFor(user)
          println "> Interest calculated for ${ user.email }: $interest"
        }

    expect:
      true
  }

}
