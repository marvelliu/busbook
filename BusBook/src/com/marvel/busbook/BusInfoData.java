package com.marvel.busbook;

import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import android.content.res.Resources;
import android.util.Log;



public class BusInfoData {
	

    private static final BusInfoData instance = new BusInfoData();
    private Global global = Global.getInstance();
    
    private BusInfoData()
    {
    }

    public static BusInfoData getInstance() {
        return instance;
    }
    private final Map<String, BusInfo> mDict = new ConcurrentHashMap<String, BusInfo>();

    public boolean loaded = false;
    
    public synchronized void ensureLoaded(final Resources resources, boolean forced) {
    	if (Global.getInstance().getContext() == null)
    		return;
    	
        if (loaded && !forced) return;

        new Thread(new Runnable() {
            public void run() {
            	LoadBusInfo(global.buslinepath+global.buslinefile, global.encoding);
            	loaded = true;
            }
        }).start();
    }
    
    public BusInfo getBus(String num)
    {
    	return mDict.get(num);
    }    
    
    private void LoadBusInfo( String filename, String encoding)
    {
    	try {
    		mDict.clear();
    		File file = new File(filename);     		
    		InputStream in = new FileInputStream(file); 
    		BufferedReader br = new BufferedReader(new InputStreamReader(in, encoding));
    		String line = null;
    		while ((line = br.readLine()) != null) {
    			int i1 = line.indexOf(" ");
    			if(i1<0)
    				continue;
    			String busnum = line.substring(0, i1);
    			
    			int i2 = line.indexOf("-", i1);
    			if(i2<0)
    				continue;
    			String startPlace = line.substring(i1+1, i2);

    			i2 = line.lastIndexOf(":{");
    			if(i2<0)
    				i2 = line.length(); 
    			int t1 = line.indexOf('-', i1);
    			while((t1=line.indexOf('-', t1+1))>0 && t1<i2)
    			{
    				i1 = t1;
    			}
    			if(i1<0)
    				continue;
    			String endPlace = line.substring(i1+1, i2);
    			
    			mDict.put(busnum, new BusInfo(busnum, startPlace,endPlace));
    			Log.i(global.TAG, busnum);
    			
    		}
    		br.close();
    	}
    	catch (FileNotFoundException e) {
    		Log.e(global.TAG, "FileNotFoundException: "+ e.getCause().toString());
    	}
    	catch (IOException e) {
    		Log.e(global.TAG, "IOException: "+ e.getCause().toString());
    	}
    	catch (Exception e) {
    		Log.e(global.TAG, "Other exception: "+ e.getCause().toString());
    	}

    }    

    public void getExtendBusInfo(BusInfo bus) throws Exception
    {
		File file = new File(global.buslinepath+global.buslinefile);     		
		InputStream in = new FileInputStream(file); 
		BufferedReader br = new BufferedReader(new InputStreamReader(in, global.encoding));
		
		String line = null;
		while((line=br.readLine())!= null)
			if(line.startsWith(bus.getNum()+" "))
				break;

		ArrayList<String> stops = new ArrayList<String>();
		int i1 = bus.getNum().length();
		int i2 = line.lastIndexOf(":{");

		if(i2<0)
			i2 = line.length();		
		int t1 = 0;
		while((t1=line.indexOf('-', t1+1))>0 && t1<i2)
		{
			String stop = line.substring(i1+1, t1); 
			stops.add(stop);
			i1 = t1;
		}
		stops.add(line.substring(i1+1, i2));
		String [] t = stops.toArray(new String[stops.size()]);
		bus.setStops(t);
			
		i1 = i2+2;
		i2 = line.lastIndexOf("}");
		if(i1>=0 && i2>=0)
		{
			String time = line.substring(i1, i2);
			bus.setTime(time);
		}
		
		br.close();

	
    }

    public List<BusInfo> getMatches(String query) {
    	return getMatches(query, false);
    }
	public List<BusInfo> getMatches(String query, boolean exactlyMatch) {
		
    	List<BusInfo> list = new ArrayList<BusInfo>(); 
    	if(query == "" || query == null)
    		return list;
    	Iterator<String> busnums = mDict.keySet().iterator();
    	while(busnums.hasNext())
    	{
    		String busnum = busnums.next();
    		if(busnum.indexOf(query)<0)
    			continue;
    		if(exactlyMatch){
    			if(isExactlyMatch(busnum, query)){
    				list.add(mDict.get(busnum));
    				return list;
    			}
    		}
    		else    		
    			list.add(mDict.get(busnum));
    	}
        return (list.size() == 0)? Collections.<BusInfo>emptyList() : list;
    }
	
	public static boolean isExactlyMatch(String busnum, String query){
		char lc = busnum.charAt(query.length());
		if(busnum.startsWith(query) && !(lc>='0' && lc<='9'))
			return true;
		else
			return false;
	}
    
    public String[] searchBusStops(String query) throws Exception
    {
		ArrayList<String> stops = new ArrayList<String>();
		File file = new File(global.buslinepath+global.buslinefile);     		
		InputStream in = new FileInputStream(file); 
		BufferedReader br = new BufferedReader(new InputStreamReader(in, global.encoding));
		
		int i=0,start=0,end=0;
		String line = null;
		while((line=br.readLine())!= null)
		{
			int last = line.indexOf(":{");
			if((i=line.indexOf(query))<0)
				continue;
			do
			{
				if(i>=last)
					break;
				for(start=i;start>0;start--)
					if(line.charAt(start)=='-' || line.charAt(start)==' ')
						break;
				start++;
				
				for(end=i;end<line.length();end++)
					if(line.charAt(end)=='-' || line.charAt(end)==':' || line.charAt(end)=='\0')
						break;
				String stop = null;
				if(start>0)
					stop = line.substring(start, end).trim();
				if(!stops.contains(stop))
					stops.add(stop);
			}while((i=line.indexOf(query, i+1))>0);
		}    		
		br.close();
		String result[] = stops.toArray(new String[stops.size()]);
		return result;
    	
    }
    

    public String[] searchBusViaStop(String stop) throws Exception
    {
		ArrayList<String> busnums = new ArrayList<String>();
		File file = new File(global.buslinepath+global.buslinefile);     		
		InputStream in = new FileInputStream(file); 
		BufferedReader br = new BufferedReader(new InputStreamReader(in, global.encoding));
		
		int i=0;
		String line = null;
		while((line=br.readLine())!= null)
		{
			if(line.indexOf("-"+stop+"-")>0 
					|| line.indexOf(" "+stop+"-")>0 
					|| line.indexOf("-"+stop+":")>0 
				)
			{
    			i = line.indexOf(" ");
    			if(i<0)
    				continue;
    			String busnum = line.substring(0, i);
    			busnums.add(busnum);
			}
		}    		
		br.close();
		String result[] = busnums.toArray(new String[busnums.size()]);
		return result;
    	
    }
}
