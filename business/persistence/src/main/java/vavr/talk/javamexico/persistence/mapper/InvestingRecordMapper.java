package vavr.talk.javamexico.persistence.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;
import vavr.talk.javamexico.InvestingUser;
import vavr.talk.javamexico.investing.InvestingAccount;
import vavr.talk.javamexico.investing.InvestingContract;
import vavr.talk.javamexico.investing.InvestingContractMovement;
import vavr.talk.javamexico.persistence.jooq.tables.records.InvestingAccountRecord;
import vavr.talk.javamexico.persistence.jooq.tables.records.InvestingContractMovementRecord;
import vavr.talk.javamexico.persistence.jooq.tables.records.InvestingContractRecord;
import vavr.talk.javamexico.persistence.jooq.tables.records.InvestingUserRecord;

@Mapper(
  nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_DEFAULT,
  nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
  unmappedTargetPolicy = ReportingPolicy.IGNORE,
  unmappedSourcePolicy = ReportingPolicy.IGNORE,
  uses = DateTypesConverter.class
)
public interface InvestingRecordMapper {

  InvestingRecordMapper INSTANCE = Mappers.getMapper(InvestingRecordMapper.class);

  InvestingContractRecord from(InvestingContract investingContract);

  InvestingContract to(InvestingContractRecord investingContractRecord);

  InvestingAccountRecord from(InvestingAccount investingAccount);

  InvestingAccount to(InvestingAccountRecord investingAccountRecord);

  InvestingContractMovement from(InvestingContractMovement investingContractMovement);

  InvestingContractMovement to(InvestingContractMovementRecord investingContractMovementRecord);

  InvestingUser to(InvestingUserRecord investingUserRecord);

}
