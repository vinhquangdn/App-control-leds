package luandn.dt.ck;

import android.os.AsyncTask;
import android.widget.TextView;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketAddress;

public class UDP_Receive extends AsyncTask<TextView, Void, Void> {
    private DatagramSocket socket;
    private String message = "";

    @Override
    protected Void doInBackground(TextView... params) {
        try {
            socket = new DatagramSocket(8888);
            byte[] buffer = new byte[1024];

            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            socket.receive(packet);
            String receivedData = new String(packet.getData(), 0, packet.getLength());
            System.out.println(receivedData);
            int length = receivedData.length();
            if(length != 0) {
                message = receivedData;
                String temp = "", humi;
                int i, j = 0;
                for(i = 0; i < message.length(); i++)
                {
                    if(message.charAt(i) == ';')
                    {
                        temp = message.substring(0, i);
                        j = i + 1;
                    }
                }
                humi = message.substring(j, i);
                params[0].setText(temp);
                params[1].setText(humi);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            if (socket != null) {
                socket.close();
            }
        }

        return null;
    }
}
