package edu.utep.cs.cs4330.mypricewatcher;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import edu.utep.cs.cs4330.mypricewatcher.DTO.Item;
import edu.utep.cs.cs4330.mypricewatcher.DTO.ItemController;

public class CustomDialog extends Dialog implements View.OnClickListener {

    private Activity activity;
    private TextView itemName, itemInitPrice, itemUrl;
    private Button cancelBtn, addBtn;
    private ItemController itemController;

    public CustomDialog(Activity activity, ItemController itemController) {
        super(activity);
        this.activity = activity;
        this.itemController = itemController;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_layout);

        itemName = findViewById(R.id.itemName);
        itemInitPrice = findViewById(R.id.itemInitPrice);
        itemUrl = findViewById(R.id.ItemURL);
        cancelBtn = findViewById(R.id.CancelButton);
        addBtn = findViewById(R.id.AddButton);

        cancelBtn.setOnClickListener(this);
        addBtn.setOnClickListener(this);

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.AddButton:
                //we are going to add the element to the listview
                double price = Double.valueOf(String.valueOf(itemInitPrice.getText()));
                Item item = new Item(itemName.getText().toString(), "https://" +
                        itemUrl.getText().toString(),
                        price,
                        price, 0.0);
                itemController.addItem(item);
            case R.id.CancelButton:
                //we are going to cancel the dialog
                dismiss();
        }
    }
}
