package vavr.talk.javamexico.persistence.db.repository

import org.jooq.RecordMapper
import spock.lang.Ignore
import vavr.talk.javamexico.investing.InvestingAccount
import vavr.talk.javamexico.jooq.api.JooqStreamOperations
import vavr.talk.javamexico.jooq.stream.JooqReadStreamOperations
import vavr.talk.javamexico.persistence.jooq.tables.records.InvestingAccountRecord
import vavr.talk.javamexico.persistence.mapper.InvestingRecordMapper
import vavr.talk.javamexico.persistence.test.DbRepositorySpecification

import static vavr.talk.javamexico.persistence.jooq.tables.InvestingAccount.INVESTING_ACCOUNT
import static vavr.talk.javamexico.persistence.jooq.tables.InvestingContract.INVESTING_CONTRACT

class InvestingAccountWithStreamSpecs extends DbRepositorySpecification {

    static RecordMapper<InvestingAccountRecord, InvestingAccount> recordMapper =
        (InvestingAccountRecord accountRecord) -> InvestingRecordMapper.INSTANCE.to(accountRecord)

    JooqStreamOperations streamOperations

    def setup() {
        streamOperations = JooqReadStreamOperations.create(dataSource, INVESTING_ACCOUNT.getName())
    }

    def 'Test stream couple of hundreds of rows with a condition'() {
        given:
            //Let's get the contract id for mxn
            def contractId = streamOperations.context
                .selectFrom(INVESTING_CONTRACT)
                .where(INVESTING_CONTRACT.CURRENCY.eq('mxn'))
                .fetchAny()
                .get(INVESTING_CONTRACT.ID)

            def totalOfAccounts = streamOperations.context
                .selectCount()
                .from(INVESTING_ACCOUNT)
                .where(INVESTING_ACCOUNT.CONTRACT_ID.eq(contractId))
                .fetchOneInto(Integer)

        when:
            def stream = streamOperations.streamAllBy(INVESTING_ACCOUNT,
                INVESTING_ACCOUNT.CONTRACT_ID.eq(contractId), recordMapper)

        then:
            stream.isRight()
            stream.get().toList().size() == totalOfAccounts
    }

    def 'Test stream couple of hundreds of rows with no condition'() {
        when:
            streamOperations.streamAll(INVESTING_ACCOUNT, recordMapper)
                .get()
                .forEach({ account -> println account })
        then:
            true
    }

}
