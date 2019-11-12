package cs4330.cs.utep.edu;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import cs4330.cs.utep.edu.models.Item;
import cs4330.cs.utep.edu.models.ItemManager;
import cs4330.cs.utep.edu.models.PriceFinder;

public class PriceFinderAdapter extends ArrayAdapter<PriceFinder> {

    private Context context;
    private ArrayList<PriceFinder> items;
    ArrayList<PriceFinder> tmpItems;
    ArrayList<PriceFinder> suggestions;
    private static LayoutInflater inflater = null;


    public PriceFinderAdapter(@NonNull Context context, /*@LayoutRes*/ ArrayList<PriceFinder> items) {
        super(context, 0, items);
        this.context = context;
        this.items = items;
        this.tmpItems = new ArrayList<PriceFinder>(items);
        this.suggestions = new ArrayList<PriceFinder>(items);
    }

    public void addItem(PriceFinder pf) {
        this.tmpItems.add(pf);
    }

    public void editItem(int position, String name, String url) {
        PriceFinder tmp = this.tmpItems.get(position);
        tmp.setName(name);
        tmp.setLink(url);
    }

    public void removeItem(Integer position) {
        PriceFinder tmp = this.tmpItems.get(position);
        this.tmpItems.remove(tmp);
    }

    @Override
    public Filter getFilter(){
        return PriceFinderFilter;
    }

    @SuppressLint("SetTextI18n") // This was added by the IDE
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;

        DecimalFormat f = new DecimalFormat("##.00");

        if(listItem == null)
            listItem = LayoutInflater.from(context).inflate(R.layout.items_list,parent,false);

        PriceFinder item = items.get(position); // Get current item(object) on the ArrayList of items

        TextView name = (TextView) listItem.findViewById(R.id.itemName);
        name.setText(item.getName().toString());

        TextView price = (TextView) listItem.findViewById(R.id.itemPrice);
        price.setText("$"+f.format(item.getPrice())+" USD");

        String s;
        TextView newPrice = (TextView) listItem.findViewById(R.id.itemPriceNew);

        ImageView iconImage = (ImageView) listItem.findViewById(R.id.productIcon);
        if(item.getImage().contains("jpg") || item.getImage().contains("png")){
            Picasso.get().load(item.getImage()).into(iconImage);
        }

        if(item.changePositive()) {
            newPrice.setTextColor(Color.rgb(200, 0, 0));
            s = "+";
        }
        else {
            newPrice.setTextColor(Color.rgb(0,200,0));
            s = "-";
        }
        newPrice.setText("$" + f.format(item.getNewPrice()) + " USD (" + s + f.format(item.calculatePrice())+"%)");

        return listItem;

    }

    private Filter PriceFinderFilter = new Filter() {

        /**
         * Method will do a search of the items (PriceFinder object name) and add them into a
         * temporal arraylist that will only contain objects that are related to users serach
         * @param constraint
         * @return FilterResults which contains the different results of the serach
         */
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            if (constraint != null) {
                suggestions.clear();
                for (PriceFinder pf : tmpItems) {
                    String filterPattern = constraint.toString().toLowerCase().trim();
                    if (pf.getName().toLowerCase().contains(filterPattern)) {
                        suggestions.add(pf);
                    }
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = suggestions;
                filterResults.count = suggestions.size();
                return filterResults;
            } else {
                return new FilterResults();
            }

        }

        /**
         * Method to publish the filtered results of the search on the Arraylist of PriceFinder objects
         * @param constraint
         * @param results of the filtered search
         */
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            ArrayList<PriceFinder> p = (ArrayList<PriceFinder>) results.values;

            if (results.count > 0) {
                clear();
                for (PriceFinder pf : p) {
                    add(pf);
                    notifyDataSetChanged();
                }
            } else {
                clear();
                notifyDataSetChanged();
            }

        }
    };


    public int getSize() {
        return items.size();
    }
}
