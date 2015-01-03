package jp.gr.java_conf.kazuki.sixcat;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.provider.MediaStore.Images.Media;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by kazuki on 2015/01/03.
 */
public abstract class AbstractProfileEditFragment extends Fragment {

    final int REQUEST_ACTION_PICK = 1;

    public static final int TAG_IMG_FILE_NAME = 1;

    public static final String imageDirectoryName = AbstractProfileEditFragment.class.getPackage().getName() + "/image/";

    private String imageFileName;


    public AbstractProfileEditFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_profile_edit, container, false);


        //img_profile_edit_portrait
        final ImageView portrait = (ImageView)rootView.findViewById(R.id.img_profile_edit_portrait);
        if (portrait != null) {
            portrait.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    //実行フロー
//                        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    //これだとギャラリー専門が開きます。
                    Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.setType("image/*");
                    //createChooserを使うと選択ダイアログのタイトルを変更する事ができます。
                    startActivityForResult(Intent.createChooser(intent, "select"), REQUEST_ACTION_PICK);
                    //デフォルトで「アプリ選択」と出ます。
                    //startActivityForResult(intent, REQUEST_ACTION_PICK);
                }
            });
        }

        return rootView;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("debug_kazuki", "onActivityResult called");
        if(resultCode == getActivity().RESULT_OK){
            if(requestCode == REQUEST_ACTION_PICK){
                try {
                    InputStream iStream = getActivity().getApplicationContext().getContentResolver().openInputStream(data.getData());
                    Bitmap bm = BitmapFactory.decodeStream(iStream);

                    // 新しいフォルダにギャラリーから選ん画像を保存
                    createFolderSaveImage(bm, getImageFileName());
                    iStream.close();

                    ImageView imgView = (ImageView) getActivity().findViewById(R.id.img_profile_edit_portrait);
                    imgView.setImageBitmap(bm);

                    EditText editText = (EditText)getActivity().findViewById(R.id.txt_profile_edit_portrait);
                    editText.setText(imageFileName);

                }catch (IOException e) {}
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 新規フォルダを作成し、画像ファイルを保存する
     * 参考：http://d.hatena.ne.jp/acid-panda/20110420/1303316310
     *
     * @param imageToSave
     * @param fileName
     */
    private void createFolderSaveImage(Bitmap imageToSave, String fileName) {

        Log.d("debug_kazuki", "createFolderSaveImage called");
        // 新しいフォルダへのパス
        String folderPath = Environment.getExternalStorageDirectory()
                + "/" + imageDirectoryName;

        Log.d("debug_kazuki", folderPath);
        File folder = new File(folderPath);
        if (!folder.exists()) {
            Log.d("debug_kazuki", "mkdirs calling");
            folder.mkdirs();
        }

        if (imageFileName != null) {
            //前回選択した画像は不要なので削除
            File old_file = new File(folder,imageFileName );
            if (old_file.exists()) {
                old_file.delete();
            }
        }
        File file = new File(folder, fileName);
        if (file.exists()) {
            file.delete();
        }

        try {
            FileOutputStream out = new FileOutputStream(file);

            Log.d("debug_kazuki", "save calling");
            imageToSave.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
            imageFileName = fileName;
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            // これをしないと、新規フォルダは端末をシャットダウンするまで更新されない
            Log.d("debug_kazuki", "showFolder calling");
            //showFolder(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * ContentProviderに新しいイメージファイルが作られたことを通知する
     * 参考：http://d.hatena.ne.jp/acid-panda/20110420/1303316310
     * @param path
     * @throws Exception
     */
    private void showFolder(File path) throws Exception {
        try {
            ContentValues values = new ContentValues();
            ContentResolver contentResolver = getActivity().getApplicationContext()
                    .getContentResolver();
            values.put(Images.Media.MIME_TYPE, "image/jpeg");
            values.put(Images.Media.DATE_MODIFIED,
                    System.currentTimeMillis() / 1000);
            values.put(Images.Media.SIZE, path.length());
            values.put(Images.Media.TITLE, path.getName());
            values.put(Images.Media.DATA, path.getPath());
            contentResolver.insert(Media.EXTERNAL_CONTENT_URI, values);
        } catch (Exception e) {
            throw e;
        }
    }

    public static String getImageFileName(){
        return "img_" + System.currentTimeMillis() + ".jpg";
    }

}
