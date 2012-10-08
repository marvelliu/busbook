package com.marvel.busbook;

import java.util.List;

import android.app.*;
import android.content.*;
import android.os.*;
import android.util.Log;
import android.view.*;
import android.widget.*;
import android.text.TextUtils;

public class SearchBusLine extends Activity {
    private static final int MENU_SEARCH = 1;
    private static final int MENU_PREF = MENU_SEARCH+1;
    private static final int MENU_ABOUT = MENU_SEARCH+2;
    
	private Global global = Global.getInstance();
	
    private Button btnSearchBusNum;
    private Button btnSearchBusStop;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.busline);
        
        Global global = Global.getInstance();
        global.setContext(getBaseContext());
        global.InitPref();

        Intent intent = getIntent();
        
        btnSearchBusNum = (Button) findViewById(R.id.btnSearchBusNum);
        btnSearchBusNum.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
		        onSearchRequested();
				
			}
		});
        

        btnSearchBusStop = (Button) findViewById(R.id.btnSearchBusStop);
        btnSearchBusStop.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent next = new Intent();
		        next.setClass(SearchBusLine.this, BusStopActivity.class);		        
		        startActivity(next);
			}
		});
        
        if (Intent.ACTION_VIEW.equals(intent.getAction())) {
            // from click on search results
        	BusInfoData.getInstance().ensureLoaded(getResources(),false);
            String word = intent.getDataString();
            BusInfo bus = BusInfoData.getInstance().getMatches(word).get(0);
            showBusStopInfo(bus);
            finish();
        } else if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
        	BusInfoData.getInstance().ensureLoaded(getResources(),false);
            String word = intent.getStringExtra(SearchManager.QUERY);
            BusInfo bus = BusInfoData.getInstance().getMatches(word, true).get(0);
            showBusStopInfo(bus);
        }

        Log.d("dict", intent.toString());
        if (intent.getExtras() != null) {
            Log.d("dict", intent.getExtras().keySet().toString());
        }
    }
    
    @Override
    public  boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, MENU_SEARCH, 0, R.string.search)
	        .setIcon(android.R.drawable.ic_search_category_default)
	        .setAlphabeticShortcut(SearchManager.MENU_KEY);
	    menu.add(0, MENU_PREF, 0, R.string.pref)
	    	.setIcon(android.R.drawable.ic_menu_preferences);
	    menu.add(0, MENU_ABOUT, 0, R.string.about)
    		.setIcon(android.R.drawable.ic_menu_info_details);

		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		    case MENU_SEARCH:
		        onSearchRequested();
		        return true;
		    case MENU_ABOUT:
		    	AboutDialog dialog = new AboutDialog(this);
		    	dialog.show();
		        return true;
		    case MENU_PREF:
		    	Intent next = new Intent();
		        next.setClass(this, PrefActivity.class);		        
		        startActivity(next);
		        return true;
		}
		return super.onOptionsItemSelected(item);
	} 
	

    public void showBusStopInfo(BusInfo bus)
    {
    	Intent next = new Intent();
        next.setClass(this, BusInfoActivity.class);
        try{
        	BusInfoData.getInstance().getExtendBusInfo(bus);
        }
    	catch (Exception e) {
    		Log.e(global.TAG, "Exception: "+ e.getCause().toString());
			Toast.makeText(getBaseContext(), "Error:" +e.getCause().toString(), Toast.LENGTH_LONG).show();
			return;
    	}
        next.putExtra("num", bus.getNum());
        next.putExtra("time", bus.getTime());
        next.putExtra("stops", bus.getStops());
        startActivity(next);
    	
    }
    
	class BusAdapter extends BaseAdapter implements AdapterView.OnItemClickListener {

        private final List<BusInfo> buses;
        private final LayoutInflater mInflater;

        public BusAdapter(List<BusInfo> buses) {
        	this.buses = buses;
            mInflater = (LayoutInflater) SearchBusLine.this.getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
        }

        public int getCount() {
            return buses.size();
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            TwoLineListItem view = (convertView != null) ? (TwoLineListItem) convertView :
                    createView(parent);
            bindView(view, buses.get(position));
            return view;
        }

        private TwoLineListItem createView(ViewGroup parent) {
            TwoLineListItem item = (TwoLineListItem) mInflater.inflate(
                    android.R.layout.simple_list_item_2, parent, false);
            item.getText2().setSingleLine();
            item.getText2().setEllipsize(TextUtils.TruncateAt.END);
            return item;
        }

        private void bindView(TwoLineListItem view, BusInfo bus) {
            view.getText1().setText(bus.getNum());
            view.getText2().setText(bus.getStartPlace());
        }

        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            showBusStopInfo(buses.get(position));
        }
        
    }

   
}