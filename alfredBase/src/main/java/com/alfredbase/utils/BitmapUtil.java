package com.alfredbase.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.view.View;

public class BitmapUtil {
	public static Bitmap convertViewToBitmap(View view) {
		Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(),
				Bitmap.Config.RGB_565);
		view.draw(new Canvas(bitmap));

		return bitmap;
	}
    public static Bitmap getResizedBitmap(Bitmap bm, int newHeight, int newWidth) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleWidth);

        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);
        return resizedBitmap;
    }

    public static Bitmap rotate(int rotate, float scale, Bitmap img){
        Matrix matrix = new Matrix();
        matrix.postRotate(rotate);
        matrix.postScale(scale, scale);
        int width = img.getWidth();
        int height = img.getHeight();
        img = Bitmap.createBitmap(img, 0, 0, width, height, matrix, true);
        return img;
    }

    public static Bitmap getResizedBitmap(Bitmap bigimage, float scale){
        // 获取这个图片的宽和高
        int width = bigimage.getWidth();
        int height = bigimage.getHeight();
        // 创建操作图片用的matrix对象
        Matrix matrix = new Matrix();
        // 缩放图片动作
        matrix.postScale(scale, scale);
        Bitmap bitmap = Bitmap.createBitmap(bigimage, 0, 0, width, height,matrix, true);
        return bitmap;
    }


    public static Bitmap getTableBitmap(int rotate, int scale, Bitmap img){
        Matrix matrix = new Matrix();
        if(rotate != 0)
            matrix.postRotate(rotate);

        switch (scale){
            case 1:
                matrix.postScale((float)(2.0/3), (float)(2.0/3));
                break;
            case 2:
                matrix.postScale((float)(5.0/6), (float)(5.0/6));
                break;
            case 3:
                matrix.postScale(1, 1);
                break;
        }
        int width = img.getWidth();
        int height = img.getHeight();
        img = Bitmap.createBitmap(img, 0, 0, width, height, matrix, true);
        return img;
    }
}
