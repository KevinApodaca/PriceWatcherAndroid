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

//imports 
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

import cs4330.cs.utep.edu.models.DatabaseHelper;
import cs4330.cs.utep.edu.models.ItemManager;
import cs4330.cs.utep.edu.models.PriceFinder;

public class  MainActivity extends AppCompatActivity implements DeleteDialog.DeleteDialogListener {

    private DatabaseHelper appDb;
    public ItemManager itm;
    public PriceFinderAdapter itemAdapter;
    private String FILE_NAME = "items.json";
    private Gson gson = new Gson();
    private String jsonText;
    private EditText filter;
    private ProgressBar progressBar;
    ListView itemsList;

    @RequiresApi(api = Build.VERSION_CODES.N)
    /**
     * Creating the instance by overriding onCreate
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /** The variables we will be using through the activity. */
        appDb                      = new DatabaseHelper(this);
        String text                = null;
        ArrayList<PriceFinder> tmp = new ArrayList<PriceFinder>();
        this.filter                = findViewById(R.id.searchFilter);
        this.itm                   = new ItemManager();

        // Set visibility of search bar off from the beginning
        this.filter.setVisibility(View.GONE);

        // Check network connection
        if (isNetworkOn()) {
             Toast.makeText(getBaseContext(), "Welcome", Toast.LENGTH_SHORT).show();
        } else {
             showNetWorkDialog();
             Toast.makeText(getBaseContext(), "You are Offline", Toast.LENGTH_SHORT).show();
        }

        // Load into text "items.json" string (If exist)
        if(!isNetworkOn()) {

            try {
                text = load();
            } catch (IOException e) {
                e.printStackTrace();
            }

            this.itemsList = findViewById(R.id.items_list);

            // Convert text (json text) into objects to load into the adapter list
            if (text != null) {
                tmp = gson.fromJson(text, new TypeToken<ArrayList<PriceFinder>>() {
                }.getType());
                tmp.forEach(x -> {
                    this.itm.addItem(x);
                });
            }
        } else {
            this.itemsList = findViewById(R.id.items_list);
            this.itm = loadFromDB();
        }

        //Log.i("Name", this.itm.getItem(0).getName());

        this.itemAdapter = new PriceFinderAdapter(this, this.itm.getList());
        this.itemsList.setAdapter(itemAdapter);
        this.itemsList.setTextFilterEnabled(true);

        // Create search filter
        filter.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                itemAdapter.getFilter().filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        String action = getIntent().getAction();
        String type   = getIntent().getType();
        if (Intent.ACTION_SEND.equalsIgnoreCase(action) && type != null && ("text/plain".equals(type))){
            String url = getIntent().getStringExtra(Intent.EXTRA_TEXT);
            FragmentManager fm = getSupportFragmentManager();
            AddDialog addDialogFragment = new AddDialog();
            Bundle args = new Bundle();
            args.putString("url", url);
            addDialogFragment.setArguments(args);
            addDialogFragment.show(fm, "add_item");
        }

        registerForContextMenu(itemsList);
        itemsList.setOnCreateContextMenuListener(this);

