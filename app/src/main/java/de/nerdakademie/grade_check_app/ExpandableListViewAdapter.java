package de.nerdakademie.grade_check_app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckedTextView;
import android.widget.TextView;

import java.util.ArrayList;


@SuppressWarnings("unchecked")
public class ExpandableListViewAdapter extends BaseExpandableListAdapter {
	public ArrayList<String> groupItem, tempChild;
	public ArrayList<ArrayList<String>> Childtem = new ArrayList<>();
	LayoutInflater minflater;

	public ExpandableListViewAdapter(Context context,ArrayList<String> grList, ArrayList<ArrayList<String>> childItem) {
		groupItem = grList;
		this.Childtem = childItem;
		minflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}


	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return Childtem.get(groupPosition).get(childPosition);
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return Childtem.get(groupPosition).get(childPosition).hashCode();
	}

	@Override
	public View getChildView(int groupPosition, final int childPosition,
							 boolean isLastChild, View convertView, ViewGroup parent) {
		tempChild = Childtem.get(groupPosition);

		if (convertView == null) {
			convertView = minflater.inflate(R.layout.childrow, null);
		}
		TextView text = (TextView) convertView.findViewById(R.id.textView1);
		text.setText(tempChild.get(childPosition));

		return convertView;
	}


	@Override
	public int getChildrenCount(int groupPosition) {
		return (Childtem.get(groupPosition)).size();
	}

	@Override
	public Object getGroup(int groupPosition) {
		return groupItem.get(groupPosition);
	}

	@Override
	public int getGroupCount() {
		return groupItem.size();
	}

	@Override
	public void onGroupCollapsed(int groupPosition) {
		super.onGroupCollapsed(groupPosition);
	}

	@Override
	public void onGroupExpanded(int groupPosition) {
		super.onGroupExpanded(groupPosition);
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupItem.get(groupPosition).hashCode();
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
							 View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = minflater.inflate(R.layout.grouprow, null);
		}
		((CheckedTextView) convertView).setText(groupItem.get(groupPosition));
//		((CheckedTextView) convertView).setChecked(isExpanded);
		return convertView;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}
	
	public void addData(ArrayList<String> group, ArrayList<ArrayList<String>> child){
		groupItem.addAll(group);
		Childtem.addAll(child);
		this.notifyDataSetChanged();
	}

}
