package kr.ac.kumoh.ce.mobile.sportsgo;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
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
import java.util.List;

import static kr.ac.kumoh.ce.mobile.sportsgo.SignIn.Email;

public class Mypost extends AppCompatActivity {

    String [] txt;
    String info[];
    String myemail;
    LinearLayout layout;
    TextView textView;

    protected ArrayList<BoardContents> rArray = new ArrayList<BoardContents>();
    protected ListView mList;
    protected BoardContentsAdapter mAdapter;

    int jsonSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mypost);

        Intent intent = getIntent();
        myemail = intent.getStringExtra("user_email");

        rArray = new ArrayList<BoardContents>();
        mAdapter = new BoardContentsAdapter(this, R.layout.listitem1, rArray);
        mList = (ListView)findViewById(R.id.listview1);
        mList.setAdapter(mAdapter);
        mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(Mypost.this, MyBoardreply.class);
                info = new String[] {
                        mAdapter.getItem(i).getBoardid(), mAdapter.getItem(i).getUser_email(), mAdapter.getItem(i).getTitle(),
                        mAdapter.getItem(i).getContents(), mAdapter.getItem(i).getPlayers(), mAdapter.getItem(i).getTotal_players(),
                        mAdapter.getItem(i).getCalendar(), mAdapter.getItem(i).getTime()
                };
                intent.putExtra("info", info);
                startActivity(intent);
                finish();
            }
        });

        textView = (TextView)findViewById(R.id.testtxt);
        watchMypostDB wldb = new watchMypostDB();
        wldb.execute();

    }

    public class watchMypostDB extends AsyncTask<String, Integer, String> {
        String param = "my_email=" + Email + "";

        @Override
        protected String doInBackground(String... unused) {
            StringBuilder jsonHtml = new StringBuilder();

            try {
                URL url = new URL( "http://shid1020.dothome.co.kr/mypost.php" );

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
                jsonSize = ja.length();
                txt = new String[9];

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
                    txt[8] = jo.getString("stadium");

                    rArray.add(new BoardContents(txt[0], txt[1], txt[2] , txt[3], txt[4], txt[5], txt[6], txt[7], txt[8]));
                }
                mAdapter.notifyDataSetChanged();
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    public class BoardContents {
        String boardid;
        String user_email;
        String title;
        String contents;
        String players;
        String total_players;
        String calendar;
        String time;
        String stadium;

        public BoardContents(String boardid, String user_email, String title, String contents,
                             String players, String total_players, String calendar, String time, String stadium) {
            this.boardid = boardid;
            this.user_email = user_email;
            this.title = title;
            this.contents = contents;
            this.players = players;
            this.total_players = total_players;
            this.calendar = calendar;
            this.time = time;
            this.stadium = stadium;
        }

        public String getPlayers() {
            return players;
        }

        public String getTime() {
            return time;
        }

        public String getCalendar() {
            return calendar;
        }

        public String getTotal_players() {
            return total_players;
        }

        public String getContents() {
            return contents;
        }

        public String getBoardid() {
            return boardid;
        }

        public String getUser_email() {
            return user_email;
        }

        public String getTitle() {
            return title;
        }
    }
    static class BoardContentsViewHolder {
        TextView txUser_email;
        TextView txTitle;
        TextView txPlayers;
        TextView txCalendar;
    }

    public class BoardContentsAdapter extends ArrayAdapter<BoardContents> {
        public BoardContentsAdapter(Context context, int resource, List<BoardContents> objects) {
            super(context, resource, objects);
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            BoardContentsViewHolder holder;

            if (convertView == null) {
                convertView = Mypost.this.getLayoutInflater().inflate(R.layout.listitem1, parent, false);
                holder = new BoardContentsViewHolder();
                holder.txTitle = (TextView) convertView.findViewById(R.id.board_title);
                holder.txUser_email = (TextView) convertView.findViewById(R.id.board_username);
                holder.txPlayers = (TextView) convertView.findViewById(R.id.board_people);
                holder.txCalendar = (TextView) convertView.findViewById(R.id.board_playtime);
                convertView.setTag(holder);

            } else {
                holder = (BoardContentsViewHolder) convertView.getTag();
            }
            holder.txTitle.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/BMJUA.ttf"));
            holder.txTitle.setText(getItem(position).getTitle());
            holder.txUser_email.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/iloveyou.ttf"));
            holder.txUser_email.setText(getItem(position).getUser_email());
            holder.txPlayers.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/iloveyou.ttf"));
            holder.txPlayers.setText(getItem(position).getPlayers() + " / " + getItem(position).getTotal_players());
            holder.txCalendar.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/iloveyou.ttf"));
            holder.txCalendar.setText(getItem(position).getCalendar());
            return convertView;
        }
    }

}







