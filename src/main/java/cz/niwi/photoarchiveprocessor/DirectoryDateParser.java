package cz.niwi.photoarchiveprocessor;

/**
 * Interface for date string parsers.
 */
public interface DirectoryDateParser {

    /**
     * Parses the year name and return the year value if matches the pattern or 0 otherwise.
     * @return
     */
    short parseYear(String yearString);

    /**
     * Parses the month name and return the year value if matches the pattern or 0 otherwise.
     * @return
     */
    byte parseMonth(String monthString);

    /**
     * Parses the day name and return the year value if matches the pattern or 0 otherwise.
     * @return
     */
    byte parseDay(String dayString);
}
