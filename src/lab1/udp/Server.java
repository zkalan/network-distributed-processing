package lab1.udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class Server {
	
	int PORT = 8888;
	DatagramSocket socket;
	
	public Server() throws SocketException{
		socket = new DatagramSocket(PORT);
		System.out.println("服务器启动！");
	}
	
	public void server() throws IOException{
		while (true) {
			
			DatagramPacket dp = new DatagramPacket(new byte[512] , 512);
			//接收客户端信息
			socket.receive(dp);
			String msg = new String (dp.getData(),0,dp.getLength());
			//获取客户端信息
			System.out.println(dp.getAddress() + ":" + dp.getPort() + ">" +msg);
			dp.setData(("Server:" + msg).getBytes());
			socket.send(dp);
		}
	}

	public static void main(String[] args) throws SocketException, IOException {
		new Server().server();
	}

}
