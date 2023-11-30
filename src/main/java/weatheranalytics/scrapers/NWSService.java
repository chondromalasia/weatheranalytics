package weatheranalytics.scrapers;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import weatheranalytics.Producer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class NWSService {
    private final NWSWebClient nwsWebClient;

    @Autowired
    Producer producer;

    public NWSService(NWSWebClient nwsWebClient){
        this.nwsWebClient = nwsWebClient;
    }

    public String simpleCall() {

        List<String> notGood = Arrays.asList("Tonight", "This Afternoon", "Today");
        String test = nwsWebClient.callNWS().block();

        JSONObject jsonObject = new JSONObject(test);
        JSONArray jsonArray = jsonObject.getJSONObject("properties").getJSONArray("periods");

        for (int i = 0; i < jsonArray.length(); i++) {
            String name = jsonArray.getJSONObject(i).getString("name");

            if (!notGood.contains(name) && !name.endsWith("Night")) {
                Integer temp = jsonArray.getJSONObject(i).getInt("temperature");
                System.out.println(name + ": " + temp);
                return name + ": " + temp;
            }
        }


        return "nermp";
    }

    public String dummyProduce() {
        return producer.send("forecasts", "testtest");
    }

    public String isAlive() {
        return nwsWebClient.isAlive();
    }
}
