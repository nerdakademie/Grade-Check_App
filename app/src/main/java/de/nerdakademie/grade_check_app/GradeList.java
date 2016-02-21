package de.nerdakademie.grade_check_app;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.ArrayAdapter;
import android.widget.ListView;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GradeList extends AppCompatActivity {

    String user;
    String pass;
    ArrayAdapter adapter;
    private GradesCheckContainer checkContainer = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            user = extras.getString("user", "");
            pass = extras.getString("pass", "");
        }
        checkContainer = new GradesCheckContainer(user, pass);

        setContentView(R.layout.grade_main);
        initUI();

    }

    private void initUI() {
        ListView l = (ListView) GradeList.this.findViewById(R.id.listView);
        //<String>(this, android.R.layout.simple_list_item_1, items);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, new ArrayList<String>());
        l.setAdapter(adapter);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    checkContainer.checkLoadOldGrades();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                Message msg = Message.obtain();
                msg.what = 1;
                permissionHandler.sendMessage(msg);
            }
        }).start();

    }

    Handler permissionHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    //adapter.add("TE");
                    adapter.addAll(checkContainer.getGrades().keySet());
                    return true;
                default:
                    return true;
            }
        }
    });

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.grademenu_main, menu);
        return true;
    }

}
