package cs4330.cs.utep.pricewatcher.view;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import java.util.Objects;

import cs4330.cs.utep.pricewatcher.R;

public class EditProductDialogActivity extends AppCompatDialogFragment {

    //Text fields displayed in the dialog
    private EditText productName;
    private EditText productURL;
    private EditText productPrice;
    //Listener used to call methods inside an activity
    private EditProductDialogListener listener;

    @Override
    public Dialog onCreateDialog(Bundle saveInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getActivity()));
        LayoutInflater inflater = getActivity().getLayoutInflater();
        @SuppressLint("InflateParams") View view = inflater.inflate(R.layout.activity_new_product_dialog, null);
        productName = view.findViewById(R.id.editNameString);
        productURL = view.findViewById(R.id.editURLString);
        productPrice = view.findViewById(R.id.editPriceDouble);
        builder.setView(view).setTitle("Edit Product")
                //Action when user presses cancel button.
                .setNegativeButton("Cancel", (dialog, which) -> dialog.cancel())
                //Action when user presses ok button.
                .setPositiveButton("Ok", (dialog, which) -> {
                    String name = productName.getText().toString();
                    String url = productURL.getText().toString();
                    String price = productPrice.getText().toString();
                    //Check to make sure fields are not empty
                    if (!name.equals("") && !url.equals("")) {
                        assert getArguments() != null;
                        //Call updateProduct passing the new information
                        listener.updateProduct(name, url, price, getArguments().getInt("index"));
                    } else {
                        //Inform user that an activity was empty
                        Toast.makeText(getContext(), getString(R.string.errorMessage), Toast.LENGTH_SHORT).show();
                    }
                });
        assert getArguments() != null;
        productName.setText(getArguments().getString("currentName"));
        productURL.setText(getArguments().getString("currentUrl"));
        productPrice.setText(getArguments().getString("currentPrice"));
        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        listener = (EditProductDialogListener) context;
    }

    public interface EditProductDialogListener {
        void updateProduct(String name, String url, String price, int index);
    }
}
