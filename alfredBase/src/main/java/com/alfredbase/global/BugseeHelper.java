package com.alfredbase.global;

import android.app.Application;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.alfredbase.BuildConfig;
import com.bugsee.library.Bugsee;

import java.util.HashMap;

/**
 * Created by Arif S. on 6/19/19
 */
public class BugseeHelper {
    public static void init(Application application, String token) {
        HashMap<String, Object> options = new HashMap<>();
        options.put(Bugsee.Option.VideoEnabled, false);//set true to recode video
        options.put(Bugsee.Option.MaxRecordingTime, 2 * 60 * 60);
        options.put(Bugsee.Option.ShakeToTrigger, false);

        if (BuildConfig.DEBUG) {
            Bugsee.launch(application, "2cecb9d3-e94e-44c9-a9ee-7cd1f11a51f2", options);
        } else {
            Bugsee.launch(application, token, options);
        }

    }

    public static void setEmail(String email) {
        Bugsee.setEmail(email);
    }

    public static void setAttribute(String key, Object value) {
        Bugsee.setAttribute(key, value);
    }

    public static void trace(String key, Object value) {
        Bugsee.setAttribute(key, value);
    }

    public static void buttonClicked(View v) {
        String text = String.valueOf(v.getId());

        try {
            if (v instanceof Button) {
                text = ((Button) v).getText().toString();
            } else if (v instanceof TextView) {
                text = ((TextView) v).getText().toString();
            } else if (v instanceof ImageButton) {
            } else if (v instanceof ImageView) {
            }
        } catch (Exception ex) {
            text = String.valueOf(v.getId());
        }

        if (v.getTag() != null)
            text += " -> " + v.getTag();

        buttonClicked(text);
    }

    public static void buttonClicked(String message) {
        event("BC -> " + message);
    }

    public static void event(String message) {
        Bugsee.event(message);
    }

    public static void event(String key, HashMap<String, Object> param) {
        Bugsee.setAttribute(key, param);
    }

    public static void log(String message) {
        Bugsee.log(message);
    }

    public static void reportBugsee() {
        try {
            throw new NullPointerException();
        } catch (NullPointerException ex) {

        }
    }

}
