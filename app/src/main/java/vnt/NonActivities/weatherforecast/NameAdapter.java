package vnt.NonActivities.weatherforecast;
import java.util.List;

import vnt.activities.weatherforecast.R;
import android.content.Context;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class NameAdapter extends ArrayAdapter<City> {
    private Context mContext;
    private int row;
    private List<City> list ;
    
    private SparseBooleanArray mSelectedItemsIds;
    public NameAdapter(Context context, int textViewResourceId, List<City> list) {
        super(context, textViewResourceId, list);
        mSelectedItemsIds = new SparseBooleanArray();
        this.mContext=context;
        this.row=textViewResourceId;
        this.list=list;
    }

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		ViewHolder holder;
		if (view == null) {
			LayoutInflater inflater = (LayoutInflater) mContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(row, null);

			holder = new ViewHolder();
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}
		if ((list == null) || ((position + 1) > list.size()))
			return view; // Can't extract item

		City obj = list.get(position);
		
		holder.name = (TextView)view.findViewById(R.id.label);
		
		if(null!=holder.name&&null!=obj&&obj.getCityName().length()!=0){
			holder.name.setText(obj.getCityName());
		}
		return view;
	}

	public static class ViewHolder {
		public TextView name;
	}
	@Override
    public void remove(City object) {


       list.remove(object);
        notifyDataSetChanged();
    }
	 public void removeAll()
	 {
		 list.removeAll(list); 
		 notifyDataSetChanged();
	 }

    public void toggleSelection(int position) {
        selectView(position, !mSelectedItemsIds.get(position));
    }

    public void removeSelection() {
        mSelectedItemsIds = new SparseBooleanArray();
        notifyDataSetChanged();
    }

    public void selectView(int position, boolean value) {
        if (value)
            mSelectedItemsIds.put(position, value);

        else
            mSelectedItemsIds.delete(position);
        notifyDataSetChanged();
    }
    public SparseBooleanArray getSelectedIds() {
        return mSelectedItemsIds;
    }

    public boolean isSelected(int position){
        boolean isSelected;
        if(!mSelectedItemsIds.get(position))
            isSelected = true;
        else
            isSelected = false;
        return isSelected;
    }
	
}
