package toby.spring.object.dependecy.user.sqlservice.updatable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import toby.spring.object.dependecy.user.sqlservice.SqlNotFoundException;

public class ConcurrentHashMapSqlRegistry implements UpdatableSqlRegistry {
  private Map<String, String> sqlMap = new ConcurrentHashMap<>();

  @Override
  public void updateSql(String key, String sql) throws SqlUpdateFailureException {
    if (sqlMap.get(key) == null) {
      throw new SqlUpdateFailureException(key + " not found");
    }
    sqlMap.put(key, sql);
  }

  @Override
  public void updateSql(Map<String, String> sqlmap) throws SqlUpdateFailureException {
    for (Map.Entry<String, String> entry : sqlmap.entrySet()) {
      updateSql(entry.getKey(), entry.getValue());
    }
  }

  @Override
  public void registerSql(String key, String sql) {
    sqlMap.put(key, sql);
  }

  @Override
  public String findSql(String key) throws SqlNotFoundException {
    String sql = sqlMap.get(key);
    if (sql == null) {
      throw new SqlNotFoundException(key + " not found");
    } else return sql;
  }
}
