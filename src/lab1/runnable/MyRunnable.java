package lab1.runnable;

import java.lang.Runnable;
import java.lang.Thread;

public class MyRunnable implements Runnable{
	
	private String name;
	
	public MyRunnable(String threadName){
		this.name = threadName; 
	}
	

	@Override
	public void run() {
		// TODO Auto-generated method stub
		for (int i = 0 ;i < 10 ; i++ ){
			System.out.println(getName() + ": Hello from MyRunnable");
		}
	}
	
	private String getName() {
		// TODO Auto-generated method stub
		return this.name;
	}

	public static void main(String args[]){
		Thread thread1 = new Thread(new MyRunnable("thread1"));
		Thread thread2 = new Thread(new MyRunnable("thread2"));
		thread1.start();
		thread2.start();
	}

}
