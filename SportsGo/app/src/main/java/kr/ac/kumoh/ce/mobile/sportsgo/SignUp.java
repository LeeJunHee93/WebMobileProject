package kr.ac.kumoh.ce.mobile.sportsgo;


import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
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
public class SignUp extends Activity {
    EditText et_email, et_pw, et_pw_chk;
    String sEl="", sPw, sPw_chk;

    int Email_check;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);

        et_email = (EditText) findViewById(R.id.etSignUpId);
        et_pw = (EditText) findViewById(R.id.etSignUpPW);
        et_pw_chk = (EditText) findViewById(R.id.etSignUpPPW);

        Email_check = 0;
    }
    public void bt_Join(View view) {
        sEl = et_email.getText().toString();
        sPw = et_pw.getText().toString();
        sPw_chk = et_pw_chk.getText().toString();

        if(sPw.equals(sPw_chk) && Email_check ==1)
        {
            registDB rdb = new registDB();
            rdb.execute();
            et_email.setText("");
            et_pw.setText("");
            et_pw_chk.setText("");
            Email_check = 0;
        }
        else if(!sPw.equals(sPw_chk))
        {
            Toast.makeText(this,"비밀번호를 다시 확인하세요!",Toast.LENGTH_SHORT).show();
            et_email.setText("");
            et_pw.setText("");
            et_pw_chk.setText("");
        }
        else if(Email_check==0){
            Toast.makeText(this,"Nmae 중복확인을 해주세요!",Toast.LENGTH_SHORT).show();
            et_email.setText("");
        }
    }

    public void check(View view)
    {
        sEl = et_email.getText().toString();
        checkEmailDB cedb = new checkEmailDB();
        cedb.execute();
    }

    public class registDB extends AsyncTask<Void, Integer, Void> {
        String data = "";
        @Override
        protected Void doInBackground(Void... unused) {

            String param = "user_email=" + sEl + "&user_password=" + sPw;
            try {
                URL url = new URL(
                        "http://shid1020.dothome.co.kr/join.php");
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
                while ( ( line = in.readLine() ) != null )
                {
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

            AlertDialog.Builder dialog = new AlertDialog.Builder(SignUp.this);
            if(data.equals("0"))
            {
                dialog
                        .setTitle("알림")
                        .setMessage("성공적으로 등록되었습니다!")
                        .setCancelable(true)
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(getApplicationContext(), SignIn.class);
                                startActivity(intent);

                                finish();
                            }
                        });
                dialog.show();
            }
            else
            {
               dialog
                        .setTitle("알림")
                        .setMessage("등록중 에러가 발생했습니다! errcode : "+ data)
                        .setCancelable(true)
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(getApplicationContext(), SignIn.class);
                                startActivity(intent);

                                finish();
                            }
                        });
                dialog.show();
            }

        }

    }


    public class checkEmailDB extends AsyncTask<Void, Integer, Void> {
        String data = "";
        @Override
        protected Void doInBackground(Void... unused) {

            String param = "user_email=" + sEl+"";
            try {
                URL url = new URL(
                        "http://shid1020.dothome.co.kr/checkemail.php");
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
                while ( ( line = in.readLine() ) != null )
                {
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

            AlertDialog.Builder dialog = new AlertDialog.Builder(SignUp.this);
            if(data.equals("1")) {
                dialog.setTitle("알림")
                        .setMessage("이미 존재하는 Name 입니다.")
                        .setCancelable(true)
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                dialog.show();
            }
            else if(data.equals("0")) {
                dialog
                        .setTitle("알림")
                        .setMessage("사용 가능한 Name입니다.")
                        .setCancelable(true)
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                dialog.show();
                Email_check = 1;
            }
        }

    }

}

