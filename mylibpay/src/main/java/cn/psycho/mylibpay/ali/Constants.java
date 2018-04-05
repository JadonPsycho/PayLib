package cn.psycho.mylibpay.ali;

/**
 * 参数设置
 * Created by Psycho on 2018/4/4.
 */

public class Constants {

    /** 支付宝支付业务：入参app_id */
    public static String APPID = "2014100900013222";

    //授权
    /** 支付宝账户登录授权业务：入参pid值 */
    public static String PID = "2088121345789370";
    /** 支付宝账户登录授权业务：入参target_id值 */
    public static String TARGET_ID = "20141225xxxx";


    /** 商户私钥，pkcs8格式 */
    /** 如下私钥，RSA2_PRIVATE 或者 RSA_PRIVATE 只需要填入一个 */
    /** 如果商户两个都设置了，优先使用 RSA2_PRIVATE */
    /** RSA2_PRIVATE 可以保证商户交易在更加安全的环境下进行，建议使用 RSA2_PRIVATE */
    /** 获取 RSA2_PRIVATE，建议使用支付宝提供的公私钥生成工具生成， */
    /** 工具地址：https://doc.open.alipay.com/docs/doc.htm?treeId=291&articleId=106097&docType=1 */
    public static String RSA2_PRIVATE = "";
    public static String RSA_PRIVATE = "";

    public static final int SDK_PAY_FLAG = 1;
    public static final int SDK_AUTH_FLAG = 2;

    //支付信息从服务器接受
    public Constants(String APPID){
        this.APPID = APPID;
    }

    //授权（登录） 实例化数据
    public Constants(String APPID, String PID, String TARGET_ID) {
        this.APPID = APPID;
        this.PID = PID;
        this.TARGET_ID = TARGET_ID;
    }

    //支付信息签名 有客户端完成(私钥使用由用户自行选择)
    public Constants(String APPID, String RSA_PRIVATE ,boolean isRSA2){
        this.APPID = APPID;
        if (isRSA2){
            this.RSA2_PRIVATE = RSA_PRIVATE;
        }else {
            this.RSA_PRIVATE = RSA_PRIVATE;
        }

    }

}
