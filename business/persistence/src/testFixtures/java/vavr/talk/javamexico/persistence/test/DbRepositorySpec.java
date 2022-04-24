package vavr.talk.javamexico.persistence.test;

import io.vavr.control.Try;
import org.apache.commons.dbcp2.BasicDataSourceFactory;
import vavr.talk.javamexico.validation.BeanValidator;
import vavr.talk.javamexico.validation.jakarta.JakartaBeanValidation;

import javax.sql.DataSource;
import java.util.Properties;

public final class DbRepositorySpec {

  private static final Properties properties = new Properties();
  private static DataSource dataSource;
  private static final BeanValidator<?> beanValidator = JakartaBeanValidation.ofDefaults();

  static {
    properties.setProperty("url", "jdbc:postgresql://localhost:5434/investing");
    properties.setProperty("username", "admin");
    properties.setProperty("password", "password");
    properties.setProperty("maxIdle", "5");
  }

  public void init() {
    dataSource = Try.of(() -> BasicDataSourceFactory.createDataSource(properties))
      .getOrElseThrow(() -> new RuntimeException("Cannot create a datasource for %s".formatted(properties)));
  }

  public BeanValidator<?> getBeanValidator() {
    return beanValidator;
  }

  public DataSource getDataSource() {
    return dataSource;
  }

}
