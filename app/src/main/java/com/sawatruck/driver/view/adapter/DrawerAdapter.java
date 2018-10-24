package com.sawatruck.driver.view.adapter;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.sawatruck.driver.view.design.DrawerOption;
import com.sawatruck.driver.R;

public class DrawerAdapter extends ArrayAdapter<DrawerOption> {

    private final Context context;
    private final DrawerOption[] values;

    public DrawerAdapter(Context context, DrawerOption[] values) {

        super(context, R.layout.navigation_drawer_list_item, values);
        this.context = context;
        this.values = values;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.navigation_drawer_list_item, parent, false);
        TextView textView = (TextView) rowView.findViewById(R.id.sidemenu_title);
        TextView countView = (TextView) rowView.findViewById(R.id.sidemenu_count);

        ImageView imageView = (ImageView) rowView.findViewById(R.id.sidemenu_icon);
        textView.setText(values[position].getSection());
        imageView.setImageResource(values[position].getIcon());
        if(values[position].getCount() != 0)
            countView.setText(String.valueOf(values[position].getCount()));
        return rowView;
    }

}
