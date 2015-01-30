package jp.gr.java_conf.kazuki.sixcat;

import android.content.ContentResolver;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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
import java.util.ArrayList;
import java.util.List;

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

            SixCatSQLiteOpenHelper helper = new SixCatSQLiteOpenHelper(getActivity());

            SQLiteDatabase db = helper.getReadableDatabase();
            cursor = db.query("view_profile_detail",
                    null,
                    "profile_id = ?",new String[]{getArguments().getString(ARG_ITEM_ID)}
                    ,null,null,"sort_order");

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_profile_detail, container, false);

        if (cursor != null) {

            LinearLayout containerView = (LinearLayout)rootView.findViewById(R.id.container_profile_detail);

            while(cursor.moveToNext()){

                String value =  cursor.getString(cursor.getColumnIndex("value"));
                String sequence_str =  cursor.getString(cursor.getColumnIndex("sequence"));
                int sequence = (sequence_str == null) ? 1 : Integer.valueOf(sequence_str);
                String label_str = cursor.getString(cursor.getColumnIndex("name")) + ((sequence>1?sequence:""));
                int value_type =  cursor.getInt(cursor.getColumnIndex("value_type_id"));

                List<KeyValueItem> options = new ArrayList<>();
                options.add(new KeyValueItem(R.integer.option_no_select_id, getString(R.string.option_default_string)));
                for(int i=getResources().getInteger(R.integer.option_index_from); i<=getResources().getInteger(R.integer.option_index_to); i++) {
                    String column_name = String.format("option%02d",i);
                    String option = cursor.getString(cursor.getColumnIndex(column_name));
                    if (option != null) {
                        options.add(new KeyValueItem(i,option));
                    }
                }

                if (value == null) {
                    continue;
                }


                View row = null;
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
                        row = inflater.inflate(R.layout.partial_profile_detail_element_text, null);
                        TextView textView2 = (TextView) row.findViewById(R.id.txt_profile_detail_element);

                        //AbstractProfileEditFragment # getSelectView と重複してるなー
                        String option_str = "";
                        for(int i=0; i<options.size(); i++) {
                            if (value.equals(""+options.get(i).key)) {
                                option_str = options.get(i).value;
                            }
                        }
                        textView2.setText(option_str);
                        break;
                    case 7:
                        row = inflater.inflate(R.layout.partial_profile_detail_element_image, null);
                        ImageView imageView = (ImageView) row.findViewById(R.id.img_profile_detail_element);

                        if (value != null) {
                            imageView.setTag(R.string.tag_image_file_path,value);
                            ContentResolver resolver = getActivity().getContentResolver();
                            File srcFile = new File(value);
                            ImageUtility.loadImage(resolver, imageView, Uri.fromFile(srcFile));
                        }
                        break;
                }
                if (row != null) {
                    TextView label = (TextView) row.findViewById(R.id.lbl_profile_detail_element);
                    label.setText(label_str);
                    containerView.addView(row);
                }
            }

        }

        return rootView;
    }


}
