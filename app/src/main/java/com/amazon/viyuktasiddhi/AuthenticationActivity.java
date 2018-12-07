package com.amazon.viyuktasiddhi;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton;
import com.amazon.viyuktasiddhi.model.CustomerSellerDataModel;

import java.io.IOException;

public class AuthenticationActivity extends AppCompatActivity {

    private static Context mContext;

    private EditText otp;

    private TextView otpText;

    private static TextView paymentStatusText, processingText;

    private static CircularProgressButton circularProgressButton;

    public static void completeTask(){
        circularProgressButton.doneLoadingAnimation(Color.parseColor("#333639"), BitmapFactory.decodeResource(getContext().getResources(), R.drawable.ic_done_white_48dp));
        circularProgressButton.stopAnimation();
        paymentStatusText.setVisibility(View.VISIBLE);
        processingText.setVisibility(View.INVISIBLE);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.activity_authentication);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Amazon Pay");
        setSupportActionBar(toolbar);

        circularProgressButton = findViewById(R.id.btnProcessing);

        otp = findViewById(R.id.otp);
        otpText = findViewById(R.id.otpText);
        processingText = findViewById(R.id.processingText);
        paymentStatusText = findViewById(R.id.paymentStatusText);
    }

    public static Context getContext(){
        return mContext;
    }


    public void authenticate(View view) {
        circularProgressButton.startAnimation();

        otp.setVisibility(View.INVISIBLE);
        otpText.setVisibility(View.INVISIBLE);
        processingText.setVisibility(View.VISIBLE);
        try {
            Toast.makeText(AuthenticationActivity.this, "Sending message", Toast.LENGTH_SHORT).show();
            sendMessage("9427635124", otp.getText().toString());

        } catch (Exception e) {
            Log.d("Sending", "Error while sending message", e);
        }
    }

    private void sendMessage(final String phone, final String message) {
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage("+91" + phone, null, message, null, null);
    }


}
