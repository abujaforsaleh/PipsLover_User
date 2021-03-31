package com.banglabs.pipslover.datahandler;

import android.util.Log;

public class PairBundle {

    String pair_name;
    String pair_type;
    String pair_statas;//online or expired
    String pair_action;
    String initial_price;
    String current_price;
    String stop_loss;
    String take_profit_1;
    String take_profit_2;
    String trade_result;//current setuation (weating/sl/tp1/tp2)
    String update_time;//when the signal was created
    String sl_pip;
    String tp1_pip;
    String tp2_pip;

    public PairBundle(){

        Log.d("outmsg", "null valued");

    }

    public PairBundle(String pair_name, String current_price, String pair_statas, String pair_action, String pair_type, String initial_price, String stop_loss, String take_profit_1, String take_profit_2, String trade_result, String LastUpdateDate, String sl_pip, String tp1_pip, String tp2_pip) {

        this.pair_name = pair_name;
        this.current_price = current_price;
        this.pair_statas = pair_statas;
        this.pair_action = pair_action;
        this.initial_price = initial_price;
        this.stop_loss = stop_loss;
        this.pair_type = pair_type;
        this.take_profit_1 = take_profit_1;
        this.take_profit_2 = take_profit_2;
        this.trade_result = trade_result;
        this.update_time = LastUpdateDate;
        this.sl_pip = sl_pip;
        this.tp1_pip = tp1_pip;
        this.tp2_pip = tp2_pip;
        Log.d("outmsg", "multi valued");
    }

    public PairBundle(String pair_name) {
        this.pair_name = pair_name;
        this.pair_statas ="";
        this.current_price="";
        this.pair_action ="";
        this.initial_price ="";
        this.stop_loss ="";
        pair_type = "";
        this.take_profit_1 ="";
        this.take_profit_2 ="";
        this.trade_result ="";
        this.update_time ="";
        this.sl_pip = "";
        this.tp1_pip = "";
        this.tp2_pip = "";

    }


    public String getPair_name() {
        return pair_name;
    }

    public String getPair_statas() {
        return pair_statas;
    }

    public String getPair_action() {
        return pair_action;
    }

    public String getInitial_price() {
        return initial_price;
    }

    public String getStop_loss() {
        return stop_loss;
    }

    public String getCurrent_price() { return current_price; }

    public String getPair_type() { return pair_type; }

    public String getTake_profit_1() {
        return take_profit_1;
    }

    public String getTake_profit_2() {
        return take_profit_2;
    }

    public String getTrade_result() {
        return trade_result;
    }

    public String getUpdate_time() {
        return update_time;
    }

    public String getSl_pip() { return sl_pip; }

    public String getTp1_pip() { return tp1_pip; }

    public String getTp2_pip() { return tp2_pip; }
}
