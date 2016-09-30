package com.example.zeynep.havadurumu;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.menu.ListMenuItemView;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    EditText ara;
    TextView baslik, bugunlukhavatahmini;
    ListView havalistele;
    ListView list;
    ArrayList<String> havalist = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ara = (EditText) findViewById(R.id.il);
        baslik = (TextView) findViewById(R.id.baslik);
        bugunlukhavatahmini = (TextView) findViewById(R.id.bugunlukhavatahmini);
        list = (ListView) findViewById(R.id.listView);
        String url = "https://query.yahooapis.com/v1/public/yql?q=select%20item%20from%20weather.forecast%20where%20woeid%20in%20(select%20woeid%20from%20geo.places%20where%20text%3D%27istanbul%27)%20and%20u%3D%27c%27&format=json&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys";
        new havaDurumu(url, this).execute();
    }

    public void ilegorehavadurumuara(View v) {
        String Ilara = ara.getText().toString().trim();
        String url = "https://query.yahooapis.com/v1/public/yql?q=select%20item%20from%20weather.forecast%20where%20woeid%20in%20(select%20woeid%20from%20geo.places%20where%20text%3D%27" + Ilara + "%27)%20and%20u%3D%27c%27&format=json&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys";
        new havaDurumu(url, this).execute();

    }


    class havaDurumu extends AsyncTask<Void, Void, Void> {
        private ProgressDialog pro;
        String data = "";
        String url = "";

        public havaDurumu(String url, Activity ac) {
            this.url = url;
            pro = new ProgressDialog(ac);
            pro.setMessage("Lütfen Bekleyiniz ...");
            pro.show();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... param) {
            try {
                data = Jsoup.connect(url).ignoreContentType(true).execute().body();
            } catch (Exception ex) {
                Log.d("Json hatası : ", ex.toString());
            } finally {
                pro.dismiss();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void o) {
            super.onPostExecute(o);
            try {
                JSONObject obj = new JSONObject(data);
                JSONObject query = obj.getJSONObject("query");
                JSONObject results = query.getJSONObject("results");
                JSONArray channel = results.getJSONArray("channel");
                JSONObject item = channel.getJSONObject(0).getJSONObject("item");
                String title = item.getString("title");
                JSONObject condition = item.getJSONObject("condition");
                String date = condition.getString("date");
                String temp = condition.getString("temp");
                String text = condition.getString("text");
                baslik.setText(title);
                String havatahmini = date + "-- " + temp + "-- " + text;
                bugunlukhavatahmini.setText(havatahmini);
                ara.setText(null);
                JSONArray forecast = item.getJSONArray("forecast");
                for (int j = 0; j < forecast.length(); j++) {
                    String gun = forecast.getJSONObject(j).getString("date");
                    String high = forecast.getJSONObject(j).getString("high");
                    String low = forecast.getJSONObject(j).getString("low");
                    String aciklama = forecast.getJSONObject(j).getString("text");
                    String havatahmin = gun + " ," + "Açıklama:  " + aciklama + "\n" + "En Yüksek Sıcaklık:  " + high + "\n" + "En Düşük Sıcaklık:  " + low + "\n";
                    havalist.add(havatahmin);
                    Log.d("Açıklama:", havatahmin);

                }
                ArrayAdapter<String> adp = new ArrayAdapter<>(MainActivity.this, R.layout.havalistrow, R.id.havalistele, havalist);
                list.setAdapter(adp);
            } catch (JSONException e) {
                Log.d("HATA:", e.toString());

            }

        }
    }

}
