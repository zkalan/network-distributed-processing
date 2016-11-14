	package lab1.udp;
	
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketException;

public class Client {
	//定义链接的服务器
	int PORT = 8888;
	String HOST = "127.0.0.1";
	
	DatagramSocket socket;
	
	public Client() throws SocketException{
		//随机可用端口，又称为匿名端口
		socket = new DatagramSocket();
	}

	public static void main(String[] args) throws SocketException, IOException {
		new Client().send();
		
	}
	
	public void send() throws IOException {
		BufferedReader cin = new BufferedReader(new InputStreamReader(System.in));
	SocketAddress socketAddress = new InetSocketAddress(HOST , PORT);
	//服务器端地址
	while (true) {
		String msg = cin.readLine();
		byte[] info = msg.getBytes();
		//创建数据包，指定服务器地址
		DatagramPacket dp = new DatagramPacket(info,info.length,socketAddress);
		//向服务器发送数据包
		socket.send(dp);
		DatagramPacket inputDp = new DatagramPacket(new byte[512],512);
		//接收服务器返回的数据
		socket.receive(inputDp);
		String recMsg = new String (inputDp.getData(),0,inputDp.getLength());
		System.out.println(recMsg);
		if (recMsg.equals("quit")) {
			break;
		}
	}
	socket.close();
	}
	
	

}
