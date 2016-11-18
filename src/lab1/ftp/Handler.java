/**
 * 这是服务器工作线程
 * 2016年11月13日
 */
package lab1.ftp;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.DecimalFormat;

/**
 * 单个进程负责与单个用户通信
 * @author Administrator
 *
 */
public class Handler implements Runnable {
	
	private Socket socket;
	String currentDir = System.getProperty("user.dir");
	
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
			System.out.println("成功连接！连接地址：" + socket.getInetAddress() + ":" + socket.getPort());
			//初始化输入输出流的对象
			initStream();
			String info = null;
			while (null != (info = br.readLine())){
				System.out.println("Client" + socket.getInetAddress() + ":" + socket.getPort() + ">" + info);
				
				//分析用户输入
				String[] customInput = info.split(" ");
				
				if (info.equals("quit")){
					break;
				} else {
					switch (customInput[0]) {
					case "ls" : fileList();
					case "cd" : System.out.println(customInput[1]);
					case "get" : getFile();
					case "\n" : pw.println("\n");
					default :
						pw.println(customInput[0] + "\n");
					
					}
				}
				if (info.equals("ls")){

				}
			}
		}catch (IOException e){
			e.printStackTrace();
		}finally {
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
	
	public void cd(String childDir) {
		// TODO Auto-generated method stub
		currentDir = currentDir + "\\" + childDir;
	}

	public void getFile() {
		// TODO Auto-generated method stub
		
	}

	public void fileList() throws IOException{

		StringBuilder dirs = new StringBuilder();
		File dir = new File(currentDir);
		File[] files = dir.listFiles();
		String flag = null;
		String temp = currentDir;
		//首部信息
		dirs.append("当前目录" + "/" + temp.replace(System.getProperty("user.dir"), "/").replace("\\", "/") + "\123");
		
		for (int i = 0;i < files.length;i++){
			if (files[i].isDirectory()){
				flag = "<D>";
			} else {
				flag = "<F>";
			}
			dirs.append(flag + "        " + files[i].getName() + "        " + getFileSize(files[i]) + "\123");
		}
		pw.println(dirs.toString());
	}
	
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
			size = "";
		} else {
			size = "0B";
		}
		return size;
	}
}
