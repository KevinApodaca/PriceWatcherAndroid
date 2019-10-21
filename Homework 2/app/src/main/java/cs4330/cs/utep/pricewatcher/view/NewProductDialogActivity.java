package cs4330.cs.utep.pricewatcher.view;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatDialogFragment;

import java.util.Objects;

import cs4330.cs.utep.pricewatcher.R;

public class NewProductDialogActivity extends AppCompatDialogFragment {

    //Text fields displayed in the dialog
    private EditText productName;
    private EditText productURL;
    private EditText productPrice;
    //Listener used to call methods inside an activity
    private NewProductDialogListener listener;

    @Override
    public Dialog onCreateDialog(Bundle saveInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = Objects.requireNonNull(getActivity()).getLayoutInflater();
        @SuppressLint("InflateParams") View view = inflater.inflate(R.layout.activity_new_product_dialog, null);
        productName = view.findViewById(R.id.editNameString);
        productURL = view.findViewById(R.id.editURLString);
        productPrice = view.findViewById(R.id.editPriceDouble);
        builder.setView(view).setTitle("Adding Product...")
                //Action when user presses cancel button.
                .setNegativeButton("Cancel", (dialog, which) -> dialog.cancel())
                //Action when user presses cancel button.
                .setPositiveButton("OK", (dialog, which) -> {
                    String name = productName.getText().toString();
                    String url = productURL.getText().toString();
                    String price = productPrice.getText().toString();
                    if (!name.equals("") && !url.equals("") && !price.equals("")) {
                        listener.addProduct(name, url, price);
                    } else {
                        //Inform user that an activity was empty
                        Toast.makeText(getContext(), getString(R.string.errorMessage), Toast.LENGTH_SHORT).show();
                    }
                });
        //If a user shared a link from another app
        if (getArguments() != null) {
            productURL.setText(getArguments().getString("url"));
        }
        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        listener = (NewProductDialogListener) context;
    }

    public interface NewProductDialogListener {
        void addProduct(String name, String url, String price);
    }
}
