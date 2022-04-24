package vavr.talk.javamexico.persistence.db.repository;

import io.vavr.control.Either;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.Select;
import org.jooq.UpdateReturningStep;
import vavr.talk.javamexico.Failure;
import vavr.talk.javamexico.investing.InvestingContractMovement;
import vavr.talk.javamexico.jooq.api.JooqReadOperations;
import vavr.talk.javamexico.jooq.api.JooqStreamOperations;
import vavr.talk.javamexico.jooq.api.JooqWriteOperations;
import vavr.talk.javamexico.jooq.stream.JooqReadStreamOperations;
import vavr.talk.javamexico.jooq.transactional.TransactionAwareJooqReadOperations;
import vavr.talk.javamexico.jooq.transactional.TransactionAwareJooqWriteOperations;
import vavr.talk.javamexico.persistence.jooq.tables.records.InvestingContractMovementRecord;
import vavr.talk.javamexico.repository.InvestingContractMovementRepository;
import vavr.talk.javamexico.validation.BeanValidator;

import javax.sql.DataSource;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

import static vavr.talk.javamexico.persistence.jooq.tables.InvestingContractMovement.INVESTING_CONTRACT_MOVEMENT;
import static vavr.talk.javamexico.persistence.mapper.InvestingRecordMapper.INSTANCE;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class InvestingContractMovementDbRepository implements InvestingContractMovementRepository {

  static final String DOMAIN_NAME = INVESTING_CONTRACT_MOVEMENT.getName();
  private final JooqWriteOperations writeOperations;
  private final JooqReadOperations readOperations;
  private final JooqStreamOperations streamOperations;
  private final BeanValidator<?> beanValidator;

  public static InvestingContractMovementDbRepository create(final DataSource dataSource,
                                                             final BeanValidator<?> beanValidator) {
    final var writer = TransactionAwareJooqWriteOperations.create(dataSource, DOMAIN_NAME, beanValidator);
    final var reader = TransactionAwareJooqReadOperations.create(dataSource, DOMAIN_NAME);
    final var streamer = JooqReadStreamOperations.create(dataSource, DOMAIN_NAME);
    return new InvestingContractMovementDbRepository(writer, reader, streamer, beanValidator);
  }

  @Override
  public Either<Failure, InvestingContractMovement> save(final InvestingContractMovement contractMovement) {
    return beanValidator.validateBean(contractMovement)
      .map(INSTANCE::from)
      .flatMap(record -> writeOperations.save(record, INSTANCE::to));
  }

  @Override
  public Either<Failure, InvestingContractMovement> find(final long id) {
    final Function<DSLContext, Select<InvestingContractMovementRecord>> query =
      context -> context.selectFrom(INVESTING_CONTRACT_MOVEMENT)
        .where(INVESTING_CONTRACT_MOVEMENT.ID.eq(id));
    return readOperations.get(query, INSTANCE::to);
  }

  @Override
  public Either<Failure, List<InvestingContractMovement>> findAllByAccountId(final long accountId) {
    final Function<DSLContext, Select<InvestingContractMovementRecord>> query =
      context -> context.selectFrom(INVESTING_CONTRACT_MOVEMENT)
        .where(INVESTING_CONTRACT_MOVEMENT.ACCOUNT_ID.eq(accountId));
    return readOperations.findAll(query, INSTANCE::to);
  }

  @Override
  public Either<Failure, Stream<InvestingContractMovement>> streamAllByAccountId(final long accountId) {
    return streamOperations.streamAllBy(INVESTING_CONTRACT_MOVEMENT,
      INVESTING_CONTRACT_MOVEMENT.ACCOUNT_ID.eq(accountId), INSTANCE::to);
  }

  @Override
  public Either<Failure, InvestingContractMovement> update(final InvestingContractMovement contractMovement) {
    final Function<DSLContext, UpdateReturningStep<InvestingContractMovementRecord>> updater =
      context -> context.update(INVESTING_CONTRACT_MOVEMENT)
        .set(INVESTING_CONTRACT_MOVEMENT.AMOUNT, contractMovement.getAmount().toPlainString())
        .set(INVESTING_CONTRACT_MOVEMENT.MOVEMENT_TYPE, contractMovement.getMovementType());
    return writeOperations.updateAndMap(updater, INSTANCE::to);
  }

}
