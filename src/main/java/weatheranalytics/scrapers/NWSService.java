package weatheranalytics.scrapers;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import weatheranalytics.Producer;
import weatheranalytics.entities.Forecast;

import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;


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

        String timeOfPrediction = jsonObject.getJSONObject("properties").getString("generatedAt");
        JSONArray jsonArray = jsonObject.getJSONObject("properties").getJSONArray("periods");

        String weatherService = "NWS";
        String location = "centralPark";
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject period = jsonArray.getJSONObject(i);
            Forecast forecast = new Forecast(
                   weatherService,
                    location,
                    period.getInt("temperature"),
                    timeOfPrediction,
                    period.getString("startTime"),
                    period.getString("endTime"),
                    period.getJSONObject("dewpoint").getFloat("value"),
                    period.getString("windDirection"),
                    period.getString("windSpeed")

            );

            System.out.println("forecast: " + forecast.toString());

            producer.send("forecasts", forecast);

        }


        return "nermp";
    }
}
