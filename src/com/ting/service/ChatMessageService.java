package com.ting.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.jfinal.plugin.ehcache.CacheKit;
import com.ting.model.ChatMessageBean;
import com.ting.utils.Utils;

public class ChatMessageService {
	/**
	 * ��Ϣ����������
	 * ��������Ϣ��keyΪ senderId-receiverId ����1-2
	 * @param cacheName
	 * @param key
	 * @param msg
	 */
	public static void saveChatMessage(String cacheName, String key, ChatMessageBean msg) {
		Object sender = CacheKit.get(cacheName, key + "-" + msg.getReceiver());
		List<ChatMessageBean> list;
		if (sender != null) {
			list = (List<ChatMessageBean>) sender;
			list.add(msg);
			CacheKit.put(cacheName, key + "-" + msg.getReceiver(), list);
		} else {
			list = new ArrayList<ChatMessageBean>();
			list.add(msg);
			CacheKit.put(cacheName, key + "-" + msg.getReceiver(), list);
		}
	}
	/**
	 *  ��ȡ��������컥������,receiverΪ��ǰ��¼���û�id����Ϊ������Ϣ�Ķ���
	 * @param cacheName
	 * @param receiver
	 * @return
	 */
	public static List<ChatMessageBean> queryRecentChatMessage(String cacheName, long receiver) {
		List<String> cacheKeys = CacheKit.getKeys("message");
		cacheKeys.forEach(System.out::println);
		final List<ChatMessageBean> list = new ArrayList<ChatMessageBean>();
		cacheKeys.stream().filter(x -> x.endsWith("-" + receiver) || x.startsWith(receiver + "-"))
				.collect(Collectors.toList()).forEach(x -> {
					list.addAll((List<ChatMessageBean>) CacheKit.get(cacheName, x));
				});
		return list.stream().filter((x) -> x.getReceiver() == receiver || x.getSender() == receiver)
				.sorted((x, y) -> x.getCreateTime() > y.getCreateTime() ? -1 : 1).distinct()
				.map((x) -> x.setFormatTime(Utils.formatTime(x.getCreateTime()))).collect(Collectors.toList());
	}
	/**
	 * ���ҽ�����Ϣ ͨ��Ϊĳ���û�����б��е�һλ����ʱ����
	 * @param cacheName
	 * @param sender
	 * @param receiver
	 * @return
	 */
	public static List<ChatMessageBean> interactiveChatMesage(String cacheName, String sender, String receiver) {
		Object senderl = CacheKit.get(cacheName, sender + "-" + receiver);
		Object receiverl = CacheKit.get(cacheName, receiver + "-" + sender);
		List<ChatMessageBean> list = new ArrayList<ChatMessageBean>();
		if (senderl != null) {
			list.addAll((ArrayList<ChatMessageBean>) senderl);
			// �޸Ļ���������״̬
			CacheKit.put(cacheName, sender + "-" + receiver,
					((ArrayList<ChatMessageBean>) senderl).stream().map(x -> x.setState(1)).collect(Collectors.toList()));
		}
		if (receiverl != null) {
			list.addAll((ArrayList<ChatMessageBean>) receiverl);
			CacheKit.put(cacheName, receiver + "-" + sender,
					((ArrayList<ChatMessageBean>) receiverl).stream().map(x -> x.setState(1)).collect(Collectors.toList()));
		}
		list = list.stream().map((x) -> x.setFormatTime(Utils.formatTime(x.getCreateTime()))).collect(Collectors.toList());
		return list;
	}
}
