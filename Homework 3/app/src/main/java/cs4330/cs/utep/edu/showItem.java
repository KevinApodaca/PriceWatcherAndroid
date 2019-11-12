/**
 * CS 4330 Homework 3
 * @author Kevin Apodaca, Wan Koo
 * @version 3.0
 * @since 11/10/19
 * In this assignment, you are to extend your HW2 code and create the
ultimate version of the My Price Watcher app that supports network and
data persistence. Your app shall meet all the relevant requirements
from the previous homework assignments (HW1 and HW2) as well as the
following new ones.

R1. The app shall find the price of a watched item from the item's Web
    page. Remember that the URL of an item is provided by the user
    when the item is added to the watch list.
    
    a. Your app shall inform the user if the price of an item can't be
       found (e.g., malformed or non-existing URL).

    b. Your app shall support item pages from the following websites:
       - Home Depot (www.homedepot.com) and
       - Lowe's (www.lowes.com) or at least two online stores of your 
         choice

R2. The app shall persist watched items. Use a SQLite database to
    store watched items. For bonus points, consider using a
    CursorAdapter to provide data to the UI (a ListView).

R3. The app shall be aware of the current network status: on/off. If
    Wifi is off, it shall inform the user and direct to the built-in
    Network (Wifi) Setting app to enable it.

R4. You should separate network and database operations into separate
    modules (or classes) to decouple them from the rest of the code.

R5. Use the following configuration for your project.
 */

package cs4330.cs.utep.edu;
// imports
import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;

import cs4330.cs.utep.edu.models.ItemManager;
import cs4330.cs.utep.edu.models.PriceFinder;

/**
 * Class will use the fragments to display the items that are inserted into the list view.
 */
public class showItem extends FragmentActivity {
    TextView itemTitle;
    TextView newPrice;
    TextView oldPrice;
    TextView diff;
    EditText itemUrl;

    Button checkPrice;
    Button openWebpage;
    Button editItem;
    Button deleteItem;

    ImageView imageView;

    private PriceFinder item;
    private ItemManager itm;
    private Gson gson;
    private String FILE_NAME = "items.json";
    private int position;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @SuppressLint({"SetTextI18n", "WrongViewCast"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_detail);

        DecimalFormat f = new DecimalFormat("##.00");

        /** Mapping the button to the View on the R file. */
        this.itemTitle = (TextView) findViewById(R.id.textViewNameItem);
        this.oldPrice = (TextView) findViewById(R.id.textViewInitialPriceItem);
        this.newPrice = (TextView) findViewById(R.id.textViewCurrentPriceItem);
        this.itemUrl = (EditText) findViewById(R.id.editTextSourceItem);
        this.checkPrice = (Button) findViewById(R.id.buttonReloadItem);
        this.diff = (TextView) findViewById(R.id.textViewPriceChangeItem);
        this.openWebpage = (Button) findViewById(R.id.btnWebItem);
        this.editItem = (Button) findViewById(R.id.btnEditItem);
        this.deleteItem = (Button) findViewById(R.id.btnDeleteItem);
        this.imageView = (ImageView) findViewById(R.id.imageView);

        openWebpage.setOnClickListener(this::WebClicked);
        editItem.setOnClickListener(this::editClicked);
        deleteItem.setOnClickListener(this::deleteClicked);

        this.gson = new Gson();
        this.itm = new ItemManager();
        String itemDataAsString = getIntent().getStringExtra("itemDataAsString");

        ArrayList<PriceFinder> tmp = new ArrayList<PriceFinder>();
        tmp = gson.fromJson(itemDataAsString, new TypeToken<ArrayList<PriceFinder>>(){}.getType());
        tmp.forEach(x -> {
            this.itm.addItem(x);
        });

        this.position = getIntent().getIntExtra("position", 0);

        this.itemTitle.setText(this.itm.getItem(position).getName());
        String op = String.valueOf(f.format(this.itm.getItem(position).getPrice()));

        this.oldPrice.setText("Initial price: $" + op);
        this.newPrice.setText("Current Price: $" + String.valueOf(f.format(this.itm.getItem(position).getNewPrice())));
        this.itemUrl.setText(this.itm.getItem(position).getUrl());
        diff.setText("Price change: " +f.format(this.itm.getItem(position).calculatePrice())+"%");

        if(this.itm.getItem(position).getImage().contains("jpg") || this.itm.getItem(position).getImage().contains("png")){
            Picasso.get().load(this.itm.getItem(position).getImage()).into(imageView);
        }

        itemUrl.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                itm.getItem(position).setLink(s.toString());
                try {
                    save();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            public void afterTextChanged(Editable s) {}});


        checkPrice.setOnClickListener( view -> {
            this.itm.getItem(position).randomPrice();
            this.newPrice.setText("Current price: $" + f.format(this.itm.getItem(position).getNewPrice()));
            String s;

            if(this.itm.getItem(position).changePositive()) {
                diff.setTextColor(Color.rgb(200, 0, 0));
                s = "+";
            }
            else {
                diff.setTextColor(Color.rgb(0,200,0));
                s = "-";
            }
            diff.setText("Price change: " + s + f.format(this.itm.getItem(position).calculatePrice())+"%");
            try {
                save();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Method will be used to handle the views for when a user decides to visit the webpage of an item.
     */
    protected void WebClicked(View view){
        String url = this.itm.getItem(position).getUrl();
        if (!url.startsWith("http://") && !url.startsWith("https://"))
            url = "http://" + url;
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        try {
            startActivity(browserIntent);
        } catch (ActivityNotFoundException e){
             Toast.makeText(getBaseContext(), "Webpage " + url + "does not exist", Toast.LENGTH_SHORT).show();
        }
    }

    //TODO - connect with delete method
    protected void deleteClicked(View view){
        PriceFinder pf = new PriceFinder();
        pf = this.itm.getItem(this.position);
        this.itm.removeItem(pf);
        try {
            save();
        } catch (IOException e) {
            e.printStackTrace();
        }
        finish();
    }

    //TODO - connect with edit method
    protected void editClicked(View view){
        FragmentManager fm = getSupportFragmentManager();
        ЕditDialog editDialogFragment = new ЕditDialog(2);
        Bundle args = new Bundle();
        args.putInt("position", this.position);
        args.putString("itemName", this.itm.getItem(this.position).getName());
        args.putString("itemUrl", this.itm.getItem(this.position).getUrl());
        editDialogFragment.setArguments(args);
        editDialogFragment.show(fm, "edit_item");
    }

    public void editItem(String name, String source, int position, String image, String id){
        PriceFinder pf = this.itm.getItem(position);
        this.itm.editItem(pf, pf.getPrice(), name, source, image);
        try {
            save();
        } catch (IOException e) {
            e.printStackTrace();
        }

        DecimalFormat f = new DecimalFormat("##.00");

        this.itemTitle.setText(pf.getName());
        String op = String.valueOf(f.format(pf.getPrice()));

        this.oldPrice.setText("Initial price: $" + op);
        this.newPrice.setText("Current Price: $" + String.valueOf(f.format(pf.getNewPrice())));
        this.itemUrl.setText(this.itm.getItem(position).getUrl());
        diff.setText("Price change: " +f.format(pf.calculatePrice())+"%");
    }



    public void save() throws IOException {
        FileOutputStream fos = null;
        String jsonSerial = this.gson.toJson(this.itm.getList());

        try{
            fos = openFileOutput(FILE_NAME, MODE_PRIVATE);
            fos.write(jsonSerial.getBytes());
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        } 
        catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                fos.close();
            }
        }
    }

}
