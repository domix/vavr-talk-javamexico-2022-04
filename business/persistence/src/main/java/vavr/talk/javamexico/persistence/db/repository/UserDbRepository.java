package vavr.talk.javamexico.persistence.db.repository;

import io.vavr.control.Either;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.Select;
import vavr.talk.javamexico.Failure;
import vavr.talk.javamexico.User;
import vavr.talk.javamexico.jooq.api.JooqReadOperations;
import vavr.talk.javamexico.jooq.api.JooqWriteOperations;
import vavr.talk.javamexico.jooq.transactional.TransactionAwareJooqReadOperations;
import vavr.talk.javamexico.jooq.transactional.TransactionAwareJooqWriteOperations;
import vavr.talk.javamexico.persistence.jooq.tables.records.UserRecord;
import vavr.talk.javamexico.persistence.mapper.UserRecordMapper;
import vavr.talk.javamexico.validation.BeanValidator;

import javax.sql.DataSource;
import java.util.function.Function;

import static vavr.talk.javamexico.persistence.jooq.tables.User.USER;


@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class UserDbRepository {

    static final String DOMAIN_NAME = USER.getName();
    private final JooqWriteOperations writeOperations;

    private final JooqReadOperations readOperations;
    private final BeanValidator<?> beanValidator;

    public static UserDbRepository create(final DataSource dataSource,
                                          final BeanValidator<?> beanValidator) {
        final var writer = TransactionAwareJooqWriteOperations.create(dataSource, DOMAIN_NAME, beanValidator);
        final var reader = TransactionAwareJooqReadOperations.create(dataSource, DOMAIN_NAME);
        return new UserDbRepository(writer, reader, beanValidator);
    }

    public Either<Failure, User> save(final User user) {
        return beanValidator.validateBean(user)
            .map(UserRecordMapper.INSTANCE::from)
            .flatMap(record -> writeOperations.save(record, UserRecordMapper.INSTANCE::to));
    }

    public Either<Failure, User> get(final long userId) {
        final Function<DSLContext, Select<UserRecord>> query =
            context -> context.selectFrom(USER)
                .where(USER.ID.eq(userId));
        return readOperations.get(query, UserRecordMapper.INSTANCE::to);
    }

}
