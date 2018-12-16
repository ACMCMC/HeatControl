package acmc.heatcontrol;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.SeekBar;
import android.widget.TextView;

/**
 * Created by acmc on 16/12/2018.
 */

public class ControlDeviceActivity extends AppCompatActivity {

    Device mDevice;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        App mApp = (App) App.getAppContext();
        setContentView(R.layout.control_device_activity);
        Intent intent = getIntent();

        mDevice = mApp.getDeviceArrayList().get(intent.getIntExtra("device_index",0));

        SeekBar seekBar = (SeekBar) findViewById(R.id.seleccionTemp);

        TextView nombreDevice = (TextView) findViewById(R.id.nombreTextView);
nombreDevice.setText(mDevice.getName());

        final TextView valorSeekBar = (TextView) findViewById(R.id.valorSeekBar);


        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //Le sumamos 10 para ajustarlo a la seekbar, que no tiene el atributo "min".
                valorSeekBar.setText(Integer.toString(progress + 10));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //Le sumamos 10 para ajustarlo a la seekbar, que no tiene el atributo "min".
                mDevice.setTemp(seekBar.getProgress() + 10);
            }
        });
    }
}
