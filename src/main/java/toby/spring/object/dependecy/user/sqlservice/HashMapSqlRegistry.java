package toby.spring.object.dependecy.user.sqlservice;

import java.util.HashMap;
import java.util.Map;

public class HashMapSqlRegistry implements SqlRegistry {
  private Map<String, String> sqlMap = new HashMap<>();

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
