package lab1.multthreadscanner;

import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PortScanner {
	
	//线程池
	ExecutorService executorService;
	//单个处理器线程池工作线程的数量
	final static int POOL_SIZE = 4;
	
	public PortScanner()throws IOException{
		//创建线程池
		//Runtime的availableProcessors()方法返回当前系统的可用处理器数目
		//由JVM根据系统的情况来决定线程的数量
		executorService = Executors.newFixedThreadPool(Runtime.getRuntime()
				.availableProcessors() * POOL_SIZE);
		
	}

	public static void main(String[] args) throws IOException {
		
		
		@SuppressWarnings("resource")
		Scanner sc = new Scanner(System.in);
		//扫描的目标主机
		System.out.print("输入目标主机地址>");
		String HOST = sc.nextLine();
		//扫描的开始端口
		System.out.print("输入开始端口>");
		int lScope = sc.nextInt();
		//扫描的结束端口
		System.out.print("输入结束端口>");
		int rScope = sc.nextInt();
		//开始扫描
		System.out.println("######开始扫描######");
		for (int i = 0 ; i < 4 ; i ++ ){
			new PortScanner().executorService.execute(new Scan(HOST,lScope,rScope));
		}
	}
	


}
