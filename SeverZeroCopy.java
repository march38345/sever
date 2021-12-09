/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class SeverZeroCopy {

    public static void main(String[] args) {
        Thread ServerZero = new ServerZC();
        ServerZero.start();
    }
}

class ServerZC extends Thread {

    ServerSocketChannel sever_socket = null;
    //static String path = "D:/ServerTest/os.mkv";
    static String path = "D:\\server\\movie.mkv";
    ByteBuffer Buffer;
    FileChannel file_ch;
    long Size;
    public static final int PORT = 1998; //พอร์ต

    ServerZC() {
        InetSocketAddress ServerAddress = new InetSocketAddress(PORT); //ประกาศ InetSocketAddress
        try {
            file_ch = new FileInputStream(path).getChannel(); //FileChannel ไฟล์ขาเข้า
            Size = file_ch.size(); //Size = ขนาดไฟล์ของFileChannel
            sever_socket = ServerSocketChannel.open(); // สั่งเปิด ServerSocketChannel
            ServerSocket Server_Socket = sever_socket.socket();
            Server_Socket.bind(ServerAddress);
            System.out.println("---Open Sever---");
            System.out.println("ServerSocket Address:" + PORT);

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("sever-error");
        }
    }

    @Override
    public void run() {
        try {
            while (true) {
                SocketChannel Soc_Ch = sever_socket.accept(); // ยอมรับ SocketChannel
                System.out.println("\nClient Coppy_Zero");
                Buffer = ByteBuffer.allocate(4096);
                Buffer.asLongBuffer().put(Size);
                Soc_Ch.write(Buffer);
                System.out.println("size " + Size + " byte");//
                Thread t = new Do_ZeroCopy(Size, file_ch, Soc_Ch); //ประกาศเทรด
                t.start(); //รันเทรด

            }

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("sever-error");
        }
    }

    class Do_ZeroCopy extends Thread {
        long Size; //ขนาดไฟล์
        FileChannel file_ch; //ไฟล์
        SocketChannel Soc_Chl; //พอร์ต

        Do_ZeroCopy(long Size, FileChannel FileChannel, SocketChannel Soc_Chl) {
            this.Size = Size;
            this.file_ch = FileChannel;
            this.Soc_Chl = Soc_Chl;
        }
        public void run() {
            try {
                double size = (double) Size;
                long TN = 0;
                while (TN < Size) { 
                    long tranferTo = file_ch.transferTo(TN, Size - TN, Soc_Chl); //transferTo(...,ขนาด - tranIN,พอร์ต)
                    if (tranferTo <= 0) {
                        break;
                    }
                    TN += tranferTo; 
                  
                    
                        percent(TN,size);
                
                    
                    
                    }
                System.out.println("finish    ");
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Sever");
            }

        }
        void percent(long TN,double size){
        
        System.out.printf(" Size: %.2f ", (TN / size) * 100);
        System.out.println("%");
        
        }
        
    }
}
