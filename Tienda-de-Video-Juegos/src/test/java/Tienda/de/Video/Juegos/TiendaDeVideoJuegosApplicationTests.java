package Tienda.de.Video.Juegos;

import io.awspring.cloud.sqs.operations.SqsTemplate;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;

// En los tests no nos conectamos a AWS: se apaga SQS/SNS y se usan objetos simulados
@SpringBootTest(properties = {
        "spring.cloud.aws.sqs.enabled=false",
        "spring.cloud.aws.sns.enabled=false"
})
class TiendaDeVideoJuegosApplicationTests {

	@MockBean
	private SqsTemplate sqsTemplate;

	@MockBean
	private SqsAsyncClient sqsAsyncClient;

	@MockBean
	private SnsClient snsClient;

	@Test
	void contextLoads() {
	}

}
