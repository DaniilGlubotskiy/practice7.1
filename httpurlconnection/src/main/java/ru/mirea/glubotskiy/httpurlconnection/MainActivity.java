package ru.mirea.glubotskiy.httpurlconnection;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    TextView textView;
    TextView textViewCountry;
    TextView textViewCity;
    TextView textViewTimeZone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = findViewById(R.id.textViewIp);
        textViewCountry = findViewById(R.id.textViewCountry);
        textViewCity = findViewById(R.id.textViewCity);
        textViewTimeZone = findViewById(R.id.textViewTimeZone);
    }

    public void onClick(View view) {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = null;
        if (connectivityManager != null){
            networkInfo = connectivityManager.getActiveNetworkInfo();
        }

        if (networkInfo != null && networkInfo.isConnected()){
            String url = "http://ip.jsontest.com/";
            new DownloadPageTask().execute("http://ip-api.com/json/188.123.231.202");
        } else {
            Toast.makeText(this,"Нет интернета",Toast.LENGTH_SHORT).show();
        }
    }

    private class DownloadPageTask extends AsyncTask<String,Void,String> {
        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            textView.setText("Загружаем...");
        }
        @Override
        protected String doInBackground(String... urls){
            try {
                return downloadIpInfo(urls[0]);
            }catch (IOException e) {
                e.printStackTrace();
                return "error";
            }
        }
        @Override
        protected void onPostExecute(String result){
            Log.d(MainActivity.class.getSimpleName(),result);
            try {
                JSONObject responseJson = new JSONObject(result);
                String ip = responseJson.getString("query");
                String country = responseJson.getString("country");
                String city = responseJson.getString("city");
                String timeZone = responseJson.getString("timezone");
                Log.d(MainActivity.class.getSimpleName(),ip);
                Log.d(MainActivity.class.getSimpleName(),country);
                Log.d(MainActivity.class.getSimpleName(),city);
                Log.d(MainActivity.class.getSimpleName(),timeZone);
                textView.setText("IP: " + ip);
                textViewCountry.setText("County: " + country);
                textViewCity.setText("City: " + city);
                textViewTimeZone.setText("TimeZone: " + timeZone);
            } catch (JSONException e){
                e.printStackTrace();
            }
            super.onPostExecute(result);
        }
    }
    private String downloadIpInfo(String address) throws IOException{
        InputStream inputStream = null;
        String data = "";
        try {
            URL url = new URL(address);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(100000);
            connection.setConnectTimeout(100000);
            connection.setRequestMethod("GET");
            connection.setInstanceFollowRedirects(true);
            connection.setUseCaches(false);
            connection.setDoInput(true);
            int responceCode = connection.getResponseCode();

            if (responceCode == HttpURLConnection.HTTP_OK){
                inputStream = connection.getInputStream();
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                int read = 0;
                while ((read = inputStream.read()) != -1){
                    bos.write(read);
                }
                byte[] result = bos.toByteArray();
                bos.close();
                data = new String(result);
            }else {
                data = connection.getResponseMessage() + ". Error Code : " + responceCode;
            }
            connection.disconnect();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e){
            e.printStackTrace();
        } finally {
            if (inputStream != null){
                inputStream.close();
            }
        }
        return data;
    }
}