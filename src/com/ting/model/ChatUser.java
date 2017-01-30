package com.ting.model;

import java.util.List;
import java.util.stream.Collectors;


import com.jfinal.plugin.activerecord.Model;
import com.ting.utils.Utils;

public class ChatUser extends Model<ChatUser> {
	public final static ChatUser dao = new ChatUser();

	public ChatUser existUser(String name, String password) {// �ж����ݿ����Ƿ�����û���
	ChatUser cu = dao.findFirst("select * from chat_user where name=? and password=?", name, password);
	if(null!=cu)
	cu = cu.set("url", Utils.getGravatar(cu.get("name")));
	return cu;
	}
	public boolean isExistEmail(String email){
	return null!=dao.findFirst("select * from chat_user where name=?", email);
	}
	public ChatUser findUserByEmail(String email){
	ChatUser cu = dao.findFirst("select * from chat_user where name=?", email);
	if(null!=cu)
	cu = cu.set("url", Utils.getGravatar(cu.get("name")));
	return cu;
	}
	public List<ChatUser> queryFriends(String friends) {
	String sql = "select *from chat_user where id in(" + friends + ")";
	List<ChatUser> list = dao.find(sql);
	//����ͷ��
	if (null != list)
	list = list.parallelStream().map((x) -> x.set("url", Utils.getGravatar(x.get("name"))))
	.collect(Collectors.toList());
	return list;
	}
	//ͨ��������id��ѯ�û��б�����ʽ���� in��id��˳��
	public List<ChatUser> queryUserByIds(String ids){
	List<ChatUser> list = dao.find("select *from chat_user where id in("+ids+") order by instr('"+ids+"',id)");
	if (null != list)
	list = list.parallelStream().map((x) -> x.set("url", Utils.getGravatar(x.get("name"))))
	.collect(Collectors.toList());
	return list;
	}
	//���˵�ǰ��¼�û����������û�
	public List<ChatUser> queryAllUsers(long id) {
	String sql = "select *from chat_user where id!="+id;
	List<ChatUser> list = dao.find(sql);
	//����ͷ��
	if (null != list)
	list = list.parallelStream().map((x) -> x.set("url", Utils.getGravatar(x.get("name"))))
	.collect(Collectors.toList());
	return list;
	}
	}
