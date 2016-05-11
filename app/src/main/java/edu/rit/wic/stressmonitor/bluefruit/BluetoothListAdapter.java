package edu.rit.wic.stressmonitor.bluefruit;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.rit.wic.stressmonitor.R;

/**
 * Created by Matthew on 5/6/2016.
 */
public class BluetoothListAdapter extends BaseAdapter {

    private List<BluetoothDevice> devices;
    private LayoutInflater inflater;

    public BluetoothListAdapter(Activity activity) {
        super();
        this.devices = new ArrayList<>();
        this.inflater = activity.getLayoutInflater();
    }

    public void addDevice(BluetoothDevice device) {
        if(!devices.contains(device))
            this.devices.add(device);
    }

    public BluetoothDevice getDevice(int position) {
        return this.devices.get(position);
    }

    public void clear() {
        this.devices.clear();
    }

    @Override
    public int getCount() {
        return devices.size();
    }

    @Override
    public Object getItem(int position) {
        return devices.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if(convertView == null) {
            convertView = inflater.inflate(R.layout.listitem_le_device, null);
            holder = new ViewHolder();
            holder.deviceAddress = (TextView) convertView.findViewById(R.id.device_address);
            holder.deviceName = (TextView) convertView.findViewById(R.id.device_name);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        BluetoothDevice device = devices.get(position);
        final String deviceName = device.getName();
        if(deviceName != null && deviceName.length() > 0)
            holder.deviceName.setText(deviceName);
        else
            holder.deviceName.setText(R.string.unknown_le_device);
        holder.deviceAddress.setText(device.getAddress());

        return convertView;
    }

    static class ViewHolder {
        TextView deviceName;
        TextView deviceAddress;
    }
}
