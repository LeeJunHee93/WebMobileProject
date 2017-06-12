package kr.ac.kumoh.ce.mobile.sportsgo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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

/**
 * Created by dlgus on 2017-04-29.
 */
public class MyPage extends Fragment {
    View rootView;
    TextView myname;
    TextView textView;
    String[] txt;
    String info[];
    Button btn_signin;
    LinearLayout layout;

    static final int REQUEST_LOGIN_1 = 1;
    int flag = 0;
    protected ArrayList<BoardContents> rArray = new ArrayList<BoardContents>();
    protected ListView mList;
    protected BoardContentsAdapter mAdapter;

    int jsonSize;

    @Override
    public void onResume() {
        myname.setText(Email);
        rArray = new ArrayList<BoardContents>();
        mAdapter = new BoardContentsAdapter(getActivity(), R.layout.listitem1, rArray);
        mList = (ListView) rootView.findViewById(R.id.listview1);
        mList.setAdapter(mAdapter);
        mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getActivity(), Boardreply.class);
                info = new String[]{
                        mAdapter.getItem(i).getBoardid(), mAdapter.getItem(i).getUser_email(), mAdapter.getItem(i).getTitle(),
                        mAdapter.getItem(i).getContents(), mAdapter.getItem(i).getPlayers(), mAdapter.getItem(i).getTotal_players(),
                        mAdapter.getItem(i).getCalendar(), mAdapter.getItem(i).getTime()
                };
                intent.putExtra("info", info);
                intent.putExtra("backinfo", "1");
                startActivity(intent);
            }
        });
        if (flag == 1) {
            watchmyListDB wldb = new watchmyListDB();
            wldb.execute();
        } else
            flag = 1;

        super.onResume();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.mypage_main, container, false);
        myname = (TextView) rootView.findViewById(R.id.myname);

        btn_signin = (Button) rootView.findViewById(R.id.mypage_signin);
        btn_signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btn_signin.getText() == "SIGN OUT") {
                    Email = "";
                    myname.setText(Email);
                    btn_signin.setText("SIGN IN");
                    rArray = new ArrayList<BoardContents>();
                    mAdapter = new BoardContentsAdapter(getActivity(), R.layout.listitem1, rArray);
                    mList = (ListView) rootView.findViewById(R.id.listview1);
                    mList.setAdapter(mAdapter);
                    mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            Intent intent = new Intent(getActivity(), Boardreply.class);
                            info = new String[]{
                                    mAdapter.getItem(i).getBoardid(), mAdapter.getItem(i).getUser_email(), mAdapter.getItem(i).getTitle(),
                                    mAdapter.getItem(i).getContents(), mAdapter.getItem(i).getPlayers(), mAdapter.getItem(i).getTotal_players(),
                                    mAdapter.getItem(i).getCalendar(), mAdapter.getItem(i).getTime()
                            };
                            intent.putExtra("info", info);
                            intent.putExtra("backinfo", "1");
                            startActivity(intent);
                        }
                    });
                    Toast.makeText(getContext(), "SIGN OUT!", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(getActivity(), SignIn.class);
                    startActivityForResult(intent, REQUEST_LOGIN_1);
                }
            }
        });

        Button btn_myboard = (Button) rootView.findViewById(R.id.mypage_myboard);
        btn_myboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), Mypost.class);
                startActivity(intent);
            }
        });

        rArray = new ArrayList<BoardContents>();
        mAdapter = new BoardContentsAdapter(getActivity(), R.layout.listitem1, rArray);
        mList = (ListView) rootView.findViewById(R.id.listview1);
        mList.setAdapter(mAdapter);
        mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getActivity(), Boardreply.class);
                info = new String[]{
                        mAdapter.getItem(i).getBoardid(), mAdapter.getItem(i).getUser_email(), mAdapter.getItem(i).getTitle(),
                        mAdapter.getItem(i).getContents(), mAdapter.getItem(i).getPlayers(), mAdapter.getItem(i).getTotal_players(),
                        mAdapter.getItem(i).getCalendar(), mAdapter.getItem(i).getTime()
                };
                intent.putExtra("info", info);
                intent.putExtra("backinfo", "1");
                startActivity(intent);
            }
        });

        myname.setText(Email);
        textView = (TextView) rootView.findViewById(R.id.testtxt);

        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_LOGIN_1) {
                btn_signin.setText("SIGN OUT");
            }
        }
    }

    public class watchmyListDB extends AsyncTask<String, Integer, String> {
        String param = "my_email=" + Email + "";

        @Override
        protected String doInBackground(String... unused) {
            StringBuilder jsonHtml = new StringBuilder();

            try {
                URL url = new URL("http://shid1020.dothome.co.kr/mylist.php");

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
                StringBuffer buff = new StringBuffer();

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
                txt = new String[8];

                for (int i = 0; i < jsonSize; i++) {
                    JSONObject jo = ja.getJSONObject(i);

                    txt[0] = jo.getString("boardid");
                    txt[1] = jo.getString("user_email");
                    txt[2] = jo.getString("title");
                    txt[3] = jo.getString("contents");
                    txt[4] = jo.getString("playesr");
                    txt[5] = jo.getString("total_players");
                    txt[6] = jo.getString("calendar");
                    txt[7] = jo.getString("time");

                    rArray.add(new BoardContents(txt[0], txt[1], txt[2], txt[3], txt[4], txt[5], txt[6], txt[7]));
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

        public BoardContents(String boardid, String user_email, String title, String contents, String players, String total_players, String calendar, String time) {
            this.boardid = boardid;
            this.user_email = user_email;
            this.title = title;
            this.contents = contents;
            this.players = players;
            this.total_players = total_players;
            this.calendar = calendar;
            this.time = time;
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
                convertView = getActivity().getLayoutInflater().inflate(R.layout.listitem1, parent, false);
                holder = new BoardContentsViewHolder();
                holder.txTitle = (TextView) convertView.findViewById(R.id.board_title);
                holder.txUser_email = (TextView) convertView.findViewById(R.id.board_username);
                holder.txPlayers = (TextView) convertView.findViewById(R.id.board_people);
                holder.txCalendar = (TextView) convertView.findViewById(R.id.board_playtime);
                convertView.setTag(holder);

            } else {
                holder = (BoardContentsViewHolder) convertView.getTag();
            }
            holder.txTitle.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "fonts/BMJUA.ttf"));
            holder.txTitle.setText("제목 : " + getItem(position).getTitle());
            holder.txUser_email.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "fonts/iloveyou.ttf"));
            holder.txUser_email.setText("작성자 : " + getItem(position).getUser_email());
            holder.txPlayers.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "fonts/iloveyou.ttf"));
            holder.txPlayers.setText(getItem(position).getPlayers() + " / " + getItem(position).getTotal_players());
            holder.txCalendar.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "fonts/iloveyou.ttf"));
            holder.txCalendar.setText("경기시간 : " + getItem(position).getCalendar());

            return convertView;
        }
    }


}
