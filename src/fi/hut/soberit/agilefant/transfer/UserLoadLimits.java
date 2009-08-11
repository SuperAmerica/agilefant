package fi.hut.soberit.agilefant.transfer;

public class UserLoadLimits {
    private double dailyLoadLow = 0;

    private double dailyLoadMedium = 0;

    private double dailyLoadHigh = 0;

    private double dailyLoadCritical = 0;

    private double dailyLoadMaximum = 0;

    public double getDailyLoadLow() {
        return dailyLoadLow;
    }

    public void setDailyLoadLow(double dailyLoadLow) {
        this.dailyLoadLow = dailyLoadLow;
    }

    public double getDailyLoadMedium() {
        return dailyLoadMedium;
    }

    public void setDailyLoadMedium(double dailyLoadMedium) {
        this.dailyLoadMedium = dailyLoadMedium;
    }

    public double getDailyLoadHigh() {
        return dailyLoadHigh;
    }

    public void setDailyLoadHigh(double dailyLoadHigh) {
        this.dailyLoadHigh = dailyLoadHigh;
    }

    public double getDailyLoadCritical() {
        return dailyLoadCritical;
    }

    public void setDailyLoadCritical(double dailyLoadCritical) {
        this.dailyLoadCritical = dailyLoadCritical;
    }

    public double getDailyLoadMaximum() {
        return dailyLoadMaximum;
    }

    public void setDailyLoadMaximum(double dailyLoadMaximum) {
        this.dailyLoadMaximum = dailyLoadMaximum;
    }
}
