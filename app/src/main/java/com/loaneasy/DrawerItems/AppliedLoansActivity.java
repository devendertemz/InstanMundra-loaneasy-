package com.loaneasy.DrawerItems;


import android.os.Bundle;


import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.loaneasy.Adapter.CustomPagerAdapter;
import com.loaneasy.R;
import com.loaneasy.utils.UserSharedPreference;

public class AppliedLoansActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;

    UserSharedPreference sharedPreference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_applied_loans);

        tabLayout = findViewById(R.id.ordersTabs);
        viewPager = findViewById(R.id.viewpager_myOrder);

       /* sharedPreference = new UserSharedPreference(this);

        sharedPreference.setUserId("179379");
*/
        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);
        //openLoanFrag();
    }

   /* public void onClick(View v){
        new AppliedLoansFragment().onClick(v);
    }*/

    void setupViewPager(ViewPager viewPager) {

        CustomPagerAdapter adapter = new CustomPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new AppliedLoansFragment(), "My Loans");
        adapter.addFragment(new PayNowFragment(), "Pay Now");
        adapter.addFragment(new CompletedLoansFragment(), "Completed");
        viewPager.setAdapter(adapter);
    }

    /*public void openLoanFrag() {
        getSupportFragmentManager().beginTransaction().add(R.id.containerLoans, new AppliedLoansFragment()).commit();
    }*/

}
