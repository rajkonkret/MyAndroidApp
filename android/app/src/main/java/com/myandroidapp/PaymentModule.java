package com.myandroidapp;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.facebook.react.bridge.ActivityEventListener;
import com.facebook.react.bridge.BaseActivityEventListener;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Collections;
import java.util.UUID;

public class PaymentModule extends ReactContextBaseJavaModule {

    private static final int PAYMENT_REQUEST_CODE = 1001;
    private Promise paymentPromise;

    public PaymentModule(ReactApplicationContext reactContext) {
        super(reactContext);
        reactContext.addActivityEventListener(activityEventListener);
    }

    @NonNull
    @Override
    public String getName() {
        return "PaymentModule";
    }

    @ReactMethod
    public void makePayment(ReadableMap paymentData, Promise promise) {
        Activity activity = getCurrentActivity();

        if (activity == null) {
            promise.reject("NO_ACTIVITY", "Activity is null");
            return;
        }

        try {
            int amount = paymentData.getInt("amount");
            String number = paymentData.getString("number");
            int operatorId = paymentData.getInt("operatorId");

            // Budujemy dane transakcji jako JSON
            JSONObject jsonRequest = new JSONObject();
            jsonRequest.put("requestedAmount", amount);
            jsonRequest.put("currency", "PLN");
            jsonRequest.put("reference", "OP_" + operatorId + "_" + number + "_" + amount);
            
            JSONArray receiptFormat = new JSONArray();
            receiptFormat.put("JSON");
            jsonRequest.put("receiptFormat", receiptFormat);

            // Intent do zewnętrznej aplikacji płatniczej
            Intent intent = new Intent("com.worldline.payment.action.PROCESS_TRANSACTION");
            intent.putExtra("WPI_SERVICE_TYPE", "WPI_SVC_PAYMENT");
            intent.putExtra("WPI_VERSION", "2.2");
            intent.putExtra("WPI_SESSION_ID", UUID.randomUUID().toString());
            intent.putExtra("WPI_REQUEST", jsonRequest.toString());
            Log.d(TAG, "Intent to start payment: " + jsonRequest.toString());
            this.paymentPromise = promise;

            activity.startActivityForResult(intent, PAYMENT_REQUEST_CODE);

        } catch (Exception e) {
            Log.e(TAG, "Failed to start payment intent", e);
            
            this.paymentPromise = null;
            promise.reject("INTENT_ERROR", e.getMessage(), e);
        }
    }

    private final ActivityEventListener activityEventListener = new BaseActivityEventListener() {
        @Override
        public void onActivityResult(Activity activity, int requestCode, int resultCode, @Nullable Intent data) {
            if (requestCode == PAYMENT_REQUEST_CODE && paymentPromise != null) {
                if (resultCode == Activity.RESULT_OK && data != null) {
                    String response = data.getStringExtra("WPI_RESPONSE");
                    Log.d(TAG, "Payment result OK: " + response);
                    paymentPromise.resolve(response);
                } else {
                    Log.e(TAG, "Payment failed or cancelled");
                    paymentPromise.reject("PAYMENT_FAILED", "Transaction was cancelled or failed");
                }

                paymentPromise = null;
            }
        }
    };
}