package http;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

/**
 * 这是服务器工作线程
 * 2016年11月21日
 */

import java.io.IOException;
import java.net.Socket;

/**
 * 单个进程负责与单个用户通信
 * @author Administrator
 *
 */
public class Response {
	
	private Socket socket;
	
	String serverRoot = "D:\\myServer";

	/**
	 * Allow a maximum buffer size of 8192 bytes
	 */
	private static int buffer_size = 8192;

	/**
	 * Response is stored in a byte array.
	 */
	byte[] buffer;
	/**
	 * Output stream to the socket.
	 */
	BufferedOutputStream ostream = null;

	/**
	 * Input stream from the socket.
	 */
	BufferedInputStream istream = null;

	/**
	 * StringBuffer storing the header
	 */
	StringBuffer header = null;
	/**
	 * StringBuffer response the header
	 */
	StringBuffer rHeader = null;
	
	StringBuilder postContent = new StringBuilder();
	/**
	 * byte[] content
	 */
	byte[] content;
	/**
	 * contentLength
	 */
	long contentLength;
	
	/**
	 * String to represent the Carriage Return and Line Feed character sequence.
	 */
	static private String CRLF = "\r\n";

	/**
	 * HttpClient constructor;
	 * @throws IOException 
	 */
	public Response(Socket socket) throws IOException {
		buffer = new byte[buffer_size];
		header = new StringBuffer();
		rHeader = new StringBuffer();
		contentLength = -1;
		content = new byte[buffer_size];
		this.socket = socket;
		/**
		 * init input and output stream
		 */
		/**
		 * Create the output stream.
		 */
		ostream = new BufferedOutputStream(socket.getOutputStream());

		/**
		 * Create the input stream.
		 */
		istream = new BufferedInputStream(socket.getInputStream());
	}
	
	/**
	 * analyis request header
	 * str[0] type get put other
	 * str[1] path url
	 * str[i+1] contentlength
	 */
	public String[] getRequestTypeContent(){
		String[] str = null;
		int last = 0, c = 0;
		/**
		 * Process the header and add it to the header StringBuffer.
		 */
		boolean inHeader = true; // loop control
		try {
			while (inHeader && ((c = istream.read()) != -1)) {
				switch (c) {
				case '\r':
					break;
				case '\n':
					if (c == last) {
						inHeader = false;
						break;
					}
					last = c;
					header.append("\n");
					break;
				default:
					last = c;
					header.append((char) c);
				}
			}
			str = header.toString().replace(CRLF, "\\s").split("\\s");
		/**
		 * Read the contents and add it to the response StringBuffer.
		 */
		} catch (IOException e) {
			e.printStackTrace();
		} 
		return str;
	}
	
	/**
	 * return static resource and create header
	 * @param url
	 * @throws IOException
	 */
	public void staticResourceResponse(String url) throws IOException{
		FileInputStream fis = null;
		File file = new File(serverRoot,url);
		int key = 0;
		if (file.exists() && !file.isDirectory()) {
			key = 200;
			fis = new FileInputStream(file);
			long fileLength = fis.available();
			createRHeader(key,"OK",file,url);
			System.out.println(rHeader.toString());
			ostream.write(CRLF.getBytes(), 0, CRLF.length());
			int currentPos = 0,bytesRead = 0;
			while (currentPos < fileLength) {
				bytesRead = fis.read(buffer);
				ostream.write(buffer, 0, bytesRead);
				currentPos += bytesRead;
			}
			ostream.flush();
			fis.close();
		} else {
			key = 404;
			File error = new File(serverRoot,"404.html");
			fis = new FileInputStream(error);
			long fileLength = fis.available();
			createRHeader(key,"Not Found",error,"404.html");
			String info = "Location: http://127.0.0.1/404.html" + CRLF;
			ostream.write(info.getBytes(), 0, info.length());
			System.out.println(rHeader.toString());
			ostream.write(CRLF.getBytes(), 0, CRLF.length());
			int currentPos = 0,bytesRead = 0;
			while (currentPos < fileLength) {
				bytesRead = fis.read(buffer);
				ostream.write(buffer, 0, bytesRead);
				currentPos += bytesRead;
			}
			ostream.flush();
			fis.close();
		}
	}
	
