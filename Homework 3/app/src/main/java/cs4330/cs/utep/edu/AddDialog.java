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
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Objects;

import cs4330.cs.utep.edu.models.ItemManager;
/**
 * Class will handle dialog fragments.
 */
public class AddDialog extends DialogFragment {

    EditText itemName;
    EditText itemSource;
    ProgressBar pb; // progress bar.

    /**
     * This will just take care of the dialog instance.
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View DialogView = inflater.inflate(R.layout.edit_dialog,null);
        itemSource = DialogView.findViewById(R.id.editTextSource);
        String url = getArguments() != null ? getArguments().getString("url") : null;
        itemSource.setText(url);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Add Item");
        builder.setView(DialogView)
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        String url = itemSource.getText().toString();
                        ((MainActivity) Objects.requireNonNull(getActivity())).doAsync(url);
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                 }});
        return builder.create();
    }
}
