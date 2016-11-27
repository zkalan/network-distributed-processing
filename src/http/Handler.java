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
				/**
				 * 解析并获得String[]格式的报文头部
				 */
				head = responseServer.getRequestTypeContent();
				/**
				 * 输出报文首部行
				 */
				System.out.println(head[0]+ " " + head[1] + " " + head[2]);
				/**
				 * 处理GET请求
				 */
				if (head[0].equals("GET")) {
					responseServer.staticResourceResponse(responseServer.getURL(head[1]));
				} else if (head[0].equals("PUT")) {//处理PUT请求
					for (int i = 0;i < head.length; i++) {
						if (head[i].equals("Content-Length:")) {
							/**
							 * 提交内容的长度
							 */
							responseServer.putResourceResponse(head[1],head[i+1]);
							break;
						}
					}
				} else if (head[0].equals("POST")){//处理POST请求
					for (int i = 0;i < head.length; i++) {
						if (head[i].equals("Content-Length:")) {
							/**
							 * 传递提交内容的长度
							 */
							responseServer.processPOSTResponse(head[i+1]);
							i = head.length;
						}
					}
					/**
					 * 返回POST请求成功的内容页面
					 * success.html
					 */
					responseServer.sendSuccess();
					
				} else {
					/**
					 * 返回不支持请求的405错误
					 */
					responseServer.notAllowedMethod();
				}
				/**
				 * 清除线程
				 * 关闭istream
				 * 关闭ostream
				 * 关闭socket连接
				 */
				responseServer.close();
			//}
			} catch (IOException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
	
	}
}