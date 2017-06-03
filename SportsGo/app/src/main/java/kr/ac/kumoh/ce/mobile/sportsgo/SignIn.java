package kr.ac.kumoh.ce.mobile.sportsgo;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by dlgus on 2017-05-16.
 */

public class SignIn extends Activity {
    static public String Id, Email;
    EditText et_email, et_pw, et_pw_chk,et_pm;
    String sEl, sPw, sPw_chk,sPm,sUi;
    TextView txtView;
    phpDown task;
    int Tu_length;
    int uid;
    //  int Email_check; // '1': 확인됨  '0': 확인안됨
    Button login; //로그인버튼
    int login_chk; //로그인 되었는지 확인

    ArrayList<MainActivity.ListItem> listItem= new ArrayList<MainActivity.ListItem>();
    MainActivity.ListItem Item;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signin);

        et_email = (EditText) findViewById(R.id.email);
        et_pw = (EditText) findViewById(R.id.password);
        login = (Button)findViewById(R.id.bt_Login);

        login_chk = 0;

        //서버의 user테이블에서 user들의 정보를 가져옴
        task = new phpDown();
        task.execute("http://shid1020.dothome.co.kr/server.php");
    }


    public class phpDown extends AsyncTask<String, Integer,String> {
        String data = "";
        @Override
        protected String doInBackground(String... urls) {
            StringBuilder jsonHtml = new StringBuilder();
            try{
                // 연결 url 설정
                URL url = new URL(urls[0]);
                // 커넥션 객체 생성
                HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                // 연결되었으면.
                if(conn != null){
                    conn.setConnectTimeout(10000);
                    conn.setUseCaches(false);
                    // 연결되었음 코드가 리턴되면.
                    if(conn.getResponseCode() == HttpURLConnection.HTTP_OK){
                        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                        for(;;){
                            // 웹상에 보여지는 텍스트를 라인단위로 읽어 저장.
                            String line = br.readLine();
                            if(line == null) break;
                            // 저장된 텍스트 라인을 jsonHtml에 붙여넣음
                            jsonHtml.append(line + "\n");
                        }
                        br.close();
                    }
                    conn.disconnect();
                }
            } catch(Exception ex){
                ex.printStackTrace();
            }
            return jsonHtml.toString();

        }
        protected void onPostExecute(String str){
            String [] txt;

            try{
                JSONObject root = new JSONObject(str);
                JSONArray ja = root.getJSONArray("results");
                Tu_length = ja.length();
                txt = new String[3];

                for(int i=0; i<ja.length(); i++){
                    JSONObject jo = ja.getJSONObject(i);
                    txt[0] = jo.getString("id");
                    txt[1] = jo.getString("user_email");
                    txt[2] = jo.getString("user_password");
                    listItem.add(new MainActivity.ListItem(txt[0],txt[1],txt[2]));
                    sUi = String.valueOf(uid);
                }
            }catch(JSONException e){
                e.printStackTrace();
            }
        }
    }

    public class loginDB extends AsyncTask<Void, Integer, Void> {
        String data = "";
        @Override
        protected Void doInBackground(Void... unused) {

    /* 인풋 파라메터값 생성 */
            String param = "user_email=" + sEl + "&user_password=" + sPw + "";
            try {
    /* 서버연결 */
                URL url = new URL(
                        "http://shid1020.dothome.co.kr/login.php");
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
                if(data.equals("0")) {
                    Log.e("RESULT","성공적으로 처리되었습니다");
                }
                else {
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
            AlertDialog.Builder dialog = new AlertDialog.Builder(SignIn.this);
            if(data.equals("1"))
            {
                Log.e("RESULT","성공적으로 처리되었습니다!");
                login_chk = 1;
                login.setText("로그아웃");

                dialog.setTitle("알림")
                        .setMessage("성공적으로 로그인 하였습니다!")
                        .setCancelable(true)
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                dialog.show();
            }
            else if(data.equals("0"))
            {
                Log.e("RESULT","비밀번호를 다시 확인하세요");
                dialog
                        .setTitle("알림")
                        .setMessage("비밀번호를 다시 확인하십시오")
                        .setCancelable(true)
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                dialog.show();
            }
        }

    }//LoginDB

    //로그인 버튼
    public void bt_Login(View v)
    {
        if(login_chk==0){
            sEl = et_email.getText().toString();
            int i;
            int check =0;
            for(i = 0;i<Tu_length;i++){
                if(sEl.equals(listItem.get(i).getData(0) ) ){
                    check++;
                    break;
                }
            }

            if(check==0){
                Toast.makeText(this,"email이 존재하지 않습니다.",Toast.LENGTH_SHORT).show();
                return;
            }
            else {
                try {
                    sEl = et_email.getText().toString();
                    //서버에서 비밀번호 확인
                    sPw = et_pw.getText().toString();

                } catch (NullPointerException e) {
                    Log.e("err", e.getMessage());
                }
                //check = 0;
                loginDB lDB = new loginDB();
                lDB.execute();
                Id = listItem.get(i).getData(0);
                Email = listItem.get(i).getData(1);
            }
        }
        else if(login_chk==1){
            login_chk = 0;
            login.setText("로그인");
            Toast.makeText(this,"로그아웃 하였습니다.",Toast.LENGTH_SHORT).show();
        }

    }
}