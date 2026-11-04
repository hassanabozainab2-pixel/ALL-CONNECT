package com.allconnect.app;

import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.app.Notification;
import android.os.Bundle;
import org.json.JSONArray;
import org.json.JSONObject;

public class NotificationCaptureService extends NotificationListenerService {
    // Only capture notifications from the social platforms explicitly disclosed in the app.
    private static final java.util.Set<String> ALLOWED_PACKAGES = new java.util.HashSet<>(
            java.util.Arrays.asList(
                    "com.whatsapp",
                    "com.instagram.android",
                    "com.facebook.katana",
                    "org.telegram.messenger",
                    "com.google.android.youtube",
                    "com.twitter.android"
            ));

    @Override public void onNotificationPosted(StatusBarNotification sbn) {
        if(sbn==null || sbn.getPackageName()==null || !ALLOWED_PACKAGES.contains(sbn.getPackageName())) return;
        Notification n=sbn.getNotification();
        if(n==null || n.extras==null) return;
        Bundle extras=n.extras;
        String title=String.valueOf(extras.getCharSequence(Notification.EXTRA_TITLE,""));
        CharSequence text=extras.getCharSequence(Notification.EXTRA_BIG_TEXT);
        if(text==null) text=extras.getCharSequence(Notification.EXTRA_TEXT);
        String body=text==null?"":text.toString();
        if(title.trim().isEmpty() && body.trim().isEmpty()) return;
        try {
            android.content.SharedPreferences prefs=getSharedPreferences("captured",MODE_PRIVATE);
            JSONArray old=new JSONArray(prefs.getString("items","[]"));
            JSONArray next=new JSONArray();
            JSONObject item=new JSONObject();
            item.put("app",sbn.getPackageName());item.put("title",title);item.put("body",body);item.put("time",sbn.getPostTime());
            next.put(item);
            for(int i=0;i<old.length() && next.length()<100;i++) {
                JSONObject previous=old.optJSONObject(i);
                if(previous!=null) next.put(previous);
            }
            prefs.edit().putString("items",next.toString()).apply();
        } catch(Exception ignored) {}
    }
}
