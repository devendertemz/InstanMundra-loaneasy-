package com.loaneasy.Drawer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.recyclerview.widget.RecyclerView;

import com.loaneasy.R;

import java.util.Collections;
import java.util.List;


public class NavigationDrawerAdapter extends RecyclerView.Adapter<NavigationDrawerAdapter.MyViewHolder> {
    List<NavDrawerItem> data = Collections.emptyList();
    private LayoutInflater inflater;
    private int[] icons = new int[]{
            // Put your Icon from Drawable folder here ..
     //       R.drawable.ic_home_red,
            R.drawable.ic_user_red,
            R.drawable.ic_statement_red,
            R.drawable.ic_applied,
            R.drawable.ic_about_red,
            R.drawable.ic_ecs_mandate,
            R.drawable.ic_user_bank_details,
            R.drawable.ic_tc_red,
            R.drawable.ic_faq,
            R.drawable.ic_contactus_red,
            R.drawable.ic_logout_red
    };

    NavigationDrawerAdapter(Context context, List<NavDrawerItem> data) {
        inflater = LayoutInflater.from(context);
        this.data = data;
    }

    public void delete(int position) {
        data.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.nav_drawer_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        NavDrawerItem current = data.get(position);
        holder.title.setText(current.getTitle());
        holder.icons.setImageResource(icons[position]);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        ImageView icons;

        MyViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.titleDrawer);
            icons = itemView.findViewById(R.id.iconsDrawer);
        }
    }
}
