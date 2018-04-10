package cn.psycho.mylibpay;


import android.app.Activity;

/**
 * Created by Psycho on 2018/4/8.
 */

public class PsychoPay {

    private Activity activity;
    private int payType = 0;

    private PsychoPay(Activity activity){
        this.activity = activity;
    }

    public static PsychoPay with(Activity activity) {
        return new PsychoPay(activity);
    }

    public PsychoPay setPayType(int payType){
        this.payType = payType;
        return this;
    }



}
