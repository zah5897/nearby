package com.easemob.server.example;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.http.conn.EofSensorInputStream;

import com.easemob.server.example.api.IMUserAPI;
import com.easemob.server.example.api.SendMessageAPI;
import com.easemob.server.example.api.impl.EasemobChatMessage;
import com.easemob.server.example.comm.ClientContext;
import com.easemob.server.example.comm.EasemobRestAPIFactory;
import com.easemob.server.example.comm.body.CmdMessageBody;
import com.easemob.server.example.comm.body.IMUserBody;
import com.easemob.server.example.comm.body.TextMessageBody;
import com.easemob.server.example.comm.wrapper.BodyWrapper;
import com.easemob.server.example.comm.wrapper.ResponseWrapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.zhan.app.nearby.bean.user.BaseUser;
import com.zhan.app.nearby.service.VipService;
import com.zhan.app.nearby.util.DateTimeUtil;
import com.zhan.app.nearby.util.HX_SessionUtil;
import com.zhan.app.nearby.util.JSONUtil;
import com.zhan.app.nearby.util.SpringContextUtil;

import sun.misc.BASE64Encoder;

public class Main {
	public static EasemobRestAPIFactory factory;
	public static final String SYS = "admin";

	public static void initFactory() {
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
		 * // Create a IM user // BodyWrapper userBody = new IMUserBody("User101",
		 * "123456", "HelloWorld"); // user.createNewIMUserSingle(userBody);
		 * 
		 * // Create some IM users List<IMUserBody> users = new ArrayList<IMUserBody>();
		 * users.add(new IMUserBody("User002", "123456", null)); users.add(new
		 * IMUserBody("User003", "123456", null)); BodyWrapper usersBody = new
		 * IMUsersBody(users); user.createNewIMUserBatch(usersBody);
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
		// System.out.println(sendTxtMessage("admin",new String[]{"15"},"",new
		// HashMap<String, String>()));

		// String password = MD5Util.getMd5_16(90046+"");
		// Object obj = registUser("90046", password, "90046");
		// if (obj instanceof ResponseWrapper) {
		// ResponseWrapper response = (ResponseWrapper) obj;
		////
		// ObjectNode node = (ObjectNode) response.getResponseBody();
		////
		// System.out.println(response.getResponseStatus());
		// System.out.println(node.get("error"));
		// }

		// boolean r = disconnectUser("41");
		// System.out.println(r);

//		Map<String, String> ext = new HashMap<String, String>();
//		ext.put("nickname", "27");
//		ext.put("avatar", "测试");
//		ext.put("origin_avatar", "http://www.abc");
//		ext.put("bottle_id", "20");
//		Object obj=sendTxtMessage(String.valueOf(27), new String[] { String.valueOf(41) }, "测试", ext,
//				PushMsgType.TYPE_NEW_CONVERSATION,"测试消息");
//		System.out.println(obj.toString());

//		System.out.println(disconnect("112410"));

//		Object obj = Main.sendCmdMessage( new String[] { String.valueOf(133258) }, new HashMap<>());
//		System.out.println(obj);

//		Map map=new HashMap<>();
//		map.put("image_id", "0");
//		Main.sendTxtMessage("admin", new String[] {"133258"},map);
		// disconnectUser("1");
		initFactory();
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

