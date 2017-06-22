package ru.startandroid.mybarcodeapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class FirstActivity extends AppCompatActivity implements View.OnClickListener {

    String[] listName, listId;
    Button btnSelect;
    Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);

        btnSelect = (Button)findViewById(R.id.btnSelect);
        btnSelect.setOnClickListener(this);

        getRoom();

        // адаптер
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, listName);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setAdapter(adapter);
        // заголовок
        spinner.setPrompt("Кабинеты");
        // выделяем элемент
//        spinner.setSelection(2);
    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.btnSelect){
//            Toast.makeText(this, "Kabinet : " + spinner.getSelectedItem().toString(), Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("room" ,listId[spinner.getSelectedItemPosition()]);
            startActivity(intent);
        }

    }

    void getRoom(){

        String myURL = "http://192.168.1.40/getRoom.php";
        String params = "content=" + "my desk";
        byte[] data1 = null;
        InputStream is = null;

        try {
            URL url = new URL(myURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();


            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setConnectTimeout(8000);

            conn.setRequestProperty("Content-Length", "" + Integer.toString(params.getBytes().length));
            OutputStream os = conn.getOutputStream();
            data1 = params.getBytes("UTF-8");
            os.write(data1);
            data1 = null;


            conn.connect();


            InputStream inputStream = conn.getInputStream();
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(inputStream, "UTF-8"));
            StringBuilder sb = new StringBuilder();

            String bfr_st = null;
            while ((bfr_st = br.readLine()) != null) {
                sb.append(bfr_st);
            }

            String ansver = sb.toString();
            ansver = ansver.substring(0, ansver.indexOf("]") + 1);


            inputStream.close();
            br.close();
            conn.disconnect();

            JSONArray ja = new JSONArray(ansver);
            JSONObject jo;

            Integer i = 0;

            StringBuilder sb2 = new StringBuilder();
            listName = new String[ja.length()];
            listId = new String[ja.length()];

            while (i < ja.length()) {

                // разберем JSON массив построчно
                jo = ja.getJSONObject(i);
                listName[i] = jo.getString("name");
                listId[i] = jo.getString("id");
                i++;
            }

        }catch (Exception e){
        }
    }
}
