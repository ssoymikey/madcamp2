package com.example.project1_test3;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class PenSetting extends Dialog {

    private static final int THICKNESS_MAX = 30;
    private static final int THICKNESS_MIN = 3;
    private Button mLeftButton;
    private Button mRightButton;
    private SeekBar mThicknessBar;
    private TextView mCurValue;
    private int num = 3;

    public interface PenSettingEventListener {
        public void penSettingEvent(float value);
    }

    private PenSettingEventListener onPenSettingEventListener;

    public PenSetting(Context context, PenSettingEventListener onPenSettingEventListener) {
        super(context);
        this.onPenSettingEventListener = onPenSettingEventListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pen_setting);

        mCurValue = findViewById(R.id.curValue);
        mThicknessBar = findViewById(R.id.seekBar);
        mLeftButton = findViewById(R.id.btnCancel);
        mRightButton = findViewById(R.id.btnSubmit);

        mThicknessBar.setMax(THICKNESS_MAX);
        mThicknessBar.setMin(THICKNESS_MIN);

        mThicknessBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                num = mThicknessBar.getProgress();
                update();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                num = mThicknessBar.getProgress();

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                num = mThicknessBar.getProgress();
            }
        });

        mLeftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getContext(), "취소",
                //        Toast.LENGTH_SHORT).show();
                dismiss();
            }
        });
        mRightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getContext(), "두께" + mThicknessBar.getProgress() ,
                //        Toast.LENGTH_SHORT).show();
                onPenSettingEventListener.penSettingEvent(mThicknessBar.getProgress());
                // width 값 setting 하기
                dismiss();
            }
        });

    }



    public void update() {
        mCurValue.setText(new StringBuilder().append(num));
    }



}
