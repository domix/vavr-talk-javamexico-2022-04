package vavr.talk.javamexico.persistence.db.repository

import io.vavr.control.Either
import vavr.talk.javamexico.Failure
import vavr.talk.javamexico.InvestingUser
import vavr.talk.javamexico.persistence.test.DbRepositorySpecification
import vavr.talk.javamexico.repository.InvestingUserRepository

class InvestingUserDbRepositorySpecs extends DbRepositorySpecification {

    InvestingUserRepository investingUserRepository

    def setup() {
        investingUserRepository = InvestingUserDbRepository.create(dataSource, beanValidator)
    }

    def 'Test save user interest some '() {
        given:
            def users = (1..5).collect {
                user(it, testCase)
            }
        when:
            def savedUsers = users.collect { investingUserRepository.save(it) }

        then:
            def saved = savedUsers.collect { Either<Failure, InvestingUser> saved -> saved.get() }

            saved
                .findAll { InvestingUser user ->
                    user.id
                        && user.firstName in users.firstName
                        && user.lastName in users.lastName
                        && user.createdAt
                        && user.updatedAt
                }.size() == users.size()

        when: "Now let's get them one by one"
            def read = savedUsers.collect {
                investingUserRepository.get(it.get().id)
            }

        then:
            read.every {
                def user = it.get()
                user.id in saved.id
                    && user.firstName in saved.firstName
                    && user.lastName in saved.lastName
                    && user.createdAt in saved.createdAt
                    && user.updatedAt in saved.updatedAt
            }

        where:
            testCase  | _
            'success' | _
    }

    static InvestingUser user(final long index, final String testCase) {
        InvestingUser.builder()
            .firstName("${ testCase }-${ index }")
            .lastName("${ testCase }-${ index }")
            .build()
    }

}
