package com.example.core.peyepeliner;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
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
    public ArrayList<P3D> points3D = new ArrayList<P3D>();
    private int currIndex = 0;  //first existing point

    private ImageButton connectButton;
    private TriangleCanvas topView; //first picture+points+triangles of ImportAndEnterActivity
    private PointCanvas frontView; //second picture+points
    private Toolbar customToolbar;

    private String pictureImagePath = "";

    //import frontPic and topPic from ImportAndEnterActivity - point to actual objects
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_third);

        //TODO - create correct toolbar
        customToolbar = (Toolbar) findViewById(R.id.menu_ToolbarTA);
        setSupportActionBar(customToolbar);

        //P1 - topView
        //TODO - import topView and point/triangle-data - done.
        topView = (TriangleCanvas) findViewById(R.id.pictureTop);
        topView = FirstActivity.importedPhoto;
        //TODO - import frontView and point-data - done.
        //P2 - frontView
        frontView = (PointCanvas) findViewById(R.id.pictureFront);
        frontView = SecondActivity.importedPhoto;

        //TODO - translate topView and its data to 3-D-Objects - done.
        //first populate point list
        topView.populatePointList();
        //then create 3-D-Point-List corresponding to it
        points3D.clear();
		//TODO - test: arraylist cloneable?
		//points3D = topView.points.clone();
		//bloedsinn, y-coord wird z-coord in neuer liste...
		for (P3D pointInTV : topView.points) {
            points3D.add(new P3D(pointInTV.y)); // constructor with float only, remainder later
        }
		/*
        for (PointF pointInTV : topView.points) {
            //copy point data
            //TODO - implement correct 3DPointConstructor/coordinate-setter - done.
            //constructor with PointF and z-coordinate
            //PointF pointInTV in Point3D is later overwritten by coordinate data from point in frontView
            points3D.add(new P3D(pointInTV.y)); // constructor with float only, remainder later
        }
		*/

        connectButton = (ImageButton) findViewById(R.id.connectButtonTA);
        //TODO - encapsulate connectButton.setOnClickListener in Iterating-method ?
        //iterate over points in topView
        //begin iteration
        topView.setSelectedPoint(topView.points.get(currIndex));

        //TODO - create list of points in topView - in TriangleCanvas - done.
        connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO - implement method for 3-D-Point-creation - done.
                //currentTVPoint = topView.getSelectedPoint();
                //currentFVPoint = frontView.points.get(frontView.getSelectedPoint())
                //NEWPOINT3DTESTER(currentTVPoint, currentFVPoint);
                //
                //TODO - implement 3DP.setPointF(PointF) - done.
                //currIndex = points3D.indexOf(topView.getSelectedPoint());
                //set PointF-value of P3D-obj @ currIndex to relevant value from frontView
                points3D.get(currIndex).set(frontView.points.get(frontView.getSelectedPoint()));
                //currentTVPoint.setYValue(currentFVPoint.y); SEE ABOVE
                //deselect all points in frontView
                frontView.selectedPoint = -1;
                //next, increment currIndex
                currIndex++;
                if(currIndex<topView.points.size()){
                    topView.setSelectedPoint(topView.points.get(currIndex));
                }else{
                    Toast.makeText(ThirdActivity.this, "Alle Punkte identifiziert.", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar3, menu);
        return true;
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

}

