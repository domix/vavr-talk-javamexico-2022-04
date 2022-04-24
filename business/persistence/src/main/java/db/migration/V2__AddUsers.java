package db.migration;

import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import util.DataUtil;

public class V2__AddUsers extends BaseJavaMigration {

  @Override
  public void migrate(Context context) throws Exception {
    DataUtil.populate(
      context,
      "insert into investing_user (first_name, last_name, email) values %s;",
      2_000_000,
      DataUtil::userDataLine
    );
  }
}
