/**
 * 这是服务器工作线程
 * 2016年11月13日
 */
package lab1.ftp;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.text.DecimalFormat;

/**
 * 单个进程负责与单个用户通信
 * @author Administrator
 *
 */
public class Handler implements Runnable {
	
	private Socket socket;
	String currentDir = System.getProperty("user.dir");
	int uPORT = 0;
	
	BufferedWriter bw;
	BufferedReader br;
	PrintWriter pw;
	
	/**
	 * 初始化
	 * @param socket
	 */
	public Handler(Socket socket){
		this.socket = socket;
	}
	
	/**
	 * 初始化输入输出流
	 * @throws IOException
	 */
	public void initStream() throws IOException{
		br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
		pw = new PrintWriter(bw, true);
	}
	
	public void run(){
		try {
			//输出提示信息
			System.out.println("成功连接！连接地址：" + socket.getInetAddress() + ":" + socket.getPort());
			
			//初始化输入输出流的对象
			initStream();
			
			//读取客户端发送的UDP端口
			uPORT = Integer.parseInt(br.readLine());
			System.out.println("UDP port " + uPORT);
			
			pw.println("[" + System.getProperty("user.dir") + "]" + "{connect success}");
			
			String info = null;
			
			while (null != (info = br.readLine())){
				
				//在服务器打印客户的输入
				System.out.println("Client" + socket.getInetAddress() + ":" + socket.getPort() + ">" + info);
				
				if (info.equals("quit")){
					//断开连接
					socket.close();
					System.out.println("Client" + socket.getInetAddress() + ":" + socket.getPort() + ">" + info);
					System.out.println("quit success");
					break;
				} else {
					String result = selectCommand(info);
					System.out.println("Server print>" + result);
					if (result.equals("-1")) {
						pw.println("[" + currentDir + "]{Download failed.&sdfg45sdfgnjk4}");
					} else if (result.equals("[" + currentDir + "]{Is not File&sdfg45sdfgnjk4}")){
						pw.println(result);
					} else {
						pw.println(result);
					}
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (null != socket){
					try {
						//断开连接
						socket.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	
	/**
	 * 
	 * @param info
	 * @return
	 * @throws IOException
	 */
	public String selectCommand(String info) throws IOException{
		//格式化用户输入
		String[] customInput = info.split("\\s+");
		String result = null;
		if (customInput[0].equals("ls")){
			//获得文件目录
			result = fileList();
		} else if (customInput[0].equals("cd")) {
			//进入某一级目录
			result = cd(customInput[1]);
		} else if (customInput[0].equals("get")) {
			//下载文件
			//-1
			//success
			//is not file
			result = getFile(customInput[1]);
		} else if (customInput[0].equals("cd..")){
			//返回目录
			result = backDir();
		}
		return result;
	}
	
	public String backDir() {
		StringBuilder bCurrent = new StringBuilder();
		String temp = currentDir;
		String[] dirList = temp.split("\\\\");
		if (!currentDir.equals(System.getProperty("user.dir"))){
			for (int i = 0 ; i < (dirList.length - 1); i++) {
				if (i < (dirList.length - 2)) {
					bCurrent.append(dirList[i] + "\\");
				} else {
					bCurrent.append(dirList[i]);
				}
			}
			currentDir = bCurrent.toString();
			return "[" + currentDir + "]{Success}";
		}
		return "[" + currentDir + "]{The current path is the root directory}";
	}

	public String cd(String childDir) {
		String temp = currentDir + "\\" + childDir;
		File dir = new File(temp);
		if (dir.isDirectory()){
			currentDir = currentDir + "\\" + childDir;
			return "[" + currentDir + "]{Success}";
		} else {
			return "[" + currentDir + "]{" + childDir +" does not Exist or is not Dir}";
		}
	}

	public String getFile(String fileName) throws IOException {
		String temp = currentDir + "\\" + fileName;
		File dir = new File(temp);
		String result = null;
		if (dir.isFile()){
			result = transFileByUdp(temp);
			return result;
		} else {
			return "[" + currentDir + "]{Is not File&sdfg45sdfgnjk4}";
		}
	}

	
	/**
	 * 
	 * @param filePath
	 * @throws IOException
	 */
	public String transFileByUdp(String filePath) throws IOException{
		String result = null;
		DatagramSocket udpSocket;
		FileInputStream fileReader = new FileInputStream(filePath);
		long fileLength = fileReader.available();
		
		//回复客户端
		pw.println("["+ currentDir + "]" 
				+ "{The file is in place. Available for download&sdfg45sdfgnjk4file length }" 
				+ "(" + Long.toString(fileLength) + ")");
		System.out.println("[" + currentDir + "]{" 
				+ "The file is in place. Available for download&sdfg45sdfgnjk4file length " 
				+ "}(" + Long.toBinaryString(fileLength) + ")");
		
		//等待客户端回复
		String clientReply = br.readLine();
		System.out.println("clientReply: " + clientReply);
		if (clientReply != null && clientReply.equals("ok")) {
			InetAddress toAddress = InetAddress.getByName("localhost");
			long currentPos = 0, bytesRead;
			
			byte[] msg = new byte[1024];
			
			try {
				udpSocket = new DatagramSocket(8079);
				udpSocket.connect(toAddress, uPORT);
				
				DatagramPacket packet = new DatagramPacket(msg, msg.length);
				
				
				while (currentPos < fileLength) {
					bytesRead = fileReader.read(msg);
					udpSocket.send(packet);
					currentPos = currentPos + bytesRead;
					if (!br.readLine().equals("ok")) {
						udpSocket.close();
						fileReader.close();
						return "-1";
					}
				}
				udpSocket.close();
				fileReader.close();
			} catch (SocketException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			System.out.println("Downlaod success");
			result = "[" + currentDir + "]{" + "Downlaod success&sdfg45sdfgnjk4file length " + "}(" + Long.toBinaryString(fileLength) + ")";
		} else {
			System.out.println("Downlaod failed");
			result = "-1";
		}
		return result;
	}

	/**
	 * 返回文件列表
	 * @return String
	 * @throws IOException
	 */
	public String fileList() throws IOException{

		StringBuilder dirs = new StringBuilder();
		File dir = new File(currentDir);
		File[] files = dir.listFiles();
		String flag = null;
		String temp = currentDir;
		//首部信息
		dirs.append("[" + currentDir + "]{CurrentDir" + " " + temp.replace(System.getProperty("user.dir"), "/").replace("\\", "/") 
				+ "&sdfg45sdfgnjk4");
		
		for (int i = 0;i < files.length;i++){
			if (files[i].isDirectory()){
				flag = "<D>";
			} else {
				flag = "<F>";
			}
			dirs.append(flag + "        " + files[i].getName() + "        " + getFileSize(files[i]) + "&sdfg45sdfgnjk4");
		}
		dirs.append("}");
		return dirs.toString();
	}
	
	/**
	 * 得到文件大小
	 * @param file
	 * @return
	 */
	public static String getFileSize(File file){
		
		String size = "";
		if (file.exists() && file.isFile()) {
			long files = file.length();
			DecimalFormat df = new DecimalFormat("#.00");
			if (files < 1024) {
				size = df.format((double)files) + "B";
			} else if (files < 1048576) {
				size = df.format((double)files/1024) + "KB";
			} else if (files < 1073741824) {
				size = df.format((double)files/1048576) + "MB";
			} else {
				size = df.format((double)files/1073741824) + "GB";
			}
		} else if (file.exists() && file.isDirectory()) {
			size = "         - ";
		} else {
			size = "0B";
		}
		return size;
	}
}
