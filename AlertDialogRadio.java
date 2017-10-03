package com.example.core.peyepeliner;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;


/**
 * Created by Core on 27.09.2017.
 */

public class AlertDialogRadio  extends DialogFragment {

    final CharSequence[] canvasTypes = {" Top View ", " Side View "};
    /** Declaring the interface, to invoke a callback function in the implementing activity class */
    AlertPositiveListener alertPositiveListener;
    /** An interface to be implemented in the hosting activity for "OK" button click listener */
    interface AlertPositiveListener {
        public void onPositiveClick(int position);
    }
    /** This is a callback method executed when this fragment is attached to an activity.
     *  This function ensures that, the hosting activity implements the interface AlertPositiveListener
     * */
    public void onAttach(android.app.Activity activity) {
        super.onAttach(activity);
        try{
            alertPositiveListener = (AlertPositiveListener) activity;
        }catch(ClassCastException e){
            // The hosting activity does not implemented the interface AlertPositiveListener
            throw new ClassCastException(activity.toString() + " must implement AlertPositiveListener");
        }
    }
    /** This is the OK button listener for the alert dialog,
     *  which in turn invokes the method onPositiveClick(position)
     *  of the hosting activity which is supposed to implement it
     */
    DialogInterface.OnClickListener positiveListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            AlertDialog alert = (AlertDialog)dialog;
            int position = alert.getListView().getCheckedItemPosition();
            alertPositiveListener.onPositiveClick(position);
        }
    };
    /** This is a callback method which will be executed
     *  on creating this fragment
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        /** Getting the arguments passed to this fragment */
        Bundle bundle = getArguments();
        int position = bundle.getInt("position");
        /** Creating a builder for the alert dialog window */
        AlertDialog.Builder dBuilder = new AlertDialog.Builder(getActivity());
        /** Setting a title for the window */
        dBuilder.setTitle("Pick the Perspective:");
        /** Setting items to the alert dialog */
        dBuilder.setSingleChoiceItems(canvasTypes, position, null);
        /** Setting a positive button and its listener */
        dBuilder.setNeutralButton("OK",positiveListener);
        /** Setting a negative button and its listener */
        //dBuilder.setNegativeButton("Cancel", null);
        /** Creating the alert dialog window using the builder class */
        AlertDialog dialog = dBuilder.create();
        /** Return the alert dialog window */
        return dialog;
    }
}
