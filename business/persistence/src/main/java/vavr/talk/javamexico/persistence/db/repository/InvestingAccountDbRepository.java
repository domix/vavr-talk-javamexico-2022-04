package vavr.talk.javamexico.persistence.db.repository;

import io.vavr.control.Either;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.Select;
import org.jooq.UpdateReturningStep;
import vavr.talk.javamexico.Failure;
import vavr.talk.javamexico.investing.InvestingAccount;
import vavr.talk.javamexico.jooq.api.JooqReadOperations;
import vavr.talk.javamexico.jooq.api.JooqStreamOperations;
import vavr.talk.javamexico.jooq.api.JooqWriteOperations;
import vavr.talk.javamexico.jooq.stream.JooqReadStreamOperations;
import vavr.talk.javamexico.jooq.transactional.TransactionAwareJooqReadOperations;
import vavr.talk.javamexico.jooq.transactional.TransactionAwareJooqWriteOperations;
import vavr.talk.javamexico.persistence.jooq.tables.records.InvestingAccountRecord;
import vavr.talk.javamexico.repository.InvestingAccountRepository;
import vavr.talk.javamexico.validation.BeanValidator;

import javax.sql.DataSource;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

import static org.apache.commons.lang3.ObjectUtils.defaultIfNull;
import static vavr.talk.javamexico.persistence.jooq.tables.InvestingAccount.INVESTING_ACCOUNT;
import static vavr.talk.javamexico.persistence.mapper.InvestingRecordMapper.INSTANCE;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class InvestingAccountDbRepository implements InvestingAccountRepository {

  static final String DOMAIN_NAME = INVESTING_ACCOUNT.getName();

  private final JooqReadOperations jooqReadOperations;
  private final JooqWriteOperations jooqWriteOperations;

  private final JooqStreamOperations streamOperations;
  private final BeanValidator<?> beanValidator;

  public static InvestingAccountDbRepository create(final DataSource dataSource,
                                                    final BeanValidator<?> beanValidator) {
    final var writer = TransactionAwareJooqWriteOperations.create(dataSource, DOMAIN_NAME, beanValidator);
    final var reader = TransactionAwareJooqReadOperations.create(dataSource, DOMAIN_NAME);
    final var streamer = JooqReadStreamOperations.create(dataSource, DOMAIN_NAME);
    return new InvestingAccountDbRepository(reader, writer, streamer, beanValidator);
  }

  @Override
  public Either<Failure, InvestingAccount> save(final InvestingAccount investingAccount) {
    return beanValidator.validateBean(investingAccount)
      .map(INSTANCE::from)
      .flatMap(record -> jooqWriteOperations.save(record, INSTANCE::to));
  }

  @Override
  public Either<Failure, InvestingAccount> find(final long id) {
    final Function<DSLContext, Select<InvestingAccountRecord>> query =
      context -> context.selectFrom(INVESTING_ACCOUNT)
        .where(INVESTING_ACCOUNT.ID.eq(id));
    return jooqReadOperations.get(query, INSTANCE::to);
  }

  @Override
  public Either<Failure, List<InvestingAccount>> findAllByUserId(final long userId) {
    final Function<DSLContext, Select<InvestingAccountRecord>> query =
      context -> context.selectFrom(INVESTING_ACCOUNT)
        .where(INVESTING_ACCOUNT.USER_ID.eq(userId));
    return jooqReadOperations.findAll(query, INSTANCE::to);
  }

  @Override
  public Either<Failure, List<InvestingAccount>> findAllActiveAccounts(final long userId) {
    final Function<DSLContext, Select<InvestingAccountRecord>> query =
      context -> context.selectFrom(INVESTING_ACCOUNT)
        .where(INVESTING_ACCOUNT.USER_ID.eq(userId))
        .and(INVESTING_ACCOUNT.STATUS.eq("open"));
    return jooqReadOperations.findAll(query, INSTANCE::to);
  }

  @Override
  public Either<Failure, Stream<InvestingAccount>> streamAllActiveAccounts(final long userId) {
    final var conditions = INVESTING_ACCOUNT.USER_ID.eq(userId)
      .and(INVESTING_ACCOUNT.STATUS.eq("open"));
    return streamOperations.streamAllBy(INVESTING_ACCOUNT, conditions, INSTANCE::to);
  }

  @Override
  public Either<Failure, InvestingAccount> update(final InvestingAccount investingAccount) {
    final Function<DSLContext, UpdateReturningStep<InvestingAccountRecord>> updater =
      context -> context.update(INVESTING_ACCOUNT)
        .set(INVESTING_ACCOUNT.STATUS, investingAccount.getStatus())
        .set(INVESTING_ACCOUNT.CURRENT_BALANCE, investingAccount.getCurrentBalance().toPlainString())
        .set(INVESTING_ACCOUNT.UPDATED_AT, defaultIfNull(investingAccount.getUpdatedAt(), OffsetDateTime.now()));
    return jooqWriteOperations.updateAndMap(updater, INSTANCE::to);
  }

}
