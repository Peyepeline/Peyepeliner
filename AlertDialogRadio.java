package com.example.core.peyepeliner;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;


/**
 * Created by Core on 27.09.2017.
 */

public class AlertDialogRadio  extends DialogFragment {
    boolean dialogUse = true;  //could use int for more options, but since only 2 of these are used...
    //dialogUse = true for perspective choice, = false for peaks choice
    boolean testB = true;
    final CharSequence[] canvasTypes = {" Top View ", " Side View "};
    final CharSequence[] peakTypes = {" flat top and bottom ", " peak on top ", " peak on top and bottom "};
    /** Declaring the interface, to invoke a callback function in the implementing activity class */
    AlertPositiveListener alertPositiveListener;
    /** An interface to be implemented in the hosting activity for "OK" button click listener */
    interface AlertPositiveListener {
        public void onPositiveClick(boolean dialogType, int position);
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
            alertPositiveListener.onPositiveClick(dialogUse, position);
        }
    };
    /** Listener for choice feedback */
    DialogInterface.OnClickListener itemSelectListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            AlertDialog alert = (AlertDialog)dialog;
            int position = alert.getListView().getCheckedItemPosition();
            if(dialogUse){  //case: perspective choice
                if((position == 0) && FirstActivity.importedPhoto.canvasTypeTri){
                    //what to do if choice not possible?
                    Toast.makeText(getActivity(), " DO NOT CHANGE THE CHOICE GOD MADE FOR YOU! ", Toast.LENGTH_LONG).show();
                    testB = false;
                    alert.getButton(AlertDialog.BUTTON_NEUTRAL).setEnabled(testB);
                }
                if((position == 1) && !FirstActivity.importedPhoto.canvasTypeTri){
                    //what to do if choice not possible?
                    Toast.makeText(getActivity(), " DO NOT CHANGE THE CHOICE GOD MADE FOR YOU! ", Toast.LENGTH_LONG).show();
                    testB = false;
                    alert.getButton(AlertDialog.BUTTON_NEUTRAL).setEnabled(testB);
                }
                if((position == 0) && !FirstActivity.importedPhoto.canvasTypeTri){
                    //what to do if choice possible?
                    testB = true;
                    alert.getButton(AlertDialog.BUTTON_NEUTRAL).setEnabled(testB);
                }
                if((position == 1) && FirstActivity.importedPhoto.canvasTypeTri){
                    //what to do if choice possible?
                    testB = true;
                    alert.getButton(AlertDialog.BUTTON_NEUTRAL).setEnabled(testB);
                }
            }else{  //case: peaks choice ... "ok"-button always enabled
                testB = true;
                alert.getButton(AlertDialog.BUTTON_NEUTRAL).setEnabled(testB);
            }

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
        //dBuilder.setTitle("Pick the Perspective:");
        /** Setting items to the alert dialog */
        /** Second argument states default selection */
        //dBuilder.setSingleChoiceItems(canvasTypes, position, null);
        if(dialogUse){
            if(FirstActivity.isPicTaken()){
                if(FirstActivity.importedPhoto.canvasTypeTri){
                    dBuilder.setTitle("Pick the Perspective (Top View already taken!):");
                    dBuilder.setSingleChoiceItems(canvasTypes, 1, itemSelectListener);
                }else{
                    dBuilder.setTitle("Pick the Perspective (Side View already taken!):");
                    dBuilder.setSingleChoiceItems(canvasTypes, 0, itemSelectListener);
                }
            }else{
                testB = true;
                dBuilder.setTitle("Pick the Perspective:");
                dBuilder.setSingleChoiceItems(canvasTypes, 0, null);
            }
        }else{
            testB = true;
            dBuilder.setTitle("Pick general shape of object:");
            dBuilder.setSingleChoiceItems(peakTypes, 0, null);
        }

        /** Setting a positive button and its listener */
        dBuilder.setNeutralButton("OK",positiveListener);
        /** Setting a negative button and its listener */
        //dBuilder.setNegativeButton("Cancel", null);
        /** Creating the alert dialog window using the builder class */
        AlertDialog dialog = dBuilder.create();
        //Button buttonOk = dialog.getButton(AlertDialog.BUTTON_NEUTRAL);
        //buttonOk.setEnabled(testB);
        /** Return the alert dialog window */
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setEnabled(testB);
        return dialog;
    }
}
