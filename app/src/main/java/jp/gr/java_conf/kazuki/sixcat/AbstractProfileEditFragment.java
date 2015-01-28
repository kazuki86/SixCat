package jp.gr.java_conf.kazuki.sixcat;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import jp.gr.java_conf.kazuki.sixcat.data.SixCatSQLiteOpenHelper;

/**
 * AbstractProfileEditFragment
 *
 * Created by kazuki on 2015/01/03.
 */
public abstract class AbstractProfileEditFragment extends Fragment {

    public static final String ARG_ITEM_ID = "item_id";
//    public static final String KEY_BUILT_EDIT_FLG = "key_built_edit_flg";
    public static final String KEY_EDIT_DATA = "key_edit_data";

    final int REQUEST_ACTION_PICK = 1;

//    public static final int TAG_IMG_FILE_NAME = 1;

    public static final String imageDirectoryName = AbstractProfileEditFragment.class.getPackage().getName() + "/.image/";

    private String imageFileName;

    protected SQLiteDatabase db;

//    protected boolean already_built_edit_flg = false;

    public AbstractProfileEditFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (db == null) {
            SixCatSQLiteOpenHelper helper = new SixCatSQLiteOpenHelper(getActivity());
            db = helper.getReadableDatabase();
        }

        View rootView = inflater.inflate(R.layout.fragment_profile_edit, container, false);


        initializeView(inflater, rootView, savedInstanceState);

//        //img_profile_edit_portrait
//        final ImageView portrait = (ImageView)rootView.findViewById(R.id.img_profile_edit_portrait);
//        if (portrait != null) {
//            portrait.setOnClickListener(new View.OnClickListener() {
//
//                @Override
//                public void onClick(View v) {
//                    //実行フロー
////                        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//                    //これだとギャラリー専門が開きます。
//                    Intent intent = new Intent(Intent.ACTION_PICK);
//                    intent.setType("image/*");
//                    //createChooserを使うと選択ダイアログのタイトルを変更する事ができます。
//                    startActivityForResult(Intent.createChooser(intent, "select"), REQUEST_ACTION_PICK);
//                    //デフォルトで「アプリ選択」と出ます。
//                    //startActivityForResult(intent, REQUEST_ACTION_PICK);
//                }
//            });
//        }

        return rootView;
    }

