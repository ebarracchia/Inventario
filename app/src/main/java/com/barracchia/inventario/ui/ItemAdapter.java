package com.barracchia.inventario.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.barracchia.inventario.R;
import com.barracchia.inventario.model.ItemInventario;

import java.util.Collections;
import java.util.List;

public class ItemAdapter extends BaseAdapter {
    private final Context context; //context
    private List<ItemInventario> items = Collections.emptyList(); //data source of the list adapter

    //public constructor
    public ItemAdapter(Context context) {
        this.context = context;
    }

    public void updateItems(List<ItemInventario> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return items.size(); //returns total of items in the list
    }

    @Override
    public Object getItem(int position) {
        return items.get(position); //returns list item at the specified position
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).
                    inflate(R.layout.listview_item, parent, false);
        }

        LinearLayout renglon = ViewHolder.get(convertView, R.id.renglon);
        TextView code = ViewHolder.get(convertView, R.id.text_view_item_code);
        TextView group = ViewHolder.get(convertView, R.id.text_view_item_group);
        TextView brand = ViewHolder.get(convertView, R.id.text_view_item_brand);

        // get current item to be displayed
        ItemInventario currentItem = (ItemInventario) getItem(position);

        //sets the text for item name and item description from the current item object
        code.setText(currentItem.getCode());
        group.setText(currentItem.getGroup());
        brand.setText(currentItem.getBrand());

        // returns the view for the current row
        return convertView;
    }
}
