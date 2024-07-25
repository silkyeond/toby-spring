package toby.spring.object.dependecy.user.sqlservice;

import java.io.IOException;
import javax.annotation.PostConstruct;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import org.springframework.oxm.Unmarshaller;
import toby.spring.object.dependecy.user.sqlservice.jaxb.SqlType;
import toby.spring.object.dependecy.user.sqlservice.jaxb.Sqlmap;

public class OxmSqlService implements SqlService {
  private final OxmSqlReader oxmSqlReader = new OxmSqlReader();
  private SqlRegistry sqlRegistry = new HashMapSqlRegistry();

  public void setSqlRegistry(SqlRegistry sqlRegistry) {
    this.sqlRegistry = sqlRegistry;
  }

  public void setUnmarshaller(Unmarshaller unmarshaller) {
    this.oxmSqlReader.setUnmarshaller(unmarshaller);
  }

  public void setSqlmapFile(String sqlmapFile) {
    this.oxmSqlReader.setSqlmapFile(sqlmapFile);
  }

  @Override
  public String getSql(String key) throws SqlRetrievalFailureException {
    try {
      return this.sqlRegistry.findSql(key);
    } catch (SqlNotFoundException e) {
      throw new SqlRetrievalFailureException(e + " SQL Not Found");
    }
  }

  @PostConstruct
  public void loadSql() {
    this.oxmSqlReader.read(this.sqlRegistry);
  }

  private class OxmSqlReader implements SqlReader {
    private Unmarshaller unmarshaller;
    private static final String DEFAULT_SQLMAP_FILE = "sqlmap.xml";
    private String sqlmapFile = DEFAULT_SQLMAP_FILE;

    void setUnmarshaller(Unmarshaller unmarshaller) {
      this.unmarshaller = unmarshaller;
    }

    void setSqlmapFile(String sqlmapFile) {
      this.sqlmapFile = sqlmapFile;
    }

    @Override
    public void read(SqlRegistry sqlRegistry) {
      try {
        Source source =
            new StreamSource(this.getClass().getClassLoader().getResourceAsStream(sqlmapFile));
        Sqlmap sqlmap = (Sqlmap) this.unmarshaller.unmarshal(source);

        for (SqlType sql : sqlmap.getSql()) {
          sqlRegistry.registerSql(sql.getKey(), sql.getValue());
        }
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
  }
}
