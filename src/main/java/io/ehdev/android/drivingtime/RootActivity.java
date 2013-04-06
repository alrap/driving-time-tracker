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

package io.ehdev.android.drivingtime;

import android.os.Bundle;
import android.util.Log;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.j256.ormlite.table.TableUtils;
import io.ehdev.android.drivingtime.database.dao.DrivingRecordDao;
import io.ehdev.android.drivingtime.database.dao.DrivingTaskDao;
import io.ehdev.android.drivingtime.database.model.DrivingRecord;
import io.ehdev.android.drivingtime.database.model.DrivingTask;
import io.ehdev.android.drivingtime.fragments.MainFragment;
import io.ehdev.android.drivingtime.fragments.TabListenerImpl;
import org.joda.time.DateTime;
import org.joda.time.Duration;

import java.sql.SQLException;

public class RootActivity extends SherlockFragmentActivity {

    private DrivingTaskDao drivingTaskDao;
    private DrivingRecordDao drivingRecordDao;

    private static final String TAG = RootActivity.class.getName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setupTempDatabase();

        getSupportActionBar().setTitle("");
        getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().addTab(
                getSupportActionBar()
                        .newTab()
                        .setTabListener(new TabListenerImpl<MainFragment>(this, MainFragment.class.getSimpleName(), MainFragment.class))
                        .setText("Overview"));
    }

    private void setupTempDatabase() {
        drivingRecordDao = new DrivingRecordDao(this);
        drivingTaskDao = new DrivingTaskDao(this);

        try{
            TableUtils.dropTable(drivingTaskDao.getConnectionSource(), DrivingTask.class, true);
            TableUtils.dropTable(drivingTaskDao.getConnectionSource(), DrivingRecord.class, true);

            TableUtils.createTable(drivingTaskDao.getConnectionSource(), DrivingTask.class);
            TableUtils.createTable(drivingTaskDao.getConnectionSource(), DrivingRecord.class);

            buildTempDatabase();
        } catch (Exception e){
            Log.i(TAG, e.getMessage());
        }
    }

    private void buildTempDatabase() throws SQLException {
        DrivingTask drivingTask1 = new DrivingTask("Highway", Duration.standardHours(40));
        DrivingTask drivingTask2 = new DrivingTask("Night", Duration.standardHours(8));
        drivingTaskDao.getDao().create(drivingTask1);
        drivingTaskDao.getDao().create(drivingTask2);

        DrivingRecord drivingRecord = new DrivingRecord(drivingTask1, DateTime.now().minusHours(15), Duration.standardHours(10));
        DrivingRecord drivingRecord2 = new DrivingRecord(drivingTask2, DateTime.now().minusHours(15), Duration.standardHours(6));
        drivingRecordDao.getDao().create(drivingRecord);
        drivingRecordDao.getDao().create(drivingRecord2);
    }

}
