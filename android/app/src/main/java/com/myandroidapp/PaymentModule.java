public package pcom.myandroidapp;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Promise;

import java.util.Collections;
import com.google.gson.Gson;

public class PaymentModule extends ReactContextBaseJavaModule {
    private static final String TAG = "PaymentModule";

    public PaymentModule(@NonNull ReactApplicationContext reactContext) {
        super(reactContext);
    }

    @NonNull
    @Override
    public String getName() {
        return "PaymentModule";
    }

    @ReactMethod
    public void makePayment(int operatorId, int amount, String phoneNumber, Promise promise) {
        try {
            WpiSaleTransactionRequest request = new WpiSaleTransactionRequest();
            request.setRequestedAmount(amount);
            request.setCurrency("PLN");
            request.setReference(operatorId + "_" + phoneNumber + "_" + amount);
            request.setReceiptFormat(Collections.singletonList("JSON"));

            String json = new Gson().toJson(request);
            Intent intent = new Intent("com.worldline.payment.action.PROCESS_TRANSACTION");
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            intent.putExtra("WPI_SERVICE_TYPE", "WPI_SVC_PAYMENT");
            intent.putExtra("WPI_REQUEST", json);
            intent.putExtra("WPI_VERSION", "2.2");
            intent.putExtra("WPI_SESSION_ID", UUID.randomUUID().toString()) // ← tu wstaw getSessionId()

            Activity activity = getCurrentActivity();
            if (activity != null) {
                activity.startActivity(intent);
                promise.resolve("OK");
            } else {
                promise.reject("NO_ACTIVITY", "Activity is null");
            }
        } catch (Exception e) {
            Log.e(TAG, "makePayment error", e);
            promise.reject("ERROR", e);
        }
    }
} {
    
}
