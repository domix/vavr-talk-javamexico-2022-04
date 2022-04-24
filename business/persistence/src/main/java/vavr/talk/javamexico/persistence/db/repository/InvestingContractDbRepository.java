package vavr.talk.javamexico.persistence.db.repository;

import io.vavr.control.Either;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.Select;
import org.jooq.UpdateReturningStep;
import vavr.talk.javamexico.Failure;
import vavr.talk.javamexico.investing.InvestingContract;
import vavr.talk.javamexico.jooq.api.JooqReadOperations;
import vavr.talk.javamexico.jooq.api.JooqStreamOperations;
import vavr.talk.javamexico.jooq.api.JooqWriteOperations;
import vavr.talk.javamexico.jooq.stream.JooqReadStreamOperations;
import vavr.talk.javamexico.jooq.transactional.TransactionAwareJooqReadOperations;
import vavr.talk.javamexico.jooq.transactional.TransactionAwareJooqWriteOperations;
import vavr.talk.javamexico.persistence.jooq.tables.records.InvestingContractRecord;
import vavr.talk.javamexico.repository.InvestingContractRepository;
import vavr.talk.javamexico.validation.BeanValidator;

import javax.sql.DataSource;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

import static org.apache.commons.lang3.ObjectUtils.defaultIfNull;
import static vavr.talk.javamexico.persistence.jooq.tables.InvestingContract.INVESTING_CONTRACT;
import static vavr.talk.javamexico.persistence.mapper.InvestingRecordMapper.INSTANCE;


@NoArgsConstructor(access = AccessLevel.PACKAGE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class InvestingContractDbRepository implements InvestingContractRepository {

  static final String DOMAIN_NAME = INVESTING_CONTRACT.getName();

  private JooqReadOperations jooqReadOperations;
  private JooqWriteOperations jooqWriteOperations;

  private JooqStreamOperations streamOperations;
  private BeanValidator<?> beanValidator;

  public static InvestingContractDbRepository create(final DataSource dataSource,
                                                     final BeanValidator<?> beanValidator) {
    final var writer = TransactionAwareJooqWriteOperations.create(dataSource, DOMAIN_NAME, beanValidator);
    final var reader = TransactionAwareJooqReadOperations.create(dataSource, DOMAIN_NAME);
    final var streamer = JooqReadStreamOperations.create(dataSource, DOMAIN_NAME);
    return new InvestingContractDbRepository(reader, writer, streamer, beanValidator);
  }

  @Override
  public Either<Failure, InvestingContract> save(final InvestingContract investingContract) {
    return beanValidator.validateBean(investingContract)
      .map(INSTANCE::from)
      .flatMap(termRecord ->
        jooqWriteOperations.save(termRecord, INSTANCE::to));
  }

  @Override
  public Either<Failure, InvestingContract> find(final long id) {
    final Function<DSLContext, Select<InvestingContractRecord>> query =
      context -> context.selectFrom(INVESTING_CONTRACT)
        .where(INVESTING_CONTRACT.ID.eq(id));
    return jooqReadOperations.get(query, INSTANCE::to);
  }

  @Override
  public Either<Failure, List<InvestingContract>> findAll() {
    final Function<DSLContext, Select<InvestingContractRecord>> query =
      context -> context.selectFrom(INVESTING_CONTRACT);
    return jooqReadOperations.findAll(query, INSTANCE::to);
  }

  @Override
  public Either<Failure, Stream<InvestingContract>> streamAll() {
    return streamOperations.streamAll(INVESTING_CONTRACT, INSTANCE::to);
  }

  @Override
  public Either<Failure, InvestingContract> update(final InvestingContract investingContract) {
    final Function<DSLContext, UpdateReturningStep<InvestingContractRecord>> updater =
      context -> context
        .update(INVESTING_CONTRACT)
        .set(INVESTING_CONTRACT.ANNUAL_INTEREST_RATE, investingContract.getAnnualInterestRate())
        .set(INVESTING_CONTRACT.CONTRACT_NAME, investingContract.getContractName())
        .set(INVESTING_CONTRACT.CURRENCY, investingContract.getCurrency())
        .set(INVESTING_CONTRACT.UPDATED_AT, defaultIfNull(investingContract.getUpdatedAt(), OffsetDateTime.now()));
    return jooqWriteOperations.updateAndMap(updater, INSTANCE::to);
  }

}
