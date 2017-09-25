package com.example.core.peyepeliner;

import android.content.Intent;
import android.graphics.BitmapFactory;
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


public class FirstActivity extends AppCompatActivity {

    // private ImageButton importFromCamera;
    // private ImageButton rotateButton;
    // private ImageButton menuMenu;
    public static TriangleCanvas importedPhoto;
    //private ImageView importedPhoto;  //now in customized TriangleCanvas-class
    private final int CAMERA_REQUEST = 815;
    private boolean picTaken = false;

    private String pictureImagePath = "";

    //private Menu dropDownMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);

        Toolbar customToolbar = (Toolbar) findViewById(R.id.menuToolbarFA);
        setSupportActionBar(customToolbar);

        //TODO - test
        importedPhoto = (TriangleCanvas) findViewById(R.id.triCanvas);
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
    //TODO - rewrite TriangleOperations in TriangleCanvas to use PointF and operationBooleans

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
                importedPhoto.delTri(importedPhoto.getSelectedTri()); // .delete(TriangleOperations.getSelectedTriangle());
                Toast.makeText(FirstActivity.this, "Ausgewähltes Dreieck löschen", Toast.LENGTH_SHORT).show();
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

    public void nextActivityFA(View v)
    {
        Intent intent = new Intent(getApplicationContext(), SecondActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
    }

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
        }
    }
}

