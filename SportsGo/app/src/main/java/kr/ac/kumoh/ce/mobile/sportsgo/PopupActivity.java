package kr.ac.kumoh.ce.mobile.sportsgo;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

public class PopupActivity extends Activity  {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        TextView title, address, call;

        super.onCreate(savedInstanceState);
        //타이틀바 없애기
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.popup_activity);

        //UI 객체생성
        title = (TextView)findViewById(R.id.title);
        address = (TextView)findViewById(R.id.address);
        call = (TextView)findViewById(R.id.call);

        //데이터 가져오기
        Intent intent = getIntent();
        String data = intent.getStringExtra("title");
        title.setText(data);
        data = intent.getStringExtra("address");
        address.setText(data);
        data = intent.getStringExtra("call");
        call.setText(data);
    }


    public void mOnGoBoard(View v){ //여기에 게시판으로 이동하는 코드 넣으면 됨
//        Intent intent = new Intent();
//        intent.putExtra("result", "Close Popup");
//        setResult(RESULT_OK, intent);
//
//        //액티비티(팝업) 닫기
//        finish();
    }

    //확인 버튼 클릭
    public void mOnClose(View v){
        //데이터 전달하기
        Intent intent = new Intent();
        intent.putExtra("result", "Close Popup");
        setResult(RESULT_OK, intent);

        //액티비티(팝업) 닫기
        finish();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //바깥레이어 클릭시 안닫히게
        if(event.getAction()==MotionEvent.ACTION_OUTSIDE){
            return false;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        //안드로이드 백버튼 막기
        return;
    }

}