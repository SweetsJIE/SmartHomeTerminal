package com.sweetsjie.smarthometerminal;

import android.annotation.SuppressLint;
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
import android.widget.TextView;
import android.widget.ToggleButton;

import org.w3c.dom.Text;

public class LedFragment extends Fragment {

    private ToggleButton ledBt;
    private ImageView ledImage;
    private View ledFragment;
    private Button ledOpenBt;
    private Button ledCloseBt;
    private TextView dayEt;
    private TextView monthEt;
    private TextView hourEt;
    private TextView minuteEt;
    private  String time;

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
        ledOpenBt = ledFragment.findViewById(R.id.ledOpenBt);
        ledCloseBt = ledFragment.findViewById(R.id.ledCloseBt);
        monthEt = ledFragment.findViewById(R.id.monthEt);
        dayEt = ledFragment.findViewById(R.id.dayEt);
        hourEt = ledFragment.findViewById(R.id.hourEt);
        minuteEt = ledFragment.findViewById(R.id.minuteEt);

        ledOpenBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                time = monthEt.getText().toString() + "-" + dayEt.getText().toString() +  "-" + hourEt.getText().toString() +  "-" + minuteEt.getText().toString() + "-" + "O";
                ((MainActivity)getActivity()).sendCmd(time);
            }
        });

        ledCloseBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                time = monthEt.getText().toString() + "-" + dayEt.getText().toString() +  "-" + hourEt.getText().toString() +  "-" + minuteEt.getText().toString() + "-" + "C";
                ((MainActivity)getActivity()).sendCmd(time);
            }
        });

        ledBt.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    ledImage.setImageResource(R.mipmap.ledo);
                    ((MainActivity)getActivity()).sendCmd("open");
                    ledBt.setBackground(getResources().getDrawable(R.drawable.button_true_shape));
                }
                else {
                    ledImage.setImageResource(R.mipmap.ledc);
                    ((MainActivity)getActivity()).sendCmd("close");
                    ledBt.setBackground(getResources().getDrawable(R.drawable.button_false_shape));
                }
            }
        });
        super.onStart();
    }
}
