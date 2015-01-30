package jp.gr.java_conf.kazuki.sixcat;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;


/**
 * An activity representing a single Profile detail screen. This
 * activity is only used on handset devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link ProfileListActivity}.
 * <p/>
 * This activity is mostly just a 'shell' activity containing nothing
 * more than a {@link ProfileDetailFragment}.
 */
public class ProfileDetailActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_detail);

        // Show the Up button in the action bar.
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (savedInstanceState == null) {
            Bundle arguments = new Bundle();
            arguments.putString(ProfileDetailFragment.ARG_ITEM_ID,
                    getIntent().getStringExtra(ProfileDetailFragment.ARG_ITEM_ID));
            ProfileDetailFragment fragment = new ProfileDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.profile_detail_container, fragment)
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_profile_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            NavUtils.navigateUpTo(this, new Intent(this, ProfileListActivity.class));
            return true;
        }

        switch(id) {
            case R.id.menu_profile_detail_profile_edit:
                Intent intent = new Intent(this, ProfileEditActivity.class);
                String profile_id = getIntent().getStringExtra(ProfileDetailFragment.ARG_ITEM_ID);
                intent.putExtra(AbstractProfileEditFragment.ARG_ITEM_ID, profile_id);
                startActivity(intent);

                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
