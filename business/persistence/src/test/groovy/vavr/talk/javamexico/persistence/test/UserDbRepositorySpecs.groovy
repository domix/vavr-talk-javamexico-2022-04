package vavr.talk.javamexico.persistence.test

import io.micronaut.test.extensions.spock.annotation.MicronautTest
import io.vavr.control.Either
import jakarta.inject.Inject
import vavr.talk.javamexico.Failure
import vavr.talk.javamexico.User
import vavr.talk.javamexico.persistence.db.repository.UserDbRepository

@MicronautTest
class UserDbRepositorySpecs extends DbRepositorySpecification {

    @Inject
    UserDbRepository userDbRepository

    def 'Test save user interest some '() {
        given:
            def users = (1..5).collect {
                user(it, testCase)
            }
        when:
            def savedUsers = users.collect { userDbRepository.save(it) }

        then:
            def saved = savedUsers.collect { Either<Failure, User> saved -> saved.get() }

            saved
                .findAll { User user ->
                    user.id
                        && user.firstName in users.firstName
                        && user.lastName in users.lastName
                        && user.createdAt
                        && user.updatedAt
                }.size() == users.size()

        when: "Now let's get them one by one"
            def read = savedUsers.collect {
                userDbRepository.get(it.get().id)
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

    static User user(final long index, final String testCase) {
        User.builder()
            .firstName("${ testCase }-${ index }")
            .lastName("${ testCase }-${ index }")
            .build()
    }

}
