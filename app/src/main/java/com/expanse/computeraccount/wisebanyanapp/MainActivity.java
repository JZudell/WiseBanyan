package com.expanse.computeraccount.wisebanyanapp;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.expanse.computeraccount.wisebanyanapp.network.GetTickerDataTask;
import com.expanse.computeraccount.wisebanyanapp.pojo.StockTickerObject;
import com.expanse.computeraccount.wisebanyanapp.pojo.TickerDateInfoObject;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements MainActivityListener{

    RecyclerView recyclerView;
    HistoryRecyclerAdapter adapter;
    StockTickerObject currentTickerObject;
    String query = "";
    ProgressDialog nDialog;
    TextView tickerSymbol;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
      //  Toolbar toolbar = findViewById(R.id.toolbar);
     //   setSupportActionBar(toolbar);

        tickerSymbol = findViewById(R.id.main_symbol_text);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showEditSearchDialog();
            }
        });
        currentTickerObject = new StockTickerObject();
        startGetTickerData("TSLA");
    }
    void showEditSearchDialog(){
        LayoutInflater adbInflater = LayoutInflater.from(this);
        View editTextLayout = adbInflater.inflate(R.layout.edit_input, null);

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(R.string.search_title);
        final EditText input = editTextLayout.findViewById(R.id.edit_input);

        alert.setView(editTextLayout);
        alert.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                query = input.getText().toString();
                if(!query.equals("")){
                    startGetTickerData(query);
                }
            }
        });
        alert.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled.
            }
        });
        alert.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void startGetTickerData(String symbol) {

        showLoadingPopup();

        GetTickerDataTask getTickerDataTask = new GetTickerDataTask();
        getTickerDataTask.setListener(this);
        getTickerDataTask.execute(symbol);
    }

    public void showLoadingPopup(){
        nDialog = new ProgressDialog(this);
        String loading = this.getString(R.string.loading)+"...";
        nDialog.setMessage(loading);
        nDialog.setIndeterminate(false);
        nDialog.setCancelable(true);
        nDialog.show();
    }


    @Override
    public void returnTickerData(StockTickerObject data) {

        currentTickerObject = data;
        tickerSymbol.setText(currentTickerObject.symbol);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new HistoryRecyclerAdapter(this, currentTickerObject);
        // adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);
        initializeGraph(400);
    }

    @Override
    public void returnError() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.error_alert)
                .setCancelable(false)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //do things
                        dialog.dismiss();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
        nDialog.dismiss();
    }


    void initializeGraph(int days){
        DataPoint[] dataPoints = new DataPoint[days];
        ArrayList<TickerDateInfoObject> listOfInfo = currentTickerObject.dataList;
        for ( int x = days-1;x>-1;x--){
            String dayClose = listOfInfo.get(x).close;
            dataPoints[days-x-1] = new DataPoint(days-x, Double.parseDouble(dayClose));
        }

        GraphView graph = findViewById(R.id.graph);
        graph.removeAllSeries();
        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(dataPoints);

        graph.addSeries(series);
        GridLabelRenderer gridLabel = graph.getGridLabelRenderer();
        gridLabel.setHorizontalAxisTitle("One Year");

        nDialog.dismiss();
    }
}
