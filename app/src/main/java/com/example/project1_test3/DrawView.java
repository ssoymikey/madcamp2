package com.example.project1_test3;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.ColorInt;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class DrawView extends View  {

    private static final float DEFAULT_STROKE_WIDTH = 3;
    private static final int DEFAULT_COLOR = Color.BLACK;
    private static final float TOUCH_TOLERANCE = 4;

    private Canvas mCanvas;
    private Paint mPaint;
    private Path mPath = new Path();
    private Bitmap mBitmap;
    private float mX, mY;

    private ArrayList<Path> paths = new ArrayList<>();
    private ArrayList<Path> undonePaths = new ArrayList<>();

    private ArrayList<Point> arrP = new ArrayList<>();
    private ArrayList<Point> undoArrP = new ArrayList<>();
    int mColor;
    float mWidth;

    public DrawView(Context context) {
        super(context);
        init();
    }

    public DrawView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();

    }

    public DrawView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(DEFAULT_COLOR);
        mColor = DEFAULT_COLOR;
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(DEFAULT_STROKE_WIDTH);
        mWidth = DEFAULT_STROKE_WIDTH;
        mCanvas = new Canvas();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        Bitmap img = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas();
        canvas.setBitmap(img);
        canvas.drawColor(Color.WHITE);

        mBitmap = img;
        mCanvas = canvas;
    }


    @Override
    protected void onDraw(Canvas canvas) {
        setColor(mColor, mWidth);

        if (mBitmap != null) {
            canvas.drawBitmap(mBitmap, 0, 0, null);
        }

        for (Point p : arrP){
            canvas.drawPath(p.path, p.paint);
        }
        canvas.drawPath(mPath, mPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        float x = event.getX();
        float y = event.getY();
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                undoArrP.clear();
                //undonePaths.clear();
                mPath.reset();
                mPath.moveTo(x, y);
                mX = x;
                mY = y;
                invalidate();
                break;

            case MotionEvent.ACTION_MOVE:
                float dx = Math.abs(x - mX);
                float dy = Math.abs(y - mY);
                if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
                    mPath.quadTo(mX, mY, (x + mX)/2, (y + mY)/2);
                    mX = x;
                    mY = y;
                }
                invalidate();
                break;

            case MotionEvent.ACTION_UP:
                mPath.lineTo(mX, mY);
                mCanvas.drawPath(mPath, mPaint);
                arrP.add(new Point (mPath, mPaint));
                //Toast.makeText(getContext(), "arrP add: " + arrP.size(), Toast.LENGTH_SHORT).show();
                // paths.add(mPath);
                mPath = new Path();
                invalidate();
                break;
        }
        return true;
    }


    public void onClickUndo () {
        if(arrP.size() > 0) {
            undoArrP.add(arrP.remove(arrP.size()-1));
            invalidate();
            //Toast.makeText(getContext(), "arrP: " + arrP.size() + " undoArrP: " + undoArrP.size(), Toast.LENGTH_SHORT).show();
        } else {
            //Toast.makeText(getContext(), "arrP: " + arrP.size() + " undoArrP: " + undoArrP.size(), Toast.LENGTH_SHORT).show();
        }
        /*
        if (paths.size()>0) {
            undonePaths.add(paths.remove(paths.size()-1));
            invalidate();
        }else{
        }*/
    }

    public void onClickRedo (){
        if(undoArrP.size()>0) {
            arrP.add(undoArrP.remove(undoArrP.size()-1));
            invalidate();
            //Toast.makeText(getContext(), "arrP: " + arrP.size() + " undoArrP: " + undoArrP.size(), Toast.LENGTH_SHORT).show();
        } else {
            //Toast.makeText(getContext(), "arrP: " + arrP.size() + " undoArrP: " + undoArrP.size(), Toast.LENGTH_SHORT).show();
        }
        /*
        if (undonePaths.size()>0){
            paths.add(undonePaths.remove(undonePaths.size()-1));
            invalidate();
        }else {
        }*/
    }

    public void setSize(float width) {
        mWidth = width;
        mPaint.setStrokeWidth(width);
        invalidate();
    }

    //getWidth, getShape 해야함
    public void setColor(int color, float width) {
        init();
        //       mPaint = new Paint();
        mColor = color;
        mWidth = width;
        mPaint.setColor(color);
        //     mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(width);

/*
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);

        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.getStrokeWidth();
  */  }

    public int getColor() {
        return mPaint.getColor();
    }

    public void setWidth(int color, float width) {
        init();
        //       mPaint = new Paint();
        mColor = color;
        mWidth = width;
        mPaint.setColor(color);
        mPaint.setStrokeWidth(width);
        //      mPaint.setStyle(Paint.Style.STROKE);
        /*
        mPaint.getColor();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);*/
    }

    public void reset() {
        arrP.clear();
        undoArrP.clear();
        invalidate();
    }

    public void save(Context context) {
        this.setDrawingCacheEnabled(true);
        //final Bitmap screenshot = this.getDrawingCache();
        final Context mContext = context;

        final Bitmap screenshot = Bitmap.createBitmap(this.getWidth(), this.getHeight(), Bitmap.Config.ARGB_8888);
        //System.out.println("Width : "+this.getWidth() + " Height : "+this.getHeight());

        Canvas c = new Canvas(screenshot);
        //this.layout(this.getLeft(), this.getTop(), this.getRight(), this.getBottom());
        this.draw(c);

        // 현재 날짜로 파일을 저장하기
        @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        Date currentTime = new Date();
        String dateString = formatter.format(currentTime);
        String filename = dateString + "_drawImage.jpg";
        String mBasePath;
        try {
            //File file = new File(Environment.getExternalStorageDirectory(), filename);
            mBasePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)+ File.separator+"Camera";
            System.out.println("path : "+mBasePath);

            final File file = new File(mBasePath, filename);
            if (file.createNewFile())
                Log.d("save", "파일 생성 성공");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        final OutputStream outStream = new FileOutputStream(file);
                        screenshot.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
                        outStream.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();

            // 갤러리에 변경을 알려줌
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                // 안드로이드 버전이 Kitkat 이상 일때
                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                Uri contentUri = Uri.fromFile(file);
                mediaScanIntent.setData(contentUri);
                mContext.sendBroadcast(mediaScanIntent);
            } else {
                //context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://" + Environment.getExternalStorageDirectory())));
                mContext.sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://" + mBasePath)));
            }

            Toast.makeText(mContext.getApplicationContext(), "저장완료", Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.setDrawingCacheEnabled(false);
    }

}
