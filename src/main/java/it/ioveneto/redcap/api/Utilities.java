package it.ioveneto.redcap.api;

import java.text.ParseException;
import java.text.SimpleDateFormat;

public class Utilities {

    public static boolean isValidDate(String inDate) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss:ms");
        dateFormat.setLenient(false);
        try {
            dateFormat.parse(inDate.trim());
        } catch (ParseException pe) {
            return false;
        }
        return true;
    }

    public static boolean checkNumberPositive(String number) {
        int num;
        try {
            num = Integer.parseInt(number);
        } catch (NumberFormatException e) {
            return false;
        }
        if (num > 0)
            return true;
        else
            return false;
    }
}
