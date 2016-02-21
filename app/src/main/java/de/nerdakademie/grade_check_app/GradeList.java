package de.nerdakademie.grade_check_app;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListView;
import android.widget.Toast;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GradeList extends AppCompatActivity {

    String user;
    String pass;
    ExpandableListViewAdapter adapter;
    private GradesCheckContainer checkContainer = null;
    ProgressDialog progressDialog;


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
        ExpandableListView l = (ExpandableListView) GradeList.this.findViewById(R.id.listView);
        //<String>(this, android.R.layout.simple_list_item_1, items);
        adapter = new ExpandableListViewAdapter(GradeList.this, new ArrayList<String>(), new ArrayList<ArrayList<String>>());
        l.setAdapter(adapter);
        progressDialog = new ProgressDialog(GradeList.this);
        progressDialog.setTitle("loading grades");
        progressDialog.setIndeterminate(true);
        progressDialog.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                Message msg = Message.obtain();
                try {
                    checkContainer.checkLoadOldGrades();
                    msg.what = 1;
                } catch (Exception e) {
                    msg.what = 0;
                }

                permissionHandler.sendMessage(msg);
            }
        }).start();

    }

    Handler permissionHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    Toast.makeText(GradeList.this, "password incorrect", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                    Intent intent = new Intent(GradeList.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(intent);
                    return true;
                case 1:
                    //adapter.add("TE");
                    ArrayList<String> group = new ArrayList<>();
                    ArrayList<ArrayList<String>> child = new ArrayList<>();
                    Map<String,String> grades = checkContainer.getGrades();
                    for(String key: grades.keySet()){
                        if (key.length() > 0) {
                            group.add(key);
                            ArrayList<String> temparray = new ArrayList<>();
                            temparray.add(grades.get(key));
                            child.add(temparray);
                        }
                    }
                    adapter.addData(group, child);
                    progressDialog.dismiss();
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
