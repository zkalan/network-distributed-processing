package http;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HttpServer{
	
	ServerSocket serverSocket;
	final static int PORT = 80;
	//线程池
	ExecutorService executorService;
	//单个处理器线程池工作线程的数量
	final static int POOL_SIZE = 6;
	
	/**
	 * 启动服务的函数
	 * @throws IOException 
	 */
	public HttpServer() throws IOException{
		
		//创建服务器套接字
		serverSocket = new ServerSocket(PORT);
		//创建线程池
		//Runtime的availableProcessors()方法返回当前系统的可用处理器数目
		//由JVM根据系统的情况来决定线程的数量
		executorService = Executors.newFixedThreadPool(Runtime.getRuntime()
				.availableProcessors() * POOL_SIZE);
		System.out.println("服务器已启动！");
	}

	/**
	 * 启动服务器的入口函数
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		
		new HttpServer().service();

	}
	
	/**
	 * 提供服务器服务的函数
	 * @return 
	 */
	public void service(){
		Socket socket = null;
		while (true) {
			try {
				socket = serverSocket.accept();
				//将处理交给线程池服务
				executorService.execute(new Handler(socket));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}

}