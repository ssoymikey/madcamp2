package com.example.project2.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;
import com.example.project2.R;
import com.example.project2.kicycle_Activity;


public class MemoFragment extends Fragment {

    View v;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_memo, container, false);
        Button button = (Button) v.findViewById(R.id.Kicycle);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), kicycle_Activity.class);
                startActivity(intent);
            }
        });


        return v;
    }
}