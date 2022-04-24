package vavr.talk.javamexico.persistence.context;

import io.micronaut.context.annotation.Bean;
import io.micronaut.context.annotation.Factory;
import vavr.talk.javamexico.persistence.db.repository.InvestingContractDbRepository;
import vavr.talk.javamexico.persistence.db.repository.InvestingUserDbRepository;
import vavr.talk.javamexico.validation.BeanValidator;
import vavr.talk.javamexico.validation.jakarta.JakartaBeanValidation;

import javax.sql.DataSource;

@Factory
public class DbRepositoryContext {

    @Bean
    public BeanValidator<?> beanValidator() {
        return JakartaBeanValidation.ofDefaults();
    }

    @Bean
    public InvestingUserDbRepository investingUserInterestDbRepository(
        final DataSource dataSource,
        final BeanValidator<?> beanValidator
    ) {
        return InvestingUserDbRepository.create(dataSource, beanValidator);
    }

    @Bean
    public InvestingContractDbRepository investingContractDbRepository(
        final DataSource dataSource,
        final BeanValidator<?> beanValidator
    ) {
        return InvestingContractDbRepository.create(dataSource, beanValidator);
    }

}
