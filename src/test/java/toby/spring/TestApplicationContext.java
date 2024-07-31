package toby.spring;

import com.mysql.cj.jdbc.Driver;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.mail.MailSender;
import org.springframework.oxm.Unmarshaller;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import toby.spring.object.dependecy.dao.UserDao;
import toby.spring.object.dependecy.dao.UserDaoJdbc;
import toby.spring.object.dependecy.user.service.DummyMailSender;
import toby.spring.object.dependecy.user.service.UserService;
import toby.spring.object.dependecy.user.service.UserServiceImpl;
import toby.spring.object.dependecy.user.sqlservice.OxmSqlService;
import toby.spring.object.dependecy.user.sqlservice.SqlService;

@Configuration
@EnableTransactionManagement
public class TestApplicationContext {
  @Autowired SqlService sqlService;

  @Bean
  public DataSource dataSource() {
    SimpleDriverDataSource dataSource = new SimpleDriverDataSource();

    dataSource.setDriverClass(Driver.class);
    dataSource.setUrl("jdbc:mysql://localhost/springbook?characterEncoding=UTF-8");
    dataSource.setUsername("spring");
    dataSource.setPassword("book");

    return dataSource;
  }

  @Bean
  public PlatformTransactionManager transactionManager() {
    DataSourceTransactionManager transactionManager = new DataSourceTransactionManager();
    transactionManager.setDataSource(dataSource());
    return transactionManager;
  }

  @Bean
  public UserDao userDao() {
    UserDaoJdbc dao = new UserDaoJdbc();
    dao.setDataSource(dataSource());
    dao.setSqlService(this.sqlService);
    return dao;
  }

  @Bean
  public UserService userService() {
    UserServiceImpl service = new UserServiceImpl();
    service.setUserDao(userDao());
    service.setMailSender(mailSender());
    return service;
  }

  @Bean
  public UserService testUserService() {
    UserServiceImpl.TestUserService testService = new UserServiceImpl.TestUserService();
    testService.setUserDao(userDao());
    testService.setMailSender(mailSender());
    return testService;
  }

  @Bean
  public MailSender mailSender() {
    return new DummyMailSender();
  }

  @Bean
  public SqlService sqlService() {
    OxmSqlService oxmSqlService = new OxmSqlService();
    oxmSqlService.setUnmarshaller(unmarshaller());
    //    oxmSqlService.setSqlRegistry(sqlRegistry());
    //    oxmSqlService.setSqlmap("");
    return oxmSqlService;
  }

  //  @Bean
  //  public SqlRegistry sqlRegistry() {
  //    EmbeddedDbSqlRegistry sqlRegistry = new EmbeddedDbSqlRegistry();
  //    //    sqlRegistry.setDataSource(embeddedDatabase());
  //    return sqlRegistry;
  //  }

  @Bean
  public Unmarshaller unmarshaller() {
    Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
    marshaller.setContextPath("toby.spring.object.dependecy.user.sqlservice.jaxb");
    return marshaller;
  }

  //  @Bean
  //  public DataSource embeddedDatabase() {
  //    return new EmbeddedDatabaseBuilder()
  //        .setName("embeddedDatabase")
  //        .setType(EmbeddedDatabaseType.HSQL)
  //        .addScript("classpath:schema.sql")
  //        .build();
  //  }
}
