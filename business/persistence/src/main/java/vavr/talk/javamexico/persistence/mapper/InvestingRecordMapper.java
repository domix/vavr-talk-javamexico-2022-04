package vavr.talk.javamexico.persistence.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;
import vavr.talk.javamexico.investing.InvestingTerm;
import vavr.talk.javamexico.investing.InvestingUserInterest;
import vavr.talk.javamexico.persistence.jooq.tables.records.InvestingTermRecord;
import vavr.talk.javamexico.persistence.jooq.tables.records.InvestingUserInterestRecord;

@Mapper(
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_DEFAULT,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    unmappedSourcePolicy = ReportingPolicy.IGNORE,
    uses = DateTypesConverter.class
)
public interface InvestingRecordMapper {

    InvestingRecordMapper INSTANCE = Mappers.getMapper(InvestingRecordMapper.class);

    InvestingTermRecord from(InvestingTerm investingTerm);

    InvestingTerm to(InvestingTermRecord investingTermRecord);

    InvestingUserInterestRecord from(InvestingUserInterest userInterest);

    InvestingUserInterest to(InvestingUserInterestRecord userInterestRecord);

}
