package kr.ac.kumoh.ce.mobile.sportsgo;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

public class PopupActivity extends Activity  {
    String data;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        TextView title, address, call;

        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.popup_activity);

        title = (TextView)findViewById(R.id.title);
        address = (TextView)findViewById(R.id.address);
        call = (TextView)findViewById(R.id.call);

        Intent intent = getIntent();
        data = intent.getStringExtra("title");
        title.setText(data);
        data = intent.getStringExtra("address");
        address.setText(data);
        data = intent.getStringExtra("call");
        call.setText(data);
        data = intent.getStringExtra("point");
    }


    public void onGoBoard(View v){
        Intent intent = new Intent(PopupActivity.this, Board.class);
        intent.putExtra("stadiuminfo", data);
        startActivity(intent);

        finish();
    }

    public void mOnClose(View v){
        Intent intent = new Intent();
        intent.putExtra("result", "Close Popup");
        setResult(RESULT_OK, intent);

        finish();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(event.getAction()==MotionEvent.ACTION_OUTSIDE){
            return false;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        return;
    }
}