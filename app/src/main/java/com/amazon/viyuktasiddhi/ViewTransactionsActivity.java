package com.amazon.viyuktasiddhi;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.amazon.viyuktasiddhi.model.TransactionResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

public class ViewTransactionsActivity extends AppCompatActivity {
    private static final String TAG = "ViewTransactions";

    private TransactionAdapter mAdapter;
    private ViyuktServiceClient viyuktServiceClient;
    private ObjectMapper mapper;
    ListView listView;
    TextView textView;
    List<TransactionResponse> transactions;
    private Context mContext;

    @RequiresApi(api = Build.VERSION_CODES.O)
    public ViewTransactionsActivity() {

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_transactions);
        textView = findViewById(R.id.no_internet);
        if (isNetworkConnected()) {
            this.viyuktServiceClient = new ViyuktServiceClient();
            mapper = new ObjectMapper();
            Toolbar toolbar = findViewById(R.id.toolbar);
            toolbar.setTitle("Amazon Pay");
            setSupportActionBar(toolbar);
            mContext = this;
            listView = findViewById(R.id.transactionsList);
            textView.setVisibility(View.INVISIBLE);
            new ProcessPayment().execute("das");
        } else {
            try {
                listView.setVisibility(View.INVISIBLE);
            } catch (Exception e) {
                Log.d(TAG, "onCreate: " + e.getMessage());
            }
        }
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }

    private class ProcessPayment extends AsyncTask<String, Void, String> {
        private String storeId;

        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        protected String doInBackground(String... params) {
            try {
                return viyuktServiceClient.getTransactionsForStore("AS1");
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            Log.i(TAG, "onPostExecute " + result);
            try {
                transactions = (List<TransactionResponse>)
                        mapper.readValue(result, new TypeReference<List<TransactionResponse>>() {
                        });
                System.out.println(transactions);
                mAdapter = new TransactionAdapter(mContext, transactions, "ApayStore");
                listView.setAdapter(mAdapter);
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
            }
        }
    }
}