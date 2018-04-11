package com.example.core.peyepeliner;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Region;
import android.support.annotation.NonNull;
import android.support.v4.graphics.ColorUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;

import java.util.ArrayList;

/**
 * Created by Core on 27.09.2017.
 */
class Dreieck{
    Path path;
    float z;
    Paint paint;
    int pos;
    float x,y;
    public Dreieck(Tri3D tri, Path path, int pos){
        this.path=path;
        this.z = tri.getp0().z+tri.getp1().z+tri.getp2().z;
        this.x = tri.getp0().x;
        this.y = tri.getp0().y;
        //this.z = Math.max(tri.getp0().z, Math.max(tri.getp1().z, tri.getp2().z));
        paint=tri.getColour();
        this.pos = pos;
    }
    /*Dreieck currentDrei;
            Dreiecke.clear();
            int position;
            while (i<model.triangles.size()) {
                currentTri = model.triangles.get(i);
                currentDrei = new Dreieck(currentTri, pathify(currentTri), i++);
                position = Dreiecke.size();
                for (int j = Dreiecke.size() - 1; j >= 0; j--) {
                    if (currentDrei.z < Dreiecke.get(j).z) {
                        break;
                    } else {
                        position = j;
                    }
                }
                Dreiecke.add(position, currentDrei);
                //currentTri = currentTri.getNextTriangle();
            }
            isSorted();
            Dreieck d;
            for(int h=0;h<Dreiecke.size();h++){
                d=Dreiecke.get(h);
                canvas.drawPath(d.path, d.paint);   //draw filled Tri
                canvas.drawPath(d.path, contourPaint); //draw Tri borders
                //canvas.drawText(d.pos+"",d.x,d.y,new Paint(Color.CYAN));
            }*/
}

public class ImageCanvas extends ImageView {

    //public ArrayList<Dreieck> Dreiecke = new ArrayList<>();
    public boolean isAxisObject = false;
    public Model3D model = new Model3D();
    public P3D extremL, extremR; // aus vorheriger Activity mit uebertragen -> vllt auch nur Indices??
    //ArrayList mit Punkten
    //enthält zuerst nur 2/3 der koordinaten, wird in 3rdactivity vervollständigt
    //public ArrayList<P3D> points = new ArrayList<P3D>();
    //boolean for type: TriCanvas = true, PoiCanvas = false
    //public Boolean canvasTypeTri = true;
    public ArrayList<PointF> pointsToDraw = new ArrayList<PointF>(); //2D-Koordinaten auf Bildschirm der 3-dimensionalen Punkte
    public boolean xy = true; //werden x und y Koordinaten gemalt oder x und z Koordinaten

    public ArrayList<Tri3D> boden = new ArrayList<Tri3D>();
    public ArrayList<Tri3D> deckel = new ArrayList<Tri3D>();
    public ArrayList<Tri3D> seiten = new ArrayList<Tri3D>();

    //public boolean fourthActivity = false;
    public int extremZ = 20;
    public P3D point1, point2, point3;
    public Tri3D firstNewTriangle; //erstes in FourthActivity hinzugefuegtes Dreieck
    public int newTriangles=0; // Anzahl der in Fourth Activity hinzugefuegten Dreiecke

    private Paint contourPaint = new Paint();   //for contours
    private Paint pointMarker = new Paint();
    private Paint selectMarker = new Paint();
    private Paint[] pointMarkers= new Paint[5];
    public int anzahlPunkteInRing;

    private int operationID = 0;
    //vars for newTriangle
    //PointFs for newTriangleCreation
    private PointF p1 = new PointF();
    private PointF p2 = new PointF();
    private PointF p3 = new PointF();
    private int newTriangleId = 0;
    //how many points are already set?
    private int pointsNb = 0;

    //vars for moveOperation
    //PointFs weil 2-D-Operation
    PointF moveFrom = new PointF();
    PointF moveTo = new PointF();

    //vars for generic operations
    private Tri3D firstTriangle;
    private Tri3D lastTriangle;
    private Tri3D selectedTriangle;
    private P3D selectedPoint;
    private int anzahl = 0;
    public int selectedPointIndex = -1; //Position von selectedPoint in ArrayList


    public ImageCanvas(Context context) {
        super(context);
    }

