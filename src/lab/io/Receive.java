package lab.io;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class Receive {
	
	DatagramSocket udpSocket = null;
	DatagramPacket receivedPacket;
	byte[] buffer = new byte[1024];
	
	
	public void receFileByUdp(String fileName) throws SocketException {
		long bytesReceived = 0;
		udpSocket = new DatagramSocket(8081);
		DatagramPacket packet = null;
		try {
			FileOutputStream fileWriter = new FileOutputStream("C:\\Users\\zk\\Desktop\\test.txt");
			while (bytesReceived < 20) {
				
				receivedPacket = receivePacket();
				fileWriter.write(receivedPacket.getData(),0,receivedPacket.getLength());
				bytesReceived = bytesReceived + receivedPacket.getLength();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	public DatagramPacket receivePacket() throws IOException{
		DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
		udpSocket.receive(packet);
		return packet;
	}

	public static void main(String[] args) throws SocketException {
		new Receive().receFileByUdp("123");
	}

}
