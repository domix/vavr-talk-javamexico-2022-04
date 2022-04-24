package vavr.talk.javamexico.persistence.context;

import io.micronaut.context.annotation.Bean;
import io.micronaut.context.annotation.Factory;
import vavr.talk.javamexico.persistence.mapper.InvestingTermDbRepository;
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
    public InvestingTermDbRepository investingTermDbRepository(
        final DataSource dataSource,
        final BeanValidator<?> beanValidator
    ) {
        return InvestingTermDbRepository.create(dataSource, beanValidator);
    }

}
