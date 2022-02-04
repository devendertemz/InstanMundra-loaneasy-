package com.loaneasy.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.loaneasy.Beans.ItemObject;
import com.loaneasy.HomeActivity;
import com.loaneasy.LoanCalculator;
import com.loaneasy.R;

import java.util.List;


@SuppressWarnings("ALL")
public class HomeFragmentAdapter extends RecyclerView.Adapter<HomeFragmentAdapter.ViewHolder> {

    private List<ItemObject> itemList;
    private Context context;

    public HomeFragmentAdapter(Context context, List<ItemObject> itemList)
    {
        this.context = context;
        this.itemList = itemList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_fragment_items, null);
        return new ViewHolder(layoutView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.iv_home_fragment.setImageResource(itemList.get(position).getPhoto());
        holder.tv_name.setText(itemList.get(position).getName());

        holder.card_home_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = null;
                Intent intent = null;
                switch (position) {
                    case 0:

                        context.startActivity(new Intent(context, LoanCalculator.class));

                        break;

                    case 1:
                        context.startActivity(new Intent(context, LoanCalculator.class));
                        break;

                    case 2:
                        context.startActivity(new Intent(context, LoanCalculator.class));
                        break;

                    case 3:
                        context.startActivity(new Intent(context, LoanCalculator.class));
                        break;

                    case 4:
                        context.startActivity(new Intent(context, LoanCalculator.class));
                        break;

                    case 5:
                        context.startActivity(new Intent(context, LoanCalculator.class));
                        break;

                    default:
                        break;
                }


                if (fragment != null){
                    FragmentManager fragmentManager = ((HomeActivity)context).getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.container_body, fragment);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        ImageView iv_home_fragment;
        TextView tv_name;
        CardView card_home_list;

        ViewHolder(View itemView) {
            super(itemView);

            iv_home_fragment = itemView.findViewById(R.id.iv_home_fragment);
            tv_name = itemView.findViewById(R.id.tv_name);
            card_home_list = itemView.findViewById(R.id.card_home_list);
        }
    }
}
