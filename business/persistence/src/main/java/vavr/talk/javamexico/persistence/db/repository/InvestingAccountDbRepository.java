package vavr.talk.javamexico.persistence.db.repository;

import io.vavr.control.Either;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.Select;
import vavr.talk.javamexico.Failure;
import vavr.talk.javamexico.investing.InvestingAccount;
import vavr.talk.javamexico.jooq.api.JooqReadOperations;
import vavr.talk.javamexico.jooq.api.JooqWriteOperations;
import vavr.talk.javamexico.jooq.transactional.TransactionAwareJooqReadOperations;
import vavr.talk.javamexico.jooq.transactional.TransactionAwareJooqWriteOperations;
import vavr.talk.javamexico.persistence.jooq.tables.records.InvestingAccountRecord;
import vavr.talk.javamexico.persistence.mapper.InvestingRecordMapper;
import vavr.talk.javamexico.repository.InvestingAccountRepository;
import vavr.talk.javamexico.validation.BeanValidator;

import javax.sql.DataSource;
import java.util.List;
import java.util.function.Function;

import static vavr.talk.javamexico.persistence.jooq.tables.InvestingAccount.INVESTING_ACCOUNT;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class InvestingAccountDbRepository implements InvestingAccountRepository {

  static final String DOMAIN_NAME = INVESTING_ACCOUNT.getName();

  private final JooqReadOperations jooqReadOperations;
  private final JooqWriteOperations jooqWriteOperations;
  private final BeanValidator<?> beanValidator;

  public static InvestingAccountDbRepository create(final DataSource dataSource,
                                                    final BeanValidator<?> beanValidator) {
    final var writer = TransactionAwareJooqWriteOperations.create(dataSource, DOMAIN_NAME, beanValidator);
    final var reader = TransactionAwareJooqReadOperations.create(dataSource, DOMAIN_NAME);
    return new InvestingAccountDbRepository(reader, writer, beanValidator);
  }

  @Override
  public Either<Failure, List<InvestingAccount>> findAllByUserId(final long userId) {
    final Function<DSLContext, Select<InvestingAccountRecord>> query =
      context -> context.selectFrom(INVESTING_ACCOUNT)
        .where(INVESTING_ACCOUNT.USER_ID.eq(userId));
    return jooqReadOperations.findAll(query, InvestingRecordMapper.INSTANCE::to);
  }

  @Override
  public Either<Failure, List<InvestingAccount>> findAllActiveAccounts(final long userId) {
    final Function<DSLContext, Select<InvestingAccountRecord>> query =
      context -> context.selectFrom(INVESTING_ACCOUNT)
        .where(INVESTING_ACCOUNT.USER_ID.eq(userId))
        .and(INVESTING_ACCOUNT.STATUS.eq("open"));
    return jooqReadOperations.findAll(query, InvestingRecordMapper.INSTANCE::to);
  }

}
