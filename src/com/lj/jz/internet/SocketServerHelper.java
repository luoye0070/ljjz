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
	 * ��������
	 * ********************/
	public void startServer() {
		serverIsActive=true;
		// ����һ���߳�ȥ����8000�˿�
		new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					ServerSocket serverSocket = new ServerSocket(8000);
					while (serverIsActive) {
						final Socket socket = serverSocket.accept();
						// ����������ʱ��û���µ����ݣ����ټ����ȴ�����
						// socket.setSoTimeout(1000);
						// ��socket���뵽������
						System.out.println("���յ�һ���ͻ�������");
						new Thread(new Runnable() {

							@Override
							public void run() {
								// TODO Auto-generated method stub
								osc.doTb(socket);
							}
						}).start();
						// Thread.sleep(1000);//����1����

					}
					serverSocket.close();// �ر�socket����
					serverSocket=null;
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}).start();

	}

	/********************
	 * ֹͣ����
	 * ********************/
	public void stopServer() {
		this.serverIsActive = false;
	}
}
