package com.example.core.peyepeliner;

/**
 * Created by Isabell on 12.09.2017.
 **/

import com.example.core.peyepeliner.AlertDialogRadio.AlertPositiveListener;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.graphics.drawable.shapes.Shape;
import android.net.Uri;
import android.os.Environment;
import android.os.Bundle;
import android.provider.MediaStore;
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
import android.support.*;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SecondActivity extends AppCompatActivity implements AlertPositiveListener {
    public static ShapeCanvas importedPhoto;
    //public Bitmap bitmap;

    private final int CAMERA_REQUEST = 816;
    private boolean pic2Taken = false;
    private int item = -1;

    private String pictureImagePath = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //bitmap = (Bitmap) getIntent().getParcelableExtra("Bitmap");

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_second);

        Toolbar customToolbar = (Toolbar) findViewById(R.id.menuToolbarSA);
        setSupportActionBar(customToolbar);

        //importedPhoto = (ShapeCanvas) getIntent().getSerializableExtra("Canvas");

        //TODO - test
        importedPhoto = (ShapeCanvas) findViewById(R.id.poiCanvas);

        importedPhoto.initCPaint();

        importedPhoto.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                importedPhoto.onTouchEvent(event);
                return true;
            }
        });

        /*if(getIntent().getFloatArrayExtra("XPunkte")!=null){
            importedPhoto.rebuildFormerPoints(getIntent().getFloatArrayExtra("XPunkte"),getIntent().getFloatArrayExtra("YPunkte"),
                    getIntent().getFloatArrayExtra("ZPunkte"));
        }
        if(getIntent().getFloatArrayExtra("Dreiecke")!=null){
            importedPhoto.rebuildFormerTriangles(getIntent().getFloatArrayExtra("Dreiecke"));
        }*/
        //importedPhoto.canvasTypeTri = !getIntent().getBooleanExtra("Typ",true);
        /*if(getIntent().getStringExtra("Pfad")!=null){ //Test
            File imgFile = new  File(getIntent().getStringExtra("Pfad"));
            if(imgFile.exists()){
                Bitmap photo = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                importedPhoto.setImageBitmap(photo);
            }
        }*/

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu2) {
        getMenuInflater().inflate(R.menu.menu_toolbarcombined, menu2);
        //MenuItem item = menu.findItem(R.id.action_rotatePic);//which menuItem?
        MenuItem item = menu2.findItem(R.id.action_takePicFA);
        item.setVisible(false);
        item = menu2.findItem(R.id.action_takePicSA);
        item.setVisible(true);
        item = menu2.findItem(R.id.action_rotatePicFA);
		//TopView
        //importedPhoto.canvasTypeTri = true;
		//eq.	(0,0) = 0	f&&f	f
		//		(0,1) = 0	f&&t	f
		//		(1,0) = 0	t&&f	f
		//		(1,1) = 1	t&&t	t
		//&& ignores second argument if first is false
        item.setVisible(pic2Taken && importedPhoto.canvasTypeTri);
        item = menu2.findItem(R.id.action_newTriFA);
        item.setVisible(pic2Taken && importedPhoto.canvasTypeTri);
        item = menu2.findItem(R.id.action_movePtFA);
        item.setVisible(pic2Taken && importedPhoto.canvasTypeTri);
        item = menu2.findItem(R.id.action_selectPtFA);
        item.setVisible(pic2Taken && importedPhoto.canvasTypeTri);
        item = menu2.findItem(R.id.action_selectTriFA);
        item.setVisible(pic2Taken && importedPhoto.canvasTypeTri);
        item = menu2.findItem(R.id.action_delTriFA);
        item.setVisible(pic2Taken && importedPhoto.canvasTypeTri);
        item = menu2.findItem(R.id.action_nextActivityFA);
        item.setVisible(false);
        //item.setVisible(pic2Taken && importedPhoto.canvasTypeTri);
		//SideView
		//importedPhoto.canvasTypeTri = false;
		//via additional XOR
		//eq.	(0,0) = 0 ^ 0 = 0
		//		(0,1) = 0 ^ 0 = 0
		//		(1,0) = 0 ^ 1 = 1
		//		(1,1) = 1 ^ 1 = 0
		item = menu2.findItem(R.id.action_rotatePicSA);
        item.setVisible((pic2Taken && importedPhoto.canvasTypeTri) ^ pic2Taken);
        item = menu2.findItem(R.id.action_newPointSA);
        item.setVisible((pic2Taken && importedPhoto.canvasTypeTri) ^ pic2Taken);
        item = menu2.findItem(R.id.action_selectSA);
        item.setVisible((pic2Taken && importedPhoto.canvasTypeTri) ^ pic2Taken);
        item = menu2.findItem(R.id.action_delPointSA);
        item.setVisible((pic2Taken && importedPhoto.canvasTypeTri) ^ pic2Taken);
        item = menu2.findItem(R.id.action_delEvSA);
        item.setVisible((pic2Taken && importedPhoto.canvasTypeTri) ^ pic2Taken);
        item = menu2.findItem(R.id.action_nextActivitySA);
        item.setVisible(pic2Taken);
        //item.setVisible((pic2Taken && importedPhoto.canvasTypeTri) ^ pic2Taken);
        return true;
    }

    //TODO - strings done.
    //TODO - rewrite TriangleOperations in TriangleCanvas to use PointF and operationBooleans

    @Override   //TODO - change string ref of action_openDropdownMenu to last used action
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_takePicSA:
                accessCamera();
                //pic2Taken = true;
                invalidateOptionsMenu();
                return true;

            case R.id.action_rotatePicSA:
                importedPhoto.setRotation(importedPhoto.getRotation() + 90);
                return true;

            case R.id.action_newPointSA:
                importedPhoto.setOperationID(1);
                Toast.makeText(SecondActivity.this, "Neuer Punkt", Toast.LENGTH_SHORT).show();
                Toast.makeText(SecondActivity.this, "OperationID "+importedPhoto.getOperationID() , Toast.LENGTH_SHORT).show();
                return true;

            case R.id.action_delPointSA:
                importedPhoto.setOperationID(2);
                Toast.makeText(SecondActivity.this, "Punkt loeschen", Toast.LENGTH_SHORT).show();
                if(importedPhoto.getSelectedPointIndex()==-1){
                    Toast.makeText(SecondActivity.this, "KEIN PUNKT AUSGEWÄHLT!", Toast.LENGTH_LONG).show();
                }
                return true;

            case R.id.action_delEvSA:
                importedPhoto.setOperationID(3);
                Toast.makeText(SecondActivity.this, "Alles loeschen", Toast.LENGTH_SHORT).show();
                return true;

            case R.id.action_selectSA:
                importedPhoto.setOperationID(0);
                Toast.makeText(SecondActivity.this, "Punkt auswaehlen", Toast.LENGTH_SHORT).show();
                return true;

            case R.id.action_nextActivitySA:
                //startActivity(new Intent(this, SecondActivity.class));
                Intent intent = new Intent(getApplicationContext(), ThirdActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                if(importedPhoto.canvasTypeTri){
                    if(getIntent().getFloatArrayExtra("XPunkte")!=null){
                        intent.putExtra("XPunkte",getIntent().getFloatArrayExtra("XPunkte"));
                        intent.putExtra("YPunkte",getIntent().getFloatArrayExtra("YPunkte"));
                        intent.putExtra("ZPunkte",getIntent().getFloatArrayExtra("ZPunkte"));
                    }
                    if (importedPhoto.getFirstTriangle() != null) {
                        intent.putExtra("Dreiecke", importedPhoto.getTriangleArray());
                    }
                }else{
                    if(importedPhoto.points!=null) {
                        intent.putExtra("XPunkte", importedPhoto.getPointArray('x'));
                        intent.putExtra("YPunkte", importedPhoto.getPointArray('y'));
                        intent.putExtra("ZPunkte", importedPhoto.getPointArray('z'));
                    }
                    if(getIntent().getFloatArrayExtra("Dreiecke")!=null){
                        if(getIntent().getFloatArrayExtra("Dreiecke")!=null){
                            intent.putExtra("Dreiecke", getIntent().getFloatArrayExtra("Dreiecke"));
                        }
                    }
                }

                if(getIntent().getStringExtra("Pfad")!=null){
                    String str = getIntent().getStringExtra("Pfad");
                    intent.putExtra("PfadBild1", str);
                }
                if(pictureImagePath!=null){
                    intent.putExtra("PfadBild2",pictureImagePath);
                }
                intent.putExtra("TypBild2",importedPhoto.canvasTypeTri);
                intent.putExtra("OrigAbmessX", importedPhoto.getWidth());
                intent.putExtra("OrigAbmessY",importedPhoto.getHeight());
                startActivity(intent);
                return true;
				
			//former 1stAct-Actions
			case R.id.action_newTriFA:
                importedPhoto.setOperationID(1);
                Toast.makeText(SecondActivity.this, "Neues Dreieck", Toast.LENGTH_SHORT).show();
                Toast.makeText(SecondActivity.this, "OperationID "+importedPhoto.getOperationID() , Toast.LENGTH_SHORT).show();
                return true;

            case R.id.action_movePtFA:
                importedPhoto.setOperationID(2);
                Toast.makeText(SecondActivity.this, "Punkt bewegen", Toast.LENGTH_SHORT).show();
                if(importedPhoto.getSelectedPoint()==null){
                    Toast.makeText(SecondActivity.this, "KEIN PUNKT AUSGEWÄHLT!", Toast.LENGTH_LONG).show();
                }
                return true;

            case R.id.action_selectPtFA:
                importedPhoto.setOperationID(3);
                Toast.makeText(SecondActivity.this, "Punkt auswählen", Toast.LENGTH_SHORT).show();
                return true;

            case R.id.action_selectTriFA:
                importedPhoto.setOperationID(0);
                Toast.makeText(SecondActivity.this, "Dreieck auswählen", Toast.LENGTH_SHORT).show();
                return true;

            case R.id.action_delTriFA:
                importedPhoto.deleteTri(importedPhoto.getSelectedTri()); // .delete(TriangleOperations.getSelectedTriangle());
                Toast.makeText(SecondActivity.this, "Ausgewähltes Dreieck löschen", Toast.LENGTH_SHORT).show();
                return true;

            default:

                return super.onOptionsItemSelected(item);

        }
    }
    /** Defining button click listener for the OK button of the alert dialog window */
    @Override
    public void onPositiveClick(int item) {
        switch (item) {
            case 0:
                if(pictureImagePath!=null) {
                    //TopView
                    importedPhoto.canvasTypeTri = true;
                    pic2Taken = true;
                    //via boolean comparative//
                    // TODO - give reaction to choice - only in 2ndAct.
                }
                break;
            case 1:
                //Side-|FrontView
                if(pictureImagePath!=null) {
                    importedPhoto.canvasTypeTri = false;
                    pic2Taken = true;
                    //via boolean comparative
                    //TODO - give reaction to choice - only in 2ndAct.
                }
                break;
        }
        Toast.makeText(SecondActivity.this, "Typ: "+ item, Toast.LENGTH_LONG).show();
        invalidateOptionsMenu();
    }

    public void choseCanvasType() {
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

		//test
        /*
		list = alert.getArguments();
		final ListAdapter adaptor = alert.getListView().getAdapter();
		//final CharSequence[] canvasTypes = {" Top View ", " Side View "};
		if (FirstActivity.importedPhoto.canvasTypeTri) {
			// Disable TopView (choice 0), enable other choice
			adaptor.getView(0, null, list).setEnabled(false);
			adaptor.getView(1, null, list).setEnabled(true);
		} else {
			// Disable SideView (choice 1), enable other choice
			adaptor.getView(1, null, list).setEnabled(false);
			adaptor.getView(0, null, list).setEnabled(true);
		}*/
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    /*
    public void nextActivitySA(View v)
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

    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth){
        // Raw width of image
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (width > reqWidth) {
            final int halfWidth = width / 2;
            // Calculate the largest inSampleSize value that is a power of 2 and keeps width larger than the requested width.
            while((halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

    public static Bitmap decodeScaledBitmap(String imageFileAbsPath){
        //while inJustDecodeBounds = true, bitmap remains empty but dimensions and mimeType are readable
        //therefore: get raw dimensions with iJDB true, recalc, set with iJDB false.
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imageFileAbsPath, options);
        //calculate scaling
        int maxWidth = Resources.getSystem().getDisplayMetrics().widthPixels;
        options.inSampleSize = calculateInSampleSize(options, maxWidth);
        //decode with correct scaling
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(imageFileAbsPath, options);
    }

    //TODO: scale+orient photo? -   DONE, see onCreate.rotateButton
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //Toast.makeText(SecondActivity.this, "foto lädt", Toast.LENGTH_SHORT).show();
        if (requestCode == CAMERA_REQUEST) {
            File imgFile = new  File(pictureImagePath);
            if(imgFile.exists()){
                /*Bitmap photo = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                importedPhoto.setImageBitmap(photo);*/
                Bitmap photo = decodeScaledBitmap(imgFile.getAbsolutePath());
                importedPhoto.setImageBitmap(photo);
            }
            choseCanvasType();
        }
    }
}
