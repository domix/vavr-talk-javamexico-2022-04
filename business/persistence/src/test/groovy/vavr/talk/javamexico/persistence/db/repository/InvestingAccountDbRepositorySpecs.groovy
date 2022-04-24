package vavr.talk.javamexico.persistence.db.repository

import io.vavr.control.Either
import vavr.talk.javamexico.Failure
import vavr.talk.javamexico.InvestingUser
import vavr.talk.javamexico.persistence.test.DbRepositorySpecification
import vavr.talk.javamexico.repository.InvestingAccountRepository
import vavr.talk.javamexico.repository.InvestingContractRepository
import vavr.talk.javamexico.repository.InvestingUserRepository

class InvestingAccountDbRepositorySpecs extends DbRepositorySpecification {

  InvestingAccountRepository accountRepository
  InvestingContractRepository contractRepository
  InvestingUserRepository userRepository


  def setup() {
    accountRepository = InvestingAccountDbRepository.create(dataSource, beanValidator)
    contractRepository = InvestingContractDbRepository.create(dataSource, beanValidator)
    userRepository = InvestingUserDbRepository.create(dataSource, beanValidator)
  }

  def foo() {
    def user = InvestingUser.builder()
      .firstName("")
      .lastName("")
      .email("")
      .build()
    def savedUser = userRepository.save(user)

    contractRepository.findAll()

    savedUser.map( user -> {

    })

    //InvestingAccount.builder().contractId(1l).
    //investingAccountRepository.create()
  }
}
