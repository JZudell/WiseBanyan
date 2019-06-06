package com.expanse.computeraccount.wisebanyanapp.pojo;

import java.util.ArrayList;

public class StockTickerObject {
    //For each historical item, show the date, close price, volume and percentage change in price from the previous day.
    public String symbol = "";
    public String lastRefreshed = "";
    public String timeZone = "";
    public ArrayList<TickerDateInfoObject> dataList = new ArrayList<>();
}
