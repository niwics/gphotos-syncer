package cz.niwi.photoarchiveprocessor;

import java.time.Month;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NiwiDirectoryDateParser implements DirectoryDateParser {

    public NiwiDirectoryDateParser() {}

    /**
     * Parses the year directory name and return the year value if matches the pattern or 0 otherwise.
     * @return
     */
    public short parseYear(String yearString) {

        Calendar now = Calendar.getInstance();
        String thisYearString = String.valueOf(now.get(Calendar.YEAR));
        if (yearString.compareTo("1900") >= 0 && yearString.compareTo(thisYearString) <= 0)
            return Short.parseShort(yearString);
        return 0;
    }

    public byte parseMonth(String monthString) {

        ArrayList<String> possibleMonthNames = new ArrayList<String>();
        for (int i=1; i <= 12; i++) {
            String monthNum = String.format("%02d", i);
            String monthName = Month.of(i).getDisplayName(TextStyle.FULL_STANDALONE, new Locale("cs"));
            possibleMonthNames.add(monthNum);
            possibleMonthNames.add(monthNum + " - " + monthName);
            possibleMonthNames.add(monthName + ". " + monthName);
        }

        if (possibleMonthNames.contains(monthString.toLowerCase()))
            return Byte.parseByte(monthString.substring(0, 2));
        return 0;
    }

    public byte parseDay(String dayString) {
        String dayNumberString;
        //dayString.matches("\\d{8}")
        Pattern pattern = Pattern.compile("(([0123]?\\d)(\\.?\\s+\\w+.*)?)|(\\d{6}(\\d{2}))", Pattern.UNICODE_CHARACTER_CLASS);
        Matcher m = pattern.matcher(dayString);
        if (m.matches()) {
            if (m.group(2) != null)
                return Byte.parseByte(m.group(2));
            if (m.group(5) != null)
                return Byte.parseByte(m.group(5));
        }
        return 0;
    }
}
