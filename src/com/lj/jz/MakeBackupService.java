package com.lj.jz;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.lj.jz.data.CanShu;
import com.lj.jz.data.CanShuDao;
import com.lj.jz.data.SystemParam;
import com.lj.jz.data.SystemParamDao;
import com.lj.jz.data.SystemParamNames;
import com.lj.jz.data.ZhangDan;
import com.lj.jz.data.ZhangDanDao;
import com.lj.jz.filewr.FileReadWrite;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

public class MakeBackupService extends Service {
	Thread thread=null;
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void onStart(Intent intent, int startId) {
		// TODO Auto-generated method stub
		thread=new Thread(new MyRuable());
		thread.start();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		return super.onStartCommand(intent, flags, startId);
	}
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		if(thread!=null&&!thread.isInterrupted())
		{
			thread.interrupt();
		}
		super.onDestroy();
	}
	class MyRuable implements Runnable{

		@Override
		public void run() {
			System.out.println("run service");
			Context context=getApplicationContext();
			SystemParamDao spd=new SystemParamDao(context);
			SystemParam sp=spd.findFirstByName(SystemParamNames.autoBackup);
			if(sp!=null&&"true".equals(sp.paramValue)){
				SystemParam sp1=spd.findFirstByName(SystemParamNames.needBackCanShu);
				if(sp1!=null&&"true".equals(sp1.paramValue)){//���ݲ���
					String dirName="ljjz/";
					String fileName="cs"+(new Date()).getTime();
					FileReadWrite frw=new FileReadWrite();
					File file=frw.createNewFile(dirName, fileName);
					frw.writeFile("cs", file);
					CanShuDao csd=new CanShuDao(context); 
					List<CanShu> csList=csd.findAll();
					for(CanShu cs:csList){
						String csStr=cs.lb+";";
						csStr+=cs.xx+";";
						csStr+=cs.by+"";
						frw.writeFile(csStr, file);
					}
					frw.deleteFile("ljjz/", "cs.txt");
					frw.rename(file, "ljjz/", "cs.txt");
					sp1.paramValue="false";
					spd.update(sp1);
				}
				SystemParam sp2=spd.findFirstByName(SystemParamNames.needBackZhangDan);
				if(sp2!=null&&"true".equals(sp2.paramValue)){//�����˵�
					String dirName="ljjz/";
					String fileName="zd"+(new Date()).getTime();
					FileReadWrite frw=new FileReadWrite();
					File file=frw.createNewFile(dirName, fileName);
					frw.writeFile("zd", file);
					ZhangDanDao zdd=new ZhangDanDao(context); 
					List<ZhangDan> zdList=zdd.findAll();
					for(ZhangDan zhangdan:zdList){
						String zdStr="";
						zdStr+=zhangdan.fkr+";";//������
						zdStr+=zhangdan.rq+";";//����
						zdStr+=zhangdan.yt+";";//��;
						zdStr+=zhangdan.je+";";//���
						zdStr+=zhangdan.jemx+";";//�����ϸ
						zdStr+=zhangdan.yqr+";";//��Ǯ��
						zdStr+=zhangdan.szbz+";";//���˱�־
						zdStr+=zhangdan.jzr+";";//������
						zdStr+=zhangdan.status+";";//״̬��0��ʼ̬��1ȷ��,2�ϴ���������
						zdStr+=zhangdan.bz;
						frw.writeFile(zdStr, file);
					}
					frw.deleteFile("ljjz/", "zd.txt");
					frw.rename(file, "ljjz/", "zd.txt");
					sp2.paramValue="false";
					spd.update(sp2);
				}
			}
		}
		
	}
}
