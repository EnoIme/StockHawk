package com.udacity.stockhawk.ui;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.EntryXComparator;
import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    public static final String KEY_SYMBOL = "symbol";
    private static String SYMBOL;

    @BindView(R.id.stock_trend)
    LineChart mStockTrend;
    // List<Entry> stockHistoryDates;
    List<Entry> stockHistoryAmounts;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        SYMBOL = getIntent().getStringExtra(KEY_SYMBOL);
        ButterKnife.bind(this);

        getSupportLoaderManager().initLoader(1, null, this);

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(DetailActivity.this,
                Contract.Quote.makeUriForStock(SYMBOL),
                new String[]{Contract.Quote.COLUMN_HISTORY},
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        //stockHistoryDates = new ArrayList<>();
        stockHistoryAmounts = new ArrayList<>();

        String[] history;
        while (data.moveToNext()) {
            history = data.getString(data.getColumnIndexOrThrow(Contract.Quote.COLUMN_HISTORY)).split("\n");

            for (int i = 0; i < history.length; i++) {
                stockHistoryAmounts.add(new Entry(i, Float.parseFloat(history[i].split(",")[1].trim())));
            }
        }
        Collections.sort(stockHistoryAmounts, new EntryXComparator());

        LineDataSet dataSetAmounts = new LineDataSet(stockHistoryAmounts, getString(R.string.stock_trend_amount, SYMBOL));
        dataSetAmounts.setAxisDependency(YAxis.AxisDependency.LEFT);

        LineData lineData = new LineData(dataSetAmounts);
        mStockTrend.setData(lineData);
        mStockTrend.getXAxis().setGranularity(1f);
        mStockTrend.invalidate();

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
