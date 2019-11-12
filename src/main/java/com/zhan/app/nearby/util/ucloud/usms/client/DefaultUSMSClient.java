package com.zhan.app.nearby.util.ucloud.usms.client;

import com.zhan.app.nearby.util.ucloud.usms.model.GetUSMSSendReceiptParam;
import com.zhan.app.nearby.util.ucloud.usms.model.GetUSMSSendReceiptResult;
import com.zhan.app.nearby.util.ucloud.usms.model.SendUSMSMessageParam;
import com.zhan.app.nearby.util.ucloud.usms.model.SendUSMSMessageResult;

import cn.ucloud.common.client.DefaultUcloudClient;
import cn.ucloud.common.handler.UcloudHandler;
import cn.ucloud.common.http.UcloudHttp;
import cn.ucloud.common.http.UcloudHttpImpl;
import cn.ucloud.common.pojo.UcloudConfig;

/**
 * @Description : USMS 默认客户端
 * @Author : codezhang
 * @Date : 2019-06-04 17:34
 **/
public class DefaultUSMSClient extends DefaultUcloudClient implements USMSClient {

    public DefaultUSMSClient(UcloudConfig config) {
        super(config);
    }

    @Override
    public SendUSMSMessageResult sendUSMSMessage(SendUSMSMessageParam param) throws Exception {
        UcloudHttp http = new UcloudHttpImpl(SendUSMSMessageResult.class);
        return (SendUSMSMessageResult) http.doGet(param, config, null);
    }

    @Override
    public void sendUSMSMessage(SendUSMSMessageParam param, UcloudHandler<SendUSMSMessageResult> handler,
                                Boolean... asyncFlag) {
        UcloudHttp http = new UcloudHttpImpl(SendUSMSMessageResult.class);
        try {
            http.doGet(param, config, handler, asyncFlag);
        } catch (Exception e) {
        }
    }

    @Override
    public GetUSMSSendReceiptResult getUSMSSendReceipt(GetUSMSSendReceiptParam param) throws Exception {
        UcloudHttp http = new UcloudHttpImpl(GetUSMSSendReceiptResult.class);
        return (GetUSMSSendReceiptResult) http.doGet(param, config, null);
    }

    @Override
    public void getUSMSSendReceipt(GetUSMSSendReceiptParam param, UcloudHandler<GetUSMSSendReceiptResult> handler, Boolean... asyncFlag) {
        UcloudHttp http = new UcloudHttpImpl(GetUSMSSendReceiptResult.class);
        try {
            http.doGet(param, config, handler, asyncFlag);
        } catch (Exception e) {
        }
    }
}
