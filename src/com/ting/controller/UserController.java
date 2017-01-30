package com.ting.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jfinal.aop.Clear;
import com.jfinal.core.ActionKey;
import com.jfinal.core.Controller;
import com.jfinal.kit.StrKit;
import com.ting.model.ChatUser;
import com.ting.service.ChatUserService;

public class UserController extends Controller {
	@Clear
	public void register() {
		Map<String, Object> result = new HashMap<String, Object>();
		String email = getPara("email");
		String password = getPara("password"), repetition = getPara("repetition");
		String nickName = getPara("nickName");
		if (StrKit.isBlank(email) || !email.matches("^([a-zA-Z0-9_-])+@([a-zA-Z0-9_-])+(.[a-zA-Z0-9_-])+")) {
			result.put("result", false);
			result.put("msg", "��������");
			renderJson(result);
			return;
		}
		if (!StrKit.notBlank(password, repetition) || !password.equals(repetition)) {
			result.put("result", false);
			result.put("msg", "��������(Ϊ�ջ��߲�һ��)");
			renderJson(result);
			return;
		}
		if (StrKit.isBlank(nickName)) {
			result.put("result", false);
			result.put("msg", "�ǳƲ���Ϊ��");
			renderJson(result);
			return;
		}
		if (ChatUser.dao.isExistEmail(email)) {
			result.put("result", false);
			result.put("msg", "�����Ѿ�����");
			renderJson(result);
			return;
		}
		ChatUser cu = new ChatUser();
		cu.set("name", email).set("nickName", nickName).set("password", password).set("createTime",
				new Date().getTime());
		try {
			if (!cu.save()) {
				result.put("result", false);
				result.put("msg", "ע��ʧ��");
				renderJson(result);
				return;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		result.put("result", true);
		getSession().setAttribute("u", ChatUser.dao.findUserByEmail(email));
		renderJson(result);
	}
	
	@Clear
	public void login() {
		String username = getPara("username");
		String password = getPara("password");
		Map<String, Object> result = new HashMap<String, Object>();
		// ��֤�û�������
		if (StrKit.isBlank(username) || StrKit.isBlank(password)) {
			result.put("result", false);
			result.put("msg", "�˺ź����벻��Ϊ��");
			renderJson(result);
			return;
		}

		ChatUser chatUser = ChatUser.dao.existUser(username, password);
		if (chatUser == null) {
			result.put("result", false);
			result.put("msg", "�˺Ż����������");
			renderJson(result);
			return;
		}
		if (ChatUserService.identity(chatUser.getLong("Id"))) {
			result.put("result", false);
			result.put("msg", "�û��Ѿ���¼");
			renderJson(result);
			return;
		}
		result.put("result", true);
		getSession().setAttribute("u", chatUser);
		renderJson(result);
	}
	/**
	 * ��ȡ�����û�������������ҳ�������û��б� �첽����
	 */
	public void users() {
		ChatUser cu = (ChatUser) getSession().getAttribute("u");
		List<ChatUser> uList = ChatUser.dao.queryAllUsers(cu.getLong("Id"));
		renderJson(uList);
	}

	/**
	 * ��ȡ��ǰ��½�û��ĺ��ѣ��첽����
	 */
	public void friends() {
		Object u = getSession().getAttribute("u");
		List<ChatUser> fList = ChatUser.dao.queryFriends(((ChatUser) u).getStr("friends"));
		renderJson(fList);
	}
	/**
	 * ��ȡ����ǰ�û�����Ϣ�����������û��б�
	 */
	public void recentCaches() {
		ChatUser cu = (ChatUser) getSession().getAttribute("u");
		List<ChatUser> list = ChatUserService.recentInteractiveUser(cu.getLong("Id").longValue());
		renderJson(list);
	}
	/**
	 * �˳���ǰϵͳ
	 */
	public void logout() {
		getSession().removeAttribute("u");
		getSession().invalidate();
		renderJsp("login.jsp");
	}
}