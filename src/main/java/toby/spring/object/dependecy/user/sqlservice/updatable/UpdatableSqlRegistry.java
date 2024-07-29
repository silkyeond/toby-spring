package toby.spring.object.dependecy.user.sqlservice.updatable;

import java.util.Map;
import toby.spring.object.dependecy.user.sqlservice.SqlRegistry;

public interface UpdatableSqlRegistry extends SqlRegistry {
  void updateSql(String key, String sql) throws SqlUpdateFailureException;

  void updateSql(Map<String, String> sqlmap) throws SqlUpdateFailureException;
}
