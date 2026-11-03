package com.allconnect.app;
import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;
public class MainActivity extends Activity { public void onCreate(Bundle b) { super.onCreate(b); TextView t=new TextView(this); t.setText("ALL CONNECT"); t.setTextSize(28); setContentView(t); } }
