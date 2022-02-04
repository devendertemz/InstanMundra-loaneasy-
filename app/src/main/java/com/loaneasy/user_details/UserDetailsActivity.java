package com.loaneasy.user_details;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;


import androidx.appcompat.app.AppCompatActivity;

import com.loaneasy.OpenFrontCamera;
import com.loaneasy.R;

public class UserDetailsActivity extends AppCompatActivity {

    ImageView icon_1, icon_2, icon_3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_details);
        icon_1 = findViewById(R.id.icon_1);
        icon_2 = findViewById(R.id.icon_2);
        icon_3 = findViewById(R.id.icon_3);

        getSupportFragmentManager().beginTransaction().add(R.id.containerUser, new PersonalDetailsFragment()).commit();
    }


    public void startOfficialDetailFrag(String firstName, String lastName, String officialEmail, String gender, String dob,
                                        String currentLoan, String houseType, String etState, String etCity, String etPinCode,
                                        String etLocalAddress, String uriSelfie) {

        OfficialDetailsFragment fragment = new OfficialDetailsFragment();
        Bundle bundle = new Bundle();
        bundle.putString("firstName", firstName);
        bundle.putString("lastName", lastName);
        bundle.putString("officialEmail", officialEmail);
        bundle.putString("gender", gender);
        bundle.putString("dob", dob);
        bundle.putString("currentLoan", currentLoan);
        bundle.putString("houseType", houseType);
        bundle.putString("etState", etState);
        bundle.putString("etCity", etCity);
        bundle.putString("etPinCode", etPinCode);
        bundle.putString("etLocalAddress", etLocalAddress);
        bundle.putString("uriSelfie", uriSelfie);
        fragment.setArguments(bundle);
        if (!isFinishing()){
            getSupportFragmentManager().beginTransaction().replace(R.id.containerUser, fragment).addToBackStack(null).commit();
        }

        /*          intent.putExtra("firstName", etFirstName.getText().toString().trim());
                    intent.putExtra("lastName", etLastName.getText().toString().trim());
                    intent.putExtra("officialEmail", etOfficialEmail.getText().toString().trim());
                    intent.putExtra("gender", getGender);
                    intent.putExtra("dob", getDob);
                    intent.putExtra("currentLoan", getCurrentLoan);
                    intent.putExtra("houseType", getHouseType);
                    intent.putExtra("etState", etState.getText().toString().trim());
                    intent.putExtra("etCity", etCity.getText().toString().trim());
                    intent.putExtra("etPinCode", etPinCode.getText().toString().trim());
                    intent.putExtra("etLocalAddress", etLocalAddress.getText().toString().trim());  */
    }

    public void showBankDetailsFrag() {
        getSupportFragmentManager().beginTransaction().replace(R.id.containerUser, new BankDetailsFragment()).addToBackStack(null).commit();
    }

    public void getStatus(int id) {
        switch (id) {
            case 1:
                icon_1.setImageResource(R.drawable.ic_1);
                icon_2.setImageResource(R.drawable.ic_2);
                break;
            case 2:
                icon_1.setImageResource(R.drawable.ic_1_tick);
                icon_2.setImageResource(R.drawable.ic_2);
                break;
            case 3:
                icon_2.setImageResource(R.drawable.ic_2_tick);
                break;
        }
    }

    public void startFrontCam(String firstName, String lastName, String officialEmail, String gender, String dob,
                              String currentLoan, String houseType, String etState, String etCity, String etPinCode,
                              String etLocalAddress) {
        Intent intent = new Intent(UserDetailsActivity.this, OpenFrontCamera.class);
        intent.putExtra("firstName", firstName);
        intent.putExtra("lastName", lastName);
        intent.putExtra("officialEmail", officialEmail);
        intent.putExtra("gender", gender);
        intent.putExtra("dob", dob);
        intent.putExtra("currentLoan", currentLoan);
        intent.putExtra("houseType", houseType);
        intent.putExtra("etState", etState);
        intent.putExtra("etCity", etCity);
        intent.putExtra("etPinCode", etPinCode);
        intent.putExtra("etLocalAddress", etLocalAddress);
        startActivity(intent);
    }
}
