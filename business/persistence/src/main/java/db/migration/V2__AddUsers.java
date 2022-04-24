package db.migration;

import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import util.DataUtil;

public class V2__AddUsers extends BaseJavaMigration {
  public static final int USER_COUNT = 2000;

  @Override
  public void migrate(Context context) throws Exception {
    DataUtil.populate(
      context,
      "insert into investing_user (first_name, last_name, email) values %s;",
      USER_COUNT,
      DataUtil::userDataLine
    );
  }
}
