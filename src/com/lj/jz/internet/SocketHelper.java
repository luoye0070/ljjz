package com.lj.jz.internet;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.UnknownHostException;

public class SocketHelper {
	
	public static final String ZD_COUNT="zdcount";
	public static final String ZD_MX="zdmx";
	public static final String END="end";
	
	public static final String CS_COUNT="";
	public static final String CS_MX="";
	
	public String host;
	public int port;
	public SocketHelper(String host, int port) {
		this.host = host;
		this.port = port;
	}
	public SocketHelper(String host) {
		this.host = host;
		this.port = 5000;
	}
	public SocketHelper(int port) {
		this.host ="127.0.0.1";
		this.port = port;
	}
	public SocketHelper() {
		this.host ="127.0.0.1";
		this.port = 5000;
	}
	
	private Socket socket=null;
	//�ͷ���������
	public boolean connect(){
		try {
			this.socket=new Socket(host, port);
			this.socket.setTcpNoDelay(true);
			return true;
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	//�رպͷ�����������
	public void close(){
		if(socket!=null){
			//�ر�socket
			try {
				System.out.println("�ر�socket");
				socket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	//�����������һ����Ϣ
	public boolean send(String message){		
		if(socket!=null){
			try {
				//��������
				BufferedWriter bw=new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "GBK"));
				bw.write(message);
				bw.flush();
				System.out.println("send string:"+message);
				//������Ӧ
				BufferedReader br=new BufferedReader(new InputStreamReader(socket.getInputStream(),"GBK"));
				StringBuilder sb=new StringBuilder("");
				char []buffer=new char[32];
				br.read(buffer,0,32);
				String str=new String(buffer);
				//String str=br.readLine();
				System.out.println("recive string:"+str.trim());
				if(str!=null&&"true".equals(str.trim())){
					return true;
				}
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return false;
	}
	//�ӷ���������һ������,����������������Ӧ
	public String receive(){
		if(socket!=null){
			try {
				//����������������
				BufferedWriter bw=new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "GBK"));
				bw.write("getCs");
				bw.flush();
				System.out.println("send string:get");
				//������Ӧ
				BufferedReader br=new BufferedReader(new InputStreamReader(socket.getInputStream(),"GBK"));
				StringBuilder sb=new StringBuilder("");
				char []buffer=new char[1024];
				br.read(buffer,0,1024);
				String str=new String(buffer);
				//String str=br.readLine();
				System.out.println("recive string:"+str.trim());
				return str.trim();
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}
	
}
