package edu.utep.cs.cs4330.mypricewatcher;

//imports
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Random;

import edu.utep.cs.cs4330.mypricewatcher.DTO.Item;
import edu.utep.cs.cs4330.mypricewatcher.DTO.ItemController;
import edu.utep.cs.cs4330.mypricewatcher.DTO.ItemModel;
import edu.utep.cs.cs4330.mypricewatcher.DTO.PriceFinder;

/**
 * This class will be our Main activity that handles everything related to views.
 */
public class MainActivity extends AppCompatActivity {

    private ItemController itemController;
    private ListView listView;
    private FloatingActionButton floatingActionButton;
    private CustomAdapter listViewAdapter;
    private CustomDialog dialog;
    TextView currentPrice;
    private PriceFinder priceFinder;
    private ItemModel model;
    double current = createRandom();

/**
 * This method will create the views.
 * @param savedInstanceState - the saved instance.
 */
    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        itemController = new ItemController(new ItemModel(), this);
        listViewAdapter = new CustomAdapter(this, android.R.layout.simple_list_item_1, new ArrayList<Item>());
        this.dialog = new CustomDialog(this, itemController);

        listView = findViewById(R.id.list1);
        listView.setAdapter(listViewAdapter);

        currentPrice = findViewById(R.id.currentPriceLabelList);
        floatingActionButton = findViewById(R.id.add);

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            /**
             * Method will show the dialog.
             * @param view - the current view.
             */
            @Override
            public void onClick(View view) {
                dialog.show();
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            /**
             * Method will handle the item click action.
             * @param adapterView - the adapter view.
             * @param view - the view.
             * @param i - an integer.
             * @param l - an integer.
             */
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Item selectedItem = listViewAdapter.getItem(i);
                    PopupMenu pop = new PopupMenu(MainActivity.this, view);
                    pop.inflate(R.menu.menu);
                    pop.show();

                    pop.setOnMenuItemClickListener(item -> {
                                switch (item.getItemId()) {
                                    case R.id.popDelete:
                                        itemController.removeItem(selectedItem);
                                        listViewAdapter.notifyDataSetChanged();
                                        return true;

                                    case R.id.popURL:
                                        Intent intent = new Intent(getApplicationContext(), WebViewActivity.class);
                                        intent.putExtra("url", selectedItem.getUrl());
                                        startActivity(intent);
                                        return true;

                                    case R.id.popEdit:
                                        dialog.show();
                                        return true;

                                    case R.id.popupdate:
                                        itemController.updatePrice(i);

                                    default:
                                        return false;
                                }
                                });
                /*
                Log.d("TESTING", "Item name selected: "+selectedItem.priceChage);
                */

            }

        });

        itemController.addItem(new Item("Airpods", "https://www.amazon.com/ZAHIUS-Airpods-Silicone-Compatible-Cartoon/dp/B07WBYR6CN/ref=sr_1_22?keywords=snorlax+bean+bag&qid=1571594574&sr=8-22", 10,90,0));

        itemController.updateView();
    }
/**
 * Method will handle the displaying of the item.
 * @param name - the name of the item.
 * @param iniPrice - the initial price.
 * @param url - the item url.
 * @param changePrice - how much the price changed
 * @param currPrice - the current price.
 */
    public void displayItem(String name, double iniPrice, String url, double changePrice, double currPrice){

        double current = createRandom();
        double changeInPrice = (currPrice - iniPrice)/iniPrice;
        listViewAdapter.add(new Item(name, url, iniPrice, currPrice, changeInPrice));
        listViewAdapter.notifyDataSetChanged();
    }
/**
 * Method will clear all item views.
 */
    public void clearItems(){
        listViewAdapter.clear();
    }
/**
 * Method will generate a random price.
 * @return the price.
 */
    public double createRandom() {
        double min = 250;
        double max = 400;
        Random random = new Random();
        double holder = min + (max - min) * random.nextDouble();
        return Math.round(holder * 100.00) / 100.0;
    }
}

