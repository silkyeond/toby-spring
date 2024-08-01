package toby.spring.object.dependecy.config;

import static toby.spring.object.dependecy.config.AppContext.*;

import com.mysql.cj.jdbc.Driver;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
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
public class AppContext {

  @Autowired UserDao userDao;

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
}
