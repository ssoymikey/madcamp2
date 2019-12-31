package com.example.project1_test3.Fragment;

import android.Manifest;
import android.annotation.SuppressLint;
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
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
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
import android.widget.Button;
import android.widget.Gallery;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.example.project1_test3.R;
import com.github.chrisbanes.photoview.PhotoView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ImageFragment extends Fragment {

    private GridView gridView;
    private String basePath;
    private String mBasePath;
    private MyAdapter adapter;
    private String[] mImgs;

    private String TAG = "Gallery : ";
    private View v;

    private ViewPager viewPager;
    private SliderAdapter sliderAdapter;

    private Dialog dialog;
    private ImageButton btn;
    private String currentPhotoPath;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_image, container, false);

        // 사진 찍는 함수
        takePicture();
        // 갤러리 켜는 함수
        startGallery();

        return v;
    }

    public void takePicture() {
        btn = (ImageButton) v.findViewById(R.id.camera_btn);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                if(intent.resolveActivity(v.getContext().getPackageManager()) != null) {
                    // 찍은것 저장할 파일 만들기
                    File photoFile = null;

                    @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd_HHmmss");
                    Date currentTime = new Date();
                    String dateString = formatter.format(currentTime);
                    String filename = dateString + ".jpg";

                    //File file = new File(Environment.getExternalStorageDirectory(), filename);
                    mBasePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)+File.separator+"Camera";
                    //System.out.println("path : "+mBasePath);

                    try {
                        photoFile = new File(mBasePath, filename);
                        if (photoFile.createNewFile())
                            Log.d("save", "파일 생성 성공");
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }

                    // 카메라 Activity 불러오기
                    if(photoFile != null) {
                        currentPhotoPath = photoFile.getAbsolutePath();
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, currentPhotoPath);
                        startActivityForResult(intent, 1);
                    }
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        System.out.println("requestCode : "+requestCode+" resultCode : "+resultCode);
        // Activity가 끝나면 저장
        if(requestCode == 1 && resultCode != 0) {
            // 갤러리에 변경을 알려줌
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                // 안드로이드 버전이 Kitkat 이상 일때
                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                File file = new File(currentPhotoPath);
                Uri contentUri = Uri.fromFile(file);
                mediaScanIntent.setData(contentUri);
                v.getContext().sendBroadcast(mediaScanIntent);
            } else {
                //context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://" + Environment.getExternalStorageDirectory())));
                v.getContext().sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://" + mBasePath)));
            }
            Toast.makeText(v.getContext().getApplicationContext(), "저장완료", Toast.LENGTH_LONG).show();
        }

        // 왠진 모르겠는데 이거 해야 깨진 사진이 하나 더 생기는 오류가 안 생김
        File file = new File(currentPhotoPath);
        file.delete();

        if(requestCode == 1 && resultCode != 0) {
            // 갤러리 새로고침
            startGallery();
        }
    }

    public void startGallery() {
        // 카메라로 찍은 사진 있는 폴더에 액세스
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

        // 카메라 폴더 안에 있는 jpg 파일 다 불러와서 mImgs에 넣기
        mImgs = mediaStorageDir.list(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return (name.endsWith(".jpg"));
            }
        });
//        for(int i=0;i<mImgs.length;i++) {
//            System.out.println(mImgs[i]);
//        }

        // 이미지가 없을때 없다는거 띄우기
        if (mImgs == null) {
            TextView noimage = v.findViewById(R.id.no_image);
            noimage.setVisibility(View.VISIBLE);
        }

        // gridView로 이미지 정렬
        adapter = new MyAdapter(v.getContext(), R.layout.row, basePath, mImgs);
        gridView = (GridView) v.findViewById(R.id.gridview);
        gridView.setAdapter(adapter);

        // gridView에 있는 이미지 눌렀을 때 확대해서 보여주기. dialog 팝업 이용
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(final AdapterView<?> parent, View view, int position, long id) { // 선택되었을 때 콜백메서드
                dialog = new Dialog(v.getContext(), android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
                //dialog.setContentView(R.layout.activity_image_popup);
                dialog.setContentView(R.layout.slider);

//                ImageView iv = (ImageView) dialog.findViewById(R.id.imageView);
//                Glide.with(v.getContext()).load(adapter.getFilePath(position)).placeholder(R.drawable.loading_image).dontAnimate().into(iv);

                // 팝업으로 viewPager를 띄우기 때문에 좌우 스크롤 가능
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
            iv = (ImageView) convertView.findViewById(R.id.imageView);
        } else {
            iv = (ImageView) convertView.findViewById(R.id.imageView);
        }

        // Glide 라이브러리 너무 좋음. 이미지 잘 불러옴. 썸네일 불러오기
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

        // ViewPager 안에 PhotoView 있음. 사진 확대를 위해.
        PhotoView iv = (PhotoView) v.findViewById(R.id.photoView);
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