package cs4330.cs.utep.pricewatcher.model;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.text.Html;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.view.menu.MenuPopupHelper;
import androidx.appcompat.widget.PopupMenu;
import androidx.browser.customtabs.CustomTabsIntent;

import java.util.List;
import java.util.Objects;

import cs4330.cs.utep.pricewatcher.R;

public class ListAdapter extends ArrayAdapter<Product> {

    private final List<Product> productList;
    private Listener listener;

    public ListAdapter(Context ctx, List<Product> product) {
        super(ctx, -1, product);
        this.productList = product;
        listener = (Listener) getContext();
    }

    @SuppressLint("RestrictedApi")
    private boolean createPopup(View view, int position) {
        PopupMenu menu = new PopupMenu(Objects.requireNonNull(getContext()), view);
        menu.inflate(R.menu.popup_menu);
        menu.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.popDelete:
                    listener.deleteProduct(position);
                    return true;
                case R.id.popBrowse1:
                    listener.openProductURL(position, true);
                    return true;
                case R.id.popBrowse2:
                    listener.openProductURL(position, false);
                    return true;
                case R.id.popEdit:
                    listener.editProduct(position);
                    return true;
                case R.id.popRefreshProduct:
                    listener.refreshProduct(position);
                default:
                    return false;
            }
        });
        MenuPopupHelper menuHelper = new MenuPopupHelper(getContext(), (MenuBuilder) menu.getMenu(), view);
        menuHelper.setForceShowIcon(true);
        menuHelper.setGravity(Gravity.END);
        menuHelper.show();
        return false;
    }

    private void patternClicked(String url) {
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        CustomTabsIntent customTabsIntent = builder.build();
        customTabsIntent.launchUrl(Objects.requireNonNull(getContext()), Uri.parse(url));
    }

    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView != null ? convertView
                : LayoutInflater.from(parent.getContext())
                .inflate(R.layout.simple_list_product, parent, false);
        //Obtain the product from the given position
        Product product = productList.get(position);


        FrameLayout layout = row.findViewById(R.id.frameLayout);
        //Set actions from long pressing or quick pressing each layout
        layout.setOnLongClickListener((view1) -> createPopup(view1, position));
        layout.setOnClickListener((view1) -> patternClicked(product.getURL()));
        TextView view = row.findViewById(R.id.productNameString);
        view.setText(Html.fromHtml(String.format("<b>%s</b><br>",
                product.getName())));
        TextView view2 = row.findViewById(R.id.description);
        String color = "";
        if (product.getChange() < 0) {
            color = "#008000";
        } else if (product.getChange() > 0) {
            color = "#FF0000";
        }
        view2.setText(Html.fromHtml(String.format("<b>Initial Price:</b> $%s<br>" +
                        "<b>Current Price:</b> $%.2f<br>" +
                        "<b>Change:</b> <font color='%s'>%.2f%%<br></font> " +
                        "<b>Date Added:</b> %s", product.getInitialPrice(),
                product.getCurrentPrice(), color, product.getChange(), product.getDate())));
        return row;
    }

    public interface Listener {
        void deleteProduct(int index);

        void editProduct(int index);

        void openProductURL(int index, boolean isInternal);

        void refreshProduct(int index);
    }
}

