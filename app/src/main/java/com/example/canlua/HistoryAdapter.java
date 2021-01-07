package com.example.canlua;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> implements Filterable {
    public ArrayList<History> search;
    private ArrayList<History> list;
    private Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<History> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(search);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (History item : search) {
                    if (item.getTenGiongLua().toLowerCase().contains(filterPattern) || item.getTimestamp().contains(filterPattern)) {
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
            list.addAll((Collection<? extends History>) results.values);
            notifyDataSetChanged();
        }
    };
    private Context context;
    private PopupMenuListener popupMenuListener;

    public HistoryAdapter(Context context, ArrayList<History> obj, PopupMenuListener popupMenuListener) {
        list = obj;
        this.context = context;
        this.search = new ArrayList<>(obj);
        this.popupMenuListener = popupMenuListener;
    }

    @NonNull
    @Override
    public HistoryAdapter.HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.history_item, parent, false);
        HistoryViewHolder holder = new HistoryViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final HistoryViewHolder holder, final int position) {
        History customer = list.get(position);

        DecimalFormat decimalFormat = new DecimalFormat("###,###,###,###");
        decimalFormat.setRoundingMode(RoundingMode.UP);

        holder.textView_tengiong.setText(customer.getTenGiongLua());
        holder.textView_dongia.setText(String.format("%s VND", decimalFormat.format(customer.getDonGia())));
        holder.textView_trubi.setText(String.format("%s bao/KG", customer.getBaoBi()));
        holder.textView_sobao.setText(String.format("%s bao", customer.getSoBao()));
        holder.textView_sokg.setText(String.format("%s KG", customer.getTongSoKG()));
        holder.textView_tiencoc.setText(String.format("%s VND", decimalFormat.format(customer.getTienCoc())));
        holder.textView_thanhtien.setText(String.format("%s VND", decimalFormat.format(customer.getThanhTien())));
        holder.textView_date.setText(customer.getTimestamp());
        holder.section_expand.setVisibility(View.GONE);
        holder.ic_more.setVisibility(View.VISIBLE);

        holder.ic_more_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(context, holder.ic_more_menu);
                popup.getMenuInflater().inflate(R.menu.pop_up_history, popup.getMenu());
                popup.show();
                popupMenuListener.PopupMenuClick(popup, position);
            }
        });

        holder.ic_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.section_expand.setVisibility(View.VISIBLE);
                holder.ic_more.setVisibility(View.GONE);
            }
        });

        holder.ic_less.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.section_expand.setVisibility(View.GONE);
                holder.ic_more.setVisibility(View.VISIBLE);
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

    public static class HistoryViewHolder extends RecyclerView.ViewHolder {
        public TextView textView_tengiong;
        public TextView textView_dongia;
        public TextView textView_trubi;
        public TextView textView_sobao;
        public TextView textView_sokg;
        public TextView textView_tiencoc;
        public TextView textView_thanhtien;
        public TextView textView_date;
        public ImageView ic_more_menu;
        public ImageView ic_more;
        public ImageView ic_less;
        ConstraintLayout section_expand;

        public HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            textView_tengiong = itemView.findViewById(R.id.history_item_tengiong_data);
            textView_dongia = itemView.findViewById(R.id.history_item_dongia_data);
            textView_trubi = itemView.findViewById(R.id.history_item_trubi_data);
            textView_sobao = itemView.findViewById(R.id.history_item_sobao_data);
            textView_sokg = itemView.findViewById(R.id.history_item_sokg_data);
            textView_tiencoc = itemView.findViewById(R.id.history_item_tiencoc_data);
            textView_thanhtien = itemView.findViewById(R.id.history_item_thanhtien_data);
            textView_date = itemView.findViewById(R.id.history_item_date_lb);
            ic_more_menu = itemView.findViewById(R.id.ic_more_menu_history);
            ic_more = itemView.findViewById(R.id.ic_expand_more);
            ic_less = itemView.findViewById(R.id.ic_expand_less);
            section_expand = itemView.findViewById(R.id.expand_section);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (section_expand.getVisibility() == View.GONE) {
                        section_expand.setVisibility(View.VISIBLE);
                        ic_more.setVisibility(View.GONE);
                    } else {
                        section_expand.setVisibility(View.GONE);
                        ic_more.setVisibility(View.VISIBLE);
                    }
                }
            });
        }
    }
}