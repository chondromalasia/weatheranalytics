package weatheranalytics.entities;

public class Forecast {
    private String weatherService;
    private String location;
    private int maxTemp;
    private String timeOfPrediction;
    private String predictionUntil;

    public Forecast (String weatherService, String location, int maxTemp,
                     String timeOfPrediction, String predictionUntil) {
        this.weatherService = weatherService;
        this.location = location;
        this.maxTemp = maxTemp;
        this.timeOfPrediction = timeOfPrediction;
        this.predictionUntil = predictionUntil;
    }
}
