package luandn.dt.ck;

import android.app.Dialog;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import luandn.dt.ck.Interfaces.OnRoomItemListener;
import luandn.dt.ck.Interfaces.UpdateFirebaseDevice;
import luandn.dt.ck.Interfaces.UpdateNameRoom;

public class FragmentRoom extends Fragment {
    ActionBar actionBar;
    ActionBarDrawerToggle actionBarDrawerToggle;
    FloatingActionButton fab;
    String email;

    RoomAdapter roomAdapter;
    ArrayList<RoomData> roomDataArrayList = new ArrayList<>();
    ArrayList<FragmentDevice> fragmentDeviceArrayList = new ArrayList<>();

    UpdateFirebaseDevice UpdateFirebaseDevice;
    UpdateNameRoom UpdateNameRoom;

    FirebaseFirestore db;

    TextView textViewName;
    FragmentSettings fragmentSettings;

    String name;

    int[] f = {0, 0, 0, 0};

    public FragmentRoom(ActionBar actionBar, ActionBarDrawerToggle actionBarDrawerToggle, FloatingActionButton fab, String email) {
        this.actionBar = actionBar;
        this.actionBarDrawerToggle = actionBarDrawerToggle;
        this.fab = fab;
        this.email = email;

        // truyen ham cho bien
        UpdateFirebaseDevice = FragmentRoom.this::UpdateFirebaseDevice;
        UpdateNameRoom = FragmentRoom.this::UpdateNameRoom;
        db = FirebaseFirestore.getInstance();

        // xu ly cac su kien thong qua Interface
        OnRoomItemListener onRoomItemListener = new OnRoomItemListener() {
            @Override
            public void OnRoomItemListener(RoomAdapter.ViewHolder holder) {

                    holder.itemView.setOnClickListener(v -> {
                        getParentFragmentManager().beginTransaction()
                                .replace(R.id.frameLayout, fragmentDeviceArrayList.get(holder.getAdapterPosition()))  //goi fragment
                                .addToBackStack("") // them vao stack
                                .commit();  //hien thi fragment duoc goi
                    });

                    // xu ly su kien khi longClick
                    holder.itemView.setOnLongClickListener(v -> {
                        int position = holder.getAdapterPosition(); // lay vi tri trong mang

                        db.collection(email).document("deviceInfo" + (roomDataArrayList.size() - 1)).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(actionBar.getThemedContext(), "Đã xóa phòng " + roomDataArrayList.get(position).getName(), Toast.LENGTH_SHORT).show();    //thong bao ten cua phong bi xoa
                                roomDataArrayList.remove(position);    //remove khoi roomData
                                fragmentDeviceArrayList.remove(position); //remove khoi arrFragmentRoom123
                                roomAdapter.notifyItemRemoved(position);    //thong bao cho adapter
                                UpdateData();
                            }
                        });

                        return true;
                    });

