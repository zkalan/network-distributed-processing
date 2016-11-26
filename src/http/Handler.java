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
	String serverRoot = "D:\\myServer";
	
	static private String CRLF = "\r\n";
	
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
				System.out.println(head[0]+ " " + head[1] + " " + head[2]);
				if (head[0].equals("GET")) {
					responseServer.staticResourceResponse(responseServer.getURL(head[1]));
				} else if (head[0].equals("PUT")) {
					for (int i = 0;i < head.length; i++) {
						if (head[i].equals("Content-Length:")) {
							responseServer.putResourceResponse(head[1],head[i+1]);
							break;
						}
					}
				} else if (head[0].equals("POST")){
					for (int i = 0;i < head.length; i++) {
						if (head[i].equals("Content-Length:")) {
							responseServer.processPOSTResponse(head[i+1]);
							i = head.length;
						}
					}
					responseServer.sendSuccess();
					
				} else {
					responseServer.notAllowedMethod();
				}
				
				responseServer.close();
			//}
			} catch (IOException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
	
	}
}