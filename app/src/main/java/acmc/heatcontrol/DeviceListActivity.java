package acmc.heatcontrol;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

/**
 * Created by acmc on 16/12/2018.
 */

public class DeviceListActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private DeviceListAdapter recyclerAdapter;
    private RecyclerView.LayoutManager recyclerLayoutManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.device_list_activity);
        final App mApp = (App) getApplicationContext();

        Button botonUpdate = (Button) findViewById(R.id.list_botonUpdate);

        botonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mApp.setDeviceArrayList(BroadlinkAPI.getInstance().updateDevices());
                recyclerAdapter.setDevices(mApp.getDeviceArrayList());
            }
        });

        mRecyclerView = (RecyclerView) findViewById(R.id.list_RecyclerView);

        recyclerLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(recyclerLayoutManager);

        recyclerAdapter = new DeviceListAdapter(mApp.getDeviceArrayList(),this);
        mRecyclerView.setAdapter(recyclerAdapter);
    }
}

class DeviceListAdapter extends RecyclerView.Adapter<DeviceListAdapter.DeviceHolder> {
    private List<Device> devices;
    private Context contexto;

    DeviceListAdapter(List<Device> devices, Context contexto) {
        this.devices = devices;
        this.contexto = contexto;
    }

    public void setDevices(List<Device> devices) {
        this.devices = devices;
        notifyDataSetChanged();
    }

    @Override
    public DeviceHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(contexto).inflate(R.layout.device_list_item_layout,parent,false);
        DeviceHolder holder = new DeviceHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(DeviceHolder holder, final int position) {
        holder.nombre.setText(devices.get(position).getName());

        holder.manageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(),ControlDeviceActivity.class);
                intent.putExtra("device_index",position);
                v.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return devices.size();
    }


    static class DeviceHolder extends RecyclerView.ViewHolder {
        private TextView nombre;
        private Button manageButton;
        private RelativeLayout layoutDevice;

        DeviceHolder(View itemView) {
            super(itemView);

            manageButton = (Button) itemView.findViewById(R.id.buttonDeviceListItem);
            nombre = (TextView) itemView.findViewById(R.id.nombreDeviceListItem);
            layoutDevice = (RelativeLayout) itemView.findViewById(R.id.layoutDeviceListItem);
        }
    }

}