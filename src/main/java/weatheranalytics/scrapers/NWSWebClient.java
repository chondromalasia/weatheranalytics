package weatheranalytics.scrapers;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Collections;

@Component
public class NWSWebClient {
    private final WebClient webClient;

    public NWSWebClient(){
        this.webClient =  WebClient.builder()
                .baseUrl("https://api.weather.gov/gridpoints")
                .defaultCookie("cookieKey", "cookieValue")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultUriVariables(Collections.singletonMap("url", "https://api.weather.gov/gridpoints"))
                .build();
    }

    public Mono<String> callNWS() {


        return webClient.get()
                .uri("/OKX/34,38/forecast")
                .retrieve()
                .bodyToMono(String.class);
    }

    public String isAlive() {
        return "Alive";
    }

    // TODO move config like https://archive.ph/wXmVX
/*


        }
    }
*/


}
