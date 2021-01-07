package com.example.canlua;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Collection;

public class HistoryFragment extends Fragment implements PopupMenuListener {
    FloatingActionButton btn_add_history;
    RecyclerView recyclerView_history;
    TextView textView_notify_empty_history;
    TextView banner_name;
    SearchView searchView;
    ArrayList<History> historyList;
    Customer customer;
    HistoryAdapter adapter;
    DatabaseHelper helper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_history, container, false);
        btn_add_history = view.findViewById(R.id.btn_add_history);
        recyclerView_history = view.findViewById(R.id.recycleview_history);
        textView_notify_empty_history = view.findViewById(R.id.history_notify_empty_recycleview);
        banner_name = view.findViewById(R.id.banner_name);
        searchView = view.findViewById(R.id.search_bar_history);
        historyList = new ArrayList<>();
        customer = new Customer();
        helper = new DatabaseHelper(getContext());

        recyclerView_history.setLayoutManager(new LinearLayoutManager(getContext()));
        historyList = helper.getAllHistories(getArguments().getLong("ID"));
        adapter = new HistoryAdapter(getContext(), historyList, this);
        recyclerView_history.setAdapter(adapter);

        if (historyList.isEmpty()) {
            textView_notify_empty_history.setVisibility(View.VISIBLE);
        } else {
            textView_notify_empty_history.setVisibility(View.GONE);
        }

        customer = helper.getCustomer(getArguments().getLong("ID"));
        banner_name.setText(customer.getHoTen() + " - " + customer.getSDT());
        //Toast.makeText(getContext(), String.valueOf(getArguments().getLong("ID")), Toast.LENGTH_SHORT).show();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        btn_add_history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle args = new Bundle();
                args.putLong("id", getArguments().getLong("ID"));
                Navigation.findNavController(getActivity(), R.id.nav_host_fragment)
                        .navigate(R.id.action_historyFragment_to_inputRiceInfoFragment, args);
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });
    }

    @Override
    public void PopupMenuClick(PopupMenu popupMenu, final int position) {
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                //Toast.makeText(context, "" + item.getTitle(), Toast.LENGTH_SHORT).show();
                switch (item.getItemId()) {
                    case R.id.menuEdit_history:
                        //fragment edit
                        Bundle args = new Bundle();
                        args.putString("timestamp", historyList.get(position).getTimestamp());
                        Navigation.findNavController(getActivity(), R.id.nav_host_fragment)
                                .navigate(R.id.action_historyFragment_to_editHistoryFragment, args);
                        break;
                    case R.id.menuDel_history:
                        //delete from db
                        helper.deleteHistory(historyList.get(position));
                        Snackbar.make(getActivity().findViewById(android.R.id.content), "Thông tin lịch sử đơn hàng đã được xóa!", Snackbar.LENGTH_SHORT)
                                .show();
                        //reload view
                        adapter.search.remove(position);
                        if (!searchView.isIconified()) {
                            //trường hợp có dùng search view
                            searchView.onActionViewCollapsed();//đóng khung tìm kiếm và xóa tìm kiếm
                            historyList.addAll((Collection<? extends History>) adapter.search);
                        } else {
                            historyList.remove(position);
                        }
                        recyclerView_history.removeViewAt(position);
                        historyList.remove(position);
                        adapter.notifyItemRemoved(position);
                        adapter.notifyDataSetChanged();

                        if (historyList.isEmpty()) {
                            textView_notify_empty_history.setVisibility(View.VISIBLE);
                        } else {
                            textView_notify_empty_history.setVisibility(View.GONE);
                        }
                        break;
                    default:
                        throw new IllegalStateException("Unexpected value: " + item.getItemId());
                }
                return true;
            }
        });
    }
}