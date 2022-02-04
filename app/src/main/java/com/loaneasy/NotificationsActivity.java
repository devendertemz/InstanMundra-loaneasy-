package com.loaneasy;

import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;


import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.loaneasy.DrawerItems.CompletedLoansFragment;
import com.loaneasy.utils.NotifDatabase;
import com.loaneasy.utils.VerticalSpaceItemDecoration;

import java.util.ArrayList;

public class NotificationsActivity extends AppCompatActivity {

    private ArrayList<String> alNotifs;
    private NotifAdapter notifAdapter;
    private TextView tvNotifHeader, tvClear;
    private LinearLayout lyNoNotif;
    private RecyclerView rvNotif;
    private NotifDatabase notifDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        notifDatabase = new NotifDatabase(this);
        alNotifs = new ArrayList<>();
        lyNoNotif = findViewById(R.id.lyNoNotif);
        rvNotif = findViewById(R.id.rvNotif);
        tvNotifHeader = findViewById(R.id.tvNotifHeader);
        tvClear = findViewById(R.id.tvClear);

        notifAdapter = new NotifAdapter(NotificationsActivity.this);
        rvNotif.setAdapter(notifAdapter);
        rvNotif.setLayoutManager(new LinearLayoutManager(NotificationsActivity.this));
        rvNotif.addItemDecoration(new VerticalSpaceItemDecoration(22));
        findViewById(R.id.ivBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        tvClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notifDatabase.clearTable();
                notifications();
            }
        });

        notifications();
    }

    public void notifications(){
        if (!notifDatabase.tableIsEmpty()) {

            Cursor cursor = notifDatabase.getAllData();
            if (cursor.getCount() != 0) {
                lyNoNotif.setVisibility(View.GONE);
                rvNotif.setVisibility(View.VISIBLE);
                if (!alNotifs.isEmpty())
                    alNotifs.clear();
                tvNotifHeader.setText(cursor.getCount()+" new notification(s)");
                while (cursor.moveToNext()) {
                    alNotifs.add(cursor.getString(1));
                }
            }
            else
                tvClear.setVisibility(View.GONE);
            notifAdapter.notifyDataSetChanged();
            notifDatabase.closedb();
        }else{
            tvNotifHeader.setText("0 new notification(s)");
            lyNoNotif.setVisibility(View.VISIBLE);
            rvNotif.setVisibility(View.GONE);
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public class NotifAdapter extends RecyclerView.Adapter<NotifAdapter.ViewHolder> {

        private Context context;
        ProgressDialog progressDialog;

        public NotifAdapter(Context context) {
            this.context = context;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_notifs, null, false);
            RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutView.setLayoutParams(lp);

            return new ViewHolder(layoutView);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
             holder.tvNotifMsg.setText(alNotifs.get(position));
        }

        @Override
        public int getItemCount() {
            return alNotifs.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            //ImageView itemType;
            TextView tvNotifMsg;

            public ViewHolder(View itemView) {
                super(itemView);

                tvNotifMsg = itemView.findViewById(R.id.tvNotifMsg);
            }
        }
    }
}
