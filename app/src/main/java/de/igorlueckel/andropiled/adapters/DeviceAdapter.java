package de.igorlueckel.andropiled.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.igorlueckel.andropiled.R;
import de.igorlueckel.andropiled.models.LedDevice;

/**
 * Created by Igor on 02.08.2015.
 */
public class DeviceAdapter extends AbstractListAdapter<LedDevice, DeviceAdapter.ViewHolder> {

    private final LayoutInflater mInflater;

    public DeviceAdapter(Context context) {
        super(context);
        mInflater = LayoutInflater.from(context);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        @InjectView(R.id.textViewDeviceName)
        TextView textViewDeviceName;

        @InjectView(R.id.radioButton)
        RadioButton radioButton;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.inject(this, view);
        }

        public void bind(final LedDevice ledDevice) {
            textViewDeviceName.setText(ledDevice.getAddress().toString());
            radioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                }
            });
        }
    }

    @Override
    public DeviceAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        return new ViewHolder(mInflater.inflate(R.layout.listitem_device, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(DeviceAdapter.ViewHolder viewHolder, int position) {
        viewHolder.bind(getItem(position));
    }
}
