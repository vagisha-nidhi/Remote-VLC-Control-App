package com.example.vagisha.remotevlccontrol;

import android.os.AsyncTask;
import android.provider.SyncStateContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

public class MainActivity extends AppCompatActivity  {

    public Socket socket = null;
    Button previousButton;
    Button playPauseButton;
    Button nextButton;
    TextView mousePad;
    float x,y,distx,disty;
    PrintWriter out;
    boolean mousemoved = false;
    private boolean isConnected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        previousButton = (Button) findViewById(R.id.button_previous);
        playPauseButton = (Button) findViewById(R.id.button_play_pause);
        nextButton = (Button) findViewById(R.id.button_next);
        mousePad = (TextView) findViewById(R.id.mousePad);

        playPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isConnected && out!=null) {
                    out.println(Constants.PLAY_PAUSE);
                    Toast.makeText(MainActivity.this,"Play pause",Toast.LENGTH_SHORT).show();
                    Log.i("play","play pause");
                }


            }
        });
        previousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isConnected && out!=null)
                    out.println(Constants.PREVIOUS);
            }
        });
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isConnected && out!=null)
                    out.println(Constants.NEXT);
            }
        });

        mousePad.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
               if(isConnected && out!=null) {
                   switch (event.getAction()) {

                       case MotionEvent.ACTION_DOWN: x = event.getX();
                                                     y = event.getY();
                           mousemoved = false;
                           break;

                       case MotionEvent.ACTION_MOVE:
                                                     distx = event.getX() - x;
                           disty = event.getY() -y;
                           x = event.getX();
                           y = event.getY();
                           if(distx!=0 || disty!=0)
                           { out.println(distx + "," + disty);
                             mousemoved = true;
                           }
                           break;

                       case MotionEvent.ACTION_UP:
                           if(!mousemoved)
                               out.println(Constants.LEFT_CLICK);
                           break;

                   }
               }

                return false;
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.mymenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.action_connect)
        {
            if(socket == null) {
                SocketConnectAsyncTask socketConnectAsyncTask = new SocketConnectAsyncTask();
                socketConnectAsyncTask.execute(Constants.IPADDR);
            }


        }

        return true;
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        if(isConnected && out!=null) {
            try {
                out.println("exit"); //tell server to exit
                socket.close(); //close socket
            } catch (IOException e) {
                Log.e("remotedroid", "Error in closing socket", e);
            }
        }
    }


    public class SocketConnectAsyncTask extends AsyncTask<String,Void,Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {
            boolean result = true;
            try {
                String ip = params[0];
                socket = new Socket(ip,8999);//Open socket on server IP and port
            } catch (IOException e) {
                Log.e("remotedroid", "Error while connecting", e);
                result = false;
            }
            return result;
        }

        @Override
        protected void onPostExecute(Boolean result)
        {
            isConnected = result;
            Toast.makeText(MainActivity.this,isConnected?"Connected to server!":"Error while connecting",Toast.LENGTH_LONG).show();
            try {
                if(isConnected) {
                    out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket
                            .getOutputStream())), true); //create output stream to send data to server
                }
            }catch (IOException e){
                Log.e("remotedroid", "Error while creating OutWriter", e);
                Toast.makeText(MainActivity.this,"Error while connecting",Toast.LENGTH_LONG).show();
            }
        }
    }


}
