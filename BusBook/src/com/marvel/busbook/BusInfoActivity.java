package com.marvel.busbook;

import java.util.*;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.*;

public class BusInfoActivity extends Activity {

    private TextView busNum;
    private TextView busTime;
    private ListView busStop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.businfo);

        busNum = (TextView) findViewById(R.id.txtViewBusnum);
        busTime = (TextView) findViewById(R.id.txtViewBusTime);
        busStop = (ListView) findViewById(R.id.lstViewBusStop);

        Intent intent = getIntent();

        String num = intent.getStringExtra("num");
        String time = intent.getStringExtra("time");
        String stops[] = intent.getStringArrayExtra("stops");

        busNum.setText(num);
        busTime.setText(time);

        ArrayList<HashMap<String, String>> mylist = new ArrayList<HashMap<String, String>>();
        for(int i=0;i<stops.length;i++)
        {
            HashMap<String, String> map = new HashMap<String, String>();
	        map.put("num", String.valueOf(i));
	        map.put("stop", stops[i]);
	        mylist.add(map);
        }
        SimpleAdapter adapter = new SimpleAdapter(this, mylist, R.layout.itemlist,
                new String[] {"num", "stop"}, new int[] {R.id.item_num, R.id.item_value});
        busStop.setAdapter(adapter);

    }
}
