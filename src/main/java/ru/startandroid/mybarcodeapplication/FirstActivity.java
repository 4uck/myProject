package ru.startandroid.mybarcodeapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

public class FirstActivity extends AppCompatActivity implements View.OnClickListener {

    String[] data = {"one", "two", "three", "four", "five"};
    Button btnSelect;
    Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);

        btnSelect = (Button)findViewById(R.id.btnSelect);
        btnSelect.setOnClickListener(this);

        // адаптер
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, data);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setAdapter(adapter);
        // заголовок
        spinner.setPrompt("Кабинеты");
        // выделяем элемент
        spinner.setSelection(2);
    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.btnSelect){
//            Toast.makeText(this, "Kabinet : " + spinner.getSelectedItem().toString(), Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("room" ,spinner.getSelectedItem().toString());
            startActivity(intent);
        }

    }
}
