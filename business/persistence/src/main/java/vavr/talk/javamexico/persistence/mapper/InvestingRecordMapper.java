package vavr.talk.javamexico.persistence.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import vavr.talk.javamexico.investing.InvestingTerm;
import vavr.talk.javamexico.persistence.jooq.tables.records.InvestingTermRecord;

@Mapper(
    uses = DateTypesConverter.class
)
public interface InvestingRecordMapper {

    InvestingRecordMapper INSTANCE = Mappers.getMapper(InvestingRecordMapper.class);

    InvestingTermRecord from(InvestingTerm investingTerm);

    InvestingTerm to(InvestingTermRecord investingTermRecord);

}
