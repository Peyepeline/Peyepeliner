package com.example.core.peyepeliner;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import com.example.core.peyepeliner.AlertDialogRadio.AlertPositiveListener;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Environment;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
//import android.view.MenuItem;
import android.view.*;
import android.widget.Toast;
import android.view.View;
//import android.widget.ImageButton;
//import android.widget.ImageView;
//import android.graphics.Bitmap;
import android.graphics.*;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;


public class FirstActivity extends AppCompatActivity implements AlertPositiveListener {

    // private ImageButton importFromCamera;
    // private ImageButton rotateButton;
    // private ImageButton menuMenu;
    public static ShapeCanvas importedPhoto;
    //private ImageView importedPhoto;  //now in customized ShapeCanvas-class
    private final int CAMERA_REQUEST = 815;
    private boolean picTaken = false;
    private int item = -1;

    private String pictureImagePath = "";

    //Neu:
    //Intent intent;

    //private Menu dropDownMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);

        Toolbar customToolbar = (Toolbar) findViewById(R.id.menuToolbarFA);
        setSupportActionBar(customToolbar);

        //TODO - test
        importedPhoto = (ShapeCanvas) findViewById(R.id.triCanvas);
        //importedPhoto.setImageBitmap(mutableBitmap);  //handled in accessCamera() - onActivityResult()
        //this.importedPhoto = (ImageView) findViewById(R.id.importedPhoto);

        //initialize contour paint
        importedPhoto.initCPaint();

