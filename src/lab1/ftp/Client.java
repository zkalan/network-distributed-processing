/**
 * My Ftp Client
 */
package lab1.ftp;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

public class Client {
	
	//TCP连接的端口
	static final int PORT = 8080;
	//udp连接的端口
	static final String uPORT = "8081";
	//连接地址
	static final String HOST = "127.0.0.1";
	//建立客户端套接字
	Socket socket = new Socket();
	
	BufferedWriter bw;
	BufferedReader br;
	PrintWriter pw;
	
	//根目录
	String currentDir = "/";
	String root = null;
	
	public Client() throws IOException{
		//也可以在此创建客户端套接字，有待试验
		//socket = new Socket(HOST,PORT);
		socket = new Socket();
		socket.connect(new InetSocketAddress(HOST,PORT));
		//建立客户端输出流，向服务器发送数据
		bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
		//建立客户端输入流，接收服务器的数据
		br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		
		//装饰输出流，即时刷新
		pw = new PrintWriter(bw,true);
	}
	
	/**
	 * send implements
	 */
	public void send(){
		try{
			System.out.print("Client />");
			//接受用户输入的信息
			BufferedReader cin = new BufferedReader(new InputStreamReader(System.in));
			//用户的输入
			String msg = null;
			//局部变量
			String recStr = null;
			String serverResp = null;
			int beginIndex = 0;
			int endIndex = 0;
			long fileLength = 0;
			//客户端回复给服务器的字符串
			String clientToServer = null;
			
			//向服务器发送UDP端口
			pw.println(uPORT);
			
			//截取服务器根目录
			String serverRoot = br.readLine();
			beginIndex = serverRoot.indexOf("[")+1;  
			endIndex = serverRoot.lastIndexOf("]");
			root = serverRoot.substring(beginIndex,endIndex);
			
			while ((msg = cin.readLine()) != null){
				//发送消息至服务器
				if (msg.equals("quit")){
					pw.println("quit");
					//退出
					//断开服务器的套接字
					socket.close();
					System.out.println("quit Success");
					break;
				} else if (createClientWrite(msg) != null) {
					//发送消息至服务器
					clientToServer = createClientWrite(msg);
					pw.println(clientToServer); 
					
					//接收服务器回复信息并且输出
					serverResp = br.readLine();
					
					//更新currentDir
					beginIndex = serverResp.indexOf("[")+1;  
					endIndex = serverResp.lastIndexOf("]");
					currentDir = serverResp.substring(beginIndex,endIndex);
					
					recStr = serverResp.substring(serverResp.indexOf("{")+1,serverResp.lastIndexOf("}"))
							.replace(root, "/")
							.replace("&sdfg45sdfgnjk4", "\n");
					System.out.println(recStr);
					
					String[] analyMsg = clientToServer.split("\\s+");
					if (analyMsg[0].equals("get")){
						if (serverResp.substring(serverResp.indexOf("{")+1,serverResp.lastIndexOf("}")).equals("-1")){
							System.out.println("file does not exist. check the input command");
						} else if (serverResp.substring(serverResp.indexOf("{")+1,serverResp.lastIndexOf("}"))
								.equals("Is not File or does not exist&sdfg45sdfgnjk4")){
						} else {
							fileLength = Integer.parseInt(serverResp.substring(serverResp.indexOf("(")+1,serverResp.lastIndexOf(")")));
							receFileByUdp(analyMsg[1],fileLength);
						}
					}
				} else {
					System.out.println("Please Input command");
				}
				//输出用户的当前的目录
				System.out.print("Client " + currentDir.replace(root, "/").replace("\\", "/") + ">");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (null != socket) {
				try {
					//断开客户端的套接字
					socket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	//用户命令格式化
	public String createClientWrite(String msg) throws SocketException {
		
		String[] customInput = msg.split("\\s+");
		StringBuilder clientWrite = new StringBuilder();
		//String regEx = "\u002E+";&& !Pattern.compile(regEx).matcher(customInput[1]).find()
		
		if (2 == customInput.length && customInput[0].equals("cd") 
				&& occurPoints(customInput[1])) {
			clientWrite.append(customInput[0]);
			clientWrite.append(" ");
			clientWrite.append(customInput[1]);
		} else if (2 == customInput.length && customInput[0].equals("get"))  {
			clientWrite.append(customInput[0]);
			clientWrite.append(" ");
			clientWrite.append(customInput[1]);
		} else if (1 == customInput.length && customInput[0].equals("cd..")){
			clientWrite.append(customInput[0]);
			clientWrite.append(" ");
			clientWrite.append("");
		} else if (1 == customInput.length && customInput[0].equals("ls")){
			clientWrite.append(customInput[0]);
			clientWrite.append(" ");
			clientWrite.append("");
		} else if (1 == customInput.length && customInput[0].equals("quit")){
			clientWrite.append("quit");
		} else {
			return null;
		}
		return clientWrite.toString();
	}

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException,UnknownHostException {
		
		new Client().send();
		
	}
	
	/**
	 * 计算字符串中的“.”的个数
	 * @param str
	 * @return
	 */
	public boolean occurPoints(String str) {
	    int pos = -2;
	    int n = 0;
	    while (pos != -1) {
	        if (pos == -2) {
	            pos = -1;
	        }
	        pos = str.indexOf(".", pos + 1);
	        if (pos != -1) {
	            n++;
	        }
	    }
	    if (str.length() == (n + occurXieGang(str))) {
	    	return false;
	    } else {
	    	return true;
	    }
	}
	
	public int occurXieGang(String str){
	    int pos = -2;
	    int n = 0;
	    while (pos != -1) {
	        if (pos == -2) {
	            pos = -1;
	        }
	        pos = str.indexOf("/", pos + 1);
	        if (pos != -1) {
	            n++;
	        }
	    }
	    return n;
	}
	
	public void receFileByUdp(String fileName,long fileLength) throws IOException {
		
		String serverReply = null;
		System.out.println("file being ready");
		System.out.println("fileLength:" + fileLength);
		DatagramSocket udpSocket;
		byte[] buffer = new byte[1024];
		long bytesReceived = 0;
		DatagramPacket receivedPacket;
		FileOutputStream fileWriter;
		try {
			udpSocket = new DatagramSocket(8081);
			fileWriter = new FileOutputStream(Class.class.getClass().getResource("/").getPath() + "\\" + fileName);
			receivedPacket = new DatagramPacket(buffer, buffer.length);
			pw.println("ok");
			while (bytesReceived < fileLength) {
				udpSocket.receive(receivedPacket);
				fileWriter.write(receivedPacket.getData(),0,receivedPacket.getLength());
				bytesReceived = bytesReceived + receivedPacket.getLength();
				pw.println("ok");
			}
			fileWriter.close();
			udpSocket.close();
			if (bytesReceived == fileLength) {
				System.out.println("File download success");
			}
			serverReply = br.readLine();
			System.out.println(serverReply.substring(serverReply.indexOf("{")+1,serverReply.lastIndexOf("}")).replace("&sdfg45sdfgnjk4", "\n"));
			//System.out.println("file download success");
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Saved as " + Class.class.getClass().getResource("/").getPath() + "/" + fileName);
	}

}