//
//    public  ArrayList<String> getInstanceState() {
//        ArrayList<String> list = new ArrayList<String>();
//        list.add("hello");
//        return list;
//    }
//
//    public void loadInstanceState(ArrayList<String> state) {
//        String str = "";
//        if (state != null){
//            for(String val : state) {
//                str += val;
//            }
//        }
//        Log.d("load state", str);
//    }
//     public void onRestoreInstanceState(Bundle inState) {
//
//         ArrayList<String> list =inState.getStringArrayList(KEY_EDIT_DATA);
//         String str = "";
//         if (list != null){
//             for(String val : list) {
//                 str += val;
//             }
//         }
//         Log.d("load state", str);
//    }

    protected class ImageClickListener implements View.OnClickListener {

        private int image_view_id;
        ImageClickListener(int image_view_id){
            this.image_view_id = image_view_id;
        }
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.putExtra(ARG_ITEM_ID,image_view_id);
            intent.setType("image/*");
            startActivityForResult(Intent.createChooser(intent, "select"), REQUEST_ACTION_PICK);
        }
    }
    abstract protected void initializeView(LayoutInflater inflater, View rootView, Bundle savedInstanceState);

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == Activity.RESULT_OK){
            if(requestCode == REQUEST_ACTION_PICK){
//                try {
                    ContentResolver resolver = getActivity().getContentResolver();

//                    InputStream iStream = getActivity().getApplicationContext().getContentResolver().openInputStream(data.getData());
//                    Bitmap bm = BitmapFactory.decodeStream(iStream);


                    int image_view_id = R.id.img_profile_edit_element;//TODO 一時策//data.getExtras().getInt(ARG_ITEM_ID);
                    ImageView imgView = (ImageView) getActivity().findViewById(image_view_id);

                    Bitmap bm = ImageUtility.loadImage(resolver,imgView, data.getData());

                    // 新しいフォルダにギャラリーから選んだ画像を保存
                    createFolderSaveImage(bm, getImageFileName());
//                    iStream.close();

//                    imgView.setImageBitmap(bm);
                    imgView.setTag(R.string.tag_image_file_path,
                            Environment.getExternalStorageDirectory()
                                    + "/" + imageDirectoryName
                                    + imageFileName );

//                    EditText editText = (EditText)getActivity().findViewById(R.id.txt_profile_edit_portrait);
//                    editText.setText(imageFileName);
//
//                }catch (IOException e) {
//                    e.printStackTrace();
//                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 新規フォルダを作成し、画像ファイルを保存する
     * 参考：http://d.hatena.ne.jp/acid-panda/20110420/1303316310
     *
     * @param imageToSave 保存対象のBitmap画像
     * @param fileName 保存先
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
            if (!folder.mkdirs()) {
                throw new RuntimeException("cannot make a directory [" + folderPath + "]");
            }
        }

        if (imageFileName != null) {
            //前回選択した画像は不要なので削除
            File old_file = new File(folder,imageFileName );
            if (old_file.exists()) {
                if (!old_file.delete()) {
                    Log.w("", "cannot delete file [" + imageFileName + "]");
                }
            }
        }
        File file = new File(folder, fileName);
        if (file.exists()) {
            if (!file.delete()){
                throw new RuntimeException("cannot delete file [" + fileName + "]");

            }
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
    }

    public static String getImageFileName(){
        //ファイル名の先頭をドットにするとデフォルトでは隠しファイルになる
        //media scan時にもおそらくスキップされる。
        return ".img_" + System.currentTimeMillis() + ".jpg";
    }





    protected View createRowView(LayoutInflater inflater, ProfileParcelable profile) {

//            Log.d("construct data", "       " + key_id + "," + value);
        View row = null;
        int content_view_id = R.id.txt_profile_edit_element;
        // 1:数値、2:単一行テキスト、3:複数行テキスト、4:英数字、5:選択、6:日付、7:画像
        switch (profile.value_type) {
            case 1:
                row = getNumberView(inflater, profile.value);
                break;
            case 2:
                row = getTextView(inflater, profile.value);
                break;
            case 3:
                row = getMultiTextView(inflater, profile.value);
                break;
            case 4:
                row = getAlphanumView(inflater, profile.value);
                break;
            case 5:
                row = getSelectView(inflater, profile.value, profile.options);
                content_view_id = R.id.spn_profile_edit_element;
                break;
            case 6:
                row = getDateView(inflater, profile.value);
                break;
            case 7:
                row = getImageView(inflater, profile.value);
                content_view_id = R.id.img_profile_edit_element;
                break;
        }
        if (row == null) {
            throw new RuntimeException("cannot make row view");
        }
        row.setTag(R.string.tag_key_id, profile.key_id);
        row.setTag(R.string.tag_key_sequence, profile.sequence);
        row.setTag(R.string.tag_key_type, profile.value_type);

        TextView label = (TextView) row.findViewById(R.id.lbl_profile_edit_element);
        label.setText(profile.label_str);

        View contentView = row.findViewById(content_view_id);

        contentView.setTag(R.string.tag_key_id, profile.key_id);
        contentView.setTag(R.string.tag_key_sequence, profile.sequence);
        contentView.setTag(R.string.tag_key_type, profile.value_type);

        return row;
    }

    private View getSelectView(LayoutInflater inflater, String value, List<String> options) {
        View row;
        row = inflater.inflate( R.layout.partial_profile_edit_element_select, null);


        Spinner spinnerEdit = (Spinner) row.findViewById(R.id.spn_profile_edit_element);
        ArrayAdapter<String> adapter = new MyArrayAdapter(this.getActivity(), android.R.layout.simple_list_item_1, options);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerEdit.setAdapter(adapter);
        if( value != null && !value.isEmpty()) {
            spinnerEdit.setSelection(Integer.valueOf(value));
        }
        spinnerEdit.setSaveEnabled(false);

        return row;
    }

    private View _getTextView(LayoutInflater inflater, String value, int layout_id) {
        View row;
        row = inflater.inflate(layout_id, null);

        EditText editView = (EditText) row.findViewById(R.id.txt_profile_edit_element);
        if (value != null) {
            editView.setText(value);
        }
        editView.setSaveEnabled(false);

        return row;
    }

    private View getTextView(LayoutInflater inflater, String value) {
        return _getTextView(inflater, value, R.layout.partial_profile_edit_element_text);
    }
    private View getMultiTextView(LayoutInflater inflater, String value) {
        return _getTextView(inflater, value, R.layout.partial_profile_edit_element_multitext);
    }
    private View getNumberView(LayoutInflater inflater, String value) {
        return _getTextView(inflater, value, R.layout.partial_profile_edit_element_number);
    }
    private View getAlphanumView(LayoutInflater inflater, String value) {
        return _getTextView(inflater, value, R.layout.partial_profile_edit_element_alphanum);
    }

    private View getImageView(LayoutInflater inflater, String value) {
        View row;
        row = inflater.inflate(R.layout.partial_profile_edit_element_image, null);
        ImageView imageView = (ImageView) row.findViewById(R.id.img_profile_edit_element);
        imageView.setOnClickListener(new ImageClickListener(R.id.img_profile_edit_element));

        if (value != null) {
            imageView.setTag(R.string.tag_image_file_path, value);
            File srcFile = new File(value);
            ContentResolver resolver = getActivity().getContentResolver();
            ImageUtility.loadImage(resolver,imageView, Uri.fromFile(srcFile));

//            try {
//                FileInputStream fis = new FileInputStream(srcFile);
//                Bitmap bm = BitmapFactory.decodeStream(fis);
//                imageView.setImageBitmap(bm);
//            } catch (FileNotFoundException e) {
//                Log.d("IMAGE ERROR", e.toString());
//                e.printStackTrace();
//            }
        }
        return row;
    }

    private View getDateView(LayoutInflater inflater, String value) {
        View row;
        row = inflater.inflate(R.layout.partial_profile_edit_element_date, null);
        final EditText editText = (EditText) row.findViewById(R.id.txt_profile_edit_element);
        if (value != null) {
            editText.setText(value);
        }
        editText.setSaveEnabled(false);
        editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog.OnDateSetListener DateSetListener = new DatePickerDialog.OnDateSetListener() {
                    public void onDateSet(android.widget.DatePicker datePicker, int year,
                                          int monthOfYear, int dayOfMonth) {
                        String dateStr = String.format("%4d/%02d/%02d",year, (monthOfYear + 1), dayOfMonth);
                        editText.setText(dateStr);
                    }
                };

                // 日付情報の初期設定
                Calendar calendar = Calendar.getInstance();
                try {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
                    dateFormat.setLenient(false);
                    java.util.Date date = dateFormat.parse(editText.getText().toString());
                    calendar.setTime(date);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                DatePickerDialog dialog = new DatePickerDialog(
                        getActivity(),
                        android.R.style.Theme_Light,
                        DateSetListener,
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)
                );
                dialog.show();
            }
        });
        return row;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Log.d("DEBUG", "onSaveInstanceState called.");

        LinearLayout containerView = (LinearLayout) getActivity().findViewById(R.id.container_profile_edit);

        ArrayList<ProfileParcelable> contents = new ArrayList<>();

        int childCount = containerView.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View row = containerView.getChildAt(i);

            if (row.getTag(R.string.tag_key_id) == null) {
                continue;
            }

            String key_id = (String) row.getTag(R.string.tag_key_id);
            int sequence = (Integer) row.getTag(R.string.tag_key_sequence);
            int value_type = (Integer) row.getTag(R.string.tag_key_type);

            TextView label = (TextView) row.findViewById(R.id.lbl_profile_edit_element);
            String label_str = label.getText().toString();

            String value = getValue(row, value_type);
            List<String> options = getOptions(row, value_type);

            ProfileParcelable content = new ProfileParcelable(key_id, sequence, value, value_type, label_str, options);
            contents.add(content);

            Log.d("save", content.toString());

            releaseResource(row, value_type);
        }
        outState.putParcelableArrayList(KEY_EDIT_DATA, contents );

    }

    @Override
    public void onPause(){
        super.onPause();

        LinearLayout containerView = (LinearLayout) getActivity().findViewById(R.id.container_profile_edit);

        int childCount = containerView.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View row = containerView.getChildAt(i);
            if (row.getTag(R.string.tag_key_id) == null) {
                continue;
            }
            int value_type = (Integer) row.getTag(R.string.tag_key_type);
            releaseResource(row, value_type);
        }
    }
    private void releaseResource(View container, int value_type) {
        if (value_type == 7) {
            //Image
            ImageView imageView = (ImageView) container.findViewById(R.id.img_profile_edit_element);
            imageView.setImageDrawable(null);
            Log.d("memory leak check", "a");
        }
    }

    private String getValue(View container, int value_type) {
        String value = null;
        switch (value_type) {
            case 1:
            case 2:
            case 3:
            case 4:
                //
                EditText textEdit = (EditText) container.findViewById(R.id.txt_profile_edit_element);
                value = textEdit.getText().toString();
                break;
            case 5:
                //
                Spinner spinnerEdit = (Spinner) container.findViewById(R.id.spn_profile_edit_element);
                value = "" + spinnerEdit.getSelectedItemPosition();
                break;
            case 6:
                //
                EditText dateEdit = (EditText) container.findViewById(R.id.txt_profile_edit_element);
                value = dateEdit.getText().toString();
                break;
            case 7:

                ImageView imageView = (ImageView) container.findViewById(R.id.img_profile_edit_element);
                value = (String) imageView.getTag(R.string.tag_image_file_path);
                break;
        }
        return value;
    }


    private List<String> getOptions(View container, int value_type) {
        List<String> options = new ArrayList<>();
        for(int i=0; i<=9; i++) {
            options.add(null);
        }
        if(value_type == 5) {
            Spinner spinnerEdit = (Spinner) container.findViewById(R.id.spn_profile_edit_element);
            ArrayAdapter<String> adapter = (ArrayAdapter<String>)spinnerEdit.getAdapter();
            for(int i=0; i<adapter.getCount(); i++) {
                String item = (String)adapter.getItem(i);
                options.set(i, item);
            }
        }
        return options;
    }
}

class MyArrayAdapter extends ArrayAdapter<String> {
    private int resource;
    public  MyArrayAdapter(Context context, int resource, List<String> objects) {
        super(context, resource, objects);
        this.resource = resource;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView v = null;
        if (convertView != null && convertView instanceof TextView) {
            v = (TextView) convertView;
        } else {
            v = (TextView) TextView.inflate(getContext(), this.resource, null);
        }
        v.setText(this.getItem(position));
        return v;
    }
}