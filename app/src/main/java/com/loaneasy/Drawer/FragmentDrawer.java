package com.loaneasy.Drawer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.loaneasy.DrawerItems.AppliedLoansActivity;
import com.loaneasy.DrawerItems.ContactUsActivity;
import com.loaneasy.DrawerItems.FAQActivity;
import com.loaneasy.DrawerItems.PrivacyPolicy;
import com.loaneasy.DrawerItems.ProfileActivity;
import com.loaneasy.DrawerItems.RefundPolicy;
import com.loaneasy.DrawerItems.TermsConditionsActivity;
import com.loaneasy.EMandateBankDetails;
import com.loaneasy.R;
import com.loaneasy.RegisterMandate;
import com.loaneasy.login_signup.PhoneNoActivity;
import com.loaneasy.utils.NotifDatabase;
import com.loaneasy.utils.UserSharedPreference;
import java.util.ArrayList;
import java.util.List;

public class FragmentDrawer extends Fragment implements View.OnClickListener {

    ImageView iv_userPic;
    TextView tv_userName, tv_usermail, tcNotifCount;

    NotifDatabase notifDatabase;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private View containerView;
    private static String[] titles = null;
    FragmentDrawerListener drawerListener;
    UserSharedPreference sharedPreference;

    public FragmentDrawer() {

    }

    public void setDrawerListener(FragmentDrawerListener listener) {
        this.drawerListener = listener;
    }

    public static List<NavDrawerItem> getData() {
        List<NavDrawerItem> data = new ArrayList<>();

        // preparing navigation drawer items
        for (String title : titles) {
            NavDrawerItem navItem = new NavDrawerItem();
            navItem.setTitle(title);
            data.add(navItem);
        }
        return data;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPreference = new UserSharedPreference(getActivity());
        // drawer label
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.lyMyAccount:
                startActivity(new Intent(getActivity(), ProfileActivity.class));
                mDrawerLayout.closeDrawer(containerView);
                break;
            case R.id.lyApplLoans:
                startActivity(new Intent(getActivity(), AppliedLoansActivity.class));
                mDrawerLayout.closeDrawer(containerView);
                break;

            case R.id.lyTerms:
                startActivity(new Intent(getActivity(), TermsConditionsActivity.class));
                mDrawerLayout.closeDrawer(containerView);
                break;

            case R.id.lyEcsMandate:
                startActivity(new Intent(getActivity(), RegisterMandate.class));
                mDrawerLayout.closeDrawer(containerView);
                break;

            case R.id.lyBankDetails:
                Intent openBankDetails = new Intent(getActivity(), EMandateBankDetails.class);
                openBankDetails.putExtra("updateBank", true);
                startActivity(openBankDetails);
                mDrawerLayout.closeDrawer(containerView);
                break;

            case R.id.lyFaq:
                startActivity(new Intent(getActivity(), FAQActivity.class));
                mDrawerLayout.closeDrawer(containerView);
                break;

            case R.id.lycontact_us:
                startActivity(new Intent(getActivity(), ContactUsActivity.class));
                mDrawerLayout.closeDrawer(containerView);
                break;

            case R.id.lyLogout:
                sharedPreference.clearPrefs();
                startActivity(new Intent(getActivity(), PhoneNoActivity.class));
                getActivity().finish();
                break;

            case R.id.lyPrivacyPolicy:
                startActivity(new Intent(getActivity(), PrivacyPolicy.class));
                mDrawerLayout.closeDrawer(containerView);
                break;

            case R.id.lyRefundPolicy:
                startActivity(new Intent(getActivity(), RefundPolicy.class));
                mDrawerLayout.closeDrawer(containerView);
                break;


            default:
                break;
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflating view layout
        View layout = inflater.inflate(R.layout.fragment_navigation_drawer, container, false);
        iv_userPic = layout.findViewById(R.id.iv_userPic);
        tv_userName = layout.findViewById(R.id.tv_userName);
        tv_usermail = layout.findViewById(R.id.tv_usermail);
        tcNotifCount = layout.findViewById(R.id.tcNotifCount);
        notifDatabase = new NotifDatabase(getActivity());

        Glide.with(getActivity()).load(sharedPreference.getProfilePic()).apply(RequestOptions.circleCropTransform()).apply(RequestOptions.placeholderOf(R.drawable.user_image).error(R.drawable.user_image)).into(iv_userPic);
        return layout;
    }

    public void setNotifCount(){

        if (!notifDatabase.tableIsEmpty()) {

            Cursor cursor = notifDatabase.getAllData();
            if (cursor.getCount() != 0) {
                tcNotifCount.setVisibility(View.VISIBLE);
                tcNotifCount.setText(""+cursor.getCount());
            }
            notifDatabase.closedb();
        }
    }

    @Override
    public void onResume() {



        super.onResume();
        if (!sharedPreference.getUserSocialName().isEmpty()) {
            tv_userName.setText(sharedPreference.getUserSocialName());
            tv_usermail.setText(sharedPreference.getUserEmail());
        }
    }

    public void showLogoutAlertDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());

        // set title
        alertDialogBuilder.setTitle("Confirmation");

        // set dialog message
        alertDialogBuilder
                .setMessage("Logout")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        sharedPreference.clearPrefs();
                        startActivity(new Intent(getActivity(), PhoneNoActivity.class));
                        getActivity().finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        dialog.cancel();
                    }
                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }

    @SuppressLint("NewApi")
    public void setUp(int fragmentId, DrawerLayout drawerLayout, final Toolbar toolbar) {
        containerView = getActivity().findViewById(fragmentId);
        mDrawerLayout = drawerLayout;
        // Drawer Arrow Icon change color
        // mDrawerToggle.getDrawerArrowDrawable().setColor(getActivity().getColor(R.color.white));
        mDrawerToggle = new ActionBarDrawerToggle(getActivity(), drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getActivity().invalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                getActivity().invalidateOptionsMenu();
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                toolbar.setAlpha(1 - slideOffset / 2);
            }
        };

        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });

    }


    public interface ClickListener {
        void onClick(View view, int position);

        void onLongClick(View view, int position);
    }

    private static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private ClickListener clickListener;

        RecyclerTouchListener(Context context, final RecyclerView recyclerView, final ClickListener clickListener) {
            this.clickListener = clickListener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clickListener != null) {
                        clickListener.onLongClick(child, recyclerView.getChildPosition(child));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {

            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
                clickListener.onClick(child, rv.getChildPosition(child));
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
    }

    public interface FragmentDrawerListener {
        void onDrawerItemSelected(View view, int position);
    }



}