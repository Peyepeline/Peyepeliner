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

public class ThirdActivity extends AppCompatActivity {

    //TODO !
    //TODO - change class Triangle to use P3Ds instead of PointFs or change all references of Triangle to Tri3D and add functionality to Tri3D
    //TODO !
    //TODO - generally: activity-change-buttons (dialogues?)

    //deprecated? call topView.populatePointList() and use topView.points
    //TODO - change data type to P3D - done.
    public ArrayList<P3D> points3D = new ArrayList<P3D>(); //P3D-liste mit punkten (3-koord.) für modell
    public ArrayList<P3D> originalRing = new ArrayList<P3D>();
    private int currIndex = 0;  //first existing point

    private ImageButton connectButton;
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
                    Bitmap photo2 = BitmapFactory.decodeFile(imgFile2.getAbsolutePath());
                    bottomView.setImageBitmap(photo2); //Verknuepfe erstes Bild mit frontView
                }
            }
            if(getIntent().getStringExtra("PfadBild2")!=null) { //Test
                File imgFile1 = new File(getIntent().getStringExtra("PfadBild2"));
                if (imgFile1.exists()) {
                    Bitmap photo1 = BitmapFactory.decodeFile(imgFile1.getAbsolutePath());
                    topView.setImageBitmap(photo1); //Verknuepfe zweites Bild mit TopView
                }
            }

        }else{ //wenn das zweite Bild Sideview war
            if(getIntent().getStringExtra("PfadBild1")!=null) { //Test
                File imgFile2 = new File(getIntent().getStringExtra("PfadBild1"));
                if (imgFile2.exists()) {
                    Bitmap photo2 = BitmapFactory.decodeFile(imgFile2.getAbsolutePath());
                    topView.setImageBitmap(photo2); //Verknuepfe erstes Bild mit topView
                }
            }
            if(getIntent().getStringExtra("PfadBild2")!=null) { //Test
                File imgFile1 = new File(getIntent().getStringExtra("PfadBild2"));
                if (imgFile1.exists()) {
                    Bitmap photo1 = BitmapFactory.decodeFile(imgFile1.getAbsolutePath());
                    bottomView.setImageBitmap(photo1); //Verknuepfe zweites Bild mit frontView
                }
            }
        }

        if(getIntent().getFloatArrayExtra("Dreiecke")!=null){
            topView.rebuildFormerTriangles(getIntent().getFloatArrayExtra("Dreiecke"));
        }
        if(getIntent().getFloatArrayExtra("XPunkte")!=null){
            bottomView.rebuildFormerPoints(getIntent().getFloatArrayExtra("XPunkte"),getIntent().getFloatArrayExtra("YPunkte"),
                    getIntent().getFloatArrayExtra("ZPunkte"));
        }

        //TODO - get RingPath from topView
        //first populate point list
        topView.populatePointList();
        //then create 3-D-Point-List corresponding to it
        points3D.clear();
        originalRing.clear();   //later: fill and use for scaled ring data (modell rotationskörper)
        //fill RingList
        //topView(x)=reality(x), topView(y)=reality(z), reality(y) not in topView
        for (P3D pointInTV : topView.points) {
            points3D.add(new P3D(pointInTV.x, 0, pointInTV.y));
        }
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

        connectButton = (ImageButton) findViewById(R.id.connectButtonTA);
        connectButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
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
                            //find corresponding point to p1ForConnect in points3D
                            if((pointInPoints3D.x == p1ForConnect.x) && (pointInPoints3D.z == p1ForConnect.y)) {
                                //fill in y-coord.
                                pointInPoints3D.y = p2ForConnect.y;
                                //TODO - is this byReference or byValue???
                                if(extremPtLinks == null){
                                    extremPtLinks = pointInPoints3D;
                                }else{
                                    extremPtRechts = pointInPoints3D;
                                }
                            }
                        }
                        //clear connectInProgress state
                        connectInProgress = false;
                        //if both extreme pts connected cross-View, disable (connect!)-button!
                        connectButton.setEnabled(false);
                        connectButton.setClickable(false);
                        connectButton.setAlpha(.5f);
                    }else{
                        //error: select 1 pkt each!
                        Toast.makeText(ThirdActivity.this, "Je 1 Punkt muss markiert sein!", Toast.LENGTH_LONG).show();
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

}

