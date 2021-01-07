package com.example.canlua;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import static android.content.Context.INPUT_METHOD_SERVICE;

public class InputRiceInfoFragment extends Fragment {

    EditText editText_tengiong;
    EditText editText_dongia;
    EditText editText_trubi;
    EditText editText_tiencoc;
    Button btn_to_input_weight;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_input_rice_info, container, false);
        editText_tengiong = view.findViewById(R.id.edittext_tenLua_info);
        editText_dongia = view.findViewById(R.id.edittext_dongia_info);
        editText_trubi = view.findViewById(R.id.edittext_trubi_info);
        editText_tiencoc = view.findViewById(R.id.edittext_tiencoc_info);
        btn_to_input_weight = view.findViewById(R.id.btn_to_input_weight);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //set giá trị sẵn cho khung nhập
        editText_dongia.setText("5000");
        editText_trubi.setText("8");
        editText_tiencoc.setText("0");

        btn_to_input_weight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateData()) {
                    String tengiong = editText_tengiong.getText().toString().trim();
                    int dongia, tiencoc, trubi;
                    dongia = Integer.parseInt(editText_dongia.getText().toString());
                    trubi = Integer.parseInt(editText_trubi.getText().toString());
                    tiencoc = Integer.parseInt(editText_tiencoc.getText().toString());

                    Bundle args = new Bundle();
                    args.putString("tengiong", tengiong);
                    args.putInt("dongia", dongia);
                    args.putInt("trubi", trubi);
                    args.putInt("tiencoc", tiencoc);
                    args.putLong("id", getArguments().getLong("id"));
                    Navigation.findNavController(getActivity(), R.id.nav_host_fragment)
                            .navigate(R.id.action_inputRiceInfoFragment_to_inputRiceWeightFragment, args);
                }
            }
        });
    }

    private boolean validateData() {
        int dongia, trubi;
        boolean data = true;

        if (editText_dongia.getText().toString().length() == 0) {
            editText_dongia.setError("Không được bỏ trống");
            data = false;
        } else {
            dongia = Integer.parseInt(editText_dongia.getText().toString());
            if (dongia == 0) {
                editText_dongia.setError("Giá trị phải lớn hơn 0");
                data = false;
            }
        }

        if (editText_trubi.getText().toString().length() == 0) {
            editText_trubi.setError("Không được bỏ trống");
            data = false;
        } else {
            trubi = Integer.parseInt(editText_trubi.getText().toString());
            if (trubi == 0) {
                editText_trubi.setError("Giá trị phải lớn hơn 0");
                data = false;
            }
        }

        if (editText_tiencoc.getText().toString().length() == 0) {
            editText_tiencoc.setError("Không được bỏ trống");
            data = false;
        }

        return data;
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