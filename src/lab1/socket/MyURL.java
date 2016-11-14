package lab1.socket;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

public class MyURL {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		BufferedReader br = null;
		FileWriter fw = null;
		
		try{
			//获取网页对象
			URL u = new URL("http://netservice.zkalan.com");
			//获得URL的输入数据流
			InputStream fis = u.openStream();
			InputStreamReader fr = new InputStreamReader(fis);
			//读取并封装字符流
			br = new BufferedReader(fr);
			//保存为文件
			fw = new FileWriter("zkalan.html");
			String s= null;
			while ((s = br.readLine()) != null){
				System.out.println(s);
				fw.write(s);
			}
		}catch(MalformedURLException e){
			e.printStackTrace();
		}catch(IOException e){
			e.printStackTrace();
		}finally{
			try{
				br.close();
				fw.flush();
				fw.close();
			}catch(IOException e){
				e.printStackTrace();
			}
		}
		
	}

}
