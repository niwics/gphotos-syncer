package cz.niwi.gphotossyncer;

import java.security.InvalidParameterException;

public class DateMarker {

    private short year = 0;
    private byte month = 0;
    private byte day = 0;

    public DateMarker(short year) {
        DateMarker.validateYear(year);
        this.year = year;
    }

    public DateMarker(short year, byte month) {
        DateMarker.validateYear(year);
        DateMarker.validateMonth(month);
        this.year = year;
        this.month = month;
    }

    public DateMarker(short year, byte month, byte day) {
        DateMarker.validateYear(year);
        DateMarker.validateMonth(month);
        DateMarker.validateDay(day);
        this.year = year;
        this.month = month;
        this.day = day;
    }

    private static void validateYear(short year) {
        if (year < 1900)
            throw new InvalidParameterException("Invalid year value: " + year);
    }

    private static void validateMonth(byte month) {
        if (month < 1 || month > 12)
            throw new InvalidParameterException("Invalid month value: " + month);
    }

    private static void validateDay(byte day) {
        if (day < 1 || day > 31)
            throw new InvalidParameterException("Invalid day value: " + day);
    }

    public DateMarker cloneWithMonth(byte month) {
        DateMarker.validateMonth(month);
        if (this.getYear() == 0)
            throw new RuntimeException("Invalid function usage");
        return new DateMarker(this.getYear(), month);
    }

    public DateMarker cloneWithDay(byte day) {
        DateMarker.validateDay(day);
        if (this.getMonth() == 0)
            throw new RuntimeException("Invalid function usage");
        return new DateMarker(this.getYear(), this.getMonth(), day);
    }

    public boolean hasYear() {
        return this.getYear() > 0;
    }

    public boolean hasMonth() {
        return this.getMonth() > 0;
    }

    public boolean hasDay() {
        return this.getDay() > 0;
    }

    public short getYear() {
        return year;
    }

    public byte getMonth() {
        return month;
    }

    public byte getDay() {
        return day;
    }
}
