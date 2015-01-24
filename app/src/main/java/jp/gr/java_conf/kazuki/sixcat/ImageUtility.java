package jp.gr.java_conf.kazuki.sixcat;

import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import android.widget.ImageView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by kazuki on 2015/01/24.
 */
public class ImageUtility {

    static int maxSize = 100;
    public static void loadImage(ContentResolver resolver, ImageView imageView, File srcFile) {
        InputStream inputStream = null;
        BitmapFactory.Options imageOptions = new BitmapFactory.Options();
        try {
            inputStream = resolver.openInputStream(Uri.fromFile(srcFile));

// 画像サイズ情報を取得する
            imageOptions.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(inputStream, null, imageOptions);
            Log.v("image", "Original Image Size: " + imageOptions.outWidth + " x " + imageOptions.outHeight);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

// もし、画像が大きかったら縮小して読み込む
//  今回はimageSizeMaxの大きさに合わせる
        Bitmap bitmap = null;
        inputStream = null;
        try {
            inputStream = resolver.openInputStream(Uri.fromFile(srcFile));

            float imageScaleWidth = (float)imageOptions.outWidth / maxSize;
            float imageScaleHeight = (float)imageOptions.outHeight / maxSize;

            Log.d("image size", "" + imageOptions.outWidth + " / "+ maxSize + " = " + imageScaleWidth);

            // もしも、縮小できるサイズならば、縮小して読み込む
            if (imageScaleWidth > 2 && imageScaleHeight > 2) {
                BitmapFactory.Options imageOptions2 = new BitmapFactory.Options();

                // 縦横、小さい方に縮小するスケールを合わせる
                int imageScale = (int)Math.floor((imageScaleWidth > imageScaleHeight ? imageScaleHeight : imageScaleWidth));

                // inSampleSizeには2のべき上が入るべきなので、imageScaleに最も近く、かつそれ以下の2のべき上の数を探す
                for (int i = 2; i <= imageScale; i *= 2) {
                    imageOptions2.inSampleSize = i;
                }

                bitmap = BitmapFactory.decodeStream(inputStream, null, imageOptions2);
                Log.v("image", "Sample Size: 1/" + imageOptions2.inSampleSize);
            } else {
                bitmap = BitmapFactory.decodeStream(inputStream);
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        if ( bitmap != null) {
            imageView.setImageBitmap(bitmap);
        }
    }
}