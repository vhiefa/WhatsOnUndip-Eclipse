package com.vhiefa.whatsonundip;

import com.vhiefa.whatsonundip.data.EventContract.EventEntry;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class EventAdapter extends CursorAdapter{

	
	
	public EventAdapter(Context context, Cursor c, int flags) {
		super(context, c, flags);
		// TODO Auto-generated constructor stub
	}

   @Override
   public int getItemViewType(int position){
	   return position;
   }

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {		
		//int viewType = getItemViewType(cursor.getPosition());
		
		//return LayoutInflater.from(context).inflate(R.layout.list_item_event, parent, false);
		
		View view = LayoutInflater.from(context).inflate(R.layout.list_item_event, parent, false);
		ViewHolder viewHolder = new ViewHolder(view);
		view.setTag(viewHolder);
		return view;
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {

   
		ViewHolder viewHolder = (ViewHolder) view.getTag();
		
	   // int EventId = cursor.getInt(EventFragment.COL_ID);
	    
	    		
	    String dateStr = cursor.getString(EventFragment.COL_EVENT_DATE);
	    viewHolder.dateView.setText(Utility.formatDate(dateStr));

        
        String title = cursor.getString(EventFragment.COL_EVENT_TITLE);
        viewHolder. titleView .setText(title);
        
        String venue = cursor.getString(EventFragment.COL_EVENT_VENUE);
        viewHolder. venueView .setText(venue);
        
        
        String category = cursor.getString(EventFragment.COL_EVENT_CATEGORY);
        viewHolder. categoryView.setText(category);

        viewHolder.iconView.setImageResource(Utility.getIconResourceForEventCategory(category));
		
	}
	
	
    /**
     * Cache of the children views for event list item.
     */
    public static class ViewHolder {
        public final ImageView iconView;
        public final TextView titleView;
        public final TextView dateView;
        public final TextView venueView;
        public final TextView categoryView;
       

        public ViewHolder(View view) {
            iconView = (ImageView) view.findViewById(R.id.list_item_icon);
            dateView = (TextView) view.findViewById(R.id.list_item_date_textview);
            titleView = (TextView) view.findViewById(R.id.list_item_title_textview);
            venueView =  ((TextView) view.findViewById(R.id.list_item_venue_textview));
            categoryView =  ((TextView) view.findViewById(R.id.list_item_category_textview));
        }
    }
}
