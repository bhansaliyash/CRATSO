package com.example.yash.cratso;

import java.io.BufferedReader;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.app.Activity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class MainActivity extends Activity {


    EditText start,end;
    Button bt;
    int pointer,offset;
    RecyclerView rv;
    URL url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        bt=(Button) findViewById(R.id.button);
        start=(EditText) findViewById(R.id.editText);
        end=(EditText) findViewById(R.id.editText2);

        rv= (RecyclerView) findViewById(R.id.rv);

        LinearLayoutManager llm = new LinearLayoutManager(this.getApplicationContext());
        rv.setLayoutManager(llm);

        bt.performClick();

        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                pointer=Integer.parseInt(start.getText().toString());
                offset=Integer.parseInt(end.getText().toString());
                pointer--;
                offset-=pointer;


                String s="https://us-central1-cratso-171712.cloudfunctions.net/cratso_internship/leaderboard?pointer="+pointer+"&offset="+offset;
                new RetrieveFeedTask(s).execute();


            }
        });




    }


    class RetrieveFeedTask extends AsyncTask<Void, Void, String> {

        String link;

        RetrieveFeedTask(String s){
            link=s;
        }

        ArrayList<Person> list=new ArrayList<>();


        protected String doInBackground(Void... urls) {

            // Do some validation here

            try {


                URL url = new URL(link);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                if(urlConnection.equals(null))
                    return "NULL";

                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                    bufferedReader.close();
                    return stringBuilder.toString();
                }
                finally{
                    urlConnection.disconnect();
                }
            }
            catch(Exception e) {
                Log.e("ERROR", e.getMessage(), e);
                return null;
            }
        }

        protected void onPostExecute(String response) {
            if(response == null) {
                response = "THERE WAS AN ERROR";
            }


            try {
                JSONObject jObj = new JSONObject(response);
                JSONArray data=jObj.getJSONArray("data");

                for(int i=0;i<offset;i++){
                    JSONObject obj=data.getJSONObject(i);

                    list.add(new Person(obj.getString("name"),obj.getString("user_id"),obj.getString("profile_pic"),pointer+1+i));
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }


            RVAdapter adapter = new RVAdapter(list);
            rv.setAdapter(adapter);


        }

    }






}