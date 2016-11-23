package http;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
//import java.io.FileInputStream;
import java.net.Socket;

/**
 * Class <em>HttpClient</em> is a class representing a simple HTTP client.
 *
 * @author wben
 */

public class HttpClient {

//	/**
//	 * default HTTP port is port 80
//	 */
//	private static int port = 80;

	String clientRoot = "C:\\Users\\zk\\workspace\\networkdp\\client";
	/**
	 * Allow a maximum buffer size of 8192 bytes
	 */
	private static int buffer_size = 8192;

	/**
	 * Response is stored in a byte array.
	 */
	private byte[] buffer;

	/**
	 * My socket to the world.
	 */
	Socket socket = null;

	/**
	 * Default port is 80.
	 */
	private static final int PORT = 80;

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
	private StringBuffer header = null;

	/**
	 * StringBuffer storing the response.
	 */
	private StringBuffer response = null;
	
	/**
	 * String to represent the Carriage Return and Line Feed character sequence.
	 */
	static private String CRLF = "\r\n";

	/**
	 * HttpClient constructor;
	 */
	public HttpClient() {
		buffer = new byte[buffer_size];
		header = new StringBuffer();
		response = new StringBuffer();
	}

	/**
	 * <em>connect</em> connects to the input host on the default http port --
	 * port 80. This function opens the socket and creates the input and output
	 * streams used for communication.
	 */
	public void connect(String host) throws Exception {

		/**
		 * Open my socket to the specified host at the default port.
		 */
		socket = new Socket(host, PORT);

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
	 * <em>processGetRequest</em> process the input GET request.
	 */
	public void processGetRequest(String request) throws Exception {
		/**
		 * Send the request to the server.
		 */
		request += CRLF + CRLF;
		buffer = request.getBytes();
		ostream.write(buffer, 0, request.length());
		ostream.flush();
		/**
		 * waiting for the response.
		 */
		processResponse();
	}
	
	/**
	 * <em>processPutRequest</em> process the input PUT request.
	 */
	public void processPutRequest(String request) throws Exception {
		//=======start your job here============//
		/**
		 * 
		 */
		String[] clientInput = request.split("\\s");
		File face = new File(clientRoot,clientInput[1]);
		/**
		 * send header to server
		 */
		createPutRequestHeader(clientInput[1],face);
		/**
		 * write byte[] of file into ostream
		 */
		staticResourceResponse(clientInput[1]);
		/**
		 * clear
		 */
//		FileInputStream fi = new FileInputStream("face.jpg");
//		buffer = fi.
		ostream.flush();
		/**
		 * waiting for the response
		 */
		processResponse();
		
		//=======end of your job============//
	}
	
	/**
	 * 
	 * @param url
	 * @throws IOException
	 */
	public void staticResourceResponse(String url) throws IOException{
		FileInputStream fis = null;
		File file = new File(clientRoot,url);
		if (file.exists()) {
			fis = new FileInputStream(file);
			long fileLength = fis.available();
			int currentPos = 0,bytesRead = 0;
			while (currentPos < fileLength) {
				bytesRead = fis.read(buffer);
				ostream.write(buffer, 0, bytesRead);
				currentPos += bytesRead;
			}
			fis.close();
		}
	}
	
	/**
	 * @throws IOException 
	 * 
	 */
	public void createPutRequestHeader(String url,File file) throws IOException{
		header.append("PUT " + url + " HTTP/1.0" + CRLF);
		header.append("Content-Type: " + compireType(url) + CRLF);
		header.append("Content-Length: " + Long.toString(file.length()) + CRLF + CRLF);
		System.out.println(header.toString());
		ostream.write(header.toString().getBytes(), 0, header.length());
	}
	
	/**
	 * 
	 * 
	 * @return
	 */
	public String compireType(String url){
		String ends = url.substring(url.lastIndexOf(".") + 1);
		if (ends.equals("html")){
			return "text/html";
		} else if (ends.equals("htm")) {
			return "text/html";
		} else if (ends.equals("css")) {
			return "text/css";
		} else if (ends.equals("png")) {
			return "image/png";
		} else if (ends.equals("jpg")) {
			return "image/jpg";
		} else if (ends.equals("xml")){
			return "text/xml";
		} else if (ends.equals("txt")){
			return "text/plain";
		} else if (ends.equals("js")){
			return "application/x-javascript";
		} else if (ends.equals("jpeg")){
			return "image/jpeg";
		} else if (ends.equals("gif")){
			return "image/gif";
		} else if (ends.equals("pdf")){
			return "application/pdf";
		} else if (ends.equals("bmp")){
			return "image/bmp";
		} else if (ends.equals("ico")){
			return "image/x-icon";
		} else if (ends.equals("mp4")){
			return "video/mp4";
		} else {
			return null;
		}
	}
	
	/**
	 * <em>processResponse</em> process the server response.
	 * 
	 */
	public void processResponse() throws Exception {
		int last = 0, c = 0;
		/**
		 * Process the header and add it to the header StringBuffer.
		 */
		boolean inHeader = true; // loop control
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

		/**
		 * Read the contents and add it to the response StringBuffer.
		 */
		while (istream.read(buffer) != -1) {
			response.append(new String(buffer,"iso-8859-1"));
		}
	}

	/**
	 * Get the response header.
	 */
	public String getHeader() {
		return header.toString();
	}

	/**
	 * Get the server's response.
	 */
	public String getResponse() {
		return response.toString();
	}

	/**
	 * Close all open connections -- sockets and streams.
	 */
	public void close() throws Exception {
		socket.close();
		istream.close();
		ostream.close();
	}
}
