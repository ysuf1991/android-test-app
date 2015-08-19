package com.examples.android.showcategories.app;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class ServiceListFragment extends ListFragment{
    private final static String EXTRA_SEVICE_BEAN = "showcategories.app.ServiceListFragment.ServiceBean";
    private ServiceBean serviceBean;
    private OnServiceListSelectedListener mCallback;

    public static ServiceListFragment newInstance(ServiceBean serviceBean) {
        Bundle agrs = new Bundle();
        agrs.putSerializable(EXTRA_SEVICE_BEAN, serviceBean);
        ServiceListFragment serviceListFragment = new ServiceListFragment();
        serviceListFragment.setArguments(agrs);
        return serviceListFragment;
    }

    public interface OnServiceListSelectedListener {
        void onListSelected(long id);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallback = (OnServiceListSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString());
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        serviceBean = (ServiceBean) getArguments().getSerializable(EXTRA_SEVICE_BEAN);
        setTitle();
        ServiceListAdapter adapter = new ServiceListAdapter(serviceBean.getSubs());
        setListAdapter(adapter);
        setHasOptionsMenu(true);
        setRetainInstance(true);
    }

    public void setTitle() {
        if (serviceBean.getTitle() == null) {
            getActivity().setTitle(R.string.app_name);
        } else {
            getActivity().setTitle(serviceBean.getTitle());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        setTitle();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, parent, savedInstanceState);
        return v;
    }

    public void onListItemClick(ListView l, View v, int position, long id) {
        ServiceBean serviceBean = ((ServiceListAdapter)getListAdapter()).getItem(position);
        mCallback.onListSelected(serviceBean.getKeyId());
    }

    private class ServiceListAdapter extends ArrayAdapter<ServiceBean> {
        public ServiceListAdapter(ServiceBean[] serviceBeans) {
            super(getActivity(), android.R.layout.simple_list_item_1, serviceBeans);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getActivity().getLayoutInflater()
                        .inflate(R.layout.service_list_item, parent, false);
            }

            ServiceBean serviceBean = getItem(position);

            TextView titleTextView =
                    (TextView)convertView.findViewById(R.id.service_list_item_titleTextView);
            titleTextView.setText(serviceBean.getTitle());
            return convertView;
        }
    }
}
