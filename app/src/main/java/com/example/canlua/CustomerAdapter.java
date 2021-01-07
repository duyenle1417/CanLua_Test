package com.example.canlua;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collection;

public class CustomerAdapter extends RecyclerView.Adapter<CustomerAdapter.CustomerViewHolder> implements Filterable {

    private static RecycleViewItemOnClick recycleViewItemOnClick;
    private static PopupMenuListener popupMenuListener;
    private static Context context;
    public ArrayList<Customer> search;
    private ArrayList<Customer> list;
    private String orderByTime = DatabaseContract.CustomerTable._ID + " DESC";
    private Filter filter = new Filter() {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<Customer> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                DatabaseHelper helper = new DatabaseHelper(context);
                SharedPreferences sharedPreferences = context.getSharedPreferences("sort_customer", Context.MODE_PRIVATE);
                search.clear();
                search.addAll(helper.getAllCustomers(sharedPreferences.getString("sort_type", orderByTime)));
                filteredList.addAll(search);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (Customer item : search) {
                    if (item.getHoTen().toLowerCase().contains(filterPattern) || item.getSDT().contains(filterPattern)) {
                        filteredList.add(item);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            list.clear();
            list.addAll((Collection<? extends Customer>) results.values);
            notifyDataSetChanged();
        }
    };

    public CustomerAdapter(Context context, ArrayList<Customer> list, RecycleViewItemOnClick recycleViewItemOnClick, PopupMenuListener popupMenuListener) {
        CustomerAdapter.context = context;
        this.list = list;
        this.search = new ArrayList<>(list);
        CustomerAdapter.recycleViewItemOnClick = recycleViewItemOnClick;
        CustomerAdapter.popupMenuListener = popupMenuListener;
    }

    @NonNull
    @Override
    public CustomerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_item, parent, false);
        CustomerViewHolder holder = new CustomerViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final CustomerViewHolder holder, final int position) {
        Customer customer = list.get(position);

        holder.textView_name.setText(customer.getHoTen());
        holder.textView_phone.setText(customer.getSDT());
        holder.textView_date.setText("Thêm vào " + customer.getDate());
        holder.textView_index.setText(String.valueOf(position + 1));
        //open popup memu, include view, delete, fix data
        holder.ic_more_menu.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(context, holder.ic_more_menu);
                popup.getMenuInflater().inflate(R.menu.pop_up_menu, popup.getMenu());
                popup.show();
                //switch case for each menu item clicked
                popupMenuListener.PopupMenuClick(popup, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public Filter getFilter() {
        return filter;
    }


    //view holder for recycle view
    public static class CustomerViewHolder extends RecyclerView.ViewHolder {
        private TextView textView_name;
        private TextView textView_phone;
        private TextView textView_date;
        private TextView textView_index;
        private ImageView ic_more_menu;


        public CustomerViewHolder(@NonNull View itemView) {
            super(itemView);
            textView_name = itemView.findViewById(R.id.home_item_name_tv);
            textView_phone = itemView.findViewById(R.id.home_item_phone_tv);
            textView_date = itemView.findViewById(R.id.home_item_date);
            textView_index = itemView.findViewById(R.id.home_item_index);
            ic_more_menu = itemView.findViewById(R.id.ic_more_menu);

            //onclick => open history of that customer
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    recycleViewItemOnClick.OnItemClicked(getAdapterPosition());
                }
            });
        }
    }
}
