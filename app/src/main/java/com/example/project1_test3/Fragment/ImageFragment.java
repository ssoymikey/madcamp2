package com.example.project1_test3.Fragment;

import android.Manifest;
import android.app.ActionBar;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.example.project1_test3.R;

import java.io.File;
import java.io.FilenameFilter;
import java.lang.ref.WeakReference;
import java.lang.reflect.Type;

public class ImageFragment extends Fragment {

    private GridView gridView;
    private String basePath;
    private MyAdapter adapter;
    private String[] mImgs;

    private String TAG = "Gallery : ";
    private View v;

    private ViewPager viewPager;
    private SliderAdapter sliderAdapter;

    private Dialog dialog;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_image, container, false);

        startGallery();
        //firstAction();

        return v;
    }

    public void startGallery() {
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "Camera");
        if (!mediaStorageDir.exists()) {
            Log.d(TAG, "not exist!!");
            if (!mediaStorageDir.mkdirs()) {
                Log.d(TAG, "failed to create directory");
            }
        } else {
            Log.d(TAG, "exist!!");
        }
        basePath = mediaStorageDir.getPath();

        mImgs = mediaStorageDir.list(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return (name.endsWith(".jpg"));
            }
        });
//        for(int i=0;i<mImgs.length;i++) {
//            System.out.println(mImgs[i]);
//        }

        if (mImgs == null) {
            TextView noimage = v.findViewById(R.id.no_image);
            noimage.setVisibility(View.VISIBLE);
        }

        adapter = new MyAdapter(v.getContext(), R.layout.row, basePath, mImgs);
        gridView = (GridView) v.findViewById(R.id.gridview);
        gridView.setAdapter(adapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(final AdapterView<?> parent, View view, int position, long id) { // 선택되었을 때 콜백메서드
                dialog = new Dialog(v.getContext(), android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
                //dialog.setContentView(R.layout.activity_image_popup);
                dialog.setContentView(R.layout.slider);

//                ImageView iv = (ImageView) dialog.findViewById(R.id.imageView);
//                Glide.with(v.getContext()).load(adapter.getFilePath(position)).placeholder(R.drawable.loading_image).dontAnimate().into(iv);

                sliderAdapter = new SliderAdapter(v.getContext(), R.layout.activity_image_popup, basePath, mImgs);
                viewPager = (ViewPager) dialog.findViewById(R.id.view);
                viewPager.setAdapter(sliderAdapter);
                viewPager.setCurrentItem(position);

                dialog.show();
                Window window = dialog.getWindow();
                dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND, WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            }
        });
    }
}

class MyAdapter extends BaseAdapter {
    String mBasePath; // CustomGalleryAdapter를 선언할 때 지정 경로를 받아오기 위한 변수
    Context mContext; // CustomGalleryAdapter를 선언할 때 해당 activity의 context를 받아오기 위한 context 변수
    String[] mImgs; // 위 mBasePath내의 file list를 String 배열로 저장받을 변수
    Bitmap bm; // 지정 경로의 사진을 Bitmap으로 받아오기 위한 변수
    int layout;

    public MyAdapter(Context context, int layout, String basePath, String[] mImgs) {
        this.mContext = context;
        this.mImgs = mImgs;
        this.mBasePath = basePath;
        this.layout = layout;
    }

    @Override
    public int getCount() { // 보여줄 데이터의 총 개수 - 꼭 작성해야 함
        if(mImgs == null)
            return 0;

        return mImgs.length;
    }

    @Override
    public Object getItem(int position) { // 해당행의 데이터- 안해도 됨
        return position;
    }

    @Override
    public long getItemId(int position) { // 해당행의 유니크한 id - 안해도 됨
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ImageView iv;
        if (convertView == null) {
            LayoutInflater inf = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inf.inflate(layout, null);
            iv = (ImageView)convertView.findViewById(R.id.imageView);
        } else {
            iv = (ImageView)convertView.findViewById(R.id.imageView);
        }

        Glide.with(mContext).load(getFilePath(position)).override(300,300).centerCrop().into(iv);

        return convertView;
    }

    public String getFilePath(int position) {
        return mBasePath + File.separator + mImgs[position];
    }
}

class SliderAdapter extends PagerAdapter {

    private LayoutInflater inflater;
    String mBasePath;
    Context mContext;
    String[] mImgs;
    int layout;

    public SliderAdapter(Context context, int layout, String mBasePath, String[] mImgs) {
        this.mContext = context;
        this.mImgs = mImgs;
        this.mBasePath = mBasePath;
        this.layout = layout;
    }

    @Override
    public int getCount() {
        if(mImgs == null)
            return 0;

        return mImgs.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((RelativeLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(layout, container, false);
        v.setTag(position);

        ImageView iv = (ImageView) v.findViewById(R.id.imageView);
        Glide.with(mContext).load(getFilePath(position)).placeholder(R.drawable.loading_image).override(1000, 1000).dontAnimate().into(iv);
        container.addView(v);
        return v;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.invalidate();
    }

    public String getFilePath(int position) {
        return mBasePath + File.separator + mImgs[position];
    }
}