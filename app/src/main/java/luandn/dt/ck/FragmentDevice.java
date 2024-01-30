package luandn.dt.ck;

import androidx.appcompat.app.ActionBar;

import android.app.Dialog;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import luandn.dt.ck.Interfaces.OnDeviceItemListener;
import luandn.dt.ck.Interfaces.UpdateFirebaseDevice;

public class FragmentDevice extends Fragment {
    String roomName;
    String IP;
    int pic;
    ActionBar actionBar;
    ActionBarDrawerToggle actionBarDrawerToggle;
    FloatingActionButton fab;
    UpdateFirebaseDevice updateFirebaseDevice;

    ArrayList<DeviceData> deviceDataArrayList = new ArrayList<>();
    ArrayList<Integer> orderDevice = new ArrayList<>();
    DeviceAdapter deviceAdapter;

    CountDownTimer countDownTimer;

    public FragmentDevice(String roomName, String ip, int pic, ActionBar actionBar, ActionBarDrawerToggle actionBarDrawerToggle, FloatingActionButton fab, UpdateFirebaseDevice updateFirebaseDevice) {
        this.roomName = roomName;
        this.IP = ip;
        this.pic = pic;
        this.actionBar = actionBar;
        this.actionBarDrawerToggle = actionBarDrawerToggle;
        this.fab = fab;
        this.updateFirebaseDevice = updateFirebaseDevice;

        for (int i = 0; i < 19; i++) {
            orderDevice.add(i, 0);
        }

        // xu ly cac su kien thong qua Interface
        OnDeviceItemListener onDeviceItemListener = holder -> {
            // neu switch duoc check
            if (deviceDataArrayList.get(holder.getAdapterPosition()).isSwitchChecked()) {
                // hien thi seekBar
                holder.seekBar.setEnabled(true);

                if (deviceDataArrayList.get(holder.getAdapterPosition()).getPic() == R.drawable.lamp) {
                    // set background cho lamp
                    holder.itemView.setBackground(actionBar.getThemedContext().getDrawable(R.drawable.device_background_lamp));
                }
                else if (deviceDataArrayList.get(holder.getAdapterPosition()).getPic() == R.drawable.fan) {
                    // set background cho fan
                    holder.itemView.setBackground(actionBar.getThemedContext().getDrawable(R.drawable.device_background_fan));
                }
            }
            else {
                // tat seekBar
                holder.seekBar.setEnabled(false);
                // set background
                holder.itemView.setBackground(actionBar.getThemedContext().getDrawable(R.drawable.device_background_off));
            }

            //xoa thiet bi khi co su kien longClick tren item
            holder.itemView.setOnLongClickListener(v -> {
                int position = holder.getAdapterPosition();
                Toast.makeText(
                        v.getContext()
                        , "Đã xóa thiết bị " + deviceDataArrayList.get(position).getName()
                        , Toast.LENGTH_SHORT
                ).show();//hien thi ten thiet bi bi xoa
                orderDevice.set(Integer.parseInt(deviceDataArrayList.get(position).getOrder()), 0);
                send(IP, deviceDataArrayList.get(position).getOrder() + "," + 0);
                deviceDataArrayList.remove(position);  //xoa thiet bi khoi deviceList
                deviceAdapter.notifyItemRemoved(position);  //thong bao cho adapter
                FragmentDevice.this.updateFirebaseDevice.UpdateFirebaseDevice();

                return true;
            });

            // xu ly su kien cho switch
            holder.switchCompat.setOnCheckedChangeListener((buttonView, isChecked) -> {
                // cap nhat trang thai cua device cho mang
                deviceDataArrayList.get(holder.getAdapterPosition()).setSwitchChecked(isChecked);

                // neu switch duoc check
                if (isChecked) {
                    // at hien thi seekBar
                    holder.seekBar.setEnabled(true);
                    if (deviceDataArrayList.get(holder.getAdapterPosition()).getPic() == R.drawable.lamp) {
                        // set background cho item lamp
                        holder.itemView.setBackground(actionBar.getThemedContext().getDrawable(R.drawable.device_background_lamp));
                    }
                    else if (deviceDataArrayList.get(holder.getAdapterPosition()).getPic() == R.drawable.fan) {
                        // set background cho item fan
                        holder.itemView.setBackground(actionBar.getThemedContext().getDrawable(R.drawable.device_background_fan));
                    }
                    send(IP, deviceDataArrayList.get(holder.getAdapterPosition()).getOrder() + "," + deviceDataArrayList.get(holder.getAdapterPosition()).getSeekBarValue());
                }
                else {
                    // hien thi seekBar
                    holder.seekBar.setEnabled(false);
                    // set background cho item
                    holder.itemView.setBackground(actionBar.getThemedContext().getDrawable(R.drawable.device_background_off));
                    send(IP, deviceDataArrayList.get(holder.getAdapterPosition()).getOrder() + "," + 0);
                }

                // cap nhat sharedPreference
                this.updateFirebaseDevice.UpdateFirebaseDevice();
            });

            // xu ly cac su kien seekBar
            holder.seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    // cap nhat trang thai seekBar cho mang
                    deviceDataArrayList.get(holder.getAdapterPosition()).setSeekBarValue(progress);
                    // cap nhat do mo cho hinh anh
                    holder.imageView.setImageAlpha(progress * 255 / 100);
                    send(IP, deviceDataArrayList.get(holder.getAdapterPosition()).getOrder() + "," + progress);

                    updateFirebaseDevice.UpdateFirebaseDevice();
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });

