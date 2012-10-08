package com.marvel.busbook;

import java.util.*;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;

public class BusStopActivity extends Activity {
	public static boolean SEARCH_STOP = true;  
	
	private Global global = Global.getInstance();
	
	Button btnSearch;
	ListView lstStops;
	EditText edtSearchStopName;
	

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.busstop);
    	btnSearch = (Button) findViewById(R.id.btnSearchStop);
    	lstStops = (ListView) findViewById(R.id.lstSearchStops);
    	edtSearchStopName = (EditText)findViewById(R.id.edtSearchStopName);
    	
    	btnSearch.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String query = edtSearchStopName.getText().toString();
				String stops[] = null;
				try{
					stops = BusInfoData.getInstance().searchBusStops(query);
				}catch (Exception e) {
		    		Log.e(global.TAG, "Exception: "+ e.getCause().toString());
    				Toast.makeText(getBaseContext(), "Error:" +e.getCause().toString(), Toast.LENGTH_LONG).show();
    				return;
		    	}
				
				ArrayList<HashMap<String, String>> mylist = new ArrayList<HashMap<String, String>>();
		        for(int i=0;i<stops.length;i++)
		        {
		            HashMap<String, String> map = new HashMap<String, String>();
			        map.put("num", String.valueOf(i));
			        map.put("stop", stops[i]);
			        mylist.add(map);
		        }
		        SimpleAdapter adapter = new SimpleAdapter(BusStopActivity.this, mylist, R.layout.itemlist,
		                new String[] {"num", "stop"}, new int[] {R.id.item_num, R.id.item_value});
		        lstStops.setAdapter(adapter);
		        BusStopActivity.SEARCH_STOP = true;
				
			}
		});
    	
    	lstStops.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				ListView l = (ListView)arg0;
				@SuppressWarnings("unchecked")
				HashMap<String, String> hashmap = (HashMap<String, String>)l.getItemAtPosition(arg2);
				if(BusStopActivity.SEARCH_STOP)
				{
					String stop = hashmap.get("stop");
					String[] busnums = null;
					try{
						busnums = BusInfoData.getInstance().searchBusViaStop(stop);
					}
			    	catch (Exception e) {
			    		Log.e(global.TAG, "Exception: "+ e.getCause().toString());
	    				Toast.makeText(getBaseContext(), "Error:" +e.getCause().toString(), Toast.LENGTH_LONG).show();
	    				return;
			    	}
					
					ArrayList<HashMap<String, String>> mylist = new ArrayList<HashMap<String, String>>();
					if(busnums !=null)
				        for(int i=0;i<busnums.length;i++)
				        {
				            HashMap<String, String> map = new HashMap<String, String>();
					        map.put("num", String.valueOf(i));
					        map.put("busnum", busnums[i]);
					        mylist.add(map);
				        }
			        SimpleAdapter adapter = new SimpleAdapter(BusStopActivity.this, mylist, R.layout.itemlist,
			                new String[] {"num", "busnum"}, new int[] {R.id.item_num, R.id.item_value});
			        lstStops.setAdapter(adapter);
			        BusStopActivity.SEARCH_STOP = false;
				}
				else
				{
					String busnum = hashmap.get("busnum");

			    	Intent next = new Intent();
			        next.setClass(BusStopActivity.this, BusInfoActivity.class);

			        BusInfo bus = BusInfoData.getInstance().getBus(busnum);
			        try{
			        	BusInfoData.getInstance().getExtendBusInfo(bus);
			        }
			    	catch (Exception e) {
			    		Log.e(global.TAG, "Exception: "+ e.getCause().toString());
						Toast.makeText(getBaseContext(), "Error:" +e.getCause().toString(), Toast.LENGTH_LONG).show();
			    	}
			        next.putExtra("num", bus.getNum());
			        next.putExtra("time", bus.getTime());
			        next.putExtra("stops", bus.getStops());
			        startActivity(next);
					
					return;
				}

				
			}

    		
		});
    }
    
}
