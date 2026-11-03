package com.allconnect.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends Activity {
    private final int navy = Color.rgb(15, 23, 55);
    private final int blue = Color.rgb(49, 94, 202);
    private final int orange = Color.rgb(255, 145, 55);
    private EditText phone;
    private EditText password;

    private int dp(float v) {
        return (int)(v * getResources().getDisplayMetrics().density + 0.5f);
    }

    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        getWindow().setStatusBarColor(navy);
        getWindow().setNavigationBarColor(navy);
        buildLoginScreen();
    }

    private void buildLoginScreen() {
        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);
        scroll.setBackgroundColor(Color.rgb(247, 249, 253));

        LinearLayout page = new LinearLayout(this);
        page.setOrientation(LinearLayout.VERTICAL);
        page.setGravity(Gravity.CENTER_HORIZONTAL);
        page.setPadding(dp(24), dp(24), dp(24), dp(28));
        scroll.addView(page);

        // Brand header
        LinearLayout logo = new LinearLayout(this);
        logo.setGravity(Gravity.CENTER);
        GradientDrawable logoBg = new GradientDrawable(GradientDrawable.Orientation.TL_BR,
                new int[]{blue, navy});
        logoBg.setCornerRadius(dp(25));
        logo.setBackground(logoBg);
        TextView mark = text("AC", 29, Color.WHITE, true);
        mark.setGravity(Gravity.CENTER);
        logo.addView(mark, new LinearLayout.LayoutParams(dp(82), dp(82)));
        page.addView(logo);

        TextView brand = text("ALL CONNECT", 27, navy, true);
        brand.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams brandP = params(-1, -2);
        brandP.topMargin = dp(15);
        page.addView(brand, brandP);

        TextView tagline = text("كل منصاتك في مكان واحد", 15, Color.rgb(105, 117, 140), false);
        tagline.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams tagP = params(-1, -2);
        tagP.topMargin = dp(5);
        tagP.bottomMargin = dp(25);
        page.addView(tagline, tagP);

        LinearLayout panel = new LinearLayout(this);
        panel.setOrientation(LinearLayout.VERTICAL);
        panel.setPadding(dp(20), dp(23), dp(20), dp(21));
        GradientDrawable panelBg = new GradientDrawable();
        panelBg.setColor(Color.WHITE);
        panelBg.setCornerRadius(dp(22));
        panelBg.setStroke(dp(1), Color.rgb(230, 235, 245));
        panel.setBackground(panelBg);
        page.addView(panel, params(-1, -2));

        TextView title = text("تسجيل الدخول", 22, navy, true);
        panel.addView(title);
        TextView desc = text("سجّل دخولك للمتابعة إلى حسابك", 13, Color.rgb(112, 123, 144), false);
        LinearLayout.LayoutParams descP = params(-1, -2);
        descP.topMargin = dp(7);
        descP.bottomMargin = dp(19);
        panel.addView(desc, descP);

        panel.addView(fieldLabel("رقم الهاتف"));
        phone = edit("أدخل رقم هاتفك", InputType.TYPE_CLASS_PHONE);
        LinearLayout.LayoutParams fieldP = params(-1, 54);
        fieldP.topMargin = dp(7);
        fieldP.bottomMargin = dp(15);
        panel.addView(phone, fieldP);

        panel.addView(fieldLabel("كلمة المرور"));
        password = edit("أدخل كلمة المرور", InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        LinearLayout.LayoutParams passP = params(-1, 54);
        passP.topMargin = dp(7);
        panel.addView(password, passP);

        TextView forgot = text("نسيت كلمة المرور؟", 13, blue, true);
        forgot.setGravity(Gravity.LEFT);
        LinearLayout.LayoutParams forgotP = params(-1, -2);
        forgotP.topMargin = dp(12);
        forgotP.bottomMargin = dp(17);
        panel.addView(forgot, forgotP);
        forgot.setOnClickListener(v -> showInfo("استعادة كلمة المرور", "سيتم تفعيل استعادة كلمة المرور بعد ربط خدمة الحسابات."));

        TextView login = actionButton("دخول", blue, Color.WHITE);
        panel.addView(login, params(-1, 54));
        login.setOnClickListener(v -> {
            String number = phone.getText().toString().trim();
            String pass = password.getText().toString();
            if (number.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "اكتب رقم الهاتف وكلمة المرور أولاً", Toast.LENGTH_SHORT).show();
            } else {
                showInfo("تسجيل الدخول", "واجهة الدخول جاهزة، لكن خدمة التحقق من الحسابات لم تُربط بعد؛ لم يتم تسجيل الدخول فعلياً.");
            }
        });

        TextView or = text("أو", 13, Color.rgb(130, 140, 158), false);
        or.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams orP = params(-1, -2);
        orP.topMargin = dp(15);
        orP.bottomMargin = dp(13);
        panel.addView(or, orP);

        TextView google = actionButton("G   المتابعة باستخدام Google", Color.WHITE, navy);
        GradientDrawable googleBg = new GradientDrawable();
        googleBg.setColor(Color.WHITE);
        googleBg.setCornerRadius(dp(14));
        googleBg.setStroke(dp(1), Color.rgb(215, 222, 235));
        google.setBackground(googleBg);
        panel.addView(google, params(-1, 52));
        google.setOnClickListener(v -> showInfo("Google", "زر Google جاهز في التصميم، ويلزم إعداد OAuth وربطه قبل تسجيل الدخول الحقيقي."));

        LinearLayout signupRow = new LinearLayout(this);
        signupRow.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams signupP = params(-1, -2);
        signupP.topMargin = dp(21);
        panel.addView(signupRow, signupP);
        TextView prompt = text("ليس لديك حساب؟", 13, Color.rgb(105, 117, 140), false);
        TextView signup = text(" سجل الآن", 14, orange, true);
        signupRow.addView(prompt);
        signupRow.addView(signup);
        signup.setOnClickListener(v -> showRegisterDialog());

        TextView footer = text("ALL CONNECT  •  تواصل أسهل، في مكان واحد", 11,
                Color.rgb(145, 154, 170), false);
        footer.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams footerP = params(-1, -2);
        footerP.topMargin = dp(22);
        page.addView(footer, footerP);

        setContentView(scroll);
    }

    private void showRegisterDialog() {
        LinearLayout form = new LinearLayout(this);
        form.setOrientation(LinearLayout.VERTICAL);
        form.setPadding(dp(18), dp(8), dp(18), dp(4));
        EditText name = new EditText(this);
        name.setSingleLine(true);
        name.setHint("الاسم");
        EditText mobile = new EditText(this);
        mobile.setSingleLine(true);
        mobile.setInputType(InputType.TYPE_CLASS_PHONE);
        mobile.setHint("رقم الهاتف");
        EditText pass = new EditText(this);
        pass.setSingleLine(true);
        pass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        pass.setHint("كلمة المرور");
        form.addView(name);
        form.addView(mobile);
        form.addView(pass);
        new AlertDialog.Builder(this).setTitle("إنشاء حساب جديد")
                .setView(form)
                .setPositiveButton("إنشاء الحساب", (d, w) ->
                        showInfo("إنشاء حساب", "نموذج التسجيل جاهز، لكن إنشاء الحساب الفعلي يحتاج تفعيل خدمة المصادقة أولاً."))
                .setNegativeButton("إلغاء", null).show();
    }

    private void showInfo(String title, String message) {
        new AlertDialog.Builder(this).setTitle(title).setMessage(message)
                .setPositiveButton("حسناً", null).show();
    }

    private TextView fieldLabel(String value) {
        TextView t = text(value, 13, navy, true);
        return t;
    }

    private EditText edit(String hint, int inputType) {
        EditText e = new EditText(this);
        e.setSingleLine(true);
        e.setTextSize(15);
        e.setTextColor(navy);
        e.setHintTextColor(Color.rgb(155, 164, 180));
        e.setHint(hint);
        e.setInputType(inputType);
        e.setPadding(dp(14), 0, dp(14), 0);
        GradientDrawable bg = new GradientDrawable();
        bg.setColor(Color.rgb(248, 250, 254));
        bg.setCornerRadius(dp(13));
        bg.setStroke(dp(1), Color.rgb(224, 230, 241));
        e.setBackground(bg);
        e.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
        return e;
    }

    private TextView actionButton(String label, int background, int foreground) {
        TextView t = text(label, 15, foreground, true);
        t.setGravity(Gravity.CENTER);
        GradientDrawable bg = new GradientDrawable();
        bg.setColor(background);
        bg.setCornerRadius(dp(14));
        t.setBackground(bg);
        return t;
    }

    private TextView text(String value, int size, int color, boolean bold) {
        TextView t = new TextView(this);
        t.setText(value);
        t.setTextSize(size);
        t.setTextColor(color);
        if (bold) t.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        t.setGravity(Gravity.RIGHT);
        t.setTextDirection(View.TEXT_DIRECTION_RTL);
        return t;
    }

    private LinearLayout.LayoutParams params(int width, int height) {
        return new LinearLayout.LayoutParams(width < 0 ? width : dp(width),
                height < 0 ? height : dp(height));
    }
}
