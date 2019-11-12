package com.zhan.app.nearby.util.ucloud.usms.client;

import com.zhan.app.nearby.util.ucloud.usms.model.GetUSMSSendReceiptParam;
import com.zhan.app.nearby.util.ucloud.usms.model.GetUSMSSendReceiptResult;
import com.zhan.app.nearby.util.ucloud.usms.model.SendUSMSMessageParam;
import com.zhan.app.nearby.util.ucloud.usms.model.SendUSMSMessageResult;

import cn.ucloud.common.client.UcloudClient;
import cn.ucloud.common.handler.UcloudHandler;

/**
 * @Description : USMS 客户端接口
 * @Author : codezhang
 * @Date : 2019年06月04日
 **/
public interface USMSClient extends UcloudClient {


    /**
     * 发送短信息
     *
     * @param param 请求参数对象
     * @return 结果对象
     * @throws Exception 请求出错则抛出异常
     */
    SendUSMSMessageResult sendUSMSMessage(SendUSMSMessageParam param) throws Exception;

    /**
     * 发送短信息
     *
     * @param param     请求参数对象
     * @param handler   异步处理器
     * @param asyncFlag 异步标记位，默认true异步
     */
    void sendUSMSMessage(SendUSMSMessageParam param, UcloudHandler<SendUSMSMessageResult> handler,
                         Boolean... asyncFlag);


    /**
     * 获取短信发送回执信息
     *
     * @param param 请求参数对象
     * @return 结果对象
     * @throws Exception 请求出错则抛出异常
     */
    GetUSMSSendReceiptResult getUSMSSendReceipt(GetUSMSSendReceiptParam param) throws Exception;

    /**
     * 获取短信发送回执信息
     *
     * @param param     请求参数对象
     * @param handler   异步处理器
     * @param asyncFlag 异步标记位，默认true异步
     */
    void getUSMSSendReceipt(GetUSMSSendReceiptParam param, UcloudHandler<GetUSMSSendReceiptResult> handler,
                            Boolean... asyncFlag);


}