package cz.niwi.photoarchiveprocessor;

import java.security.InvalidParameterException;

/**
 * Hold the informations about stored date and its "level".
 */
public class DateMarker {

    /**
     * Stored year
     */
    private short year = 0;
    /**
     * Stored month
     */
    private byte month = 0;
    /**
     * Stored day
     */
    private byte day = 0;

    /**
     * Constructor
     * @param year
     */
    public DateMarker(short year) {
        DateMarker.validateYear(year);
        this.year = year;
    }

    /**
     * Constructor
     * @param year
     * @param month
     */
    public DateMarker(short year, byte month) {
        DateMarker.validateYear(year);
        DateMarker.validateMonth(month);
        this.year = year;
        this.month = month;
    }

    /**
     * Constructor
     * @param year
     * @param month
     * @param day
     */
    public DateMarker(short year, byte month, byte day) {
        DateMarker.validateYear(year);
        DateMarker.validateMonth(month);
        DateMarker.validateDay(day);
        this.year = year;
        this.month = month;
        this.day = day;
    }

    /**
     * Validates the year
     * @param year
     */
    private static void validateYear(short year) {
        if (year < 1900)
            throw new InvalidParameterException("Invalid year value: " + year);
    }

    /**
     * Validates the month
     * @param month
     */
    private static void validateMonth(byte month) {
        if (month < 1 || month > 12)
            throw new InvalidParameterException("Invalid month value: " + month);
    }

    /**
     * Validates the day
     * @param day
     */
    private static void validateDay(byte day) {
        if (day < 1 || day > 31)
            throw new InvalidParameterException("Invalid day value: " + day);
    }

    /**
     * Creates the new object based on the current and adds the month to it
     * @param month
     * @return
     */
    public DateMarker cloneWithMonth(byte month) {
        DateMarker.validateMonth(month);
        if (this.getYear() == 0)
            throw new RuntimeException("Invalid function usage");
        return new DateMarker(this.getYear(), month);
    }

    /**
     * Creates the new object based on the current and adds the day to it
     * @param day
     * @return
     */
    public DateMarker cloneWithDay(byte day) {
        DateMarker.validateDay(day);
        if (this.getMonth() == 0)
            throw new RuntimeException("Invalid function usage");
        return new DateMarker(this.getYear(), this.getMonth(), day);
    }

    /**
     * Tests whether the year is set
     * @return
     */
    public boolean hasYear() {
        return this.getYear() > 0;
    }

    /**
     * Tests whether the month is set
     * @return
     */
    public boolean hasMonth() {
        return this.getMonth() > 0;
    }

    /**
     * Tests whether the day is set
     * @return
     */
    public boolean hasDay() {
        return this.getDay() > 0;
    }

    public short getYear() { return year; }
    public byte getMonth() { return month; }
    public byte getDay() { return day; }

    /**
     * Returns the ISO form of the date
     * @return
     */
    public String getIsoDate() {
        return String.format("%02d-%02d-%02d", this.getYear(), this.getMonth(), this.getDay());
    }
}
