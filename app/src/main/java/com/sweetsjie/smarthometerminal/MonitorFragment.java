package com.sweetsjie.smarthometerminal;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class MonitorFragment extends Fragment {
    private View monitorFragment;
    private TextView temperatureTv;
    private TextView humidityTv;
    private Button refreshBt;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        monitorFragment = inflater.inflate(R.layout.fragment_monitor,container,false);
        return monitorFragment;
    }

    @Override
    public void onStart() {
        temperatureTv = monitorFragment.findViewById(R.id.temperatureTv);
        humidityTv = monitorFragment.findViewById(R.id.humidityTv);
        refreshBt = monitorFragment.findViewById(R.id.refreshBt);

        refreshBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).sendCmd("GETINFO");
            }
        });
        super.onStart();
    }

    public void setTemperatureTv(String string)
    {
        temperatureTv.setText(string);
    }
    public void setHumidityTv(String string)
    {
        humidityTv.setText(string);
    }
}
