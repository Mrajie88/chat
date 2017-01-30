package com.ting.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.ting.controller.ChatWebSocket;
import com.ting.model.ChatMessageBean;
import com.ting.model.ChatUser;

public class ChatUserService {
	/**
	 * �û���½��֤ ��ֹ���˺�ͬʱ��½
	 * 
	 * @param uid
	 * @return
	 */
	public static boolean identity(long uid) {
		List<ChatWebSocket> l = ChatWebSocket.chatSocketSet.stream().filter((x) -> {
			return x.getUserId() == uid;
		}).collect(Collectors.toList());
		return null != l ? l.size() > 0 : false;
	}

	/**
	 * ��ȡ�뵱ǰ��½�û�����Ľ����û�
	 * 
	 * @param receiver
	 * @return
	 */
	public static List<ChatUser> recentInteractiveUser(long receiver) {
		List<ChatMessageBean> iMessage = ChatMessageService.queryRecentChatMessage("message", receiver);
		if (null == iMessage || iMessage.size() == 0)
			return new ArrayList<ChatUser>();
		List<Long> lu = new ArrayList<Long>();
		// iMessage.stream().map(x ->{return
		// x.getSender()==receiver?receiver:x.getSender();}).collect(Collectors.toList());
		// ƴ�Ӳ�����ʽ id1,id2,id3
		return ChatUser.dao.queryUserByIds(iMessage.stream().map(x -> {
			return x.getSender() == receiver ? (x.getReceiver() + "") : (x.getSender() + "");
		}).reduce(((x, y) -> {
			return (x + "," + y);
		})).get());
	}
}
