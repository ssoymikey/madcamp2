package com.example.project2.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import com.example.project2.R;
import com.example.project2.SetContactsAsyncTask;
import com.example.project2.kicycle_Activity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;


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
                String UserPassword = login_password.getText().toString();
                String UserPhone = login_password.getText().toString();


                //send to server (in case create)
                JSONObject accountinfo = new JSONObject();
                try {
                    accountinfo.put("ID", UserID);
                    accountinfo.put("phone", UserPhone);
                    accountinfo.put("password", UserPassword);
                    accountinfo.put("money", "500");

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                // send to server
                String json = accountinfo.toString();
                SetContactsAsyncTask task = new SetContactsAsyncTask("POST", "http://192.249.19.251:980/account");
                try {
                    boolean success = task.execute(json).get();
                    Toast.makeText(v.getContext(), "PUT to MongoDB!! : "+success, Toast.LENGTH_SHORT).show();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
                intent.putExtra("UserID", UserID);
                startActivity(intent);
            }
        });


        return v;
    }
}