package com.examples.android.showcategories.app;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import org.apache.commons.io.IOUtils;

import java.io.InputStream;
import java.io.StringReader;
import java.net.URL;
import java.util.ArrayList;

public class ServiceDataSource {
    public final static String JSON_URL = "https://money.yandex.ru/api/categories-list";
    public final static long ROOT_PARENT_ID = 1;
    private SQLiteDatabase database;
    private ServiceDBHelper dbHelper;

    public ServiceDataSource(Context context) {
        dbHelper = new ServiceDBHelper(context);
    }

    public void open() {
        database = dbHelper.getWritableDatabase();
    }

    public boolean isEmpty() {
        Cursor cursor = database.query(ServiceDBHelper.TABLE_NAME, null, null, null, null, null, null);
        boolean empty = (cursor.getCount() - 1) == 0;
        cursor.close();
        return  empty;
    }

    public void addToDataBase(ServiceBean[] serviceBeans, long parentId) {
        for (ServiceBean serviceBean : serviceBeans) {
            long id = addToDataBase(serviceBean, parentId);
            if (serviceBean.getSubs() != null) {
                addToDataBase(serviceBean.getSubs(), id);
            }
        }
    }

    public ServiceBean getServiceById(long id) {
        Cursor cursor = database.query(ServiceDBHelper.TABLE_NAME, null, ServiceDBHelper.KEY_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null);

        cursor.moveToFirst();
        ServiceBean serviceBean = new ServiceBean(cursor.getLong(0),
                cursor.getLong(2),
                cursor.getString(3));
        cursor.close();
        serviceBean.setSubs(getAllServiceWithParentId(id));
        return serviceBean;
    }

    public ServiceBean[] getAllServiceWithParentId(long keyId) {
        ArrayList<ServiceBean> serviceBeans = new ArrayList<ServiceBean>();
        Cursor cursor = database.query(ServiceDBHelper.TABLE_NAME, null, ServiceDBHelper.PARENT_ID + "=?",
                                                         new String[]{String.valueOf(keyId)}, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            serviceBeans.add(new ServiceBean(cursor.getLong(0),
                                             cursor.getLong(2),
                                             cursor.getString(3)));
            cursor.moveToNext();
        }
        cursor.close();
        if (serviceBeans.isEmpty()) {
            return  null;
        }
        ServiceBean[] beans = new ServiceBean[serviceBeans.size()];
        serviceBeans.toArray(beans);
        return beans;
    }

    private long addToDataBase(ServiceBean serviceBean, long parentId) {
        ContentValues values = new ContentValues();
        values.put(ServiceDBHelper.PARENT_ID, parentId);
        values.put(ServiceDBHelper.SERVICE_ID, serviceBean.getId());
        values.put(ServiceDBHelper.SERVICE_TITLE, serviceBean.getTitle());
        return database.insert(ServiceDBHelper.TABLE_NAME, null, values);
    }

    public void dropBD() {
        dbHelper.onUpgrade(database, 1, 1);
    }
    public void close() {
       dbHelper.close();
    }

    public void getDataFromJson(ServiceListActivity activity, String url) {
        new GetDataFromJson(activity).execute(url);
    }

    public class GetDataFromJson extends AsyncTask<String, Void, Void> {
        private ProgressDialog dialog;
        ServiceListActivity activity;

        public GetDataFromJson(ServiceListActivity activity) {
            this.activity = activity;
            dialog = new ProgressDialog(activity);
        }


        @Override
        protected void onPreExecute() {
            dialog.setMessage("Please wait...");
            dialog.setIndeterminate(false);
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected Void doInBackground(String... arg) {
            Gson gson = new GsonBuilder()
                    .serializeSpecialFloatingPointValues()
                    .setPrettyPrinting()
                    .create();
            ServiceBean[] serviceBeans = null;
            JsonReader reader = null;
            InputStream input = null;
            try {
                URL url = new URL(arg[0]);
                input = url.openStream();
                String json = IOUtils.toString(input, "UTF-8");
                reader = new JsonReader(new StringReader(json));
                serviceBeans = gson.fromJson(reader, ServiceBean[].class);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (input != null) {
                        input.close();
                    }
                    if (reader != null) {
                        reader.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            addToDataBase(serviceBeans, ROOT_PARENT_ID);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (dialog.isShowing()) {
                    dialog.dismiss();
            }
            activity.setFragment();
        }
    }

}
