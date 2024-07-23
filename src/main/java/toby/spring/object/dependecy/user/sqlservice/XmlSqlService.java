package toby.spring.object.dependecy.user.sqlservice;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import toby.spring.object.dependecy.user.sqlservice.jaxb.SqlType;
import toby.spring.object.dependecy.user.sqlservice.jaxb.Sqlmap;

public class XmlSqlService implements SqlService, SqlRegistry, SqlReader {
  // sqlMap은 SqlRegistry 구현의 일부가 된다. 따라서 외부에서 직접 접근할 수 없다.
  private Map<String, String> sqlMap = new HashMap<>();
  private String sqlmapFile;
  private SqlReader sqlReader;
  private SqlRegistry sqlRegistry;

  public void setSqlmapFile(String sqlmapFile) {
    this.sqlmapFile = sqlmapFile;
  }

  public void setSqlReader(SqlReader sqlReader) {
    this.sqlReader = sqlReader;
  }

  public void setSqlRegistry(SqlRegistry sqlRegistry) {
    this.sqlRegistry = sqlRegistry;
  }

  @PostConstruct
  public void loadSql() {
    this.sqlReader.read(this.sqlRegistry);
  }

  @Override
  public String getSql(String key) throws SqlRetrievalFailureException {
    try {
      return this.sqlRegistry.findSql(key);
    } catch (SqlNotFoundException e) {
      throw new SqlRetrievalFailureException(e.getMessage());
    }
  }

  @Override
  public void registerSql(String key, String sql) {
    // HashMap이라는 저장소를 사용하는 구체적인 구현 방법에서 독립될 수 있도록 인터페이스의 메소드로 접근하게 해준다.
    sqlMap.put(key, sql);
  }

  @Override
  public String findSql(String key) throws SqlNotFoundException {
    String sql = sqlMap.get(key);
    if (sql == null) {
      throw new SqlNotFoundException(key + " not found");
    } else {
      return sql;
    }
  }

  @Override
  public void read(SqlRegistry sqlRegistry) {
    String contextPath = Sqlmap.class.getPackage().getName();
    try {
      JAXBContext context = JAXBContext.newInstance(contextPath);
      Unmarshaller unmarshaller = context.createUnmarshaller();
      InputStream is = this.getClass().getClassLoader().getResourceAsStream(sqlmapFile);
      Sqlmap sqlmap = (Sqlmap) unmarshaller.unmarshal(is);

      for (SqlType sql : sqlmap.getSql()) {
        sqlRegistry.registerSql(sql.getKey(), sql.getValue());
      }
    } catch (JAXBException e) {
      throw new RuntimeException(e);
    }
  }
}
