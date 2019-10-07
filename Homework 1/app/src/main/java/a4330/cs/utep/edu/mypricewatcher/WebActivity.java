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

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class WebActivity extends AppCompatActivity {

    /**
     * This method will be used to process the action of when the user decides to open the web browser.
     * @param savedInstanceState - the app before the button was clicked.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);
        WebView webView = findViewById(R.id.browserAndroid);
        webView.setWebViewClient(new WebViewClient());
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webView.loadUrl(getIntent().getStringExtra("item url"));
    }
}
