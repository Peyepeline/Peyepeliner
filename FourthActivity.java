package com.example.core.peyepeliner;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.example.core.peyepeliner.AlertDialogRadio.AlertPositiveListener;

import java.util.ArrayList;

//import android.view.MenuItem;
//import android.widget.ImageButton;
//import android.widget.ImageView;
//import android.graphics.Bitmap;


public class FourthActivity extends AppCompatActivity implements AlertPositiveListener {

    // private ImageButton importFromCamera;
    // private ImageButton rotateButton;
    // private ImageButton menuMenu;
    public static ImageCanvas figure; //ShapeCanvas
    public int anzahlPunkteInRing; //Anzahl der Eckpunkte jedes Ringes
    public int anzahlRinge; //Anzahl der Ringe
    public ArrayList<P3D> polygonringe = new ArrayList<P3D>(); //nur aeussere Punkte
    public float[][] originalPunkte;
    //boolean xy = true;
    public boolean rotate = false;
    public boolean versch = false;
    public boolean zoom = false;
    public boolean isFilled = false; //Dreiecke wurden noch nicht aufgefuellt


    //private ImageView figure;  //now in customized ShapeCanvas-class

    private int item = -1;

    private String pictureImagePath = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fourth);

        Toolbar customToolbar = (Toolbar) findViewById(R.id.menuToolbarFA);
        setSupportActionBar(customToolbar);

        //TODO - test
        figure = (ImageCanvas) findViewById(R.id.triCanvas); //ShapeCanvas
        figure.initCPaint();

        //uebergebene Punkte und Dreiecke in ShapeCanvas

        figure.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                figure.onTouchEvent(event);
                return true;
            }
        });

        /*anzahlPunkteInRing= getIntent().getIntExtra("anzahlPunkteImRing", 0);
        anzahlRinge= getIntent().getIntExtra("anzahlRinge",0);

        //figure.fourthActivity = true;
        rebuildPointsVersch(getIntent().getFloatArrayExtra("x"), getIntent().getFloatArrayExtra("y"), getIntent().getFloatArrayExtra("z"));
        rebuildTriangles(getIntent().getIntArrayExtra("p0"),getIntent().getIntArrayExtra("p1"),getIntent().getIntArrayExtra("p2"));
        rebuildRing(getIntent().getIntArrayExtra("kompletterRing"));

        try {
            fillBottom();
        }catch (NullPointerException e){
            Toast.makeText(FourthActivity.this, "NullPointerException", Toast.LENGTH_SHORT).show();
        }catch (IndexOutOfBoundsException e){
            Toast.makeText(FourthActivity.this, "IndexOutOfBoundsException", Toast.LENGTH_SHORT).show();
        }

        figure.setPointsToDraw();*/
        mockup1();
        this.figure.anzahlPunkteInRing = this.anzahlPunkteInRing;
        copy();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if(!isFilled){
            firstTwoRings();
        }
    }

    public void copy(){
        this.originalPunkte = new float[this.figure.model.points.size()][3];
        for(int i=0;i<this.figure.model.points.size();i++){
            this.originalPunkte[i][0]=this.figure.model.points.get(i).x;
            this.originalPunkte[i][1]=this.figure.model.points.get(i).y;
            this.originalPunkte[i][2]=this.figure.model.points.get(i).z;
        }
    }

    public void reset(){
        for(int i=0;i<this.figure.model.points.size();i++){
            this.figure.model.points.get(i).x=this.originalPunkte[i][0];
            this.figure.model.points.get(i).y=this.originalPunkte[i][1];
            this.figure.model.points.get(i).z=this.originalPunkte[i][2];
        }
        this.figure.changePointsToDraw();
    }

    public void rebuildPoints(float[] x, float[] y, float[] z){
        for(int i=0;i<x.length;i++){
            figure.model.addPointToMesh(new P3D(x[i],y[i],z[i]));
        }
    }

    public void rebuildPointsVersch(float[] x, float[] y, float[] z){
        for(int i=0;i<x.length;i++){
            figure.model.addPointToMesh(new P3D(x[i],y[i],z[i]));
        }
    }

    public void rebuildTriangles(int[] p0, int[] p1, int[] p2){
        for(int i=0;i<p0.length;i++){
            figure.model.addTriangleToMesh(new Tri3D(figure.model.points.get(p0[i]),figure.model.points.get(p1[i]),figure.model.points.get(p2[i])));
        }
    }

    public void rebuildRing(int[] ring){
        for(int i=0;i<ring.length;i++){
            polygonringe.add(figure.model.points.get(ring[i]));
        }
    }

    public void setExtreme(){
        this.figure.extremL = this.polygonringe.get(0);
        this.figure.extremR = this.polygonringe.get(0);
        for(int i=1;i<anzahlPunkteInRing;i++){
            if(this.polygonringe.get(i).x<this.figure.extremL.x){
                this.figure.extremL=this.polygonringe.get(i);
            }
            if(this.polygonringe.get(i).x>this.figure.extremR.x){
                this.figure.extremR=this.polygonringe.get(i);
            }
        }
    }

    /*public void mockup2(){
        int maxHeight = Resources.getSystem().getDisplayMetrics().heightPixels;
        int maxWidth = Resources.getSystem().getDisplayMetrics().widthPixels;
        int abstandHoehe = maxHeight/8;
        int abstandBreite = maxWidth/5;

        figure.points.add(new P3D(abstandBreite,abstandHoehe,20));
        figure.points.add(new P3D(2*abstandBreite,abstandHoehe,10));
        figure.points.add(new P3D(3*abstandBreite,abstandHoehe,20));
        figure.points.add(new P3D(2*abstandBreite,abstandHoehe,30));

        figure.points.add(new P3D(abstandBreite,5*abstandHoehe,20));
        figure.points.add(new P3D(2*abstandBreite,5*abstandHoehe,10));
        figure.points.add(new P3D(3*abstandBreite,5*abstandHoehe,20));
        figure.points.add(new P3D(2*abstandBreite,5*abstandHoehe,30));

        figure.addTri(figure.points.get(0),figure.points.get(1),figure.points.get(2));
        figure.addTri(figure.points.get(0),figure.points.get(3),figure.points.get(2));

        figure.addTri(figure.points.get(4),figure.points.get(5),figure.points.get(6));
        figure.addTri(figure.points.get(4),figure.points.get(7),figure.points.get(6));

        for(int i=0;i<figure.points.size();i++){
            polygonringe.add(figure.points.get(i));
        }

        this.anzahlPunkteInRing=4;
        this.anzahlRinge=2;

    }*/

    public void mockup1(){
        int maxHeight = Resources.getSystem().getDisplayMetrics().heightPixels;
        int maxWidth = Resources.getSystem().getDisplayMetrics().widthPixels;
        int abstandHoehe = maxHeight/8;
        int abstandBreite = maxWidth/5;


        //figure.canvasTypeTri=false;
        figure.model.addPointToMesh(new P3D(abstandBreite,abstandHoehe,20));
        figure.model.addPointToMesh(new P3D(2*abstandBreite,abstandHoehe,10));
        figure.model.addPointToMesh(new P3D(3*abstandBreite,abstandHoehe,10));
        figure.model.addPointToMesh(new P3D(4*abstandBreite,abstandHoehe,20));

        figure.extremR=figure.model.points.get(figure.model.points.size()-1);

        figure.model.addPointToMesh(new P3D(3*abstandBreite,abstandHoehe,30));
        figure.model.addPointToMesh(new P3D(2*abstandBreite,abstandHoehe,30));

        int abstandMitte = (3*abstandBreite)/5;

        figure.model.addPointToMesh(new P3D(abstandBreite+abstandMitte,3*abstandHoehe,15));
        figure.model.addPointToMesh(new P3D(abstandBreite+2*abstandMitte,3*abstandHoehe,5));
        figure.model.addPointToMesh(new P3D(abstandBreite+3*abstandMitte,3*abstandHoehe,5));
        figure.model.addPointToMesh(new P3D(abstandBreite+4*abstandMitte,3*abstandHoehe,15));
        figure.model.addPointToMesh(new P3D(abstandBreite+3*abstandMitte,3*abstandHoehe,25));
        figure.model.addPointToMesh(new P3D(abstandBreite+2*abstandMitte,3*abstandHoehe,25));

        figure.model.addPointToMesh(new P3D(abstandBreite,5*abstandHoehe,20));
        figure.model.addPointToMesh(new P3D(2*abstandBreite,5*abstandHoehe,10));
        figure.model.addPointToMesh(new P3D(3*abstandBreite,5*abstandHoehe,10));
        figure.model.addPointToMesh(new P3D(4*abstandBreite,5*abstandHoehe,20));
        figure.model.addPointToMesh(new P3D(3*abstandBreite,5*abstandHoehe,30));
        figure.model.addPointToMesh(new P3D(2*abstandBreite,5*abstandHoehe,30));

        for(int i=0;i<figure.model.points.size();i++){
            polygonringe.add(figure.model.points.get(i));
        }

        figure.model.addTriangleToMesh(new Tri3D(figure.model.points.get(0),figure.model.points.get(1),figure.model.points.get(5)));
        figure.model.addTriangleToMesh(new Tri3D(figure.model.points.get(1),figure.model.points.get(4),figure.model.points.get(5)));
        figure.model.addTriangleToMesh(new Tri3D(figure.model.points.get(1),figure.model.points.get(2),figure.model.points.get(4)));
        figure.model.addTriangleToMesh(new Tri3D(figure.model.points.get(2),figure.model.points.get(3),figure.model.points.get(4)));

        figure.model.addTriangleToMesh(new Tri3D(figure.model.points.get(12),figure.model.points.get(13),figure.model.points.get(17)));
        figure.model.addTriangleToMesh(new Tri3D(figure.model.points.get(13),figure.model.points.get(14),figure.model.points.get(17)));
        figure.model.addTriangleToMesh(new Tri3D(figure.model.points.get(14),figure.model.points.get(16),figure.model.points.get(17)));
        figure.model.addTriangleToMesh(new Tri3D(figure.model.points.get(14),figure.model.points.get(15),figure.model.points.get(16)));

        this.anzahlPunkteInRing=6;
        this.anzahlRinge=3;
        this.figure.setPointsToDraw();
        for(int i=0;i<figure.model.triangles.size()/2;i++){
            this.figure.deckel.add(this.figure.model.triangles.get(i));
        }
        for(int i=figure.model.triangles.size()/2;i<figure.model.triangles.size();i++){
            this.figure.boden.add(this.figure.model.triangles.get(i));
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu1) {
        getMenuInflater().inflate(R.menu.menu_toolbar4, menu1);
        //MenuItem item = menu.findItem(R.id.action_rotatePic);//which menuItem?
        MenuItem item = menu1.findItem(R.id.action_takePicFA);
        item.setVisible(false);
        item = menu1.findItem(R.id.action_takePicSA);
        item.setVisible(false);
        item = menu1.findItem(R.id.action_rotatePicFA);
        item.setVisible(false);
        item = menu1.findItem(R.id.action_newTriFA);
        item.setVisible(false);
        item = menu1.findItem(R.id.action_movePtFA);
        item.setVisible(false);
        item = menu1.findItem(R.id.action_selectPtFA);
        item.setVisible(false);
        item = menu1.findItem(R.id.action_selectTriFA);
        item.setVisible(false);
        item = menu1.findItem(R.id.action_delTriFA);
        item.setVisible(false);
        item = menu1.findItem(R.id.action_nextActivityFA);
        item.setVisible(false);
        item = menu1.findItem(R.id.action_rotateX);
        item.setVisible(rotate);
        item = menu1.findItem(R.id.action_rotateY);
        item.setVisible(rotate);
        item = menu1.findItem(R.id.action_rotateZ);
        item.setVisible(rotate);
		item = menu1.findItem(R.id.action_rotatePicSA);
        item.setVisible(false);
        item = menu1.findItem(R.id.action_newPointSA);
        item.setVisible(false);
        item = menu1.findItem(R.id.action_selectSA);
        item.setVisible(true);
        item = menu1.findItem(R.id.action_delPointSA);
        item.setVisible(false);
        item = menu1.findItem(R.id.action_delEvSA);
        item.setVisible(false);
        item = menu1.findItem(R.id.action_nextActivitySA);
        item.setVisible(false);
        item = menu1.findItem(R.id.action_XZ); //xz-Koordinaten werden angezeigt
        item.setVisible(false);
        item = menu1.findItem(R.id.action_rotate);
        item.setVisible(true);
        item = menu1.findItem(R.id.action_versch);
        item.setVisible(true);
        item = menu1.findItem(R.id.action_verschOben);
        item.setVisible(versch);
        item = menu1.findItem(R.id.action_verschUnten);
        item.setVisible(versch);
        item = menu1.findItem(R.id.action_verschLinks);
        item.setVisible(versch);
        item = menu1.findItem(R.id.action_verschRechts);
        item.setVisible(versch);
        item = menu1.findItem(R.id.action_zoom);
        item.setVisible(true);
        item = menu1.findItem(R.id.action_zoomPlus);
        item.setVisible(zoom);
        item = menu1.findItem(R.id.action_zoomMinus);
        item.setVisible(zoom);
        item = menu1.findItem(R.id.action_fill);
        item.setVisible(true);
        item = menu1.findItem(R.id.action_undo);
        item.setVisible(!versch);
        item = menu1.findItem(R.id.action_reset);
        item.setVisible(true);
        //item.setVisible((picTaken && figure.canvasTypeTri) ^ picTaken);
        return true;
    }


    public void firstTwoRings(){
        if(anzahlRinge<2){
            return;
        }
        int IndexExLinks=0;
        int IndexExRechts=this.figure.model.points.indexOf(figure.extremR); //uebertragen aus dritter Acticity?
        float minAbstandLinks=this.polygonringe.get(IndexExLinks).x;
        if(minAbstandLinks>this.polygonringe.get(IndexExLinks+anzahlPunkteInRing).x){
            minAbstandLinks=this.polygonringe.get(IndexExLinks+anzahlPunkteInRing).x;
        }
        float minAbstandRechts=Resources.getSystem().getDisplayMetrics().widthPixels-this.polygonringe.get(IndexExRechts).x;
        if(minAbstandRechts>Resources.getSystem().getDisplayMetrics().widthPixels-this.polygonringe.get(IndexExRechts+anzahlPunkteInRing).x){
            minAbstandRechts=this.polygonringe.get(IndexExRechts+anzahlPunkteInRing).x;
        }
        float minAbstand;
        if(minAbstandLinks<minAbstandRechts){
            minAbstand=minAbstandLinks;
        }else{
            minAbstand=minAbstandRechts;
        }
        minAbstand=minAbstand-20; //Durchmesser von gemalten Punkten auf Bildschirm
        float k=Resources.getSystem().getDisplayMetrics().widthPixels-(minAbstand*2);
        float scalefactor = Resources.getSystem().getDisplayMetrics().widthPixels/k;
        this.figure.zoom(scalefactor);
        float ringmitte=(polygonringe.get(IndexExLinks).y+polygonringe.get(IndexExLinks+anzahlPunkteInRing).y
                +polygonringe.get(IndexExRechts).y+polygonringe.get(IndexExRechts+anzahlPunkteInRing).y)/4;
        //float bildschirmmitte=Resources.getSystem().getDisplayMetrics().heightPixels/2;
        float bildmitte=figure.getHeight()/2;
        float versch = bildmitte-ringmitte;
        figure.upOrDown(versch);
    }

    public void fillDown(){ //kopiere die Dreiecke, die sich zwischen Ring 1 und Ring 2 befinden, nach unten
        if(anzahlRinge==2||figure.model.triangles.size()==0||figure.newTriangles==0){ //wenn es nur zwei Ringe oder gar keine Dreiecke im Modell oder keine neu hinzugefuegten gibt
            return;
        }
        int counter = figure.model.triangles.size()-figure.newTriangles;
        Tri3D currentTriangle = figure.model.triangles.get(counter);
        P3D p0,p1,p2;
        int pos0, pos1, pos2;
        int neueDreiecke=figure.newTriangles;
        for(int i=1;i<=anzahlRinge-2;i++){
            for(int j=0;j<neueDreiecke;j++){
                pos0=this.polygonringe.indexOf(currentTriangle.getp0())+(i*anzahlPunkteInRing);
                pos1=this.polygonringe.indexOf(currentTriangle.getp1())+(i*anzahlPunkteInRing);
                pos2=this.polygonringe.indexOf(currentTriangle.getp2())+(i*anzahlPunkteInRing);
                p0 = this.polygonringe.get(pos0);
                p1 = this.polygonringe.get(pos1);
                p2 = this.polygonringe.get(pos2);
                figure.model.addTriangleToMesh(new Tri3D(p0,p1,p2));
                currentTriangle = figure.model.triangles.get(++counter);
                figure.newTriangles++;
            }
        }
        figure.invalidate();
    }

    public void fillSide(){ //fuelle die nicht sichtbaren Seiten des Modells mithilfe der manuell eingefuegten Dreiecke auf
        int neededTriangles = anzahlPunkteInRing*2;
        int anzahl=this.figure.model.triangles.size();
        int counter = anzahl-figure.newTriangles;
        if(anzahl==0||figure.newTriangles==0){
            return;
        }
        Tri3D currentTriangle = figure.model.triangles.get(counter);
        P3D p0,p1,p2;
        int pos0, pos1, pos2;
        int schrittweite = figure.newTriangles/2;
        while(figure.newTriangles<neededTriangles){
            pos0=this.polygonringe.indexOf(currentTriangle.getp0());
            pos1=this.polygonringe.indexOf(currentTriangle.getp1());
            pos2=this.polygonringe.indexOf(currentTriangle.getp2());

            if((pos0+schrittweite)%this.anzahlPunkteInRing==0){
                pos0=(pos0+schrittweite)-this.anzahlPunkteInRing;
            }else{
                pos0=pos0+schrittweite;
            }
            if((pos1+schrittweite)%this.anzahlPunkteInRing==0){
                pos1=(pos1+schrittweite)-this.anzahlPunkteInRing;
            }else{
                pos1=pos1+schrittweite;
            }
            if((pos2+schrittweite)%this.anzahlPunkteInRing==0){
                pos2=(pos2+schrittweite)-this.anzahlPunkteInRing;
            }else{
                pos2=pos2+schrittweite;
            }
            p0 = this.polygonringe.get(pos0);
            p1 = this.polygonringe.get(pos1);
            p2 = this.polygonringe.get(pos2);
            figure.model.addTriangleToMesh(new Tri3D(p0,p1,p2));
            if(anzahl<figure.model.triangles.size()){ //wenn das Dreieck tatsaechlich eingefuehrt wurde in addTriangleToMesh
                anzahl= figure.model.triangles.size();
                figure.newTriangles++;
            }
            if(++counter<anzahl) {
                currentTriangle = figure.model.triangles.get(counter);
            }
        }
        figure.setOperationID(1); //???
        figure.invalidate();
    }

    public void fillSideOld(){
        //Tri3D currentTriangle = figure.firstNewTriangle;
        int anzahl= figure.model.triangles.size();

        if(anzahl==0||figure.newTriangles==0){
            return;
        }

        int counter = anzahl-figure.newTriangles;
        Tri3D currentTriangle = figure.model.triangles.get(counter);
        P3D p0,p1,p2;
        int pos0, pos1, pos2;

        for(int i=1;i<=this.anzahlPunkteInRing;i++){
            for(int j=0;j<figure.newTriangles;j++){
                pos0=this.polygonringe.indexOf(currentTriangle.getp0());
                pos1=this.polygonringe.indexOf(currentTriangle.getp1());
                pos2=this.polygonringe.indexOf(currentTriangle.getp2());

                if((pos0+1)%this.anzahlPunkteInRing==0){
                    pos0=(pos0+1)-this.anzahlPunkteInRing;
                }else{
                    pos0=pos0+1;
                }
                if((pos1+1)%this.anzahlPunkteInRing==0){
                    pos1=(pos1+1)-this.anzahlPunkteInRing;
                }else{
                    pos1=pos1+1;
                }
                if((pos2+1)%this.anzahlPunkteInRing==0){
                    pos2=(pos2+1)-this.anzahlPunkteInRing;
                }else{
                    pos2=pos2+1;
                }
                p0 = this.polygonringe.get(pos0);
                p1 = this.polygonringe.get(pos1);
                p2 = this.polygonringe.get(pos2);
                figure.model.addTriangleToMesh(new Tri3D(p0,p1,p2));
                anzahl= figure.model.triangles.size();
                if(++counter<anzahl) {
                    currentTriangle = figure.model.triangles.get(counter);
                }
                //currentTriangle = currentTriangle.getNextTriangle();
            }
        }
        figure.setOperationID(1);
        figure.invalidate();
    }

    public void fillBottom(){ //kopiere die Dreiecke des "Deckels" des Modells (TopView) auf den Boden des Modells
        int abstandLetzterRing=this.polygonringe.size()-this.anzahlPunkteInRing;
        for(int i=0;i<this.figure.model.triangles.size();i++){
            int pos0=this.polygonringe.indexOf(figure.model.triangles.get(i).getp0())+abstandLetzterRing;
            int pos1=this.polygonringe.indexOf(figure.model.triangles.get(i).getp1())+abstandLetzterRing;
            int pos2=this.polygonringe.indexOf(figure.model.triangles.get(i).getp2())+abstandLetzterRing;
            figure.model.addTriangleToMesh(new Tri3D(this.polygonringe.get(pos0),this.polygonringe.get(pos1),this.polygonringe.get(pos2)));
        }

    }

    /*public int lookUp(P3D point){
       for(int i=0;i<this.polygonringe.size();i++){
            if(this.polygonringe.get(i)==point){
                return i;
            }
        }
        return  0;
    }*/
    //TODO - strings done.
    //TODO - rewrite TriangleOperations in ShapeCanvas to use PointF and operationBooleans

    @Override   //TODO - change string ref of action_openDropdownMenu to last used action
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.action_fill:
                //figure.setRotation(figure.getRotation() + 90);
                if(isFilled){
                    figure.xy=!figure.xy;
                    figure.invalidate();
                    return true;
                }
                try {
                    fillSide();
                    //fillSide();
                }catch (NullPointerException e){
                    Toast.makeText(FourthActivity.this, "NullPointerException Side", Toast.LENGTH_SHORT).show();
                }catch (IndexOutOfBoundsException e){
                    Toast.makeText(FourthActivity.this, "IndexOutOfBoundsException Side", Toast.LENGTH_SHORT).show();
                }catch (Exception e){
                    Toast.makeText(FourthActivity.this, "SomeOtherException Side", Toast.LENGTH_SHORT).show();
                }
                try {
                    fillDown();
                    //fillSide();
                }catch (NullPointerException e){
                    Toast.makeText(FourthActivity.this, "NullPointerException Down", Toast.LENGTH_SHORT).show();
                }catch (IndexOutOfBoundsException e){
                    Toast.makeText(FourthActivity.this, "IndexOutOfBoundsException Down", Toast.LENGTH_SHORT).show();
                }catch (Exception e){
                    Toast.makeText(FourthActivity.this, "SomeOtherException Down", Toast.LENGTH_SHORT).show();
                }
                isFilled=true;
                for(int i=this.figure.boden.size()*2;i<this.figure.model.triangles.size();i++){
                    this.figure.seiten.add(this.figure.model.triangles.get(i));
                }
                reset();
                return true;

            case R.id.action_newTriFA:    //TODO: CrossClassConnect
                figure.setOperationID(1);
                Toast.makeText(FourthActivity.this, "Neues Dreieck", Toast.LENGTH_SHORT).show();
                Toast.makeText(FourthActivity.this, "OperationID "+figure.getOperationID() , Toast.LENGTH_SHORT).show();
                return true;
            /*
            case R.id.action_optTriVertices:    //TODO: CCConnect
                //automatisch ausführen bei addTriangle?
                TriangleOperations.connect();
                return true;
            */

            case R.id.action_movePtFA:    //TODO: CCConnect
                figure.setOperationID(2);
                Toast.makeText(FourthActivity.this, "Punkt bewegen", Toast.LENGTH_SHORT).show();
                if(figure.getSelectedPoint()==null){
                    Toast.makeText(FourthActivity.this, "KEIN PUNKT AUSGEWÄHLT!", Toast.LENGTH_LONG).show();
                }
                return true;

            case R.id.action_selectPtFA:    //TODO: CCConnect
                figure.setOperationID(3);
                Toast.makeText(FourthActivity.this, "Punkt auswählen", Toast.LENGTH_SHORT).show();
                return true;

            case R.id.action_selectTriFA:    //TODO: CCConnect
                figure.setOperationID(0);
                Toast.makeText(FourthActivity.this, "Dreieck auswählen", Toast.LENGTH_SHORT).show();
                return true;

            case R.id.action_delTriFA:    //TODO: CCConnect - need Method to select Triangle! (in CANVAS?)
                figure.deleteTri(figure.getSelectedTri()); // .delete(TriangleOperations.getSelectedTriangle());
                Toast.makeText(FourthActivity.this, "Ausgewähltes Dreieck löschen", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.action_undo:
                try {
                    this.figure.undo();
                    this.figure.invalidate();
                }catch (NullPointerException e){
                    Toast.makeText(FourthActivity.this, "NullPointerException", Toast.LENGTH_SHORT).show();
                }catch (IndexOutOfBoundsException e){
                    Toast.makeText(FourthActivity.this, "IndexOutOfBoundsException", Toast.LENGTH_SHORT).show();
                }
                return true;

            case R.id.action_reset:
                try {
                    reset();
                    this.figure.invalidate();
                }catch (NullPointerException e){
                    Toast.makeText(FourthActivity.this, "NullPointerException", Toast.LENGTH_SHORT).show();
                }catch (IndexOutOfBoundsException e){
                    Toast.makeText(FourthActivity.this, "IndexOutOfBoundsException", Toast.LENGTH_SHORT).show();
                }
                return true;

            case R.id.action_rotateX:
                try {
                    this.figure.rotateXAxis();
                    figure.invalidate();
                }catch (NullPointerException e){
                    Toast.makeText(FourthActivity.this, "NullPointerException", Toast.LENGTH_SHORT).show();
                }catch (IndexOutOfBoundsException e){
                    Toast.makeText(FourthActivity.this, "IndexOutOfBoundsException", Toast.LENGTH_SHORT).show();
                }
                return true;
            case R.id.action_rotateY:
                try {
                    this.figure.rotateYAxis();
                    figure.invalidate();
                }catch (NullPointerException e){
                    Toast.makeText(FourthActivity.this, "NullPointerException", Toast.LENGTH_SHORT).show();
                }catch (IndexOutOfBoundsException e){
                    Toast.makeText(FourthActivity.this, "IndexOutOfBoundsException", Toast.LENGTH_SHORT).show();
                }
                return true;
            case R.id.action_rotateZ:
                try {
                    this.figure.rotateZAxis();
                    figure.invalidate();
                }catch (NullPointerException e){
                    Toast.makeText(FourthActivity.this, "NullPointerException", Toast.LENGTH_SHORT).show();
                }catch (IndexOutOfBoundsException e){
                    Toast.makeText(FourthActivity.this, "IndexOutOfBoundsException", Toast.LENGTH_SHORT).show();
                }
                return true;
            case R.id.action_XZ:
                try {
                    this.figure.xy=!this.figure.xy;
                    figure.changePointsToDraw();
                }catch (NullPointerException e){
                    Toast.makeText(FourthActivity.this, "NullPointerException", Toast.LENGTH_SHORT).show();
                }catch (IndexOutOfBoundsException e){
                    Toast.makeText(FourthActivity.this, "IndexOutOfBoundsException", Toast.LENGTH_SHORT).show();
                }
                return true;
                //startActivity(new Intent(this, SecondActivity.class));
                /*Intent intent = new Intent(getApplicationContext(), SecondActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                if(figure.canvasTypeTri) {
                    if (figure.getFirstTriangle() != null) {
                        intent.putExtra("Dreiecke", figure.getTriangleArray());
                    }
                }else {
                    if(figure.points!=null) {
                        intent.putExtra("XPunkte", figure.getPointArray('x'));
                        intent.putExtra("YPunkte", figure.getPointArray('y'));
                        intent.putExtra("ZPunkte", figure.getPointArray('z'));
                    }
                }
                if (pictureImagePath != null) {
                    intent.putExtra("Pfad", pictureImagePath);
                }
                //figure.buildDrawingCache();
                //Bitmap bitmap = figure.getDrawingCache();
                //intent.putExtra("Bitmap", bitmap);
		        intent.putExtra("Typ",figure.canvasTypeTri);
                startActivity(intent);

                return true;*/

            //former 2ndAct-Actions
            case R.id.action_rotate:
                this.rotate=true;
                this.versch=false;
                this.zoom=false;
                invalidateOptionsMenu();
                return true;
            case R.id.action_versch:
                this.rotate=false;
                this.versch=true;
                this.zoom=false;
                invalidateOptionsMenu();
                return true;
            case R.id.action_zoom:
                this.rotate=false;
                this.versch=false;
                this.zoom=true;
                invalidateOptionsMenu();
                return true;
            case R.id.action_verschOben:
                this.figure.up();
                return true;
            case R.id.action_verschUnten:
                this.figure.down();
                return true;
            case R.id.action_verschLinks:
                this.figure.left();
                return true;
            case R.id.action_verschRechts:
                this.figure.right();
                return true;
            case R.id.action_zoomPlus:
                this.figure.zoomPlus();
                return true;
            case R.id.action_zoomMinus:
                this.figure.zoomMinus();
                return true;

			case R.id.action_newPointSA:
                figure.setOperationID(1);
                Toast.makeText(FourthActivity.this, "Neuer Punkt", Toast.LENGTH_SHORT).show();
                Toast.makeText(FourthActivity.this, "OperationID "+figure.getOperationID() , Toast.LENGTH_SHORT).show();
                return true;

            case R.id.action_delPointSA:
                figure.setOperationID(2);
                Toast.makeText(FourthActivity.this, "Punkt loeschen", Toast.LENGTH_SHORT).show();
                if(figure.getSelectedPointIndex()==-1){
                    Toast.makeText(FourthActivity.this, "KEIN PUNKT AUSGEWÄHLT!", Toast.LENGTH_LONG).show();
                }
                return true;

            case R.id.action_delEvSA:
                figure.setOperationID(3);
                Toast.makeText(FourthActivity.this, "Alles loeschen", Toast.LENGTH_SHORT).show();
                return true;

            case R.id.action_selectSA:
                figure.setOperationID(0);
                Toast.makeText(FourthActivity.this, "Punkt auswaehlen", Toast.LENGTH_SHORT).show();
                return true;

            default:
                // n.def.action
                // Invoke superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    /** Defining button click listener for the OK button of the alert dialog window */
    @Override
    public void onPositiveClick(int item) {

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


}
