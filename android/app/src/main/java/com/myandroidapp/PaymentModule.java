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

import java.util.UUID;

public class PaymentModule extends ReactContextBaseJavaModule {

    private static final int PAYMENT_REQUEST_CODE = 1001;
    private Promise paymentPromise;

    public PaymentModule(ReactApplicationContext reactContext) {
        super(reactContext);

        // Rejestrujemy listener, który będzie odbierał wyniki
        reactContext.addActivityEventListener(activityEventListener);
    }

    @NonNull
    @Override
    public String getName() {
        return "PaymentModule";
    }

    @ReactMethod
    public void makePayment(String json, Promise promise) {
        Activity activity = getCurrentActivity();

        if (activity == null) {
            promise.reject("NO_ACTIVITY", "Activity is null");
            return;
        }

        Intent intent = new Intent("com.worldline.payment.action.PROCESS_TRANSACTION");
        intent.putExtra("WPI_SERVICE_TYPE", "WPI_SVC_PAYMENT");
        intent.putExtra("WPI_REQUEST", json);
        intent.putExtra("WPI_VERSION", "2.2ko");
        intent.putExtra("WPI_SESSION_ID", UUID.randomUUID().toString());

        this.paymentPromise = promise;

        try {
            activity.startActivityForResult(intent, PAYMENT_REQUEST_CODE);
        } catch (Exception e) {
            this.paymentPromise = null;
            promise.reject("INTENT_ERROR", e.getMessage(), e);
        }
    }

    // Listener do odbierania wyników
    private final ActivityEventListener activityEventListener = new BaseActivityEventListener() {
        @Override
        public void onActivityResult(Activity activity, int requestCode, int resultCode, @Nullable Intent data) {
            if (requestCode == PAYMENT_REQUEST_CODE && paymentPromise != null) {
                if (resultCode == Activity.RESULT_OK && data != null) {
                    String response = data.getStringExtra("WPI_RESPONSE");
                    paymentPromise.resolve(response);
                } else {
                    paymentPromise.reject("PAYMENT_FAILED", "Transaction was cancelled or failed");
                }

                paymentPromise = null;
            }
        }
    };
}