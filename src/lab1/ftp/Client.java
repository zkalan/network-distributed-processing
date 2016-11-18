/**
 * My Echo Client
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
import java.util.regex.Pattern;

public class Client {
	
	//连接的端口
	static final int PORT = 8080;
	//连接地址
	static final String HOST = "127.0.0.1";
	//建立客户端套接字
	Socket socket = new Socket();
	//根目录
	String currentDir = System.getProperty("user.dir");
	
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
			System.out.print("Client" + currentDir.replace(System.getProperty("user.dir"), "/").replace("\\", "/") + ">");
			//建立客户端输出流，向服务器发送数据
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			//建立客户端输入流，接收服务器的数据
			BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			
			//装饰输出流，即时刷新
			PrintWriter pw = new PrintWriter(bw,true);
			//接受用户输入的信息
			//@SuppressWarnings("resource")
			//Scanner cin = new Scanner(System.in);
			BufferedReader cin = new BufferedReader(new InputStreamReader(System.in));
			//用户的输入
			String msg = null;
			//局部变量
			String recStr = null;
			String serverResp = null;
			int beginIndex = 0;
			int endIndex = 0;
			while ((msg = cin.readLine()) != null){
				//发送消息至服务器
				if (msg.equals("quit")){
					//退出
					System.out.println("quit Success");
					break;
				} else if (createClientWrite(msg) != null) {
					pw.println(createClientWrite(msg)); 
					//接收服务器回复信息并且输出
					serverResp = br.readLine();
					
					//更新currentDir
					beginIndex = serverResp.indexOf("[")+1;  
					endIndex = serverResp.lastIndexOf("]");
					currentDir = serverResp.substring(beginIndex,endIndex);
					
					
					recStr = serverResp.substring(serverResp.indexOf("{")+1,serverResp.lastIndexOf("}")).replaceAll("&sdfg45sdfgnjk4", "\n");
					System.out.println("Client " + recStr);
				} else {
					System.out.println("Please check the Input command");
				}
				System.out.print("Client" + currentDir.replace(System.getProperty("user.dir"), "/").replace("\\", "/") + ">");
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
		String regEx = "\\s+";
		
		if (customInput[0].equals("cd") && 2 == customInput.length 
				&& !Pattern.compile(regEx).matcher(customInput[1]).find()) {
			clientWrite.append(customInput[0]);
			clientWrite.append(" ");
			clientWrite.append(customInput[1]);
		} else if (customInput[0].equals("get")&& 2 == customInput.length)  {
			clientWrite.append(customInput[0]);
			clientWrite.append(" ");
			clientWrite.append(customInput[1]);
		} else if (customInput[0].equals("cd..") && 1 == customInput.length){
			clientWrite.append(customInput[0]);
			clientWrite.append(" ");
			clientWrite.append("");
		} else if (customInput[0].equals("ls") && 1 == customInput.length){
			clientWrite.append(customInput[0]);
			clientWrite.append(" ");
			clientWrite.append("");
		} else if (customInput[0].equals("quit") && 1 == customInput.length){
			return "quit";
		} else {
			return null;
		}
		return clientWrite.toString();
	}
	
	//客户端格式化输出
	public void clientPrint(String printContent){
		System.out.println();
	}

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException,UnknownHostException {
		
		new Client().send();
		
	}

}
