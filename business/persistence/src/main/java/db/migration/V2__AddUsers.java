package db.migration;

import com.github.javafaker.Faker;
import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;

import java.sql.Statement;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class V2__AddUsers extends BaseJavaMigration {
  private static Faker faker = Faker.instance();

  @Override
  public void migrate(Context context) throws Exception {

    final var dataLines = IntStream.rangeClosed(1, 2_000_000)
      .mapToObj(__ -> dataLine())
      .collect(Collectors.joining(","));

    try (Statement insertStmt = context.getConnection().createStatement()) {
      String massiveInsert = "insert into investing_user (first_name, last_name, email) values %s;"
        .formatted(dataLines);
      insertStmt.executeUpdate(massiveInsert);
    }

  }

  public String dataLine() {
    return "('%s','%s','%s')"
      .formatted(
        faker.name().firstName().replace("'", "''"),
        faker.name().lastName().replace("'", "''"),
        faker.internet().emailAddress()
      );
  }
}
