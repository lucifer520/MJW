package cn.mangues.db;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;


public class DBTypeUtil {
	/**
	 * 利用Class 提取出其中参数的类型
	 * @Title: classType2ParamTypes 
	 * @param classType
	 * @return 
	 * @return String[]    返回类型 
	 * @throws
	 */
	public static HashMap<Integer,Class[]> classType2ParamTypes(Class classType) {
		HashMap<Integer, Class[]> map = new HashMap<Integer,Class[]>();
		//---- 获取所有构造方法
		Constructor[] cons=classType.getDeclaredConstructors();
		for (int i=0;i<cons.length;i++)
		{
		     Constructor con=cons[i]; //取出第i个构造方法。
//		     System.out.print(Modifier.toString(con.getModifiers()));
		//---- 打印该构造方法的前缀修饰符
//		System.out.print(" "+con.getName()+"("); //打印该构造方法的名字
		//---- 打印该构造方法的参数。
		    Class[] parameterTypes=con.getParameterTypes(); //构造方法参数集但是 数组类型显示特殊
		    map.put(parameterTypes.length, parameterTypes);
		}
		return map;
	}
	/**
	 * 
	 * @Title: mql2sql 
	 * @param mql
	 * @return 
	 * @return String[]    返回类型 
	 * @throws
	 */
	public static String[] mql2sql(String mql){
		//String mql = "select mangues.model.Stations(station_name,telphone) from Stations";
    	//获取去javabean地址 mangues.model.Stations
    	String sql = mql.substring(mql.indexOf("select")+6,mql.indexOf("(")).trim();
    	//获取请求参数 station_name,telphone
    	String sql2 = mql.substring(mql.indexOf("(")+1,mql.indexOf(")")).trim();
    	//station_name,telphone
    	String sql3 = mql.substring(mql.indexOf(")")+1,mql.length()).trim();
    	//构造获取mql
    	String sql4 = "select "+sql2 +" "+sql3;
    	String[] strings = {sql,sql2,sql4}; 
    	return strings;
	}
	
	
	public static void main(String[] args) throws ClassNotFoundException {
		Class class1 = Class.forName("mangues.model.User");
		classType2ParamTypes(class1);
	}
}
