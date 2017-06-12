package kr.ac.kumoh.ce.mobile.sportsgo;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
    EditText et_email, et_pw;
    String sEl, sPw, sUi;
    Button login;
    phpDown task;

    static public String Email = "";
    int Tu_length;
    int uid;
    int login_chk = 0;

    ArrayList<MainActivity.ListItem> listItem = new ArrayList<MainActivity.ListItem>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signin);

        et_email = (EditText) findViewById(R.id.email);
        et_pw = (EditText) findViewById(R.id.password);
        login = (Button) findViewById(R.id.bt_Login);

        Button btn_join = (Button) findViewById(R.id.btnSignUp);
        btn_join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplication(), SignUp.class);
                startActivity(intent);
                finish();
            }
        });

        task = new phpDown();
        task.execute("http://shid1020.dothome.co.kr/server.php");
    }

    public class phpDown extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... urls) {
            StringBuilder jsonHtml = new StringBuilder();
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                if (conn != null) {
                    conn.setConnectTimeout(10000);
                    conn.setUseCaches(false);
                    if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                        for (; ; ) {
                            String line = br.readLine();
                            if (line == null) break;
                            jsonHtml.append(line + "\n");
                        }
                        br.close();
                    }
                    conn.disconnect();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return jsonHtml.toString();

        }

        protected void onPostExecute(String str) {
            String[] txt;

            try {
                JSONObject root = new JSONObject(str);
                JSONArray ja = root.getJSONArray("results");
                Tu_length = ja.length();
                txt = new String[3];

                for (int i = 0; i < ja.length(); i++) {
                    JSONObject jo = ja.getJSONObject(i);
                    txt[0] = jo.getString("id");
                    txt[1] = jo.getString("user_email");
                    txt[2] = jo.getString("user_password");
                    listItem.add(new MainActivity.ListItem(txt[0], txt[1], txt[2]));
                    sUi = String.valueOf(uid);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public class loginDB extends AsyncTask<Void, Integer, Void> {
        String data = "";

        @Override
        protected Void doInBackground(Void... unused) {

            String param = "user_email=" + sEl + "&user_password=" + sPw + "";
            try {
                URL url = new URL(
                        "http://shid1020.dothome.co.kr/login.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.connect();

                OutputStream outs = conn.getOutputStream();
                outs.write(param.getBytes("UTF-8"));
                outs.flush();
                outs.close();

                InputStream is = null;
                BufferedReader in = null;

                is = conn.getInputStream();
                in = new BufferedReader(new InputStreamReader(is), 8 * 1024);
                String line = null;
                StringBuffer buff = new StringBuffer();
                while ((line = in.readLine()) != null) {
                    buff.append(line + "\n");
                }
                data = buff.toString().trim();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            if (data.equals("1")) {
                Intent intent = new Intent();
                intent.putExtra("result", "OK");
                Email = sEl;
                setResult(Activity.RESULT_OK, intent);
                finish();

            } else if (data.equals("0")) {
                Toast.makeText(getApplicationContext(), "비밀번호를 확인하세요", Toast.LENGTH_SHORT).show();
                login_chk = 0;
                sEl = "";
            }
        }

    }

    @Override
    public void onBackPressed() {
        this.setResult(Activity.RESULT_CANCELED);
        super.onBackPressed();
    }

    public void bt_Login(View v) {
        sEl = et_email.getText().toString();
        int i;
        int check = 0;
        for (i = 0; i < Tu_length; i++) {
            if (sEl.equals(listItem.get(i).getData(1))) {
                check++;
                break;
            }
        }
        if (check == 0) {
            Toast.makeText(this, "Name이 존재하지 않습니다.", Toast.LENGTH_SHORT).show();
            return;
        } else {
            try {
                sEl = et_email.getText().toString();
                sPw = et_pw.getText().toString();

            } catch (NullPointerException e) {
                Log.e("err", e.getMessage());
            }
            loginDB lDB = new loginDB();
            lDB.execute();

            if (login_chk == 1) {
                Intent intent = new Intent();
                intent.putExtra("result", "OK");
                Email = sEl;
                this.setResult(Activity.RESULT_OK, intent);
                this.finish();
            } else {

            }
        }
    }

}