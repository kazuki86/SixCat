package jp.gr.java_conf.kazuki.sixcat;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;


/**
 * An activity representing a list of ProfileList. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link ProfileDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 * <p/>
 * The activity makes heavy use of fragments. The list of items is a
 * {@link ProfileListFragment} and the item details
 * (if present) is a {@link ProfileDetailFragment}.
 * <p/>
 * This activity also implements the required
 * {@link ProfileListFragment.Callbacks} interface
 * to listen for item selections.
 */
public class ProfileListActivity extends ActionBarActivity
        implements ProfileListFragment.Callbacks {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    //private boolean mTwoPane;
    private Boolean mTwoPane;

    private String current_item_id = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_list);

        if (isTwoPane()) {
            // In two-pane mode, list items should be given the
            // 'activated' state when touched.
            ((ProfileListFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.profile_list))
                    .setActivateOnItemClick(true);
        }

    }

    /**
     * Callback method from {@link ProfileListFragment.Callbacks}
     * indicating that the item with the given ID was selected.
     */
    @Override
    public void onItemSelected(String id) {
        current_item_id = id;
        if (mTwoPane) {
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            Bundle arguments = new Bundle();
            arguments.putString(ProfileDetailFragment.ARG_ITEM_ID, id);
            ProfileDetailFragment fragment = new ProfileDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.profile_detail_container, fragment)
                    .commit();

        } else {
            // In single-pane mode, simply start the detail activity
            // for the selected item ID.
            Intent detailIntent = new Intent(this, ProfileDetailActivity.class);
            detailIntent.putExtra(ProfileDetailFragment.ARG_ITEM_ID, id);
            startActivity(detailIntent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        if (isTwoPane()) {
            getMenuInflater().inflate(R.menu.menu_profile_list_twopane, menu);
        } else {
            getMenuInflater().inflate(R.menu.menu_profile_list, menu);

        }
        updateMenuEnabled(menu);
        return true;
    }

    @Override
    public void onAttachFragment(Fragment fragment){
        //メニュー再表示のために一度破棄させる。
        invalidateOptionsMenu();
    }

    public boolean onPrepareOptionsMenu(Menu menu) {
        updateMenuEnabled(menu);
        return true;
    }

    private void updateMenuEnabled(Menu menu){
        MenuItem item = menu.findItem(R.id.menu_profile_detail_profile_edit);
        if (item != null){
            item.setEnabled(isDetailDisplayed());
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        Intent intent = null;
        switch(id) {
            case R.id.menu_profile_list_profile_register:
                //showDialog("menu_profile_list_profile_register");
                intent = new Intent(this, ProfileRegisterActivity.class);
                startActivity(intent);

                break;
            case R.id.menu_profile_detail_profile_edit:

                intent = new Intent(this, ProfileEditActivity.class);
                String profile_id = current_item_id;
                Log.d("Debug", profile_id);
                intent.putExtra(AbstractProfileEditFragment.ARG_ITEM_ID, profile_id);
                startActivity(intent);
                break;
            case R.id.menu_profile_list_appointment_list:
                showDialog("menu_profile_list_appointment_list");
                break;
            case R.id.menu_profile_list_appointment_register:
                showDialog("menu_profile_list_appointment_register");
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showDialog(String text){

        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("メニュー結果");
        dialog.setMessage(text);
        dialog.setPositiveButton("OK",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ProfileListActivity.this.setResult(Activity.RESULT_OK);
            }
        });
        dialog.create();
        dialog.show();
    }

    private boolean isTwoPane(){
        if (mTwoPane == null) {
            mTwoPane = (findViewById(R.id.profile_detail_container) != null);
        }
        return mTwoPane;
    }

    private boolean isDetailDisplayed(){
        if (isTwoPane()) {
            if (findViewById(R.id.profile_detail_container).findViewById(R.id.fragment_profile_detail_root) != null){
                return true;
            }
        }
        return false;
    }


}
