package jp.gr.java_conf.kazuki.sixcat;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

    private final int IDX_ID = 0;
    private final int IDX_STATUS = 1;
    private final int IDX_NAME = 2;
    private final int IDX_BIRTHDAY = 3;
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
            cursor = db.query("view_profile_list",
                    new String[]{"_id","status","name","birthday"},
                    "_id = ?",new String[]{getArguments().getString(ARG_ITEM_ID)}
                    ,null,null,null);
            cursor.moveToFirst();

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

            ((TextView) rootView.findViewById(R.id.txt_profile_detail_name)).setText(cursor.getString(IDX_NAME));

            ((TextView) rootView.findViewById(R.id.txt_profile_detail_address)).setText(cursor.getString(IDX_BIRTHDAY));

        }

        return rootView;
    }
}
