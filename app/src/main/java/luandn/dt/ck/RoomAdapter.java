package luandn.dt.ck;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import luandn.dt.ck.Interfaces.OnRoomItemListener;

public class RoomAdapter extends RecyclerView.Adapter<RoomAdapter.ViewHolder> {
    ArrayList<RoomData> roomDataArrayList;
    OnRoomItemListener onRoomItemListener;

    public RoomAdapter(ArrayList<RoomData> roomDataArrayList, OnRoomItemListener onRoomItemListener) {
        this.roomDataArrayList = roomDataArrayList;
        this.onRoomItemListener = onRoomItemListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext()); //tao layoutInflater
        View itemView = layoutInflater.inflate(R.layout.item_room, parent, false); //anh xa R.layout.item_row thanh itemView

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        //tao du lieu cho tung itemView
        holder.textViewItemRoom.setText(roomDataArrayList.get(position).getName());  //dat du lieu cho textView tu roomData
        holder.imageViewItemRoom.setImageResource(roomDataArrayList.get(position).getPic()); //dat du lieu cho imageView tu roomData

        // truyen holder cho ham de xu ly su kien thong qua Interface
        onRoomItemListener.OnRoomItemListener(holder);
    }

    @Override
    public int getItemCount() {
        return roomDataArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        //thanh phan cua moi item
        TextView textViewItemRoom;
        ImageView imageViewItemRoom;
        ImageView changeIP;

        //xu ly cac su kien cho itemView thong qua ViewHolder
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            //anh xa cac bien cua itemView
            textViewItemRoom = itemView.findViewById(R.id.textViewItemRoom);
            imageViewItemRoom = itemView.findViewById(R.id.imageViewItemRoom);
            changeIP = itemView.findViewById(R.id.changeIP);
        }
    }
}
