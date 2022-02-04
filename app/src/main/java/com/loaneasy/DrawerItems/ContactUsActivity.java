package com.loaneasy.DrawerItems;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;


import androidx.appcompat.app.AppCompatActivity;

import com.loaneasy.R;

public class ContactUsActivity extends AppCompatActivity {
    TextView call1,call2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us);
        call1=findViewById(R.id.call1);
        call2=findViewById(R.id.call2);

        call1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:"+"7290077011"));//change the number
                startActivity(callIntent);
            }
        });
        call2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:"+"8448863608"));//change the number
                startActivity(callIntent);
            }
        });

    }
}
