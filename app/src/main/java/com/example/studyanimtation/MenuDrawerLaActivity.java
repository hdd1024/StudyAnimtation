package com.example.studyanimtation;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.studyanimtation.view.menu.MenuBgView;
import com.example.studyanimtation.view.menu.MenuContentLayout;

public class MenuDrawerLaActivity extends AppCompatActivity {
    TextView tv_wallet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menudrawer);
        tv_wallet = findViewById(R.id.tv_wallet);

        tv_wallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getBaseContext(), "马上进入钱包", Toast.LENGTH_SHORT).show();
            }
        });

    }
}
