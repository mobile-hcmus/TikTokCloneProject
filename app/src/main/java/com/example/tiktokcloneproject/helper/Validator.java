package com.example.tiktokcloneproject.helper;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Year;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Validator {
    private static Validator instance = null;
    private Pattern pattern;
    private Matcher matcher;
    private final String regexPhone = "^(0|\\+84)\\d{9}$";
//    private final String regexEmail = "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$";
    private final String regexEmail = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{3,}$";
    private  final  String regexUsername = "^[a-zA-Z_][a-zA-Z_0-9]{2,}$";
    private final String regexPassword = "^[a-zA-Z0-9]{2,}$";
    private final String regexDate = "^(0?[1-9]|1\\d|2\\d|3[01])/(0[1-9]|1[0-2])/(\\d{4})$";
    private Validator() {

    }
    public static Validator getInstance() {
        if(instance == null) {
            instance = new Validator();
        }
        return instance;
    }
    public boolean isValidPhone(String phone) {
        if(phone.isEmpty()) {
            return false;
        }
        pattern = Pattern.compile(regexPhone);
        matcher = pattern.matcher(phone);
        return matcher.matches();
    }
    public boolean isValidEmail(String email) {
        if(email.isEmpty()) {
            return false;
        }
        pattern = Pattern.compile(regexEmail);
        matcher = pattern.matcher(email);
        return matcher.matches();
    }
    public boolean isValidUsername(String username) {
        if(username.isEmpty()) {
            return false;
        }
        pattern = Pattern.compile(regexUsername);
        matcher = pattern.matcher(username);
        return matcher.matches();
    }

    public boolean isValidDate(String dateStr) {
        pattern = Pattern.compile(regexDate);
        matcher = pattern.matcher(dateStr);
        if(!matcher.matches()) return false;
        DateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        sdf.setLenient(false);
        try {
            Date date = sdf.parse(dateStr);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            int year = calendar.get(Calendar.YEAR);
            int currentYear = Year.now().getValue();
            if(currentYear - year < 16) {
                return false;
            }
        } catch (ParseException e) {
            return false;
        }
        return true;
    }

    public boolean isValidBirthdate(String birthdate) {
        if(birthdate.isEmpty()) {
            return true;
        }
        return isValidDate(birthdate);
    }

    public boolean isValidPassword(String password) {
       pattern = Pattern.compile(regexPassword);//. represents single character
        matcher = pattern.matcher(password);
        return matcher.matches();
    }

    public boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }
        try {
            double d = Double.parseDouble(strNum);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

}
