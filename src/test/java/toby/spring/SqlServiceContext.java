package toby.spring;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.oxm.Unmarshaller;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import toby.spring.object.dependecy.user.sqlservice.OxmSqlService;
import toby.spring.object.dependecy.user.sqlservice.SqlService;

@Configuration
public class SqlServiceContext {
  @Bean
  public SqlService sqlService() {
    OxmSqlService sqlService = new OxmSqlService();
    sqlService.setUnmarshaller(unmarshaller());
    //    sqlService.setSqlRegistry(sqlRegistry());
    return sqlService;
  }

  //  @Bean
  //  public SqlRegistry sqlRegistry() {
  //    EmbeddedDbSqlRegistry sqlRegistry = new EmbeddedDbSqlRegistry();
  //    sqlRegistry.setDataSource(embeddedDatabase());
  //    return sqlRegistry;
  //  }

  @Bean
  public Unmarshaller unmarshaller() {
    Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
    marshaller.setContextPath("toby.spring.object.dependecy.user.sqlservice.jaxb");
    return marshaller;
  }

  //    @Bean
  //    public DataSource embeddedDatabase() {
  //      return new EmbeddedDatabaseBuilder()
  //          .setName("embeddedDatabase")
  //          .setType(EmbeddedDatabaseType.HSQL)
  //          .addScript("classpath:schema.sql")
  //          .build();
  //    }

}
