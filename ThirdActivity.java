package com.example.core.peyepeliner;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
//import android.graphics.Bitmap;
import android.graphics.*;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Vector;

public class ThirdActivity extends AppCompatActivity {

    //TODO !
    //TODO - change class Triangle to use P3Ds instead of PointFs or change all references of Triangle to Tri3D and add functionality to Tri3D
    //TODO !
    //TODO - generally: activity-change-buttons (dialogues?)

    //deprecated? call topView.populatePointList() and use topView.points
    //TODO - change data type to P3D - done.
    public ArrayList<P3D> points3D = new ArrayList<P3D>(); //P3D-liste mit punkten
    //WIRD NUR FÜR ERFASSUNG VON extremPtLinks/extremPktRechts GENUTZT!
    public ArrayList<P3D> originalRing = new ArrayList<P3D>();
    private int currIndex = 0;  //first existing point
    public Model3D MODEL = new Model3D();   //empty model to be built stepwise

    private ImageButton connectButton;
    private ImageButton connectButton2;
    private ImageButton createRingsFromViews;
    private boolean connectInProgress = false;
    //TODO - make those 2 pts selectable...
    private P3D p1ForConnect;
    private P3D p2ForConnect;
    //pts. for RingScaling etc.pp.
    private P3D extremPtLinks = null;
    private P3D extremPtRechts = null;

    private ShapeCanvas topView; //first picture+points+triangles of ImportAndEnterActivity
    private ShapeCanvas bottomView; //second picture+points
    private Toolbar customToolbar;

    private String pictureImagePath = "";
    private String bottomViewPath="";
    private String topViewPath="";

    public ArrayList<Tri3D> triangles = new ArrayList<Tri3D>();

    public float verschSide;
    public float verschTop;
    public float scale;

    public int anzahlSpitzen;

    private boolean scaled=false;

