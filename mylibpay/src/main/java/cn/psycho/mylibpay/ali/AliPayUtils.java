package cn.psycho.mylibpay.ali;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.psycho.mylibpay.IPayResultListener;

import static cn.psycho.mylibpay.ali.Constants.SDK_PAY_FLAG;

/**
 * 支付宝新的支付签名调用
 * Created by Psycho on 2018/4/4.
 */

public class AliPayUtils {

    private Activity activity;
    private IPayResultListener payResultListener;

    public AliPayUtils(Activity activity) {
        this.activity = activity;
    }

    public void setPayResultListener(IPayResultListener payResultListener) {
        this.payResultListener = payResultListener;
    }

    /**
     * 支付结果并回调给当前Activity
     */
    @SuppressLint("HandlerLeak")
    Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case SDK_PAY_FLAG:
                    PayResult payResult = new PayResult((Map<String, String>) msg.obj);
                    String resultInfo = payResult.getResult();// 同步返回需要验证的信息
                    String resultStatus = payResult.getResultStatus();
                    // 判断resultStatus 为9000则代表支付成功
                    if (TextUtils.equals(resultStatus, "9000")) {
                        // 该笔订单是否真实支付成功，需要依赖服务端的异步通知。
                        Log.e("TAG", "handleMessage: "+ resultStatus + "   支付成功" );

                        payResultListener.onSuccess(resultInfo);

                    } else {
                        // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
                        Toast.makeText(activity, "支付失败", Toast.LENGTH_SHORT).show();
                        payResultListener.onFail();
                    }
                    break;
            }
        }
    };

    /**
     *
     * @param orderInfo 支付信息（已签名） 该数据由服务器返回
     */
    public void pay(final String orderInfo){
        Runnable payRunnable = new Runnable() {

            @Override
            public void run() {
                PayTask alipay = new PayTask(activity);
                Map<String, String> result = alipay.payV2(orderInfo, true);
                Log.i("msp", result.toString());
                Message msg = new Message();
                msg.what = SDK_PAY_FLAG;
                msg.obj = result;
                mHandler.sendMessage(msg);
            }
        };

        Thread payThread = new Thread(payRunnable);
        payThread.start();
    }


    /**
     * 直接在客户端进行参数签名
     * @param entity
     */
    public void pay(PayInfoEntity entity){

        boolean rsa2 = (Constants.RSA2_PRIVATE.length() > 0);
        Map<String, String> params = buildOrderParamMap(Constants.APPID, rsa2,entity);
        String orderParam = buildOrderParam(params);

        String privateKey = rsa2 ? Constants.RSA2_PRIVATE : Constants.RSA_PRIVATE;
        String sign = getSign(params, privateKey, rsa2);
        final String orderInfo = orderParam + "&" + sign;

        Runnable payRunnable = new Runnable() {

            @Override
            public void run() {
                PayTask alipay = new PayTask(activity);
                Map<String, String> result = alipay.payV2(orderInfo, true);
                Log.i("msp", result.toString());

                Message msg = new Message();
                msg.what = SDK_PAY_FLAG;
                msg.obj = result;
                mHandler.sendMessage(msg);
            }
        };

        Thread payThread = new Thread(payRunnable);
        payThread.start();

    }



    /**
     * 对支付参数信息进行签名
     *
     * @param map
     *            待签名授权信息
     *
     * @return
     */
    public static String getSign(Map<String, String> map, String rsaKey, boolean rsa2) {
        List<String> keys = new ArrayList<String>(map.keySet());
        // key排序
        Collections.sort(keys);

        StringBuilder authInfo = new StringBuilder();
        for (int i = 0; i < keys.size() - 1; i++) {
            String key = keys.get(i);
            String value = map.get(key);
            authInfo.append(buildKeyValue(key, value, false));
            authInfo.append("&");
        }

        String tailKey = keys.get(keys.size() - 1);
        String tailValue = map.get(tailKey);
        authInfo.append(buildKeyValue(tailKey, tailValue, false));

        String oriSign = SignUtils.sign(authInfo.toString(), rsaKey, rsa2);
        String encodedSign = "";

        try {
            encodedSign = URLEncoder.encode(oriSign, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "sign=" + encodedSign;
    }


    /**
     * 构造支付订单参数信息
     *
     * @param map
     * 支付订单参数
     * @return
     */
    public static String buildOrderParam(Map<String, String> map) {
        List<String> keys = new ArrayList<String>(map.keySet());

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < keys.size() - 1; i++) {
            String key = keys.get(i);
            String value = map.get(key);
            sb.append(buildKeyValue(key, value, true));
            sb.append("&");
        }

        String tailKey = keys.get(keys.size() - 1);
        String tailValue = map.get(tailKey);
        sb.append(buildKeyValue(tailKey, tailValue, true));

        return sb.toString();
    }


    /**
     * 拼接键值对
     *
     * @param key
     * @param value
     * @param isEncode
     * @return
     */
    private static String buildKeyValue(String key, String value, boolean isEncode) {
        StringBuilder sb = new StringBuilder();
        sb.append(key);
        sb.append("=");
        if (isEncode) {
            try {
                sb.append(URLEncoder.encode(value, "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                sb.append(value);
            }
        } else {
            sb.append(value);
        }
        return sb.toString();
    }

    /**
     * 构造支付订单参数列表
     * @param
     * @param app_id
     * @param
     * @return
     */
    private Map<String, String> buildOrderParamMap(String app_id, boolean rsa2,PayInfoEntity entity) {
        Map<String, String> keyValues = new HashMap<String, String>();

        keyValues.put("app_id", app_id);

        keyValues.put("biz_content", getOrderContentParams(entity));

        keyValues.put("charset", "utf-8");

        keyValues.put("method", "alipay.trade.app.pay");

        keyValues.put("sign_type", rsa2 ? "RSA2" : "RSA");

        keyValues.put("timestamp", TimeUtils.getCurrentTime());

        keyValues.put("version", "1.0");

        return keyValues;
    }

    /**
     * 构造支付订单业务参数列表
     * @param entity 关于订单信息的参数
     * @return
     */
    private String getOrderContentParams(PayInfoEntity entity){
        StringBuffer sb = new StringBuffer();
        sb.append("{\"subject\":\""+entity.getSubject()+"\",");
        sb.append("\"product_code\":\"QUICK_MSECURITY_PAY\",");
        sb.append("\"out_trade_no\":\"" + entity.getOut_trade_no() +  "\"}");
        sb.append("\"total_amount\":\""+entity.getTotal_amount()+"\"");
        if (!entity.getBody().equals("")){
            sb.append(",\"body\":\""+entity.getBody()+"\"");
        }
        if (!entity.getGoods_type().equals("")){
            sb.append(",\"goods_type\":\""+entity.getGoods_type()+"\"");
        }
        if (!entity.getStore_id().equals("")){
            sb.append(",\"store_id\":\""+entity.getStore_id()+"\"");
        }
        if (!entity.getTimeout_express().equals("")){
            sb.append(",\"timeout_express\":\""+entity.getTimeout_express()+"\"");
        }
        sb.append("}");
        return sb.toString();
    }



}
