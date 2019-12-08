package com.example.kilianovski_pz2_colorpicker;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = MainActivity.class.getSimpleName();

    private SeekBar redSeekbar;
    private SeekBar greenSeekbar;
    private SeekBar blueSeekbar;

    private View colorView;

    private int redState = 0;
    private  int greenState = 0;
    private int blueState = 0;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        redSeekbar = findViewById(R.id.red_seekbar);
        greenSeekbar = findViewById(R.id.green_seekbar);
        blueSeekbar = findViewById(R.id.blue_seekbar);
        colorView = findViewById(R.id.view_color);
        onStateChanged();
        redSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                redState = i;
                onStateChanged();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        greenSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                greenState = i;
                onStateChanged();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        blueSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                blueState = i;
                onStateChanged();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void onStateChanged() {
        colorView.setBackgroundColor(getHexColorState());
    }

    private int getHexColorState() {
        String hex = "%02X";
        String res = "#" + String.format(
                hex,
                redState
        ) + String.format(
                hex,
                greenState
        ) + String.format(
                hex,
                blueState
        );

        return Color.parseColor(res);
    }
}
