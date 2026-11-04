package com.allconnect.app;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONObject;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends Activity {
    private LinearLayout list;
    private TextView permissionStatus;
    private int dp(float v) { return (int)(v * getResources().getDisplayMetrics().density + 0.5f); }

    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        getWindow().setStatusBarColor(Color.rgb(15, 23, 55));
        getWindow().setNavigationBarColor(Color.rgb(15, 23, 55));
        buildScreen();
    }

    @Override protected void onResume() {
        super.onResume();
        if (permissionStatus != null) updatePermissionStatus();
        if (list != null) renderNotifications();
    }

    private void buildScreen() {
        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);
        LinearLayout page = new LinearLayout(this);
        page.setOrientation(LinearLayout.VERTICAL);
        page.setPadding(dp(18), dp(22), dp(18), dp(26));
        page.setBackgroundColor(Color.rgb(244, 247, 252));
        scroll.addView(page);

        TextView brand = text("ALL CONNECT", 27, Color.rgb(18, 31, 72), true);
        brand.setGravity(Gravity.CENTER);
        page.addView(brand);
        TextView subtitle = text("كل منصاتك وإشعاراتك في مكان واحد", 14, Color.rgb(103, 116, 139), false);
        subtitle.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams sp = params(-1,-2); sp.topMargin=dp(5); sp.bottomMargin=dp(18);
        page.addView(subtitle,sp);

        LinearLayout hero = card(true);
        hero.setOrientation(LinearLayout.VERTICAL);
        hero.addView(text("أهلاً بك في ALL CONNECT",21,Color.WHITE,true));
        TextView hint=text("افتح منصاتك من مكان واحد، وتابع الإشعارات التي تسمح للتطبيق بالوصول إليها.",14,Color.rgb(228,235,255),false);
        LinearLayout.LayoutParams hp=params(-1,-2); hp.topMargin=dp(8); hero.addView(hint,hp);
        page.addView(hero,params(-1,-2));

        TextView platformTitle=text("منصات التواصل",18,Color.rgb(18,31,72),true);
        LinearLayout.LayoutParams pt=params(-1,-2);pt.topMargin=dp(20);pt.bottomMargin=dp(10);page.addView(platformTitle,pt);
        addPlatformRow(page,"واتساب","إنستجرام","com.whatsapp","com.instagram.android","https://www.whatsapp.com","https://www.instagram.com");
        addPlatformRow(page,"فيسبوك","تيليجرام","com.facebook.katana","org.telegram.messenger","https://www.facebook.com","https://web.telegram.org");
        addPlatformRow(page,"يوتيوب","X","com.google.android.youtube","com.twitter.android","https://www.youtube.com","https://x.com");

        TextView notificationTitle=text("مركز الإشعارات",18,Color.rgb(18,31,72),true);
        LinearLayout.LayoutParams nt=params(-1,-2);nt.topMargin=dp(22);nt.bottomMargin=dp(8);page.addView(notificationTitle,nt);
        permissionStatus=text("",13,Color.rgb(8,125,114),true);
        page.addView(permissionStatus,params(-1,-2));
        Button permission=button("تفعيل صلاحية الإشعارات");
        LinearLayout.LayoutParams pp=params(-1,-2);pp.topMargin=dp(9);page.addView(permission,pp);
        permission.setOnClickListener(v -> new android.app.AlertDialog.Builder(this)
                .setTitle("إذن الوصول للإشعارات")
                .setMessage("إذا وافقت، سيقرأ ALL CONNECT محتوى الإشعارات التي تصدر من واتساب وإنستجرام وفيسبوك وتيليجرام ويوتيوب وX فقط، ليعرضها داخل التطبيق. تُحفظ على هاتفك محلياً في هذه النسخة ولا تُرسل إلى خادم. يمكنك رفض الإذن أو إيقافه في أي وقت من إعدادات أندرويد.")
                .setNegativeButton("إلغاء", (d, w) -> d.dismiss())
                .setPositiveButton("متابعة إلى الإعدادات", (d, w) -> {
                    try { startActivity(new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)); }
                    catch(Exception e) { Toast.makeText(this,"افتح إعدادات الإشعارات من إعدادات الهاتف",Toast.LENGTH_LONG).show(); }
                }).show());

        LinearLayout controls=new LinearLayout(this);controls.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams cp=params(-1,-2);cp.topMargin=dp(12);cp.bottomMargin=dp(8);page.addView(controls,cp);
        Button refresh=button("تحديث");Button clear=button("مسح السجل");
        LinearLayout.LayoutParams c1=params(0,-2);c1.weight=1;c1.rightMargin=dp(5);controls.addView(refresh,c1);
        LinearLayout.LayoutParams c2=params(0,-2);c2.weight=1;c2.leftMargin=dp(5);controls.addView(clear,c2);
        refresh.setOnClickListener(v -> renderNotifications());
        clear.setOnClickListener(v -> {
            getSharedPreferences("captured",MODE_PRIVATE).edit().putString("items","[]").apply();
            renderNotifications();
            Toast.makeText(this,"تم مسح الإشعارات المحفوظة على هذا الهاتف",Toast.LENGTH_SHORT).show();
        });

        list=new LinearLayout(this);list.setOrientation(LinearLayout.VERTICAL);page.addView(list,params(-1,-2));
        TextView privacy=text("الخصوصية: تُحفظ الإشعارات على هذا الجهاز فقط في هذه النسخة. لا يتم إرسال محتواها إلى خادم. يمكنك إيقاف الصلاحية في أي وقت من إعدادات أندرويد.",12,Color.rgb(103,116,139),false);
        LinearLayout.LayoutParams pr=params(-1,-2);pr.topMargin=dp(22);page.addView(privacy,pr);
        setContentView(scroll);
        updatePermissionStatus();
        renderNotifications();
    }

    private void addPlatformRow(LinearLayout page,String a,String b,String pkgA,String pkgB,String urlA,String urlB) {
        LinearLayout row=new LinearLayout(this);row.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams rp=params(-1,-2);rp.bottomMargin=dp(8);page.addView(row,rp);
        Button one=button(a);Button two=button(b);
        LinearLayout.LayoutParams p1=params(0,54);p1.weight=1;p1.rightMargin=dp(5);row.addView(one,p1);
        LinearLayout.LayoutParams p2=params(0,54);p2.weight=1;p2.leftMargin=dp(5);row.addView(two,p2);
        one.setOnClickListener(v -> openPlatform(pkgA,urlA));
        two.setOnClickListener(v -> openPlatform(pkgB,urlB));
    }

    private void openPlatform(String packageName,String url) {
        Intent launch=getPackageManager().getLaunchIntentForPackage(packageName);
        try {
            if(launch!=null) startActivity(launch);
            else startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
        } catch(Exception e) {
            Toast.makeText(this,"تعذر فتح المنصة على هذا الجهاز",Toast.LENGTH_SHORT).show();
        }
    }

    private void updatePermissionStatus() {
        boolean enabled=false;
        String flat=Settings.Secure.getString(getContentResolver(),"enabled_notification_listeners");
        if(flat!=null) {
            ComponentName me=new ComponentName(this,NotificationCaptureService.class);
            for(String item:flat.split(":")) {
                ComponentName parsed=ComponentName.unflattenFromString(item);
                if(me.equals(parsed)){enabled=true;break;}
            }
        }
        permissionStatus.setText(enabled ? "● استقبال الإشعارات مفعّل" : "○ استقبال الإشعارات غير مفعّل — اضغط الزر واسمح لـ ALL CONNECT");
        permissionStatus.setTextColor(enabled?Color.rgb(8,125,80):Color.rgb(160,92,20));
    }

    private void renderNotifications() {
        list.removeAllViews();
        JSONArray items;
        try { items=new JSONArray(getSharedPreferences("captured",MODE_PRIVATE).getString("items","[]")); }
        catch(Exception e) { items=new JSONArray(); }
        if(items.length()==0) {
            TextView empty=text("لا توجد إشعارات محفوظة حتى الآن. فعّل الصلاحية ثم انتظر وصول إشعار جديد.",14,Color.rgb(103,116,139),false);
            empty.setGravity(Gravity.CENTER);empty.setPadding(dp(14),dp(24),dp(14),dp(24));
            list.addView(empty,params(-1,-2));return;
        }
        for(int i=0;i<items.length();i++) {
            JSONObject item=items.optJSONObject(i);if(item==null)continue;
            LinearLayout box=card(false);box.setOrientation(LinearLayout.VERTICAL);
            String pkg=item.optString("app","تطبيق"),name=pkg;
            try { android.content.pm.ApplicationInfo info=getPackageManager().getApplicationInfo(pkg,0);name=getPackageManager().getApplicationLabel(info).toString(); } catch(Exception ignored) {}
            box.addView(text(name,14,Color.rgb(49,94,202),true));
            String title=item.optString("title","");
            if(!title.isEmpty()){TextView t=text(title,14,Color.rgb(18,31,72),true);LinearLayout.LayoutParams p=params(-1,-2);p.topMargin=dp(5);box.addView(t,p);}
            String body=item.optString("body","");
            if(!body.isEmpty()){TextView t=text(body,13,Color.rgb(55,65,81),false);LinearLayout.LayoutParams p=params(-1,-2);p.topMargin=dp(4);box.addView(t,p);}
            long time=item.optLong("time",0);
            box.addView(text(time>0?new SimpleDateFormat("dd/MM/yyyy  hh:mm a",Locale.getDefault()).format(new Date(time)):"",11,Color.rgb(103,116,139),false));
            LinearLayout.LayoutParams bp=params(-1,-2);bp.bottomMargin=dp(8);list.addView(box,bp);
        }
    }

    private TextView text(String value,int size,int color,boolean bold) {
        TextView t=new TextView(this);t.setText(value);t.setTextSize(size);t.setTextColor(color);
        if(bold)t.setTypeface(Typeface.DEFAULT,Typeface.BOLD);
        t.setGravity(Gravity.RIGHT);t.setTextDirection(View.TEXT_DIRECTION_RTL);return t;
    }
    private LinearLayout card(boolean gradient) {
        LinearLayout l=new LinearLayout(this);l.setPadding(dp(17),dp(17),dp(17),dp(17));
        GradientDrawable bg=gradient?new GradientDrawable(GradientDrawable.Orientation.TL_BR,new int[]{Color.rgb(15,23,55),Color.rgb(49,94,202)}):new GradientDrawable();
        if(!gradient){bg.setColor(Color.WHITE);bg.setStroke(dp(1),Color.rgb(230,235,245));}
        bg.setCornerRadius(dp(18));l.setBackground(bg);return l;
    }
    private Button button(String label) {
        Button b=new Button(this);b.setText(label);b.setAllCaps(false);b.setTextSize(14);b.setTextColor(Color.WHITE);
        GradientDrawable bg=new GradientDrawable();bg.setColor(Color.rgb(49,94,202));bg.setCornerRadius(dp(12));b.setBackground(bg);
        b.setPadding(dp(8),dp(6),dp(8),dp(6));return b;
    }
    private LinearLayout.LayoutParams params(int w,int h){return new LinearLayout.LayoutParams(w<0?w:dp(w),h<0?h:dp(h));}
}
