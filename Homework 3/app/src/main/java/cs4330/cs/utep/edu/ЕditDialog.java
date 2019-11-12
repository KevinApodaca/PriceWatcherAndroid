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

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Class handles the popup for when a user wants to edit a document using fragments. It allows for removal or addition of information.
 */
@SuppressLint("ValidFragment")
public class ЕditDialog extends DialogFragment {


    EditText itemName;
    EditText itemSource;
    Activity getActivity;
    int activityId;

    @SuppressLint("ValidFragment")
    public ЕditDialog(int id){
        this.activityId = id;
    }

    /**
     * Method creates the dialog for editing an item.
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View DialogView = inflater.inflate(R.layout.edit_item_dialog,null);

        itemName = DialogView.findViewById(R.id.editTextItem);

        itemSource = DialogView.findViewById(R.id.editTextSourceItem);

        itemName.setText(getArguments().getString("itemName"));

        itemSource.setText(getArguments().getString("itemUrl"));

        String id = getArguments().getString("id");

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.editDialogTitle);

        /**
         * Allowing the user to save changes or to cancel and return to the previous screen.
         */
        if (this.activityId == 1) {
            builder.setView(DialogView)
                    .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            String name = itemName.getText().toString();
                            String source = itemSource.getText().toString();
                            ((MainActivity) getActivity()).editItem(name, source, getArguments().getInt("position"), "", String.valueOf(id));
                            dialog.dismiss();
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                        }
                    });
        }
        else{
            builder.setView(DialogView)
                    .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            String name = itemName.getText().toString();
                            String source = itemSource.getText().toString();
                            ((showItem) getActivity()).editItem(name, source, getArguments().getInt("position"), "", String.valueOf(id));
                            dialog.dismiss();
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                        }
                    });
        }


        return builder.create();
    }

}
