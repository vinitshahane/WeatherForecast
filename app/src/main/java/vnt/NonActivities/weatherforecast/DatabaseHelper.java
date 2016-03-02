package vnt.NonActivities.weatherforecast;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
 
public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

	private Context _context;
	private static final String DATABASE_NAME = "Citydb.db";
	private static final int DATABASE_VERSION = 1;
	private Dao<City, String> simpleDao = null;
	private RuntimeExceptionDao<City, String> simpleRuntimeDao = null;
	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		_context = context;
	}
	public Dao<City, String> getDao() throws SQLException {
		if (simpleDao == null) {
			simpleDao = getDao(City.class);
		}
		return simpleDao;
	}
	public RuntimeExceptionDao<City, String> getSimpleDataDao() {
		if (simpleRuntimeDao == null) {
			simpleRuntimeDao = getRuntimeExceptionDao(City.class);
		}   
		return simpleRuntimeDao;
	}
	//method for list of Reference
	public List<City> GetData()
	{
		DatabaseHelper helper = new DatabaseHelper(_context);
		RuntimeExceptionDao<City, String> simpleDao = helper.getSimpleDataDao();
		
		List<City> list = simpleDao.queryForAll();
		List<City> listf=new ArrayList<City>( new LinkedHashSet<City>(list));
		/*hs.addAll(list);
		list.clear();
		list.addAll(hs);*/
		
		return listf;
	}
	//method for insert data
	public int addData(City cty)
	{
		RuntimeExceptionDao<City, String> dao = getSimpleDataDao();
		int i=0;
		try
		{
		 i = dao.create(cty);
		 return i;
		}
		catch(Exception e)
		{
			
			deleteData(cty);
			i=dao.create(cty);
			
		}
		return i;
	}
	//method to delete single data
	public void deleteData(City cty)
	{
		RuntimeExceptionDao<City, String> dao = getSimpleDataDao();
DeleteBuilder<City, String> deleteBuider=dao.deleteBuilder();
try {
	deleteBuider.where().eq("cityName", cty.getCityName());
	deleteBuider.delete();
} catch (SQLException e) {
	// TODO Auto-generated catch block
	e.printStackTrace();
}
		
		
	}
	//method for delete all rows
	public void deleteAll()
	{
		RuntimeExceptionDao<City, String> dao = getSimpleDataDao();
		List<City> list = dao.queryForAll();
		DeleteBuilder<City, String> deleteBuider=dao.deleteBuilder();
		for(int i=0;i<list.size();i++)
		{
		try {
			deleteBuider.where().eq("cityName", list.get(i).getCityName());
			deleteBuider.delete();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		}
	}
	@Override
	public void close() {
		super.close();
		simpleRuntimeDao = null;
	}
	@Override
	public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {
		try {
			Log.i(DatabaseHelper.class.getName(), "onCreate");
			TableUtils.createTable(connectionSource, City.class);
		} catch (SQLException e) {
			Log.e(DatabaseHelper.class.getName(), "Can't create database", e);
			throw new RuntimeException(e);
		}
	}
	@Override
	public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource, int oldVersion, int newVersion) {
		try {
			Log.i(DatabaseHelper.class.getName(), "onUpgrade");
			TableUtils.dropTable(connectionSource, City.class, true);
			onCreate(db, connectionSource);
		} catch (SQLException e) {
			Log.e(DatabaseHelper.class.getName(), "Can't drop databases", e);
			throw new RuntimeException(e);
		}
	}
}