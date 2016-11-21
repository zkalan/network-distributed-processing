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
	
	String serverRoot = "D:\\pavo\\www";

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
	
	public void staticResourceResponse(String url) throws IOException{
		FileInputStream fis = null;
		File file = new File(serverRoot,url);
		if (file.exists()) {
			fis = new FileInputStream(file);
			long fileLength = fis.available();
			int currentPos = 0,bytesRead = 0;
			while (currentPos < fileLength) {
				bytesRead = fis.read(buffer);
				ostream.write(buffer, 0, bytesRead);
				currentPos += bytesRead;
			}
			ostream.flush();
			
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
