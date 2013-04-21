/*
 * Copyright (C) 2013 by Ethan Hall
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package io.ehdev.android.drivingtime.view.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import com.j256.ormlite.table.TableUtils;
import io.ehdev.android.drivingtime.R;
import io.ehdev.android.drivingtime.StaticInstances;
import io.ehdev.android.drivingtime.backend.model.Record;
import io.ehdev.android.drivingtime.backend.model.Task;
import io.ehdev.android.drivingtime.view.fragments.AllDrivingRecordReviewFragment;
import io.ehdev.android.drivingtime.view.fragments.MainFragment;
import org.joda.time.DateTime;
import org.joda.time.Duration;

import java.sql.SQLException;

public class RootActivity extends Activity implements ActionBar.TabListener {

    private Fragment listOfFragments[];

    private static final String TAG = RootActivity.class.getName();
    private StaticInstances staticInstance;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        staticInstance = StaticInstances.getInstance(this);

        if (savedInstanceState == null) {
            setupTempDatabase();
        }

        listOfFragments = new Fragment[]{
                MainFragment.instantiate(this, MainFragment.class.getName()),
                AllDrivingRecordReviewFragment.instantiate(this, AllDrivingRecordReviewFragment.class.getName())
        };

        getActionBar().addTab(
                getActionBar()
                        .newTab()
                        .setTabListener(this)
                        .setText("Overview"));

        getActionBar().addTab(
                getActionBar()
                        .newTab()
                        .setTabListener(this)
                        .setText("Review Entries"));
        getActionBar().setTitle("");
        getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        getActionBar().setDisplayShowTitleEnabled(true);

        if(savedInstanceState != null){
            getActionBar().setSelectedNavigationItem(savedInstanceState.getInt("tab"));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.preferences_menu, menu);
        return true;
    }

    private void setupTempDatabase() {

        try{
            TableUtils.dropTable(staticInstance.getDrivingTaskHelper().getConnectionSource(), Task.class, true);
            TableUtils.dropTable(staticInstance.getDrivingRecordHelper().getConnectionSource(), Record.class, true);

            TableUtils.createTable(staticInstance.getDrivingTaskHelper().getConnectionSource(), Task.class);
            TableUtils.createTable(staticInstance.getDrivingRecordHelper().getConnectionSource(), Record.class);

            buildTempDatabase();
        } catch (Exception e){
            Log.i(TAG, e.getMessage());
        }
    }

    private void buildTempDatabase() throws SQLException {
        Task drivingTask1 = new Task("Highway", Duration.standardHours(40));
        Task drivingTask2 = new Task("Night", Duration.standardHours(8));
        staticInstance.getDrivingTaskDao().create(drivingTask1);
        staticInstance.getDrivingTaskDao().create(drivingTask2);

        Record drivingRecord = new Record(drivingTask1, DateTime.now().minusHours(15), Duration.standardHours(10));
        Record drivingRecord2 = new Record(drivingTask2, DateTime.now().minusHours(15), Duration.standardHours(6));
        staticInstance.getDrivingRecordDao().create(drivingRecord);
        staticInstance.getDrivingRecordDao().create(drivingRecord2);
    }

    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
        if(listOfFragments[tab.getPosition()].isDetached())
            ft.attach(listOfFragments[tab.getPosition()]);
        else {
            ft.replace(android.R.id.content, listOfFragments[tab.getPosition()]);
        }
    }

    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
        ft.detach(listOfFragments[tab.getPosition()]);
    }

    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {
        // User selected the already selected tab. Usually do nothing.
    }

}
