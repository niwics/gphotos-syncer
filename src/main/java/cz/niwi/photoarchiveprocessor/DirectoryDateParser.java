package cz.niwi.photoarchiveprocessor;

public interface DirectoryDateParser {

    /**
     * Parses the year directory name and return the year value if matches the pattern or 0 otherwise.
     * @return
     */
    short parseYear(String yearString);
    byte parseMonth(String monthString);
    byte parseDay(String dayString);
}