	/**
	 * create header of response
	 * @param keyInt
	 * @param keyWord
	 * @param contentType
	 * @param fileLength
	 * @throws IOException
	 */
	public void createRHeader(int keyInt,String keyWord,File file,String url) throws IOException{
		rHeader.append("HTTP/1.1 " + String.valueOf(keyInt) + " " + keyWord + CRLF);
		rHeader.append("Server: zhangkai/1.0.0" + CRLF);
		rHeader.append("Content-Type: " + getContentType(file,url) + CRLF);
		rHeader.append("Content-Length: " + Long.toString(file.length()) + CRLF);
		ostream.write(rHeader.toString().getBytes(), 0, rHeader.length());
	}
	
	public String getContentType(File file,String url) throws IOException{
		byte[] by = getFileHeader(file);
		String str = bytesToHexString(by);
		return compireType(str,url);
	}
	/**
	 * return content type of header
	 * @param fis
	 * @return
	 * @throws IOException
	 */
	public byte[] getFileHeader(File file) throws IOException{
		byte[] headByte = new byte[4];
		FileInputStream fis = new FileInputStream(file);
		fis.read(headByte);
		fis.close();
		return headByte;
	}
	
	/**
	 * 
	 * @param bStr
	 * @return
	 */
	public String bytesToHexString(byte[] bStr) {
		StringBuffer stringBuffer = new StringBuffer();
		if (null == bStr || bStr.length <= 0){
			return null;
		} else {
			int v = 0;
			String hv = null;
			for (int i = 0;i < bStr.length; i++) {
				v = bStr[i] & 0xFF;
				hv = Integer.toHexString(v);
				if (hv.length() < 2) {
					stringBuffer.append(0);
				}
				stringBuffer.append(hv);
			}
			return stringBuffer.toString().toUpperCase();
		}
	}
	
	/**
	 * 
	 * @param hexString
	 * @return
	 * 依次为服务器支持的文件类型
	 * 用以返回Content-Type
	 * 文本文件：解析文件后缀名
	 * 媒体文件：解析文件头4个Byte
	 */
	public String compireType(String hexString,String url){
		String ends = url.substring(url.lastIndexOf(".") + 1);
		if (ends.equals("html")){
			return "text/html";
		} else if (ends.equals("htm")) {
			return "text/html";
		} else if (ends.equals("css")) {
			return "text/css";
		} else if (ends.equals("png") || hexString.equals("89504E47")) {
			return "image/png";
		} else if (ends.equals("jpg") || hexString.equals("FFD8FFE0")) {
			return "image/jpg";
		} else if (ends.equals("xml") || hexString.equals("")){
			return "text/xml";
		} else if (ends.equals("txt") || hexString.equals("")){
			return "text/plain";
		} else if (ends.equals("js") || hexString.equals("")){
			return "application/x-javascript";
		} else if (ends.equals("jsp") || hexString.equals("")){
			return "text/html";
		} else if (ends.equals("jsp") || hexString.equals("")){
			return "text/html";
		} else if (ends.equals("jpeg") || hexString.equals("")){
			return "image/jpeg";
		} else if (ends.equals("gif") || hexString.equals("")){
			return "image/gif";
		} else if (ends.equals("pdf") || hexString.equals("")){
			return "application/pdf";
		} else if (ends.equals("bmp") || hexString.equals("")){
			return "image/bmp";
		} else if (ends.equals("ico") || hexString.equals("")){
			return "image/x-icon";
		} else if (ends.equals("mp4")){
			return "video/mp4";
		} else if (ends.equals("webm")){
			return "video/webm";
		} else if (ends.equals("ogv")){
			return "video/ogg";
		} else {
			return null;
		}
	}
	
	/**
	 * 对网站首页链接进行跳转
	 * @param path
	 * @return
	 */
	public String getURL(String path){
		if (path.equals("/")){
			return "index.html";
		}
		return path;
	}
	
	
	/**
	 * 接受PUT请求提交的内容
	 * 当路径存在时：更新文件
	 * 当路径不存在时：创建文件
	 * @param url
	 * @param fileLength
	 * @throws IOException
	 */
	public void putResourceResponse(String url,String fileLength) throws IOException{
		File file = new File(serverRoot,url);
		long length = Long.parseLong(fileLength);
		//文件存在时
		if (file.exists()) {
			FileOutputStream fos = new FileOutputStream(file);
			int currentPos = 0,bytesRead = 0;
			//接受浏览器提交的字节流
			while (currentPos < length) {
				bytesRead = istream.read(content);
				fos.write(content, 0, bytesRead);
				currentPos += bytesRead;
			}
			fos.close();
			createRHeader(200,"OK",file,url);
		} else {
			FileOutputStream fos = new FileOutputStream(file);
			int currentPos = 0,bytesRead = 0;
			//接受浏览器提交的字节流
			while (currentPos < length) {
				bytesRead = istream.read(content);
				fos.write(content, 0, bytesRead);
				currentPos += bytesRead;
			}
			fos.close();
			createRHeader(201,"Created",file,url);
		}
		//添加报文尾部
		rHeader.append("Location: " + "http://127.0.0.1" + url + CRLF + CRLF);
		System.out.println(rHeader.toString());
		ostream.flush();
	}
	
