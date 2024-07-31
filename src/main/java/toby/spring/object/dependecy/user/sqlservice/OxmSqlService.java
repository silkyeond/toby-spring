package toby.spring.object.dependecy.user.sqlservice;

import java.io.IOException;
import javax.annotation.PostConstruct;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.oxm.Unmarshaller;
import toby.spring.object.dependecy.user.sqlservice.jaxb.SqlType;
import toby.spring.object.dependecy.user.sqlservice.jaxb.Sqlmap;

public class OxmSqlService implements SqlService {
  private final OxmSqlReader oxmSqlReader = new OxmSqlReader();
  private SqlRegistry sqlRegistry = new HashMapSqlRegistry();
  private final BaseSqlService baseSqlService = new BaseSqlService();

  public void setSqlmap(Resource resource) {
    this.oxmSqlReader.setSqlmap(resource);
  }

  public void setSqlRegistry(SqlRegistry sqlRegistry) {
    this.sqlRegistry = sqlRegistry;
  }

  public void setUnmarshaller(Unmarshaller unmarshaller) {
    this.oxmSqlReader.setUnmarshaller(unmarshaller);
  }

  @Override
  public String getSql(String key) throws SqlRetrievalFailureException {
    return this.baseSqlService.getSql(key);
  }

  @PostConstruct
  public void loadSql() {
    this.baseSqlService.setSqlReader(this.oxmSqlReader);
    this.baseSqlService.setSqlRegistry(this.sqlRegistry);

    this.baseSqlService.loadSql();
  }

  private class OxmSqlReader implements SqlReader {
    private Resource sqlmap = new ClassPathResource("sqlmap.xml");
    private Unmarshaller unmarshaller;

    void setUnmarshaller(Unmarshaller unmarshaller) {
      this.unmarshaller = unmarshaller;
    }

    void setSqlmap(Resource sqlmap) {
      this.sqlmap = sqlmap;
    }

    @Override
    public void read(SqlRegistry sqlRegistry) {
      try {
        Source source = new StreamSource(sqlmap.getInputStream());
        Sqlmap sqlmap = (Sqlmap) this.unmarshaller.unmarshal(source);

        for (SqlType sql : sqlmap.getSql()) {
          sqlRegistry.registerSql(sql.getKey(), sql.getValue());
        }
      } catch (IOException e) {
        throw new IllegalArgumentException(this.sqlmap.getFilename() + "을 가져올 수 없습니다.", e);
      }
    }
  }
}
