package com.allconnect.app;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
public class NotificationCaptureService extends NotificationListenerService {
 @Override public void onNotificationPosted(StatusBarNotification sbn) {
  CharSequence title=sbn.getNotification().extras.getCharSequence("android.title");
  CharSequence body=sbn.getNotification().extras.getCharSequence("android.text");
  String line=sbn.getPackageName()+" | "+(title==null?"":title)+" | "+(body==null?"":body);
  String old=getSharedPreferences("captured",MODE_PRIVATE).getString("items","");
  getSharedPreferences("captured",MODE_PRIVATE).edit().putString("items",line+"\n"+old).apply();
 }
}
