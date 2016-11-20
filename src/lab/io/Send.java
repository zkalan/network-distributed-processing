package lab.io;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class Send {
	

	
	public void transFileByUdp(String filePath) throws IOException{
		DatagramSocket udpSocket;
		FileInputStream fileReader;
		
		InetAddress toAddress = InetAddress.getByName("localhost");
		int toPort = 8081;
		long currentPos = 0, bytesRead;
		
		byte[] msg = new byte[1024];
		
		try {
			udpSocket = new DatagramSocket(8079);
			udpSocket.connect(toAddress, toPort);
			
			DatagramPacket packet = new DatagramPacket(msg, msg.length);
			
			fileReader = new FileInputStream(filePath);
			
			long fileLength = fileReader.available();
			
			while (currentPos < fileLength) {
				bytesRead = fileReader.read(msg);
				udpSocket.send(packet);
				currentPos = currentPos + bytesRead;
			}
			System.out.println(currentPos);
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws IOException {
		new Send().transFileByUdp("C:\\Users\\zk\\test.txt");
	}

}
