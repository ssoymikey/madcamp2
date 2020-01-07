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

import com.example.project2.Account;
import com.example.project2.GetAccountAsyncTask;
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
        final Button btn_login = (Button) v.findViewById(R.id.login);
        final Button btn_create = (Button) v.findViewById(R.id.create);
        final Button btn_submit = (Button) v.findViewById(R.id.login_submit);
        final EditText login_ID = v.findViewById(R.id.login_name);
        final EditText login_password = v.findViewById(R.id.login_password);
        final EditText login_phone = v.findViewById(R.id.login_phone);


        btn_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login_phone.setVisibility(View.VISIBLE);
            }
        });

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login_phone.setVisibility(View.INVISIBLE);
            }
        });

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), kicycle_Activity.class);
                String UserID = login_ID.getText().toString();
                String UserPassword = login_password.getText().toString();

                if(!UserID.equals("") && !UserPassword.equals("")) {
                    //case create
                    if (login_phone.getVisibility() == View.VISIBLE) {
                        //send to server (in case create)
                        String UserPhone = login_phone.getText().toString();

                        if(!UserPhone.equals("")) {
                            JSONObject accountinfo = new JSONObject();
                            try {
                                accountinfo.put("name", UserID);
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
                                //Toast.makeText(v.getContext(), "PUT to MongoDB!! : " + success, Toast.LENGTH_SHORT).show();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            } catch (ExecutionException e) {
                                e.printStackTrace();
                            }

                            intent.putExtra("UserID", UserID);
                            intent.putExtra("UserPhone", UserPhone);
                            startActivity(intent);
                        }
                    }

                    //case login
                    else {
                        //id 서버한테 보냄
                        //account 받아옴
                        //account 없으면 toast
                        //있는데 비번이 안맞다 토스트
                        //있는데 비번이 맞다 phone ID 풋하고 스타트
                        Account account = new Account();

                        GetAccountAsyncTask accTask = new GetAccountAsyncTask("http://192.249.19.251:980/login");
                        try {
                            account = accTask.execute(UserID).get();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        }

                        if (account.getID() == null) {
                            Toast.makeText(v.getContext(), "해당하는 계정이 없습니다.", Toast.LENGTH_SHORT).show();
                        } else if (!account.getPwd().equals(UserPassword)) {
                            Toast.makeText(v.getContext(), "비밀번호가 틀립니다.", Toast.LENGTH_SHORT).show();
                        } else {
                            intent.putExtra("UserID", UserID);
                            intent.putExtra("UserPhone", account.getPhone());
                            startActivity(intent);
                        }
                    }
                }
            }
        });


        return v;
    }
}