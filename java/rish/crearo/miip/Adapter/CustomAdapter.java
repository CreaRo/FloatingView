package rish.crearo.miip.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import rish.crearo.miip.Manager.PInfo;
import rish.crearo.miip.R;

public class CustomAdapter extends ArrayAdapter {

    private int resource;
    private LayoutInflater inflater;

    public CustomAdapter(Context ctx, int resourceId, List apps) {
        super(ctx, resourceId, apps);
        resource = resourceId;
        inflater = LayoutInflater.from(ctx);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        convertView = inflater.inflate(resource, null);

        PInfo app = (PInfo) getItem(position);

        TextView txtName = (TextView) convertView.findViewById(R.id.textView1);
        txtName.setText(app.appname);

        ImageView imageCity = (ImageView) convertView.findViewById(R.id.imageView1);
        imageCity.setImageDrawable(app.icon);
        return convertView;
    }
}