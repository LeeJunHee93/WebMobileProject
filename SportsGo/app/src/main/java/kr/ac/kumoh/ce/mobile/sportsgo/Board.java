package kr.ac.kumoh.ce.mobile.sportsgo;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class Board extends AppCompatActivity {

    Button[] btn;
    ArrayList<MainActivity.ListItem> listItem = new ArrayList<MainActivity.ListItem>();
    LinearLayout layout;
    String[] info; //게시판 정보
    TextView textView;
    watchDB wdb;

    int jsonSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.board);
        textView = (TextView)findViewById(R.id.testtxt);
        wdb = new watchDB();
        wdb.execute();
    }

/*
    public void onClick(View v) {
        Intent intent = new Intent(getApplicationContext(), writeTest.class);
        startActivity(intent);

        finish();
    }
    */

    public void onQuit(View v) {
        finish();
    }


    public void onNew(View v) {
        Intent intent = new Intent(this, Boardreply.class);
        startActivity(intent);

        finish();
    }

    public class watchDB extends AsyncTask<String, Integer, String> {
        String data = "";
        String param = "id=" + SignIn.Id + "";

        @Override
        protected String doInBackground(String... unused) {
            StringBuilder jsonHtml = new StringBuilder();
            try {
                // 연결 url 설정
                URL url = new URL( "http://shid1020.dothome.co.kr/board.php" );
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
                txt = new String[8];

                for (int i = 0; i < jsonSize; i++) {
                    JSONObject jo = ja.getJSONObject(i);

                    txt[0]= jo.getString("boardid");
                    txt[1] = jo.getString("user_email");
                    txt[2] = jo.getString("title");
                    txt[3] = jo.getString("contents");
                    txt[4] = jo.getString("playesr");
                    txt[5] = jo.getString("total_players");
                    txt[6] = jo.getString("calendar");
                    txt[7] = jo.getString("time");

                    listItem.add(new MainActivity.ListItem(txt));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            Button.OnClickListener onClickListener = new Button.OnClickListener() {
                @Override
                public void onClick(View v) {
                    info = new String[] {
                            listItem.get(v.getId()).getData(0), listItem.get(v.getId()).getData(1), listItem.get(v.getId()).getData(2),
                            listItem.get(v.getId()).getData(3), listItem.get(v.getId()).getData(4), listItem.get(v.getId()).getData(5),
                            listItem.get(v.getId()).getData(6), listItem.get(v.getId()).getData(7)
                    };
                    Intent intent = new Intent(getApplicationContext(), Boardreply.class);
                    intent.putExtra("info", info);
                    startActivity(intent);
                }
            };

            btn = new Button[jsonSize];
            layout = (LinearLayout) findViewById(R.id.layout);

            for (int i = 0; i < jsonSize; i++) {
                btn[i] = new Button(getApplicationContext());
                btn[i].setId(i);
                btn[i].setOnClickListener(onClickListener);

                btn[i].setText(listItem.get(i).getData(2) +"\n"+  "작성자 : " + listItem.get(i).getData(1)+ "     인원 : " +
                        listItem.get(i).getData(4)+"/"+listItem.get(i).getData(5)+"     경기시간 : "+listItem.get(i).getData(6));
                layout.addView(btn[i]);
            }
        }
    }
}
