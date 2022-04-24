package vavr.talk.javamexico.persistence.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;
import vavr.talk.javamexico.InvestingUser;
import vavr.talk.javamexico.persistence.jooq.tables.records.InvestingUserRecord;

@Mapper(
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_DEFAULT,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    unmappedSourcePolicy = ReportingPolicy.IGNORE,
    uses = DateTypesConverter.class
)
public interface InvestingUserRecordMapper {

    InvestingUserRecordMapper INSTANCE = Mappers.getMapper(InvestingUserRecordMapper.class);

    InvestingUserRecord from(InvestingUser investingUser);

    InvestingUser to(InvestingUserRecord investingUserRecord);

}
