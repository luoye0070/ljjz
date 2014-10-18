package com.lj.jz.filewr;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

public class FileReadWrite {
	String SDCardRoot=null;//sd卡路径
	public FileReadWrite()
	{
		getSDPath();
	}
	private void getSDPath()
	{
		SDCardRoot=Environment.getExternalStorageDirectory().getAbsolutePath()+"/";
	}
	
	public String getSDCardRoot() {
		return SDCardRoot;
	}
	/*************************************************************
	 *检查文件是否存在
	 *************************************************************/
	public boolean isFileExist(String qfileName)
	{
		File file=new File(SDCardRoot+qfileName);
		return file.exists();
	}
	
	private File createDir(String dirName)
	{
		File file=new File(SDCardRoot+dirName);
		file.mkdirs();
		return file;
	}
	
	private File createFile(String dirName,String fileName)
	{
		File file=new File(SDCardRoot+dirName+fileName);
		try {
			file.createNewFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(SDCardRoot+dirName+fileName);
		return file;
	}
	
	/*****************************************************
	 * 将一个输入流数据写入文件
	 ******************************************************/
	public int writeFile(InputStream input,String fileName,String dirName)
	{
		int recode=0;
		File file=null;
		OutputStream output=null;
		System.out.println(dirName+fileName);
		if(!isFileExist(dirName))
		{
			createDir(dirName);
		}
		if(!isFileExist(dirName+fileName))
		{
			file=createFile(dirName, fileName);
		}
		else
		{
			return 1;//文件已经存在
		}
		if(file.canWrite())
		{
			try {
				output=new FileOutputStream(file);
				byte []buff=new byte[4*1024];
				int count=-1;
				while((count=input.read(buff))!=-1)
				{
					//output.write(buff);
					output.write(buff, 0, count);
					output.flush();
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				recode=-2;//IO异常
			}
			finally
			{
				try {
					if(output!=null)
						output.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		else
		{
			recode=-1;//文件不能写
		}
		return recode;//写文件成功
	}
	
	/***********************************************
	 * 获取文件读入流
	 **********************************************/
	public InputStream getFileInputStream(String dirName,String fileName)
	{
		InputStream input=null;
		try{
			File file=new File(SDCardRoot+dirName+fileName);
			//file.setReadable(true);
			input=new FileInputStream(file);
		}
		catch(IOException ex){
			ex.printStackTrace();
		}
		finally{
			
		}
		return input;
	}
	
	/****************************************************
	*将一个字符串写入文件
	****************************************************/
	public int writeFile(String data,String fileName,String dirName)
	{
		int reCode=0;
		File file=null;
		OutputStream output=null;
		BufferedWriter bw=null;
		System.out.println(dirName+fileName);
		if(!isFileExist(dirName))
		{
			createDir(dirName);
		}
		if(!isFileExist(dirName+fileName))
		{
			file=createFile(dirName, fileName);
		}
		else
		{
			file=new File(SDCardRoot+dirName+fileName);
		}
		if(file.canWrite())
		{
			try {
				output=new FileOutputStream(file,true);
				bw=new BufferedWriter(new OutputStreamWriter(output));
				bw.write(data+"\r\n");
				bw.flush();
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				reCode=-1;
			}
			finally{
				if(bw!=null)
				{
					try {
						bw.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						reCode=-1;
					}
				}
			}
		}
		else
		{
			reCode=-1;
		}
		return reCode;
	}
	
	/***************************************************
	 * 从文件中读取数据到字符列表中，以行分
	 ***************************************************/
	public ArrayList<String> getStringArrayFromFile(String dirName,String fileName)
	{
		ArrayList<String> strList=null;
		BufferedReader br=null;
		try{
			File file=new File(SDCardRoot+dirName+fileName);
			if(file.exists())
			{
				//file.setReadable(true);
				br=new BufferedReader(new InputStreamReader(new FileInputStream(file)));
				strList=new ArrayList<String>();
				String line=null;
				while((line=br.readLine())!=null)
				{
					strList.add(line);
				}
			}
		}
		catch(IOException ex){
			ex.printStackTrace();
		}
		finally{
			if(br!=null)
			{
				try {
					br.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return strList;
	}
	
	/***************************************************************
	 * 检查字符串是否在文件中
	 ***************************************************************/
	public boolean stringIsInFile(String data,String fileName,String dirName)
	{
		boolean isInFile=false;
		BufferedReader br=null;
		try{
			File file=new File(SDCardRoot+dirName+fileName);
			if(file.exists())
			{
				//file.setReadable(true);
				br=new BufferedReader(new InputStreamReader(new FileInputStream(file)));
				
				String line=null;
				while((line=br.readLine())!=null)
				{
					if(line.equals(data))
					{
						isInFile=true;
						break;
					}
				}
			}
		}
		catch(IOException ex){
			ex.printStackTrace();
		}
		finally{
			if(br!=null)
			{
				try {
					br.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return isInFile;
	}
	
	/***************************************************************
	 * 从SD卡获取图片
	 ***************************************************************/
	public Bitmap getImg(String path)
	{
		if(path==null||path.equals(""))
		{
			return null;
		}
		Bitmap bitmap=null;
		InputStream input=null;
		try{
			File file=new File(SDCardRoot+path);
//			if(file.length()>1024*1024)
//			{
//				return null;
//			}
			//file.setReadable(true);
			input=new FileInputStream(file);
			bitmap=BitmapFactory.decodeStream(input);
		}
		catch(IOException ex){
			ex.printStackTrace();
			bitmap=null;//发生IO异常时返回一个空图片对象
		}
		finally{
			
		}
		return bitmap;
	}
	
	/*****************************************************
	 * 将一个输入流数据写入文件
	 ******************************************************/
	public int writeFile(InputStream input,String fileName,String dirName,ProgressDialog pd)
	{
		int recode=0;
		File file=null;
		OutputStream output=null;
		System.out.println(dirName+fileName);
		if(!isFileExist(dirName))
		{
			createDir(dirName);
		}
		if(!isFileExist(dirName+fileName))
		{
			file=createFile(dirName, fileName);
		}
		else
		{
			file=new File(SDCardRoot+dirName+fileName);//文件已经存在,重新写文件
		}
		if(file.canWrite())
		{
			try {
				output=new FileOutputStream(file,false);
				byte []buff=new byte[4*1024];
				int count=-1;
				int total=0;
				while((count=input.read(buff))!=-1)
				{
					//output.write(buff);
					output.write(buff, 0, count);
					output.flush();
					total+=count;
					pd.setProgress(total);
					System.out.println(total);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				recode=-2;//IO异常
			}
			finally
			{
				try {
					if(output!=null)
						output.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		else
		{
			recode=-1;//文件不能写
		}
		return recode;//写文件成功
	}
	
	/****************************************
	 * 获取目录下所有文件
	 ***************************************/
	public File[] getFiles(String dirName){
		File file=new File(SDCardRoot+dirName);
		if(file.exists())
		{
			if(file.isDirectory()){
				return file.listFiles();
			}
		}		
		return null;
	}
	
	/***************************************************
	 * 从文件中读取数据到字符列表中，以行分
	 ***************************************************/
	public ArrayList<String> getStringArrayFromFile(File file)
	{
		ArrayList<String> strList=null;
		BufferedReader br=null;
		try{
			if(file!=null&&file.exists())
			{
				//file.setReadable(true);
				br=new BufferedReader(new InputStreamReader(new FileInputStream(file),"GBK"));
				strList=new ArrayList<String>();
				String line=null;
				while((line=br.readLine())!=null)
				{
					strList.add(line);
				}
			}
		}
		catch(IOException ex){
			ex.printStackTrace();
		}
		finally{
			if(br!=null)
			{
				try {
					br.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return strList;
	}
	/*****
	 * 删除文件
	 * ****/
	public int deleteFile(String dirName,String fileName){
		if(!isFileExist(dirName))
		{
			return -1;
		}
		if(!isFileExist(dirName+fileName))
		{
			return -1;
		}
		else
		{
			File file=new File(SDCardRoot+dirName+fileName);
			file.delete();
		}
		return 0;
	}
	/**************
	 * 重命名文件
	 * *********************/
	public boolean rename(File file,String dirName,String fileName){
		if(file==null||dirName==null||fileName==null){
			return false;
		}
		return file.renameTo(new File(SDCardRoot+dirName+fileName));
	}
	/***************
	 * 创建一个新的文件
	 * ***************/
	public File createNewFile(String dirName,String fileName)
	{
		if(!isFileExist(dirName))
		{
			createDir(dirName);
		}
		File file=new File(SDCardRoot+dirName+fileName);
		try {
			file.createNewFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(SDCardRoot+dirName+fileName);
		return file;
	}
	/****************************************************
	*将一个字符串写入文件
	****************************************************/
	public int writeFile(String data,File file)
	{
		if(data==null||file==null){
			return -1;
		}
		int reCode=0;
		OutputStream output=null;
		BufferedWriter bw=null;
		if(file.canWrite())
		{
			try {
				output=new FileOutputStream(file,true);
				bw=new BufferedWriter(new OutputStreamWriter(output,"GBK"));
				bw.write(data+"\r\n");
				bw.flush();
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				reCode=-1;
			}
			finally{
				if(bw!=null)
				{
					try {
						bw.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						reCode=-1;
					}
				}
			}
		}
		else
		{
			reCode=-1;
		}
		return reCode;
	}
}
