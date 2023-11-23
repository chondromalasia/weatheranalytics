package weatheranalytics.entities;

public class Forecast {
    private String weatherService;
    private String location;
    private int temp;
    private String timeOfPrediction;
    private String forecastStart;
    private String forecastEnd;
    private float dewpoint;
    private String windDirection;
    private String windSpeed;

    public Forecast (String weatherService, String location, int temp,
                     String timeOfPrediction, String forecastStart,
                     String forecastEnd, float dewpoint,
                     String windDirection, String windSpeed) {
        this.weatherService = weatherService;
        this.location = location;
        this.temp = temp;
        this.timeOfPrediction = timeOfPrediction;
        this.forecastStart = forecastStart;
        this.forecastEnd = forecastEnd;
        this.dewpoint = dewpoint;
        this.windDirection = windDirection;
        this.windSpeed = windSpeed;
    }

    public String getWeatherService() {
        return weatherService;
    }

    public void setWeatherService(String weatherService) {
        this.weatherService = weatherService;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getTemp() {
        return temp;
    }

    public void setTemp(int temp) {
        this.temp = temp;
    }

    public String getTimeOfPrediction() {
        return timeOfPrediction;
    }

    public void setTimeOfPrediction(String timeOfPrediction) {
        this.timeOfPrediction = timeOfPrediction;
    }

    public String getForecastStart() {
        return forecastStart;
    }

    public void setForecastStart(String forecastStart) {
        this.forecastStart = forecastStart;
    }

    public String getForecastEnd() {
        return forecastEnd;
    }

    public void setForecastEnd(String forecastEnd) {
        this.forecastEnd = forecastEnd;
    }

    public float getDewpoint() {
        return dewpoint;
    }

    public void setDewpoint(float dewpoint) {
        this.dewpoint = dewpoint;
    }

    public String getWindDirection() {
        return windDirection;
    }

    public void setWindDirection(String windDirection) {
        this.windDirection = windDirection;
    }

    public String getWindSpeed() {
        return windSpeed;
    }

    public void setWindSpeed(String windSpeed) {
        this.windSpeed = windSpeed;
    }
}
