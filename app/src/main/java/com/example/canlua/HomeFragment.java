package com.example.canlua;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
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

import static android.content.Context.INPUT_METHOD_SERVICE;


public class HomeFragment extends Fragment
        implements RecycleViewItemOnClick, PopupMenuListener {
    FloatingActionButton btn_add_customer;
    RecyclerView recyclerView_customer;
    SearchView searchView;
    ImageView btn_sort;
    CustomerAdapter adapter;
    TextView textView_notify_empty_customer;
    ArrayList<Customer> customerlist;
    DatabaseHelper helper;
    SharedPreferences sharedPreferences;
    private String orderByName = DatabaseContract.CustomerTable.COLUMN_TENKH + " ASC";
    private String orderByTime = DatabaseContract.CustomerTable._ID + " DESC";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        helper = new DatabaseHelper(getContext());
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        btn_add_customer = view.findViewById(R.id.btn_add_customer);
        recyclerView_customer = view.findViewById(R.id.recycle_view_customer);
        searchView = view.findViewById(R.id.search_bar);
        btn_sort = view.findViewById(R.id.btn_sort);
        textView_notify_empty_customer = view.findViewById(R.id.home_notify_empty_recycleview);
        customerlist = new ArrayList<>();
        sharedPreferences = getActivity().getSharedPreferences("sort_customer", Context.MODE_PRIVATE);

        recyclerView_customer.setLayoutManager(new LinearLayoutManager(getContext()));

        customerlist = helper.getAllCustomers(sharedPreferences.getString("sort_type", orderByTime));
        adapter = new CustomerAdapter(getContext(), customerlist, this, this);
        if (customerlist.isEmpty()) {
            textView_notify_empty_customer.setVisibility(View.VISIBLE);
        } else {
            textView_notify_empty_customer.setVisibility(View.GONE);
        }
        recyclerView_customer.setAdapter(adapter);


        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        btn_add_customer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //start dialog to add customer
                Bundle args = new Bundle();
                args.putString("action", "add");
                Navigation.findNavController(getActivity(), R.id.nav_host_fragment)
                        .navigate(R.id.action_nav_home_to_addCustomerDialog, args);
            }
        });

        btn_sort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(getContext(), btn_sort);
                popup.getMenuInflater().inflate(R.menu.sort_menu, popup.getMenu());
                popup.show();
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.sort_by_name:
                                Sort(orderByName);
                                break;
                            case R.id.sort_by_time:
                                Sort(orderByTime);
                                break;
                            default:
                                throw new IllegalStateException("Unexpected value: " + item.getItemId());
                        }
                        return true;
                    }
                });
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter.getFilter().filter(query);//load kết quả khi ấn ic tìm kiếm
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);//load kết quả khi gõ
                return false;
            }
        });
    }

    //reload view khi chọn kiểu sort khác
    private void Sort(String order) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        customerlist.clear();
        customerlist.addAll(helper.getAllCustomers(order));
        adapter.notifyDataSetChanged();
        editor.putString("sort_type", order).apply();//lưu vào sharedpreferences
        searchView.onActionViewCollapsed();//đóng khi reload view
    }

    @Override
    public void OnItemClicked(int position) {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
        Bundle args = new Bundle();
        args.putLong("ID", customerlist.get(position).getID());

        Navigation.findNavController(getActivity(), R.id.nav_host_fragment)
                .navigate(R.id.action_nav_home_to_nav_history, args);
    }

    @Override
    public void PopupMenuClick(PopupMenu popupMenu, final int position) {
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menuView_customer:
                        OnItemClicked(position);
                        break;
                    case R.id.menuEdit_customer:
                        //Start dialog to edit customer info
                        //send data to trigger the edit dialog and customer's id
                        Bundle args = new Bundle();
                        args.putString("action", "edit");
                        args.putLong("ID", customerlist.get(position).getID());
                        Navigation.findNavController(getActivity(), R.id.nav_host_fragment)
                                .navigate(R.id.action_nav_home_to_addCustomerDialog, args);
                        break;
                    case R.id.menuDel_customer:
                        //delete from db
                        helper.deleteCustomer(customerlist.get(position));
                        Snackbar.make(getActivity().findViewById(android.R.id.content), "Thông tin khách hàng đã được xóa!", Snackbar.LENGTH_SHORT)
                                .show();
                        //reload recycle view and data set
                        recyclerView_customer.removeViewAt(position);
                        adapter.search.remove(position);
                        if (!searchView.isIconified()) {
                            //trường hợp có dùng search view
                            searchView.onActionViewCollapsed();//đóng khung tìm kiếm và xóa tìm kiếm
                            customerlist.addAll(adapter.search);
                        } else {
                            customerlist.remove(position);
                        }
                        adapter.notifyItemRemoved(position);
                        adapter.notifyDataSetChanged();

                        if (customerlist.isEmpty()) {
                            textView_notify_empty_customer.setVisibility(View.VISIBLE);
                        } else {
                            textView_notify_empty_customer.setVisibility(View.GONE);
                        }
                        break;
                    default:
                        throw new IllegalStateException("Unexpected value: " + item.getItemId());
                }
                return true;
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        HideKeyboard();
    }

    private void HideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
    }
}