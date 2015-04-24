package com.vhiefa.whatsonundip;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import java.text.DateFormat;
import java.util.Date;

import com.vhiefa.whatsonundip.data.EventContract;


public class Utility {
		
    static String formatDate(String dateString) {
        Date date = EventContract.getDateFromDb(dateString);
        return DateFormat.getDateInstance().format(date);
    }
    
    public static int getIconResourceForEventCategory(String category) {

       if (category.equals("festival")) {
            return R.drawable.ic_festival;
        } else if (category.equals("")) {
            return R.drawable.ic_festival;
        } else if (category.equals("")) {
            return R.drawable.ic_festival;
        } else if (category.equals("")) {
            return R.drawable.ic_festival;
        } else if (category .equals("")) {
            return R.drawable.ic_festival;
        }

        return -1;
    }
    
    

}
