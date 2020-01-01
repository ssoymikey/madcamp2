package com.example.project1_test3;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.Toast;

public class ShapeSetting extends Dialog{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shape_setting);

        Button btnCircle = findViewById(R.id.btnCircle);
        Button btnRect = findViewById(R.id.btnRect);

        btnCircle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "원", Toast.LENGTH_SHORT).show();
                dismiss();
            }
        });

        btnRect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "사각형", Toast.LENGTH_SHORT).show();
                dismiss();
            }
        });
    }

    public ShapeSetting(Context context) {
        super(context);
    }

}
