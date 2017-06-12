package kr.ac.kumoh.ce.mobile.sportsgo;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created by dlgus on 2017-05-16.
 */
public class WriteBoard extends Activity {
    String memo,title,total, playplan, stadiuminfo;
    EditText writetitleedt, writecontentedt, writetotlaedt;
    TextView mTxDate, mTxTime;

    int mYear, mMonth, mDay, mHour, mMinute;
    PostDB pdb = new PostDB();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.writeboard);

        Intent stadiuminfointent = getIntent();
        stadiuminfo= stadiuminfointent.getStringExtra("stadiuminfo");

        writetitleedt = (EditText)findViewById(R.id.writetitleeedt);
        writecontentedt = (EditText)findViewById(R.id.writecontentedt);
        writetotlaedt = (EditText)findViewById(R.id.writetotlaedt);

        mTxDate = (TextView)findViewById(R.id.setDate);
        mTxTime = (TextView)findViewById(R.id.setTime);
        Calendar cal = new GregorianCalendar();
        mYear = cal.get(Calendar.YEAR);
        mMonth = cal.get(Calendar.MONTH);
        mDay = cal.get(Calendar.DAY_OF_MONTH);
        mHour = cal.get(Calendar.HOUR_OF_DAY);
        mMinute = cal.get(Calendar.MINUTE);

    }

    public void mOnClick(View v) {
        switch (v.getId()) {
            case R.id.InputDate:
                new DatePickerDialog(WriteBoard.this, mDateSetListener, mYear, mMonth, mDay).show();
                break;
            case R.id.InputHour:
                new TimePickerDialog(WriteBoard.this, mTimeSetListener, mHour, mMinute, false).show();
                break;
        }
    }

    DatePickerDialog.OnDateSetListener mDateSetListener =
            new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    mYear = year;
                    mMonth = monthOfYear;
                    mDay= dayOfMonth;
                    mTxDate.setText(String.format("%d-%d-%d", mYear, mMonth+1, mDay));
                }
            };

    TimePickerDialog.OnTimeSetListener mTimeSetListener =
            new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    mHour = hourOfDay;
                    mMinute = minute;

                    if(mHour > 12)
                        mTxTime.setText(String.format("PM %d:%d", mHour-12, mMinute));
                    else
                        mTxTime.setText(String.format("AM %d:%d", mHour, mMinute));
                }
            };

    public void onCancel(View v){
        Intent intent = new Intent(this,Board.class);
        startActivity(intent);
        finish();
    }

    public void onEntry(View v){
        Intent intent = new Intent(this, Board.class);
        intent.putExtra("stadiuminfo",stadiuminfo);

        title = writetitleedt.getText().toString();
        memo =writecontentedt.getText().toString();
        total = writetotlaedt.getText().toString();
        playplan =""+mYear+ "-" + mMonth + "-" + mDay + " "+ mTxTime.getText();
        pdb.execute();

        startActivity(intent);
        finish();
    }

    public class PostDB extends AsyncTask<Void, Integer, Void> {
        String data = "";

        @Override
        protected Void doInBackground(Void... unused) {

            String param = "user_email=" + SignIn.Email + "&title=" + title + "&memo=" +memo +
                    "&total="+ total + "&cal="+ playplan + "&stadiuminfo="+ stadiuminfo+"";
            try {
                URL url = new URL(
                        "http://shid1020.dothome.co.kr/writeboard.php");
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

            if(data.equals("0"))
                Toast.makeText(getApplicationContext(), "글쓰기 성공하였습니다.", Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(getApplicationContext(), "글쓰기 실패하였습니다.", Toast.LENGTH_SHORT).show();
        }
    }
}
