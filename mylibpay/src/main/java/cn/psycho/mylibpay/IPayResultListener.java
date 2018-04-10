package cn.psycho.mylibpay;

/**
 * Created by Psycho on 2018/4/8.
 */

public interface IPayResultListener {
    /**
     * 支付成功
     * @param resultInfo
     */
    void onSuccess(String resultInfo);

    /**
     * 支付失败
     */
    void onFail();
}