	/**
	 * 处理POST请求
	 * 主要是解析出receiver和words
	 * 专门的函数
	 * 
	 */
	public void processPOSTResponse(String contentLength) throws Exception {

		long length = Long.parseLong(contentLength);
		System.out.println("Text Length:" + length);
		int currentPos = 0,bytesRead = 0;
		String temp;
		/**
		 * Read the contents and add it to the response StringBuffer.
		 */
		while (currentPos < length) {
			bytesRead = istream.read(buffer);
			System.out.println("Readed Length:" + bytesRead);
			temp = new String(buffer,"utf-8");
			postContent.append(temp);
			currentPos += bytesRead;
		}
		System.out.println(postContent.toString());
		
		//下面的处理将会抹去所有换行
		//String[] message = postContent.toString().replace(CRLF, " ").split(" +");
		
		//下面的方法可以处理换行
		String[] message = postContent.toString().split(CRLF);		
		
		/**
		 * 分隔符meassage[0]
		 */
		String receiver = null;
		StringBuilder words = new StringBuilder();
//		for (int i = 0;i < message.length;i++) {
//			if (message[i].equals("name=\"emailaddr\"")) {
//				receiver = message[i+1];
//			} else if (message[i].equals("name=\"words\"")) {
//				for (int j = i + 1; !message[j].equals(message[0])&& j < message.length;j++) {
//					words.append(message[j] + " ");
//				}
//				i = message.length;
//			}
//		}
		
		for (int i = 0;i < message.length;i++) {
		if (message[i].equals("Content-Disposition: form-data; name=\"emailaddr\"")) {
			receiver = message[i+2];
		} else if (message[i].equals("Content-Disposition: form-data; name=\"words\"")) {
			for (int j = i + 1; !message[j].equals(message[0])&& j < message.length;j++) {
				words.append(message[j] + CRLF);
			}
			i = message.length;
		}
	}
		while (true){
			sendMail(receiver,words.toString().getBytes());
			break;
		}
	}
	
	/**
	 * 类比于GET请求的返回函数
	 * 仅仅返回POST请求下的success.html页面
	 * @throws IOException
	 */
	public void sendSuccess() throws IOException{
		int key;
		FileInputStream fis = null;
		key = 200;
		File success = new File(serverRoot,"success.html");
		fis = new FileInputStream(success);
		long fileLength = fis.available();
		createRHeader(key,"OK",success,"success.html");
		String info = "Location: http://127.0.0.1/success.html" + CRLF;
		ostream.write(info.getBytes(), 0, info.length());
		System.out.println(rHeader.toString());
		ostream.write(CRLF.getBytes(), 0, CRLF.length());
		int currentPos = 0,bytesRead = 0;
		while (currentPos < fileLength) {
			bytesRead = fis.read(buffer);
			ostream.write(buffer, 0, bytesRead);
			currentPos += bytesRead;
		}
		ostream.flush();
		fis.close();
	}
	
	/**
	 * send email
	 * @throws IOException 
	 */
	public void sendMail(String receiver,byte[] words) throws IOException{
		
		MailUtil email = new MailUtil();
		
		email.main(receiver, words);
		
	}
	
	/**
	 * 返回405错误
	 * @throws IOException
	 */
	public void notAllowedMethod() throws IOException{
		rHeader.append("HTTP/1.1 405 Method Not Allowed" + CRLF);
		rHeader.append("Server: zhangkai/1.0.0" + CRLF);
		rHeader.append("Allow: GET,PUT,POST" + CRLF + CRLF);
		ostream.write(rHeader.toString().getBytes(), 0, rHeader.length());
		ostream.flush();
	}
	
	/**
	 * @throws IOException 
	 * 关闭所有连接
	 */
	public void close() throws IOException{
		istream.close();
		ostream.close();
		socket.close();
	}
}
