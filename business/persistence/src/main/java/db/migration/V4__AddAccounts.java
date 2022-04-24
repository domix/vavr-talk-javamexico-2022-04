package db.migration;

import org.apache.commons.lang3.RandomUtils;
import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import util.DataUtil;

import java.sql.Statement;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class V4__AddAccounts extends BaseJavaMigration {
  @Override
  public void migrate(Context context) throws Exception {
    try (Statement insertStmt = context.getConnection().createStatement()) {

      int accountCount = V2__AddUsers.USER_COUNT / 2;

      insertStmt.executeUpdate(
        DataUtil.massiveInsert(
          "insert into investing_account (contract_id, user_id, status, start_balance, current_balance) values %s;",
          accountCount,
          V4__AddAccounts::dataLine)
      );
    }
  }

  public static String dataLine() {
    int userId = RandomUtils.nextInt(1, V2__AddUsers.USER_COUNT);
    int userAccountCount = RandomUtils.nextInt(4, 7);
    final var initialBalance = DataUtil.faker.commerce().price(1_000, 200_000);
    return IntStream.rangeClosed(1, userAccountCount)
      .mapToObj(value -> {
        int contractId = RandomUtils.nextInt(1, V3__AddContracts.CONTRACT_COUNT);
        return "(%d, %d, 'open', '%s', '%s')".formatted(contractId, userId, initialBalance, initialBalance);
      })
      .collect(Collectors.joining(","));
  }
}
