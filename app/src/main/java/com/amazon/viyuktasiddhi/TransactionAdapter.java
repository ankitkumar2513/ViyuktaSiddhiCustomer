package com.amazon.viyuktasiddhi;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.amazon.viyuktasiddhi.model.TransactionResponse;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TransactionAdapter extends ArrayAdapter<TransactionResponse> {
    private final String userType;
    private Context mContext;

    public TransactionAdapter(@NonNull Context context, @NonNull List<TransactionResponse> objects,
                              @NonNull String userType) {
        super(context, R.layout.custom_list_item, objects);
        this.mContext = context;
        this.userType = userType;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        TransactionResponse response = getItem(position);


        ViewHolderItem viewHolder;

        /*
         * The convertView argument is essentially a "ScrapView" as described is Lucas post
         * http://lucasr.org/2012/04/05/performance-tips-for-androids-listview/
         * It will have a non-null value when ListView is asking you recycle the row layout.
         * So, when convertView is not null, you should simply update its contents instead of inflating a new row layout.
         */
        if(convertView==null){

            // inflate the layout
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            convertView = inflater.inflate(R.layout.custom_list_item, parent, false);

            // well set up the ViewHolder
            viewHolder = new ViewHolderItem();
            viewHolder.storeName = (TextView) convertView.findViewById(R.id.storeName);
            viewHolder.transactionDate = (TextView) convertView.findViewById(R.id.date);
            viewHolder.transactionAmount = (TextView) convertView.findViewById(R.id.amount);

            // store the holder with the view.
            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolderItem) convertView.getTag();
        }

        final DateFormat df = new SimpleDateFormat("dd-MM-YY HH:MM");

        if(response != null) {

            if ("ApayStore".equals(userType)) {
                viewHolder.storeName.setText(response.getCustomerName());
            } else {
                viewHolder.storeName.setText(response.getStoreName());
            }
            viewHolder.storeName.setTag(response.getStoreId());

            Date date = new Date(response.getTimestamp());
            viewHolder.transactionDate.setText(df.format(date));
            viewHolder.storeName.setTag(response.getTimestamp());

            viewHolder.transactionAmount.setText("\u20B9 " + response.getTransactionAmount());
            viewHolder.transactionAmount.setTag(response.getTransactionId());

        }

        return convertView;
    }

    // our ViewHolder.
    // caches our TextView
    static class ViewHolderItem {
        TextView storeName;
        TextView transactionDate;
        TextView transactionAmount;

    }
}