    public ImageCanvas(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ImageCanvas(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public int getAnzahl(){
        return this.anzahl;
    }

    public void initCPaint() {
        contourPaint.setStyle(Paint.Style.STROKE);
        contourPaint.setColor(Color.BLACK);
        contourPaint.setStrokeWidth(5);
        pointMarker.setStyle(Paint.Style.FILL);
        pointMarker.setColor(Color.MAGENTA);
        pointMarker.setStrokeWidth(2);
        selectMarker.setStyle(Paint.Style.STROKE);
        selectMarker.setColor(Color.YELLOW);
        selectMarker.setStrokeWidth(10);

        pointMarkers[0] = new Paint();
        pointMarkers[0].setStyle(Paint.Style.FILL);
        pointMarkers[0].setColor(Color.MAGENTA);
        pointMarkers[0].setStrokeWidth(2);
        pointMarkers[1] = new Paint();
        pointMarkers[1].setStyle(Paint.Style.FILL);
        pointMarkers[1].setColor(Color.BLUE);
        pointMarkers[1].setStrokeWidth(2);
        pointMarkers[2] = new Paint();
        pointMarkers[2].setStyle(Paint.Style.FILL);
        pointMarkers[2].setColor(Color.GREEN);
        pointMarkers[2].setStrokeWidth(2);
        pointMarkers[3] = new Paint();
        pointMarkers[3].setStyle(Paint.Style.FILL);
        pointMarkers[3].setColor(Color.WHITE);
        pointMarkers[3].setStrokeWidth(2);
        pointMarkers[4] = new Paint();
        pointMarkers[4].setStyle(Paint.Style.FILL);
        pointMarkers[4].setColor(Color.RED);
        pointMarkers[4].setStrokeWidth(2);
    }

    public void setPointsToDraw(){
        for(int i=0;i<model.points.size();i++){
            pointsToDraw.add(new PointF(model.points.get(i).x, model.points.get(i).y));
        }
    }

    public void rotateXAxis(){
        float yStrich, zStrich;
        float verschY = getMiddleY();
        float verschZ = getMiddleZ();
        for(P3D currentPt : model.points){
            currentPt.y=currentPt.y-verschY;
            currentPt.z=currentPt.z-verschZ;
            //currentPt.x=currentPt.x-(1/2*breite);
        }
        /*for(P3D currentPt : model.points){
            currentPt.y=currentPt.y-(1/2*hoehe);
        }*/
        for(int i=0;i<model.points.size();i++){
            yStrich = (float)(Math.cos(Math.toRadians(30))*model.points.get(i).y-(Math.sin(Math.toRadians(30))*model.points.get(i).z));
            zStrich = (float)(Math.sin(Math.toRadians(30))*model.points.get(i).y+(Math.cos(Math.toRadians(30))*model.points.get(i).z));
            model.points.get(i).y=yStrich;
            model.points.get(i).z=zStrich;
        }
        for(P3D currentPt : model.points){
            currentPt.y=currentPt.y+verschY;
            currentPt.z=currentPt.z+verschZ;
            //currentPt.x=currentPt.x-(1/2*breite);
        }
        /*for(P3D currentPt : model.points){
            currentPt.y=currentPt.y+(1/2*hoehe);
        }*/
        /*for(int i=0;i<model.points.size();i++){
            pointsToDraw.get(i).y=model.points.get(i).y;
        }*/
        changePointsToDraw();
        this.invalidate();
    }

    public void rotateYAxis(){
        float xStrich,zStrich;
        float verschX = getMiddleX();
        float verschZ = getMiddleZ();
        for(P3D currentPt : model.points){
            currentPt.x=currentPt.x-verschX;
            currentPt.z=currentPt.z-verschZ;
            //currentPt.x=currentPt.x-(1/2*breite);
        }
        for(int i=0;i<model.points.size();i++){
            xStrich = (float)(Math.cos(Math.toRadians(30))*model.points.get(i).x+(Math.sin(Math.toRadians(30))*model.points.get(i).z));
            zStrich = (float)(-Math.sin(Math.toRadians(30))*model.points.get(i).x+(Math.cos(Math.toRadians(30))*model.points.get(i).z));
            model.points.get(i).x=xStrich;
            model.points.get(i).z=zStrich;
        }
        for(P3D currentPt : model.points){
            currentPt.x=currentPt.x+verschX;
            currentPt.z=currentPt.z+verschZ;
            //currentPt.x=currentPt.x+(1/2*breite);
        }
        changePointsToDraw();
        /*for(int i=0;i<model.points.size();i++){
            pointsToDraw.get(i).x=model.points.get(i).x;
        }*/
        this.invalidate();
    }

    public void rotateZAxis(){
        float xStrich,yStrich;
        float verschX = getMiddleX();
        float verschY = getMiddleY();
        for(P3D currentPt : model.points){
            currentPt.x=currentPt.x-verschX;
            currentPt.y=currentPt.y-verschY;
            //currentPt.x=currentPt.x-(1/2*breite);
        }
        for(int i=0;i<model.points.size();i++){
            xStrich=(float)(Math.cos(Math.toRadians(30))*model.points.get(i).x-(Math.sin(Math.toRadians(30))*model.points.get(i).y));
            yStrich=(float)(Math.sin(Math.toRadians(30))*model.points.get(i).x+(Math.cos(Math.toRadians(30))*model.points.get(i).y));
            model.points.get(i).x=xStrich;
            model.points.get(i).y=yStrich;
        }
        for(P3D currentPt : model.points){
            currentPt.x=currentPt.x+verschX;
            currentPt.y=currentPt.y+verschY;
            //currentPt.x=currentPt.x-(1/2*breite);
        }
        changePointsToDraw();
        /*for(int i=0;i<model.points.size();i++){
            pointsToDraw.get(i).x=model.points.get(i).x;
            pointsToDraw.get(i).y=model.points.get(i).y;
        }*/
        this.invalidate();
    }

    public float getMiddleY(){
        float middle=0;
        for(int i=0;i<model.points.size();i++){
            middle = middle+model.points.get(i).y;
        }
        return (middle/model.points.size());
    }

    public float getMiddleX(){
        float middle=0;
        for(int i=0;i<model.points.size();i++){
            middle = middle+model.points.get(i).x;
        }
        return (middle/model.points.size());
    }

    public float getMiddleZ(){
        float middle=0;
        for(int i=0;i<model.points.size();i++){
            middle = middle+model.points.get(i).z;
        }
        return (middle/model.points.size());
    }

    public void changePointsToDraw(){
        //if(xy){
            for (int i = 0; i < model.points.size(); i++) {
                this.pointsToDraw.get(i).x = this.model.points.get(i).x;
                this.pointsToDraw.get(i).y = this.model.points.get(i).y;
            }
        /*}else {
            for (int i = 0; i < model.points.size(); i++) {
                this.pointsToDraw.get(i).x = this.model.points.get(i).x;
                this.pointsToDraw.get(i).y = this.model.points.get(i).z;
            }
        }*/
        this.invalidate();
    }

    public void up(){
        for(int i=0;i<this.model.points.size();i++){
            this.model.points.get(i).y=this.model.points.get(i).y-10;
        }
        changePointsToDraw();
        invalidate();
    }

    public void down(){
        for(int i=0;i<this.model.points.size();i++){
            this.model.points.get(i).y=this.model.points.get(i).y+10;
        }
        changePointsToDraw();
        invalidate();
    }

    public void upOrDown(float versch){
        for(int i=0;i<this.model.points.size();i++){
            this.model.points.get(i).y=this.model.points.get(i).y+versch;
        }
        changePointsToDraw();
        invalidate();
    }

    public void left(){
        for(int i=0;i<this.model.points.size();i++){
            this.model.points.get(i).x=this.model.points.get(i).x-10;
        }
        changePointsToDraw();
        invalidate();
    }

    public void right(){
        for(int i=0;i<this.model.points.size();i++){
            this.model.points.get(i).x=this.model.points.get(i).x+10;
        }
        changePointsToDraw();
        invalidate();
    }

    public void zoomPlus(){
        float xStrich,yStrich,zStrich;
        float verschX = getMiddleX();
        float verschY = getMiddleY();
        float verschZ = getMiddleZ();
        for(P3D currentPt : model.points){
            currentPt.x=currentPt.x-verschX;
            currentPt.y=currentPt.y-verschY;
            currentPt.z=currentPt.z-verschZ;
        }
        for(P3D currentPt : model.points){
            currentPt.x=(float)(currentPt.x*1.1);
            currentPt.y=(float)(currentPt.y*1.1);
            currentPt.z=(float)(currentPt.z*1.1);
        }
        for(P3D currentPt : model.points){
            currentPt.x=currentPt.x+verschX;
            currentPt.y=currentPt.y+verschY;
            currentPt.z=currentPt.z+verschZ;
        }
        changePointsToDraw();
        invalidate();
    }

    public void zoomMinus(){
        float xStrich,yStrich,zStrich;
        float verschX = getMiddleX();
        float verschY = getMiddleY();
        float verschZ = getMiddleZ();
        for(P3D currentPt : model.points){
            currentPt.x=currentPt.x-verschX;
            currentPt.y=currentPt.y-verschY;
            currentPt.z=currentPt.z-verschZ;
        }
        for(P3D currentPt : model.points){
            currentPt.x=(float)(currentPt.x*0.9);
            currentPt.y=(float)(currentPt.y*0.9);
            currentPt.z=(float)(currentPt.z*0.9);
        }
        for(P3D currentPt : model.points){
            currentPt.x=currentPt.x+verschX;
            currentPt.y=currentPt.y+verschY;
            currentPt.z=currentPt.z+verschZ;
        }
        changePointsToDraw();
        invalidate();
    }

    public void zoom(float zoomfactor){
        float xStrich,yStrich,zStrich;
        float verschX = getMiddleX();
        float verschY = getMiddleY();
        float verschZ = getMiddleZ();
        for(P3D currentPt : model.points){
            currentPt.x=currentPt.x-verschX;
            currentPt.y=currentPt.y-verschY;
            currentPt.z=currentPt.z-verschZ;
        }
        for(P3D currentPt : model.points){
            currentPt.x=(float)(currentPt.x*zoomfactor);
            currentPt.y=(float)(currentPt.y*zoomfactor);
            currentPt.z=(float)(currentPt.z*zoomfactor);
        }
        for(P3D currentPt : model.points){
            currentPt.x=currentPt.x+verschX;
            currentPt.y=currentPt.y+verschY;
            currentPt.z=currentPt.z+verschZ;
        }
        changePointsToDraw();
        invalidate();
    }

    public void undo(){
        switch(pointsNb){
            case 0:
                if(newTriangles>0){
                    this.model.triangles.remove(this.model.triangles.size()-1);
                    newTriangles--;
                }
                return;
            case 1:
                this.selectedPoint=null;
                point1=null;
                pointsNb--;
                return;
            case 2:
                this.selectedPoint=null;
                point2=null;
                pointsNb--;
                return;
        }
    }

    public Tri3D getFirstTriangle(){
        return this.firstTriangle;
    }

    public void setOperationID(int x){
        //mit test auf validität
        if (x<=4 && x>=0){
            this.operationID = x;
        }else{
            this.operationID = 0;    // "Dreieck auswählen" als standard-fallback
        }
    }

    public int getOperationID(){
        return this.operationID;
    }

    //done - T only.
    public void setSelectedTri(int x, int y){    //methode zum setten und einfärben von selectedTriangle
        //if(canvasTypeTri){
            if(this.selectedTriangle!=null){
                //this.selectedTriangle.setColor(Color.white);
                this.selectedTriangle.setColour(Color.WHITE);
            }
            this.selectedTriangle = selectTri(x,y);
            if(this.selectedTriangle!=null){
                //this.selectedTriangle.setColor(Color.cyan);
                this.selectedTriangle.setColour(Color.CYAN);
            }
        //}
    }

    public Tri3D getSelectedTri(){
        return this.selectedTriangle;
    }

    /*public void setSelectedPoint(int x, int y){ //methode zum setten: selectedPoint|selectedPointIndex
        if(!canvasTypeTri&&points.isEmpty()){
            return;
        }
        if(canvasTypeTri&&this.firstTriangle==null){
            return;
        }
        PointF checkPos = new PointF(x, y);
        this.setSelectedPoint(getClosestPoint(checkPos));
        //Index:
        this.selectedPointIndex = this.points.indexOf(this.selectedPoint);
    }*/

    public void setSelectedPoint4(int x, int y){ //methode zum setten: selectedPoint|selectedPointIndex
        if(model.points.isEmpty()){
            return;
        }
        PointF checkPos = new PointF(x, y);
        this.setSelectedPoint(getClosestPoint4(checkPos));
        //Index:
        if(this.selectedPoint!=null){
            this.selectedPointIndex = this.model.points.indexOf(this.selectedPoint);
        }
    }

    public P3D getClosestPoint4(PointF pos){
        //this.populatePointList();
        P3D currPt = this.model.points.get(0);
        double currAbstand = abstand(pos, currPt.getPointF());
        P3D pointInArrayList;
        int lastPointToCheck;
        if(anzahlPunkteInRing*2<=this.model.points.size()){ //sollte immer true sein, da jedes Modell mindestens zwei Ringe haben muesste
            lastPointToCheck=anzahlPunkteInRing*2;
        }else{
            lastPointToCheck=this.model.points.size(); //falls es doch nur einen Ring gibt
        }
        for (int i=1;i<lastPointToCheck;i++) { //nur die ersten beiden Ringe
            pointInArrayList=this.model.points.get(i);
            if (abstand(pos, pointInArrayList.getPointF()) < currAbstand && i%anzahlPunkteInRing<=this.model.points.indexOf(extremR) ) {
                currPt = pointInArrayList;
                currAbstand = abstand(pos, currPt.getPointF());
            }
        }
        return currPt;
        //return new P3D(0, 0);   //IF NO POINT FOUND, FALLBACK TO THIS ONE
    }

    public P3D getClosestPoint4Temp(PointF pos){
        ArrayList<P3D> closestPoints = new ArrayList<P3D>();
        //this.populatePointList();
        //P3D currPt = this.model.points.get(0);
        double abstand = 30;
        //double currAbstand = abstand(pos, currPt.getPointF());
        for (P3D pointInArrayList : this.model.points) {
            if (abstand(pos, pointInArrayList.getPointF()) < abstand) {
                closestPoints.add(pointInArrayList);
                //currPt = pointInArrayList;
                //currAbstand = abstand(pos, currPt.getPointF());
            }
        }
        if(closestPoints.isEmpty()){
            return null;
        }
        P3D closest = closestPoints.get(0);
        for(P3D currPt : closestPoints){
            if(currPt.z<closest.z){
                closest=currPt;
            }
        }
        return closest;
        //return new P3D(0, 0);   //IF NO POINT FOUND, FALLBACK TO THIS ONE
    }



    public void capturePoints4(float x, float y){
        if(this.pointsNb==0){
            point1 = getClosestPoint4(new PointF(x,y));
            if(point1!=null) {
                this.pointsNb = 1;
            }
            return;
        }
        if(this.pointsNb==1){
            point2 = getClosestPoint4(new PointF(x,y));
            if(point2!=null) {
                if(point2.compare(point1)){ //zweiter Punkt des Dreiecks darf nicht gleich dem ersten Punkt des Dreiecks sein
                    point2=null;
                }else {
                    this.pointsNb = 2;
                }
            }
            return;
        }
        if(this.pointsNb==2){
            point3 = getClosestPoint4(new PointF(x,y));
            if(point3.compare(point1)||point3.compare(point2)){ // dritter Punkt muss unterschiedlich zu ersten und zweitem Punkt sein
                point3=null;
            }
            if(point3!=null) {
                this.pointsNb = 0;
                int alt = this.model.triangles.size();
                this.model.addTriangleToMesh(new Tri3D(point1, point2, point3));
                if(alt<this.model.triangles.size()) {
                    ++this.newTriangles;
                    try {
                        sortInNewTriangle();
                    }catch (NullPointerException e){
                        this.model.triangles.get(this.model.triangles.size()-1).setColour(Color.GREEN);
                    }catch (IndexOutOfBoundsException e){
                        this.model.triangles.get(this.model.triangles.size()-1).setColour(Color.RED);
                    }
                }
            }
        }
    }

    public void sortInNewTriangle(){ // sortiert ein neu manuell hinzugefuegtes Dreieck richtig ein (von links nach rechts)
        if(newTriangles<=1){ //nur ein neues Dreieck => keine Sortierung notwendig
            return;
        }
        int firstNew = this.model.triangles.size()-newTriangles; //TO-DO: als globale Variable abspeichern, aendert sich ja nicht
        Tri3D lastAddedTri = this.model.triangles.get(this.model.triangles.size()-1); //letztes hinzugefuegtes Dreieck
        Tri3D currentTri;
        int position=this.model.triangles.size()-1; //spätere richtige Position des Dreiecks
        for(int i=this.model.triangles.size()-2;i>=firstNew;i--){ //NOCHMAL UEBERPRUEFEN
            currentTri = this.model.triangles.get(i);
            if(IndexModulSumme(lastAddedTri)<IndexModulSumme(currentTri)){
                position=i;
            }else{
               break; // richtige Stelle wurde gefunden
            }
        }
        if(position<this.model.triangles.size()-1) {
            this.model.triangles.remove(lastAddedTri);
            this.model.triangles.add(position, lastAddedTri);
        }else{
            return;
        }
    }

    public int IndexModulSumme(Tri3D dreieck){
        int p0 = this.model.points.indexOf(dreieck.getp0())%anzahlPunkteInRing;
        int p1 = this.model.points.indexOf(dreieck.getp1())%anzahlPunkteInRing;
        int p2 = this.model.points.indexOf(dreieck.getp2())%anzahlPunkteInRing;
        return (p0+p1+p2);
    }

    public void setSelectedPoint(P3D p){
        this.selectedPoint = p;
    }

    public P3D getSelectedPoint(){
        return this.selectedPoint;
    }

    public int getSelectedPointIndex(){
        return this.selectedPointIndex;
    }

    public double abstand(PointF p1, PointF p2){
        return Math.sqrt(((p1.x-p2.x)*(p1.x-p2.x))+((p1.y-p2.y)*(p1.y-p2.y)));
    }

    //done - applies to T, but simply does nothing in P.
    /*public void populatePointList(){
        if(canvasTypeTri){
            this.points.clear();
        }
        //this.points.clear(); not globally, since it equals DELETE_EVERYTHING in P.
        //iteriere über triangles, check eckpunkte auf präsenz, vervollständige
        Tri3D currentTri = this.firstTriangle;
        P3D pointTBA;
        while(currentTri!=null){
            //point0
            pointTBA = currentTri.getp0();
            if(!this.points.contains(pointTBA)){
                this.points.add(pointTBA);
            }
            //point1
            pointTBA = currentTri.getp1();
            if(!this.points.contains(pointTBA)){
                this.points.add(pointTBA);
            }
            //point2
            pointTBA = currentTri.getp2();
            if(!this.points.contains(pointTBA)){
                this.points.add(pointTBA);
            }
            //continue...
            currentTri=currentTri.getNextTriangle();
        }
    }*/

    /*public void addTri(PointF point1, PointF point2, PointF point3){

        P3D newPoint1 = new P3D(point1);
        P3D newPoint2 = new P3D(point2);
        P3D newPoint3 = new P3D(point3);
        Tri3D newTriangle = new Tri3D(newPoint1, newPoint2, newPoint3, newTriangleId++);

        if(this.firstTriangle==null){
            this.firstTriangle = newTriangle;
            this.lastTriangle = this.firstTriangle;
            this.anzahl = 1;
        }else {
            this.lastTriangle.setNextTriangle(newTriangle);
            this.lastTriangle = newTriangle;
            //this.lastTriangle = this.lastTriangle.getNextTriangle();
            this.anzahl++;
        }
        //TODO - adjust connectTri(tri) for 1080x1920-screen resolution (10px too small) - Wurstfingersyndrom
        connectTri(newTriangle);    //works, BUT: need to adjust distance values regarding screenResolution...
        //try: 50?
    }*/

    /*public void addTri(P3D point1, P3D point2, P3D point3){ // wird in rebuildFormerTriangle aufgerufen

        Tri3D newTriangle = new Tri3D(point1, point2, point3, newTriangleId++);

        if(this.firstTriangle==null){
            this.firstTriangle = newTriangle;
            this.lastTriangle = this.firstTriangle;
            this.anzahl = 1;
        }else {
            this.lastTriangle.setNextTriangle(newTriangle);
            this.lastTriangle = newTriangle;
            //this.lastTriangle = this.lastTriangle.getNextTriangle();
            this.anzahl++;
        }
        //TODO - adjust connectTri(tri) for 1080x1920-screen resolution (10px too small) - Wurstfingersyndrom
        connectTri(newTriangle);    //works, BUT: need to adjust distance values regarding screenResolution...
        //try: 50?
    }*/

    /*public void capturePoints(float x, float y){
        if(this.pointsNb==0){
            this.p1.set(x,y);
            this.pointsNb = 1;
            return;
        }
        if(this.pointsNb==1){
            this.p2.set(x,y);
            this.pointsNb = 2;
            return;
        }
        if(this.pointsNb==2){
            this.p3.set(x,y);
            this.pointsNb = 0;
            this.addTri(this.p1,this.p2,this.p3);
        }
    }*/

    public void deleteTri(Tri3D toBeDeleted){
        //TODO - implement - done.
        if(toBeDeleted==null){
            return;
        }
        if(toBeDeleted.getId()==firstTriangle.getId()){
            firstTriangle = firstTriangle.getNextTriangle();
        }else{
            Tri3D prevTriangle = firstTriangle;
            Tri3D currentTriangle = firstTriangle.getNextTriangle();
            while(currentTriangle!=null){
                if(currentTriangle.getId()==toBeDeleted.getId()){
                    prevTriangle.setNextTriangle(currentTriangle.getNextTriangle());
                    break;
                }else{
                    prevTriangle = currentTriangle;
                    currentTriangle = currentTriangle.getNextTriangle();
                }
            }
            if(prevTriangle.getNextTriangle()==null){
                lastTriangle = prevTriangle;
            }
        }
        this.anzahl--;
        this.invalidate();
    }

    //done - P only.
    /*public void deletePoint(){
        if(!canvasTypeTri){
            if(this.selectedPointIndex!=-1) {
                this.points.remove(this.selectedPoint);
            }
        }
    }*/

    /*public void deleteEverything(){
        this.points.clear();
    }*/

    private Tri3D selectTri(int x, int y){   //interne methode zum auswählen eines dreiecks
        Tri3D currentTriangle = this.firstTriangle;
        Region currentTriReg = new Region();
        while(currentTriangle!=null){
            currentTriReg.setPath(currentTriangle.pathify(), currentTriangle.clip);
            if(currentTriReg.contains(x, y)){
                //currentTriangle.setColor(Color.yellow);
                break;
            }else{
                currentTriangle = currentTriangle.getNextTriangle();
            }
        }
        return currentTriangle;
    }

    /*public P3D getClosestPoint(PointF pos){
        this.populatePointList();
        P3D currPt = this.points.get(0);
        double currAbstand = abstand(pos, currPt.getPointF());
        for (P3D pointInArrayList : this.points) {
            if (abstand(pos, pointInArrayList.getPointF()) < currAbstand) {
                currPt = pointInArrayList;
                currAbstand = abstand(pos, currPt.getPointF());
            }
        }
        if(!canvasTypeTri){
            return currPt;
        }
        //return currPt;
        Tri3D currentTriangle = this.firstTriangle;
        while(currentTriangle!=null) {
            //check:does currentTriangle use currPoint?
            if (currentTriangle.getp0().equals(currPt)) {
                return currentTriangle.getp0();
            }
            if (currentTriangle.getp1().equals(currPt)) {
                return currentTriangle.getp1();
            }
            if (currentTriangle.getp2().equals(currPt)) {
                return currentTriangle.getp2();
            }
            currentTriangle = currentTriangle.getNextTriangle();
        }
        return new P3D(0, 0);   //IF NO POINT FOUND, FALLBACK TO THIS ONE
    }*/



    public void adjustSelectedPt(PointF fromWhere, PointF toWhere){    //move point of multiple triangles (w/o collision detect)
        //check on this.selectedPoint
        if(this.getSelectedPoint()==null){
            return;
        }
        //calc vector and reuse dX,dY for new coords
        //man muss nicht mehr den punkt verschieben, sondern kann egal wo auf dem schirm rumfahren.
        //die koordinaten des selectedPoint verändern sich trotzdem.
        float dX = toWhere.x - fromWhere.x;
        float dY = toWhere.y - fromWhere.y;
        //calc move-endpoint
        this.selectedPoint.x += dX;
        this.selectedPoint.y += dY;
        //re-set calculation start point to avoid summation of vector during prolonged movement
        moveFrom.set(toWhere);
    }

    //done - T only.
    /*public void connectTri(Tri3D tri){
        if(canvasTypeTri){
            if(tri==null){
                return;
            }
            if(this.anzahl==1){
                return;
            }
            Tri3D currentTriangle = this.firstTriangle;
            while(currentTriangle!=null){
                if(currentTriangle.getId()!=tri.getId()){   //connect tri and currentTriangle unless .equals()
                    tri.connect(currentTriangle);
                }
                currentTriangle = currentTriangle.getNextTriangle();
            }
        }
    }*/

    //done - both.
    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {
        float x = event.getX();     //.getRawX();
        float y = event.getY();     //.getRawY();
        //boolean firstPointSet = false;
        //boolean secondPointSet = false;
        //int pointsNb = 0;
        switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    switch (this.operationID){
                        case 1:
                            break;

                        case 2: // Punkt loeschen
                            //deletePoint();
                            invalidate();
                            break;

                        case 3: // alle Punkte loeschen
                            //Anfrage, ob man wirklich alles loeschen will?
                            //deleteEverything();
                            invalidate();
                            break;

                        //NEED THIS FOR ACTIVITY 3!
                        case 4:
                            setSelectedPoint4(Math.round(x),Math.round(y));
                            invalidate();
                            break;

                        default:
                            capturePoints4(Math.round(x),Math.round(y));
                            invalidate();
                            break;
                    }
                    invalidate();
                    break;
            /*case MotionEvent.ACTION_MOVE:
                if (this.operationID == 2){
                    moveTo.set(x,y);    //where to move (moveFrom is set on ACTION_DOWN, and thus already determined)?
                    //adjustPoint(moveFrom, moveTo);
                    //adjustSelectedPt(moveFrom, moveTo);
                }
                invalidate();
                break;
            */
                case MotionEvent.ACTION_UP: //was passiert wenn finger von touchoberfläche gehoben? - nix.
                    invalidate();
                    break;

                case MotionEvent.ACTION_CANCEL: //~eq. above.
                    //point = null;
                    invalidate();
                    break;
            }
            return true;
    }

    //done - both.
    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);
        if(isAxisObject){   //FALL: ist achsenobjekt (also keine dreiecke, nur punkte und kanten)
            //punkte (mittelpunkt ist leicht größer und weiss):
            canvas.drawCircle(pointsToDraw.get(0).x, pointsToDraw.get(0).y, 10, pointMarkers[3]);
            canvas.drawCircle(pointsToDraw.get(1).x, pointsToDraw.get(1).y, 5, pointMarkers[2]);
            canvas.drawCircle(pointsToDraw.get(2).x, pointsToDraw.get(2).y, 5, pointMarkers[4]);
            canvas.drawCircle(pointsToDraw.get(3).x, pointsToDraw.get(3).y, 5, pointMarkers[1]);

            //achsen:
            canvas.drawLine (pointsToDraw.get(0).x, pointsToDraw.get(0).y, pointsToDraw.get(1).x, pointsToDraw.get(1).y, pointMarkers[2]);
            canvas.drawLine (pointsToDraw.get(0).x, pointsToDraw.get(0).y, pointsToDraw.get(2).x, pointsToDraw.get(2).y, pointMarkers[4]);
            canvas.drawLine (pointsToDraw.get(0).x, pointsToDraw.get(0).y, pointsToDraw.get(3).x, pointsToDraw.get(3).y, pointMarkers[1]);

            //wenn isAxisObject: abbruch hiernach!
            return;
        }

