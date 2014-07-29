package com.doctl.patientcare.main.constants;

import android.content.Context;

import com.doctl.patientcare.main.Cards.CardHeaderInnerView;
import com.doctl.patientcare.main.om.BaseTask;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import it.gmariotti.cardslib.library.internal.CardHeader;

/**
 * Created by Administrator on 7/28/2014.
 */
public final class Utils {
    public static Date parseFromString(String iso8601string) {
        try {
            String s = iso8601string.replace("Z", "+00:00");
            try {
                s = s.substring(0, 22) + s.substring(23);
            } catch (IndexOutOfBoundsException e) {
                throw new ParseException("Invalid length", 0);
            }
            Date date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").parse(s);
            return date;
        } catch (ParseException e){
            e.printStackTrace();
        }
        return null;
    }

    public static CardHeader getCardHeader(Context context, BaseTask task){
        CardHeader cardHeader = null;
        boolean pastTask = false;
        int seconds  = (int)( (task.getETA().getTime() - new Date().getTime())/1000);
        if (seconds < 0) { // future tasks
            seconds = 0 - seconds;
            pastTask = true;
        }
        if (task.getEtaType() == BaseTask.TypeOfEta.DAYLONG) {
            cardHeader = new CardHeaderInnerView(context, "" + "TODAY", "", "");
        } else {
            if (seconds < 60) { // if time is less than 1 min
                cardHeader = new CardHeaderInnerView(context, "NOW", "", "");
            }else if (seconds < 60 * 60) { // if time is less than 1 hour
                cardHeader = new CardHeaderInnerView(context, "" + seconds / 60, "MINS " + (pastTask ? "AGO" : "FROM NOW"), "");
            } else if (seconds < 60 * 60 * 24) { // if time is less than 1 day
                cardHeader = new CardHeaderInnerView(context, "" + seconds / (60 * 60), "HRS " + (pastTask ? "AGO" : "FROM NOW"), "");
            } else { // if time is more than 1 day
                cardHeader = new CardHeaderInnerView(context, "" + seconds / (60 * 60 * 24), "DAYS " + (pastTask ? "AGO" : ""), "");
            }
        }
        return cardHeader;
    }
}
