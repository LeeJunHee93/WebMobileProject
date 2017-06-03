package kr.ac.kumoh.ce.mobile.sportsgo;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

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

public class Boardreply extends AppCompatActivity {
    ArrayList<MainActivity.ListItem> listItem = new ArrayList<MainActivity.ListItem>();
    watchcommentDB cdb ;
    commentpostDB comdb;

    TextView titletxt,contenttxt,timetxt,writertxt,detail;
    TextView[] commenttxt;
    TextView[] commentIdtxt;
    EditText commentEdt;
    String Edt;
    Intent infointent;
    String[] info;

    LinearLayout scrolllayout;

    int jsonSize = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.boardreply);

        infointent = getIntent();
        info=infointent.getStringArrayExtra("info");

        titletxt = (TextView)findViewById(R.id.titletxt);
        titletxt.setText(info[2]);

        writertxt = (TextView)findViewById(R.id.writertxt);
        writertxt.setText(" 작성자 :"+ info[1]);

        contenttxt = (TextView)findViewById(R.id.contenttxt);
        contenttxt.setText(info[3]);

        detail =(TextView)findViewById(R.id.detail);
        detail.setText("게시판아이디"+info[0]+", 현재인원:"+" "+info[4]+", 모집인원: "+info[5]+", 경기일정"+info[6]);

        timetxt = (TextView)findViewById(R.id.timetxt);
        timetxt.setText(info[7]);

        commentEdt = (EditText)findViewById(R.id.commentEdt);

        cdb = new watchcommentDB();
        cdb.execute();
    }

    public void onCommentEntry(View v){
        Edt = commentEdt.getText().toString();
        comdb = new commentpostDB();
        comdb.execute();
    }

    public class watchcommentDB extends AsyncTask<String, Integer, String> {
        String param = "boardid=" + info[0] + "";

        @Override
        protected String doInBackground(String... unused) {
            StringBuilder jsonHtml = new StringBuilder();
            try {
                // 연결 url 설정
                URL url = new URL("http://shid1020.dothome.co.kr/watchcomment.php");
                // 커넥션 객체 생성
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
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
                is = conn.getInputStream();

                String line = null;
                StringBuffer buff = new StringBuffer();

                BufferedReader in = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                for (; ; ) {
                    // 웹상에 보여지는 텍스트를 라인단위로 읽어 저장.
                    line = in.readLine();
                    if (line == null) break;
                    // 저장된 텍스트 라인을 jsonHtml에 붙여넣음
                    jsonHtml.append(line + "\n");
                }
                in.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return jsonHtml.toString();
        }

        protected void onPostExecute(String str) {
            //서버에서 json값 받아오기
            String [] txt;
            try {
                JSONObject root = new JSONObject(str);
                JSONArray ja = root.getJSONArray("results");
                jsonSize = ja.length();
                txt = new String[5];
                for (int i = 0; i < ja.length(); i++) {
                    JSONObject jo = ja.getJSONObject(i);

                    txt[0] = jo.getString("commentid");
                    txt[1] = jo.getString("boardid");
                    txt[2] = jo.getString("user_email");
                    txt[3] = jo.getString("memo");
                    txt[4] = jo.getString("time");

                    listItem.add(new MainActivity.ListItem(txt[0], txt[1], txt[2], txt[3], txt[4]));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            scrolllayout = (LinearLayout)findViewById(R.id.scrolllayout);
            commenttxt = new TextView[jsonSize];
            commentIdtxt = new TextView[jsonSize];

            for(int i=0;i<jsonSize;i++) {
                commenttxt[i] = new TextView(getApplicationContext());
                commentIdtxt[i] = new TextView(getApplicationContext());
                commentIdtxt[i].setText("작성자 : "+ listItem.get(i).getData(2)+"                                      "+listItem.get(i).getData(4));
                commenttxt[i].setText("내용 : " + listItem.get(i).getData(3));
                scrolllayout.addView(commentIdtxt[i]);
                scrolllayout.addView(commenttxt[i]);
            }
        }
    }

    public class commentpostDB extends AsyncTask<Void, Integer, Void> {
        String data = "";

        @Override
        protected Void doInBackground(Void... unused) {
    /* 인풋 파라메터값 생성 */
            String param = "boardid="+ info[0]+ "&user_email=" + SignIn.Email + "&memo=" + Edt;

            try {
    /* 서버연결 */
                URL url = new URL("http://shid1020.dothome.co.kr/writecomment.php");
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
                while ( ( line = in.readLine() ) != null ) {
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
            AlertDialog.Builder dialog = new AlertDialog.Builder(Boardreply.this);
            if(data.equals("0")) {
                Log.e("RESULT","성공적으로 처리되었습니다!");
                dialog
                        .setTitle("알림")
                        .setMessage("성공적으로 등록하였습니다!")
                        .setCancelable(true)
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                dialog.show();
            }
            else {
                Log.e("RESULT","글 등록에 실패하였습니다!"+data);
                dialog
                        .setTitle("알림")
                        .setMessage("글 등록에 실패하였습니다!")
                        .setCancelable(true)
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                dialog.show();
            }
        }
    }
    public void onReturn(View v){
        finish();
    }
}
