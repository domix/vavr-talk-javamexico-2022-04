package vavr.talk.javamexico.persistence.mapper;

import io.vavr.control.Either;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import vavr.talk.javamexico.Failure;
import vavr.talk.javamexico.investing.InvestingTerm;
import vavr.talk.javamexico.jooq.api.JooqReadOperations;
import vavr.talk.javamexico.jooq.api.JooqWriteOperations;
import vavr.talk.javamexico.jooq.entity.Slice;
import vavr.talk.javamexico.jooq.transactional.TransactionAwareJooqReadOperations;
import vavr.talk.javamexico.jooq.transactional.TransactionAwareJooqWriteOperations;
import vavr.talk.javamexico.validation.BeanValidator;

import javax.sql.DataSource;

import static vavr.talk.javamexico.persistence.jooq.tables.InvestingTerm.INVESTING_TERM;
import static vavr.talk.javamexico.persistence.mapper.InvestingRecordMapper.INSTANCE;


@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class InvestingTermDbRepository {

    static final String DOMAIN_NAME = INVESTING_TERM.getName();

    private final JooqReadOperations jooqReadOperations;
    private final JooqWriteOperations jooqWriteOperations;
    private final BeanValidator<?> beanValidator;

    public static InvestingTermDbRepository create(final DataSource dataSource,
                                                   final BeanValidator<?> beanValidator) {
        final var writer = TransactionAwareJooqWriteOperations.create(dataSource, DOMAIN_NAME, beanValidator);
        final var reader = TransactionAwareJooqReadOperations.create(dataSource, DOMAIN_NAME);
        return new InvestingTermDbRepository(reader, writer, beanValidator);
    }

    public Either<Failure, InvestingTerm> save(final InvestingTerm investingTerm) {
        return beanValidator.validateBean(investingTerm)
            .map(INSTANCE::from)
            .flatMap(termRecord ->
                jooqWriteOperations.save(termRecord, INSTANCE::to));
    }

    public Either<Failure, Slice<InvestingTerm>> findAll() {
        return jooqReadOperations.findAll(INVESTING_TERM, INSTANCE::to);
    }

}
