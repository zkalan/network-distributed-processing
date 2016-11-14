package lab1.multthreadscanner;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class Scan implements Runnable {
	
	String HOST = null;
	int lScope = 0;
	int rScope = 0;
	
	public Scan(String HOST,int lScope,int rScope){
		this.HOST = HOST;
		this.lScope = lScope;
		this.rScope = rScope;
	}
	
	@SuppressWarnings("unchecked")
	public void run(){
		//扫描函数
			
		Thread thread = Thread.currentThread();
			@SuppressWarnings("rawtypes")
			ArrayList openPort = new ArrayList();
			
			if (lScope < 0 || rScope >65535 || lScope > rScope){
				System.out.println("请检查端口范围");
				return ;
			}
			Socket socket = null;
			for (; lScope <= rScope;lScope++){
				try {
					//socket.setSoTimeout(1000);
					socket = new Socket(HOST,lScope);
					//socket.close();
					System.out.println("线程" + thread.getId() + "：端口" + lScope + "：开放Socket连接！");
					openPort.add(lScope);
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
			System.out.println("******开放端口******");
			for (int i = 0 ;i < openPort.size();i++){
				if (i%5 != 4 ){
					System.out.print(openPort.get(i) + ",");
				} else {
					System.out.println(openPort.get(i));
				}
			}
			System.out.println("######结束扫描######");
			
	}

}
