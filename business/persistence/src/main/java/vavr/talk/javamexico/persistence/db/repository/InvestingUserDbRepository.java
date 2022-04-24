package vavr.talk.javamexico.persistence.db.repository;

import io.vavr.control.Either;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.Select;
import vavr.talk.javamexico.Failure;
import vavr.talk.javamexico.InvestingUser;
import vavr.talk.javamexico.jooq.api.JooqReadOperations;
import vavr.talk.javamexico.jooq.api.JooqWriteOperations;
import vavr.talk.javamexico.jooq.transactional.TransactionAwareJooqReadOperations;
import vavr.talk.javamexico.jooq.transactional.TransactionAwareJooqWriteOperations;
import vavr.talk.javamexico.persistence.jooq.tables.records.InvestingUserRecord;
import vavr.talk.javamexico.persistence.mapper.InvestingUserRecordMapper;
import vavr.talk.javamexico.repository.InvestingUserRepository;
import vavr.talk.javamexico.validation.BeanValidator;

import javax.sql.DataSource;
import java.util.function.Function;

import static vavr.talk.javamexico.persistence.jooq.tables.InvestingUser.INVESTING_USER;


@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class InvestingUserDbRepository implements InvestingUserRepository {

    static final String DOMAIN_NAME = INVESTING_USER.getName();
    private final JooqWriteOperations writeOperations;

    private final JooqReadOperations readOperations;
    private final BeanValidator<?> beanValidator;

    public static InvestingUserDbRepository create(final DataSource dataSource,
                                                   final BeanValidator<?> beanValidator) {
        final var writer = TransactionAwareJooqWriteOperations.create(dataSource, DOMAIN_NAME, beanValidator);
        final var reader = TransactionAwareJooqReadOperations.create(dataSource, DOMAIN_NAME);
        return new InvestingUserDbRepository(writer, reader, beanValidator);
    }

    @Override
    public Either<Failure, InvestingUser> save(final InvestingUser investingUser) {
        return beanValidator.validateBean(investingUser)
            .map(InvestingUserRecordMapper.INSTANCE::from)
            .flatMap(record -> writeOperations.save(record, InvestingUserRecordMapper.INSTANCE::to));
    }

    @Override

    public Either<Failure, InvestingUser> find(final long userId) {
        final Function<DSLContext, Select<InvestingUserRecord>> query =
            context -> context.selectFrom(INVESTING_USER)
                .where(INVESTING_USER.ID.eq(userId));
        return readOperations.get(query, InvestingUserRecordMapper.INSTANCE::to);
    }

}
