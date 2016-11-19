/**
 * My Ftp Client
 */
package lab1.ftp;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client {
	
	//连接的端口
	static final int PORT = 8080;
	//连接地址
	static final String HOST = "127.0.0.1";
	//建立客户端套接字
	Socket socket = new Socket();
	//根目录
	String currentDir = "/";
	String root = null;
	
	public Client() throws IOException{
		//也可以在此创建客户端套接字，有待试验
		//socket = new Socket(HOST,PORT);
		socket = new Socket();
		socket.connect(new InetSocketAddress(HOST,PORT));
	}
	
	/**
	 * send implements
	 */
	public void send(){
		try{
			System.out.print("Client />");
			//建立客户端输出流，向服务器发送数据
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			//建立客户端输入流，接收服务器的数据
			BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			
			//装饰输出流，即时刷新
			PrintWriter pw = new PrintWriter(bw,true);
			//接受用户输入的信息
			BufferedReader cin = new BufferedReader(new InputStreamReader(System.in));
			//用户的输入
			String msg = null;
			//局部变量
			String recStr = null;
			String serverResp = null;
			int beginIndex = 0;
			int endIndex = 0;
			
			//截取服务器根目录
			String serverRoot = br.readLine();
			beginIndex = serverRoot.indexOf("[")+1;  
			endIndex = serverRoot.lastIndexOf("]");
			root = serverRoot.substring(beginIndex,endIndex);
			
			while ((msg = cin.readLine()) != null){
				//发送消息至服务器
				if (msg.equals("quit")){
					pw.println("quit");
					//退出
					//断开服务器的套接字
					socket.close();
					System.out.println("quit Success");
					break;
				} else if (createClientWrite(msg) != null) {
					//发送消息至服务器
					pw.println(createClientWrite(msg)); 
					
					//接收服务器回复信息并且输出
					serverResp = br.readLine();
					
					//更新currentDir
					beginIndex = serverResp.indexOf("[")+1;  
					endIndex = serverResp.lastIndexOf("]");
					currentDir = serverResp.substring(beginIndex,endIndex);
					
					
					recStr = serverResp.substring(serverResp.indexOf("{")+1,serverResp.lastIndexOf("}"))
							.replace(root, "/")
							.replace("&sdfg45sdfgnjk4", "\n");
					System.out.println(recStr);
				} else {
					System.out.println("Please check the Input command");
				}
				System.out.print("Client " + currentDir.replace(root, "/").replace("\\", "/") + ">");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (null != socket) {
				try {
					//断开客户端的套接字
					socket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	//用户命令格式化
	public String createClientWrite(String msg){
		
		String[] customInput = msg.split("\\s+");
		StringBuilder clientWrite = new StringBuilder();
		//String regEx = "\u002E+";&& !Pattern.compile(regEx).matcher(customInput[1]).find()
		
		if (2 == customInput.length && customInput[0].equals("cd") 
				&& occurPoints(customInput[1])) {
			clientWrite.append(customInput[0]);
			clientWrite.append(" ");
			clientWrite.append(customInput[1]);
		} else if (2 == customInput.length && customInput[0].equals("get"))  {
			clientWrite.append(customInput[0]);
			clientWrite.append(" ");
			clientWrite.append(customInput[1]);
		} else if (1 == customInput.length && customInput[0].equals("cd..")){
			clientWrite.append(customInput[0]);
			clientWrite.append(" ");
			clientWrite.append("");
		} else if (1 == customInput.length && customInput[0].equals("ls")){
			clientWrite.append(customInput[0]);
			clientWrite.append(" ");
			clientWrite.append("");
		} else if (1 == customInput.length && customInput[0].equals("quit")){
			clientWrite.append("quit");
		} else {
			return null;
		}
		return clientWrite.toString();
	}

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException,UnknownHostException {
		
		new Client().send();
		
	}
	
	/**
	 * 计算字符串中的“.”的个数
	 * @param str
	 * @return
	 */
	public boolean occurPoints(String str) {
	    int pos = -2;
	    int n = 0;
	    while (pos != -1) {
	        if (pos == -2) {
	            pos = -1;
	        }
	        pos = str.indexOf(".", pos + 1);
	        if (pos != -1) {
	            n++;
	        }
	    }
	    if (str.length() == n) {
	    	return false;
	    } else {
	    	return true;
	    }
	}

}
