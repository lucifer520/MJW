package cn.mangues.db;
import java.sql.Connection;
import java.sql.DriverManager;

import cn.mangues.util.SysConfig;
/**
 * 
 * 项目名称：  MJW   
 * 类名称：  DBConnection   
 * 描述：  数据库连接
 * @author 许涛
 * 创建时间：  2015-8-1 下午8:34:44
 */
public class DBConnection {
	 // 驱动程序名
    private static String driver = SysConfig.getInstance().getProperty("driver");
    // URL指向要访问的数据库名scutcs
    private static String url = SysConfig.getInstance().getProperty("url");
    // 配置时的用户名
    private static String user = SysConfig.getInstance().getProperty("user"); 
    // 配置时的密码
    private static String password = SysConfig.getInstance().getProperty("password");
    public static Connection conn = null ;
    static{
            try { 
             // 加载驱动程序
             Class.forName(driver);
             // 连续数据库
              conn = DriverManager.getConnection(url, user, password);
             }catch (Exception e) {
				System.err.println("出错");
			}
    }
}
