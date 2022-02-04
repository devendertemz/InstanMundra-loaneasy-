package com.loaneasy.DrawerItems;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.FrameLayout;
import androidx.appcompat.app.AppCompatActivity;
import com.loaneasy.R;
import com.loaneasy.WelcomeActivity;

public class PrivacyPolicy extends AppCompatActivity {

    private Button terms;
    protected FrameLayout frameLayout;
    private boolean flag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.privacy_policy_webview);

        frameLayout = findViewById(R.id.layoutTermsButton);
        terms = findViewById(R.id.tvWebTerms);

        Intent intent = getIntent();
        if (intent!= null) {
            flag = intent.getBooleanExtra("termsButton", false);
        }

        if (flag)
        {
            frameLayout.setVisibility(View.VISIBLE);
            terms.setVisibility(View.VISIBLE);
        }
        else
        {
            frameLayout.setVisibility(View.GONE);
            terms.setVisibility(View.GONE);
        }


        WebView browser = (WebView) findViewById(R.id.webview_privacy_policy);

        browser.loadUrl("https://www.instantmudra.com/privacy_policy.html");



        terms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent welcomeActivity = new Intent(getApplicationContext(), WelcomeActivity.class);
                startActivity(welcomeActivity);
                finish();

            }
        });
    }
}
