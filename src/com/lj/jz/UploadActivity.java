package com.lj.jz;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

import com.lj.jz.data.CanShu;
import com.lj.jz.data.CanShuDao;
import com.lj.jz.data.CanShuLbEnum;
import com.lj.jz.data.DatabaseHelper;
import com.lj.jz.data.SystemParam;
import com.lj.jz.data.SystemParamDao;
import com.lj.jz.data.SystemParamNames;
import com.lj.jz.data.ZhangDan;
import com.lj.jz.data.ZhangDanDao;
import com.lj.jz.filewr.FileReadWrite;
import com.lj.jz.internet.OnSocketConnect;
import com.lj.jz.internet.SocketHelper;
import com.lj.jz.internet.SocketServerHelper;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.Toast;

public class UploadActivity extends Activity {

	Button Nav_zdBt = null;
	Button Nav_scBt = null;
	Button Nav_csBt = null;
	Button Nav_szBt = null;

	CheckBox uploadAy_isDelCb = null;
	EditText uploadAy_ipEt = null;
	Button uploadAy_synParamBt = null;
	Button uploadAy_beginBt = null;
	EditText uploadAy_detailEt = null;
	Handler handler = null;
	String ipStr = null;
	boolean isDel = true;

	CheckBox uploadAy_isBackUpAuto = null;
	Button uploadAy_importDataBt = null;
	EditText uploadAy_importDetailEt = null;
	Handler drsjHandler = null;

	Button uploadAy_beginTbServiceBt = null;
	EditText uploadAy_serviceDetailEt = null;
	Handler sjtbHandler = null;
	
	SocketServerHelper ssh =null;

