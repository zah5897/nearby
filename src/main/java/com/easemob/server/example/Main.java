package com.easemob.server.example;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.easemob.server.example.api.IMUserAPI;
import com.easemob.server.example.api.SendMessageAPI;
import com.easemob.server.example.comm.ClientContext;
import com.easemob.server.example.comm.EasemobRestAPIFactory;
import com.easemob.server.example.comm.body.CmdMessageBody;
import com.easemob.server.example.comm.body.IMUserBody;
import com.easemob.server.example.comm.body.TextMessageBody;
import com.easemob.server.example.comm.wrapper.BodyWrapper;
import com.easemob.server.example.comm.wrapper.ResponseWrapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

@SuppressWarnings("unused")
public class Main {
	private static EasemobRestAPIFactory factory;
	public static final String SYS="admin";

	
	private static void initFactory() {
		if (factory == null) {
			factory = ClientContext.getInstance().init(ClientContext.INIT_FROM_PROPERTIES).getAPIFactory();
		}
	}

	public static void main(String[] args) throws Exception {
		// initFactory();
		// IMUserAPI user = (IMUserAPI)
		// factory.newInstance(EasemobRestAPIFactory.USER_CLASS);
		// ChatMessageAPI chat =
		// (ChatMessageAPI)factory.newInstance(EasemobRestAPIFactory.MESSAGE_CLASS);
		// FileAPI file =
		// (FileAPI)factory.newInstance(EasemobRestAPIFactory.FILE_CLASS);
		// SendMessageAPI message =
		// (SendMessageAPI)factory.newInstance(EasemobRestAPIFactory.SEND_MESSAGE_CLASS);
		// ChatGroupAPI chatgroup =
		// (ChatGroupAPI)factory.newInstance(EasemobRestAPIFactory.CHATGROUP_CLASS);
		// ChatRoomAPI chatroom =
		// (ChatRoomAPI)factory.newInstance(EasemobRestAPIFactory.CHATROOM_CLASS);
		//
		// ResponseWrapper fileResponse = (ResponseWrapper) file.uploadFile(new
		// File("d:/logo.png"));
		// String uuid = ((ObjectNode)
		// fileResponse.getResponseBody()).get("entities").get(0).get("uuid").asText();
		// String shareSecret = ((ObjectNode)
		// fileResponse.getResponseBody()).get("entities").get(0).get("share-secret").asText();
		// InputStream in = (InputStream) ((ResponseWrapper)
		// file.downloadFile(uuid, shareSecret, false)).getResponseBody();
		// FileOutputStream fos = new FileOutputStream("d:/logo1.png");
		// byte[] buffer = new byte[1024];
		// int len1 = 0;
		// while ((len1 = in.read(buffer)) != -1) {
		// fos.write(buffer, 0, len1);
		// }
		// fos.close();
		// BodyWrapper userBody = new IMUserBody("User101", "123456",
		// "HelloWorld");
		// user.createNewIMUserSingle(userBody);
		/*
		 * // Create a IM user // BodyWrapper userBody = new
		 * IMUserBody("User101", "123456", "HelloWorld"); //
		 * user.createNewIMUserSingle(userBody);
		 * 
		 * // Create some IM users List<IMUserBody> users = new
		 * ArrayList<IMUserBody>(); users.add(new IMUserBody("User002",
		 * "123456", null)); users.add(new IMUserBody("User003", "123456",
		 * null)); BodyWrapper usersBody = new IMUsersBody(users);
		 * user.createNewIMUserBatch(usersBody);
		 * 
		 * // Get a IM user user.getIMUsersByUserName("User001");
		 * 
		 * // Get a fake user user.getIMUsersByUserName("FakeUser001");
		 * 
		 * // Get 12 users user.getIMUsersBatch(null, null);
		 */

		// Map<String, String> ext=new HashMap<String, String>();
		// ext.put("nickname", "测试昵称25");
		// ext.put("avatar", "");
		// ext.put("origin_avatar", "");
		//
		// //System.out.println(sendTxtMessage("",new
		// String[]{"13"},"hello",ext));
		//// System.out.println(sendTxtMessage("25",new
		// String[]{"15"},"hello",ext));
		//
		// System.out.println(addFriend("26","15"));

		// Map<String, String> ext = new HashMap<String, String>();
		// ext.put("action",
		// String.valueOf(MessageAction.ACTION_SOMEONE_LIKE_ME_TIP.ordinal()));
		//
		 System.out.println(sendTxtMessage("admin",new String[]{"15"},"",new HashMap<String, String>()));

//		Object obj = registUser("13", "13", "zah");
//		if (obj instanceof ResponseWrapper) {
//			ResponseWrapper response = (ResponseWrapper) obj;
//
//			ObjectNode node = (ObjectNode) response.getResponseBody();
//
//			System.out.println(response.getResponseStatus());
//			System.out.println(node.get("error"));
//		}
	}

	public static Object registUser(String userName, String password, String nickname) {
		initFactory();
		IMUserAPI user = (IMUserAPI) factory.newInstance(EasemobRestAPIFactory.USER_CLASS);
		BodyWrapper userBody = new IMUserBody(userName, password, nickname);
		return user.createNewIMUserSingle(userBody);
	}

	public static Object updateNickName(String userName, String nickname) {
		initFactory();

		IMUserAPI user = (IMUserAPI) factory.newInstance(EasemobRestAPIFactory.USER_CLASS);
		JSONObject payload = new JSONObject();
		payload.put("nickname", nickname);
		return user.modifyIMUserNickNameWithAdminToken(userName, payload);
	}

	public static Object sendTxtMessage(String from, String[] users, String msgTxt, Map<String, String> ext) {
		
		if(ext==null) {
			ext=new HashMap<String, String>();
		}
		ext.put("send_by_admim", "admin");
		initFactory();

		BodyWrapper payload = new TextMessageBody("users", users, from, ext, msgTxt);
		SendMessageAPI message = (SendMessageAPI) factory.newInstance(EasemobRestAPIFactory.SEND_MESSAGE_CLASS);
		return message.sendMessage(payload);
	}

	public static Object sendCmdMessage(String from, String[] users, Map<String, String> ext) {
		
		if(ext==null) {
			ext=new HashMap<String, String>();
		}
		ext.put("send_by_admim", "admin");
		
		initFactory();
		BodyWrapper payload = new CmdMessageBody("users", users, from, ext);
		SendMessageAPI message = (SendMessageAPI) factory.newInstance(EasemobRestAPIFactory.SEND_MESSAGE_CLASS);

		return message.sendMessage(payload);
	}

	public static Object addFriend(String user_id, String friend_id) {
		initFactory();
		IMUserAPI user = (IMUserAPI) factory.newInstance(EasemobRestAPIFactory.USER_CLASS);
		return user.addFriendSingle(user_id, friend_id);
	}
}
