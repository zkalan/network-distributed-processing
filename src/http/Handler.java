package http;

import java.io.IOException;

/**
 * 这是服务器工作线程
 * 2016年11月21日
 */

import java.net.Socket;

/**
 * 单个进程负责与单个用户通信
 * @author Administrator
 *
 */
public class Handler implements Runnable {
	
	private Socket socket;
	String serverRoot = "D:\\pavo\\www";
	
	/**
	 * 初始化
	 * @param socket
	 */
	public Handler(Socket socket){
		this.socket = socket;
	}
	
	public void run(){
			//输出提示信息
			System.out.println("成功连接！连接地址：" + socket.getInetAddress() + ":" + socket.getPort());
			
			try {
				Response responseServer = new Response(socket);
				String[] head = null;
			//while (true){
				
				head = responseServer.getRequestTypeContent();
				System.out.println(head[0]+ "--------" + head[1]);
				responseServer.staticResourceResponse(responseServer.getURL(head[1]));
				
				responseServer.close();
			//}
			} catch (IOException e) {
				e.printStackTrace();
			}
	
	}
}