		Map<String, String> payload = new HashMap<String, String>();
		payload.put("nickname", nickname);
		return user.modifyIMUserNickNameWithAdminToken(userName, payload);
	}

	public static Object sendTxtMessageByAdmin(String[] users, String msgTxt, Map<String, String> ext, String TYPE) {
		if (ext == null) {
			ext = new HashMap<String, String>();
		}
		ext.put("send_by_admim", "admin");
		String alert = "系统消息:" + msgTxt;
		Map<String, String> apns = new HashMap<String, String>();
		apns.put("type", TYPE);
		apns.put("msg", msgTxt);

		apns.put("em_push_content", alert);
		apns.put("em_push_name", "漂流瓶交友");
		apns.put("extern", alert);
		try {
			ext.put("em_apns_ext", JSONUtil.writeValueAsString(apns));
		} catch (Exception e) {
			e.printStackTrace();
		}
		initFactory();
		BodyWrapper payload = new TextMessageBody("users", users, SYS, ext, msgTxt);
		SendMessageAPI message = (SendMessageAPI) factory.newInstance(EasemobRestAPIFactory.SEND_MESSAGE_CLASS);
		return message.sendMessage(payload);
	}

	public static Object sendTxtMessage(long from, String[] users, String msgTxt, Map<String, String> ext) {
		return sendTxtMessage(String.valueOf(from), users, msgTxt, ext);
	}

	public static Object sendTxtMessage(BaseUser from, String[] users, String msgTxt, Map<String, String> ext) {
		return sendTxtMessage(String.valueOf(from.getUser_id()), users, msgTxt, ext);
	}

	public static Object sendTxtMessage(String from, String[] users, String msgTxt, Map<String, String> ext) {

		String TYPE = "NEW_CONVERSATION";

		if (ext == null) {
			ext = new HashMap<String, String>();
		}

		String nickName = ext.get("nickname");

		ext.put("send_by_admim", "admin");
		String alert = nickName + ":" + msgTxt;
		Map<String, String> apns = new HashMap<String, String>();
		apns.put("type", TYPE);
		apns.put("msg", msgTxt);

		apns.put("em_push_content", alert);
		apns.put("em_push_name", "漂流瓶交友");
		apns.put("extern", alert);
		try {
			ext.put("em_apns_ext", JSONUtil.writeValueAsString(apns));
		} catch (Exception e) {
			e.printStackTrace();
		}
		initFactory();
		BodyWrapper payload = new TextMessageBody("users", users, from, ext, msgTxt);
		SendMessageAPI message = (SendMessageAPI) factory.newInstance(EasemobRestAPIFactory.SEND_MESSAGE_CLASS);
		return message.sendMessage(payload);
	}

	public static Object sendCmdMessageVipInfo(String[] toUsers, Map<String, String> ext) {

		Map<String, String> apns = new HashMap<String, String>();
		createCmdMsg(toUsers, ext, apns);

		VipService vipService = SpringContextUtil.getBean("vipService");
		apns.put("buy_vip_info", JSONUtil.writeValueAsString(vipService.globalInfo()));
		try {
			ext.put("em_apns_ext", JSONUtil.writeValueAsString(apns));
		} catch (Exception e) {
			e.printStackTrace();
		}
		initFactory();
		BodyWrapper payload = new CmdMessageBody("users", toUsers, SYS, ext);
		SendMessageAPI message = (SendMessageAPI) factory.newInstance(EasemobRestAPIFactory.SEND_MESSAGE_CLASS);

		return message.sendMessage(payload);
	}

	public static Object sendCmdMessageOnlineMsg(String[] toUsers, Map<String, String> ext, BaseUser onlineUser) {

		Map<String, String> apns = new HashMap<String, String>();
		createCmdMsg(toUsers, ext, apns);

		Map<String, String> onlineUserMap = new HashMap<String, String>();
		HX_SessionUtil.putDataInfo(onlineUserMap, onlineUser);
		onlineUserMap.put("sender_sex", onlineUser.getSex());
		onlineUserMap.put("sender_age", DateTimeUtil.getAge(onlineUser.getBirthday()));
		apns.put("online_user", JSONUtil.writeValueAsString(onlineUserMap));
		try {
			ext.put("em_apns_ext", JSONUtil.writeValueAsString(apns));
		} catch (Exception e) {
			e.printStackTrace();
		}
		initFactory();
		BodyWrapper payload = new CmdMessageBody("users", toUsers, SYS, ext);
		SendMessageAPI message = (SendMessageAPI) factory.newInstance(EasemobRestAPIFactory.SEND_MESSAGE_CLASS);

		return message.sendMessage(payload);
	}

	public static Object sendCmdMessage(String[] toUsers, Map<String, String> ext) {
		Map<String, String> apns = new HashMap<String, String>();
		createCmdMsg(toUsers, ext, apns);
		try {
			ext.put("em_apns_ext", JSONUtil.writeValueAsString(apns));
		} catch (Exception e) {
			e.printStackTrace();
		}

		initFactory();
		BodyWrapper payload = new CmdMessageBody("users", toUsers, SYS, ext);
		SendMessageAPI message = (SendMessageAPI) factory.newInstance(EasemobRestAPIFactory.SEND_MESSAGE_CLASS);

		return message.sendMessage(payload);
	}

	private static void createCmdMsg(String[] toUsers, Map<String, String> ext, Map<String, String> apns) {
		if (ext == null) {
			ext = new HashMap<String, String>();
		}
		ext.put("send_by_admim", SYS);
		apns.put("type", ext.get("type"));
		apns.put("msg", ext.get("msg"));

		apns.put("em_push_content", ext.get("msg"));
		apns.put("em_push_name", "漂流瓶交友");
		apns.put("extern", ext.get("msg"));
	}

	public static Object addFriend(String user_id, String friend_id) {
		initFactory();
		IMUserAPI user = (IMUserAPI) factory.newInstance(EasemobRestAPIFactory.USER_CLASS);
		return user.addFriendSingle(user_id, friend_id);
	}

	/**
	 * 强制用户下线
	 * 
	 * @param username
	 * @return
	 */
	public static Object disconnect(String username) {
		initFactory();
		IMUserAPI user = (IMUserAPI) factory.newInstance(EasemobRestAPIFactory.USER_CLASS);
		ResponseWrapper wrapper = (ResponseWrapper) user.disconnectIMUser(username);
		return wrapper.getResponseBody();
//		org_name}/{app_name}/users/{username}/disconnect
	}

	public static boolean disconnectUser(String userName) {
		initFactory();
		IMUserAPI user = (IMUserAPI) factory.newInstance(EasemobRestAPIFactory.USER_CLASS);
		ResponseWrapper wrapper = (ResponseWrapper) user.disconnectIMUser(userName);

		ObjectNode node = (ObjectNode) wrapper.getResponseBody();
		Map<String, Object> requestResult = JSONUtil.jsonToMap(node.toString());

		try {
			@SuppressWarnings("unchecked")
			boolean b = (boolean) ((Map<String, Object>) requestResult.get("data")).get("result");
			return b;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}

	static String getChatMessagesDownloadURL(String timePoint) {
		initFactory();
		EasemobChatMessage message = (EasemobChatMessage) factory.newInstance(EasemobRestAPIFactory.MESSAGE_CLASS);
		ResponseWrapper wrapper = (ResponseWrapper) message.exportChatMessages("2020051810");
		if (wrapper.getResponseStatus() == 200) {
			Map<String, Object> requestResult = JSONUtil.jsonToMap(wrapper.getResponseBody().toString());
			List<Map> data = (List<Map>) requestResult.get("data");
			return data.get(0).get("url").toString();
		}
		return null;
	}

	public static String downloadAudioFile(String remoteUrl, String secretKey) throws IOException {
		initFactory();
		EasemobChatMessage message = (EasemobChatMessage) factory.newInstance(EasemobRestAPIFactory.MESSAGE_CLASS);
		ResponseWrapper wrapper = (ResponseWrapper) message.downloadAudioFile(remoteUrl, secretKey);
		if (wrapper.getResponseStatus() == 200) {
			EofSensorInputStream input = (EofSensorInputStream) wrapper.getResponseBody();
			int size = input.available();
			byte[] buffer = new byte[size];
			input.read(buffer);
			String str = new BASE64Encoder().encode(buffer);
			input.close();
			return str;
		} else {
			return null;
		}
	}
	public static void downloadImgFile(HttpServletResponse response,String remoteUrl, String secretKey) throws IOException {
		initFactory();
		EasemobChatMessage message = (EasemobChatMessage) factory.newInstance(EasemobRestAPIFactory.MESSAGE_CLASS);
		ResponseWrapper wrapper = (ResponseWrapper) message.downloadAudioFile(remoteUrl, secretKey);
		if (wrapper.getResponseStatus() == 200) {
			EofSensorInputStream input = (EofSensorInputStream) wrapper.getResponseBody();
			
			
			ByteArrayOutputStream bos=new ByteArrayOutputStream();
			
			byte[] buffer = new byte[1024];
			int len;
			while((len=input.read(buffer))>0) {
				bos.write(buffer,0,len);
			}
			response.setContentType("image/jpg"); // 设置返回的文件类型
		    OutputStream out = response.getOutputStream();
		    out.write(bos.toByteArray());
		    out.flush();
		    out.close();
			input.close();
		}
	}
}