    //import frontPic and topPic from ImportAndEnterActivity - point to actual objects
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_third);

        //customToolbar = (Toolbar) findViewById(R.id.menu_ToolbarTA);
        //setSupportActionBar(customToolbar);

        //P1 - topView
        //TODO - import topView and point/triangle-data - done.

        topView = (ShapeCanvas) findViewById(R.id.pictureTop);
        topView.canvasTypeTri=true;
        topView.initCPaint();


        //TODO - import frontView and point-data - done.
        //P2 - frontView
        bottomView = (ShapeCanvas) findViewById(R.id.pictureBottom);
        bottomView.canvasTypeTri=false;
        bottomView.initCPaint();

        //welches Bild an welche Stelle, abhängig von canvasTypeTri
        if(getIntent().getBooleanExtra("TypBild2",false)){ //wenn das zweite Bild TopView war
            if(getIntent().getStringExtra("PfadBild1")!=null) { //Test
                File imgFile2 = new File(getIntent().getStringExtra("PfadBild1"));
                if (imgFile2.exists()) {
                    //Bitmap photo2 = BitmapFactory.decodeFile(imgFile2.getAbsolutePath());
                    Bitmap photo2 = decodeScaledBitmap(imgFile2.getAbsolutePath());
                    bottomView.setImageBitmap(photo2); //Verknuepfe erstes Bild mit frontView
                    bottomViewPath=getIntent().getStringExtra("PfadBild1");
                }
            }
            if(getIntent().getStringExtra("PfadBild2")!=null) { //Test
                File imgFile1 = new File(getIntent().getStringExtra("PfadBild2"));
                if (imgFile1.exists()) {
                    //Bitmap photo1 = BitmapFactory.decodeFile(imgFile1.getAbsolutePath());
                    Bitmap photo1 = decodeScaledBitmap(imgFile1.getAbsolutePath());
                    topView.setImageBitmap(photo1); //Verknuepfe zweites Bild mit TopView
                    topViewPath=getIntent().getStringExtra("PfadBild2");
                }
            }

        }else{ //wenn das zweite Bild Sideview war
            if(getIntent().getStringExtra("PfadBild1")!=null) { //Test
                File imgFile2 = new File(getIntent().getStringExtra("PfadBild1"));
                if (imgFile2.exists()) {
                    //Bitmap photo2 = BitmapFactory.decodeFile(imgFile2.getAbsolutePath());
                    Bitmap photo2 = decodeScaledBitmap(imgFile2.getAbsolutePath());
                    topView.setImageBitmap(photo2); //Verknuepfe erstes Bild mit topView
                    topViewPath=getIntent().getStringExtra("PfadBild1");
                }
            }
            if(getIntent().getStringExtra("PfadBild2")!=null) { //Test
                File imgFile1 = new File(getIntent().getStringExtra("PfadBild2"));
                if (imgFile1.exists()) {
                    //Bitmap photo1 = BitmapFactory.decodeFile(imgFile1.getAbsolutePath());
                    Bitmap photo1 = decodeScaledBitmap(imgFile1.getAbsolutePath());
                    bottomView.setImageBitmap(photo1); //Verknuepfe zweites Bild mit frontView
                    bottomViewPath=getIntent().getStringExtra("PfadBild2");
                }
            }
        }

        anzahlSpitzen = getIntent().getIntExtra("anzahlSpitzen",anzahlSpitzen);
        switch(anzahlSpitzen){
            case 0: MODEL.hasDeckelSpitze=false;
                    MODEL.hasBodenSpitze=false;
                    break;
            case 1: MODEL.hasDeckelSpitze=true;
                    MODEL.hasBodenSpitze=false;
                    break;
            case 2: MODEL.hasDeckelSpitze=true;
                    MODEL.hasBodenSpitze=true;
        }

        /*if(getIntent().getFloatArrayExtra("Dreiecke")!=null){
            topView.rebuildFormerTriangles(getIntent().getFloatArrayExtra("Dreiecke"));
        }
        if(getIntent().getFloatArrayExtra("XPunkte")!=null){
            bottomView.rebuildFormerPoints(getIntent().getFloatArrayExtra("XPunkte"),getIntent().getFloatArrayExtra("YPunkte"),
                    getIntent().getFloatArrayExtra("ZPunkte"));
        }*/

        //TODO - points3D: fill in missing y-data (calculate via bottomView)
		/*
        for (PointF pointInTV : topView.points) {
            //copy point data
            //constructor with PointF and z-coordinate
            //PointF pointInTV in Point3D is later overwritten by coordinate data from point in frontView
            points3D.add(new P3D(pointInTV.y)); // constructor with float only, remainder later
        }
		*/

		//TODO - override onTouches for both pictures?
        topView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event){
                //select "selectPoint" as current Operation
                topView.setOperationID(4);
                topView.onTouchEvent(event);
                //get now selected Point
                p1ForConnect = topView.getSelectedPoint();
                //finish
                return true;
            }
        });

        bottomView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event){
                //select "selectPoint" as current Operation
                bottomView.setOperationID(4);
                bottomView.onTouchEvent(event);
                //get now selected Point
                p2ForConnect = bottomView.getSelectedPoint();
                //finish
                return true;
            }
        });

        createRingsFromViews = (ImageButton) findViewById(R.id.createRingsFromViewsButtonTA);
        createRingsFromViews.setEnabled(false);
        createRingsFromViews.setClickable(false);
        createRingsFromViews.setAlpha(.5f);
        createRingsFromViews.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if((extremPtLinks != null) && (extremPtRechts != null)){
                    //necessary pts for RMR() are set. proceed!
                    RMR();
                }else{
                    //error. first set extremPtLinks, extremPtRechts
                    Toast.makeText(ThirdActivity.this, "ERST UEBEREINSTIMMENDE PUNKTE MARKIEREN!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        connectButton = (ImageButton) findViewById(R.id.connectButtonTA);
        connectButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(points3D.isEmpty()){
                    for(P3D pT : topView.points){
                        //adds (0,0,pT.y) to points3D
                        //x,y are determined by bottomView
                        points3D.add(new P3D(pT.y));
                    }
                }
                //TODO - onClickEvent? - s.u.
                if(!connectInProgress){
                    //first buttonPressEvent
                    //to be sure, clear selectedPoints (set indexes in arrayList to -1)
                    topView.selectedPointIndex = -1;
                    bottomView.selectedPointIndex = -1;
                    p1ForConnect = null;
                    p2ForConnect = null;
                    //start connecting-process
                    connectInProgress = true;

                    if((extremPtLinks == null) && (extremPtRechts == null)){    //(null, !null) durch if(!connectInProgress) abgefangen
                        Toast.makeText(ThirdActivity.this, "Markiere extremes linkes Punktepaar.", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(ThirdActivity.this, "Markiere extremes rechtes Punktepaar.", Toast.LENGTH_SHORT).show();
                    }
                    connectButton.setImageResource(R.drawable.done);
                    //Toast.makeText(ThirdActivity.this, "Markiere erstes Punktepaar.", Toast.LENGTH_SHORT).show();
                }else{
                    //second buttonPressEvent
                    //check on existence of 2 selected points (1 each)
                    //TODO - use 2 distinct variables for the 2 selected points - done.
                    //TODO - ONTOUCHEVENT + connectInProgress =  p1ForConnect|p2ForConnect !!!
                    //if((topView.getSelectedPointIndex() != -1) && (bottomView.getSelectedPointIndex() != -1)){
                    if((p1ForConnect != null) && (p2ForConnect != null)){
                        //1 pkt each selected: continue!
                        //in points3D: fill in missing y-coord. of point
                        for (P3D pointInPoints3D : points3D) {
                            //find corresponding point to p1ForConnect(topView) in points3D
                            //if((pointInPoints3D.x == p1ForConnect.x) && (pointInPoints3D.z == p1ForConnect.y)) {
                            if(pointInPoints3D.z == p1ForConnect.y){
                                //fill in x and y from bottomView
                                pointInPoints3D.x = p2ForConnect.x;
                                pointInPoints3D.y = p2ForConnect.y;
                                //TODO - is this byReference or byValue???
                                if(extremPtLinks == null){
                                    extremPtLinks = pointInPoints3D;
                                }else{
                                    extremPtRechts = pointInPoints3D;
                                    connectButton.setEnabled(false);
                                    connectButton.setClickable(false);
                                    connectButton.setAlpha(.5f);
                                    //if both extremPktLinks, extremPktRechts are set, enable button for the automatic creation of rings
                                    if(!MODEL.hasDeckelSpitze) {
                                        createRingsFromViews.setEnabled(true);
                                        createRingsFromViews.setClickable(true);
                                        createRingsFromViews.setAlpha(.5f);
                                    }else{
                                        connectButton.setVisibility(View.GONE);
                                        connectButton2.setVisibility(View.VISIBLE);
                                        connectButton2.setEnabled(true);
                                        connectButton2.setClickable(true);
                                        connectButton2.setAlpha(.5f);
                                    }
                                }
                            }
                        }
                        //clear connectInProgress state
                        connectInProgress = false;
                        connectButton.setImageResource(R.drawable.fragezeichen);
                        //if both extreme pts connected cross-View, disable (connect!)-button!

                    }else{
                        //error: select 1 pkt each!
                        Toast.makeText(ThirdActivity.this, "Je 1 Punkt muss markiert sein!", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        connectButton2 = (ImageButton) findViewById(R.id.connectButtonTA2);
        connectButton2.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(!connectInProgress){
                    topView.selectedPointIndex = -1;
                    bottomView.selectedPointIndex = -1;
                    p1ForConnect = null;
                    p2ForConnect = null;

                    connectInProgress = true;

                    if((MODEL.deckelspitze == null)){
                        Toast.makeText(ThirdActivity.this, "Markiere obere Spitze.", Toast.LENGTH_SHORT).show();
                    }else{
                        if(MODEL.hasBodenSpitze) {
                            Toast.makeText(ThirdActivity.this, "Markiere untere Spitze", Toast.LENGTH_SHORT).show();
                        }
                    }
                    connectButton2.setImageResource(R.drawable.done);
                }else{
                    if(MODEL.deckelspitze==null) {
                        if ((p1ForConnect != null) && (p2ForConnect != null)) {
                            for (P3D pointInPoints3D : points3D) {
                                if (pointInPoints3D.z == p1ForConnect.y) {
                                    pointInPoints3D.x = p2ForConnect.x;
                                    pointInPoints3D.y = p2ForConnect.y;
                                    //TODO - is this byReference or byValue???
                                    if (MODEL.deckelspitze == null) {
                                        MODEL.deckelspitze = pointInPoints3D;
                                        if (!MODEL.hasBodenSpitze) {
                                            connectButton2.setEnabled(false);
                                            connectButton2.setClickable(false);
                                            connectButton2.setAlpha(.5f);
                                            createRingsFromViews.setEnabled(true);
                                            createRingsFromViews.setClickable(true);
                                            createRingsFromViews.setAlpha(.5f);
                                        }else{
                                            topView.setVisibility(View.INVISIBLE);
                                        }
                                    }
                                }
                            }
                            //clear connectInProgress state
                            connectInProgress = false;
                            connectButton2.setImageResource(R.drawable.fragezeichen);
                            //if both extreme pts connected cross-View, disable (connect!)-button!

                        } else {
                            //error: select 1 pkt each!
                            Toast.makeText(ThirdActivity.this, "Je 1 Punkt muss markiert sein!", Toast.LENGTH_LONG).show();
                        }
                    }else{
                        if(MODEL.hasBodenSpitze){
                            if(p2ForConnect!=null) {
                                MODEL.bodenspitze = new P3D(p2ForConnect.x,p2ForConnect.y,MODEL.deckelspitze.z);
                                //MODEL.bodenspitze.z = MODEL.deckelspitze.z;
                                connectButton2.setEnabled(false);
                                connectButton2.setClickable(false);
                                connectButton2.setAlpha(.5f);
                                createRingsFromViews.setEnabled(true);
                                createRingsFromViews.setClickable(true);
                                createRingsFromViews.setAlpha(.5f);
                            }else{
                                Toast.makeText(ThirdActivity.this, "Markiere unteren Punkt", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                }
            }
        });


        //onCreate: markiere topView(NICHTS), bottomView(NICHTS)
        //onButtonConnectPressed:
        /*
        *if !connectInProgress
        *   connectInProgress true
        *   nachricht: "markiere erstes punktepaar"
        *   ENTWEDER:
        *       P1t: tap topView -> getPoint@tap-koord. +markiere farbe1
        *       P1b: tap bottomView -> getPoint@tap-koord. +markiere farbe1
        *   ODER:
        *       P1b: tap bottomView -> getPoint@tap-koord. +markiere farbe1
        *       P1t: tap topView -> getPoint@tap-koord. +markiere farbe1
        *else
        *   if P1t != null && P1b !=null
        *       //in points3D:
        *       P1t.y = P1b.y //(valueOf!!!)
        *       connectInProgress false
        *   else error-msg ("je 1 pkt auswählen!")
        * */

        //onButtonConnect2Pressed:
        /*
        *if !connectInProgress
        *   connectInProgress true
        *   nachricht: "markiere zweites punktepaar"
        *   ENTWEDER:
        *       P2t: tap topView -> getPoint@tap-koord. +markiere farbe2
        *       P2b: tap bottomView -> getPoint@tap-koord. +markiere farbe2
        *   ODER:
        *       P2b: tap bottomView -> getPoint@tap-koord. +markiere farbe2
        *       P2t: tap topView -> getPoint@tap-koord. +markiere farbe2
        *else
        *   if P2t != null && P2b !=null
        *       //in points3D:
        *       P2t.y = P2b.y //(valueOf!!!)
        *       connectInProgress false
        *   else error-msg ("je 1 pkt auswählen!")
        * */

    }

    /*
    @Override
    public boolean onCreateOptionsMenu(Menu menu3) {
        getMenuInflater().inflate(R.menu.menu_toolbar3, menu3);
        return true;
    }
    */

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


    @Override
    public void onWindowFocusChanged(boolean hasFocus){
        if(scaled){
            return;
        }
        super.onWindowFocusChanged(hasFocus);

        float neueHoehe = bottomView.getHeight(); //falls Bild hochkant ist
        float alteBreite = bottomView.getWidth();

        float origHoehe = (float) getIntent().getIntExtra("OrigAbmessY",1);
        float origBreite = (float) getIntent().getIntExtra("OrigAbmessX",1);

        float scalefactor;
        float verschiebeFactorX;
        float verschiebeFactorY;


        float bitmapbreite = bottomView.getDrawable().getIntrinsicWidth();
        float bitmaphoehe = bottomView.getDrawable().getIntrinsicHeight();
        float verhaeltnis = bitmaphoehe/bitmapbreite;
        float neuebreite;
        float obererStreifen;

        if(verhaeltnis>1) { //wenn Bild hoeher als breit ist
            neuebreite = neueHoehe / verhaeltnis;
            obererStreifen=0;
        }else{
            neuebreite=alteBreite; //wenn Bild breiter als hoch ist
            neueHoehe=verhaeltnis*neuebreite;
            obererStreifen=(bottomView.getHeight()-neueHoehe)/2;
        }

        scalefactor = neuebreite / origBreite;

        verschiebeFactorX = (alteBreite-neuebreite)/2;
        verschiebeFactorY = (origHoehe-(verhaeltnis*origBreite))/2-obererStreifen;

        verschSide=verschiebeFactorY;
        scale=scalefactor;

        if(getIntent().getFloatArrayExtra("XPunkte")!=null){
            bottomView.rebuildFormerPoints(getIntent().getFloatArrayExtra("XPunkte"),getIntent().getFloatArrayExtra("YPunkte"),
                    getIntent().getFloatArrayExtra("ZPunkte"), scalefactor, verschiebeFactorX, verschiebeFactorY);
        }

        bitmapbreite = topView.getDrawable().getIntrinsicWidth();
        bitmaphoehe = topView.getDrawable().getIntrinsicHeight();
        verhaeltnis = bitmaphoehe/bitmapbreite;

        if(verhaeltnis>1) {
            neueHoehe=topView.getHeight();
            neuebreite = neueHoehe / verhaeltnis;
            obererStreifen=0;
        }else{
            neuebreite=alteBreite;
            neueHoehe=verhaeltnis*neuebreite;
            obererStreifen=(topView.getHeight()-neueHoehe)/2;
        }
        scalefactor = neuebreite/origBreite;
        verschiebeFactorX = (alteBreite-neuebreite)/2;
        verschiebeFactorY = (origHoehe-(verhaeltnis*origBreite))/2-obererStreifen;

        verschTop=verschiebeFactorY;

        if(getIntent().getFloatArrayExtra("Dreiecke")!=null){
            topView.rebuildFormerTriangles(getIntent().getFloatArrayExtra("Dreiecke"),scalefactor,verschiebeFactorX,verschiebeFactorY);
        }
        scaled=true;
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    public void setYCoordinate(){
        //Koerper, bei denen Oben und Unten gleich ist?
        //ArrayList<P3D> mitY = new ArrayList<P3D>(); //Liste noetig?
        ArrayList<P3D> ohneY = new ArrayList<P3D>();
        //int wertFuerYNochNichtDefiniert = -100; //Platzhalter: welchen Wert hat y, wenn y noch nicht festgelegt ist
        float yOben = Integer.MIN_VALUE;
        float yUnten = Integer.MAX_VALUE;
        int angle=0;
        boolean isPointInTriangle=false;
        //P3D anglePoint=null; // Punkt, bei dem Winkel berechnet werden muss
        Tri3D currentTriangle;
        for(P3D point: points3D){
            if(point.y>0){
                ohneY.add(point);
            }else{
                //mitY.add(point);
                if(yOben<point.y){
                    yOben = point.y;
                }
                if(yUnten>point.y){
                    yUnten = point.y;
                }
            }
        }
        for(P3D point: ohneY){
            currentTriangle=topView.getFirstTriangle();
            while(currentTriangle!=null){
                if(point.x==currentTriangle.getp0().x&&point.z==currentTriangle.getp0().y){ // y-Koordinate bei currentTriangle?
                    angle = angle + angle(currentTriangle.getp1().x-point.x,currentTriangle.getp1().y-point.z,
                            currentTriangle.getp2().x-point.x,currentTriangle.getp2().y-point.z);
                }else{
                    if(point.x==currentTriangle.getp1().x&&point.z==currentTriangle.getp1().y){
                        angle = angle + angle(currentTriangle.getp0().x-point.x,currentTriangle.getp0().y-point.z,
                                currentTriangle.getp2().x-point.x,currentTriangle.getp2().y-point.z);
                    }else{
                        if(point.x==currentTriangle.getp2().x&&point.z==currentTriangle.getp2().y){
                            angle = angle + angle(currentTriangle.getp0().x-point.x,currentTriangle.getp0().y-point.z,
                                    currentTriangle.getp1().x-point.x,currentTriangle.getp1().y-point.z);
                        }
                    }
                    currentTriangle = currentTriangle.getNextTriangle();
                }
            }
            if(angle<=180){
                point.y = yUnten; //Wenn Winkel kleiner gleich 180 Grad, ist der Punkt aussen, also unten
            }else{
                point.y= yOben;
            }
            angle=0;
        }
    }


    public int angle(float x1, float y1, float x2, float y2){
        int xx1 = (int) x1;
        int yy1 = (int) y1;
        int xx2 = (int) x2;
        int yy2 = (int) y2;
        double skalarprod = x1*x2 + y1*y2;
        double laenge1 = Math.sqrt(xx1^2+yy1^2);
        double laenge2 = Math.sqrt(xx2^2+yy2^2);
        return (int) Math.toDegrees(Math.acos(skalarprod/(laenge1*laenge2)));
    }

    //private void TVPointIterate(){
        //topView.populatePointList(); already done in onCreate()
        //topView.setSelectedPoint((int)pointInTV.x, (int)pointInTV.y);
        //for (PointF pointInTV : topView.points) {
            //mark point
        //    topView.setSelectedPoint((int)pointInTV.x, (int)pointInTV.y);
        //}
    //}

    /*
    //  P1 (topView): (x,)z
    //  P2 (frontView): x,y
    //connect-procedere:
        1. translate P1 (topView) content ENTIRELY to 3-D-system (points have only z-coordinate-value)
        2. iterate points in P1 (topView)
            a. for each: let user mark corresponding point in P2 (frontView)
            b. onClick (button) set X|Y-value of current iterate point to value of user-selected point
            c. deselect points in P2 (frontView)
        3. iterate until it's done.

    connectCoordinates(){
        //TODO - check getCurrentPoint()-method
        currentTVPoint = topView.getCurrentPoint();
        currentFVPoint = frontView.getCurrentPoint();
        //TODO - implement P.setYValue(y)
        currentTVPoint.setYValue(currentFVPoint.y);
    }
    */

    //case 0: TV only contains 1 ring (no spike.)
    //ergo
    public void RMR(){//called to create rings based on topView, bottomView, extremPktLinks, extremPktRechts
        //@init: clear ring data
        //==================================================
        //FIRST:
        // create RING
        //first populate point list
        P3D copyDS = new P3D(0,0,0);
        P3D copyBS = new P3D(0,0,0);
        if(MODEL.hasDeckelSpitze){
            copyDS=new P3D(MODEL.deckelspitze.x,MODEL.deckelspitze.y,MODEL.deckelspitze.z);
            if(MODEL.hasBodenSpitze){
                copyBS = new P3D(MODEL.bodenspitze.x,MODEL.bodenspitze.y,MODEL.bodenspitze.z);
            }
        }
        topView.populatePointList();
        //then create ring via dedicated method
        originalRing.clear();
        originalRing = MODEL.getRing(topView.points);
        triangles();
        float k2=1/getK(originalRing.get(0));
        if(MODEL.hasDeckelSpitze){
            spitzen1(originalRing,k2);
        }
        scaleRing(originalRing,k2);
        if(MODEL.hasDeckelSpitze) {
            spitzen2(originalRing);
        }
        //originalRing now contains the correctly rotated Ring taken from topView
        //MODEL.getRing(topView.points);
        //0->extremLinks
        //move origRing to extremPktLinks.y and add to MODEL.points
        //---

        P3D extremLinksCopy = new P3D(extremPtLinks.x,extremPtLinks.y,extremPtLinks.z);
        //---
        vector originalVector = new vector(0,extremPtLinks.y,0);
        adjustRingPos(originalRing,originalVector); //geaendert
        MODEL.addPointListToMesh(originalRing);

        //---
        for(int i=0;i<triangles.size();i++){
            MODEL.addTriangleToMesh(triangles.get(i));
        }
        //---
        //==================================================
        //SECOND:
        //=======================edited===========================
        //USE BOTTOMVIEW-PROJECTIONS FOR CALCULATIONS!
        P3D bottomViewLinks = new P3D(extremPtLinks.x, extremPtLinks.y);
        P3D bottomViewRechts = new P3D(extremPtRechts.x, extremPtRechts.y);
        vector LR = new vector(bottomViewLinks, bottomViewRechts);
        //=======================edited===========================
        vector LToPoint = new vector(0);
        vector RToPoint = new vector(0);
        float halfDistLR = (float)LR.length()/2;
        float k = 0;
        boolean neuerRing;
        //scaleRing(originalRing,getK(originalRing.get(0)));
        //for each p left of bisector (dist<0)
        for (P3D p : bottomView.points) {
            neuerRing=true;
            //if(calcDistP3DBisector(p) < 0){ //p is LEFT of bisector
            //=======================edited===========================
            LToPoint.setVector(bottomViewLinks, p);
            RToPoint.setVector(bottomViewRechts, p);
            //=======================edited===========================
            if(LToPoint.length() < RToPoint.length()&&(!(p.x==extremLinksCopy.x&&p.y==extremLinksCopy.y))/*&&(!p.compare(extremPtRechts))*/) {
                //QUATSCH: use dreiecksungleichung!
                //for this point, create new ring, scale it, and move it to have newRing(0) coincide with the point p
                //newRing = clone of originalRing
                if (MODEL.hasDeckelSpitze) {
                    if (MODEL.hasBodenSpitze) {
                        if ((p.x == copyDS.x && p.y == copyDS.y) ||(p.x==copyBS.x && p.y==copyBS.y) ) {
                            neuerRing = false;
                        }
                    } else {
                        if (p.x == copyDS.x && p.y == copyDS.y) {
                            neuerRing = false;
                        }
                    }
                }
                if (neuerRing) {
                    ArrayList<P3D> newRing = new ArrayList<P3D>();
                    newRing.clear();
                    for (P3D point : originalRing) {
                        newRing.add(new P3D(point));
                    }
                    //calculate skalar k
                    //k = Math.abs(calcDistP3DBisector(p))/halfDistLR;
                    k = getK(p);
                    //scale newRing
                    scaleRing(newRing, k);

                    //adjustPos newRing
                    newRing = adjustRingPos(newRing, p);
                    //add points of newRing to MODEL
                    MODEL.addPointListToMesh(newRing);
                }
            }
        }
        //==================================================
        //THIRD:
        //add spikes
        //TODO - implement
        nextActivity();
    }

    public void sort(){
        ArrayList<P3D> sortedOriginalRing= new ArrayList<P3D>();
    }

    /*public float getK(P3D p){ // gibt immer k gleich oder fast gleich 1 zureuck
        P3D m = new P3D((extremPtLinks.x+extremPtRechts.x)/2,(extremPtLinks.y+extremPtRechts.y)/2, 0);
        P3D TwoDExtremLinks = new P3D(extremPtLinks.x, extremPtLinks.y, 0);
        vector LinksM = new vector(m, TwoDExtremLinks);
        double laengeLinksM = LinksM.length();
        P3D TwoDP = new P3D(p.x,p.y,0);
        vector ExLinksp = new vector(TwoDExtremLinks, TwoDP);
        P3D mStrich = new P3D(m.x+ExLinksp.x, m.y+ExLinksp.y,0);
        vector pMStrich = new vector(TwoDP, mStrich);
        double laengepMStrich = pMStrich.length();
        float k = (float) (laengepMStrich/laengeLinksM);
        return k;
    }*/

    public float getK(P3D p){
        /*float min=Float.MAX_VALUE; float max=Float.MIN_VALUE;
        for(int i=0;i<originalRing.size();i++){
            if(originalRing.get(i).x<min){
                min=originalRing.get(i).x;
            }
            if(originalRing.get(i).x>max){
                max=originalRing.get(i).x;
            }
        }*/
        /*float m = (min+max)/2;
        float XabstandP = Math.abs(p.x-m);
        float XAbstandExtr = Math.abs(min-m);*/
        float m = (extremPtLinks.x+extremPtRechts.x)/2;
        float XabstandP = Math.abs(p.x-m);
        float XAbstandExtr = Math.abs(extremPtLinks.x-m);
        //float k1 = XabstandP/XAbstandExtr;
        /*float laengeEx=Math.abs(extremPtRechts.x-extremPtLinks.x);
        float laengeOrig=Math.abs(max-min);
        XabstandP=(laengeOrig/laengeEx)*XabstandP;
        m = (min+max)/2;
        XAbstandExtr = m-min;*/
        //float XAbstandExtr = Math.abs(extremPtLinks.x-m);
        float k = XabstandP/XAbstandExtr;
        return k;
    }

    public void triangles(){
        try {
            Tri3D currentTri = topView.getFirstTriangle();
            int index0;
            int index1;
            int index2;
            P3D p0;
            P3D p1;
            P3D p2;
            while (currentTri != null) {
                index0=-1;
                index1=-1;
                index2=-1;
                p0 = new P3D(currentTri.getp0().x, 0, currentTri.getp0().y);
                p1 = new P3D(currentTri.getp1().x, 0, currentTri.getp1().y);
                p2 = new P3D(currentTri.getp2().x, 0, currentTri.getp2().y);
                for(int i=0;i<originalRing.size();i++){
                    if(originalRing.get(i).compare(p0)){
                        index0 = i;
                    }
                    if(originalRing.get(i).compare(p1)){
                        index1 = i;
                    }
                    if(originalRing.get(i).compare(p2)){
                       index2 = i;
                    }
                }
                if(index0==-1){
                    p0=MODEL.deckelspitze;
                }else{
                    p0=originalRing.get(index0);
                }
                if(index1==-1){
                    p1=MODEL.deckelspitze;
                }else{
                    p1=originalRing.get(index1);
                }
                if(index2==-1){
                    p2=MODEL.deckelspitze;
                }else{
                    p2=originalRing.get(index2);
                }
                triangles.add(new Tri3D(p0, p1, p2));
                currentTri =currentTri.getNextTriangle();
            }
        }catch (NullPointerException e){
            Toast.makeText(ThirdActivity.this, "NullPointerException triangles()", Toast.LENGTH_SHORT).show();
        }catch (IndexOutOfBoundsException e){
            Toast.makeText(ThirdActivity.this, "IndexOutOfBoundsException triangle()", Toast.LENGTH_SHORT).show();
        }
    }

    public void trianglesAlt(){
        try {
            Tri3D currentTri = topView.getFirstTriangle();
            int index0=0;
            int index1=0;
            int index2=0;
            while (currentTri != null) {
                P3D p0 = new P3D(currentTri.getp0().x, 0, currentTri.getp0().y);
                P3D p1 = new P3D(currentTri.getp1().x, 0, currentTri.getp1().y);
                P3D p2 = new P3D(currentTri.getp2().x, 0, currentTri.getp2().y);
                for(int i=0;i<originalRing.size();i++){
                    if(originalRing.get(i).compare(p0)){
                        index0 = i;
                    }
                    if(originalRing.get(i).compare(p1)){
                        index1 = i;
                    }
                    if(originalRing.get(i).compare(p2)){
                        index2 = i;
                    }
                }
                triangles.add(new Tri3D(originalRing.get(index0), originalRing.get(index1), originalRing.get(index2)));
                currentTri =currentTri.getNextTriangle();
            }
        }catch (NullPointerException e){
            Toast.makeText(ThirdActivity.this, "NullPointerException", Toast.LENGTH_SHORT).show();
        }catch (IndexOutOfBoundsException e){
            Toast.makeText(ThirdActivity.this, "IndexOutOfBoundsException", Toast.LENGTH_SHORT).show();
        }
    }

    //case 0: TV only contains 1 ring (no spike.)
    //ergo
    /*public void RMR(){//called to create rings based on topView, bottomView, extremPktLinks, extremPktRechts
        //@init: clear ring data
        //==================================================
        //FIRST:
        // create RING
        //first populate point list
        topView.populatePointList();
        //then create ring via dedicated method
        originalRing.clear();
        originalRing = MODEL.getRing(topView.points);
        //originalRing now contains the correctly rotated Ring taken from topView
        //MODEL.getRing(topView.points);
        //0->extremLinks
        //move origRing to extremPktLinks.y and add to MODEL.points
        vector originalVector = new vector(0,extremPtLinks.y,0);
        adjustRingPos(originalRing,originalVector);
        MODEL.addPointListToMesh(originalRing);
        //==================================================
        //SECOND:
        vector LR = new vector(extremPtLinks, extremPtRechts);
        vector LToPoint = new vector(0);
        vector RToPoint = new vector(0);
        float halfDistLR = (float)LR.length()/2;
        float k = 0;
        //for each p left of bisector (dist<0)
        for (P3D p : bottomView.points) {
            //if(calcDistP3DBisector(p) < 0){ //p is LEFT of bisector
            LToPoint.setVector(extremPtLinks, p);
            RToPoint.setVector(extremPtRechts, p);
            if(LToPoint.length() < RToPoint.length()&&(!p.compare(extremPtLinks))&&(!p.compare(extremPtRechts))){
                //QUATSCH: use dreiecksungleichung!
                //for this point, create new ring, scale it, and move it to have newRing(0) coincide with the point p
                //newRing = clone of originalRing
                ArrayList<P3D> newRing = new ArrayList<P3D>();
                newRing.clear();
                for(P3D point : originalRing){
                    newRing.add(new P3D(point));
                }
                //calculate skalar k
                k = Math.abs(calcDistP3DBisector(p))/halfDistLR;

                //scale newRing
                scaleRing(newRing, k);

                //adjustPos newRing
                newRing=adjustRingPos(newRing, p);
                //add points of newRing to MODEL
                MODEL.addPointListToMesh(newRing);
            }
        }
        //==================================================
        //THIRD:
        //add spikes
        //TODO - implement
        nextActivity();
    }*/

    public boolean test(P3D p){
        boolean isLinks = (extremPtLinks.x==p.x)&&(extremPtLinks.y==p.y);
        boolean isRechts= (extremPtRechts.x==p.x)&&(extremPtLinks.y==p.y);
        if(isLinks||isRechts){
            return false;
        }
        float neuX = (extremPtLinks.x+extremPtRechts.x)/2;
        float wert = neuX-p.x;
        if(wert>0){
            return true;
        }else{
            return false;
        }
    }

    public void nextActivity(){
        /*ArrayList<P3D> kompletterRing = new ArrayList<P3D>();
        for(int i=0;i<MODEL.points.size();i++){
            kompletterRing.add(MODEL.points.get(i));
        }*/

        /*topView.populatePointList();
        MODEL.addPointListToMesh(topView.points);*/

        /*Tri3D currentTriangle = topView.getFirstTriangle();
        while(currentTriangle!=null){
            MODEL.addTriangleToMesh(currentTriangle);
            currentTriangle=currentTriangle.getNextTriangle();
        }*/

        Intent intent = new Intent(getApplicationContext(), FourthActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        int anzahlPunkte=this.MODEL.points.size();
        float[] x = new float[anzahlPunkte];
        float[] y = new float[anzahlPunkte];
        float[] z = new float[anzahlPunkte];
        for(int i=0;i<anzahlPunkte;i++){
            x[i]=this.MODEL.points.get(i).x;
            y[i]=this.MODEL.points.get(i).y;
            z[i]=this.MODEL.points.get(i).z;
        }
        intent.putExtra("x",x); //xPunkte aller Punkte
        intent.putExtra("y",y); //yPunkte aller Punkte
        intent.putExtra("z",z); //zPunkte aller Punkte

        int anzahlDreiecke = this.MODEL.triangles.size();
        int[] indexTrianglep0 = new int[anzahlDreiecke]; //Index von p0 des i-ten Dreiecks in Punkte Liste
        int[] indexTrianglep1 = new int[anzahlDreiecke]; // " p1 "
        int[] indexTrianglep2 = new int[anzahlDreiecke]; // " p2 "
        for(int i=0;i<anzahlDreiecke;i++){
            indexTrianglep0[i]= this.MODEL.points.indexOf(this.MODEL.triangles.get(i).getp0());
            indexTrianglep1[i]= this.MODEL.points.indexOf(this.MODEL.triangles.get(i).getp1());
            indexTrianglep2[i]= this.MODEL.points.indexOf(this.MODEL.triangles.get(i).getp2());
        }
        intent.putExtra("p0", indexTrianglep0);
        intent.putExtra("p1", indexTrianglep1);
        intent.putExtra("p2", indexTrianglep2);
        int allePunkteImRing;
        if(MODEL.hasDeckelSpitze){
            if(MODEL.hasBodenSpitze){
                allePunkteImRing = MODEL.points.size()-2;
            }else{
                allePunkteImRing = MODEL.points.size()-1;
            }
        }else {
             allePunkteImRing = MODEL.points.size();
        }
        intent.putExtra("allePunkteImRing", allePunkteImRing);
        intent.putExtra("anzahlPunkteImRing", originalRing.size());
        intent.putExtra("anzahlRinge", allePunkteImRing/originalRing.size());

        /*int[] indexRing = new int[allePunkteImRing];
        for(int i=0;i<allePunkteImRing;i++){
            indexRing[i]=this.MODEL.points.indexOf(kompletterRing.get(i));
        }
        intent.putExtra("kompletterRing",indexRing);*/
        //TEST:
        int index=-1;
        float maxX=originalRing.get(0).x;
        for(int i=1;i<originalRing.size();i++){
            if(maxX<originalRing.get(i).x){
                index=MODEL.points.indexOf(originalRing.get(i));
                maxX=originalRing.get(i).x;
            }
        }
        intent.putExtra("indexExRechts",index);
        intent.putExtra("bottomView", bottomViewPath);
        intent.putExtra("topView", topViewPath);
        intent.putExtra("XPunkte",getIntent().getFloatArrayExtra("XPunkte"));
        intent.putExtra("YPunkte",getIntent().getFloatArrayExtra("YPunkte"));
        intent.putExtra("ZPunkte",getIntent().getFloatArrayExtra("ZPunkte"));
        intent.putExtra("Dreiecke",getIntent().getFloatArrayExtra("Dreiecke"));
        intent.putExtra("verschSide",verschSide);
        intent.putExtra("verschTop",verschTop);
        intent.putExtra("scale",1/scale);
        intent.putExtra("deckelSpitze",MODEL.hasDeckelSpitze);
        intent.putExtra("bodenSpitze",MODEL.hasBodenSpitze);
        if(MODEL.hasDeckelSpitze) {
            intent.putExtra("IndexDeckelSpitze", MODEL.points.indexOf(MODEL.deckelspitze));
        }
        if(MODEL.hasBodenSpitze) {
            intent.putExtra("IndexBodenSpitze", MODEL.points.indexOf(MODEL.bodenspitze));
        }
        startActivity(intent);
        return;
    }

    /*public float calcDistP3DBisector(P3D p){    //extremPtLinks, extremPtRechts bekannt
        //get mid(extremPtLinks, extremPtRechts) (SIDEVIEW ONLY!)
        P3D m = new P3D((extremPtLinks.x+extremPtRechts.x)/2,(extremPtLinks.y+extremPtRechts.y)/2, (extremPtLinks.z+extremPtRechts.z)/2);
        vector Lm = new vector(extremPtLinks, m);
        vector Lp = new vector(extremPtLinks, p);
        vector pLm = new vector(-Lm.y, Lm.x, 0); //vector @rightAngle(Lm)
        double phi = pLm.angle(Lp);    //TODO - formula for angle between 2 vectors - done.
        //TODO - implement vector.length() - done.
        double dist = (Lp.length() * Math.sin(phi)) - Lm.length();  //umgedreht
        return (float)dist;
    }*/

    public float calcDistP3DBisector(P3D p){    //extremPtLinks, extremPtRechts bekannt
        //get mid(extremPtLinks, extremPtRechts) (SIDEVIEW ONLY!)
        //=======================edited===========================
        P3D m = new P3D((extremPtLinks.x+extremPtRechts.x)/2,(extremPtLinks.y+extremPtRechts.y)/2, 0);
        //=======================edited===========================
        vector Lm = new vector(extremPtLinks, m);
        vector Lp = new vector(extremPtLinks, p);
        vector pLm = new vector(-Lm.y, Lm.x, 0); //vector @rightAngle(Lm)
        double phi = pLm.angle(Lp);    //TODO - formula for angle between 2 vectors - done.
        //TODO - implement vector.length() - done.
        double dist = (Lp.length() * Math.sin(phi)) - Lm.length();  //umgedreht
        return (float)dist;
    }


    public void scaleRing(ArrayList<P3D> ring, float skalar){ //p (TV) in ring has x,y, BUT is (x,z) IRL
        //get c
        float cX = 0;
        float cY = 0;
        float cZ = 0;
        int nbPts = ring.size();
        for(P3D p : ring){
            cX += p.x;
            cY += p.y;
            cZ += p.z;
        }
        cX = cX/nbPts;
        cY = cY/nbPts;
        cZ = cZ/nbPts;
        P3D c = new P3D(cX, cY, cZ);
        //scale around c
        //use vector v
        vector v = new vector(0, 0, 0);
        for(P3D p : ring){
            v.setVector(c, p);  //c->p
            v.scaleVector(skalar);
            p.x = c.x + v.x;
            p.y = c.y + v.y;
            p.z = c.z + v.z;
        }
    }

    //überladen für Nutzung mit vector und P3D
    public ArrayList<P3D> adjustRingPos(ArrayList<P3D> scaledRing, P3D p){
        //moves (freshly rescaled) Ring for coincidence [pt0Original|pt0Rescaled]
        vector v = new vector(scaledRing.get(0), p);
        for(P3D pointInRing : scaledRing){
            pointInRing.aV(v);
        }
        return scaledRing;
    }
    public ArrayList<P3D> adjustRingPos(ArrayList<P3D> scaledRing, vector v){
        for(P3D pointInRing : scaledRing){
            pointInRing.aV(v);
        }
        return scaledRing;
    }

    public void spitzen1(ArrayList<P3D> ring, float skalar){
        float cX = 0;
        float cY = 0;
        float cZ = 0;
        int nbPts = ring.size();
        for(P3D p : ring){
            cX += p.x;
            cY += p.y;
            cZ += p.z;
        }
        cX = cX/nbPts;
        cY = cY/nbPts;
        cZ = cZ/nbPts;
        P3D c = new P3D(cX, cY, cZ);

        c.y = MODEL.deckelspitze.y;

        vector v = new vector(0, 0, 0);
        v.setVector(c, MODEL.deckelspitze);
        v.scaleVector(skalar);
        MODEL.deckelspitze.x = c.x + v.x;
        MODEL.deckelspitze.y = c.y + v.y;
        MODEL.deckelspitze.z = c.z + v.z;
        if(MODEL.hasBodenSpitze){

            c.y= MODEL.bodenspitze.y;

            v.setVector(c, MODEL.bodenspitze);
            v.scaleVector(skalar);
            MODEL.bodenspitze.x = c.x + v.x;
            MODEL.bodenspitze.y = c.y + v.y;
            MODEL.bodenspitze.z = c.z + v.z;
        }

    }

    public void spitzen2(ArrayList<P3D> ring){
        /*vector v = new vector(0,0,0);
        v.setVector(ring.get(0), MODEL.deckelspitze);
        v.x=0;v.z=0;
        MODEL.deckelspitze.aV(v);*/
        MODEL.addPointToMesh(MODEL.deckelspitze);
        if(MODEL.hasBodenSpitze){
            /*v.setVector(ring.get(0), MODEL.bodenspitze);
            v.x=0;v.z=0;
            MODEL.bodenspitze.aV(v);*/
            MODEL.addPointToMesh(MODEL.bodenspitze);
        }
    }

}

