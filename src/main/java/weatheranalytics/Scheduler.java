package weatheranalytics;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import weatheranalytics.scrapers.NWSService;

@Configuration
@EnableScheduling
public class Scheduler {

    private final NWSService nwsService;

    public Scheduler(NWSService nwsService) {
        this.nwsService = nwsService;
    }

    @Scheduled(cron = "0 * * * *")
    public void nwsCentralPark (){
        nwsService.simpleCall();

    }
}
