package kr.ac.kumoh.ce.mobile.sportsgo;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ScrollView;
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

public class MyBoardreply extends AppCompatActivity {
    watchcommentDB cdb;
    commentpostDB comdb;
    search_EntrylistDB sedb;

    EditText commentEdt;
    String Edt, backinfo, stadiuminfo, checkjoin = null;
    String[] info;
    ScrollView sv;
    protected ArrayList<BoardreplyContents> rArray = new ArrayList<BoardreplyContents>();
    protected ListView mList;
    protected BoardContentsAdapter mAdapter;
    int jsonSize = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.myboardreply);
        View Footer = getLayoutInflater().inflate(R.layout.replyll, null, false);
        sv = (ScrollView) findViewById(R.id.replyscroll);

        Intent infointent = getIntent();
        info = infointent.getStringArrayExtra("info");
        backinfo = infointent.getStringExtra("backinfo");
        stadiuminfo = infointent.getStringExtra("stadiuminfo");
        TextView titletxt, contenttxt, timetxt, writertxt, people, play_timetxt;

        titletxt = (TextView) findViewById(R.id.titletxt);
        titletxt.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/BMJUA.ttf"));
        titletxt.setText(info[2]);

        writertxt = (TextView) findViewById(R.id.writertxt);
        writertxt.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/iloveyou.ttf"));
        writertxt.setText(" 작성자 : " + info[1]);

        contenttxt = (TextView) findViewById(R.id.contenttxt);
        contenttxt.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/newTown.ttf"));
        contenttxt.setText(info[3]);

        people = (TextView) findViewById(R.id.join_people);
        people.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/iloveyou.ttf"));
        people.setText("인원 : " + info[4] + " / " + info[5]);

        play_timetxt = (TextView) findViewById(R.id.playtime);
        play_timetxt.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/iloveyou.ttf"));
        play_timetxt.setText("경기일정 : " + info[6]);

        timetxt = (TextView) findViewById(R.id.timetxt);
        timetxt.setText(info[7]);

        commentEdt = (EditText) Footer.findViewById(R.id.commentEdt);

        rArray = new ArrayList<BoardreplyContents>();
        mList = (ListView) findViewById(R.id.listview2);
        mAdapter = new BoardContentsAdapter(this, R.layout.listitem2, rArray);

        mList.addFooterView(Footer);
        mList.setAdapter(mAdapter);
        mList.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                sv.requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });
        sedb = new search_EntrylistDB();
        sedb.execute();

        cdb = new watchcommentDB();
        cdb.execute();

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, Mypost.class);
        intent.putExtra("user_email", info[1]);
        startActivity(intent);
        super.onBackPressed();
    }

    public void onCommentEntry(View v) {

        Edt = commentEdt.getText().toString();
        comdb = new commentpostDB();
        comdb.execute();
    }

    public  void delete_post(View v){
        deletepostDB ddba = new deletepostDB();
        ddba.execute();
    }


    public class watchRefreshDB extends AsyncTask<String, Integer, String> {
        String param = "boardid=" + info[0] + "";
        String[] txt = new String[8];
        String[] reinfo;

        @Override
        protected String doInBackground(String... unused) {
            StringBuilder jsonHtml = new StringBuilder();

            try {
                URL url = new URL("http://shid1020.dothome.co.kr/boardrefresh.php");

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.connect();

                OutputStream outs = conn.getOutputStream();
                outs.write(param.getBytes("UTF-8"));
                outs.flush();
                outs.close();

                InputStream is = null;
                is = conn.getInputStream();

                String line = null;

                BufferedReader in = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                for (; ; ) {
                    line = in.readLine();
                    if (line == null) break;
                    jsonHtml.append(line + "\n");
                }
                in.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return jsonHtml.toString();
        }

        protected void onPostExecute(String str) {

            try {
                JSONObject root = new JSONObject(str);
                JSONArray ja = root.getJSONArray("results");

                for (int i = 0; i < 1; i++) {
                    JSONObject jo = ja.getJSONObject(i);

                    txt[0] = jo.getString("boardid");
                    txt[1] = jo.getString("user_email");
                    txt[2] = jo.getString("title");
                    txt[3] = jo.getString("contents");
                    txt[4] = jo.getString("playesr");
                    txt[5] = jo.getString("total_players");
                    txt[6] = jo.getString("calendar");
                    txt[7] = jo.getString("time");

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
            reinfo = new String[]{txt[0], txt[1], txt[2], txt[3], txt[4], txt[5], txt[6], txt[7]};
            Intent intent = new Intent(getApplicationContext(), Boardreply.class);
            intent.putExtra("info", reinfo);
            startActivity(intent);
            finish();

        }
    }

    public class watchcommentDB extends AsyncTask<String, Integer, String> {
        String param = "boardid=" + info[0] + "";

        @Override
        protected String doInBackground(String... unused) {
            StringBuilder jsonHtml = new StringBuilder();
            try {
                URL url = new URL("http://shid1020.dothome.co.kr/watchcomment.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.connect();

                OutputStream outs = conn.getOutputStream();
                outs.write(param.getBytes("UTF-8"));
                outs.flush();
                outs.close();

                InputStream is = null;
                is = conn.getInputStream();

                String line = null;

                BufferedReader in = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                for (; ; ) {
                    line = in.readLine();
                    if (line == null) break;
                    jsonHtml.append(line + "\n");
                }
                in.close();
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
                jsonSize = ja.length();
                txt = new String[5];
                for (int i = 0; i < ja.length(); i++) {
                    JSONObject jo = ja.getJSONObject(i);

                    txt[0] = jo.getString("commentid");
                    txt[1] = jo.getString("boardid");
                    txt[2] = jo.getString("user_email");
                    txt[3] = jo.getString("memo");
                    txt[4] = jo.getString("time");

                    rArray.add(new BoardreplyContents(txt[0], txt[1], txt[2], txt[3], txt[4]));
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
                convertView = MyBoardreply.this.getLayoutInflater().inflate(R.layout.listitem2, parent, false);
                holder = new BoardreplyContentsViewHolder();
                holder.txUser_email = (TextView) convertView.findViewById(R.id.replyid);
                holder.txMemo = (TextView) convertView.findViewById(R.id.replycontents);
                holder.txTime = (TextView) convertView.findViewById(R.id.replytime);
                convertView.setTag(holder);

            } else {
                holder = (BoardreplyContentsViewHolder) convertView.getTag();
            }
            holder.txUser_email.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/iloveyou.ttf"));
            holder.txUser_email.setText(getItem(position).getUser_email());
            holder.txMemo.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/newTown.ttf"));
            holder.txMemo.setText(getItem(position).getMemo());
            holder.txTime.setText(getItem(position).getTime());

            return convertView;
        }
    }

    public class commentpostDB extends AsyncTask<Void, Integer, Void> {
        String data = "";

        @Override
        protected Void doInBackground(Void... unused) {
            String param = "boardid=" + info[0] + "&user_email=" + SignIn.Email + "&memo=" + Edt;

            try {
                URL url = new URL("http://shid1020.dothome.co.kr/writecomment.php");
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

            if (data.equals("0")) {
                Toast.makeText(getApplicationContext(), "댓글 등록에 성공하였습니다.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "댓글 등록에 실패하였습니다.", Toast.LENGTH_SHORT).show();
            }
            watchRefreshDB refreshDB = new watchRefreshDB();
            refreshDB.execute();
        }
    }

    public class search_EntrylistDB extends AsyncTask<Void, Integer, Void> {
        String data = "";

        @Override
        protected Void doInBackground(Void... unused) {
            String param = "boardid=" + info[0] + "&user_email=" + SignIn.Email;

            try {
                URL url = new URL("http://shid1020.dothome.co.kr/searchentrylist.php");
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
                if (data.equals("1")) {
                    checkjoin = "참가취소";
                } else if (data.equals("0")) {
                    checkjoin = "참가";
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

    }

    public class deletepostDB extends AsyncTask<Void, Integer, Void> {
        String data = "";

        @Override
        protected Void doInBackground(Void... unused) {
            String param = "boardid=" + info[0]+"";

            try {
                URL url = new URL("http://shid1020.dothome.co.kr/deletepost.php");
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

            AlertDialog.Builder dialog = new AlertDialog.Builder(MyBoardreply.this);
            if (data.equals("0")) {
                dialog.setTitle("알림")
                        .setMessage("성공적으로 처리되었습니다!")
                        .setCancelable(true)
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        });
                dialog.show();
            } else if (data.equals("1")) {
                dialog
                        .setTitle("알림")
                        .setMessage("오류발생" + data)
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

    public void onReturn(View v) {
        finish();
    }


}
