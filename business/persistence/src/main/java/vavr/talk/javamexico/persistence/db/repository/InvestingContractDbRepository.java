package vavr.talk.javamexico.persistence.db.repository;

import io.vavr.control.Either;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import vavr.talk.javamexico.Failure;
import vavr.talk.javamexico.investing.InvestingContract;
import vavr.talk.javamexico.jooq.api.JooqReadOperations;
import vavr.talk.javamexico.jooq.api.JooqWriteOperations;
import vavr.talk.javamexico.jooq.transactional.TransactionAwareJooqReadOperations;
import vavr.talk.javamexico.jooq.transactional.TransactionAwareJooqWriteOperations;
import vavr.talk.javamexico.validation.BeanValidator;

import javax.sql.DataSource;

import static vavr.talk.javamexico.persistence.jooq.tables.InvestingContract.INVESTING_CONTRACT;
import static vavr.talk.javamexico.persistence.mapper.InvestingRecordMapper.INSTANCE;


@NoArgsConstructor(access = AccessLevel.PACKAGE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class InvestingContractDbRepository {

    static final String DOMAIN_NAME = INVESTING_CONTRACT.getName();

    private JooqReadOperations jooqReadOperations;
    private JooqWriteOperations jooqWriteOperations;
    private BeanValidator<?> beanValidator;

    public static InvestingContractDbRepository create(final DataSource dataSource,
                                                       final BeanValidator<?> beanValidator) {
        final var writer = TransactionAwareJooqWriteOperations.create(dataSource, DOMAIN_NAME, beanValidator);
        final var reader = TransactionAwareJooqReadOperations.create(dataSource, DOMAIN_NAME);
        return new InvestingContractDbRepository(reader, writer, beanValidator);
    }

    public Either<Failure, InvestingContract> save(final InvestingContract investingContract) {
        return beanValidator.validateBean(investingContract)
            .map(INSTANCE::from)
            .flatMap(termRecord ->
                jooqWriteOperations.save(termRecord, INSTANCE::to));
    }

}
