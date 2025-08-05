package com.example.currencyconverter;

import android.app.Dialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;

public class ccActivity extends AppCompatActivity {

    TextView convertFromDropdownTextView, convertToDropdownTextView, conversionRateText;
    EditText amountToConvert;
    ArrayList<String> arrayList;
    Dialog fromDialog;
    Dialog toDialog;
    Button convertButton;
    String convertFromValue, convertToValue, conversionValue;
    String[] country = {"AFN", "EUR","ALL","DZD","USD","AOA","XCD","ARS","AMD","AWG","AUD","AZN","BSD","BHD","BDT","BBD","BYN","BZD","XOF","BMD","INR","BTN","BOB","BOV","BAM","BWP","NOK","BRL","BND","BGN"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_cc);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        String user = getIntent().getStringExtra("USER");
        Toast.makeText(ccActivity.this, "Welome, " + user+"!", Toast.LENGTH_SHORT).show();

            convertFromDropdownTextView = findViewById(R.id.convert_from_dropdown_menu);
            convertToDropdownTextView = findViewById(R.id.convert_to_dropdown_menu);
            convertButton = findViewById(R.id.conversionButton);
            conversionRateText = findViewById(R.id.conversionRateText);
            amountToConvert = findViewById(R.id.amountToConvertValueEditText);

            //add countries to arraylist
            arrayList = new ArrayList<>();
            for (String i : country){
                arrayList.add(i);
            }

            //when From clicked , it will show a dialog
            convertFromDropdownTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    fromDialog = new Dialog(ccActivity.this);
                    fromDialog.setContentView(R.layout.from_spinner);
                    fromDialog.getWindow().setLayout(650,800);
                    fromDialog.show();

                    EditText editText = fromDialog.findViewById(R.id.edit_text);
                    ListView listView = fromDialog.findViewById(R.id.list_view);

                    //put items in the arraylist in dialog
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(ccActivity.this, android.R.layout.simple_list_item_1, arrayList);
                    listView.setAdapter(adapter);

                    editText.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {


                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            adapter.getFilter().filter(s);
                        }

                        @Override
                        public void afterTextChanged(Editable s) {

                        }
                    });

                    //gets the selected item from spinner and put it in From
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            convertFromDropdownTextView.setText(adapter.getItem(position));
                            fromDialog.dismiss();
                            convertFromValue = adapter.getItem(position);
                        }
                    });
                }
            });

        //put items in the arraylist in dialog
        convertToDropdownTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    toDialog = new Dialog(ccActivity.this);
                    toDialog.setContentView(R.layout.to_spinner);
                    toDialog.getWindow().setLayout(650,800);
                    toDialog.show();

                    EditText editText = toDialog.findViewById(R.id.edit_text);
                    ListView listview = toDialog.findViewById(R.id.list_view);

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(ccActivity.this, android.R.layout.simple_list_item_1, arrayList);
                    listview.setAdapter(adapter);

                    editText.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            adapter.getFilter().filter(s);
                        }

                        @Override
                        public void afterTextChanged(Editable s) {

                        }
                    });

                    //gets the selected item from spinner and put it in To
                    listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            convertToDropdownTextView.setText(adapter.getItem(position));
                            toDialog.dismiss();
                            convertToValue = adapter.getItem(position);
                        }
                    });
                }
            });

        //when button clicked it will get the text and call getConversionRate
            convertButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Double amountToConvert = Double.valueOf(ccActivity.this.amountToConvert.getText().toString());
                        getConversionRate(convertFromValue, convertToValue, amountToConvert);

                    }
                    catch (Exception e){

                    }
                }
            });
    }
    //connects api to app
    public String getConversionRate(String convertFrom, String convertTo, Double amountToConvert){
        RequestQueue queue = Volley.newRequestQueue(this);
        //2
        String apiKey = "bc5a39d84b484aa3adcfac2d6fcc705c";
        String url = "https://api.currencyfreaks.com/latest?apikey=" + apiKey + "&symbols=" + convertTo;
        //String url = "https://free.currconv.com/api/v7/convert?q="+convertFrom+"_"+convertTo+"&compact=ultra&apiKey=22e91ab924eb2aa6f9a4";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            //make the response by math
            @Override
            public void onResponse(String response) {
                JSONObject jsonObject=null;
                try {
                    jsonObject = new JSONObject(response);
                    JSONObject rates = jsonObject.getJSONObject("rates");
                    Double conversionRateValue = rates.getDouble(convertTo);
                    conversionValue = "" + round((conversionRateValue * amountToConvert), 2);
                    conversionRateText.setText(conversionValue);
                    //4
                    /*jsonObject = new JSONObject(response);
                    Double conversionRateValue = round(((Double) jsonObject.get(convertFrom+"_"+convertTo)),2);
                    conversionValue = "" + round((conversionRateValue*amountToConvert),2);
                    conversionRateText.setText(conversionValue);*/

                    //make the error to sth understandable
                } catch (JSONException e) {
                    e.printStackTrace();
                    //throw new RuntimeException(e);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //1
                error.printStackTrace();
            }
        });
        queue.add(stringRequest);
        return null;
    }

    public static double round(double value, int places){
        if (places<0) throw new IllegalArgumentException();
        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}