        int punkte=0;
        int farbe=0;
            for (int i = 0; i < model.points.size(); i++) {
                //if(points.get(i).z<=extremZ) {
                    /*if (selectedPointIndex == i) {
                        canvas.drawCircle(pointsToDraw.get(i).x, pointsToDraw.get(i).y, 20, selectMarker);
                    } else {*/
                    canvas.drawCircle(pointsToDraw.get(i).x, pointsToDraw.get(i).y, 20, pointMarkers[farbe]);
                    //}
                    if (this.pointsNb == 1) {
                        canvas.drawCircle(this.point1.x, this.point1.y, 20, selectMarker);
                    }
                    if (this.pointsNb == 2) {
                        canvas.drawCircle(this.point1.x, this.point1.y, 20, selectMarker);
                        canvas.drawCircle(this.point2.x, this.point2.y, 20, selectMarker);
                    }
                    if (selectedPointIndex == i) {
                        canvas.drawCircle(pointsToDraw.get(i).x, pointsToDraw.get(i).y, 20, selectMarker);
                    }
                    if(++punkte%anzahlPunkteInRing==0){
                        if(++farbe>=pointMarkers.length){
                            farbe=0;
                        }
                    }
                //}
            }
            /*for(int i = 0; i < model.points.size(); i++){
                canvas.drawText(""+i, pointsToDraw.get(i).x,pointsToDraw.get(i).y, new Paint(Color.CYAN));
            }*/


