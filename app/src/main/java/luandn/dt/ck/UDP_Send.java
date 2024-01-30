package luandn.dt.ck;

import android.os.AsyncTask;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UDP_Send extends AsyncTask<String, Void, Void> {

    @Override
    protected Void doInBackground(String... params) {
        try {
            String message = params[0];
            int serverPort = Integer.parseInt(params[1]); // Port to which you want to send the data
            InetAddress serverAddress = InetAddress.getByName(params[2]); // Destination IP address

            DatagramSocket socket = new DatagramSocket();
            byte[] sendData = message.getBytes();
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, serverAddress, 8888);
            socket.send(sendPacket);
            System.out.println("Send success!");
            socket.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}

