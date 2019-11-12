package cs4330.cs.utep.edu;

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

}
