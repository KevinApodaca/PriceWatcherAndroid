package cs4330.cs.utep.pricewatcher.view;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.ArrayList;
import java.util.List;

import cs4330.cs.utep.pricewatcher.R;
import cs4330.cs.utep.pricewatcher.model.ListAdapter;
import cs4330.cs.utep.pricewatcher.model.Product;

public class MainActivity extends AppCompatActivity implements NewProductDialogActivity.NewProductDialogListener,
        EditProductDialogActivity.EditProductDialogListener, ListAdapter.Listener {

    private static List<Product> listOfItems = new ArrayList<>();
    SwipeRefreshLayout swipeRefreshLayout;
    private Product product1 = new Product("https://www.amazon.com/ZAHIUS-Airpods-Silicone-Compatible-Cartoon/dp/B07WBYR6CN/ref=sr_1_22?keywords=snorlax+bean+bag&qid=1571594574&sr=8-22", "Snorlax Airpods", 10.99, 10.99, 0.00);
    private Product product2 = new Product("https://www.ebay.com/itm/Funko-Pop-Marvel-Thanos-Snap-6-Inch-PX-Exclusive-Metallic-with-Comic/174068406591?hash=item288749193f:m:mJvCymReLbOEqPtFw63fo1Q", "Thanos Funko Pop", 29.99, 29.99, 0.00);
    private ListView productView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);

        //Set the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        productView = findViewById(R.id.listView);
        if (listOfItems.size() <= 0) {
            listOfItems.add(product1);
            listOfItems.add(product2);
        }
        renewList();
        swipeRefreshLayout.setOnRefreshListener(() -> {
            for (int i = 0; i < listOfItems.size(); i++) {
                listOfItems.get(i).refreshPrice();
            }
            renewList();
            swipeRefreshLayout.setRefreshing(false);
        });
        handleShare(getIntent());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    private void renewList() {
        ListAdapter listAdapter = new ListAdapter(this, listOfItems);
        productView.setAdapter(listAdapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                for (int i = 0; i < listOfItems.size(); i++) {
                    listOfItems.get(i).refreshPrice();
                }
                renewList();
                return true;
            case R.id.action_add:
                openNewProductDialog(null);
                return true;
            case R.id.openAmazon:
                toBrowser("https://www.amazon.com");
                return true;
            case R.id.openEbay:
                toBrowser("https://www.ebay.com");
                return true;
            case R.id.openWalmart:
                toBrowser("https://www.walmart.com");
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void openNewProductDialog(String sharedText) {
        NewProductDialogActivity dialog = new NewProductDialogActivity();
        if (sharedText != null) {
            Bundle bundle = new Bundle();
            bundle.putString("url", sharedText);
            dialog.setArguments(bundle);
        }
        dialog.show(getSupportFragmentManager(), "New item added");
    }

    private void editProductDialog(int index) {
        EditProductDialogActivity dialog = new EditProductDialogActivity();
        Bundle bundle = new Bundle();
        bundle.putInt("index", index);
        bundle.putString("currentName", listOfItems.get(index).getName());
        bundle.putString("currentUrl", listOfItems.get(index).getURL());
        bundle.putString("currentPrice", String.valueOf(listOfItems.get(index).getInitialPrice()));
        dialog.setArguments(bundle);
        dialog.show(getSupportFragmentManager(), "Edit item");
    }

    @Override
    public void addProduct(String name, String url, String price) {
        listOfItems.add(new Product(url, name, Double.parseDouble(price), Double.parseDouble(price), 0.00));
        renewList();
    }

    @Override
    public void updateProduct(String name, String url, String price, int index) {
        listOfItems.get(index).setName(name);
        listOfItems.get(index).setURL(url);
        listOfItems.get(index).setInitialPrice(Double.parseDouble(price));
        renewList();
    }

    public void deleteProduct(int index) {
        listOfItems.remove(index);
        renewList();
    }

    public void editProduct(int index) {
        editProductDialog(index);
    }

    public void toBrowser(String url) {
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        builder.addDefaultShareMenuItem();
        CustomTabsIntent customTabsIntent = builder.build();
        customTabsIntent.launchUrl(this, Uri.parse(url));
    }

    public void openProductURL(int index, boolean isInternal) {
        if (isInternal) {
            toBrowser(listOfItems.get(index).getURL());
        } else {
            shouldOverrideUrlLoading(listOfItems.get(index).getURL());
        }
    }

    public void shouldOverrideUrlLoading(String url) {
        Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(i);
    }

    @Override
    public void refreshProduct(int index) {
        listOfItems.get(index).refreshPrice();
        renewList();
    }

    private void handleShare(Intent intent) {
        String action = intent.getAction();
        String type = intent.getType();
        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if ("text/plain".equals(type)) {
                String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
                if (sharedText != null) {
                    openNewProductDialog(sharedText);
                }
            }
        }
    }
}
