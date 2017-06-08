package kr.ac.kumoh.ce.mobile.sportsgo;


import android.app.Activity;
import android.content.DialogInterface;
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
public class SignUp extends Activity {
    EditText et_email, et_pw, et_pw_chk;
    String sEl, sPw, sPw_chk;
    Intent intent;

    int Email_check; //이메일 중복확인 '1': 확인됨  '0': 확인안됨
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);

        et_email = (EditText) findViewById(R.id.etSignUpId);
        et_pw = (EditText) findViewById(R.id.etSignUpPW);
        et_pw_chk = (EditText) findViewById(R.id.etSignUpPPW);

        Email_check = 0;
    }
    //회원가입 버튼
    public void bt_Join(View view) {
        /* 버튼을 눌렀을 때 동작하는 소스 */
        sEl = et_email.getText().toString();
        sPw = et_pw.getText().toString();
        sPw_chk = et_pw_chk.getText().toString();

        //비밀번호를 맞게 입력하였을 경우
        if(sPw.equals(sPw_chk) && Email_check ==1)
        {
            registDB rdb = new registDB();
            rdb.execute();
            et_email.setText("");
            et_pw.setText("");
            et_pw_chk.setText("");
            Email_check = 0;
        }
        //비밀번호를 다르게 입력했을 경우
        else if(!sPw.equals(sPw_chk))
        {
            Toast.makeText(this,"비밀번호를 다시 확인하세요!",Toast.LENGTH_SHORT).show();
            et_email.setText("");
            et_pw.setText("");
            et_pw_chk.setText("");
        }
        //이메일 중복확인을 안했을경우
        else if(Email_check==0){
            Toast.makeText(this,"이메일 중복확인을 해주세요!",Toast.LENGTH_SHORT).show();
            et_email.setText("");
        }
    }//button join end

    //중복확인 버튼
    public void check(View view)
    {
        sEl = et_email.getText().toString();
        checkEmailDB cedb = new checkEmailDB();
        cedb.execute();
    }

    /*-----------------------------회원가입 기능 -------------------------*/
    public class registDB extends AsyncTask<Void, Integer, Void> {
        String data = "";
        @Override
        protected Void doInBackground(Void... unused) {

    /* 인풋 파라메터값 생성 */
            String param = "user_email=" + sEl + "&user_password=" + sPw;
            try {
    /* 서버연결 */
                URL url = new URL(
                        "http://shid1020.dothome.co.kr/join.php");
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
            AlertDialog.Builder dialog = new AlertDialog.Builder(SignUp.this);
            if(data.equals("0"))
            {
                Log.e("RESULT","성공적으로 처리되었습니다!");


                dialog
                        .setTitle("알림")
                        .setMessage("성공적으로 등록되었습니다!")
                        .setCancelable(true)
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(intent);

                                finish();
                            }
                        });
                //AlertDialog dialog = alertBuilder.create();
                dialog.show();
            }
            else
            {
                Log.e("RESULT","에러 발생! ERRCODE = " + data);
                dialog
                        .setTitle("알림")
                        .setMessage("등록중 에러가 발생했습니다! errcode : "+ data)
                        .setCancelable(true)
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        });
                //AlertDialog dialog = alertBuilder.create();
                dialog.show();
            }

        }

    }//regDB end


    public class checkEmailDB extends AsyncTask<Void, Integer, Void> {
        String data = "";
        @Override
        protected Void doInBackground(Void... unused) {

    /* 인풋 파라메터값 생성 */
            String param = "user_email=" + sEl+"";
            try {
    /* 서버연결 */
                URL url = new URL(
                        "http://shid1020.dothome.co.kr/checkemail.php");
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
                if(data.equals("1")) {
                    Log.e("RESULT","이메일 중복됨");
                }
                else {
                    Log.e("RESULT","이메일 중복확인!");
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
            AlertDialog.Builder dialog = new AlertDialog.Builder(SignUp.this);
            if(data.equals("1")) {
                Log.e("RESULT","성공적으로 처리되었습니다!");
                dialog.setTitle("알림")
                        .setMessage("이미 존재하는 email 입니다.")
                        .setCancelable(true)
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                dialog.show();
            }
            else if(data.equals("0")) {
                Log.e("RESULT","이메일 중복확인");
                dialog
                        .setTitle("알림")
                        .setMessage("사용 가능한 email입니다.")
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

