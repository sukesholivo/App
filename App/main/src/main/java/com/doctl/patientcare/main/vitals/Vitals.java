package com.doctl.patientcare.main.vitals;

/**
 * Created by Administrator on 6/22/2014.
 */
public class Vitals {
    private int reading1;
    private String readingTime1;
    private int reading2;
    private String readingTime2;
    private String readingDay;
    private String readingDate;

    public Vitals(int reading1, String readingTime1, int reading2, String readingTime2, String readingDay, String readingDate){
        this.reading1 = reading1;
        this.readingTime1 = readingTime1;
        this.reading2 = reading2;
        this.readingTime2 = readingTime2;
        this.readingDay = readingDay;
        this.readingDate = readingDate;
    }

    public int getReading1() {
        return reading1;
    }

    public String getReadingTime1() {
        return readingTime1;
    }

    public int getReading2() {
        return reading2;
    }

    public String getReadingTime2() {
        return readingTime2;
    }

    public String getReadingDay() {
        return readingDay;
    }

    public String getReadingDate() {
        return readingDate;
    }
}
