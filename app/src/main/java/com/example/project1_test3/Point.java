package com.example.project1_test3;

import android.graphics.Paint;
import android.graphics.Path;

public class Point {
    float x, y;
    int color;
    int shape;
    int size;
    Path path;
    Paint paint;

    public Point() {}

    public Point(Path path, Paint paint) {
        this.path = path;
        this.paint = paint;
    }

    public Path getPath() {
        return path;
    }

    public void setPath(Path path) {
        this.path = path;
    }

    public Paint getPaint() {
        return paint;
    }

    public void setPaint(Paint paint) {
        this.paint = paint;
    }
}