	// ����ȡ��intתΪ������ip��ַ,�ο������ϵģ��޸�����
	private String intToIp(int i) {
		return (i & 0xFF) + "." + ((i >> 8) & 0xFF) + "." + ((i >> 16) & 0xFF)
				+ "." + ((i >> 24) & 0xFF);
	}
	private String getIp(){
		// ��ȡwifiIp��ַ
		WifiManager wm = (WifiManager) getSystemService(WIFI_SERVICE);
		if (wm.isWifiEnabled()) {
			int ip_int = wm.getConnectionInfo().getIpAddress();
			String ip_str = intToIp(ip_int);
			return ip_str;
		} else {
			return null;
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// ȥ������
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.activity_upload);

		Nav_zdBt = (Button) findViewById(R.id.Nav_zdBt);
		Nav_scBt = (Button) findViewById(R.id.Nav_scBt);
		Nav_csBt = (Button) findViewById(R.id.Nav_csBt);
		Nav_szBt = (Button) findViewById(R.id.Nav_szBt);

		uploadAy_isDelCb = (CheckBox) findViewById(R.id.uploadAy_isDelCb);
		uploadAy_ipEt = (EditText) findViewById(R.id.uploadAy_ipEt);
		uploadAy_synParamBt = (Button) findViewById(R.id.uploadAy_synParamBt);
		uploadAy_beginBt = (Button) findViewById(R.id.uploadAy_beginBt);
		uploadAy_detailEt = (EditText) findViewById(R.id.uploadAy_detailEt);

		uploadAy_isBackUpAuto = (CheckBox) findViewById(R.id.uploadAy_isBackUpAuto);
		uploadAy_importDataBt = (Button) findViewById(R.id.uploadAy_importDataBt);
		uploadAy_importDetailEt = (EditText) findViewById(R.id.uploadAy_importDetailEt);

		uploadAy_beginTbServiceBt = (Button) findViewById(R.id.uploadAy_beginTbServiceBt);
		uploadAy_serviceDetailEt = (EditText) findViewById(R.id.uploadAy_serviceDetailEt);

		Intent preIntent = getIntent();
		boolean isOnlyTb = preIntent.getBooleanExtra("isOnlyTb", false);
		if (isOnlyTb) {
			Nav_zdBt.setVisibility(View.GONE);
			Nav_csBt.setVisibility(View.INVISIBLE);
			Nav_szBt.setVisibility(View.INVISIBLE);
		}

		// ��ȡwifiIp��ַ
		WifiManager wm = (WifiManager) getSystemService(WIFI_SERVICE);
		if (wm.isWifiEnabled()) {
			int ip_int = wm.getConnectionInfo().getIpAddress();
			String ip_str = intToIp(ip_int);
			uploadAy_ipEt.setText(ip_str);
			uploadAy_ipEt.setSelection(ip_str.length());
		} else {
			AlertDialog.Builder dialog = new AlertDialog.Builder(this);
			dialog.setMessage("����wifiû�д򿪣��������wifi");
			dialog.setPositiveButton("ȷ��", null);
			dialog.show();
		}
		handler = new Handler() {
			String alwaysInStr = "";

			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);

				if (uploadAy_detailEt.getVisibility() != View.VISIBLE) {
					uploadAy_detailEt.setVisibility(View.VISIBLE);
				}
				String log = alwaysInStr + msg.obj.toString();
				uploadAy_detailEt.setText(log);
				// uploadAy_detailEt.selectAll();
				uploadAy_detailEt.setSelection(log.length());
				if (msg.arg1 == 1) {
					alwaysInStr += msg.obj.toString() + "\r\n";
				}
			}
		};

		// �Ƿ��������Զ���������
		SystemParamDao spd = new SystemParamDao(getApplicationContext());
		SystemParam sp = spd.findFirstByName("autoBackup");
		if (sp != null) {
			if (sp.paramValue != null && sp.paramValue.trim().equals("true")) {
				uploadAy_isBackUpAuto.setChecked(true);
			}
		}

		drsjHandler = new Handler() {
			String alwaysInStr = "";

			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				if (msg.what == 0) {
					if (uploadAy_importDetailEt.getVisibility() != View.VISIBLE) {
						uploadAy_importDetailEt.setVisibility(View.VISIBLE);
					}
					String log = alwaysInStr + msg.obj.toString();
					uploadAy_importDetailEt.setText(log);
					// uploadAy_importDetailEt.selectAll();
					uploadAy_importDetailEt.setSelection(log.length());
					if (msg.arg1 == 1) {
						alwaysInStr += msg.obj.toString() + "\r\n";
					}
				} else {
					Toast.makeText(UploadActivity.this, msg.obj.toString(),
							Toast.LENGTH_LONG).show();
				}
			}
		};

		sjtbHandler = new Handler() {
			String alwaysInStr = "";

			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				if (msg.what == 0) {
					if (uploadAy_serviceDetailEt.getVisibility() != View.VISIBLE) {
						uploadAy_serviceDetailEt.setVisibility(View.VISIBLE);
					}
					String log = alwaysInStr + msg.obj.toString();
					uploadAy_serviceDetailEt.setText(log);
					// uploadAy_importDetailEt.selectAll();
					uploadAy_serviceDetailEt.setSelection(log.length());
					if (msg.arg1 == 1) {
						alwaysInStr += msg.obj.toString() + "\r\n";
					}
				} else {
					Toast.makeText(UploadActivity.this, msg.obj.toString(),
							Toast.LENGTH_LONG).show();
				}
			}
		};
		registerListener();
	}

	private void writeLog(String msg) {
		if (uploadAy_importDetailEt.getVisibility() != View.VISIBLE) {
			uploadAy_importDetailEt.setVisibility(View.VISIBLE);
		}
		String log = uploadAy_importDetailEt.getText().toString() + "\r\n"
				+ msg;
		uploadAy_importDetailEt.setText(log);
		// uploadAy_importDetailEt.selectAll();
		uploadAy_importDetailEt.setSelection(log.length());
	}

	private void registerListener() {
		Nav_zdBt.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setClass(getApplicationContext(), MainActivity.class);
				startActivity(intent);
				UploadActivity.this.finish();
			}
		});
		Nav_scBt.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setClass(getApplicationContext(), UploadActivity.class);
				startActivity(intent);
				UploadActivity.this.finish();
			}
		});
		Nav_csBt.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setClass(getApplicationContext(), CsListActivity.class);
				startActivity(intent);
				UploadActivity.this.finish();
			}
		});
		Nav_szBt.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setClass(getApplicationContext(), SzActivity.class);
				startActivity(intent);
				// UploadActivity.this.finish();
			}
		});
		uploadAy_synParamBt.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				uploadAy_detailEt.setText("");
				uploadAy_detailEt.setVisibility(View.GONE);
				ipStr = uploadAy_ipEt.getText().toString().trim();
				// uploadAy_detailEt.setVisibility(View.VISIBLE);
				new Thread(new tbcsRunnable()).start();
			}
		});
		uploadAy_beginBt.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				uploadAy_detailEt.setText("");
				uploadAy_detailEt.setVisibility(View.GONE);
				isDel = uploadAy_isDelCb.isChecked();
				ipStr = uploadAy_ipEt.getText().toString().trim();
				// uploadAy_detailEt.setVisibility(View.VISIBLE);
				new Thread(new tbzdRunnable()).start();
			}
		});
		uploadAy_isBackUpAuto
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						// TODO Auto-generated method stub
						SystemParamDao spd = new SystemParamDao(
								getApplicationContext());
						SystemParam sp = spd
								.findFirstByName(SystemParamNames.autoBackup);
						boolean isNew = false;
						if (sp == null) {
							isNew = true;
							sp = new SystemParam();
							sp.paramName = SystemParamNames.autoBackup;
						}
						if (isChecked) {
							sp.paramValue = "true";
						} else {
							sp.paramValue = "false";
						}
						if (isNew) {
							spd.insert(sp);
						} else {
							spd.update(sp);
						}
					}
				});
		uploadAy_importDataBt.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				uploadAy_importDetailEt.setText("");
				uploadAy_importDetailEt.setVisibility(View.GONE);
				new Thread(new drsjRunnable()).start();
			}
		});

		uploadAy_beginTbServiceBt.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if("ֹͣͬ������".equals(uploadAy_beginTbServiceBt.getText().toString().trim())){
					if(ssh!=null){
						if(ssh.serverIsActive){
							ssh.stopServer();
						}
						ssh=null;
					}
					uploadAy_beginTbServiceBt.setText("����ͬ������");
					uploadAy_serviceDetailEt.setText("");
					uploadAy_serviceDetailEt.setVisibility(View.GONE);
				}else{
					String ipStr=getIp();
					if(ipStr==null){
						AlertDialog.Builder dialog = new AlertDialog.Builder(UploadActivity.this);
						dialog.setMessage("����wifiû�д򿪣��������wifi");
						dialog.setPositiveButton("ȷ��", null);
						dialog.show();
						return;
					}
					uploadAy_beginTbServiceBt.setText("ֹͣͬ������");
					
					String log="����ipΪ��"+ipStr+"\r\n����ȴ�������......";
					TbSj tbsj=new TbSj();
					tbsj.writeLog(log, 0, 1);
					ssh = new SocketServerHelper(tbsj);
					ssh.startServer();
				}
			}
		});
	}

	/*******************ͬ������ʱ�ķ����*******************/
	class TbSj implements OnSocketConnect {
		private void writeLog(String str, int what) {
			Message msg = new Message();
			msg.obj = str;
			msg.what = what;
			sjtbHandler.sendMessage(msg);
		}

		public void writeLog(String str, int what, int arg1) {
			Message msg = new Message();
			msg.obj = str;
			msg.what = what;
			msg.arg1 = arg1;
			sjtbHandler.sendMessage(msg);
		}

		@Override
		public void doTb(Socket socket) {
			try {
				if (socket.isConnected()) {
					// ����һ����Ϣ
					BufferedReader br = new BufferedReader(
							new InputStreamReader(socket.getInputStream(),
									"GBK"));
					StringBuilder sb = new StringBuilder("");
					char[] buffer = new char[32];
					br.read(buffer, 0, 32);
					String str = new String(buffer).trim();
					// String str=br.readLine();
					System.out.println("recive string:" + str.trim());
					if (str.trim().equals("getCs")) {// ������ͬ�����ͻ���
						System.out.println("getCs");
						CanShuDao csd = new CanShuDao(getApplicationContext());
						List<CanShu> csList = csd.findAll();
						int size = csList.size();
						// ȡ����,�����Ͳ�����¼��
						BufferedWriter bw = new BufferedWriter(
								new OutputStreamWriter(
										socket.getOutputStream(), "GBK"));
						bw.write("" + size);
						bw.flush();

						writeLog("��ʼͬ������������ͻ��˷��Ͳ���"+size+"��",0,1);
						// ѭ�����Ͳ���
						for (int i = 0; i < size; i++) {
							br.read(buffer, 0, 32);
							str = new String(buffer).trim();
							if (str.trim().equals("")
									|| str.trim().equals("end")) {
								break;
							}
							// ���Ͳ���
							sb = new StringBuilder("");
							CanShu cs = csList.get(i);
							sb.append(cs.lb + ";");// ���
							sb.append(cs.xx + ";");// ѡ��
							sb.append(cs.by + ";");// ����

							bw.write(sb.toString());
							bw.flush();
							writeLog("��ͻ��˷��Ͳ�����"+sb.toString(),0);
						}
						writeLog("ͬ��������ɣ�",0,1);
					} else {// �ͻ����ϴ��˵�����
						System.out.println("shangchuang-->" + str);
						int totalCount = 0;
						try {
							totalCount = Integer.parseInt(str);
						} catch (Exception ex) {
							ex.printStackTrace();
						}

						String rStr = "true";
						BufferedWriter bw = new BufferedWriter(
								new OutputStreamWriter(
										socket.getOutputStream(), "GBK"));
						bw.write(rStr);
						bw.flush();

						writeLog("��ʼͬ���˵��������տͻ��˴�����"+totalCount+"���˵���",0,1);
						
						ZhangDanDao zdd = new ZhangDanDao(getApplicationContext());
						int count=0;
						for (int i = 0; i < totalCount; i++) {
							// ��ȡ�˵�
							char[] msgBuffer = new char[1024];
							br.read(msgBuffer, 0, 1024);
							str = new String(msgBuffer).trim();
							writeLog("��ȡ���˵���" + str, 0);
							String[] zd = str.split(";");
							System.out.println("zd.length-->" + zd.length);
							if (zd.length >= 8) {
								ZhangDan zhangdan = new ZhangDan();
								zhangdan.fkr = zd[0];// ������
								zhangdan.rq = zd[1];// ����
								zhangdan.yt = zd[2];// ��;
								double je = 0;
								try {
									je = Double.parseDouble(zd[3]);
								} catch (Exception ex) {
								}
								if (je == 0) {
									writeLog("����ȷ", 0);
									continue;
								}
								zhangdan.je = je;// ���
								zhangdan.yqr = zd[4];// ��Ǯ��
								zhangdan.bz=zd[5];//��ע
								zhangdan.szbz = zd[6];// ���˱�־
								zhangdan.jzr = zd[7];// ������
								if(zd.length>=9){
									int status = -1;
									try {
										status = Integer.parseInt(zd[8]);
									} catch (Exception ex) {
									}
									if (status < 0 || status > 2) {
										writeLog("״̬����ȷ", 0);
										continue;
									}
									zhangdan.status = status;// ״̬��0��ʼ̬��1ȷ��,2�ϴ���������
								}else{
									zhangdan.status =1;
								}
								if (zd.length >= 10) {
									zhangdan.jemx = zd[9];// �����ϸ
								}
								int recode = zdd.insertFromExternal(zhangdan);
								if (recode == 0) {
									count++;
									writeLog("д���˵��ɹ�", 0);
								} else if (recode == 1) {
									writeLog("�Ѵ�����ͬ�˵������Ը�����¼", 0);
								} else {
									writeLog("д���˵�ʧ��", 0);
								}
							} else {
								writeLog("���ݸ�ʽ����ȷ��", 0);
							}

							// д����Ӧ
							bw.write(rStr);
							bw.flush();
						}
						if(count>0){
							zdd.writeBack();
						}
						writeLog("ͬ���˵���ɣ����ι��ϴ�"+count+"���˵���", 0,1);

					}

				} else {
					writeLog("�����ԶϿ���", 0, 1);
				}
			} catch (SocketException ex) {
				ex.printStackTrace();
			} catch (Exception ex) {
				ex.printStackTrace();
			} finally {
				// �ر�socket
				try {
					System.out.println("�ر�socket");
					socket.close();
					socket = null;
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			System.out.println("doTb");
		}
	}
	
	/**************���ļ��е���������˵�****************/
	class drsjRunnable implements Runnable {
		private void writeLog(String str, int what) {
			Message msg = new Message();
			msg.obj = str;
			msg.what = what;
			drsjHandler.sendMessage(msg);
		}

		private void writeLog(String str, int what, int arg1) {
			Message msg = new Message();
			msg.obj = str;
			msg.what = what;
			msg.arg1 = arg1;
			drsjHandler.sendMessage(msg);
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			FileReadWrite frw = new FileReadWrite();
			File[] files = frw.getFiles("ljjz");
			boolean noBackFile = true;
			if (files != null && files.length > 0) {
				for (File file : files) {
					ArrayList<String> strList = frw
							.getStringArrayFromFile(file);
					System.out.println("strList-->" + strList);
					if (strList != null && strList.size() > 0) {
						int size = strList.size();
						String fileHeader = strList.get(0).trim();
						System.out.println("fileHeader-->" + fileHeader);
						if ("cs".equals(fileHeader)) {// �������
							noBackFile = false;
							writeLog("��ʼ�������", 0, 1);
							CanShuDao csd = new CanShuDao(getApplicationContext());
							int count = 0;
							for (int i = 1; i < size; i++) {
								String str = strList.get(i);
								writeLog("��ȡ��������" + str, 0);
								String[] cs = str.split(";");
								System.out.println("cs.length-->" + cs.length);
								if (cs.length >= 2) {
									CanShu canshu = new CanShu();
									canshu.lb = cs[0];
									canshu.xx = cs[1];
									if (cs.length >= 3) {
										canshu.by = cs[2];
									}
									if ("������".equals(canshu.lb)
											|| "��Ǯ��".equals(canshu.lb)
											|| "��;".equals(canshu.lb)) {										
										int recode = csd.insertFromExternal(canshu);
										if (recode == 0) {
											count++;
											writeLog("д������ɹ�", 0);
										} else if (recode == 1) {
											writeLog("�Ѵ�����ͬ���������Ը�����¼", 0);
										} else {
											writeLog("д�����ʧ��", 0);
										}
									} else {
										writeLog("���������ȷ��", 0);
									}
								} else {
									writeLog("���ݸ�ʽ����ȷ��", 0);
								}
							}
							if(count>0){
								csd.writeBack();
							}
							writeLog("���������ɣ����ι����������¼ " + count + " ��", 0, 1);
						} else if ("zd".equals(fileHeader)) {// �����˵�
							noBackFile = false;
							writeLog("��ʼ�����˵�", 0, 1);
							ZhangDanDao zdd = new ZhangDanDao(getApplicationContext());
							int count = 0;
							for (int i = 1; i < size; i++) {
								String str = strList.get(i);
								writeLog("��ȡ���˵���" + str, 0);
								String[] zd = str.split(";");
								System.out.println("zd.length-->" + zd.length);
								if (zd.length >= 9) {
									ZhangDan zhangdan = new ZhangDan();
									zhangdan.fkr = zd[0];// ������
									zhangdan.rq = zd[1];// ����
									zhangdan.yt = zd[2];// ��;
									double je = 0;
									try {
										je = Double.parseDouble(zd[3]);
									} catch (Exception ex) {
									}
									if (je == 0) {
										writeLog("����ȷ", 0);
										continue;
									}
									zhangdan.je = je;// ���
									zhangdan.jemx = zd[4];// �����ϸ
									zhangdan.yqr = zd[5];// ��Ǯ��
									zhangdan.szbz = zd[6];// ���˱�־
									zhangdan.jzr = zd[7];// ������
									int status = -1;
									try {
										status = Integer.parseInt(zd[8]);
									} catch (Exception ex) {
									}
									if (status < 0 || status > 2) {
										writeLog("״̬����ȷ", 0);
										continue;
									}
									zhangdan.status = status;// ״̬��0��ʼ̬��1ȷ��,2�ϴ���������
									if (zd.length >= 10) {
										zhangdan.bz = zd[9];// ��ע
									}
									
									int recode = zdd.insertFromExternal(zhangdan);
									if (recode == 0) {
										count++;
										writeLog("д���˵��ɹ�", 0);
									} else if (recode == 1) {
										writeLog("�Ѵ�����ͬ�˵������Ը�����¼", 0);
									} else {
										writeLog("д���˵�ʧ��", 0);
									}
								} else {
									writeLog("���ݸ�ʽ����ȷ��", 0);
								}
							}
							if(count>0){
								zdd.writeBack();
							}
							writeLog("�����˵���ɣ����ι������˵���¼ " + count + " ��", 0, 1);
						}
					}
				}
				if (noBackFile) {
					writeLog("�����ļ���ljjz��û����Ч�ı����ļ���", 1);
				}
			} else {
				writeLog("�����ļ���ljjz�����ڻ��ļ�����û���ļ���", 1);
			}
		}

	}
	/***********������������˵�******************/
	class tbzdRunnable implements Runnable {
		private void writeLog(String str) {
			Message msg = new Message();
			msg.obj = str;
			handler.sendMessage(msg);
		}

		private void writeLog(String str, int arg1) {
			Message msg = new Message();
			msg.obj = str;
			msg.arg1 = arg1;
			handler.sendMessage(msg);
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			if (ipStr == null || "".equals(ipStr)) {
				writeLog("����д������IP��ַ");
				return;
			}
			SocketHelper socketHelper = new SocketHelper(ipStr, 8000);
			if (!socketHelper.connect()) {
				writeLog("���ӷ�����ʧ�ܣ����������IP��ַ�Ƿ���д��ȷ");
				return;
			}
			writeLog("�ϴ��˵���ʼ", 1);
			ArrayList<ZhangDan> zdList = new ArrayList<ZhangDan>();
			ZhangDanDao zdd = new ZhangDanDao(getApplicationContext());
			int totalCount = zdd.findAllByStatus(zdList, 1);
			int count = zdList.size();			
			boolean isOk = socketHelper.send(count + "");
			System.out.println(isOk);
			if (!isOk) {
				writeLog("���ͼ�¼����ʧ��");
				return;
			}
			writeLog("���ϴ��˵�������" + count, 1);
			for (int i = 0; i < count; i++) {
				// System.out.print("��Ϣ"+i+":");
				ZhangDan zd = zdList.get(i);
				StringBuilder oneRecord = new StringBuilder("");
				oneRecord.append(zd.fkr + ";");// ������
				oneRecord.append(zd.rq + ";");// ����
				oneRecord.append(zd.yt + ";");// ��;
				oneRecord.append(zd.je + ";");// ���
				oneRecord.append(zd.yqr + ";");// ��Ǯ��
				oneRecord.append(zd.bz + ";");// ��ע
				oneRecord.append(zd.szbz + ";");// ���˱�־
				oneRecord.append(zd.jzr + ";");// ������
				oneRecord.append(zd.status + ";");// ״̬
				oneRecord.append(zd.jemx + ";");// �����ϸ

				writeLog("�����˵����ݣ�" + oneRecord.toString());
				isOk = socketHelper.send(oneRecord.toString());
				if (isOk) {
					if (isDel) {// ɾ������
						zdd.delete(zd);
					} else {// �޸�״̬
						zd.status = 2;
						zdd.update(zd);
					}
					writeLog("�����˵����ݳɹ�");
				}
				System.out.println(isOk);
			}
			socketHelper.close();
			writeLog("�ϴ��˵����!", 1);
		}
	}

	class tbcsRunnable implements Runnable {
		private void writeLog(String str) {
			Message msg = new Message();
			msg.obj = str;
			handler.sendMessage(msg);
		}

		private void writeLog(String str, int arg1) {
			Message msg = new Message();
			msg.obj = str;
			msg.arg1 = arg1;
			handler.sendMessage(msg);
		}

		@Override
		public void run() {
			if (ipStr == null || "".equals(ipStr)) {
				writeLog("����д������IP��ַ");
				return;
			}
			// TODO Auto-generated method stub
			SocketHelper socketHelper = new SocketHelper(ipStr, 8000);
			if (!socketHelper.connect()) {
				writeLog("���ӷ�����ʧ�ܣ����������IP��ַ�Ƿ���д��ȷ");
				return;
			}
			writeLog("���ز�����ʼ!", 1);
			String count_str = socketHelper.receive();
			int countFromServer = 0;
			try {
				countFromServer = Integer.parseInt(count_str);
			} catch (Exception ex) {
			}
			writeLog("��Ҫ���صĲ���������" + countFromServer, 1);
			// System.out.println(count);
			CanShuDao csd = new CanShuDao(getApplicationContext());
			int count=0;
			for (int i = 0; i < countFromServer; i++) {
				String str = socketHelper.receive();
				// System.out.println("��Ϣ:"+str);
				writeLog("���յ����ݣ�" + str);
				String[] cs = str.split(";");
				System.out.println("cs.length-->" + cs.length);
				if (cs.length >= 2) {
					CanShu canshu = new CanShu();
					canshu.lb = cs[0];
					canshu.xx = cs[1];
					if (cs.length >= 3) {
						canshu.by = cs[2];
					}
					
					int recode = csd.insertFromExternal(canshu);
					if (recode == 0) {
						count++;
						writeLog("д������ɹ�");
					} else if (recode == 1) {
						writeLog("�Ѵ�����ͬ���������Ը�����¼");
					} else {
						writeLog("д�����ʧ��");
					}
				} else {
					writeLog("���յ������ݸ�ʽ����ȷ��");
				}
			}
			if(count>0){
				csd.writeBack();
			}
			writeLog("���ز�����ɣ���д�����"+count+"����", 1);
			socketHelper.close();
		}
	}

	class tbServerRunnable implements Runnable {
		private void writeLog(String str) {
			Message msg = new Message();
			msg.obj = str;
			handler.sendMessage(msg);
		}

		private void writeLog(String str, int arg1) {
			Message msg = new Message();
			msg.obj = str;
			msg.arg1 = arg1;
			handler.sendMessage(msg);
		}

		@Override
		public void run() {

		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) { // ���µ������BACK��ͬʱû���ظ�
			AlertDialog.Builder dialog = new AlertDialog.Builder(
					UploadActivity.this);
			dialog.setTitle("�˳�");
			dialog.setMessage("ȷ��Ҫ�˳���");
			dialog.setNegativeButton("ȡ��", null);
			dialog.setPositiveButton("ȷ��",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							UploadActivity.this.finish();
							Intent intent = new Intent();
							intent.setClass(getApplicationContext(),
									MakeBackupService.class);
							startService(intent);
						}
					});
			dialog.show();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	@Override
	protected void onDestroy() {
		if(ssh!=null){
			if(ssh.serverIsActive){
				ssh.stopServer();
			}
			ssh=null;
		}
		super.onDestroy();
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if(item.getItemId()==R.id.action_exit){//�˳�
			AlertDialog.Builder dialog=new AlertDialog.Builder(UploadActivity.this);
			dialog.setTitle("�˳�");
			dialog.setMessage("ȷ��Ҫ�˳���");
			dialog.setNegativeButton("ȡ��", null);
			dialog.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					UploadActivity.this.finish();
					Intent intent=new Intent();
					intent.setClass(getApplicationContext(), MakeBackupService.class);
					startService(intent);
				}
			});
			dialog.show();
		    return true;
		}
		if(item.getItemId()==R.id.action_help){//�򿪰�������
			Intent intent=new Intent();
			intent.setClass(UploadActivity.this, HelpActivity.class);
			startActivity(intent);
		}
		return super.onOptionsItemSelected(item);
	}
	
}