            holder.buttonOff.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(holder.switchCompat.isChecked()) {
                        if(holder.timerFlag == 1) {
                            holder.timerFlag = 0;
                            holder.countDownTimer.cancel();
                            holder.textViewTimer.setText("0");
                        }
                        else {
                            int time;
                            if(holder.editTextTime.getText().toString().isEmpty()) {
                                time = 0;
                            }
                            else {
                                time = Integer.parseInt(holder.editTextTime.getText().toString()) * 1000;
                            }

                            holder.editTextTime.getText().clear();
                            if(time > 0) {
                                holder.timerFlag = 1;
                                holder.countDownTimer = new CountDownTimer(time, 1000) {
                                    @Override
                                    public void onTick(long millisUntilFinished) {
                                        holder.textViewTimer.setText(millisUntilFinished / 1000 + "");
                                    }

                                    @Override
                                    public void onFinish() {
                                        holder.timerFlag = 0;
                                        holder.switchCompat.setChecked(false);
                                        deviceAdapter.notifyItemChanged(holder.getAdapterPosition());
                                        updateFirebaseDevice.UpdateFirebaseDevice();
                                    }
                                }.start();
                            }
                        }
                    }
                }
            });

            holder.buttonOn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!holder.switchCompat.isChecked()) {
                        if(holder.timerFlag == 1) {
                            holder.timerFlag = 0;
                            holder.countDownTimer.cancel();
                            holder.textViewTimer.setText("0");
                        }
                        else {
                            int time;
                            if(holder.editTextTime.getText().toString().isEmpty()) {
                                time = 0;
                            }
                            else {
                                time = Integer.parseInt(holder.editTextTime.getText().toString()) * 1000;
                            }

                            holder.editTextTime.getText().clear();
                            if(time > 0) {
                                holder.timerFlag = 1;
                                holder.countDownTimer = new CountDownTimer(time, 1000) {
                                    @Override
                                    public void onTick(long millisUntilFinished) {
                                        holder.textViewTimer.setText(millisUntilFinished / 1000 + "");
                                    }

                                    @Override
                                    public void onFinish() {
                                        holder.timerFlag = 0;
                                        holder.switchCompat.setChecked(true);
                                        deviceAdapter.notifyItemChanged(holder.getAdapterPosition());
                                        updateFirebaseDevice.UpdateFirebaseDevice();
                                    }
                                }.start();
                            }
                        }
                    }
                }
            });
        };

        // mac dinh phong co 3 thiet bi
        deviceDataArrayList.add(new DeviceData(R.drawable.lamp, "Đèn A", "0", 100, false));
        deviceDataArrayList.add(new DeviceData(R.drawable.lamp, "Đèn B", "1", 100, false));
        deviceDataArrayList.add(new DeviceData(R.drawable.lamp, "Đèn C", "2", 100, false));

        orderDevice.set(0, 1);
        orderDevice.set(1, 1);
        orderDevice.set(2, 1);

        // tao adapter
        deviceAdapter = new DeviceAdapter(deviceDataArrayList, onDeviceItemListener);    //tao moi adapter

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_device, container, false);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        // xu ly su kien cho fab
        fab.setOnClickListener(v -> AddDevice("fan"));

        actionBarDrawerToggle.setDrawerIndicatorEnabled(false); // tat chuc nang cua drawer
        actionBar.setDisplayHomeAsUpEnabled(true);  //hien thi nut home tren actionBar
        actionBar.setTitle(roomName);   //dat ten cho actionBar
        setHasOptionsMenu(true);    //hien thi menu

        //anh xa recyclerView
        ImageView imageView = view.findViewById(R.id.imageViewRoom);
        TextView textViewName = view.findViewById(R.id.textViewNameRoomInDevice);
        imageView.setImageResource(pic);
        textViewName.setText(roomName);
        RecyclerView recyclerView = view.findViewById(R.id.recyclerViewDevice); //anh xa recycleView
        recyclerView.setHasFixedSize(true); //dat kich thuoc recyclerView la co dinh

        //tao LinearLayoutManager theo huong thang dung
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);   //tao gridLayoutManager
        recyclerView.setLayoutManager(linearLayoutManager); //dat setLayoutManager cho recyclerView

        recyclerView.setAdapter(deviceAdapter); //dat adapter cho recyclerView

        TextView textViewTemp = view.findViewById(R.id.textViewTempRoom);
        TextView textViewHumi = view.findViewById(R.id.textViewHumiRoom);
        textViewTemp.setOnClickListener(v -> {
            send(IP, "100, 0");
            try {
                UDP_Receive receive = new UDP_Receive();
                receive.execute(textViewHumi, textViewTemp);
            }
            catch (Exception e) {

            }
        });

        send(IP, "100, 0");
        UDP_Receive receive = new UDP_Receive();
        receive.execute(textViewHumi, textViewTemp);

        if(countDownTimer != null) {
            countDownTimer.cancel();
        }

        countDownTimer = new CountDownTimer(180000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                send(IP, "100, 0");
                UDP_Receive receive = new UDP_Receive();
                receive.execute(textViewTemp, textViewHumi);
                this.start();
            }
        }.start();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_fragment_device, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // dua vao item duoc chon ma truyen tham so tuong ung
        if (item.getItemId() == R.id.menu_fragment_device_add_lamp) {
            AddDevice("lamp");
        }

        if (item.getItemId() == R.id.menu_fragment_device_add_fan) {
            AddDevice("fan");
        }

        if (item.getItemId() == android.R.id.home) {
            getParentFragmentManager().popBackStack();
        }

        return true;
    }

    public void AddDevice(String name) {
        Dialog dialog = new Dialog(getContext());   //tao dialog
        dialog.setContentView(R.layout.dialog_add);    //truyen content cho dialog
        dialog.getWindow().setBackgroundDrawable(getActivity().getDrawable(R.drawable.custom_dialog)); // custom background cho dialog
        dialog.setCanceledOnTouchOutside(false);    //khong tat dialog khi nhan ra ngoai

        //anh xa cac thanh phan trong dialog
        TextView textViewDialogAdd = dialog.findViewById(R.id.textViewDialogAdd);
        EditText editTextNameDialogAdd = dialog.findViewById(R.id.editTextNameDialogAdd);
        EditText editTextOrderDialogAdd = dialog.findViewById(R.id.editTextOrderDialogAdd);
        Button buttonYesDialogAdd = dialog.findViewById(R.id.buttonYesDialogAdd);
        Button buttonNoDialogAdd = dialog.findViewById(R.id.buttonNoDialogAdd);

        textViewDialogAdd.setText("Add " + name); //hien thi tieu de

        //xu kien cho nut Khong
        //tat dialog
        buttonNoDialogAdd.setOnClickListener(v -> dialog.cancel());

        buttonYesDialogAdd.setOnClickListener(v -> {
            if (!(editTextNameDialogAdd.getText().toString().isEmpty() || editTextOrderDialogAdd.getText().toString().isEmpty())) {
                if(orderDevice.get(Integer.parseInt(editTextOrderDialogAdd.getText().toString())) == 0) {
                    // them phong
                    if (name.equals("lamp")) {
                        // them lamp vao mang
                        deviceDataArrayList.add(new DeviceData(R.drawable.lamp, editTextNameDialogAdd.getText().toString(), editTextOrderDialogAdd.getText().toString(), 100, false));
                    }
                    else if (name.equals("fan")) {
                        // them device vao mang
                        deviceDataArrayList.add(new DeviceData(R.drawable.fan, editTextNameDialogAdd.getText().toString(), editTextOrderDialogAdd.getText().toString(), 100, false));
                    }

                    orderDevice.set(Integer.parseInt(editTextOrderDialogAdd.getText().toString()), 1);
                    // thong bao cho adapter
                    deviceAdapter.notifyItemInserted(deviceDataArrayList.size() - 1);
                    // cap nhat sharedPreference
                    updateFirebaseDevice.UpdateFirebaseDevice();

                    // tat dialog
                    dialog.cancel();
                }
                else {
                    Toast.makeText(getContext(), "This order is duplicated", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // hien thi dialog
        dialog.show();
    }

    // cau truc: "thu tu, muc do"
    public void send(String IP, String message) {
        String serverPort = "8888"; // Replace with the server's port

        try {
            UDP_Send client = new UDP_Send();
            client.execute(message, serverPort, IP);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
