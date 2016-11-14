package lab1.portscan;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Scanner;

public class PortScanner {

	public static void main(String[] args) {
		
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
		@SuppressWarnings("rawtypes")
		ArrayList openPort = new ArrayList();
		openPort = PortScanner.scan(HOST,lScope,rScope);
		System.out.println("######结束扫描######");
		System.out.println("******开放端口******");
		for (int i = 0 ;i < openPort.size();i++){
			if (i%5 != 4 ){
				System.out.print(openPort.get(i) + ",");
			} else {
				System.out.println(openPort.get(i));
			}
		}
	}
	
	//扫描函数
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static ArrayList scan(String host,int lScope,int rScope){
		
		ArrayList openPort = new ArrayList();
		
		if (lScope < 0 || rScope >65535 || lScope > rScope){
			System.out.println("请检查端口范围");
			return null;
		}
		Socket socket = null;
		for (int port = lScope; port <= rScope;port++){
			try {
				//socket.setSoTimeout(1000);
				socket = new Socket(host,port);
				//socket.close();
				System.out.println("端口" + port + "：开放Socket连接！");
				openPort.add(port);
			} catch (UnknownHostException e){
				System.out.println("目标" + host + "连接失败...");
			} catch (IOException e) {
				System.out.println("端口" + port + "：连接失败...");
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
		return openPort;
		
	}

}
