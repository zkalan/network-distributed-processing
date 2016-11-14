/**
 * 2016年11月13日
 * 《网络与分布计算》
 * 第一次实验课
 */
package ftp;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class MyEchoServer {
	
	ServerSocket serverSocket;
	
	static final int PORT = 8080;
	
	/**
	 * 服务器的启动程序
	 * @throws IOException
	 */
	public MyEchoServer() throws IOException{
		
		InetAddress ip = InetAddress.getLocalHost();
		//创建服务器套接字
		serverSocket = new ServerSocket(PORT,2);
		//设置连接的超时时间
		//serverSocket.setSoTimeout(5000);
		System.out.println("Server:" + ip.getHostAddress() + " " +PORT + " 启动成功");
	}
	
	public void service(){
		while (true){
			Socket socket = null;
			try {
				socket = serverSocket.accept();
				Thread work = new Thread(new Handler(socket));
				//为客户端连接创建工作线程
				work.start();
			} catch (IOException e){
				e.printStackTrace();
			}
		}
	}

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		
		new MyEchoServer().service();
	}

}
