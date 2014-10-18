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

	// 将获取的int转为真正的ip地址,参考的网上的，修改了下
	private String intToIp(int i) {
		return (i & 0xFF) + "." + ((i >> 8) & 0xFF) + "." + ((i >> 16) & 0xFF)
				+ "." + ((i >> 24) & 0xFF);
	}
	private String getIp(){
		// 获取wifiIp地址
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

		// 去掉标题
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

		// 获取wifiIp地址
		WifiManager wm = (WifiManager) getSystemService(WIFI_SERVICE);
		if (wm.isWifiEnabled()) {
			int ip_int = wm.getConnectionInfo().getIpAddress();
			String ip_str = intToIp(ip_int);
			uploadAy_ipEt.setText(ip_str);
			uploadAy_ipEt.setSelection(ip_str.length());
		} else {
			AlertDialog.Builder dialog = new AlertDialog.Builder(this);
			dialog.setMessage("您的wifi没有打开，建议你打开wifi");
			dialog.setPositiveButton("确定", null);
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

		// 是否设置了自动备份数据
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
				if("停止同步服务".equals(uploadAy_beginTbServiceBt.getText().toString().trim())){
					if(ssh!=null){
						if(ssh.serverIsActive){
							ssh.stopServer();
						}
						ssh=null;
					}
					uploadAy_beginTbServiceBt.setText("开启同步服务");
					uploadAy_serviceDetailEt.setText("");
					uploadAy_serviceDetailEt.setVisibility(View.GONE);
				}else{
					String ipStr=getIp();
					if(ipStr==null){
						AlertDialog.Builder dialog = new AlertDialog.Builder(UploadActivity.this);
						dialog.setMessage("您的wifi没有打开，建议你打开wifi");
						dialog.setPositiveButton("确定", null);
						dialog.show();
						return;
					}
					uploadAy_beginTbServiceBt.setText("停止同步服务");
					
					String log="本机ip为："+ipStr+"\r\n服务等待连接中......";
					TbSj tbsj=new TbSj();
					tbsj.writeLog(log, 0, 1);
					ssh = new SocketServerHelper(tbsj);
					ssh.startServer();
				}
			}
		});
	}

	/*******************同步数据时的服务端*******************/
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
					// 接收一个消息
					BufferedReader br = new BufferedReader(
							new InputStreamReader(socket.getInputStream(),
									"GBK"));
					StringBuilder sb = new StringBuilder("");
					char[] buffer = new char[32];
					br.read(buffer, 0, 32);
					String str = new String(buffer).trim();
					// String str=br.readLine();
					System.out.println("recive string:" + str.trim());
					if (str.trim().equals("getCs")) {// 将参数同步到客户端
						System.out.println("getCs");
						CanShuDao csd = new CanShuDao(getApplicationContext());
						List<CanShu> csList = csd.findAll();
						int size = csList.size();
						// 取参数,并发送参数记录数
						BufferedWriter bw = new BufferedWriter(
								new OutputStreamWriter(
										socket.getOutputStream(), "GBK"));
						bw.write("" + size);
						bw.flush();

						writeLog("开始同步参数，将向客户端发送参数"+size+"条",0,1);
						// 循环发送参数
						for (int i = 0; i < size; i++) {
							br.read(buffer, 0, 32);
							str = new String(buffer).trim();
							if (str.trim().equals("")
									|| str.trim().equals("end")) {
								break;
							}
							// 发送参数
							sb = new StringBuilder("");
							CanShu cs = csList.get(i);
							sb.append(cs.lb + ";");// 类别
							sb.append(cs.xx + ";");// 选项
							sb.append(cs.by + ";");// 备用

							bw.write(sb.toString());
							bw.flush();
							writeLog("向客户端发送参数："+sb.toString(),0);
						}
						writeLog("同步参数完成！",0,1);
					} else {// 客户端上传账单过来
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

						writeLog("开始同步账单，将接收客户端传来的"+totalCount+"条账单！",0,1);
						
						ZhangDanDao zdd = new ZhangDanDao(getApplicationContext());
						int count=0;
						for (int i = 0; i < totalCount; i++) {
							// 读取账单
							char[] msgBuffer = new char[1024];
							br.read(msgBuffer, 0, 1024);
							str = new String(msgBuffer).trim();
							writeLog("读取到账单：" + str, 0);
							String[] zd = str.split(";");
							System.out.println("zd.length-->" + zd.length);
							if (zd.length >= 8) {
								ZhangDan zhangdan = new ZhangDan();
								zhangdan.fkr = zd[0];// 付款人
								zhangdan.rq = zd[1];// 日期
								zhangdan.yt = zd[2];// 用途
								double je = 0;
								try {
									je = Double.parseDouble(zd[3]);
								} catch (Exception ex) {
								}
								if (je == 0) {
									writeLog("金额不正确", 0);
									continue;
								}
								zhangdan.je = je;// 金额
								zhangdan.yqr = zd[4];// 用钱人
								zhangdan.bz=zd[5];//备注
								zhangdan.szbz = zd[6];// 算账标志
								zhangdan.jzr = zd[7];// 记账人
								if(zd.length>=9){
									int status = -1;
									try {
										status = Integer.parseInt(zd[8]);
									} catch (Exception ex) {
									}
									if (status < 0 || status > 2) {
										writeLog("状态不正确", 0);
										continue;
									}
									zhangdan.status = status;// 状态，0初始态，1确认,2上传到服务器
								}else{
									zhangdan.status =1;
								}
								if (zd.length >= 10) {
									zhangdan.jemx = zd[9];// 金额明细
								}
								int recode = zdd.insertFromExternal(zhangdan);
								if (recode == 0) {
									count++;
									writeLog("写入账单成功", 0);
								} else if (recode == 1) {
									writeLog("已存在相同账单，忽略该条记录", 0);
								} else {
									writeLog("写入账单失败", 0);
								}
							} else {
								writeLog("数据格式不正确！", 0);
							}

							// 写入响应
							bw.write(rStr);
							bw.flush();
						}
						if(count>0){
							zdd.writeBack();
						}
						writeLog("同步账单完成，本次共上传"+count+"条账单！", 0,1);

					}

				} else {
					writeLog("连接以断开！", 0, 1);
				}
			} catch (SocketException ex) {
				ex.printStackTrace();
			} catch (Exception ex) {
				ex.printStackTrace();
			} finally {
				// 关闭socket
				try {
					System.out.println("关闭socket");
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
	
	/**************从文件中导入参数和账单****************/
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
						if ("cs".equals(fileHeader)) {// 导入参数
							noBackFile = false;
							writeLog("开始导入参数", 0, 1);
							CanShuDao csd = new CanShuDao(getApplicationContext());
							int count = 0;
							for (int i = 1; i < size; i++) {
								String str = strList.get(i);
								writeLog("读取到参数：" + str, 0);
								String[] cs = str.split(";");
								System.out.println("cs.length-->" + cs.length);
								if (cs.length >= 2) {
									CanShu canshu = new CanShu();
									canshu.lb = cs[0];
									canshu.xx = cs[1];
									if (cs.length >= 3) {
										canshu.by = cs[2];
									}
									if ("付款人".equals(canshu.lb)
											|| "用钱人".equals(canshu.lb)
											|| "用途".equals(canshu.lb)) {										
										int recode = csd.insertFromExternal(canshu);
										if (recode == 0) {
											count++;
											writeLog("写入参数成功", 0);
										} else if (recode == 1) {
											writeLog("已存在相同参数，忽略该条记录", 0);
										} else {
											writeLog("写入参数失败", 0);
										}
									} else {
										writeLog("参数类别不正确！", 0);
									}
								} else {
									writeLog("数据格式不正确！", 0);
								}
							}
							if(count>0){
								csd.writeBack();
							}
							writeLog("导入参数完成，本次共导入参数记录 " + count + " 条", 0, 1);
						} else if ("zd".equals(fileHeader)) {// 导入账单
							noBackFile = false;
							writeLog("开始导入账单", 0, 1);
							ZhangDanDao zdd = new ZhangDanDao(getApplicationContext());
							int count = 0;
							for (int i = 1; i < size; i++) {
								String str = strList.get(i);
								writeLog("读取到账单：" + str, 0);
								String[] zd = str.split(";");
								System.out.println("zd.length-->" + zd.length);
								if (zd.length >= 9) {
									ZhangDan zhangdan = new ZhangDan();
									zhangdan.fkr = zd[0];// 付款人
									zhangdan.rq = zd[1];// 日期
									zhangdan.yt = zd[2];// 用途
									double je = 0;
									try {
										je = Double.parseDouble(zd[3]);
									} catch (Exception ex) {
									}
									if (je == 0) {
										writeLog("金额不正确", 0);
										continue;
									}
									zhangdan.je = je;// 金额
									zhangdan.jemx = zd[4];// 金额明细
									zhangdan.yqr = zd[5];// 用钱人
									zhangdan.szbz = zd[6];// 算账标志
									zhangdan.jzr = zd[7];// 记账人
									int status = -1;
									try {
										status = Integer.parseInt(zd[8]);
									} catch (Exception ex) {
									}
									if (status < 0 || status > 2) {
										writeLog("状态不正确", 0);
										continue;
									}
									zhangdan.status = status;// 状态，0初始态，1确认,2上传到服务器
									if (zd.length >= 10) {
										zhangdan.bz = zd[9];// 备注
									}
									
									int recode = zdd.insertFromExternal(zhangdan);
									if (recode == 0) {
										count++;
										writeLog("写入账单成功", 0);
									} else if (recode == 1) {
										writeLog("已存在相同账单，忽略该条记录", 0);
									} else {
										writeLog("写入账单失败", 0);
									}
								} else {
									writeLog("数据格式不正确！", 0);
								}
							}
							if(count>0){
								zdd.writeBack();
							}
							writeLog("导入账单完成，本次共导入账单记录 " + count + " 条", 0, 1);
						}
					}
				}
				if (noBackFile) {
					writeLog("备份文件夹ljjz中没有有效的备份文件！", 1);
				}
			} else {
				writeLog("备份文件夹ljjz不存在或文件夹中没有文件！", 1);
			}
		}

	}
	/***********向服务器发送账单******************/
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
				writeLog("请填写服务器IP地址");
				return;
			}
			SocketHelper socketHelper = new SocketHelper(ipStr, 8000);
			if (!socketHelper.connect()) {
				writeLog("连接服务器失败，请检查服务器IP地址是否填写正确");
				return;
			}
			writeLog("上传账单开始", 1);
			ArrayList<ZhangDan> zdList = new ArrayList<ZhangDan>();
			ZhangDanDao zdd = new ZhangDanDao(getApplicationContext());
			int totalCount = zdd.findAllByStatus(zdList, 1);
			int count = zdList.size();			
			boolean isOk = socketHelper.send(count + "");
			System.out.println(isOk);
			if (!isOk) {
				writeLog("发送记录条数失败");
				return;
			}
			writeLog("将上传账单条数：" + count, 1);
			for (int i = 0; i < count; i++) {
				// System.out.print("消息"+i+":");
				ZhangDan zd = zdList.get(i);
				StringBuilder oneRecord = new StringBuilder("");
				oneRecord.append(zd.fkr + ";");// 付款人
				oneRecord.append(zd.rq + ";");// 日期
				oneRecord.append(zd.yt + ";");// 用途
				oneRecord.append(zd.je + ";");// 金额
				oneRecord.append(zd.yqr + ";");// 用钱人
				oneRecord.append(zd.bz + ";");// 备注
				oneRecord.append(zd.szbz + ";");// 算账标志
				oneRecord.append(zd.jzr + ";");// 记账人
				oneRecord.append(zd.status + ";");// 状态
				oneRecord.append(zd.jemx + ";");// 金额明细

				writeLog("发送账单数据：" + oneRecord.toString());
				isOk = socketHelper.send(oneRecord.toString());
				if (isOk) {
					if (isDel) {// 删除数据
						zdd.delete(zd);
					} else {// 修改状态
						zd.status = 2;
						zdd.update(zd);
					}
					writeLog("发送账单数据成功");
				}
				System.out.println(isOk);
			}
			socketHelper.close();
			writeLog("上传账单完成!", 1);
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
				writeLog("请填写服务器IP地址");
				return;
			}
			// TODO Auto-generated method stub
			SocketHelper socketHelper = new SocketHelper(ipStr, 8000);
			if (!socketHelper.connect()) {
				writeLog("连接服务器失败，请检查服务器IP地址是否填写正确");
				return;
			}
			writeLog("下载参数开始!", 1);
			String count_str = socketHelper.receive();
			int countFromServer = 0;
			try {
				countFromServer = Integer.parseInt(count_str);
			} catch (Exception ex) {
			}
			writeLog("将要下载的参数数量：" + countFromServer, 1);
			// System.out.println(count);
			CanShuDao csd = new CanShuDao(getApplicationContext());
			int count=0;
			for (int i = 0; i < countFromServer; i++) {
				String str = socketHelper.receive();
				// System.out.println("消息:"+str);
				writeLog("接收到数据：" + str);
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
						writeLog("写入参数成功");
					} else if (recode == 1) {
						writeLog("已存在相同参数，忽略该条记录");
					} else {
						writeLog("写入参数失败");
					}
				} else {
					writeLog("接收到的数据格式不正确！");
				}
			}
			if(count>0){
				csd.writeBack();
			}
			writeLog("下载参数完成，共写入参数"+count+"条！", 1);
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
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) { // 按下的如果是BACK，同时没有重复
			AlertDialog.Builder dialog = new AlertDialog.Builder(
					UploadActivity.this);
			dialog.setTitle("退出");
			dialog.setMessage("确定要退出吗？");
			dialog.setNegativeButton("取消", null);
			dialog.setPositiveButton("确定",
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
		if(item.getItemId()==R.id.action_exit){//退出
			AlertDialog.Builder dialog=new AlertDialog.Builder(UploadActivity.this);
			dialog.setTitle("退出");
			dialog.setMessage("确定要退出吗？");
			dialog.setNegativeButton("取消", null);
			dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
				
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
		if(item.getItemId()==R.id.action_help){//打开帮助界面
			Intent intent=new Intent();
			intent.setClass(UploadActivity.this, HelpActivity.class);
			startActivity(intent);
		}
		return super.onOptionsItemSelected(item);
	}
	
}
