package dev.omerdanismaz.Cryptograph.functions;

import java.security.SecureRandom;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GeneralOperations
{
    public static String generateRandomString(int length)
    {
        SecureRandom secureRandom = new SecureRandom();
        StringBuilder stringBuilder = new StringBuilder();

        for(int i = 0; i < length; ++i)
        {
            int randomNumber = secureRandom.nextInt(36);
            char randomCharacter = (char)(randomNumber < 26 ? 'a' + randomNumber : '0' + (randomNumber - 26));
            stringBuilder.append(randomCharacter);
        }

        return stringBuilder.toString();
    }

    public static String removeExtraSpaces(String string)
    {
        return string.trim().replaceAll("\\s+", " ");
    }

    public static Date initializeExpirationDate()
    {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DAY_OF_MONTH, 30);
        return calendar.getTime();
    }

    public static boolean isNotAlphaUnicode(String string)
    {
        Pattern pattern = Pattern.compile("^[\\p{L}\\s]+$");
        Matcher matcher = pattern.matcher(string);
        return !matcher.matches();
    }

    public static boolean isNotEmail(String string)
    {
        Pattern pattern = Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");
        Matcher matcher = pattern.matcher(string);
        return !matcher.matches();
    }
}
