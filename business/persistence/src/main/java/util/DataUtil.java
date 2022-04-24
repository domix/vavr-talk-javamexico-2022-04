package util;

import com.github.javafaker.Faker;
import org.flywaydb.core.api.migration.Context;

import java.sql.Statement;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class DataUtil {
  private static final Faker faker = Faker.instance();
  private static final List<String> currencies = List.of("mxn", "usd", "ars");

  public static String userDataLine() {
    return "('%s','%s','%s')"
      .formatted(
        escapeSomeCharsIfNeeded(faker.name().firstName()),
        escapeSomeCharsIfNeeded(faker.name().lastName()),
        faker.internet().emailAddress()
      );
  }

  public static String contractDataLine() {

    final var name = escapeSomeCharsIfNeeded(faker.commerce().productName());
    final var rand = new Random();
    final var randomElement = currencies.get(rand.nextInt(currencies.size()));
    final var price = faker.commerce().price(1, 15);

    return "('%s', '%s', '%s')".formatted(name, randomElement, price);
  }

  public static String escapeSomeCharsIfNeeded(String source) {
    return source.replace("'", "''");
  }

  public static String massiveDataLine(int count, Supplier<String> mapper) {
    return IntStream.rangeClosed(1, count)
      .mapToObj(__ -> mapper.get())
      .collect(Collectors.joining(","));
  }

  public static String massiveInsert(String insertSQL, int count, Supplier<String> mapper) {
    return insertSQL
      .formatted(DataUtil.massiveDataLine(count, mapper));
  }

  public static void populate(Context context, String insertSQL, int count, Supplier<String> mapper) throws Exception {
    try (Statement insertStmt = context.getConnection().createStatement()) {
      insertStmt.executeUpdate(
        DataUtil.massiveInsert(
          insertSQL,
          count,
          mapper)
      );
    }
  }
}
