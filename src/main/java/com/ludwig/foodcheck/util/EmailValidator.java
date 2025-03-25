package com.ludwig.foodcheck.util;
import java.util.regex.Pattern;

public class EmailValidator {
    private static final String EMAIL_REGEX = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
    private static final Pattern pattern = Pattern.compile(EMAIL_REGEX);

    public static boolean isValid(String email) {
        return email != null && pattern.matcher(email).matches();
    }
}