        itemsList.setOnItemClickListener((arg0, arg1, position, arg3) -> {
            itemClicked(position);
        });
        itemAdapter.notifyDataSetChanged();
        itemAdapter.setNotifyOnChange(true);

    }

    public void doAsync(String url){
        if(!isNetworkOn()){
            showNetWorkDialog();
        }
        if(isNetworkOn()) {
            AddItemSync ais = new AddItemSync();
            ais.setUrl(url);
            ais.execute();
        } else{
            Toast.makeText(getBaseContext(), "Need Internet to work!", Toast.LENGTH_LONG).show();

        }

    }

    //================================================================================
    // Async
    //================================================================================

    public class AddItemSync extends AsyncTask<Void, Void, Void> {

        String stringPrice = null;
        String name = null;
        String url = "";
        Boolean error = false;
        String image = null;
        ProgressDialog pd = new ProgressDialog(MainActivity.this);

        public void setUrl(String url) {
            this.url = url;
        }

        public String getDomainName(String url) throws URISyntaxException {
            URI uri = new URI(url);
            String domain = uri.getHost();
            return domain.startsWith("www.") ? domain.substring(4) : domain;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            this.pd.setMessage("loading");
            this.pd.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                switch(getDomainName(this.url)){
                    case "neimanmarcus.com":
                        Document doc = Jsoup.connect(this.url).userAgent("Opera").get();
                        Element price = doc.select(".retailPrice").first();
                        this.name = doc.title();
                        this.stringPrice = price.text();
                        String filter1 = this.stringPrice.replace("$", "");
                        this.stringPrice = filter1.replace(",","");
                        Elements img = doc.select("div.slick-slide img");
                        int c = 0;

                        for(Element e : img){
                            if(c == 0){
                                this.image = (e.attr("src"));
                                break;
                            }
                            c++;
                        }

                        this.image = this.image.replace("//","");
                        this.image = "http://"+this.image;
                        break;

                    case "racquetballwarehouse.com":
                        Document doc2 = Jsoup.connect(this.url).userAgent("Opera").get();
                        Element price2 = doc2.select(".commanum").first();
                        this.name = doc2.title();

                        Elements img2 = doc2.select("img.mainimage");

                        for(Element e : img2){
                            this.image = (e.attr("src"));
                        }

                        this.stringPrice = price2.text();
                        this.name = doc2.title();

                        break;

                    case "homedepot.com":
                        Document doc3 = Jsoup.connect(this.url).timeout(0)
                                .userAgent("Mozilla/5.0 (Linux; U; Android 4.0.3; ko-kr; LG-L160L Build/IML74K) AppleWebkit/534.30 (KHTML, like Gecko) Version/4.0 Mobile Safari/534.30")
                                .cookie("zip", "79902")
                                .get();

                        StringBuilder sb = new StringBuilder();
                        int counter = 0;
                        Elements priceParts = doc3.select("#ajaxPrice span");
                        this.stringPrice = "";
                        for(Element e1: priceParts){
                            if(counter > 0){
//                                sb.append(e1.text());
                                this.stringPrice += e1.text();
                            }
                            if(counter == 1) {
                                this.stringPrice += ".";
                            }
                            counter++;
                        }
                        System.out.println(this.stringPrice);
                        this.name = doc3.title();

                        Elements img3 = doc3.select("img#mainImage");
                        for(Element e3 : img3) {
                            this.image = (img3.attr("src"));
                        }
                    break;

                    default:
                        this.error = true;
                        break;
                }

            } catch (IOException e) {
                e.printStackTrace();
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void s) {
            super.onPostExecute(s);
            pd.dismiss();

//            Log.i("NAME", this.name);
//            Log.i("PRICE", this.stringPrice);
//            Log.i("IMAGE", this.image);

            if(this.url == null || this.stringPrice == null || this.name == null || this.image == null){
                Toast.makeText(getBaseContext(), "Request Timeout", Toast.LENGTH_LONG).show();
            } else {
                if(this.error){
                    Toast.makeText(getBaseContext(), "Store not supported yet! Or Invalid URL", Toast.LENGTH_LONG).show();
                }else {
                    addItem(this.url, Double.valueOf(this.stringPrice), this.name, this.image);
                }
            }


        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

    }

    /**
     * Method to get which PriceItem object (item) gets clicked in the ListView
     * @param position
     */
    public void itemClicked(int position){
        Intent itemIntent = new Intent(this, showItem.class);
        String itemDataAsString = gson.toJson(itm.getList()); // Serialize Object to pass it
        itemIntent.putExtra("position", position);
        itemIntent.putExtra("itemDataAsString", itemDataAsString);
        startActivity(itemIntent);
    }

    /**
     * This method is on Android's API, which on restoring the app (if closed or paused) will
     * reload and/or load
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onResume() {
        super.onResume();
        Log.i("Resumend", "Done!");
        String text = null;
        itm.clear();
        ArrayList<PriceFinder> tmp = new ArrayList<PriceFinder>();

        try {
            text = load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(text != null){
            tmp = gson.fromJson(text, new TypeToken<ArrayList<PriceFinder>>(){}.getType());
            tmp.forEach(x -> {
                itm.addItem(x);
            });
        }
        this.itemAdapter.notifyDataSetChanged();
    }

    //================================================================================
    // Menus and UI methods
    //================================================================================

    /**
     * Method to create context menu (General)
     * @param menu
     * @param v
     * @param menuInfo
     */
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.context_menu, menu);
    }

    /**
     * Method to create options menu
     * @param menu of the app
     * @return true if everything loads correctly
     */
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu,menu);
        return true;
    }

    /**
     * Method to create context menu on selected item on ListView
     * @param item selected
     * @return if input exist true
     */
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int itemPosition = info.position;
        switch (item.getItemId()) {
            case R.id.edit_item:
                showEditDialog(itemPosition);
                return true;
            case R.id.delete_item:
                showDeleteDialog(itemPosition);
                return true;
            case R.id.reload_item:
                setValues(itemPosition);
                return true;
            case R.id.open_detail:
                itemClicked(itemPosition);
                return true;
            case R.id.webpage_item:
                String url = itm.getItem(itemPosition).getUrl();
                if (!url.startsWith("http://") && !url.startsWith("https://"))
                    url = "http://" + url;
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                try {
                    startActivity(browserIntent);
                } catch (ActivityNotFoundException e){
                   // Toast.makeText(getBaseContext(), "Webpage " + url + "does not exist", Toast.LENGTH_SHORT).show();
                }
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    /**
     * Menu of options
     * @param item clicked
     * @return true if option exist, false if don't
     */
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.search:
                toggleSearchField();
                return true;
            case R.id.add:
                showAddDialog();
                return true;
            case R.id.reload:
                reloadItems();
                return true;
            case R.id.settings:
                Toast.makeText(getBaseContext(), "TBD", Toast.LENGTH_SHORT).show();
                return true;
        }
        return false;
    }

    /**
     * This method will toggle the EditText field for searching on the ListView of the Items
     */
    public void toggleSearchField() {
        if(this.filter.getVisibility() == View.VISIBLE) {
            this.filter.setVisibility(View.GONE);
        }else{
            this.filter.setVisibility(View.VISIBLE);
        }
    }

    //================================================================================
    // Item management | CRUD
    //================================================================================

    /**
     * Add PriceFinder into ItemManager and save it into "items.json"
     * @param source link of the PriceFinder Object
     *
     */
    public void addItem(String source, double price, String name, String image) {
        Log.i("SOURCE", source);

        appDb.insertData(name, source, price, 0.0, image);

        PriceFinder pf = new PriceFinder(name, source, price, image, appDb.lastId());
        itm.addItem(pf);

        try {
            save();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.itemAdapter.addItem(pf);
        this.itemAdapter.notifyDataSetChanged();
    }

    /**
     * Updates PriceFinder object in ItemManager
     * @param name of the PriceFinder Item to be updated
     * @param source Link of the PriceFinder Item to be updated
     * @param position position of PriceFinder in ItemManager to be updated
     * @param image Link of image of the PriceFinder Item to be updated
     */
    public void editItem(String name, String source, int position, String image, String id){
        PriceFinder pf = this.itm.getItem(position);
        appDb.edit(id, name, source);
        this.itm.editItem(pf, pf.getPrice(), name, source, image);
        try {
            save();
        } catch (IOException e) {
            e.printStackTrace();
        }

        itemAdapter.editItem(position, name, source);
        itemAdapter.notifyDataSetChanged();
    }

    /**
     * Creates a new price in Pricefinder object in ItemManager and saves it in JSON file
     * @param position of Pricefinder Object in  ItemManager
     */
    private void setValues(int position){
        itm.getItem(position).randomPrice();
        try {
            save();
        } catch (IOException e) {
            e.printStackTrace();
        }
        itemAdapter.notifyDataSetChanged();
    }

    /**
     * Reload the prices of all PriceFinder objects
     */
    private void reloadItems(){
        for(int i = 0; i < itemAdapter.getSize(); i++){
            setValues(i);
        }
    }

    //================================================================================
    // Dialogs
    //================================================================================

    /**
     * Pops a dialog for delete a PriceFinder Object
     * @param position in the PriceFinder in ItemManager(ArrayList)
     */
    public void showDeleteDialog(int position){
        FragmentManager fm = getSupportFragmentManager();
        DeleteDialog deleteDialogFragment = new DeleteDialog();
        Bundle args = new Bundle();
        args.putInt("position", position);
        deleteDialogFragment.setArguments(args);
        deleteDialogFragment.show(fm, "delete_item");
    }

    /**
     * Shows a dialog to add a PriceFinder object and adds it to ItemManager
     */
    public void showAddDialog(){
        FragmentManager fm = getSupportFragmentManager();
        AddDialog addDialogFragment = new AddDialog();
        addDialogFragment.show(fm, "add_item");
    }

    public void showNetWorkDialog(){
        FragmentManager fm = getSupportFragmentManager();
        WifiDialog addDialogFragment = new WifiDialog();
        addDialogFragment.show(fm, "wifi_dialog");
    }

    /**
     * Pops a dialog to edit PriceFinder properties (Name, URL)
     * @param position in the arraylist ItemManager
     */
    public void showEditDialog(int position){
        FragmentManager fm = getSupportFragmentManager();
        ЕditDialog editDialogFragment = new ЕditDialog(1);
        Bundle args = new Bundle();
        args.putInt("position", position);
        args.putString("itemUrl", itm.getItem(position).getUrl());
        args.putString("itemName", itm.getItem(position).getName());
        args.putString("id", itm.getItem(position).getId());
        editDialogFragment.setArguments(args);
        editDialogFragment.show(fm, "edit_item");
    }

    /**
     * Pops a dialog for delete a PriceFinder Object
     * @param position in the PriceFinder in ItemManager(ArrayList)
     */
    public void DeleteItemDialog(int position){
        PriceFinder pf = new PriceFinder();
        pf = this.itm.getItem(position);
        appDb.delete(pf.getId());
        this.itm.removeItem(pf);
        try {
            save();
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.itemAdapter.removeItem(position);
        itemAdapter.notifyDataSetChanged();
    }

    //================================================================================
    // Write & Load offline information
    //================================================================================

    /**
     * Saves the instance of the Itemmanager (items) into a JSON file "items.json"
     * @throws IOException
     */
    private void save() throws IOException {
        FileOutputStream fos = null;
        String jsonSerial = this.gson.toJson(this.itm.getList());

        try{
            fos = openFileOutput(FILE_NAME, MODE_PRIVATE);
            fos.write(jsonSerial.getBytes());
        }catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                fos.close();
            }
        }
    }

    /**
     * Loads "items.json" into a String format. Reads the file and convert JSON to a String
     * @return JSON from "items.json" in String format
     * @throws IOException
     */
    private String load() throws IOException {
        FileInputStream fis = null;

        try {
            fis = openFileInput(FILE_NAME);
        } catch(FileNotFoundException e){
            Log.d("File not found", FILE_NAME);
            return null;
        }
        InputStreamReader isr =  new InputStreamReader(fis);
        BufferedReader br = new BufferedReader(isr);
        StringBuilder sb = new StringBuilder();

        while((this.jsonText = br.readLine()) != null) {
            sb.append(this.jsonText);
        }

        return sb.toString();
    }

    private ItemManager loadFromDB(){

        ItemManager itmDb = new ItemManager();
        Cursor response = appDb.fetchAllData();

        Log.i("Count", String.valueOf(response.getCount()));
        if(response.getCount() == 0) {
            Toast.makeText(getBaseContext(), "Add new products", Toast.LENGTH_LONG).show();
        }

        while(response.moveToNext()) {
            PriceFinder tmp = new PriceFinder(response.getString(1), response.getString(3), response.getDouble(2), response.getString(4), response.getString(0));
            Log.i("Added? ", String.valueOf(itmDb.addItem(tmp)));
        }
        return itmDb;
    }

    //================================================================================
    // Network
    //================================================================================

    private Boolean isNetworkOn() {
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);

        return connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED || connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED;

    }

}
