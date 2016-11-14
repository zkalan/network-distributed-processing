/**
 * 时间2016年11月13日
 * 《网络与分布计算》
 * 第一次实验课
 * 学习java socket编程
 */
package lab1.socket;

import java.net.InetAddress;

public class MySocket {
	
	//InetAddress 网络目标地址对象
	//InetSocketAddress 网络目标地址对象和端口

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		
		InetAddress localIP = InetAddress.getLocalHost();
		//主机名+主机地址
		System.out.println(localIP.getHostName() + ":" + localIP.getHostAddress());
		
		//通过不同方式查找到主机信息
		InetAddress localIP2 = InetAddress.getByName("311E1");
		System.out.println("311E1:" + localIP2.getHostAddress() + " By HostName");
		
		//根据域名到DNS查询IP
		InetAddress localIP3 = InetAddress.getByName("www.zkalan.cn");
		System.out.println(localIP3.getHostName() + " "+ localIP3.getHostAddress() + " By Domain");
		
		//通过主机名查询主机信息
		InetAddress localIP4 = InetAddress.getByAddress(new byte[]{123,(byte)206,55,(byte)231});
		System.out.println("123.206.55.231:" + localIP4.getHostName() + " By IP");
	}

}
