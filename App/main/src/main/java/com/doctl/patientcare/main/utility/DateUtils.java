package com.doctl.patientcare.main.utility;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Satya Madala on 11/2/16.
 * email : satya.madala@Olivo.in
 */
public class DateUtils {
    public static int daysBetween(Date date1, Date date2){
        Calendar day1= java.util.Calendar.getInstance(),  day2= java.util.Calendar.getInstance();
        day1.setTime(date1);
        day2.setTime(date2);
        Calendar dayOne = (Calendar) day1.clone(),
                dayTwo = (Calendar) day2.clone();

        if (dayOne.get(Calendar.YEAR) == dayTwo.get(Calendar.YEAR)) {
            return Math.abs(dayOne.get(Calendar.DAY_OF_YEAR) - dayTwo.get(Calendar.DAY_OF_YEAR));
        } else {
            if (dayTwo.get(Calendar.YEAR) > dayOne.get(Calendar.YEAR)) {
                //swap them
                Calendar temp = dayOne;
                dayOne = dayTwo;
                dayTwo = temp;
            }
            int extraDays = 0;

            int dayOneOriginalYearDays = dayOne.get(Calendar.DAY_OF_YEAR);

            while (dayOne.get(Calendar.YEAR) > dayTwo.get(Calendar.YEAR)) {
                dayOne.add(Calendar.YEAR, -1);
                // getActualMaximum() important for leap years
                extraDays += dayOne.getActualMaximum(Calendar.DAY_OF_YEAR);
            }

            return extraDays - dayTwo.get(Calendar.DAY_OF_YEAR) + dayOneOriginalYearDays ;
        }
    }

    public static boolean isSameDay(Date date1, Date date2){
        return daysBetween(date1, date2) == 0;
    }

    private boolean isSameDateByIgnoringTime(Date date1, Date date2){

        Calendar calendar1= Calendar.getInstance(), calendar2=Calendar.getInstance();
        calendar1.setTime(date1);
        calendar2.setTime(date2);

        if( calendar1.get(Calendar.DAY_OF_MONTH) == calendar2.get(Calendar.DAY_OF_MONTH)  &&
                calendar1.get(Calendar.MONTH) == calendar2.get(Calendar.MONTH) &&
                calendar1.get(Calendar.YEAR) == calendar2.get(Calendar.YEAR)){
            return true;
        }
        return false;
    }

    public static String messageTimeInThread(Date date){

        String result;
        if(date == null){
            return "";
        }
        if(DateUtils.isSameDay(date, new Date())){
            result =  new SimpleDateFormat("hh:mm a").format(date);
        }else {
            result = new SimpleDateFormat("MMM dd, hh:mm a").format(date);
        }
        return result;
    }

    public static String messageTimeForLatestMessage(Date date){
        String result="";
        if(date == null){
            return result;
        }
        int numOfDaysBetween = daysBetween(date, new Date());
        switch (numOfDaysBetween){
            case 0: result= new SimpleDateFormat("hh:mm a").format(date);
                break;
            case 1: result = "Yesterday";
                break;
            default:
                result=new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH).format(date);
        }
        return result;
    }
}
