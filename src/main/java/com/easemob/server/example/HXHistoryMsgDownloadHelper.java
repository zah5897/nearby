package com.easemob.server.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import org.apache.log4j.Logger;

import com.zhan.app.nearby.util.DateTimeUtil;
import com.zhan.app.nearby.util.JSONUtil;
import com.zhan.app.nearby.util.TextUtils;

public class HXHistoryMsgDownloadHelper {

	private static Logger log = Logger.getLogger(HXHistoryMsgDownloadHelper.class);

	public static List<HXHistoryMsg> downloadHistoryMessage(String timePoint) {
		String downloadUrl = Main.getChatMessagesDownloadURL(timePoint);
		if (TextUtils.isEmpty(downloadUrl)) {
			return null;
		}
		log.debug(downloadUrl);
		List<String> msgsStrs = downloadGZ(downloadUrl);
		return praseToHXMsg(msgsStrs);
	}

	
	public static void main(String[] args) {
		downloadHistoryMessage(DateTimeUtil.getMessageHistoryTimePoint());
	}
	private static List<HXHistoryMsg> praseToHXMsg(List<String> msgsStrs) {
		if (msgsStrs == null || msgsStrs.isEmpty()) {
			return null;
		}
		List<HXHistoryMsg> msgObj = new ArrayList<>();
		for (String msgStr : msgsStrs) {
			if (msgStr.contains("send_by_admim")) { //说明是从系统发的消息
				continue;
			}
			Map<String, Object> map = JSONUtil.jsonToMap(msgStr);
			HXHistoryMsg msg = jsonToHXMsg(map);
			if (msg != null) {
				msgObj.add(msg);
			}
		}
		return msgObj;
	}

	private static HXHistoryMsg jsonToHXMsg(Map<String, Object> json) {
		Map<String, Object> payload = (Map<String, Object>) json.get("payload");
		String msg_id = json.get("msg_id").toString();
		long timestamp = (long) json.get("timestamp");
		String from = json.get("from").toString();
		String to = json.get("to").toString();
		String chat_type = json.get("chat_type").toString();

		HXHistoryMsg msg = new HXHistoryMsg();
		msg.setMsg_id(msg_id);
		msg.setFrom_id(Long.parseLong(from));
		msg.setTo_id(Long.parseLong(to));
		msg.setChat_type(chat_type);
		msg.setSend_time(new Date(timestamp));

		List<Object> bodies = (List<Object>) payload.get("bodies");
		Map<String, Object> msgsDetail = (Map<String, Object>) bodies.get(0);
		String type = msgsDetail.get("type").toString();
		msg.setType(type);
		if (type.equals("txt")) {
			msg.setContent(msgsDetail.get("msg").toString());
		} else {
			msg.setContent(JSONUtil.writeValueAsString(msgsDetail));
		}
		return msg;
	}

	private static List<String> downloadGZ(String downloadURL) {
		URLConnection conn = null;
		InputStream inStream = null;
		GZIPInputStream gzip = null;
		InputStreamReader reader = null;
		BufferedReader br = null;
		try {
			URL url = new URL(downloadURL);
			conn = url.openConnection();
			conn.setRequestProperty("Accept-Charset", "UTF-8");
			inStream = conn.getInputStream();
			gzip = new GZIPInputStream(inStream);
			reader = new InputStreamReader(gzip, "UTF-8");
			br = new BufferedReader(reader);
			List<String> msgs = new ArrayList<String>();
			String readLine;
			while ((readLine = br.readLine()) != null) {
				msgs.add(readLine);
			}
			return msgs;
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (gzip != null) {
				try {
					gzip.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (inStream != null) {
				try {
					inStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}

}
