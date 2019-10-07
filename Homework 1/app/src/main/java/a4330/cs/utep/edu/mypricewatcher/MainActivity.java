/*
* CS 4330 Mobile Application Development
* @author Kevin Apodaca
* @since 9/28/19
*
* In this assignment, you are to write the first version of My Price
Watcher app that tracks "simulated" prices of a single, fixed item.
Your app has to meet the following functional and non-functional
requirements.

R1. The app shall show the name of an item, its initial price, current
    price, and the percentage change of the prices (see R4 below to
    simulate the prices of an item). You may assume that the app knows
    a single item to watch over the price; you don't have to provide a
    UI for the user to enter the item information such as name and
    URL.

R2. The app shall provide a way to find the current price of the item
    and calculate a new price change. You may use a button for this.

R3. The app shall provide a way to view the webpage of the item, e.g.,
    starting a built-in web browser with the item's URL.

R4. You should define a class, say PriceFinder, to simulate the price
    of an item. Given the URL of an item, the class returns a
    "simulated" price of the item, e.g., by generating a random or
    normally-distributed price. The idea is to apply the Strategy
    design pattern [1] in later assignments by introducing a subclass
    that actually downloads and parses the Web document of the given
    URL to find the price.

R5. You should use the Model-View-Control (MVC) metaphor [2], and your
    model classes should be completely separated from the view and
    control classes. There should be no dependency from model classes
    to view/control classes to make your design extensible for future
    improvements.

R6. The app shall provide a custom launch icon. Use the Image Asset
    Studio of Android Studio to create custom icons (File > New >
    Image Asset). Read an online document entitled "Create App Icons
    with Image Asset Studio" (https://developer.android.com/studio/
    write/image-asset-studio.html) or an online tool like Android
    Asset Studio (https://romannurik.github.io/AndroidAssetStudio/).
* */

package a4330.cs.utep.edu.mypricewatcher;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    Product product = new Product("https://www.bestbuy.com/site/nintendo-switch-32gb-console-neon-red-neon-blue-joy-con/6364255.p?skuId=6364255", "Nintendo Switch Red/Blue 32 GB", 309.99, 0.0, 123.99);
    // The views that the app will be using.
    TextView productName;
    TextView initialPrice;
    TextView priceChange;
    TextView currentPrice;
    TextView url;
    Button refreshButton;
    Button webButton;

    /**
     * This method will initialize the buttons and views of the application, as well as handling the user inputs.
     * @param savedInstanceState - this is the instance of saved.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        productName = findViewById(R.id.nameString);
        currentPrice = findViewById(R.id.currentDouble);
        priceChange = findViewById(R.id.changeDouble);
        initialPrice = findViewById(R.id.initialDouble);
        url = findViewById(R.id.urlString);
        webButton = findViewById(R.id.openWeb);
        productName.setText(product.getProductName());
        // the initial values of the product. Including initial price, price change, and the current price.
        initialPrice.setText("$" + String.valueOf(product.getInitialPrice()));
        currentPrice.setText("$" + String.valueOf(product.getCurrentPrice()));
        priceChange.setText("$" + String.valueOf(product.getPriceChange()));
        url.setText(product.getURL()); // the url of the product.

        refreshButton = findViewById(R.id.refresh);
        refreshButton.setOnClickListener((view) -> onRefresh());
        webButton.setOnClickListener((view) -> onClickGoProduct());

    }

    /**
     * This method will be used to change the price of the item to a random price. Will be updated later for working with the actual price.
     */
    protected void onRefresh() {

        String changedPrice; // by how much.

        PriceFinder price = new PriceFinder();
        product.checkPrice(price.getRandomPrice(product.getCurrentPrice()));
        currentPrice.setText(String.valueOf(product.getCurrentPrice()));
        // price dropped on refresh.
        if(product.getPriceChange() > 0) {
            changedPrice = "-$" + Math.abs(product.getPriceChange());
        }
        // price increased on refresh.
        else {
            changedPrice = "+$" + Math.abs(product.getPriceChange());
        }
        // this will display the text of the changed price.

        this.priceChange.setText(changedPrice);
    }

    /**
     * This will launch the browser button and load the item's URL using an Intent.
     */
    protected void onClickGoProduct() {
        Intent launch = new Intent(getBaseContext(), WebActivity.class);
        launch.putExtra("item url", product.getURL());
        startActivity(launch);
    }
}
