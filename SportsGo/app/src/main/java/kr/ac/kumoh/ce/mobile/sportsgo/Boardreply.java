package kr.ac.kumoh.ce.mobile.sportsgo;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
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
import java.util.List;

public class Boardreply extends AppCompatActivity {
    ArrayList<MainActivity.ListItem> listItem = new ArrayList<MainActivity.ListItem>();
    watchcommentDB cdb ;
    commentpostDB comdb;
    search_EntrylistDB sedb;

    TextView titletxt,contenttxt,timetxt,writertxt,detail;
    EditText commentEdt;
    String Edt;
    Intent infointent;
    String[] info;
    Button join;
    int jsonSize = 0;
    String checkjoin=null;

    protected ArrayList<BoardreplyContents> rArray = new ArrayList<BoardreplyContents>();
    protected ListView mList;
    protected BoardContentsAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.boardreply);

        join = (Button)findViewById(R.id.join);
        infointent = getIntent();
        info = infointent.getStringArrayExtra("info");

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

        rArray = new ArrayList<BoardreplyContents>();
        mAdapter = new BoardContentsAdapter(this, R.layout.listitem2, rArray);
        mList = (ListView)findViewById(R.id.listview2);
        mList.setAdapter(mAdapter);

        sedb = new search_EntrylistDB();
        sedb.execute();

        cdb = new watchcommentDB();
        cdb.execute();

    }

    //댓글 등록 버튼
    public void onCommentEntry(View v){

        Edt = commentEdt.getText().toString();
        comdb = new commentpostDB();
        comdb.execute();

        //새로고침
        Intent intent = new Intent(getApplicationContext(), Boardreply.class);
        intent.putExtra("info", info);
        startActivity(intent);
        finish();

    }
    //참가 버튼
    public void join(View v){
        addMemberDB adb = new addMemberDB();
        adb.execute();
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

                    rArray.add(new BoardreplyContents(txt[0], txt[1], txt[2] , txt[3], txt[4]));
                }
                mAdapter.notifyDataSetChanged();
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }
    public class BoardreplyContents {
        String commentid;
        String boardid;
        String user_email;
        String memo;
        String time;

        public BoardreplyContents(String commentid, String boardid, String user_email, String memo, String time) {
            this.commentid = commentid;
            this.boardid = boardid;
            this.user_email = user_email;
            this.memo = memo;
            this.time = time;
        }

        public String getTime() {
            return time;
        }

        public String getMemo() {

            return memo;
        }

        public String getUser_email() {

            return user_email;
        }

    }
    static class BoardreplyContentsViewHolder {
        TextView txUser_email;
        TextView txMemo;
        TextView txTime;
    }

    public class BoardContentsAdapter extends ArrayAdapter<BoardreplyContents> {
        public BoardContentsAdapter(Context context, int resource, List<BoardreplyContents> objects) {
            super(context, resource, objects);
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            BoardreplyContentsViewHolder holder;

            if (convertView == null) {
                convertView = Boardreply.this.getLayoutInflater().inflate(R.layout.listitem2, parent, false);
                holder = new BoardreplyContentsViewHolder();
                holder.txUser_email = (TextView) convertView.findViewById(R.id.replyid);
                holder.txMemo = (TextView) convertView.findViewById(R.id.replycontents);
                holder.txTime = (TextView) convertView.findViewById(R.id.replytime);
                convertView.setTag(holder);

            } else {
                holder = (BoardreplyContentsViewHolder) convertView.getTag();
            }
            holder.txUser_email.setText(getItem(position).getUser_email());
            holder.txMemo.setText(getItem(position).getMemo());
            holder.txTime.setText(getItem(position).getTime());

            return convertView;
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
            if(data.equals("0"))
            {
                Log.e("RESULT","성공적으로 처리되었습니다!");
                Toast.makeText(getApplicationContext(),"댓글 등록에 성공하였습니다.", Toast.LENGTH_SHORT).show();
            }
            else
            {
                Log.e("RESULT","글 등록에 실패하였습니다!"+data);
                Toast.makeText(getApplicationContext(),"댓글 등록에 실패하였습니다.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public class search_EntrylistDB extends AsyncTask<Void, Integer, Void> {
        String data = "";

        @Override
        protected Void doInBackground(Void... unused) {
    /* 인풋 파라메터값 생성 */
            String param = "boardid="+ info[0]+ "&user_email=" + SignIn.Email;

            try {
    /* 서버연결 */
                URL url = new URL("http://shid1020.dothome.co.kr/searchentrylist.php");
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
                if(data.equals("1")) {
                    Log.e("RESULT","참가 되어있습니다.");
                    checkjoin = "참가취소";
                }
                else if(data.equals("0")) {
                    Log.e("RESULT","참가되어 있지 않습니다.");
                    checkjoin = "참가";
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
            if(data.equals("1"))
            {
                Log.e("RESULT","참가 되어있습니다");
            }
            else
            {
                Log.e("RESULT","참가되어있지 않습니다!"+data);
            }
            join.setText(""+checkjoin);
        }
    }
    public class addMemberDB extends AsyncTask<Void, Integer, Void> {
        String data = "";

        @Override
        protected Void doInBackground(Void... unused) {
    /* 인풋 파라메터값 생성 */
            String param = "boardid="+ info[0]+ "&user_email=" + SignIn.Email + "&checkjoin=" + checkjoin;

            try {
    /* 서버연결 */
                URL url = new URL("http://shid1020.dothome.co.kr/addmember.php");
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
                    Log.e("RESULT","정상 처리되었습니다");
                }
                else if(data.equals("1")) {
                    Log.e("RESULT","오류발생."+data);
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
            if(data.equals("0"))
            {
                Log.e("RESULT","성공적으로 처리되었습니다!");
                dialog.setTitle("알림")
                        .setMessage("성공적으로 처리되었습니다!")
                        .setCancelable(true)
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //새로고침
                                Intent intent = new Intent(getApplicationContext(), Board.class);
                                // intent.putExtra("info", info);
                                startActivity(intent);
                                finish();
                            }
                        });
                dialog.show();
            }
            else if(data.equals("1"))
            {
                Log.e("RESULT","오류발생"+data);
                dialog
                        .setTitle("알림")
                        .setMessage("오류발생"+data)
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
