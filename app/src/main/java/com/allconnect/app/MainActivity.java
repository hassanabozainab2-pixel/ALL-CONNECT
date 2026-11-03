package com.allconnect.app;

import android.app.Activity;
import android.os.Bundle;
import android.provider.Settings;
import android.content.ComponentName;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
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
    private int dp(float value) { return (int)(value * getResources().getDisplayMetrics().density + 0.5f); }

    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        getWindow().setStatusBarColor(Color.rgb(23,37,84));
        getWindow().setNavigationBarColor(Color.rgb(23,37,84));

        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);
        LinearLayout page = new LinearLayout(this);
        page.setOrientation(LinearLayout.VERTICAL);
        page.setPadding(dp(18), dp(22), dp(18), dp(24));
        page.setBackgroundColor(Color.rgb(243,246,252));
        scroll.addView(page);

        TextView brand = text("ALL CONNECT", 26, Color.rgb(23,37,84), true);
        page.addView(brand);
        TextView subtitle = text("كل إشعاراتك المهمة في مكان واحد", 14, Color.rgb(113,128,150), false);
        LinearLayout.LayoutParams subParams = params(-1,-2); subParams.topMargin=dp(5); subParams.bottomMargin=dp(18);
        page.addView(subtitle, subParams);

        LinearLayout hero = card();
        hero.setOrientation(LinearLayout.VERTICAL);
        TextView welcome = text("مرحباً بك", 21, Color.WHITE, true);
        hero.addView(welcome);
        TextView hint = text("فعّل صلاحية الوصول حتى يعرض التطبيق الإشعارات التي تسمح بها من تطبيقات هاتفك.", 14, Color.rgb(224,234,255), false);
        LinearLayout.LayoutParams hintP=params(-1,-2); hintP.topMargin=dp(8); hero.addView(hint,hintP);
        page.addView(hero, params(-1,-2));

        permissionStatus = text("", 14, Color.rgb(8,125,114), true);
        LinearLayout.LayoutParams statusP=params(-1,-2);statusP.topMargin=dp(16);statusP.bottomMargin=dp(8);page.addView(permissionStatus,statusP);

        Button permission = button("تفعيل الوصول للإشعارات");
        page.addView(permission, params(-1,-2));
        permission.setOnClickListener(v -> {
            try { startActivity(new android.content.Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)); }
            catch (Exception e) { Toast.makeText(this,"تعذر فتح إعدادات الإشعارات",Toast.LENGTH_LONG).show(); }
        });

        LinearLayout actions = new LinearLayout(this);
        actions.setOrientation(LinearLayout.HORIZONTAL);
        actions.setGravity(Gravity.CENTER_VERTICAL);
        LinearLayout.LayoutParams actionsP=params(-1,-2);actionsP.topMargin=dp(14);actionsP.bottomMargin=dp(8);
        page.addView(actions,actionsP);
        Button refresh=button("تحديث");
        Button clear=button("مسح السجل");
        LinearLayout.LayoutParams half=params(0,-2);half.weight=1;half.rightMargin=dp(5);actions.addView(refresh,half);
        LinearLayout.LayoutParams half2=params(0,-2);half2.weight=1;half2.leftMargin=dp(5);actions.addView(clear,half2);
        refresh.setOnClickListener(v -> renderNotifications());
        clear.setOnClickListener(v -> {
            getSharedPreferences("captured",MODE_PRIVATE).edit().putString("items","[]").apply();
            renderNotifications();
            Toast.makeText(this,"تم مسح سجل الإشعارات من هذا الجهاز",Toast.LENGTH_SHORT).show();
        });

        TextView heading=text("الإشعارات المستلمة",18,Color.rgb(23,37,84),true);
        LinearLayout.LayoutParams headingP=params(-1,-2);headingP.topMargin=dp(16);headingP.bottomMargin=dp(10);page.addView(heading,headingP);
        list=new LinearLayout(this);list.setOrientation(LinearLayout.VERTICAL);page.addView(list,params(-1,-2));

        TextView privacy=text("الخصوصية: الإشعارات تُحفظ محلياً على هاتفك فقط. لا يرسل التطبيق محتواها إلى خادم. يمكنك إيقاف الصلاحية من إعدادات أندرويد في أي وقت.",12,Color.rgb(113,128,150),false);
        LinearLayout.LayoutParams privacyP=params(-1,-2);privacyP.topMargin=dp(22);page.addView(privacy,privacyP);
        setContentView(scroll);
        refreshPermissionStatus();
        renderNotifications();
    }

    @Override protected void onResume() { super.onResume(); if(permissionStatus!=null)refreshPermissionStatus(); if(list!=null)renderNotifications(); }

    private void refreshPermissionStatus() {
        String enabled=Settings.Secure.getString(getContentResolver(),"enabled_notification_listeners");
        String mine=new ComponentName(this,NotificationCaptureService.class).flattenToString();
        boolean allowed=enabled!=null && enabled.toLowerCase(Locale.ROOT).contains(mine.toLowerCase(Locale.ROOT));
        permissionStatus.setText(allowed ? "● صلاحية الوصول مفعّلة" : "○ الصلاحية غير مفعّلة — اضغط الزر ثم اختر ALL CONNECT");
        permissionStatus.setTextColor(allowed?Color.rgb(8,125,114):Color.rgb(194,65,75));
    }

    private void renderNotifications() {
        list.removeAllViews();
        JSONArray items;
        try { items=new JSONArray(getSharedPreferences("captured",MODE_PRIVATE).getString("items","[]")); }
        catch(Exception e) { items=new JSONArray(); }
        if(items.length()==0) {
            TextView empty=text("لا توجد إشعارات محفوظة حتى الآن. بعد تفعيل الصلاحية، ستظهر الإشعارات الجديدة هنا.",14,Color.rgb(113,128,150),false);
            empty.setGravity(Gravity.CENTER);
            empty.setPadding(dp(16),dp(28),dp(16),dp(28));
            list.addView(empty,params(-1,-2));
            return;
        }
        for(int i=0;i<items.length();i++) {
            JSONObject item=items.optJSONObject(i); if(item==null)continue;
            LinearLayout box=cardLight(); box.setOrientation(LinearLayout.VERTICAL);
            String pkg=item.optString("app","تطبيق");
            String appName=pkg;
            try { android.content.pm.ApplicationInfo info=getPackageManager().getApplicationInfo(pkg,0); appName=getPackageManager().getApplicationLabel(info).toString(); } catch(Exception ignored) {}
            TextView app=text(appName,15,Color.rgb(49,94,202),true);box.addView(app);
            String title=item.optString("title","");
            if(!title.isEmpty()){TextView t=text(title,14,Color.rgb(23,37,84),true);LinearLayout.LayoutParams tp=params(-1,-2);tp.topMargin=dp(6);box.addView(t,tp);}
            String body=item.optString("body","");
            if(!body.isEmpty()){TextView b=text(body,14,Color.rgb(55,65,81),false);LinearLayout.LayoutParams bp=params(-1,-2);bp.topMargin=dp(4);box.addView(b,bp);}
            long time=item.optLong("time",0);
            TextView date=text(time>0?new SimpleDateFormat("dd/MM/yyyy  hh:mm a",Locale.getDefault()).format(new Date(time)):"",11,Color.rgb(113,128,150),false);
            LinearLayout.LayoutParams dateP=params(-1,-2);dateP.topMargin=dp(8);box.addView(date,dateP);
            LinearLayout.LayoutParams boxP=params(-1,-2);boxP.bottomMargin=dp(9);list.addView(box,boxP);
        }
    }

    private TextView text(String value,int size,int color,boolean bold) {
        TextView t=new TextView(this);t.setText(value);t.setTextSize(size);t.setTextColor(color);
        if(bold)t.setTypeface(Typeface.DEFAULT,Typeface.BOLD);
        t.setGravity(Gravity.RIGHT);t.setTextDirection(View.TEXT_DIRECTION_RTL);return t;
    }
    private LinearLayout card() {
        LinearLayout l=new LinearLayout(this);l.setPadding(dp(18),dp(18),dp(18),dp(18));
        GradientDrawable bg=new GradientDrawable(GradientDrawable.Orientation.TL_BR,new int[]{Color.rgb(23,37,84),Color.rgb(49,94,202)});bg.setCornerRadius(dp(20));l.setBackground(bg);return l;
    }
    private LinearLayout cardLight() {
        LinearLayout l=new LinearLayout(this);l.setPadding(dp(15),dp(14),dp(15),dp(14));
        GradientDrawable bg=new GradientDrawable();bg.setColor(Color.WHITE);bg.setCornerRadius(dp(15));bg.setStroke(dp(1),Color.rgb(232,237,246));l.setBackground(bg);return l;
    }
    private Button button(String label) {
        Button b=new Button(this);b.setText(label);b.setAllCaps(false);b.setTextSize(14);b.setTextColor(Color.WHITE);
        GradientDrawable bg=new GradientDrawable();bg.setColor(Color.rgb(49,94,202));bg.setCornerRadius(dp(11));b.setBackground(bg);b.setPadding(dp(10),dp(7),dp(10),dp(7));return b;
    }
    private LinearLayout.LayoutParams params(int w,int h){return new LinearLayout.LayoutParams(w<0?w:dp(w),h<0?h:dp(h));}
}
