package com.example.project2.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.fragment.app.Fragment;
import com.example.project2.R;
import com.example.project2.kicycle_Activity;


public class MemoFragment extends Fragment {

    View v;
    String UserPassword;
    String UserPhone;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_memo, container, false);
        Button button = (Button) v.findViewById(R.id.Kicycle);
        final Button btn_login = (Button) v.findViewById(R.id.login);
        final Button btn_create = (Button) v.findViewById(R.id.create);
        final Button btn_submit = (Button) v.findViewById(R.id.login_submit);
        final EditText login_ID = v.findViewById(R.id.login_name);
        final EditText login_password = v.findViewById(R.id.login_password);
        final EditText login_phone = v.findViewById(R.id.login_phone);


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), kicycle_Activity.class);
                startActivity(intent);
            }
        });

        btn_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login_phone.setVisibility(View.VISIBLE);
            }
        });

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), kicycle_Activity.class);
                String UserID = login_ID.getText().toString();

                intent.putExtra("UserID", UserID);
                startActivity(intent);
            }
        });


        return v;
    }
}