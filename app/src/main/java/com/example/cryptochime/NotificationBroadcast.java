package com.example.cryptochime;

import static android.content.Context.ALARM_SERVICE;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.cryptochime.SetAlertActivity.AlertDB.Alert;
import com.example.cryptochime.SetAlertActivity.AlertDB.MainDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class NotificationBroadcast extends BroadcastReceiver {

    MainDatabase db;
    List<Alert> alertDBList;


    static DecimalFormat df2 = new DecimalFormat("#.##");

    public static MediaPlayer mPlayer;


    String mySymbol;
    float userValue;

    int isNotified;
    int alertTypeCode, alertTypeCodeDB;
    boolean isLongAlarm;

    String notifyTitle, notifyText;

    ArrayList<CurrencyRVModel> rvModelArrayList2 = new ArrayList<>();

    SharedPreferences prefSymbol, prefUserValue, prefNotify, prefAlertTypeCode, prefisLongAlarm;
    PendingIntent alertIntent;


    @Override
    public void onReceive(Context context, Intent intent) {
        db = MainDatabase.getInstance(context.getApplicationContext());

        //Toast.makeText(context, "Broadcst Started", Toast.LENGTH_SHORT).show();


        prefSymbol = context.getSharedPreferences("mySymbol", Context.MODE_PRIVATE);
        prefUserValue = context.getSharedPreferences("userValue", Context.MODE_PRIVATE);
        prefNotify = context.getSharedPreferences("isNotified", Context.MODE_PRIVATE);
        prefAlertTypeCode = context.getSharedPreferences("alertTypeCode", Context.MODE_PRIVATE);
        prefisLongAlarm = context.getSharedPreferences("isLongAlarm", Context.MODE_PRIVATE);

        mPlayer = MediaPlayer.create(context, R.raw.alarm_sound);


        mySymbol = prefSymbol.getString("mySymbol", null);
        userValue = prefUserValue.getFloat("userValue", 0);
        isNotified = prefNotify.getInt("isNotified", 2);
        alertTypeCode = prefAlertTypeCode.getInt("alertTypeCode", 0);
        isLongAlarm = prefisLongAlarm.getBoolean("isLongAlarm", false);


        //if Not yet notified
        // Check for price change
        //if Market Price percentage changed in expected value
        //Show Notification

        //Log.i("is Notified", String.valueOf(isNotified));


//        if(isNotified == 0){
//        }
        getDataAndNotify(context);

    }


    //Show Notification
    private void showNotification(Context context, String titleNotify, String textNotify) {

        //On Notification Clcik
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 1, intent, PendingIntent.FLAG_ONE_SHOT);

        Notification notification = new NotificationCompat.Builder(context, NotifyApp.CHANNEL_1_ID)
                .setSmallIcon(R.drawable.ic_dollar)
                .setContentTitle(titleNotify)
                .setContentText(textNotify)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setContentIntent(pendingIntent)
                .build();


        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(1, notification);

        //Start another alarm
        MainActivity mainActivity = new MainActivity();
        mainActivity.startAlarm(context);
    }


    //Stop Long Alert when user tap on Notification
    private void stopAlert(Context context) {

        AlarmManager aManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        Intent sintent = new Intent(context, NotificationBroadcast.class);
        PendingIntent intent = PendingIntent.getBroadcast(context, 0, sintent, PendingIntent.FLAG_UPDATE_CURRENT);
        aManager.cancel(intent);
    }

    private void getDataAndNotify(Context context) {
        String url = "https://api.nomics.com/v1/currencies/ticker?key=ecae4f8ae82014deed75f16f14d03f2c21a819b1&per-page=100&page=1";

        RequestQueue requestQueue = Volley.newRequestQueue(context);

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @SuppressLint("CommitPrefEdits")
            @Override
            public void onResponse(JSONArray response) {


                try {
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject dataObj = response.getJSONObject(i);
                        String symbol = dataObj.getString("symbol");
                        String name = dataObj.getString("name");

                        double price = dataObj.getDouble("price");
                        float priceFloat = (float) price;


                        JSONObject oneDay = dataObj.getJSONObject("1d");
                        double pc24h = oneDay.getDouble("price_change_pct") * 100;

                        float pc24hf = (float) pc24h;

                        //Get Alert List from Database
                        MainDatabase db = MainDatabase.getInstance(context.getApplicationContext());
                        List<Alert> alertList = db.alertDao().getAllAlerts();

                        //Toast.makeText(context, "Alert DB size: " + alertList.size(), Toast.LENGTH_SHORT).show();

                        for (int j = 0; j < alertList.size(); j++) {
                            if (symbol.equals(alertList.get(j).currencySymbol)) {
                                //float userValueDB = Float.parseFloat(alertList.get(j).alertValue);
                                float userValueDB = alertList.get(j).alertValue;
                                alertTypeCodeDB = alertList.get(j).alertTypeCode;
                                boolean isNotified = alertList.get(j).isNotified;
                                boolean isLoudAlert = alertList.get(j).isLoudAlert;

                                if (!isNotified){
                                    //Check alerttypecode and Notify if condition match
                                    checkAlertCodeAndNotify(context, priceFloat, symbol, name, userValueDB, pc24hf, isLoudAlert);
                                    //notifyWithoutCheck(context, priceFloat, symbol, name, userValueDB);
                                }
                            }
                        }


                        rvModelArrayList2.add(new CurrencyRVModel(symbol, name, price, pc24h));
                    }

                } catch (JSONException e) {

                    e.printStackTrace();
                    Toast.makeText(context, "Fail to extract json data...", Toast.LENGTH_SHORT).show();
                }

            }
        }, error -> Toast.makeText(context, "Fail to get the data", Toast.LENGTH_SHORT).show());
        requestQueue.add(jsonArrayRequest);
    }

    private void checkAlertCodeAndNotify(Context context, float price, String symbol, String name, float userValueDB, float pc24hf, boolean isLoudAlert) {
        switch (alertTypeCodeDB) {
            case 0:
                if (price > userValueDB) {
                    notifyTitle = symbol + " Price rises above $" + userValueDB;
                    //notifyText = name + " Current price is : $" + df2.format(price);
                    notifyText = name + " Current price is : $" + MainActivity.dynDF(price).format(price);
                    showNotification(context, notifyTitle, notifyText);
                    //stopAlert(context);
                    //prefNotify.edit().putInt("isNotified", 1).apply();
                    if (isLoudAlert) {
                        mPlayer.start();
                    }

                    //Mark symbol as notified in DB
                    db.alertDao().updateField(true, symbol, userValueDB);
                }
                break;

            //Price Drops to
            case 1:
                if (price < userValueDB) {
                    notifyTitle = symbol + " Price drops to $" + userValueDB;
                    notifyText = name + " Current price is $: " + df2.format(price);
                    showNotification(context, notifyTitle, notifyText);
                    //stopAlert(context);
                    prefNotify.edit().putInt("isNotified", 1).apply();
                    if (isLoudAlert) {
                        mPlayer.start();
                    }
                    //Mark symbol as notified in DB
                    db.alertDao().updateField(true, symbol, userValueDB);
                }
                break;
            //24H Change is Over
            case 2:
                if (pc24hf > userValueDB) {
                    notifyTitle = symbol + " 24h Price change is over +" + df2.format(pc24hf) + "%";
                    notifyText = name + " Current price is : " + df2.format(price);
                    showNotification(context, notifyTitle, notifyText);
                    //stopAlert(context);
                    prefNotify.edit().putInt("isNotified", 1).apply();
                    if (isLoudAlert) {
                        mPlayer.start();
                    }
                    //Mark symbol as notified in DB
                    db.alertDao().updateField(true, symbol, userValueDB);
                }
                break;
            //24H Change is Down
            case 3:
                if (pc24hf < userValueDB) {
                    notifyTitle = symbol + " 24h Price change is down " + df2.format(pc24hf) + "%";
                    notifyText = name + " Current price is : $" + df2.format(price);
                    showNotification(context, notifyTitle, notifyText);
                    //stopAlert(context);
                    prefNotify.edit().putInt("isNotified", 1).apply();
                    if (isLoudAlert) {
                        mPlayer.start();
                    }
                    //Mark symbol as notified in DB
                    db.alertDao().updateField(true, symbol, userValueDB);
                }
                break;
            default:
                break;


        }
    }


}
