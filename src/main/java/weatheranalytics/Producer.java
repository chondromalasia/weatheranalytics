package weatheranalytics;

import org.json.JSONObject;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import weatheranalytics.entities.Forecast;

@Component
public class Producer {

    private final KafkaTemplate<String, Forecast> kafkaTemplate;

    public Producer(KafkaTemplate<String, Forecast> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public String send(String topic, Forecast forecast) {

        kafkaTemplate.send(topic, forecast);

        return "sent";

    }

}
