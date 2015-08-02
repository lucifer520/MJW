package servlet;


import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import com.alibaba.fastjson.JSONObject;
import cn.mangues.base.BaseServlet;
import cn.mangues.db.DBUtil;
@WebServlet(urlPatterns = { "/register"})
public class dbSevlet extends BaseServlet {
	private User user;
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	@Override
	protected Object manguesPost(HttpServletRequest arg0,
			HttpServletResponse arg1) {
		Long loInteger = System.currentTimeMillis();
		String lo = loInteger.toString();
		Integer integer = Integer.parseInt(lo.substring(7,13));
		user.setUserid(integer);
		boolean bo = DBUtil.insert(user);
		JSONObject json = new JSONObject();
		if (bo) {    //成功
			json.put("msg", "0000");
		}else{
			json.put("msg", "1111");
		}
		return json;
	}

}
