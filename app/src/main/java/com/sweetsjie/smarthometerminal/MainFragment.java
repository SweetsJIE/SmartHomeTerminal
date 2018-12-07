package com.sweetsjie.smarthometerminal;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class MainFragment extends Fragment {
    private View mainFragment;
    private TextView currentTemTv;
    private TextView weatherChineseTv;
    private TextView windDirTv;
    private TextView humTv;
    private TextView addrTv;
    private ImageView weatherImage;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mainFragment = inflater.inflate(R.layout.fragment_main,container,false);
        return mainFragment;
    }

    @Override
    public void onStart() {
        currentTemTv = mainFragment.findViewById(R.id.currentTemTv);
        weatherChineseTv = mainFragment.findViewById(R.id.weatherChineseTv);
        windDirTv = mainFragment.findViewById(R.id.windDirTv);
        humTv = mainFragment.findViewById(R.id.humTv);
        addrTv = mainFragment.findViewById(R.id.addrTv);
        weatherImage = mainFragment.findViewById(R.id.weatherImage);

        super.onStart();
    }

    public void setCurrentTemTv(String string) {
        this.currentTemTv.setText(string);
    }

    public void setWeatherChineseTv(String string) {
        this.weatherChineseTv.setText(string);
    }

    public void setWindDirTv(String string) {
        this.windDirTv .setText(string);
    }

    public void setHumTv(String string) {
        this.humTv.setText(string);
    }

    public void setAddrTv(String string) {
        this.addrTv.setText(string);
    }

    public void setWeatherImage(String string) {
        if(string.equals("阴天") || string.equals("阴"))
            weatherImage.setImageResource(R.mipmap.yintian);
        if(string.equals("晴天"))
            weatherImage.setImageResource(R.mipmap.qingtian);
    }
}
