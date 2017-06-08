package kr.ac.kumoh.ce.mobile.sportsgo;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * Created by dlgus on 2017-04-29.
 */
public class MyPage extends Fragment {
    View rootView;

    public MyPage() { }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.mypage_main, container, false);
        Button btnSignIn = (Button)rootView.findViewById(R.id.btnSignIn);
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SignIn.class);
                startActivity(intent);
            }
        });

        return rootView;
    }
}
