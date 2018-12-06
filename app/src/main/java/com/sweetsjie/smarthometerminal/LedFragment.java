package com.sweetsjie.smarthometerminal;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ToggleButton;

public class LedFragment extends Fragment {

    private ToggleButton ledBt;
    private ImageView ledImage;
    private View ledFragment;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ledFragment = inflater.inflate(R.layout.fragment_led,container,false);
        return ledFragment;
    }

    @Override
    public void onStart() {
        ledBt = ledFragment.findViewById(R.id.ledBt);
        ledImage = ledFragment.findViewById(R.id.ledImage);
        ledBt.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    ledImage.setImageResource(R.mipmap.ledo);
                }
                else {
                    ledImage.setImageResource(R.mipmap.ledc);
                }
            }
        });
        super.onStart();
    }
}
