package lab1.multscan;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Scanner;

public class PortScanner implements Runnable{
	String HOST = null;
	int lScope;
	int rScope;
	@SuppressWarnings("rawtypes")
	ArrayList openPort = new ArrayList();
	
	
	public PortScanner(String HOST,int lScope,int rScope){
		this.HOST = HOST;
		this.lScope = lScope;
		this.rScope = rScope;
		
	}
	
	public void listOpenPort(){
		for (int i = 0 ;i < openPort.size();i++){
			if (i%5 != 4 ){
				System.out.print(openPort.get(i) + ",");
			} else {
				System.out.println(openPort.get(i));
			}
		}
		System.out.println("######结束扫描######");
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
		PortScanner p1 = new PortScanner(HOST,lScope,rScope);
		Thread t1 = new Thread(p1);
		t1.start();
		Thread t2 = new Thread(p1);
		t2.start();
		Thread t3 = new Thread(p1);
		t3.start();
		Thread t4 = new Thread(p1);
		t4.start();
	}
	
	@SuppressWarnings("unchecked")
	public void run(){
		//扫描函数
		Thread thread = Thread.currentThread();
			
		if (lScope < 0 || rScope >65535 || lScope > rScope){
			System.out.println("请检查端口范围");
			return ;
		}
		Socket socket = null;
		for (; lScope <= rScope;){
			try {
				lScope++;
				int port = lScope - 1;
				//socket.setSoTimeout(1000);
				socket = new Socket(HOST,port);
				//socket.close();
				System.out.println("线程" + thread.getId() + "：端口" + port + "：开放Socket连接！");
				openPort.add(port);
			} catch (UnknownHostException e){
				System.out.println("线程" + thread.getId() + "：目标" + HOST + "连接失败...");
			} catch (IOException e) {
				System.out.println("线程" + thread.getId() + "：端口" + lScope + "：连接失败...");
			} finally {
				if (null != socket){
					try {
						socket.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		System.out.println("******" + "线程" + thread.getId() + "结果******");
		listOpenPort();
	}
	


}
