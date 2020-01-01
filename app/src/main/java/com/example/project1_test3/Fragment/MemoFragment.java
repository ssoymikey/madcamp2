package com.example.project1_test3.Fragment;

import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.project1_test3.DrawView;
import com.example.project1_test3.PenSetting;
import com.example.project1_test3.R;
import com.example.project1_test3.ShapeSetting;

import yuku.ambilwarna.AmbilWarnaDialog;

public class MemoFragment extends Fragment implements View.OnClickListener {

    private DrawView drawView;
    Button btnColor;
    ImageButton btnShape;
    Button btnSize;
    int tColor, n = 0;
    int color =0;
    float width = 3.0f;
    View v;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_memo, container, false);

        drawView = v.findViewById(R.id.sub_layout);
        tColor = ContextCompat.getColor(v.getContext(), R.color.colorAccent);

        btnColor = v.findViewById(R.id.btnColor);
        btnSize = v.findViewById(R.id.btnSize);
        btnShape = v.findViewById(R.id.btnShape);
        ImageButton btnUndo = v.findViewById(R.id.btnUndo);
        ImageButton btnRedo = v.findViewById(R.id.btnRedo);
        ImageButton btnErase = v.findViewById(R.id.btnErase);
        ImageButton btnSave = v.findViewById(R.id.btnSave);

        btnColor.setOnClickListener(this);
        btnSize.setOnClickListener(this);
        btnShape.setOnClickListener(this);
        btnUndo.setOnClickListener(this);
        btnRedo.setOnClickListener(this);
        btnErase.setOnClickListener(this);
        btnSave.setOnClickListener(this);

        return v;
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.btnColor:
                //               n = 2;
                openColorPicker();
                break;
            case R.id.btnSize:
                openSizeChange();
                break;
            case R.id.btnShape:
                openShape();
                break;
            case R.id.btnUndo:
                drawView.onClickUndo();
                break;
            case R.id.btnRedo:
                drawView.onClickRedo();
            case R.id.btnErase:
                drawView.reset();
                //두께 두껍게..?
                break;
            case R.id.btnSave:
                drawView.save(view.getContext());
                break;

        }
    }

    public void openColorPicker() {
        AmbilWarnaDialog colorPicker = new AmbilWarnaDialog(v.getContext(), tColor, new AmbilWarnaDialog.OnAmbilWarnaListener() {
            @Override
            public void onCancel(AmbilWarnaDialog dialog) {

            }
            @Override
            public void onOk(AmbilWarnaDialog dialog, int color) {
                //              tColor = color;
//                if (n == 1) {
                //paint color 바꾸기
                //              }
                //             else if (n == 2) {
                //drawView.setBackgroundColor(tColor);

                Drawable roundDrawable = getResources().getDrawable(R.drawable.colorpick_btn);
                roundDrawable.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
                if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    btnColor.setBackgroundDrawable(roundDrawable);
                } else {
                    btnColor.setBackground(roundDrawable);
                }

                //btnColor.setBackgroundColor(color);
                drawView.setColor(color, getWidth());

            }
        });
        colorPicker.show();
    }

    public void openSizeChange() {
        final PenSetting penSetting = new PenSetting(v.getContext(), new PenSetting.PenSettingEventListener() {
            public void penSettingEvent(float value) {
                String btnText = (int)value + "";
                btnSize.setText(btnText);
                drawView.setWidth(getColor(), value);
            }
        });

        penSetting.show();
    }

    public void openShape() {
        ShapeSetting shapeSetting = new ShapeSetting(v.getContext());
        shapeSetting.show();
    }

    public int getColor() {
        //return ((ColorDrawable) btnColor.getBackground()).getColor();
        return drawView.getColor();
    }

    public float getWidth() {
        String btnText;
        final StringBuilder sb = new StringBuilder(btnSize.getText().length());
        sb.append(btnSize.getText());
        btnText = sb.toString();
        return Float.valueOf(btnText);
    }


    /*
    메뉴에 쓰였던거 혹시 몰라서 남겨둠
    public void openShape() {
        PopupMenu popupMenu = new PopupMenu(this, drawView);
        getMenuInflater().inflate(R.menu.shape_setting, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Toast.makeText(MainActivity.this, "popup" + item.getTitle(), Toast.LENGTH_SHORT).show();
                return false;
            }
        });
        popupMenu.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.shape_setting, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == 1) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
*/
}