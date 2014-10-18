package com.lj.jz.data;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

	private static final int VERSION = 1;

	public DatabaseHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
	}

	public DatabaseHelper(Context context, String name, int version) {
		this(context, name, null, version);
	}

	public DatabaseHelper(Context context, String name) {
		this(context, name, VERSION);
	}

	public DatabaseHelper(Context context) {
		this(context, "ljjz.db", VERSION);
	}

	// 实体类数据类型和数据库数据类型进行转换
	private String getDbType(String type) {
		String fieldType = type;
		if ("int".equals(fieldType)) {
			fieldType = "INTEGER";
		} else if ("long".equals(fieldType)) {
			fieldType = "LONG";
		} else if ("float".equals(fieldType)) {
			fieldType = "REAL";
		} else if ("double".equals(fieldType)) {
			fieldType = "REAL";
		} else if ("String".equals(fieldType)) {
			fieldType = "TEXT";
		}
		return fieldType;
	}

	// 根据实体类创建数据表
	private void createTable(SQLiteDatabase db, Class entryClass) {
		Field[] fields = entryClass.getDeclaredFields();
		String className = entryClass.getSimpleName().toLowerCase();
		StringBuffer sb = new StringBuffer("create table " + className + "(");
		if (fields.length > 0) {
			String fieldName = fields[0].getName().toLowerCase();
			String fieldType = getDbType(fields[0].getType().getSimpleName());
			sb.append(fieldName + " " + fieldType);
		}
		for (int i = 1; i < fields.length; i++) {
			Field field = fields[i];
			String fieldName = field.getName().toLowerCase();
			String fieldType = getDbType(field.getType().getSimpleName());
			sb.append("," + fieldName + " " + fieldType);
		}
		sb.append(")");
		System.out.println(sb.toString());
		db.execSQL(sb.toString());
	}

	// 删除表
	private void dropTable(SQLiteDatabase db, Class entryClass) {
		String className = entryClass.getSimpleName().toLowerCase();
		StringBuffer sb = new StringBuffer("drop table " + className + "");
		System.out.println(sb.toString());
		db.execSQL(sb.toString());
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		createTable(db, ZhangDan.class);
		createTable(db, CanShu.class);
		createTable(db, YongHu.class);
		createTable(db, SystemParam.class);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		System.out.println("update table test");
		// 删除表
		dropTable(db, ZhangDan.class);
		dropTable(db, CanShu.class);
		dropTable(db, YongHu.class);
		dropTable(db, SystemParam.class);
		// 创建数据表
		createTable(db, ZhangDan.class);
		createTable(db, CanShu.class);
		createTable(db, YongHu.class);
		createTable(db, SystemParam.class);
	}
	
	//根据数据表列名查出对应的实例属性
	private Field getFieldByColumnName(Class entryClass, String columnName){
		Field fields[]=entryClass.getDeclaredFields();
		Field field=null;
		for(Field fieldTemp:fields){
			if(columnName.equals(fieldTemp.getName().toLowerCase())){
				field=fieldTemp;
				break;
			}
		}
		return field;
	}
	// 查询数据,返回满足条件的所有记录条数
	public int getEntryList(List entryList,Class entryClass, String selection,
			String[] selectionArgs,String orderBy,int offset,int max) {
		SQLiteDatabase db = getReadableDatabase();
		String tableName = entryClass.getSimpleName().toLowerCase();
		Cursor c = db.query(tableName, null, selection, selectionArgs, null,
				null, orderBy);
		int totalCount=c.getCount();
		if(entryList!=null){
			if(c.moveToFirst()){
				int endOffset=c.getCount();
				if(max>0){
					endOffset=(c.getCount()<(offset+max))?c.getCount():(offset+max);
				}
				for(int i=offset;i<endOffset;i++){
					if(c.moveToPosition(i)){
						try {
							Object entry=entryClass.newInstance();				
							int columnCount=c.getColumnCount();
							for(int j=0;j<columnCount;j++){
								Field field=getFieldByColumnName(entryClass,c.getColumnName(j));
								if(field!=null){
									String fieldType = field.getType().getSimpleName();
									if ("int".equals(fieldType)) {
										field.setInt(entry, c.getInt(j));
									} else if ("long".equals(fieldType)) {
										field.setLong(entry, c.getLong(j));
									} else if ("float".equals(fieldType)) {
										field.setFloat(entry, c.getFloat(j));
									} else if ("double".equals(fieldType)) {
										field.setDouble(entry, c.getDouble(j));
									} else {
										field.set(entry, c.getString(j));
									}
								}
							}					
							entryList.add(entry);				
						} catch (InstantiationException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IllegalAccessException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}
		}
		c.close();
		db.close();		
		return totalCount;
	}

	// 添加数据
	/********************************
	 * 将一条数据写入表中,成功插入一条返回rowId，否则返回-1
	 * 
	 * @param entry,实例对象
	 * *****************************/
	public long insert(Object entry) {
		Class entryClass = entry.getClass();
		String tableName = entryClass.getSimpleName().toLowerCase();

		long num = 0;
		// 获取最大编号
		SQLiteDatabase dbr = getReadableDatabase();
		Cursor c = dbr.rawQuery("select max(num) from " + tableName, null);
		if (c.moveToFirst()) {
			num = c.getLong(0);
		}
		//System.out.println(num);
		c.close();
		num += 1;
		dbr.close();

		// 插入到数据库
		Field[] fields = entryClass.getDeclaredFields();
		ContentValues values = new ContentValues();
		for (Field field : fields) {
			try {
				String fieldName = field.getName().toLowerCase();						
				if("num".equals(fieldName)){
					values.put(fieldName, num);
					field.setLong(entry, num);
					continue;
				}
				String fieldType = field.getType().getSimpleName();
				if ("int".equals(fieldType)) {
					values.put(fieldName, field.getInt(entry));
				} else if ("long".equals(fieldType)) {
					values.put(fieldName, field.getLong(entry));
				} else if ("float".equals(fieldType)) {
					values.put(fieldName, field.getFloat(entry));
				} else if ("double".equals(fieldType)) {
					values.put(fieldName, field.getDouble(entry));
				} else {
					if(field.get(entry)!=null)
						values.put(fieldName, field.get(entry).toString());
				}
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		SQLiteDatabase dbw=getWritableDatabase();
		long rowId=dbw.insert(tableName, null, values);
		dbw.close();
		return rowId;
	}
	//更新一个实例
	/********************************
	 * 将一条数据更新入表中,成功返回影响的行数，否则返回0
	 * 
	 * @param entry,实例对象
	 * *****************************/
	public int update(Object entry) {
		Class entryClass = entry.getClass();
		String tableName = entryClass.getSimpleName().toLowerCase();
		long num = 0;
		Field[] fields = entryClass.getDeclaredFields();
		ContentValues values = new ContentValues();
		for (Field field : fields) {
			try {
				String fieldName = field.getName().toLowerCase();						
				if("num".equals(fieldName)){
					num=field.getLong(entry);
					continue;
				}
				String fieldType = field.getType().getSimpleName();
				//System.out.println("fieldType-->"+fieldType);
				if ("int".equals(fieldType)) {
					values.put(fieldName, field.getInt(entry));
				} else if ("long".equals(fieldType)) {
					values.put(fieldName, field.getLong(entry));
				} else if ("float".equals(fieldType)) {
					values.put(fieldName, field.getFloat(entry));
				} else if ("double".equals(fieldType)) {
					values.put(fieldName, field.getDouble(entry));
				} else {
					if(field.get(entry)!=null){
						values.put(fieldName, field.get(entry).toString());
					}else{
						values.put(fieldName, (String) null);
					}
				}
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		SQLiteDatabase dbw=getWritableDatabase();
		int affectedNumber=dbw.update(tableName, values, "num=?", new String[]{String.valueOf(num)});
		dbw.close();
		return affectedNumber;
	}
	
	/********************************
	 * 更新表中数据,成功返回影响的行数，否则返回0
	 * 
	 * @param entryClass,实例类
	 * @param updateFields,要更新的字段
	 * @param whereClause,条件
	 * @param whereArgs,条件参数
	 * *****************************/
	public int update(Class entryClass,HashMap<String, Object> updateFields,String whereClause, String[] whereArgs){
		String tableName = entryClass.getSimpleName().toLowerCase();
		Field[] fields = entryClass.getDeclaredFields();
		ContentValues values = new ContentValues();
		for (Field field : fields) {
			try {
				String fieldNameOriginal=field.getName();
				String fieldName = fieldNameOriginal.toLowerCase();						
				if("num".equals(fieldName)){
					continue;
				}
				Object value=updateFields.get(fieldNameOriginal);
				if(value==null){
					continue;
				}
				String fieldType = field.getType().getSimpleName();
				System.out.println("fieldType-->"+fieldType);
				if ("int".equals(fieldType)) {
					values.put(fieldName, (Integer)value);
				} else if ("long".equals(fieldType)) {
					values.put(fieldName, (Long)value);
				} else if ("float".equals(fieldType)) {
					values.put(fieldName, (Float)value);
				} else if ("double".equals(fieldType)) {
					values.put(fieldName, (Double)value);
				} else {
					values.put(fieldName, (String)value);
				}
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		SQLiteDatabase dbw=getWritableDatabase();
		int affectedNumber=dbw.update(tableName, values, whereClause, whereArgs);
		dbw.close();
		return affectedNumber;
	}

	//删除数据
	public int delete(Class entryClass,String whereClause,String[] whereArgs){
		String tableName = entryClass.getSimpleName().toLowerCase();
		SQLiteDatabase dbw=getWritableDatabase();
		int affectedNumber=dbw.delete(tableName, whereClause, whereArgs);
		dbw.close();
		return affectedNumber;
	}
}
