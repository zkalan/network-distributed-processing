package http;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

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
	private byte[] buffer;
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
	/**
	 * StringBuffer storing the response.
	 */
	private StringBuffer response = null;
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
		response = new StringBuffer();
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
			while (str[0].equals("PUT") && istream.read(content) != -1) {
				//content.clone();
			}
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
		if (file.exists()) {
			fis = new FileInputStream(file);
			long fileLength = fis.available();
			createRHeader(200,"OK",file,url);
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
		rHeader.append("Content-Length: " + Long.toString(file.length()) + CRLF + CRLF);
		System.out.println(rHeader.toString());
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
		} else if (ends.equals("jpeg") || hexString.equals("")){
			return "image/jpeg";
		} else if (ends.equals("gif") || hexString.equals("")){
			return "image/gif";
		} else if (ends.equals("pdf") || hexString.equals("")){
			return "application/pdf";
		} else if (ends.equals("bmp") || hexString.equals("")){
			return "image/bmp";
		} else {
			return null;
		}
	}
	
	public String getURL(String path){
		if (path.equals("/")){
			return "index.html";
		}
		return path;
	}
	
	/**
	 * @throws IOException 
	 * 
	 */
	public void close() throws IOException{
		istream.close();
		ostream.close();
		socket.close();
	}
}
