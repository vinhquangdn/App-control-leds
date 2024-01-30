package luandn.dt.ck;

import androidx.appcompat.app.ActionBar;

import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import luandn.dt.ck.Interfaces.OnDeviceItemListener;

public class DeviceAdapter extends RecyclerView.Adapter<DeviceAdapter.ViewHolder>{
    ArrayList<DeviceData> deviceDataArrayList;
    OnDeviceItemListener onDeviceItemListener;


    public DeviceAdapter(ArrayList<DeviceData> deviceDataArrayList, OnDeviceItemListener onDeviceItemListener) {
        this.deviceDataArrayList = deviceDataArrayList;
        this.onDeviceItemListener = onDeviceItemListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext()); //tao layoutInflater
        View itemView = layoutInflater.inflate(R.layout.item_device, parent, false); //anh xa R.layout.item_row thanh itemView

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        //tao du lieu cho tung itemView
        holder.imageView.setImageResource(deviceDataArrayList.get(position).getPic()); //dat du lieu cho imageView tu roomData
        holder.textViewName.setText(deviceDataArrayList.get(position).getName()); //dat du lieu cho textView tu roomData
        holder.seekBar.setProgress(deviceDataArrayList.get(position).getSeekBarValue()); //dat du lieu cho seekBar tu roomData
        holder.switchCompat.setChecked(deviceDataArrayList.get(position).isSwitchChecked()); //dat du lieu cho switch tu roomData
        holder.imageView.setImageAlpha(deviceDataArrayList.get(position).getSeekBarValue() * 255 / 100);    //dat do mo cho imageView
        holder.textViewOrder.setText("No." + deviceDataArrayList.get(position).getOrder() + ":");

        // truyen holder cho ham de xu ly su kien thong qua Interface
        onDeviceItemListener.OnDeviceItemListener(holder);

    }

    @Override
    public int getItemCount() {
        return deviceDataArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        //thanh phan cua moi item
        TextView textViewName;
        ImageView imageView;
        SeekBar seekBar;
        SwitchCompat switchCompat;
        EditText editTextTime;
        Button buttonOn;
        Button buttonOff;
        TextView textViewOrder;
        TextView textViewTimer;

        int timerFlag = 0;
        CountDownTimer countDownTimer;

        //xu ly cac su kien cho itemView thong qua ViewHolder
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            //anh xa den cua bien
            textViewName = itemView.findViewById(R.id.textViewNameItemDevice);
            imageView = itemView.findViewById(R.id.imageViewItemDevice);
            seekBar = itemView.findViewById(R.id.seekBarItemDevice);
            switchCompat = itemView.findViewById(R.id.switchItemDevice);
            editTextTime = itemView.findViewById(R.id.editTextTimeItemDevice);
            buttonOn = itemView.findViewById(R.id.buttonOnItemDevice);
            buttonOff = itemView.findViewById(R.id.buttonOffItemDevice);
            textViewOrder = itemView.findViewById(R.id.textViewOrderItemDevice);
            textViewTimer = itemView.findViewById(R.id.textViewTimerItemDevice);
        }
    }
}
