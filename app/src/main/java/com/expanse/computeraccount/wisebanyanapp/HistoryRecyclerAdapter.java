package com.expanse.computeraccount.wisebanyanapp;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.expanse.computeraccount.wisebanyanapp.pojo.StockTickerObject;
import com.expanse.computeraccount.wisebanyanapp.pojo.TickerDateInfoObject;

import java.util.ArrayList;

public class HistoryRecyclerAdapter extends RecyclerView.Adapter<HistoryRecyclerAdapter.ViewHolder> {

    private ArrayList<TickerDateInfoObject> mData;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    // data is passed into the constructor
    HistoryRecyclerAdapter(Context context, StockTickerObject tickerObject) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = tickerObject.dataList;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.history_list_compound_row, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        TickerDateInfoObject tickerDateInfoObject = mData.get(position);
        String date = tickerDateInfoObject.date;
        String open = tickerDateInfoObject.open;
        String close = tickerDateInfoObject.close;
        String high = tickerDateInfoObject.high;
        String low = tickerDateInfoObject.low;
        String volume = tickerDateInfoObject.volume;

        String previousClose = "";
        String change = "";
        if(position<mData.size()-1){
            previousClose = mData.get(position+1).close;
            double thisClose = Double.valueOf(close);
            double preClose = Double.valueOf(previousClose);
            change = String.valueOf(round((thisClose/preClose)-1,2)*100);
        }

        double changeNumber = Double.parseDouble(change);

        String closeString = "$"+close;
        holder.closeTextView.setText(closeString);
        holder.closeTextView.setTypeface(null, Typeface.BOLD);
        holder.closeTextView.setTextSize(20);
        holder.volumeTextView.setText(volume);
        holder.dateTextView.setText(date);
        if(changeNumber<0){
            holder.percentSymbol.setTextColor(Color.RED);
        }else {
            holder.percentSymbol.setTextColor(Color.GREEN);
        }
        holder.percentTextView.setText(change);
    }
    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView closeTextView;
        TextView percentTextView;
        TextView dateTextView;
        TextView volumeTextView;
        TextView percentSymbol;

        ViewHolder(View itemView) {
            super(itemView);
            closeTextView = itemView.findViewById(R.id.close);
            percentTextView = itemView.findViewById(R.id.percent_value);
            dateTextView = itemView.findViewById(R.id.date);
            volumeTextView = itemView.findViewById(R.id.volume_value);
            percentSymbol = itemView.findViewById(R.id.percent_symbol);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            if (mClickListener != null) mClickListener.onItemClick(view, position);
        }
    }

    // convenience method for getting data at click position
    TickerDateInfoObject getItem(int id) {
        return mData.get(id);
    }

    // allows clicks events to be caught
    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}
