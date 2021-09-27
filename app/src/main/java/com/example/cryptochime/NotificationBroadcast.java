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
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class NotificationBroadcast extends BroadcastReceiver {


    static DecimalFormat df2 = new DecimalFormat("#.##");



    public static MediaPlayer mPlayer;

    String mySymbol;
    float userValue;

    int isNotified;
    int alertTypeCode;
    boolean isLongAlarm;

    String notifyTitle, notifyText;

    ArrayList<CurrencyRVModel> rvModelArrayList2 = new ArrayList<>();

    SharedPreferences prefSymbol, prefUserValue, prefNotify, prefAlertTypeCode, prefisLongAlarm;
    PendingIntent alertIntent;


    //MainActivity mainActivity = new MainActivity();
    @Override
    public void onReceive(Context context, Intent intent) {


         prefSymbol = context.getSharedPreferences("mySymbol", Context.MODE_PRIVATE);
        prefUserValue = context.getSharedPreferences("userValue", Context.MODE_PRIVATE);
         prefNotify = context.getSharedPreferences("isNotified", Context.MODE_PRIVATE);
        prefAlertTypeCode = context.getSharedPreferences("alertTypeCode", Context.MODE_PRIVATE);
        prefisLongAlarm = context.getSharedPreferences("isLongAlarm", Context.MODE_PRIVATE);

        mPlayer = MediaPlayer.create(context,R.raw.alarm_sound);



        mySymbol = prefSymbol.getString("mySymbol", null);
        userValue = prefUserValue.getFloat("userValue", 0);
        isNotified = prefNotify.getInt("isNotified", 2);
        alertTypeCode = prefAlertTypeCode.getInt("alertTypeCode", 0);
        isLongAlarm = prefisLongAlarm.getBoolean("isLongAlarm", false);


        //if Not yet notified
        // Check for price change
        //if Market Price percentage changed in expected value
        //Show Notification

        Log.i("is Notified", String.valueOf(isNotified));

        if(isNotified == 0){
            String url = "https://pro-api.coinmarketcap.com/v1/cryptocurrency/listings/latest";

            RequestQueue requestQueue = Volley.newRequestQueue(context);

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                @SuppressLint("CommitPrefEdits")
                @Override
                public void onResponse(JSONObject response) {


                    try {
                        JSONArray dataArray = response.getJSONArray("data");

                        for (int i=0; i<dataArray.length(); i++){
                            JSONObject dataObj = dataArray.getJSONObject(i);
                            String symbol = dataObj.getString("symbol");
                            String name = dataObj.getString("name");

                            JSONObject quote = dataObj.getJSONObject("quote");
                            JSONObject USD = quote.getJSONObject("USD");

                            double price = USD.getDouble("price");
                            double pc24h = USD.getDouble("percent_change_24h");

                            float pc24hf = (float)pc24h;

                            if (symbol.equals(mySymbol)){

                                switch (alertTypeCode){
                                    //Price Rises Above
                                    case 0:
                                        if (price > userValue){
                                            notifyTitle = symbol + " Price rises above $"+ userValue;
                                            notifyText = name + " Current price is : $"+df2.format(price);
                                            showNotification(context, notifyTitle, notifyText);
                                            //Toast.makeText(context, "AlertType Code is: "+alertTypeCode, Toast.LENGTH_SHORT).show();
                                            //AlertFragment.getInstance().stopAlert();
                                            stopAlert(context);
                                            prefNotify.edit().putInt("isNotified", 1).apply();
                                            if(isLongAlarm){
                                                mPlayer.start();
                                            }


                                        }
                                        break;
                                    //Price Drops to
                                    case 1:
                                        if(price<userValue){
                                            notifyTitle = symbol + " Price drops to $"+ userValue;
                                            notifyText = name + " Current price is $: "+df2.format(price);
                                            showNotification(context, notifyTitle, notifyText);
                                            //Toast.makeText(context, "AlertType Code is: "+alertTypeCode, Toast.LENGTH_SHORT).show();
                                           // AlertFragment.getInstance().stopAlert();
                                            stopAlert(context);
                                            prefNotify.edit().putInt("isNotified", 1).apply();
                                            if(isLongAlarm){
                                                mPlayer.start();
                                            }
                                        }
                                        break;
                                    //24H Change is Over
                                    case 2:
                                        if(pc24hf>userValue){
                                            notifyTitle = symbol + " 24h Price change is over +"+df2.format(pc24hf) +"%";
                                            notifyText = name + " Current price is : "+df2.format(price);
                                            showNotification(context, notifyTitle, notifyText);
                                            //Toast.makeText(context, "AlertType Code is: "+alertTypeCode, Toast.LENGTH_SHORT).show();
                                            //AlertFragment.getInstance().stopAlert();
                                            stopAlert(context);
                                            prefNotify.edit().putInt("isNotified", 1).apply();
                                            if(isLongAlarm){
                                                mPlayer.start();
                                            }
                                        }
                                        break;
                                    //24H Change is Down
                                    case 3:
                                        if(pc24hf<userValue){
                                            notifyTitle = symbol + " 24h Price change is down "+df2.format(pc24hf) +"%";
                                            notifyText = name + " Current price is : $"+df2.format(price);
                                            showNotification(context, notifyTitle, notifyText);
                                            //Toast.makeText(context, "AlertType Code is: "+alertTypeCode, Toast.LENGTH_SHORT).show();
                                            //AlertFragment.getInstance().stopAlert();
                                            stopAlert(context);
                                            prefNotify.edit().putInt("isNotified", 1).apply();
                                            if(isLongAlarm){
                                                mPlayer.start();
                                            }
                                        }
                                        break;
                                    default:
                                        break;
                                }

                            }

                            rvModelArrayList2.add(new CurrencyRVModel(symbol, name, price, pc24h));
                        }

                    } catch (JSONException e) {

                        e.printStackTrace();
                        Toast.makeText(context, "Fail to extract json data...", Toast.LENGTH_SHORT).show();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    Toast.makeText(context, "Fail to get the data", Toast.LENGTH_SHORT).show();

                }
            }){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {

                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("X-CMC_PRO_API_KEY", "cf22e625-7f13-4277-857c-6c60021e50dd");
                    return headers;
                }
            };
            requestQueue.add(jsonObjectRequest);
        }


    }


    //Show Notification
    private void showNotification(Context context, String titleNotify, String textNotify){

        //On Notification Clcik
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 1, intent, PendingIntent.FLAG_ONE_SHOT );

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
    }



    //Stop Long Alert
    private void stopAlert(Context context){

        AlarmManager aManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        Intent sintent = new Intent(context, NotificationBroadcast.class);
        PendingIntent intent = PendingIntent.getBroadcast(context, 0, sintent, PendingIntent.FLAG_UPDATE_CURRENT);
        aManager.cancel(intent);
        //Toast.makeText(context, "Alert Stopped", Toast.LENGTH_LONG).show();

    }


}
