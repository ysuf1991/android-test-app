package com.examples.android.showcategories.app;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


public class ServiceListActivity extends ActionBarActivity implements
                                                           ServiceListFragment.OnServiceListSelectedListener{
    private ServiceDataSource dataSource;

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_list);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        dataSource = new ServiceDataSource(this);
        dataSource.open();
        if (dataSource.isEmpty()) {
            dataSource.getDataFromJson(ServiceListActivity.this, ServiceDataSource.JSON_URL);
        } else {
            setFragment();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public void setFragment() {
        FragmentManager manager = getSupportFragmentManager();
        Fragment fragment = manager.findFragmentById(R.id.fragmentContainer);
        manager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        if (fragment == null) {
            fragment = ServiceListFragment.newInstance(dataSource.getServiceById(ServiceDataSource.ROOT_PARENT_ID));
            manager.beginTransaction()
                    .add(R.id.fragmentContainer, fragment)
                    .commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return true;
    }

    @Override
    public void onListSelected(long id) {
        dataSource.open();
        ServiceBean serviceBean = dataSource.getServiceById(id);
        if (serviceBean.getSubs() == null) {
            return;
        }
        FragmentManager manager = getSupportFragmentManager();
        ServiceListFragment fragment = ServiceListFragment.newInstance(serviceBean);
        manager.beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .addToBackStack(null)
                .commit();
    }

    public void onUpdateButtonClick(View view) {
        dataSource.dropBD();
        dataSource.open();
        dataSource.getDataFromJson(ServiceListActivity.this, ServiceDataSource.JSON_URL);
    }
}
