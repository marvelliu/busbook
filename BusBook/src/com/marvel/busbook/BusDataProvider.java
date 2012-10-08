package com.marvel.busbook;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.SearchManager;
import android.content.*;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.text.TextUtils;

public class BusDataProvider extends ContentProvider{


    public static String AUTHORITY = "dictionary";

    private static final int SEARCH_SUGGEST = 0;
    private static final int SHORTCUT_REFRESH = 1;
    private static final UriMatcher sURIMatcher = buildUriMatcher();

    /**
     * The columns we'll include in our search suggestions.  There are others that could be used
     * to further customize the suggestions, see the docs in {@link SearchManager} for the details
     * on additional columns that are supported.
     */
    private static final String[] COLUMNS = {
            "_id",  // must include this column
            SearchManager.SUGGEST_COLUMN_TEXT_1,
            SearchManager.SUGGEST_COLUMN_TEXT_2,
            SearchManager.SUGGEST_COLUMN_INTENT_DATA,
            };
    
    /**
     * Sets up a uri matcher for search suggestion and shortcut refresh queries.
     */
    private static UriMatcher buildUriMatcher() {
        UriMatcher matcher =  new UriMatcher(UriMatcher.NO_MATCH);
        matcher.addURI(AUTHORITY, SearchManager.SUGGEST_URI_PATH_QUERY, SEARCH_SUGGEST);
        matcher.addURI(AUTHORITY, SearchManager.SUGGEST_URI_PATH_QUERY + "/*", SEARCH_SUGGEST);
        matcher.addURI(AUTHORITY, SearchManager.SUGGEST_URI_PATH_SHORTCUT, SHORTCUT_REFRESH);
        matcher.addURI(AUTHORITY, SearchManager.SUGGEST_URI_PATH_SHORTCUT + "/*", SHORTCUT_REFRESH);
        return matcher;
    }
    
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getType(Uri uri) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean onCreate() {
        Resources resources = getContext().getResources();
        BusInfoData.getInstance().ensureLoaded(resources,false);
        return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {

        if (!TextUtils.isEmpty(selection)) {
            throw new IllegalArgumentException("selection not allowed for " + uri);
        }
        if (selectionArgs != null && selectionArgs.length != 0) {
            throw new IllegalArgumentException("selectionArgs not allowed for " + uri);
        }
        if (!TextUtils.isEmpty(sortOrder)) {
            throw new IllegalArgumentException("sortOrder not allowed for " + uri);
        }
        switch (sURIMatcher.match(uri)) {
            case SEARCH_SUGGEST:
                String query = null;
                if (uri.getPathSegments().size() > 1) {
                    query = uri.getLastPathSegment().toLowerCase();
                }
                return getSuggestions(query, projection);
            case SHORTCUT_REFRESH:
                String shortcutId = null;
                if (uri.getPathSegments().size() > 1) {
                    shortcutId = uri.getLastPathSegment();
                }
                return refreshShortcut(shortcutId, projection);
            default:
                throw new IllegalArgumentException("Unknown URL " + uri);
        }
	}
	
	

    private Cursor getSuggestions(String query, String[] projection) {
        String processedQuery = (query == null ? "" : query.toLowerCase());
        MatrixCursor cursor = new MatrixCursor(COLUMNS);
        
        if(query.length()<2)
        	return cursor;
        List<BusInfo> buses = BusInfoData.getInstance().getMatches(processedQuery);
        
        if(Global.getInstance().sortResult){
        	Collections.sort(buses);
        }

        List<BusInfo> temp = new ArrayList<BusInfo>(); 
        for (int i =0; i<buses.size();i++) {
        	BusInfo bus = buses.get(i);
			//如果是精确匹配，则放置头部，否则放置于尾部
			if(BusInfoData.isExactlyMatch(bus.getNum(), query))
				temp.add(bus);
        }
        for (BusInfo bus : temp) 
        	buses.remove(bus);        

        for (BusInfo bus : temp) 
        	buses.add(0, bus);

        int id = 0;
        for (BusInfo bus : buses) {
            cursor.addRow(columnValuesOfBus(id++, bus));
        }

        return cursor;
    }


    private Object[] columnValuesOfBus(int id, BusInfo bus) {
        return new Object[] {
        		id,           // _id
        		bus.getNum(),           // text1
        		bus.getStartPlace()+"-"+bus.getEndPlace(),     // text2
        		bus.getNum(),           // intent_data (included when clicking on item)
        };
    }

    /**
     * Note: this is unused as is, but if we included
     * {@link SearchManager#SUGGEST_COLUMN_SHORTCUT_ID} as a column in our results, we
     * could expect to receive refresh queries on this uri for the id provided, in which case we
     * would return a cursor with a single item representing the refreshed suggestion data.
     */
    private Cursor refreshShortcut(String shortcutId, String[] projection) {
        return null;
    }
    
	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		// TODO Auto-generated method stub
		return 0;
	}
	

}
