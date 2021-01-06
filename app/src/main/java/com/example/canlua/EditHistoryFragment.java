package com.example.canlua;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import java.math.RoundingMode;
import java.text.DecimalFormat;


public class EditHistoryFragment extends Fragment {

    EditText editText_tenLua, editText_dongia, editText_trubi, editText_tiencoc;
    TextView textView_tongbao, textView_tongkg, textView_thanhtien;
    Button btn_update;
    History history;
    DatabaseHelper helper;
    ConstraintLayout layout;
    DecimalFormat moneyFormat, decimalFormat;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_edit_history, container, false);
        editText_dongia = view.findViewById(R.id.edittext_dongia);
        editText_tenLua = view.findViewById(R.id.edittext_tenLua);
        editText_trubi = view.findViewById(R.id.edittext_trubi);
        editText_tiencoc = view.findViewById(R.id.edittext_tiencoc);
        textView_tongbao = view.findViewById(R.id.textView_info_tongbaolua);
        textView_tongkg = view.findViewById(R.id.textView_info_tongkylua);
        textView_thanhtien = view.findViewById(R.id.textView_info_thanhtien);
        btn_update = view.findViewById(R.id.btn_update);
        layout = view.findViewById(R.id.no_edit_data_section);
        moneyFormat = new DecimalFormat("###,###,###");
        moneyFormat.setRoundingMode(RoundingMode.UP);
        decimalFormat = new DecimalFormat("0.0");
        decimalFormat.setRoundingMode(RoundingMode.UP);

        helper = new DatabaseHelper(getContext());
        history = new History();
        history = helper.getHistory(getArguments().getString("timestamp"));

        editText_tenLua.setText(history.getTenGiongLua());
        editText_dongia.setText(String.format("%s", history.getDonGia()));
        editText_trubi.setText(String.format("%s", history.getBaoBi()));
        editText_tiencoc.setText(String.format("%s", history.getTienCoc()));
        textView_tongbao.setText(String.format("%s", history.getSoBao()));
        textView_tongkg.setText(decimalFormat.format(history.getTongSoKG()));
        textView_thanhtien.setText(moneyFormat.format(history.getThanhTien()));
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layout.setVisibility(View.VISIBLE);
                history.setTenGiongLua(editText_tenLua.getText().toString());
                history.setDonGia(Integer.parseInt(editText_dongia.getText().toString()));
                history.setBaoBi(Integer.parseInt(editText_trubi.getText().toString()));
                history.setTienCoc(Integer.parseInt(editText_tiencoc.getText().toString()));
                double money = history.getDonGia() * (history.getTongSoKG() - ((1.0 / history.getBaoBi() * history.getSoBao())));
                history.setThanhTien(money);
                helper.updateHistory(history);

                textView_tongkg.setText(decimalFormat.format(history.getTongSoKG()));
                textView_thanhtien.setText(moneyFormat.format(money));
            }
        });
    }
}