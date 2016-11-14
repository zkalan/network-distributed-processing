/**
 * 这是服务器工作线程
 * 2016年11月13日
 */
package lab1.multthread;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * 单个进程负责与单个用户通信
 * @author Administrator
 *
 */
public class Handler implements Runnable {
	
	private Socket socket;
	
	BufferedWriter bw;
	BufferedReader br;
	PrintWriter pw;
	
	/**
	 * 初始化
	 * @param socket
	 */
	public Handler(Socket socket){
		this.socket = socket;
	}
	
	/**
	 * 初始化输入输出流
	 * @throws IOException
	 */
	public void initStream() throws IOException{
		br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
		pw = new PrintWriter(bw, true);
	}
	
	public void run(){
		try {
			System.out.println("成功连接！连接地址：" + socket.getInetAddress() + ":" + socket.getPort());
			//初始化输入输出流的对象
			initStream();
			String info = null;
			while (null != (info = br.readLine())){
				System.out.println("Client@" + socket.getInetAddress() + ":" + socket.getPort() + ">" + info);
				pw.println( info );
				if (info.equals("quit")){
					break;
				}
			}
		}catch (IOException e){
			e.printStackTrace();
		}finally {
			if (null != socket){
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
