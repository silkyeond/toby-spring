package toby.spring.object.dependecy.user.sqlservice.updatable;

import java.util.HashMap;
import java.util.Map;
import javax.sql.DataSource;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;
import toby.spring.object.dependecy.user.sqlservice.SqlNotFoundException;

public class EmbeddedDbSqlRegistry implements UpdatableSqlRegistry {
  NamedParameterJdbcTemplate jdbc;
  TransactionTemplate transactionTemplate;

  public void setDataSource(DataSource dataSource) {
    jdbc = new NamedParameterJdbcTemplate(dataSource);
    transactionTemplate = new TransactionTemplate(new DataSourceTransactionManager(dataSource));
  }

  @Override
  public void updateSql(String key, String sql) throws SqlUpdateFailureException {
    Map<String, String> params = new HashMap<>();
    params.put("sql_", sql);
    params.put("key_", key);
    int affected = jdbc.update("update sqlmap set sql_ = :sql_ where key_ = :key_", params);
    if (affected == 0) {
      throw new SqlUpdateFailureException(key + " not found");
    }
  }

  @Override
  public void updateSql(final Map<String, String> sqlmap) throws SqlUpdateFailureException {
    transactionTemplate.execute(new TransactionCallbackWithoutResult() {
      @Override
      protected void doInTransactionWithoutResult(TransactionStatus status) {
        for (Map.Entry<String, String> entry : sqlmap.entrySet()) {
          updateSql(entry.getKey(), entry.getValue());
        }
      }
    });
  }

  @Override
  public void registerSql(String key, String sql) {
    Map<String, String> params = new HashMap<>();
    params.put("key_", key);
    params.put("sql_", sql);
    jdbc.update("insert into sqlmap(key_, sql_) values (:key_,:sql_)", params);
  }

  @Override
  public String findSql(String key) throws SqlNotFoundException {
    try {
      Map<String, String> params = new HashMap<>();
      params.put("key_", key);
      return jdbc.queryForObject(
          "select sql_ from sqlmap where key_ = :key_", params, String.class);
    } catch (EmptyResultDataAccessException e) {
      throw new SqlNotFoundException(key + " not found", e);
    }
  }
}
