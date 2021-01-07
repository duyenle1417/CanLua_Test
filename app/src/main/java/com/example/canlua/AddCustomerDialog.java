package com.example.canlua;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.snackbar.Snackbar;

//TO_DO: change to fragment and change layout of this
public class AddCustomerDialog extends DialogFragment {
    EditText editText_name;
    EditText editText_phone;
    AddCustomerListener listener;
    DatabaseHelper helper;
    AlertDialog.Builder builder;
    LayoutInflater inflater;
    View view;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        builder = new AlertDialog.Builder(getActivity(), R.style.AlertDialogTheme);
        inflater = getActivity().getLayoutInflater();
        view = inflater.inflate(R.layout.dialog_customer_add, null);
        helper = new DatabaseHelper(getContext());

        editText_name = view.findViewById(R.id.edittext_tenKH_dialog);
        editText_phone = view.findViewById(R.id.edittext_sdt_dialog);

        switch (getArguments().getString("action")) {
            case "add":
                AddCustomer();
                break;
            case "edit":
                EditCustomer();
                break;
        }
        return builder.create();
    }

    private void EditCustomer() {
        final Customer customer = helper.getCustomer(getArguments().getLong("ID"));

        final String phone = customer.getSDT(), checkPhone;
        if (phone.equals("(trống)")) {
            editText_phone.setText("");
            checkPhone = "";
        } else {
            editText_phone.setText(phone);
            checkPhone = phone;
        }
        editText_name.setText(customer.getHoTen());

        builder.setView(view)
                .setTitle("Sửa thông tin khách hàng")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String name = editText_name.getText().toString().trim().replace("'", "");
                        String phone = editText_phone.getText().toString().trim();
                        if ((!name.equals(customer.getHoTen()) || !phone.equals(checkPhone))
                                && name.length() > 0) {
                            customer.setHoTen(name);
                            customer.setSDT(phone);
                            helper.updateCustomer(customer);
                            listener.ApplyChange();
                            Snackbar.make(getActivity().findViewById(android.R.id.content), "Thông tin khách hàng đã được cập nhật!", Snackbar.LENGTH_SHORT)
                                    .show();
                        } else {
                            if (name.length() <= 0)
                                Snackbar.make(getActivity().findViewById(android.R.id.content), "ERROR! Không được để trống tên.", Snackbar.LENGTH_SHORT)
                                        .show();
                        }
                    }
                })
                .setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //listener.ApplyChange();
                    }
                })
                .setCancelable(false);
    }

    private void AddCustomer() {
        builder.setView(view)
                .setTitle("Thêm thông tin khách hàng")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String name = editText_name.getText().toString().trim().replace("'", "");
                        String phone = editText_phone.getText().toString().trim();
                        Customer customer = new Customer();
                        customer.setHoTen(name);
                        customer.setSDT(phone);
                        if (name.length() > 0) {
                            //AddCustomer to DB
                            helper.addCustomer(customer);
                            listener.ApplyChange();
                            Snackbar.make(getActivity().findViewById(android.R.id.content), "Đã thêm một khách hàng mới!", Snackbar.LENGTH_SHORT)
                                    .show();
                        } else {
                            Snackbar.make(getActivity().findViewById(android.R.id.content), "Lỗi! Không được để trống tên.", Snackbar.LENGTH_SHORT)
                                    .show();
                        }
                    }
                })
                .setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //listener.ApplyChange();
                    }
                })
                .setCancelable(false);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            // Instantiate the Listener so we can send events to the host
            listener = (AddCustomerListener) context;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(context.toString() + " must implement AddCustomerListener");
        }
    }

    private boolean ValidateName(String name) {
        if (name.length() > 0)
            return true;
        else {
            editText_name.setError("Không được để trống!");
            editText_name.requestFocus();
            return false;
        }
    }

    public interface AddCustomerListener {
        void ApplyChange();//when click ok, navigate to home fragment
    }
}
