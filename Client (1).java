/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;
import java.util.Scanner;

public class ClientCopy {
    Socket Socket = null;
    SocketChannel ClientSocket;
    DataInputStream datainput = null;
    OutputStream output = null;
    InputStream input = null;
    FileChannel FileChannel;
    static String Path = "D:\\os\\filedown\\1769.mkv";
    ByteBuffer Buffer;
    String str = "";

    public static void main(String[] args) {
        System.out.println("open desktop Client");
        System.out.println("pls input portnumber ");
        Scanner sc = new Scanner(System.in);
        int x = sc.nextInt();
        ClientCopy channel = new ClientCopy(x);
    }

    ClientCopy(int x) {
        try {
            SocketAddress ClientAddress = new InetSocketAddress("172.20.10.9", x);
            ClientSocket = SocketChannel.open();//เปิดport ip
            ClientSocket.connect(ClientAddress);//เอาcnไปเชื่อมต่อกับsv
            System.out.println("ConnectedZero");
            FileChannel = new FileOutputStream(Path).getChannel();//
            zeroCopy();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("clientZero");
        }
    }

    public void zeroCopy() {
        try {
            Buffer = ByteBuffer.wrap(new byte[4096]);
            ClientSocket.read(Buffer);//อ่าน
            Buffer.flip();
            long Size = Buffer.getLong();
            
            System.out.println("Size File:" + Size+" byte");
            
            long TranferFrom = 0;
            
            System.out.println("Download:");
            //zerocopy//fromรับมา//toส่งไป
            TranferFrom = FileChannel.transferFrom(ClientSocket, 0, Size);//ส่งไปเอาข้อมูลตั้ง0ถึงขนาดของมัน
            
            System.out.println("finish");
            
            
            ClientSocket.close();//ปิด
            FileChannel.close();
            Buffer.clear();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("clientzero");
        }
    }

}
