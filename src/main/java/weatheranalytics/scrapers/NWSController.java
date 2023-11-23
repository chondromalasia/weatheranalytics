package weatheranalytics.scrapers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class NWSController {
    private final NWSService nwsService;
    private final NWSWebClient nwsWebClient;

    public NWSController(NWSService nwsService, NWSWebClient nwsWebClient) {
        this.nwsService = nwsService;
        this.nwsWebClient = nwsWebClient;
    }

    @GetMapping("/beepboop")
    public String NWSTomorrowTemp(){
        return nwsService.simpleCall();
    }
}
