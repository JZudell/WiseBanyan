package com.expanse.computeraccount.wisebanyanapp;

import com.expanse.computeraccount.wisebanyanapp.pojo.StockTickerObject;

public interface MainActivityListener {

    public void returnTickerData(StockTickerObject object);

    public void returnError();
}
