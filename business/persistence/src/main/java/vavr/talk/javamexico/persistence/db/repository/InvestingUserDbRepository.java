package vavr.talk.javamexico.persistence.db.repository;

import io.vavr.control.Either;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.Select;
import org.jooq.UpdateReturningStep;
import vavr.talk.javamexico.Failure;
import vavr.talk.javamexico.investing.InvestingUser;
import vavr.talk.javamexico.jooq.api.JooqReadOperations;
import vavr.talk.javamexico.jooq.api.JooqStreamOperations;
import vavr.talk.javamexico.jooq.api.JooqWriteOperations;
import vavr.talk.javamexico.jooq.stream.JooqReadStreamOperations;
import vavr.talk.javamexico.jooq.transactional.TransactionAwareJooqReadOperations;
import vavr.talk.javamexico.jooq.transactional.TransactionAwareJooqWriteOperations;
import vavr.talk.javamexico.persistence.jooq.tables.records.InvestingUserRecord;
import vavr.talk.javamexico.repository.InvestingUserRepository;
import vavr.talk.javamexico.validation.BeanValidator;

import javax.sql.DataSource;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

import static org.apache.commons.lang3.ObjectUtils.defaultIfNull;
import static vavr.talk.javamexico.persistence.jooq.tables.InvestingUser.INVESTING_USER;
import static vavr.talk.javamexico.persistence.mapper.InvestingUserRecordMapper.INSTANCE;


@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class InvestingUserDbRepository implements InvestingUserRepository {

  static final String DOMAIN_NAME = INVESTING_USER.getName();
  private final JooqWriteOperations writeOperations;

  private final JooqReadOperations readOperations;

  private final JooqStreamOperations streamOperations;
  private final BeanValidator<?> beanValidator;

  public static InvestingUserRepository create(final DataSource dataSource,
                                                 final BeanValidator<?> beanValidator) {
    final var writer = TransactionAwareJooqWriteOperations.create(dataSource, DOMAIN_NAME, beanValidator);
    final var reader = TransactionAwareJooqReadOperations.create(dataSource, DOMAIN_NAME);
    final var streamer = JooqReadStreamOperations.create(dataSource, DOMAIN_NAME);
    return new InvestingUserDbRepository(writer, reader, streamer, beanValidator);
  }

  @Override
  public Either<Failure, InvestingUser> save(final InvestingUser investingUser) {
    return beanValidator.validateBean(investingUser)
      .map(INSTANCE::from)
      .flatMap(record -> writeOperations.save(record, INSTANCE::to));
  }

  @Override
  public Either<Failure, List<InvestingUser>> findAll() {
    final Function<DSLContext, Select<InvestingUserRecord>> query =
      context -> context.selectFrom(INVESTING_USER);
    return readOperations.findAll(query, INSTANCE::to);
  }

  @Override
  public Either<Failure, Stream<InvestingUser>> streamAll() {
    return streamOperations.streamAll(INVESTING_USER, INSTANCE::to);
  }

  @Override
  public Either<Failure, InvestingUser> update(final InvestingUser investingUser) {
    final Function<DSLContext, UpdateReturningStep<InvestingUserRecord>> updater =
      context -> context.update(INVESTING_USER)
        .set(INVESTING_USER.EMAIL, investingUser.getEmail())
        .set(INVESTING_USER.FIRST_NAME, investingUser.getFirstName())
        .set(INVESTING_USER.LAST_NAME, investingUser.getLastName())
        .set(INVESTING_USER.UPDATED_AT, defaultIfNull(investingUser.getUpdatedAt(), OffsetDateTime.now()));
    return writeOperations.updateAndMap(updater, INSTANCE::to);
  }

  @Override
  public Either<Failure, InvestingUser> find(final long userId) {
    final Function<DSLContext, Select<InvestingUserRecord>> query =
      context -> context.selectFrom(INVESTING_USER)
        .where(INVESTING_USER.ID.eq(userId));
    return readOperations.get(query, INSTANCE::to);
  }

}
