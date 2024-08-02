package toby.spring.object.dependecy.config;

import static toby.spring.object.dependecy.config.AppContext.*;

import java.sql.Driver;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.mail.MailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import toby.spring.object.dependecy.dao.UserDao;

@Configuration
@EnableTransactionManagement
@ComponentScan(basePackages = "toby.spring.object.dependecy")
@Import({SqlServiceContext.class, ProductionAppContext.class})
@PropertySource("/database.properties")
public class AppContext {

  @Autowired UserDao userDao;

  @Value("${db.driverClass}")
  Class<? extends Driver> dbDriverClass;

  @Value("${db.url}")
  String url;

  @Value("${db.username}")
  String username;

  @Value("${db.password}")
  String password;

  @Bean
  public DataSource dataSource() {
    SimpleDriverDataSource dataSource = new SimpleDriverDataSource();
    dataSource.setDriverClass(this.dbDriverClass);
    dataSource.setUrl(this.url);
    dataSource.setUsername(this.username);
    dataSource.setPassword(this.password);

    return dataSource;
  }

  @Bean
  public PlatformTransactionManager transactionManager() {
    DataSourceTransactionManager transactionManager = new DataSourceTransactionManager();
    transactionManager.setDataSource(dataSource());
    return transactionManager;
  }

  @Bean
  public MailSender mailSender() {
    JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
    mailSender.setHost("mail.mycompany.com");
    return mailSender;
  }

  @Configuration
  @Profile("production")
  public static class ProductionAppContext {
    @Bean
    public MailSender mailSender() {
      JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
      mailSender.setHost("localhost");
      return mailSender;
    }
  }

  @Bean
  public static PropertySourcesPlaceholderConfigurer placeholderConfigurer() {
    return new PropertySourcesPlaceholderConfigurer();
  }
}