            int i=0;
            Tri3D currentTri;
            if(!xy){
                int h=0;
                while (h < model.triangles.size()) {
                    currentTri = model.triangles.get(h++);
                    canvas.drawPath(pathify(currentTri), currentTri.getColour());   //draw filled Tri
                    canvas.drawPath(pathify(currentTri), contourPaint); //draw Tri borders
                    // currentTri = currentTri.getNextTriangle();
                }
                return;
            }
            try{
                float maxDeckel = this.model.points.get(0).z; //points=Ring
                float maxBoden = this.model.points.get(this.model.points.size()-anzahlPunkteInRing).z;
                int maxPos=0;
                for(int j=1;j<anzahlPunkteInRing;j++){
                    if(maxDeckel<this.model.points.get(j).z){
                        maxDeckel=this.model.points.get(j).z;
                        maxPos=j;
                    }
                    if(maxBoden<this.model.points.get(this.model.points.size()-anzahlPunkteInRing+j).z){
                        maxBoden=this.model.points.get(this.model.points.size()-anzahlPunkteInRing+j).z;
                    }
                }
                if(maxBoden>maxDeckel){
                    for(int j=0;j<boden.size();j++){
                        currentTri = boden.get(j);
                        canvas.drawPath(pathify(currentTri), currentTri.getColour());   //draw filled Tri
                        canvas.drawPath(pathify(currentTri), contourPaint); //draw Tri borders
                    }
                }else{
                    for(int j=0;j<deckel.size();j++){
                        currentTri = deckel.get(j);
                        canvas.drawPath(pathify(currentTri), currentTri.getColour());   //draw filled Tri
                        canvas.drawPath(pathify(currentTri), contourPaint); //draw Tri borders
                    }
                }
                seiten.clear();
                for(int h=2*boden.size();h<model.triangles.size();h++){
                    /*if(h%2==0){
                        model.triangles.get(h).setColour(Color.GREEN);
                    }else{
                        model.triangles.get(h).setColour(Color.BLUE);
                    }*/
                    seiten.add(model.triangles.get(h)); //nur zu Testzwecken, dauerndes Umaendern der Liste unnoetig!
                }
                boolean[] dreiecke = new boolean[seiten.size()];
                for(int h=0;h<dreiecke.length;h++){
                    dreiecke[h]=false;
                }
                if(seiten.size()>0) {
                    int hinten = hinten(model.points.get(maxPos));
                    i = 0;
                    int ebenen = this.model.points.size() / anzahlPunkteInRing - 1;
                    int next;
                    for (int j = 0; j < ebenen; j++) {
                        next = hinten + (j * anzahlPunkteInRing * 2);
                        if(next>=seiten.size()||i>=seiten.size()){
                            break;
                        }
                        currentTri = seiten.get(next);
                        dreiecke[next]=true;
                        canvas.drawPath(pathify(currentTri), currentTri.getColour());
                        canvas.drawPath(pathify(currentTri), contourPaint);
                        i++;
                    }
                    int weiter1 = hinten, weiter2 = hinten;
                    //for (int h=0;h<2*anzahlPunkteInRing;h++) {
                    while(i<seiten.size()){
                        /*if(i>=seiten.size()){
                            break;
                        }*/
                        if (--weiter1 < 0) {
                            weiter1 = (anzahlPunkteInRing * 2)-1;
                        }
                        if (++weiter2 >= anzahlPunkteInRing * 2) {
                            weiter2 = 0;
                        }
                        for(int j = 0; j < ebenen; j++){
                            next = weiter1 + (j * anzahlPunkteInRing * 2);
                            if(next<seiten.size()&&dreiecke[next]==false) {
                                currentTri = seiten.get(next);
                                dreiecke[next]=true;
                                canvas.drawPath(pathify(currentTri), currentTri.getColour());
                                canvas.drawPath(pathify(currentTri), contourPaint);
                                i++;
                                if (i == seiten.size()) {
                                    break;
                                }
                            }
                            next = weiter2 + (j * anzahlPunkteInRing * 2);
                            if(next<seiten.size()&&dreiecke[next]==false) {
                                currentTri = seiten.get(next);
                                dreiecke[next]=true;
                                canvas.drawPath(pathify(currentTri), currentTri.getColour());
                                canvas.drawPath(pathify(currentTri), contourPaint);
                                i++;
                                if (i == seiten.size()) {
                                    break;
                                }
                            }
                        }
                    }
                }

                int nicht = 0;
                for(int h=0;h<dreiecke.length;h++){
                    if(dreiecke[h]==false){
                        nicht++;
                    }
                }
                if(nicht>0){
                    canvas.drawText(nicht+"", 50, 50, new Paint(Color.RED));
                }

                if(maxBoden<=maxDeckel){
                    for(int j=0;j<boden.size();j++){
                        currentTri = boden.get(j);
                        canvas.drawPath(pathify(currentTri), currentTri.getColour());   //draw filled Tri
                        canvas.drawPath(pathify(currentTri), contourPaint); //draw Tri borders
                    }
                }else{
                    for(int j=0;j<deckel.size();j++){
                        currentTri = deckel.get(j);
                        canvas.drawPath(pathify(currentTri), currentTri.getColour());   //draw filled Tri
                        canvas.drawPath(pathify(currentTri), contourPaint); //draw Tri borders
                    }
                }

            }catch (Exception e) {
                contourPaint.setColor(Color.RED);
                int h=0;
                while (h < model.triangles.size()) {
                    currentTri = model.triangles.get(h++);
                    canvas.drawPath(pathify(currentTri), currentTri.getColour());   //draw filled Tri
                    canvas.drawPath(pathify(currentTri), contourPaint); //draw Tri borders
                    // currentTri = currentTri.getNextTriangle();
                }
            }

    }

    public int hinten(P3D punkt){
        Tri3D currentTri;
        float max=Integer.MIN_VALUE;
        int index=0;
        for(int i=0;i<seiten.size();i++){
            currentTri=seiten.get(i);
            if(currentTri.getp0().compare(punkt)||currentTri.getp1().compare(punkt)||currentTri.getp0().compare(punkt)){
                if(max<currentTri.getp0().z+currentTri.getp1().z+currentTri.getp2().z){
                    max=currentTri.getp0().z+currentTri.getp1().z+currentTri.getp2().z;
                    index=i;
                }
            }
        }
        return index;
    }

    /*public void isSorted(){
        if(Dreiecke.size()==1){
            return;
        }
        for(int i=0;i<Dreiecke.size()-1;i++){
            if(Dreiecke.get(i).z<Dreiecke.get(i+1).z){
                contourPaint=new Paint(Color.RED);
            }
        }
    }*/

    public Path pathify(Tri3D currentTri){
        Path pathTriThis = new Path();
        int p0=model.points.indexOf(currentTri.getp0());
        int p1=model.points.indexOf(currentTri.getp1());
        int p2=model.points.indexOf(currentTri.getp2());
        pathTriThis.moveTo(pointsToDraw.get(p0).x, pointsToDraw.get(p0).y);
        pathTriThis.lineTo(pointsToDraw.get(p1).x, pointsToDraw.get(p1).y);
        pathTriThis.lineTo(pointsToDraw.get(p2).x, pointsToDraw.get(p2).y);
        pathTriThis.moveTo(pointsToDraw.get(p0).x, pointsToDraw.get(p0).y);
        pathTriThis.lineTo(pointsToDraw.get(p1).x, pointsToDraw.get(p1).y);
        pathTriThis.lineTo(pointsToDraw.get(p2).x, pointsToDraw.get(p2).y);
        pathTriThis.close();
        return pathTriThis;
    }

    public void rebuildFormerModel(){

    }

}