                    holder.changeIP.setOnClickListener(v -> ChangeIP(holder.getAdapterPosition()));

            }
        };

        db.collection(email).document("roomInfo").get().addOnSuccessListener(document -> {
            if (document.exists()) {
                long numRoom = document.getLong("numRoom");
                if (numRoom > 0) {
                    for (int i = 0; i < numRoom; i++) {
                        String roomName = document.getString("roomName" + i);
                        String roomIP = document.getString("roomIP" + i);
                        int roomPicInt = Math.toIntExact(document.getLong("roomPic" + i));
                        roomDataArrayList.add(new RoomData(roomPicInt, roomName, roomIP));
                        fragmentDeviceArrayList.add(new FragmentDevice(roomName, roomIP, roomPicInt, actionBar, actionBarDrawerToggle, fab, this.UpdateFirebaseDevice));
                    }

                    for (int i = 0; i < fragmentDeviceArrayList.size(); i++) {
                        fragmentDeviceArrayList.get(i).deviceDataArrayList.clear();
                        for (int a = 0; a < 19; a++) {
                            fragmentDeviceArrayList.get(i).orderDevice.set(a, 0);
                        }

                        int finalI = i;
                        db.collection(email).document("deviceInfo" + i).get().addOnSuccessListener(deviceDocument -> {
                            if(deviceDocument.exists()) {
                                int numDevice = Math.toIntExact(deviceDocument.getLong("numDevice"));
                                if (numDevice > 0) {
                                    for (int j = 0; j < numDevice; j++) {
                                        fragmentDeviceArrayList.get(finalI).deviceDataArrayList.add(new DeviceData(
                                                Math.toIntExact(deviceDocument.getLong("pic" + j)),
                                                deviceDocument.getString("name" + j),
                                                deviceDocument.getString("order" + j),
                                                Math.toIntExact(deviceDocument.getLong("seekBarValue" + j)),
                                                deviceDocument.getBoolean("switchChecked" + j)
                                        ));
                                        fragmentDeviceArrayList.get(finalI).orderDevice.set(Integer.parseInt(deviceDocument.getString("order" + j)), 0);
                                    }
                                }
                                fragmentDeviceArrayList.get(finalI).deviceAdapter.notifyDataSetChanged();
                            }
                        });
                    }

                    if (roomAdapter != null) roomAdapter.notifyDataSetChanged();
                }
            }
        });

        db.collection(email).document("userInfo").get().addOnSuccessListener(documentSnapshot -> {
            name = documentSnapshot.getString("Name");
            if(textViewName != null) {
                textViewName.setText("Hello " + name);
            }
        });

        // tao adapter
        roomAdapter = new RoomAdapter(roomDataArrayList, onRoomItemListener); //tao moi roomAdapter neu chua tao
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_room, container, false);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        // cai dat actionBar
        fab.show();
        actionBar.show();
        actionBar.setTitle("Home"); // dat tieu de cho actionBar
        actionBar.setDisplayHomeAsUpEnabled(true);  // hien thi nut home tren actionBar
        actionBarDrawerToggle.setDrawerIndicatorEnabled(true);  // dung drawer tren ActionBar
        setHasOptionsMenu(true);    // hien thi menu

        textViewName = view.findViewById(R.id.textViewNameUserRoom);
        textViewName.setText("Hello " + name);

        RecyclerView recyclerViewRoom = view.findViewById(R.id.recyclerViewRoom);   //anh xa den recyclerViewHome
        recyclerViewRoom.setHasFixedSize(true); //dat kich thuoc cua recycleView la co dinh
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);   //tao GridLayoutManager
        recyclerViewRoom.setLayoutManager(linearLayoutManager);   //truyen GridLayoutManager cho recyclerView
        recyclerViewRoom.setAdapter(roomAdapter);   //dat adapter cho recyclerView

        fab.setOnClickListener(view1 -> AddRoom());
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_fragment_room, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_fragment_room_add) {
            AddRoom();
        }

        return true;
    }



    public void AddRoom() {
        Dialog dialog = new Dialog(getContext());   //tao dialog
        dialog.setContentView(R.layout.dialog_add_room);    //truyen content cho dialog
        dialog.getWindow().setBackgroundDrawable(getContext().getDrawable(R.drawable.custom_dialog)); // custom background cho dialog
        dialog.setCanceledOnTouchOutside(false);    //khong tat dialog khi nhan ra ngoai

        //anh xa cac thanh phan trong dialog
        TextView textViewDialogAdd = dialog.findViewById(R.id.textViewDialogAddRoom);
        EditText editTextNameDialogAdd = dialog.findViewById(R.id.editTextNameDialogAddRoom);
        EditText editTextIPDialogAdd = dialog.findViewById(R.id.editTextIPDialogAddRoom);
        Button buttonYesDialogAdd = dialog.findViewById(R.id.buttonYesDialogAddRoom);
        Button buttonNoDialogAdd = dialog.findViewById(R.id.buttonNoDialogAddRoom);
        ImageView imageView1 = dialog.findViewById(R.id.imageView1);
        ImageView imageView2 = dialog.findViewById(R.id.imageView2);
        ImageView imageView3 = dialog.findViewById(R.id.imageView3);
        ImageView imageView4 = dialog.findViewById(R.id.imageView4);

        imageView1.setOnClickListener(v -> {
            if(f[0] == 1) {
                imageView1.setBackground(null);
                f[0] = 0;
            }
            else {
                imageView1.setBackground(getResources().getDrawable(R.drawable.custom_choose_picture, getResources().newTheme()));
                f[0] = 1;
            }
            imageView2.setBackground(null);
            imageView3.setBackground(null);
            imageView4.setBackground(null);
            f[1] = 0;
            f[2] = 0;
            f[3] = 0;
        });

        imageView2.setOnClickListener(v -> {
            if(f[1] == 1) {
                imageView2.setBackground(null);
                f[1] = 0;
            }
            else {
                imageView2.setBackground(getResources().getDrawable(R.drawable.custom_choose_picture, getResources().newTheme()));
                f[1] = 1;
            }
            imageView1.setBackground(null);
            imageView3.setBackground(null);
            imageView4.setBackground(null);
            f[0] = 0;
            f[2] = 0;
            f[3] = 0;
        });

        imageView3.setOnClickListener(v -> {
            if(f[2] == 1) {
                imageView3.setBackground(null);
                f[2] = 0;
            }
            else {
                imageView3.setBackground(getResources().getDrawable(R.drawable.custom_choose_picture, getResources().newTheme()));
                f[2] = 1;
            }
            imageView1.setBackground(null);
            imageView2.setBackground(null);
            imageView4.setBackground(null);
            f[0] = 0;
            f[1] = 0;
            f[3] = 0;
        });

        imageView4.setOnClickListener(v -> {
            if(f[3] == 1) {
                imageView4.setBackground(null);
                f[3] = 0;
            }
            else {
                imageView4.setBackground(getResources().getDrawable(R.drawable.custom_choose_picture, getResources().newTheme()));
                f[3] = 1;
            }
            imageView1.setBackground(null);
            imageView2.setBackground(null);
            imageView3.setBackground(null);
            f[0] = 0;
            f[1] = 0;
            f[2] = 0;
        });

        textViewDialogAdd.setText("Add room"); //hien thi tieu de

        //xu kien cho nut Khong
        //tat dialog
        buttonNoDialogAdd.setOnClickListener(v -> dialog.cancel());

        buttonYesDialogAdd.setOnClickListener(v -> {
            if (!(editTextNameDialogAdd.getText().toString().isEmpty() || editTextIPDialogAdd.getText().toString().isEmpty()) && (f[0] + f[1] + f[2] + f[3] != 0)) {
                String name = editTextNameDialogAdd.getText().toString();
                String IP = editTextIPDialogAdd.getText().toString();

                int pic;
                if(f[0] == 1) {
                    pic = R.drawable.livingroom;
                }
                else if(f[1] == 1) {
                    pic = R.drawable.kitchen;
                }
                else if(f[2] == 1) {
                    pic = R.drawable.bedroom;
                }
                else {
                    pic = R.drawable.bathroom;
                }

                // them phong vao mang
                roomDataArrayList.add(new RoomData(pic, name, IP));
                // tham fragment vao mang
                fragmentDeviceArrayList.add(new FragmentDevice(name, IP, pic, actionBar, actionBarDrawerToggle, fab, UpdateFirebaseDevice));
                // thong bao cho adapter
                roomAdapter.notifyItemInserted(roomDataArrayList.size() - 1);
                // tat dialog
                dialog.cancel();

                UpdateData();
            }
        });

        dialog.show();
    }

    public void ChangeIP(int position) {
        Dialog dialog = new Dialog(getContext());   //tao dialog
        dialog.setContentView(R.layout.dialog_infor);    //truyen content cho dialog
        dialog.getWindow().setBackgroundDrawable(actionBar.getThemedContext().getDrawable(R.drawable.custom_dialog)); // custom background cho dialog
        dialog.setCanceledOnTouchOutside(false);    //khong tat dialog khi nhan ra ngoai

        EditText editTextIP = dialog.findViewById(R.id.editTextDialogInfo);
        Button buttonYes = dialog.findViewById(R.id.buttonYesDialogInfo);
        Button buttonNo = dialog.findViewById(R.id.buttonNoDialogInfo);

        editTextIP.setText(roomDataArrayList.get(position).getIP());

        dialog.show();

        buttonYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String IP = editTextIP.getText().toString();
                roomDataArrayList.get(position).setIP(IP);
                fragmentDeviceArrayList.get(position).IP = IP;
                roomAdapter.notifyDataSetChanged();
                UpdateFirebaseRoom();

                dialog.cancel();
            }
        });

        buttonNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
    }

    public void UpdateFirebaseRoom() {
        Map<String, Object> user = new HashMap<>();
        user.put("numRoom", roomDataArrayList.size());
        for (int i = 0; i < roomDataArrayList.size(); i++) {
            user.put("roomName" + i, roomDataArrayList.get(i).getName());  // luu ten phong
            user.put("roomIP" + i, roomDataArrayList.get(i).getIP());  // luu IP phong
            user.put("roomPic" + i, roomDataArrayList.get(i).getPic());  // luu hinh phong
        }

        db.collection(email).document("roomInfo").delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                db.collection(email)
                        .document("roomInfo")
                        .set(user);
            }
        });
    }

    public void UpdateFirebaseDevice() {
        Map<String, Object> user = new HashMap<>();
        for (int i = 0; i < fragmentDeviceArrayList.size(); i++) {
            int finalI = i;
            db.collection(email).document("deviceInfo" + i).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    ArrayList<DeviceData> deviceData = fragmentDeviceArrayList.get(finalI).deviceDataArrayList;  // lay deviceData thu i
                    user.put("numDevice", deviceData.size());
                    for (int j = 0; j < deviceData.size(); j++) {
                        // luu tung thong tin cua device
                        user.put("pic" + j, deviceData.get(j).getPic());
                        user.put("name" + j, deviceData.get(j).getName());
                        user.put("order" + j, deviceData.get(j).getOrder());
                        user.put("seekBarValue" + j, deviceData.get(j).getSeekBarValue());
                        user.put("switchChecked" + j, deviceData.get(j).isSwitchChecked());
                    }

                    db.collection(email)
                            .document("deviceInfo" + finalI)
                            .set(user)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    System.out.println("DocumentSnapshot added");
                                }
                            });
                }
            });

        }
    }

    public void UpdateData() {
        UpdateFirebaseRoom();
        UpdateFirebaseDevice();
    }

    public void UpdateNameFirebase() {
        Map<String, Object> user = new HashMap<>();
        user.put("Name", name);
        db.collection(email).document("userInfo").set(user);
    }

    public void UpdateNameRoom(String name) {
        this.name = name;
        textViewName.setText(name);
        UpdateNameFirebase();
    }

    public FragmentSettings getFragmentSetting() {
        return new FragmentSettings(actionBar, fab, email, name, UpdateNameRoom);
    }
}
