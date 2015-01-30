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
 * ImageUtility
 * 画像の取得に関するUtilityクラス
 * Created by kazuki on 2015/01/24.
 */
public class ImageUtility {

    static int maxSize = 100;


    public static Bitmap loadImage(ContentResolver resolver, ImageView imageView, Uri srcUri) {
        InputStream inputStream = null;
        BitmapFactory.Options imageOptions = new BitmapFactory.Options();
        try {
            inputStream = resolver.openInputStream(srcUri);

// 画像サイズ情報を取得する
            imageOptions.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(inputStream, null, imageOptions);

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
            inputStream = resolver.openInputStream(srcUri);

            float imageScaleWidth = (float)imageOptions.outWidth / maxSize;
            float imageScaleHeight = (float)imageOptions.outHeight / maxSize;

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

        if ( bitmap != null && imageView!= null ) {
            imageView.setImageBitmap(bitmap);
        }

        return bitmap;
    }
}