        importedPhoto.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                importedPhoto.onTouchEvent(event);
                return true;
            }
        });


    /*    importFromCamera = (ImageButton) findViewById(R.id.importFromCamera);
        importFromCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                accessCamera();
            }
        });

        rotateButton = (ImageButton) findViewById(R.id.rotateButton);
        rotateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                importedPhoto.setRotation(importedPhoto.getRotation() + 90);
            }
        });
    */
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu1) {
        getMenuInflater().inflate(R.menu.menu_toolbar, menu1);
        //MenuItem item = menu.findItem(R.id.action_rotatePic);//which menuItem?
        MenuItem item = menu1.findItem(R.id.action_rotatePicFA);
        item.setVisible(picTaken);
        item = menu1.findItem(R.id.action_newTriFA);
        item.setVisible(picTaken);
        item = menu1.findItem(R.id.action_movePtFA);
        item.setVisible(picTaken);
        item = menu1.findItem(R.id.action_selectPtFA);
        item.setVisible(picTaken);
        item = menu1.findItem(R.id.action_selectTriFA);
        item.setVisible(picTaken);
        item = menu1.findItem(R.id.action_delTriFA);
        item.setVisible(picTaken);
        item = menu1.findItem(R.id.action_nextActivityFA);
        item.setVisible(picTaken);
        return true;
    }

    //TODO - strings done.
    //TODO - rewrite TriangleOperations in ShapeCanvas to use PointF and operationBooleans

    @Override   //TODO - change string ref of action_openDropdownMenu to last used action
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_takePicFA:
                accessCamera();
                picTaken = true;
                invalidateOptionsMenu();
                return true;

            case R.id.action_rotatePicFA:
                importedPhoto.setRotation(importedPhoto.getRotation() + 90);
                return true;

            case R.id.action_newTriFA:    //TODO: CrossClassConnect
                importedPhoto.setOperationID(1);
                Toast.makeText(FirstActivity.this, "Neues Dreieck", Toast.LENGTH_SHORT).show();
                Toast.makeText(FirstActivity.this, "OperationID "+importedPhoto.getOperationID() , Toast.LENGTH_SHORT).show();
                return true;
            /*
            case R.id.action_optTriVertices:    //TODO: CCConnect
                //automatisch ausführen bei addTriangle?
                TriangleOperations.connect();
                return true;
            */

            case R.id.action_movePtFA:    //TODO: CCConnect
                importedPhoto.setOperationID(2);
                Toast.makeText(FirstActivity.this, "Punkt bewegen", Toast.LENGTH_SHORT).show();
                if(importedPhoto.getSelectedPoint()==null){
                    Toast.makeText(FirstActivity.this, "KEIN PUNKT AUSGEWÄHLT!", Toast.LENGTH_LONG).show();
                }
                return true;

            case R.id.action_selectPtFA:    //TODO: CCConnect
                importedPhoto.setOperationID(3);
                Toast.makeText(FirstActivity.this, "Punkt auswählen", Toast.LENGTH_SHORT).show();
                return true;

            case R.id.action_selectTriFA:    //TODO: CCConnect
                importedPhoto.setOperationID(0);
                Toast.makeText(FirstActivity.this, "Dreieck auswählen", Toast.LENGTH_SHORT).show();
                return true;

            case R.id.action_delTriFA:    //TODO: CCConnect - need Method to select Triangle! (in CANVAS?)
                importedPhoto.deleteTri(importedPhoto.getSelectedTri()); // .delete(TriangleOperations.getSelectedTriangle());
                Toast.makeText(FirstActivity.this, "Ausgewähltes Dreieck löschen", Toast.LENGTH_SHORT).show();
                return true;

            case R.id.action_nextActivityFA:
                //startActivity(new Intent(this, SecondActivity.class));
                Intent intent = new Intent(getApplicationContext(), SecondActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                if(importedPhoto.points!=null) {
                    intent.putExtra("XPunkte", importedPhoto.getPointArray('x'));
                    intent.putExtra("YPunkte", importedPhoto.getPointArray('y'));
                    intent.putExtra("ZPunkte", importedPhoto.getPointArray('z'));
                }
                if(importedPhoto.getFirstTriangle()!=null) {
                    intent.putExtra("Dreiecke", importedPhoto.getTriangleArray());
                }
                if(pictureImagePath!=null){
                    intent.putExtra("Pfad",pictureImagePath);
                }
                intent.putExtra("Typ",importedPhoto.canvasTypeTri);

                startActivity(intent);

                return true;
            //next-activity-button is directly wired
            //case R.id.action_openDropdownMenu:  //TODO: simple show/hide menu button (actually does NOTHING!)
            //    return true;

            default:
                // n.def.action
                // Invoke superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    /** Defining button click listener for the OK button of the alert dialog window */
    @Override
    public void onPositiveClick(int item) {
        switch (item) {
            case 0:
                //TopView
                importedPhoto.canvasTypeTri = true;
                break;
            case 1:
                //Side-|FrontView
                importedPhoto.canvasTypeTri = false;
                break;
        }
        Toast.makeText(FirstActivity.this, "Typ: "+ item, Toast.LENGTH_LONG).show();
        /*this.position = position;
        /** Getting the reference of the textview from the main layout
        TextView tv = (TextView) findViewById(R.id.tv_android);
        /** Setting the selected android version in the textview
        tv.setText("Your Choice : " + Android.code[this.position]);
        */
    }

    public void choseCanvasType() {
    /*
        CharSequence canvases[] = new CharSequence[] {"Top View", "Side View"};

        AlertDialog.Builder dBuilder = new AlertDialog.Builder(this);
        dBuilder.setTitle("Pick a perspective:");
        dBuilder.setItems(canvases, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int index) {
                // the user clicked on canvases[which]
                if(index==0){
                    importedPhoto.canvasTypeTri=true;
                }else{
                    importedPhoto.canvasTypeTri=false;
                }
            }
        });
        dBuilder.show();


        // Strings to Show In Dialog with Radio Buttons
        final CharSequence[] canvasTypes = {" Top View ", " Side View "};
        // Creating and Building the Dialog
        AlertDialog.Builder dBuilder = new AlertDialog.Builder(this);
        final AlertDialog canvasTypeDialog = dBuilder.create();
        dBuilder.setTitle("Pick the Perspective:");
        dBuilder.setSingleChoiceItems(canvasTypes, -1, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                switch (item) {
                    case 0:
                        //TopView
                        importedPhoto.canvasTypeTri = true;
                        canvasTypeDialog.dismiss(); //kann sein, dass hier ein "might not have been initialized" angezeigt wird.
                        break;
                    case 1:
                        //Side-|FrontView
                        importedPhoto.canvasTypeTri = false;
                        canvasTypeDialog.dismiss();
                        break;
                }
                //canvasTypeDialog.dismiss();
            }
        });
        //canvasTypeDialog = dBuilder.create();
        canvasTypeDialog.show();
        */
        /** Getting the fragment manager */
        FragmentManager fManager = getFragmentManager();
        /** Instantiating the DialogFragment class */
        AlertDialogRadio alert = new AlertDialogRadio();
        /** Creating a bundle object to store the selected item's index */
        Bundle b  = new Bundle();
        /** Storing the selected item's index in the bundle object */
        b.putInt("position", item);
        /** Setting the bundle object to the dialog fragment object */
        alert.setArguments(b);
        /** Creating the dialog fragment object, which will in turn open the alert dialog window */
        alert.show(fManager, "alert_dialog_radio");
    }


    /*
    public void nextActivityFA(View v)
    {
        Intent intent = new Intent(getApplicationContext(), SecondActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
    }
    */

    public void accessCamera() {
        //basically ensure correct picture (last one) is currently chosen one
        //use timestamp for distinct picture name/id
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = timeStamp + ".jpg";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        pictureImagePath = storageDir.getAbsolutePath() + "/" + imageFileName;
        File file = new File(pictureImagePath);
        Uri TakenPhotoUri = Uri.fromFile(file);

        //fileprovider-method... needlessly complicated!
        //Uri TakenPhotoUri = FileProvider.getUriForFile(this, "com.example.core.fileprovider", file);

        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, TakenPhotoUri);
        startActivityForResult(cameraIntent, CAMERA_REQUEST);
        //choseCanvasType();
    }

    //TODO: scale+orient photo? -   DONE, see onCreate.rotateButton
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //Toast.makeText(ImportAndEnterActivity.this, "foto lädt", Toast.LENGTH_SHORT).show();
        if (requestCode == CAMERA_REQUEST) {
            File imgFile = new  File(pictureImagePath);
            if(imgFile.exists()){
                Bitmap photo = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                importedPhoto.setImageBitmap(photo);
            }
            choseCanvasType();
        }
    }
}