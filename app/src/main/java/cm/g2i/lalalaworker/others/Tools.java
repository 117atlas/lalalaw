package cm.g2i.lalalaworker.others;

import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.io.File;
import java.util.ArrayList;

import cm.g2i.lalalaworker.R;
import cm.g2i.lalalaworker.controllers.settings.Settings;

/**
 * Created by Sim'S on 26/07/2017.
 */

public class Tools {
    //MODS WHAT
    public static final int MOD_PHONE = 1;
    public static final int MOD_NATIONALITY = 2;
    public static final int MOD_LOCALISATION = 3;
    public static final int MOD_TOWN = 4;
    public static final int MOD_STREET = 5;
    public static final int MOD_NAME = 6;
    public static final int MOD_PASSWD = 7;

    //INTENTS KEYS
    public static final String MOD_WHAT = "MOD_WHAT";
    public static final String MOD_WHAT_LOCAL = "MOD_WHAT_LOCAL";
    public static final String WORKER_INTENT_KEY = "Worker";
    public static final String USER_HISTORY_INTENT_KEY = "History";
    public static final String USER_INTENT_KEY = "User";
    public static final String FROM_INTENT_KEY = "From";

    //Controllers METHODS KEY
    /*
    ********
     */

    //Worker COntroller
    //for arrays adapters autocompletetextview
    public static final String NATIONALITY_KEY = "nations";
    public static final String TOWN_KEY = "towns";
    public static final String STREET_KEY = "streets";

    public static String[] split(@NonNull String string, @NonNull String c){
        ArrayList<String> tab = new ArrayList<>();
        String res = string.toString();
        while (res.contains(c)){
            String r = res.substring(0, res.indexOf(c));
            tab.add(r);
            res = res.substring(res.indexOf(c)+1, res.length());
        }
        tab.add(res);

        String[] ret = new String[tab.size()];
        return tab.toArray(ret);
    }

    public static final String URL_PROFILE_DEFAULT = "file:///android_asset/default.jpg";
    public static final String URL_PROFILE_NETW = "http://"+ Settings.ADD+"/";

    public static void renderRoundedProfileImage(ImageView imageView, String url, Context context){
        Glide.with(context).load(url)
                .crossFade()
                .thumbnail(0.5f)
                .bitmapTransform(new CircleTransform(context))
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(imageView);
    }

    public static void renderProfileImage(ImageView imageView, String url, Context context){
        if (url==null || (!url.contains("http") && !url.equals(URL_PROFILE_DEFAULT))) {
            try{
                url = new File(url).toURI().toURL().toString();
            } catch (Exception e){
                e.printStackTrace();
            }
        }
        Glide.with(context).load(url)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(imageView);
    }

    public static void renderProfileImage(ImageView imageView, String url, Context context, boolean printurl){
        renderProfileImage(imageView, url, context);
        if (printurl) System.out.println("PrintURL   " + url);
    }

    public static int indexOf(String[] tab, String s){
        if (tab!=null && tab.length>0){
            for (int i = 0; i<tab.length; i++){
                if (tab[i].equals(s)) return i;
            }
        }
        return -1;
    }

    public static String dateDifference(String expirationDate, String renewalDate, Context context){
        String _expirationDate = split(expirationDate, " ")[0];
        String _renewalDate = split(renewalDate, " ")[0];

        int _expDateYear = Integer.parseInt(split(_expirationDate, "-")[0]);
        int _expDateMonth = Integer.parseInt(split(_expirationDate, "-")[1]);
        int _renDateYear = Integer.parseInt(split(_renewalDate, "-")[0]);
        int _renDateMonth = Integer.parseInt(split(_renewalDate, "-")[1]);
        int diff = 12*(_expDateYear-_renDateYear) + (_expDateMonth-_renDateMonth);
        if (diff/12==0){
            return diff+" "+context.getString(R.string.month_label);
        }
        else{
            return (diff/12)+" "+context.getString(R.string.year_label)+ " "+(diff%12)+context.getString(R.string.month_label);
        }
    }

    public static String writeDate(String mysqlDate, Context context){
        String[] _date = split(split(mysqlDate, " ")[0], "-") ;
        String day = _date[2];
        int month = Integer.parseInt(_date[1])-1;
        String year = _date[0];
        String[] months = context.getResources().getStringArray(R.array.months);
        return day + " " + months[month] + " " + year;
    }

}
