package kr.ac.kumoh.ce.mobile.sportsgo;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by dlgus on 2017-05-16.
 */
public class WriteBoard extends Activity {
    String memo,title,total,cal;
    EditText writetitleedt;
    EditText writecontentedt;
    EditText writetotlaedt;
    EditText writecaledt;

    PostDB pdb = new PostDB();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.writeboard);

        writetitleedt = (EditText)findViewById(R.id.writetitleeedt);
        writecontentedt = (EditText)findViewById(R.id.writecontentedt);
        writetotlaedt = (EditText)findViewById(R.id.writetotlaedt);
        writecaledt = (EditText)findViewById(R.id.writecaledt);
    }

    public void onCancel(View v){
        Intent intent = new Intent(this,Board.class);
        startActivity(intent);

        finish();
    }

    public void onEntry(View v){
        Intent intent = new Intent(this, Board.class);

        title = writetitleedt.getText().toString();
        memo =writecontentedt.getText().toString();
        total = writetotlaedt.getText().toString();
        cal = writecaledt.getText().toString();
        pdb.execute();

        startActivity(intent);

        finish();
    }

    public class PostDB extends AsyncTask<Void, Integer, Void> {
        String data = "";
        @Override
        protected Void doInBackground(Void... unused) {

    /* 인풋 파라메터값 생성 */
            String param = "user_email=" + SignIn.Email + "&title=" + title + "&memo=" +memo +
                    "&total="+ total + "&cal="+ cal + "";
            try {
    /* 서버연결 */
                URL url = new URL(
                        "http://shid1020.dothome.co.kr/writeboard.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.connect();

    /* 안드로이드 -> 서버 파라메터값 전달 */
                OutputStream outs = conn.getOutputStream();
                outs.write(param.getBytes("UTF-8"));
                outs.flush();
                outs.close();

    /* 서버 -> 안드로이드 파라메터값 전달 */
                InputStream is = null;
                BufferedReader in = null;


                is = conn.getInputStream();
                in = new BufferedReader(new InputStreamReader(is), 8 * 1024);
                String line = null;
                StringBuffer buff = new StringBuffer();
                while ( ( line = in.readLine() ) != null )
                {
                    buff.append(line + "\n");
                }
                data = buff.toString().trim();
                Log.e("RECV DATA",data);

                //서버에서 응답확인
                if(data.equals("0"))
                {
                    Log.e("RESULT","성공적으로 처리되었습니다");
                }
                else{
                    Log.e("RESULT","에러 발생!"+data);
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

             /* 서버에서 응답 */
            Log.e("RECV DATA",data);
            AlertDialog.Builder dialog = new AlertDialog.Builder(WriteBoard.this);

            if(data.equals("0"))
                Toast.makeText(getApplicationContext(), "성공성공 글성공", Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(getApplicationContext(), "실패실패 글실패", Toast.LENGTH_SHORT).show();
        }

    }

}
