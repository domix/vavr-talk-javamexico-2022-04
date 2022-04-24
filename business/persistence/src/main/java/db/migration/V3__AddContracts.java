package db.migration;

import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import util.DataUtil;

public class V3__AddContracts extends BaseJavaMigration {

  @Override
  public void migrate(Context context) throws Exception {
    DataUtil.populate(
      context,
      "insert into investing_contract (contract_name, currency, annual_interest_rate) values %s;",
      20,
      DataUtil::contractDataLine);

  }
}
