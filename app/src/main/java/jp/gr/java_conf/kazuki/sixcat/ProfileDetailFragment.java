package jp.gr.java_conf.kazuki.sixcat;

import android.content.ContentResolver;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import jp.gr.java_conf.kazuki.sixcat.data.SixCatSQLiteOpenHelper;

/**
 * A fragment representing a single Profile detail screen.
 * This fragment is either contained in a {@link ProfileListActivity}
 * in two-pane mode (on tablets) or a {@link ProfileDetailActivity}
 * on handsets.
 */
public class ProfileDetailFragment extends Fragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";

    private SQLiteDatabase db;

    private Cursor cursor;


//    private final int IDX_ID = 0;
//    private final int IDX_STATUS = 1;
//    private final int IDX_NAME = 2;
//    private final int IDX_KANA = 3;
//    private final int IDX_NICKNAME = 4;
//    private final int IDX_BIRTHDAY = 5;
    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ProfileDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_ID)) {

            Log.d("View", "ARG_ITEM_ID:" + getArguments().getString(ARG_ITEM_ID));
//            long id = Long.getLong(getArguments().getString(ARG_ITEM_ID));
            SixCatSQLiteOpenHelper helper = new SixCatSQLiteOpenHelper(getActivity());

            db = helper.getReadableDatabase();
            //sample
//            cursor = db.query("view_profile_list",
//                    new String[]{"_id","status","name","kana","nickname","birthday"},
//                    "_id = ?",new String[]{getArguments().getString(ARG_ITEM_ID)}
//                    ,null,null,null);
            cursor = db.query("view_profile_detail",
                    null,
                    "profile_id = ?",new String[]{getArguments().getString(ARG_ITEM_ID)}
                    ,null,null,"sort_order");
            //cursor.moveToFirst();

            Log.d("Debug", "ccc");
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
//            mItem = DummyContent.ITEM_MAP.get(getArguments().getString(ARG_ITEM_ID));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_profile_detail, container, false);

        if (cursor != null) {

            LinearLayout containerView = (LinearLayout)rootView.findViewById(R.id.container_profile_detail);

            while(cursor.moveToNext()){

                String key_id = cursor.getString(cursor.getColumnIndex("_id"));
                String value =  cursor.getString(cursor.getColumnIndex("value"));
                String sequence_str =  cursor.getString(cursor.getColumnIndex("sequence"));
                int sequence = (sequence_str == null) ? 1 : Integer.valueOf(sequence_str);
                String label_str = cursor.getString(cursor.getColumnIndex("name")) + ((sequence>1?sequence:""));
                int value_type =  cursor.getInt(cursor.getColumnIndex("value_type_id"));

                if (value == null) {
                    continue;
                }


                View row = null;
                int content_view_id = R.id.txt_profile_edit_element;
                // 1:数値、2:単一行テキスト、3:複数行テキスト、4:英数字、5:選択、6:日付、7:画像
                switch(value_type) {
                    case 1:
                    case 2:
                    case 3:
                    case 4:
                    case 6:
                        //
                        row = inflater.inflate(R.layout.partial_profile_detail_element_text, null);
                        TextView textView = (TextView) row.findViewById(R.id.txt_profile_detail_element);
                        textView.setText(value);
                        break;
                    case 5:
                        break;
                    case 7:
                        row = inflater.inflate(R.layout.partial_profile_detail_element_image, null);
                        ImageView imageView = (ImageView) row.findViewById(R.id.img_profile_detail_element);

                        if (value != null) {
                            imageView.setTag(R.string.tag_image_file_path,value);
                            ContentResolver resolver = getActivity().getContentResolver();
                            File srcFile = new File(value);
                            ImageUtility.loadImage(resolver, imageView, srcFile);

//                                FileInputStream fis = new FileInputStream(srcFile);
//                                Bitmap bm = BitmapFactory.decodeStream(fis);
//                                imageView.setImageBitmap(bm);
                        }
                        break;
                }
                TextView label = (TextView) row.findViewById(R.id.lbl_profile_detail_element);
                label.setText(label_str);

                containerView.addView(row);
            }

        }

        return rootView;
    }


}
