package com.example.canlua;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.android.material.snackbar.Snackbar;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import static android.content.Context.INPUT_METHOD_SERVICE;

public class InputRiceWeightFragment extends Fragment {
    GridView gridView_weight;
    EditText editText_weight;
    TextView textView_weight_preview;
    Button button_add_weight, btn_save;
    TextView textView_info_sumOfBag;
    TextView textView_info_sumOfWeight;
    TextView textView_money;
    List<Double> listWeight;
    GridViewWeightAdapter adapter;
    DatabaseHelper helper;
    double money = 0, sumOfWeight = 0;
    int sumOfBag = 0;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_input_rice_weight, container, false);
        gridView_weight = view.findViewById(R.id.gridview_input_weight);
        editText_weight = view.findViewById(R.id.editText_input_weight);
        textView_weight_preview = view.findViewById(R.id.textView_input_weight_preview);
        button_add_weight = view.findViewById(R.id.btn_input_weight);
        btn_save = view.findViewById(R.id.btn_save);
        textView_info_sumOfBag = view.findViewById(R.id.info_lb_sum_bag);
        textView_info_sumOfWeight = view.findViewById(R.id.info_lb_sum_weight);
        textView_money = view.findViewById(R.id.money_tv_data);

        listWeight = new ArrayList<>();
        helper = new DatabaseHelper(getContext());
        adapter = new GridViewWeightAdapter(getContext(), R.layout.gridview_input_weight_item, listWeight);
        gridView_weight.setAdapter(adapter);
        return view;
    }

    private void HideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        HideKeyboard();
        gridView_weight.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                final EditText editText;
                final TextView textView_preview;
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.AlertDialogTheme);
                LayoutInflater inflater = getActivity().getLayoutInflater();
                View v = inflater.inflate(R.layout.dialog_weight_editor, null);

                editText = v.findViewById(R.id.edit_weight_dialog_item);
                textView_preview = v.findViewById(R.id.textview_weight_preview_dialog_item);

                builder.setView(v)
                        .setTitle("Nhập số ký bao lúa")
                        .setNegativeButton("hủy", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String newWeight = textView_preview.getText().toString();
                                listWeight.set(position, Double.parseDouble(newWeight));
                                adapter.notifyDataSetChanged();
                                //tính lại tổng số cân nặng
                                AddWeight();
                                AddMoney();

                            }
                        });

                editText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        editText.removeTextChangedListener(this);
                        String weight = editText.getText().toString();

                        //kiểm tra có nhập số 0 đầu tiên không
                        //nếu có thì xóa đi vd: 025 là lỗi
                        if (weight.length() == 1) {
                            int number = Integer.parseInt(editText.getText().toString());
                            if (number == 0)
                                editText.setText("");
                        }

                        //nếu có 2 chữ số thì hiện textview preview
                        if (weight.length() > 1) {
                            textView_preview.setText(AddFloatingPoint(weight));
                        } else
                            textView_preview.setText("");
                        editText.addTextChangedListener(this);
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });
                builder.show();
            }
        });

        editText_weight.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                editText_weight.removeTextChangedListener(this);

                String weight = editText_weight.getText().toString();
                //kiểm tra có nhập số 0 đầu tiên không
                //nếu có thì xóa đi vd: 025 là lỗi
                if (weight.length() == 1)
                    IsZero();

                //nếu có 2 chữ số thì hiện textview preview
                if (weight.length() > 1) {
                    textView_weight_preview.setText(AddFloatingPoint(weight));
                    textView_weight_preview.setVisibility(View.VISIBLE);
                } else
                    textView_weight_preview.setVisibility(View.INVISIBLE);
                editText_weight.addTextChangedListener(this);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        //nhấn btn thì thêm vào gridview và thay đổi tổng số ký và số bao lúa
        button_add_weight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (textView_weight_preview.getVisibility() == View.VISIBLE) {//check người dùng có nhập dữ liệu rồi
                    listWeight.add(Double.parseDouble(textView_weight_preview.getText().toString()));
                    adapter.notifyDataSetChanged();
                    editText_weight.getText().clear();//reset lại về trống
                    //chỉnh thông tin info
                    AddWeight();
                    AddNumOfBag();
                    AddMoney();
                }
            }
        });

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                History history = new History();
                history.setTenGiongLua(getArguments().getString("tengiong"));
                history.setDonGia(getArguments().getInt("dongia"));
                history.setBaoBi(getArguments().getInt("trubi"));
                history.setTienCoc(getArguments().getInt("tiencoc"));
                history.setID(getArguments().getLong("id"));
                history.setTongSoKG(Double.parseDouble(textView_info_sumOfWeight.getText().toString()));
                history.setSoBao(sumOfBag);
                history.setThanhTien(money);
                helper.addHistory(history);
                Snackbar.make(getActivity().findViewById(android.R.id.content), "Đã thêm lịch sử đơn hàng mới!", Snackbar.LENGTH_SHORT)
                        .show();
                //quay về giao diện history
                Bundle args = new Bundle();
                args.putLong("ID", getArguments().getLong("id"));
                Navigation.findNavController(getActivity(), R.id.nav_host_fragment)
                        .navigate(R.id.action_inputRiceWeightFragment_to_historyFragment, args);
                HideKeyboard();
            }
        });

        //hide keyboard
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                HideKeyboard();
                return true;
            }
        });
    }

    //lấy weight trong mảng cộng lại
    private void AddWeight() {
        sumOfWeight = 0;
        if (listWeight.size() != 0) {
            for (int i = 0; i < listWeight.size(); i++) {
                sumOfWeight += listWeight.get(i);
            }
        }
        //làm tròn vì double có lỗi hiển thị => kết quả đúng
        DecimalFormat decimalFormat = new DecimalFormat("0.0");
        decimalFormat.setRoundingMode(RoundingMode.UP);

        textView_info_sumOfWeight.setText(decimalFormat.format(sumOfWeight));
    }

    private void AddNumOfBag() {
        sumOfBag = Math.max(listWeight.size(), 0);
        textView_info_sumOfBag.setText("" + sumOfBag);
    }

    //kiểm tra nhập số 0 đầu tiên hay không vd 025 là sai
    private void IsZero() {
        int number = Integer.parseInt(editText_weight.getText().toString());
        if (number == 0)
            editText_weight.setText("");
    }

    private void AddMoney() {
        money = getArguments().getInt("dongia") * (sumOfWeight - ((1.0 / getArguments().getInt("trubi")) * sumOfBag));

        //làm tròn vì double có lỗi hiển thị => kết quả đúng
        DecimalFormat decimalFormat = new DecimalFormat("###,###,###");
        decimalFormat.setRoundingMode(RoundingMode.UP);
        textView_money.setText(decimalFormat.format(money));
    }

    //thêm dấu chấm động vào số
    //người dùng chỉ cần nhập số cho nhanh
    private String AddFloatingPoint(String input) {
        if (input.length() == 3) {//35.5 hay 35.0
            return input.substring(0, 2) + '.' + input.substring(2);
        } else
            return input + ".0";
    }

    @Override
    public void onPause() {
        super.onPause();
        HideKeyboard();
    }
}