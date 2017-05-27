package kr.ac.kumoh.ce.mobile.sportsgo;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by dlgus on 2017-05-16.
 */
public class SignUp extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);

        EditText etSignUpId = (EditText)findViewById(R.id.etSignUpId);
        EditText etSignUpPW = (EditText)findViewById(R.id.etSignUpPW);
        Button btnCheck = (Button)findViewById(R.id.btnCheck);
        btnCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //아이디 중복검사
            }
        });
    }
}
