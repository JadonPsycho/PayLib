package cn.psycho.mylibpay.ali;

/**
 * 支付业务信息的实体类
 * Created by Psycho on 2018/4/4.
 */

public class PayInfoEntity {

    String subject; //订单title
    String out_trade_no; //订单号
    String total_amount; //总价格

    /* ------ 以上是必选项 ---------*/

    String body; //商品详情
    String timeout_express;//允许商品支付时间
    String goods_type; //商品主类型：0—虚拟类商品，1—实物类商品

    //优惠参数（仅与支付宝协商后可用）渠道禁用和可用渠道等 外部指定买家 这些当前版本暂不考虑

    String store_id;//商户门店编号。该参数用于请求参数中以区分各门店，非必传项


    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getOut_trade_no() {
        return out_trade_no;
    }

    public void setOut_trade_no(String out_trade_no) {
        this.out_trade_no = out_trade_no;
    }

    public String getTotal_amount() {
        return total_amount;
    }

    public void setTotal_amount(String total_amount) {
        this.total_amount = total_amount;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getTimeout_express() {
        return timeout_express;
    }

    public void setTimeout_express(String timeout_express) {
        this.timeout_express = timeout_express;
    }

    public String getGoods_type() {
        return goods_type;
    }

    public void setGoods_type(String goods_type) {
        this.goods_type = goods_type;
    }

    public String getStore_id() {
        return store_id;
    }

    public void setStore_id(String store_id) {
        this.store_id = store_id;
    }
}
