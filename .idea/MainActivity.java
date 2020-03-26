package com.example.p3150107.myapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.maps.model.LatLng;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    EditText et1;
    EditText et2;
    EditText lon_btn;
    EditText lat_btn;
    EditText rang;
    EditText category;
    Button button1;
    String sleepTime;
    double longitude;
    double latitude;
    double range;
    String cat;
    int id;
    int rec;
    Socket socket;
    ObjectOutputStream out;
    ObjectInputStream in;
    int a ;
    int b;
    int c;
    private ArrayList<String> names = new ArrayList<String>();
    private ArrayList<String> longs = new ArrayList<String>();
    private ArrayList<String> lands = new ArrayList<String>();
    private ArrayList<String>  catg = new ArrayList<String>();
    private ArrayList<String>  lands1 = new ArrayList<String>();
    ArrayList<LatLng> loc ;
    boolean flag=true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        et1 = (EditText) findViewById(R.id.et1);
        et2 = (EditText) findViewById(R.id.et2);
        button1 = (Button) findViewById(R.id.button1);






        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                id = Integer.parseInt(et1.getText().toString());
                rec = Integer.parseInt(et2.getText().toString());
                latitude=Double.parseDouble(lat_btn.getText().toString());
                longitude=Double.parseDouble(lon_btn.getText().toString());
                range=Double.parseDouble(rang.getText().toString());
                cat=category.getText().toString();

                AsyncTaskRunner runner = new AsyncTaskRunner();
                sleepTime = "10";
                runner.execute(sleepTime);
                while(flag==true){ }
                Intent intent = new Intent(MainActivity.this,MapsActivity.class);
                finish();
                 a = names.size();
                 b=lands.size();
                 c=longs.size();
                 intent.putExtra("my_long", longitude);
                 intent.putExtra("my_lat",latitude);
                intent.putStringArrayListExtra("name",names);
                intent.putStringArrayListExtra("long",longs);
                intent.putStringArrayListExtra("lang",lands);
                intent.putStringArrayListExtra("category", catg);
                startActivity(intent);



            }
        });
    }
    private class AsyncTaskRunner extends AsyncTask<String, String, Integer> {

        private String resp;
        ProgressDialog progressDialog;

        @Override
        protected Integer doInBackground(String... params) {
            ObjectOutputStream out=null;
            ObjectInputStream in=null;
            Socket s =null;
            int size=rec;

            try {
                s = new Socket("192.168.1.19",4202);
                out = new ObjectOutputStream(s.getOutputStream());
                in =new ObjectInputStream(s.getInputStream());
                System.out.print("Client Connected");




                out.writeUTF(cat);
                out.flush();
                out.writeDouble(longitude);
                out.flush();
                out.writeDouble(latitude);
                out.flush();
                out.writeDouble(range);
                out.flush();


                size = in.readInt();
                for ( int j =0; j<size; j++) {
                    String name = in.readUTF();
                    names.add(name);
                     a= names.size();
                    String longitude = in.readUTF();
                    longs.add(longitude);
                     b=longs.size();
                    String latidude = in.readUTF();
                    lands.add(latidude);
                     c= lands.size();
                     String category = in.readUTF();
                     catg.add(category);

                    //loc.add(new LatLng(latidude,longitude));


                }


            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                try {
                    out.close();
                    in.close();
                    s.close();

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }


            publishProgress("Sleeping..."); // Calls onProgressUpdate()
            int i = Integer.parseInt(params[0].trim());
            for (int j = 1; j <= i; j++) {
                Log.e("DEBUGMESSAGES", Integer.toString(j));

                publishProgress(String.valueOf(j));
                sleep(1000);
            }

            //resp = "The total waiting time was: " + i;
            flag=false;
            return size;



        }

        private void sleep(int i) {
            try {
                Thread.sleep(i);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }


        @Override
        protected void onPostExecute(Integer z ) {

            progressDialog.dismiss();



        }


        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(MainActivity.this,
                    "ProgressDialog",
                    "Wait for " + sleepTime + " seconds");
        }


        @Override
        protected void onProgressUpdate(String... text) {
            progressDialog.setMessage("Time counter: " + text[0]);
        }








}

}

