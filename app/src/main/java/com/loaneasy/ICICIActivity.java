package com.loaneasy;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.widget.Toast;

public class ICICIActivity extends AppCompatActivity {
    WebView simpleWebView;
    String url;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_i_c_i_c_i);
        simpleWebView = findViewById(R.id.simpleWebView);
        if (getIntent().getExtras() != null) {
            url = getIntent().getExtras().getString("url");

            simpleWebView.loadUrl(url);
            Log.e("hjk",url);
            Toast.makeText(this, url+"", Toast.LENGTH_SHORT).show();
            //double loanAmount = Integer.parseInt(getIntent().getExtras().getString("loan_amount"));

        }

    }
}