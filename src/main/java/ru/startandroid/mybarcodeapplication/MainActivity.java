package ru.startandroid.mybarcodeapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button scanBtn, report;
    private TextView formatTxt, contentTxt;
    String TAG = "myLogs";
    Map<String, String> map = new HashMap<>();
    ListView lvMain;
    Object[]names, codes;
    String room;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        room = getIntent().getStringExtra("room");

        scanBtn = (Button)findViewById(R.id.scan_button);
        report = (Button)findViewById(R.id.report);
        formatTxt = (TextView)findViewById(R.id.scan_format);
        contentTxt = (TextView)findViewById(R.id.scan_content);
        lvMain = (ListView) findViewById(R.id.lvMain);

        scanBtn.setOnClickListener(this);
        report.setOnClickListener(this);

        getList();
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.scan_button){
            IntentIntegrator scanIntegrator = new IntentIntegrator(this);
            scanIntegrator.initiateScan();
        }
        if (v.getId()==R.id.report){
            SparseBooleanArray sbArray = lvMain.getCheckedItemPositions();
            JSONArray ja = new JSONArray();
            ja.put(room);

            for (int i = 0; i < sbArray.size(); i++) {
                int key = sbArray.keyAt(i);
                if (sbArray.get(key)){
                    ja.put(codes[key]);
                }
            }

                String myURL = "http://192.168.1.40/excel.php";
                String params = "json=" + ja.toString();
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
                    data1 = params.getBytes();
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

                    Toast toast = Toast.makeText(getApplicationContext(),
                            sb.toString(), Toast.LENGTH_SHORT);
                    toast.show();


                    inputStream.close();
                    br.close();
                    conn.disconnect();

                }catch (Exception e) {

                }

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
       IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (scanningResult != null) {

            String scanContent = scanningResult.getContents();
            String scanFormat = scanningResult.getFormatName();

            formatTxt.setText("FORMAT: " + scanFormat);
            contentTxt.setText("CONTENT: " + scanContent);

            String nameItem = map.get(scanContent);
            if (nameItem != null) {
                int i = 0;

                while (!names[i].equals(nameItem)) {
                    i++;
                }
                ;

                lvMain.performItemClick(lvMain.getAdapter().getView(i, null, null), i, lvMain.getAdapter().getItemId(i));
            }else {
                Toast.makeText(getApplicationContext(), "Данная позиция отсутствует в базе", Toast.LENGTH_SHORT).show();
            }

        }else{
            Toast toast = Toast.makeText(getApplicationContext(),
                    "No scan data received!", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    void getList() {

        String myURL = "http://192.168.1.40/getList.php";
        String params = "content=" + room;
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

            while (i < ja.length()) {

                // разберем JSON массив построчно
                jo = ja.getJSONObject(i);
                map.put(jo.getString("code"), jo.getString("name"));
                i++;
            }

            names = map.values().toArray();
            codes = map.keySet().toArray();



            ArrayAdapter<Object> adapter = new ArrayAdapter<Object>(this,
                    android.R.layout.simple_list_item_multiple_choice, names);

            lvMain.setAdapter(adapter);

        } catch (Exception e) {
            Log.d("myLogs", "error");
            e.printStackTrace();
        }
    }
}
