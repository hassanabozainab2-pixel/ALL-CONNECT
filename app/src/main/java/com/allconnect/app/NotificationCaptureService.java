package com.allconnect.app;

import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.app.Notification;
import android.os.Bundle;
import org.json.JSONArray;
import org.json.JSONObject;

public class NotificationCaptureService extends NotificationListenerService {
    @Override public void onNotificationPosted(StatusBarNotification sbn) {
        if (sbn == null || sbn.getNotification() == null) return;
        Notification notification = sbn.getNotification();
        Bundle extras = notification.extras;
        CharSequence title = extras == null ? null : extras.getCharSequence(Notification.EXTRA_TITLE);
        CharSequence body = extras == null ? null : extras.getCharSequence(Notification.EXTRA_TEXT);
        if (body == null && extras != null) body = extras.getCharSequence(Notification.EXTRA_BIG_TEXT);
        String titleText = title == null ? "" : title.toString();
        String bodyText = body == null ? "" : body.toString();
        if (titleText.trim().isEmpty() && bodyText.trim().isEmpty()) return;
        try {
            JSONObject entry = new JSONObject();
            entry.put("app", sbn.getPackageName());
            entry.put("title", titleText);
            entry.put("body", bodyText);
            entry.put("time", sbn.getPostTime());
            android.content.SharedPreferences prefs = getSharedPreferences("captured", MODE_PRIVATE);
            JSONArray previous;
            try { previous = new JSONArray(prefs.getString("items", "[]")); }
            catch (Exception ignored) { previous = new JSONArray(); }
            JSONArray updated = new JSONArray();
            updated.put(entry);
            for (int i = 0; i < previous.length() && i < 99; i++) {
                JSONObject old = previous.optJSONObject(i);
                if (old != null) updated.put(old);
            }
            prefs.edit().putString("items", updated.toString()).apply();
        } catch (Exception ignored) {
            // Keep the notification listener alive if one notification has unexpected data.
        }
    }
}