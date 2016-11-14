package lab1.review;
import java.lang.Thread;

public class MyThread extends Thread {
	public MyThread(String str) {
		super(str);
	}

	@Override
	public void run() {
		//线程体
		synchronized (this) {
			for (int i = 0; i < 10; i++) {
				System.out.println(getName() + "-" + i);
				try {
					sleep((long) (Math.random() * 1000));
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

			}
			// for循环结束
			System.out.println(getName() + "-for循环结束");
		}
		super.run();
	}

	public static void main(String args[]) {
		//启动线程
		new MyThread("TA").run();
		new MyThread("TB").run();
		//主线程
		System.out.println("===Main Thread===");
	}

}