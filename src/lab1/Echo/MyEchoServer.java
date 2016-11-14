/**
 * 2016年11月13日
 * 《网络与分布计算》
 * 第一次实验课
 */
package lab1.Echo;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
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
		Socket socket = null;
		/**
		 * 服务器本身就是一个循环结构
		 */
		while (true){
			try {
				//服务器处于阻塞状态，等待客户端请求创建socket连接实例
				socket = serverSocket.accept();
				//输出客户端连接成功的信息到控制台和客户端
				System.out.println("成功连接！连接地址：" + socket.getInetAddress() + ":" + socket.getPort());
				//输出流，向客户端发送信息
				BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
				//输入流，接受客户端发送的数据
				BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				//装饰输出流。每写一行就刷新缓冲区，不需要flush
				PrintWriter pw = new PrintWriter(bw,true);
				//接收用户输入的信息
				String info = null;
				//通过循环等待用户的每一次输入
				while ((info = br.readLine()) != null){
					//输出用户发送的信息到服务器控制台
					System.out.println("Client:" + socket.getInetAddress() + ":" + info);
					//输出用户的数据到装饰输出流
					//println输出完后会自动刷新缓冲区
					pw.println("Server>" + info );
					if (info.equals("quit")){
						break;
					}
				}
			}
			//如果服务器中断套接字，应该捕获异常，中断内层的while循环
			//这样不影响服务器与其他客户端通信
			catch (IOException e) {
				e.printStackTrace();
			}finally {
				if (null != socket) {
					try {
						//断开连接
						socket.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
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
