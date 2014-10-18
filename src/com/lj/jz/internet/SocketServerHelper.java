package com.lj.jz.internet;

import java.net.ServerSocket;
import java.net.Socket;

public class SocketServerHelper {
	private OnSocketConnect osc = null;
	public boolean serverIsActive = false;

	public SocketServerHelper(OnSocketConnect osc) {
		this.osc = osc;
	}

	/********************
	 * 启动服务
	 * ********************/
	public void startServer() {
		serverIsActive=true;
		// 启动一个线程去监听8000端口
		new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					ServerSocket serverSocket = new ServerSocket(8000);
					while (serverIsActive) {
						final Socket socket = serverSocket.accept();
						// 如果超过这个时间没有新的数据，则不再继续等待数据
						// socket.setSoTimeout(1000);
						// 将socket加入到队列中
						System.out.println("接收到一个客户端请求");
						new Thread(new Runnable() {

							@Override
							public void run() {
								// TODO Auto-generated method stub
								osc.doTb(socket);
							}
						}).start();
						// Thread.sleep(1000);//休眠1秒钟

					}
					serverSocket.close();// 关闭socket服务
					serverSocket=null;
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}).start();

	}

	/********************
	 * 停止服务
	 * ********************/
	public void stopServer() {
		this.serverIsActive = false;
	}
}
