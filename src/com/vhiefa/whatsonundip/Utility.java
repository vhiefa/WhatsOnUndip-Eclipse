package com.vhiefa.whatsonundip;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import java.text.DateFormat;
import java.util.Date;

import com.vhiefa.whatsonundip.data.EventContract;


/**
 * Created by Afifatul Mukaroh
 */

public class Utility {
		
    static String formatDate(String dateString) {
        Date date = EventContract.getDateFromDb(dateString);
        return DateFormat.getDateInstance().format(date);
    }
    
    public static int getIconResourceForEventCategory(String category) {

       if (category.equals("festival")) {
            return R.drawable.ic_festival;
        } else if ((category.equals("training")) || (category.equals("seminar/workshop"))) {
            return R.drawable.ic_seminar;
        } else if (category.equals("beasiswa")) {
            return R.drawable.ic_beasiswa;
        } else if (category.equals("kompetisi")) {
            return R.drawable.ic_kompetisi;
        } else if (category .equals("lainnya")) {
            return R.drawable.ic_lainnya;
        } else if (category .equals("pameran")) {
            return R.drawable.ic_pameran;
        }else if (category .equals("unjuk rasa")) {
            return R.drawable.ic_unjukrasa;
        }else if (category .equals("pentas seni")) {
            return R.drawable.ic_pentasseni;
        }else if (category .equals("diskusi")) {
            return R.drawable.ic_diskusi;
        }

        return -1;
    }
    
    public static String getShorterTitle(String title) {   	
         String[] titleChar = title.split("");
         int maxTitleChar = 60;
         if (titleChar.length > maxTitleChar) {
	         int i;
	         title = titleChar[1];

	         for (i=2;i<=maxTitleChar-3;i++){
	        	 title = title+titleChar[i];
	         }
	         
	         title = title + "...";
         }
         return title;
     }
    
    

}
