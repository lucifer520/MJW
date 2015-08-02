package cn.mangues.base;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
/**
 * 
 * 项目名称：  MJW   
 * 类名称：  BaseServlet   
 * 描述：  自定义servlet基类，实现参数的映射
 * @author 许涛
 * 创建时间：  2015-7-29 下午3:25:52
 */
public abstract class BaseServlet extends HttpServlet {
	private List<String> var = null; // 存储字段名
	private List<String> vars = null; // 存储字段名
	private List<Method> ms = null; // 方法

	@Override
	public void init() throws ServletException {
		super.init();
		getVar();
	}

	// 是否自定义变量
	private boolean isParameter(String mName) {
		String con[] = { "getServletContext", "getServletConfig",
				"getServletName", "getInitParameter", "getInitParameterNames",
				"getServletInfo" };
		if (mName.startsWith("set") && !mName.startsWith("getClass")) {
			for (String nameString : con) {
				if (mName.equals(nameString)) {
					return false;
				}
			}
			return true;
		} else {
			return false;
		}

	}
   //初始化
	private void getVar() {
		var = new ArrayList<String>();
		vars = new ArrayList<String>();
		ms = new ArrayList<Method>();
		Class classType = this.getClass();
		Method method[] = classType.getMethods();
		for (Method m : method) { // 遍历方法
			String mName = m.getName(); // 方法名
			if (isParameter(mName)) {
				try {
					// 获取字段名
					String fieldName = mName.substring(3, mName.length())
							.toLowerCase();
					vars.add(fieldName);
					String ds = m.toString();
					int i = ds.indexOf("(");
					int j = ds.indexOf(")");
					String str = ds.substring(i + 1, j);
					var.add(str);
					ms.add(m);
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		}
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doPost(request, response);
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("utf-8"); 
		response.setCharacterEncoding("utf-8");
		response.setContentType("text/html; charset=utf-8");
		Map<String, String[]> map = request.getParameterMap(); // 这个方法是获取用户请求时携带的参数名称和参数值，并将其组装成一个Map对象
		for (int i = 0; i < vars.size(); i++) {
			String strs = vars.get(i); // 遍历出变量名
			String str = var.get(i); // 遍历出bean位置
			Method mehMethod = ms.get(i); // 方法
			String parameterKey;
			Class classType;
			try {
				classType = Class.forName(str);
				Object o = classType.newInstance();
				Method[] methods = classType.getMethods(); // 获取方法
				for (Method m : methods) { // 遍历方法
					String mName = m.getName(); // 方法名
					if (mName.startsWith("set")) {
						// 获取字段名
						String fieldName = mName.substring(3, mName.length())
								.toLowerCase();
						parameterKey = strs + "." + fieldName;
						// 赋值
						if (map.get(parameterKey) != null) {
							String key[] = map.get(parameterKey);
							String ds = m.toString();
							int i1 = ds.indexOf("(");
							int j1 = ds.indexOf(")");
							String str1 = ds.substring(i1 + 1, j1);
							if (str1.equals("java.lang.Integer")
									|| str1.equals("int")) {
								// 如果是int类型
								int ip = Integer.parseInt(key[0]);
								m.invoke(o, ip);
							} else if(str1.equals("java.util.Date")){
								 SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
								 Date ip = sdf.parse(key[0]);
								 m.invoke(o, ip);
							}else {
								m.invoke(o, key[0]);
							}
						}
					}
				}
				if (o != null) {
					mehMethod.invoke(this, o);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		//打印值
		Object object = manguesPost(request, response);
		PrintWriter pw  = null;
		  try {
		        pw = response.getWriter();
		        if(object instanceof JSONObject){
					pw.write(JSON.toJSONString(object));     
				}else if(object instanceof Integer){
					pw.write(((Integer) object).intValue());
				}else if(object instanceof String){
					pw.write((String)object);
				}else{
					pw.write(JSON.toJSONString(object));     
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
	}
	/**
	 * 
	 * @Title: manguesPost 
	 * @param request
	 * @param response 
	 * @return void    返回类型 
	 * @throws
	 */
	protected abstract Object manguesPost(HttpServletRequest request,
			HttpServletResponse response);
}
