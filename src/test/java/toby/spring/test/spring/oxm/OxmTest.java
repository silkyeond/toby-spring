package toby.spring.test.spring.oxm;

import static org.assertj.core.api.Assertions.assertThat;

import javax.xml.bind.JAXBException;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import toby.spring.object.dependecy.user.sqlservice.jaxb.SqlType;
import toby.spring.object.dependecy.user.sqlservice.jaxb.Sqlmap;

import java.util.List;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = "/OxmTest-context.xml")
public class OxmTest {
  @Autowired private Jaxb2Marshaller unmarshaller;

  @Test
  public void unmarshall() throws JAXBException {
    Source xmlSource = new StreamSource(getClass()
            .getResourceAsStream("/sqlmap.xml"));

    Sqlmap sqlmap = (Sqlmap) this.unmarshaller.unmarshal(xmlSource);
    List<SqlType> sqlList = sqlmap.getSql();
    assertThat(sqlList.size()).isEqualTo(6);
  }
}
