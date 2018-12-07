package com.amazon.viyuktasiddhi;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.amazon.viyuktasiddhi.model.CustomerSellerDataModel;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class ConfirmationActivity extends AppCompatActivity {

    private Button payButton;
    private EditText storeId, amount;

    private String storeIdText;

    ObjectMapper mapper = new ObjectMapper();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmation);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Amazon Pay");
        setSupportActionBar(toolbar);

        storeId = findViewById(R.id.storeId);
        amount = findViewById(R.id.amount);


        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                storeIdText = null;
                storeId.setEnabled(true);
            } else {
                storeIdText= extras.getString("storeId");
                storeId.setEnabled(false);
                storeId.setText(storeIdText);
            }
        } else {
            storeIdText= (String) savedInstanceState.getSerializable("storeId");
            storeId.setEnabled(false);
            storeId.setText(storeIdText);
        }

    }


    public void pay(View view) {
        try {
            Toast.makeText(ConfirmationActivity.this, "Sending message", Toast.LENGTH_SHORT).show();
            sendMessage("9427635124", generateMessage(storeId.getText().toString(), Double.parseDouble(amount.getText().toString())));
            startActivity(new Intent(ConfirmationActivity.this, AuthenticationActivity.class));
        } catch (Exception e) {
            Log.d("Sending", "Error while sending message", e);
        }
    }

    private void sendMessage(final String phone, final String message) {
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage("+91" + phone, null, message, null, null);
    }

    private String generateMessage(final String storeId, final Double amount) throws IOException {
        CustomerSellerDataModel message = new CustomerSellerDataModel();
        message.setStoreId(storeId);
        message.setAmount(amount);
        return mapper.writeValueAsString(message);
    }

}
