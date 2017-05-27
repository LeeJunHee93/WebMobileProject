package kr.ac.kumoh.ce.mobile.sportsgo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Cache;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dlgus on 2017-05-02.
 */
public class Board extends Activity implements AdapterView.OnItemClickListener {

//    protected ArrayList<boardinfo> rArray = new ArrayList<boardinfo>();
    public static final String BOARDTAG ="BoardTag";
    protected JSONObject mResult=null;
    protected ListView mList;
//    protected boardAdapter mAdapter;
    protected RequestQueue mQueue;
    protected ImageLoader mImageLoader=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.board);

    //    mAdapter = new boardAdapter(this, R.layout.listitem1, rArray);
        mList = (ListView)findViewById(R.id.listview1);
     //   mList.setAdapter(mAdapter);
        mList.setOnItemClickListener(this);

    }


    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
        Intent intent = new Intent(this, BoardMessage.class);
        intent.putExtra("num", Integer.toString(pos));
        startActivity(intent);
    }


    /**
     * Created by dlgus on 2017-05-02.
     */
    public static class BoardMessage extends Activity{

        protected ArrayList<boardmessageinfo> rArray = new ArrayList<boardmessageinfo>();
        public static final String BOARDMESSAGETAG = "BoardMessageTag";
        protected JSONObject mResult = null;
        protected ListView mList;
        protected boardmessageAdapter mAdapter;
        protected RequestQueue mQueue;
        protected ImageLoader mImageLoader=null;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.boardmessage);

            mAdapter = new boardmessageAdapter(this, R.layout.listitem2, rArray);
            mList = (ListView)findViewById(R.id.listview2);
            mList.setAdapter(mAdapter);

            Cache cache = new DiskBasedCache(getCacheDir(),1024*1024);
            BasicNetwork network = new BasicNetwork(new HurlStack());
            mQueue = new RequestQueue(cache, network);
            mQueue.start();
            requestMessage();
        }

        protected void requestMessage(){
            String url="http://172.20.10.3/selectmessage.php";

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, new JSONObject(),
                    new Response.Listener<JSONObject>(){
                        @Override
                        public void onResponse(JSONObject response) {
                            mResult = response;
                            drawList();
                        }
                    },
                    new Response.ErrorListener(){
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            //               Toast.makeText(this, "서버에러",Toast.LENGTH_LONG).show();
                        }
                    }
            );
            jsonObjectRequest.setTag(BOARDMESSAGETAG);
            mQueue.add(jsonObjectRequest);
        }
        public void drawList(){
            rArray.clear();
            try{
                TextView txName;
                TextView txContent;
                JSONArray jsonMainNode = mResult.getJSONArray("list");
                JSONObject jsonChildNode = jsonMainNode.getJSONObject(1);
                String name = jsonChildNode.getString("name");
                Log.i("name", name);
                String content = jsonChildNode.getString("content");
                Log.i("content", content);
                txName = (TextView)findViewById(R.id.name);
                txContent =(TextView)findViewById(R.id.content);
                txName.setText(name);
                txContent.setText(content);

                String rname = jsonChildNode.getString("rname");
                Log.i("rname", rname);
                String rcontent = jsonChildNode.getString("rcontent");
                Log.i("rcontent", rcontent);
                rArray.add(new boardmessageinfo(rname, rcontent));

                for(int i=1;i<jsonMainNode.length();i++) {
                    jsonChildNode = jsonMainNode.getJSONObject(i);
                    rname = jsonChildNode.getString("rname");
                    Log.i("rname", rname);
                    rcontent = jsonChildNode.getString("rcontent");
                    Log.i("rcontent", rcontent);
                    rArray.add(new boardmessageinfo(rname, rcontent));
                }
            }
            catch(JSONException | NullPointerException e){
                //       Toast.makeText(getActivity().getApplicationContext(),"Error"+e.toString(),Toast.LENGTH_LONG).show();
                mResult=null;
            }
            mAdapter.notifyDataSetChanged();
        }

        @Override
        public void onStop() {
            super.onStop();
            if(mQueue!=null){
                mQueue.cancelAll(BOARDMESSAGETAG);
            }
        }

        public class boardmessageinfo {
            String rname;
            String rcontent;

            public boardmessageinfo(String rname, String rcontent) {
                this.rname = rname;
                this.rcontent = rcontent;
            }

            public String getRname() {
                return rname;
            }

            public String getRcontent() {
                return rcontent;
            }
        }

        static class BoardMessageViewHolder {
            TextView txRname;
            TextView txRcontent;
        }

        public class boardmessageAdapter extends ArrayAdapter<boardmessageinfo> {

            public boardmessageAdapter(Context context, int resource, List<boardmessageinfo> objects) {
                super(context, resource, objects);
            }

            @NonNull
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                BoardMessageViewHolder holder;
                if (convertView == null) {
                    convertView = getLayoutInflater().inflate(R.layout.listitem2, parent, false);
                    holder = new BoardMessageViewHolder();
                    holder.txRname = (TextView) convertView.findViewById(R.id.rname);
                    holder.txRcontent =(TextView) convertView.findViewById(R.id.rcontent);
                    convertView.setTag(holder);

                } else {
                    holder = (BoardMessageViewHolder) convertView.getTag();
                }
                holder.txRname.setText(getItem(position).getRname());
                holder.txRcontent.setText(getItem(position).getRcontent());
                return convertView;
            }
        }
    }
}
