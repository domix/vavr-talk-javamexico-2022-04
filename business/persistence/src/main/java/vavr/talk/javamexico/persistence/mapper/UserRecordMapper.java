package vavr.talk.javamexico.persistence.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;
import vavr.talk.javamexico.User;
import vavr.talk.javamexico.persistence.jooq.tables.records.UserRecord;

@Mapper(
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_DEFAULT,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    unmappedSourcePolicy = ReportingPolicy.IGNORE,
    uses = DateTypesConverter.class
)
public interface UserRecordMapper {
    UserRecordMapper INSTANCE = Mappers.getMapper(UserRecordMapper.class);

    UserRecord from(User user);

    User to(UserRecord userRecord);
}
