package acmc.heatcontrol;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import java.text.NumberFormat;

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

        mDevice = mApp.getDeviceArrayList().get(intent.getIntExtra("device_index", 0));

        final SeekBar seekBar = (SeekBar) findViewById(R.id.seleccionTemp);
        seekBar.setProgress(Math.round((mDevice.getTempSeleccionada() * 2) - 20));

        if (mDevice.isManualTemp()) {seekBar.setEnabled(true);} else {seekBar.setEnabled(false);}

        TextView nombreDevice = (TextView) findViewById(R.id.nombreTextView);
        nombreDevice.setText(mDevice.getName());

        final TextView tempSeleccionadaTextView = (TextView) findViewById(R.id.tempSeleccionada);
        tempSeleccionadaTextView.setText(NumberFormat.getInstance().format(mDevice.getTempSeleccionada()));

        final TextView tempActualTextView = (TextView) findViewById(R.id.tempActual);
        tempActualTextView.setText(NumberFormat.getInstance().format(mDevice.getTempActual()));

        //Ponemos si está encendido o no
        final TextView enabledStateTextView = (TextView) findViewById(R.id.enabledState);
        if (mDevice.getEnabledState()) {enabledStateTextView.setText(getResources().getText(R.string.on));} else {enabledStateTextView.setText(getResources().getText(R.string.off));}

        Button alternarBoton = (Button) findViewById(R.id.alternarBoton);

        alternarBoton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDevice.update();

                tempSeleccionadaTextView.setText(NumberFormat.getInstance().format(mDevice.getTempSeleccionada()));
                tempActualTextView.setText(NumberFormat.getInstance().format(mDevice.getTempActual()));
                seekBar.setProgress(Math.round((mDevice.getTempSeleccionada() * 2) - 20));
                if (mDevice.isManualTemp()) {seekBar.setEnabled(true);} else {seekBar.setEnabled(false);}
                if (mDevice.getEnabledState()) {enabledStateTextView.setText(getResources().getText(R.string.on));} else {enabledStateTextView.setText(getResources().getText(R.string.off));}
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //Le sumamos 20 (porque los incrementos son cada 0,5 y empieza en 10) para ajustarlo a la seekbar, que no tiene el atributo "min".
                tempSeleccionadaTextView.setText(NumberFormat.getInstance().format(((float) (progress + 20)) / 2));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //Le sumamos 10 para ajustarlo a la seekbar, que no tiene el atributo "min".
                mDevice.setTemp(new Float(((float) (seekBar.getProgress() + 20)) / 2));
            }
        });
    }
}
