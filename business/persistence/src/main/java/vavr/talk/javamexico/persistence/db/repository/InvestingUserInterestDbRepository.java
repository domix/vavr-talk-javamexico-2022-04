package vavr.talk.javamexico.persistence.db.repository;

import io.vavr.collection.Stream;
import io.vavr.control.Either;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import vavr.talk.javamexico.Failure;
import vavr.talk.javamexico.investing.InvestingUserInterest;
import vavr.talk.javamexico.jooq.api.JooqWriteOperations;
import vavr.talk.javamexico.jooq.stream.JooqReadStreamOperations;
import vavr.talk.javamexico.jooq.transactional.TransactionAwareJooqWriteOperations;
import vavr.talk.javamexico.persistence.mapper.InvestingRecordMapper;
import vavr.talk.javamexico.validation.BeanValidator;

import javax.sql.DataSource;

import static vavr.talk.javamexico.persistence.jooq.tables.InvestingUserInterest.INVESTING_USER_INTEREST;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class InvestingUserInterestDbRepository {

    static final String DOMAIN_NAME = INVESTING_USER_INTEREST.getName();
    private final JooqReadStreamOperations jooqReadStreamOperations;
    private final JooqWriteOperations writeOperations;
    private final BeanValidator<?> beanValidator;

    public static InvestingUserInterestDbRepository create(final DataSource dataSource,
                                                          final BeanValidator<?> beanValidator) {
        final var streamer = JooqReadStreamOperations.create(dataSource, DOMAIN_NAME);
        final var writer = TransactionAwareJooqWriteOperations.create(dataSource, DOMAIN_NAME, beanValidator);
        return new InvestingUserInterestDbRepository(streamer, writer, beanValidator);
    }

    public Either<Failure, InvestingUserInterest> save(final InvestingUserInterest userInterest) {
        return beanValidator.validateBean(userInterest)
            .map(InvestingRecordMapper.INSTANCE::from)
            .flatMap(record -> writeOperations.save(record, InvestingRecordMapper.INSTANCE::to));
    }

    public Either<Failure, Stream<InvestingUserInterest>> streamAllByUserId(final long userId) {
        return jooqReadStreamOperations.streamAllBy(
            INVESTING_USER_INTEREST,
            INVESTING_USER_INTEREST.USER_ID.eq(userId),
            InvestingRecordMapper.INSTANCE::to
        );
    }

}
