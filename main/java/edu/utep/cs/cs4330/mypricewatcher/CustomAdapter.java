package edu.utep.cs.cs4330.mypricewatcher;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import java.util.ArrayList;
import java.text.NumberFormat;
import java.text.DecimalFormat;

import edu.utep.cs.cs4330.mypricewatcher.DTO.Item;

public class CustomAdapter extends ArrayAdapter<Item> {

    private Context context;
    private ArrayList<Item> items = new ArrayList<>();

    CustomAdapter(@NonNull Context context, int resource,  ArrayList<Item> objects) {
        super(context, resource, objects);
        this.context = context;
        this.items = objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        NumberFormat format = new DecimalFormat("#0.00");
        Item item = (Item) getItem(position);

        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_layout, parent, false);
        }

        TextView nameList = convertView.findViewById(R.id.nameList);
        TextView urlList = convertView.findViewById(R.id.urlList);
        TextView initialPriceList = convertView.findViewById(R.id.initialPriceList);
        TextView currentPriceList = convertView.findViewById(R.id.currentPriceList);
        TextView priceChangeList = convertView.findViewById(R.id.priceChangeList);

        nameList.setText(item.name);
        urlList.setText(item.url);
        initialPriceList.setText("$" + format.format(item.initialPrice));
        currentPriceList.setText("$" + format.format(item.currentPrice));
        priceChangeList.setText(format.format(item.priceChage)+"%");

        return convertView;
    }

    @Override
    public int getCount() {
        return super.getCount();
    }


}
