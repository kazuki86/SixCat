package jp.gr.java_conf.kazuki.sixcat;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;


import jp.gr.java_conf.kazuki.sixcat.data.SixCatSQLiteOpenHelper;
import jp.gr.java_conf.kazuki.sixcat.dummy.DummyContent;

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

    /**
     * The dummy content this fragment is presenting.
     */
    private DummyContent.DummyItem mItem;


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
                    ,null,null,null);
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

        // Show the dummy content as text in a TextView.
        if (cursor != null) {

            LinearLayout containerView = (LinearLayout)rootView.findViewById(R.id.container_profile_detail);

            while(cursor.moveToNext()){

                String key_id = cursor.getString(cursor.getColumnIndex("_id"));
                String value =  cursor.getString(cursor.getColumnIndex("value"));
                String sequence_str =  cursor.getString(cursor.getColumnIndex("sequence"));
                int sequence = (sequence_str == null) ? 1 : Integer.valueOf(sequence_str);
                String label_str = cursor.getString(cursor.getColumnIndex("name")) + ((sequence>1?sequence:""));

                if (value == null) {
                    continue;
                }

                View row = inflater.inflate(R.layout.partial_profile_detail_element_text, null);

                TextView label = (TextView) row.findViewById(R.id.lbl_profile_detail_element);
                label.setText(label_str);

                TextView textView = (TextView) row.findViewById(R.id.txt_profile_detail_element);
                textView.setText(value);

                containerView.addView(row);
            }


//            while(cursor.moveToNext()) {
//                //TODO ここで、各値に応じたViewを動的にセットする
//                int key_id = Integer.valueOf(cursor.getString(cursor.getColumnIndex("key_id")));
//                String value = cursor.getString(cursor.getColumnIndex("value"));
//                switch(key_id) {
//                    case 1://name
//                        ((TextView) rootView.findViewById(R.id.txt_profile_detail_name)).setText(value);
//                        break;
//                    case 2://kana
//                        ((TextView) rootView.findViewById(R.id.txt_profile_detail_memo)).setText(value);
//                        break;
//                    case 3://nickname
//                        ((TextView) rootView.findViewById(R.id.txt_profile_detail_age)).setText(value);
//                        break;
//                    case 4://birthday
//                        ((TextView) rootView.findViewById(R.id.txt_profile_detail_address)).setText(value);
//                        break;
//                }
//            }
//            ((TextView) rootView.findViewById(R.id.txt_profile_detail_name)).setText(cursor.getString(cursor.getColumnIndex("name")));
//            ((TextView) rootView.findViewById(R.id.txt_profile_detail_address)).setText(cursor.getString(cursor.getColumnIndex("birthday")));

        }

        return rootView;
    }
}
