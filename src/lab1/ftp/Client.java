/**
 * My Echo Client
 */
package lab1.ftp;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client {
	
	//连接的端口
	static final int PORT = 8080;
	//连接地址
	static final String HOST = "127.0.0.1";
	//建立客户端套接字
	Socket socket = new Socket();
	
	public Client() throws IOException{
		//也可以在此创建客户端套接字，有待试验
		//socket = new Socket(HOST,PORT);
		socket = new Socket();
		socket.connect(new InetSocketAddress(HOST,PORT));
	}
	
	/**
	 * send implements
	 */
	public void send(){
		try{
			//建立客户端输出流，向服务器发送数据
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			//建立客户端输入流，接收服务器的数据
			BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			
			//装饰输出流，即时刷新
			PrintWriter pw = new PrintWriter(bw,true);
			//接受用户输入的信息
			//@SuppressWarnings("resource")
			//Scanner cin = new Scanner(System.in);
			BufferedReader cin = new BufferedReader(new InputStreamReader(System.in));
			//用户的输入
			String msg = null;
			while ((msg = cin.readLine()) != null){
				//发送消息至服务器
				//String[] gsh = msg.split(" ");
				pw.println(msg);
				//接收服务器反馈的消息
				if (msg.equals("quit")){
					//退出
					break;
				}
				String recStr = null;
				recStr = br.readLine().replace("\123", "\n");
				//while ((recStr = br.readLine()) != null){
					System.out.println(recStr);
				//} 
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

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException,UnknownHostException {
		
		new MyEchoClient().send();
		
	}

}
