package cn.mangues.db;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder.In;



/**
 * 类名称：  DBUtil   
 * 描述：  数据处理类
 * @author 许涛
 * 创建时间：  2015-7-29 下午3:14:04
 */
public class DBUtil {
	 private static Connection conn = DBConnection.conn;
    /*
     * 查数据
     */
    public static List getObjectList(String mql){
    	
    	PreparedStatement pst = null;
    	ResultSet rs = null;
    	List<Object> list = new ArrayList<Object>();
    	//把自定义的mql转化为sql
    	String sql[] = DBTypeUtil.mql2sql(mql);
    	List var = new ArrayList<String>();
    	try {
			Class classType = Class.forName(sql[0]);
			//获得构造方法参数类型列表
			HashMap<Integer, Class[]> map = DBTypeUtil.classType2ParamTypes(classType);
			Class[] paramTypes = map.get(sql[1].split(",").length); //获取构造方法参数类型
			ArrayList<Object> array = new ArrayList<Object>();// 方法传入的参数
			pst = conn.prepareStatement(sql[2]);
			rs = pst.executeQuery();
		    while (rs.next()) {
		    	array.clear();
		    	Constructor con = classType.getConstructor(paramTypes);     //主要就是这句了
		    	for(int i =1;i<=paramTypes.length;i++){
		    		Class class1 = paramTypes[i-1];
		    		if(class1.equals(String.class)){
		    			array.add(rs.getString(i));
		    		}else if(class1.equals(Integer.class)){
		    			array.add(rs.getInt(i));
		    		}else if(class1.equals(Date.class)){
		    			array.add(rs.getDate(i));
		    		}
		    	}
		    	Object base = (Object) con.newInstance(array.toArray());  //BatcherBase 为自定义
				list.add(base);
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}finally{
			try {
				rs.close();
				pst.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return list;
    }
    
    /**查看分页数量
     * @Title: getCount 
     * @param sql
     * @param pageSize  每页数量
     * @return 
     * @return Long    返回类型  总页数
     * @throws
     */
    public static Integer getCount(String sql,int pageSize){
    	int totalPage =  0;
    	PreparedStatement pst = null;
    	ResultSet rs = null;
    	try {
			pst = conn.prepareStatement(sql);
			rs = pst.executeQuery();
			Long count = (long) 0;
		    while (rs.next()) {
		    	count = rs.getLong(1);
		    }
		    if (count % pageSize == 0)
				totalPage = (int) (count / pageSize);
			else
				totalPage = (int) (count / pageSize + 1);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			try {
				rs.close();
				pst.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return totalPage;
    }
    

    /**
     * 改数据,可以利用反射写
     * 
     */
    public static boolean update(Object object){
    	 Class c = object.getClass();  
         String className = c.getName();    //获得对象名
         String table = className.substring(className.lastIndexOf(".")+1,className.length());
    	
    	List var = new ArrayList();   //存储字段名
    	List values = new ArrayList();  //存储value值
    	 // 定义一个sql字符串  
        String sql = "update %s set %s where %s = %s";  
        // 得到对象的类  
        getSqlString(var, values, object);
        //设置
        String sql2="";
        for (int i = 0; i < var.size(); i++) { 
        	if(i<var.size()-1){
        		sql2 += var.get(i) + "=" + values.get(i)+",";   
        	}else{
        		sql2+=var.get(i) + "=" + values.get(i)+"";
        	}
        } 
        sql=sql.format(sql, table,sql2,var.get(0),values.get(0));
        return dbCO(sql);
       
	}
    
    /**
     * 插入数据,可以利用反射写
     * 
     */
    public static boolean insert(Object object){
    	 Class c = object.getClass();  
         String className = c.getName();    //获得对象名
         String table = className.substring(className.lastIndexOf(".")+1,className.length());
    	
    	List var = new ArrayList();   //存储字段名
    	List values = new ArrayList();  //存储value值
    	 // 定义一个sql字符串  
        String sql = "insert into %s(";  
        // 得到对象的类  
        getSqlString(var, values, object);
        //设置
        for (int i = 0; i < var.size(); i++) {  
            if (i < var.size() - 1) {  
                sql += var.get(i) + ",";  
            } else {  
                sql += var.get(i) + ") values(";  
            }  
        }  
        for (int i = 0; i < values.size(); i++) {  
            if (i < values.size() - 1) {  
                sql += values.get(i) + ",";  
            } else {  
                sql += values.get(i) + ")";  
            }  
        }  
        sql=sql.format(sql, table);
       return  dbCO(sql);
       
	}
    
    
    /**
     * 删除数据,可以利用反射写
     * 
     */
    public static boolean delete(Object object){
    	 Class c = object.getClass();  
         String className = c.getName();    //获得对象名
         String table = className.substring(className.lastIndexOf(".")+1,className.length());
    	
    	List var = new ArrayList();   //存储字段名
    	List values = new ArrayList();  //存储value值
    	 // 定义一个sql字符串  
        String sql = "delete %s where %s";  
        // 得到对象的类  
        getSqlString(var, values, object);
        //设置
        String sql2="";
        for (int i = 0; i < var.size(); i++) { 
        	if(i<var.size()-1){
        		sql2 += var.get(i) + "=" + values.get(i)+" and ";   
        	}else{
        		sql2+=var.get(i) + "=" + values.get(i)+"";
        	}
        } 
        sql=sql.format(sql, table,sql2);
        return dbCO(sql);
       
	}
    

    private static void getSqlString(List var,List values,Object object){
   	    Class c = object.getClass();  
        Method method[] = c.getMethods();  //获取所有方法
        String className = c.getName();    //获得对象名
        String table = className.substring(className.lastIndexOf(".")+1,className.length());
        for(Method m : method){ //遍历方法
        	  String mName = m.getName();   //方法名
              if (mName.startsWith("get") && !mName.startsWith("getClass")) {  
            	  try {
            		  //获取字段名
            		String fieldName = mName.substring(3, mName.length()).toLowerCase();  
                    var.add(fieldName);  
					Object value = m.invoke(object, null);//方法调用
                    if (value instanceof String) {  
                        values.add("'" + value + "'");  
                    } else {  
                    	values.add(value);  
                    }  
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
            	  
              }
        }
   }
   
   
   private static boolean dbCO(String sql){
   	 PreparedStatement pst = null;
		try {
			conn.setAutoCommit(false);
			pst = conn.prepareStatement(sql);
			pst.executeUpdate();
			conn.commit();
			conn.setAutoCommit(true);
		    return true;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}finally{
			try {
				pst.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
   }
}
