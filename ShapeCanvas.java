package com.example.core.peyepeliner;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Region;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Core on 27.09.2017.
 */

public class ShapeCanvas extends ImageView {

    //ArrayList mit Punkten
    //enthält zuerst nur 2/3 der koordinaten, wird in 3rdactivity vervollständigt
    public ArrayList<P3D> points = new ArrayList<P3D>();
    //boolean for type: TriCanvas = true, PoiCanvas = false
    public Boolean canvasTypeTri = true;

    private Paint contourPaint = new Paint();   //for contours
    private Paint pointMarker = new Paint();
    private Paint selectMarker = new Paint();

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

    /*
    //ENTZERRUNG
    //wird noch nicht gebraucht, ergo: auskommentieren
    //3 eckpunkte des entzerrungs-dreiecks
    //TODO - blende dreieck ABC bei erstellung ein
    //TODO - blende ABC aus, sobald erste punkte gesetzt werden
    private boolean showRefTri = true;
    //werden calcOptimizedPoints() übergeben
    private PointF A;
    private PointF B;
    private PointF C;
    //hilfspunkte für entzerrungs-methoden
    private PointF peak;
    private PointF leftBase;
    private PointF rightBase;
    private PointF optLeftBase;
    private PointF optRightBase;
    private PointF optPeak;
    */

    public ShapeCanvas(Context context) {
        super(context);
    }

    public ShapeCanvas(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ShapeCanvas(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
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
        if(canvasTypeTri){
            if(this.selectedTriangle!=null){
                //this.selectedTriangle.setColor(Color.white);
                this.selectedTriangle.setColour(Color.WHITE);
            }
            this.selectedTriangle = selectTri(x,y);
            if(this.selectedTriangle!=null){
                //this.selectedTriangle.setColor(Color.cyan);
                this.selectedTriangle.setColour(Color.CYAN);
            }
        }
    }

    public Tri3D getSelectedTri(){
        return this.selectedTriangle;
    }

    public void setSelectedPoint(int x, int y){ //methode zum setten: selectedPoint|selectedPointIndex
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
    public void populatePointList(){
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
    }

    public void addTri(PointF point1, PointF point2, PointF point3){

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
    }

    public void addTri(P3D point1, P3D point2, P3D point3){ // wird in rebuildFormerTriangle aufgerufen

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
    }

    public void addTri2(P3D point1, P3D point2, P3D point3){ // wird in rebuildFormerTriangle aufgerufen

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
        connectTri2(newTriangle);
        //try: 50?
    }

    public void capturePoints(float x, float y){
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
    }

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
    public void deletePoint(){
        if(!canvasTypeTri){
            if(this.selectedPointIndex!=-1) {
                this.points.remove(this.selectedPoint);
                setSelectedPoint(null);
                this.selectedPointIndex=-1;
                invalidate();
            }
        }
    }

    public void deleteEverything(){
        this.points.clear();
        invalidate();
    }

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

    public P3D getClosestPoint(PointF pos){
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
    }

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
    public void connectTri(Tri3D tri){
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
    }

    public void connectTri2(Tri3D tri){ // ThirdActivity
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
                    tri.connect2(currentTriangle);
                }
                currentTriangle = currentTriangle.getNextTriangle();
            }
        }
    }


    //done - both.
    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {
        float x = event.getX();     //.getRawX();
        float y = event.getY();     //.getRawY();
        //boolean firstPointSet = false;
        //boolean secondPointSet = false;
        //int pointsNb = 0;
        if(canvasTypeTri){
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    switch (this.operationID){
                        case 1:
                            this.capturePoints(x, y);
                            invalidate();
                            break;

                        case 2:
                            moveFrom.set(x,y);
                            invalidate();
                            break;

                        case 3:
                            setSelectedPoint(Math.round(x),Math.round(y));
                            invalidate();
                            break;

                        case 4:
                            setSelectedPoint(Math.round(x),Math.round(y));
                            invalidate();
                            break;

                        default:
                            setSelectedTri(Math.round(x),Math.round(y));
                    }
                    invalidate();
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (this.operationID == 2){
                        moveTo.set(x,y);    //where to move (moveFrom is set on ACTION_DOWN, and thus already determined)?
                        //adjustPoint(moveFrom, moveTo);
                        adjustSelectedPt(moveFrom, moveTo);
                    }
                    invalidate();
                    break;

                case MotionEvent.ACTION_UP: //was passiert wenn finger von touchoberfläche gehoben? - nix.
                    //außer:
                    Tri3D currentTri=this.getFirstTriangle();
                    if(this.operationID==2){ //falls Punkt sich verschoben hat und zwei Punkte von zwei Dreiecken jetzt nahe zusammen sind
                        while(currentTri!=null){
                            connectTri(currentTri);
                            currentTri=currentTri.getNextTriangle();
                        }
                    }
                    invalidate();
                    break;

                case MotionEvent.ACTION_CANCEL: //~eq. above.
                    //point = null;
                    invalidate();
                    break;
            }
        }else{
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    switch (this.operationID){
                        case 1: // Punkte hinzufuegen
                            this.points.add(new P3D(x,y));
                            invalidate();
                            break;

                        case 2: // Punkt loeschen
                            deletePoint();
                            invalidate();
                            break;

                        case 3: // alle Punkte loeschen
                            //Anfrage, ob man wirklich alles loeschen will?
                            deleteEverything();
                            invalidate();
                            break;

                        //NEED THIS FOR ACTIVITY 3!
                        case 4:
                            setSelectedPoint(Math.round(x),Math.round(y));
                            invalidate();
                            break;

                        default:
                            setSelectedPoint(Math.round(x),Math.round(y));
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
        }
        return true;
    }

    //done - both.
    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);
        if (canvasTypeTri){
            //mark selected Point
            if (selectedPoint != null) {
                canvas.drawCircle(selectedPoint.x, selectedPoint.y, 30, selectMarker);
            }
            //zeige intermediate-Punkte an (neues Dreieck erstellen)
            if(this.pointsNb==1){
                canvas.drawCircle(this.p1.x, this.p1.y, 20, pointMarker);
            }
            if(this.pointsNb==2){
                canvas.drawCircle(this.p1.x, this.p1.y, 20, pointMarker);
                canvas.drawCircle(this.p2.x, this.p2.y, 20, pointMarker);
            }
            //iteriere über alle Dreiecke, male jedes.
            Tri3D currentTri = this.firstTriangle;
            while(currentTri!=null){
                canvas.drawPath(currentTri.pathify(),currentTri.getColour());   //draw filled Tri
                canvas.drawPath(currentTri.pathify(),contourPaint); //draw Tri borders
                currentTri=currentTri.getNextTriangle();
            }
        }else{
            for(int i=0;i<points.size();i++){
                if(selectedPointIndex==i){
                    canvas.drawCircle(points.get(i).x,points.get(i).y,20,selectMarker);
                }else {
                    canvas.drawCircle(points.get(i).x, points.get(i).y, 20, pointMarker);
                }
            }
        }
    }

    public float[] getPointArray(char coordinate){ //Zum Verschieben der Punkte von Activity zu Activity
        float[] pointArray = new float[points.size()];
        if(coordinate=='x'){
            for(int i=0;i<this.points.size();i++){
                pointArray[i] = this.points.get(i).x;
            }
            return pointArray;
        }
        if(coordinate=='y'){
            for(int i=0;i<this.points.size();i++){
                pointArray[i] = this.points.get(i).y;
            }
            return pointArray;
        }
        for(int i=0;i<this.points.size();i++){
            pointArray[i] = this.points.get(i).z;
        }
        return pointArray;
    }

    public void rebuildFormerPoints(float[] XPoints, float[] YPoints, float ZPoints[]){ //falls es in einer früheren Activity schon Punkte gab
        for(int i=0;i<XPoints.length;i++){
            this.points.add(new P3D(XPoints[i],YPoints[i],ZPoints[i]));
        }
    }

    public void rebuildFormerPoints(float[] XPoints, float[] YPoints, float ZPoints[], float scale, float verschX, float verschY){ //falls es in einer früheren Activity schon Punkte gab
        for(int i=0;i<XPoints.length;i++){
            this.points.add(new P3D(XPoints[i]*scale+verschX,(YPoints[i]-verschY)*scale,ZPoints[i]*scale));
        }
    }

    public float[] getTriangleArray(){ //Zum Verschieben der Dreiecke von Activity zu Activity
        int laenge = this.anzahl*3*3;
        float[] triangleArray = new float[laenge];
        Tri3D current = this.firstTriangle;
        for(int i=0;i<laenge;i++){
            triangleArray[i++] = current.getp0().x;
            triangleArray[i++] = current.getp0().y;
            triangleArray[i++] = current.getp0().z;
            triangleArray[i++] = current.getp1().x;
            triangleArray[i++] = current.getp1().y;
            triangleArray[i++] = current.getp1().z;
            triangleArray[i++] = current.getp2().x;
            triangleArray[i++] = current.getp2().y;
            triangleArray[i] = current.getp2().z;
            current = current.getNextTriangle();
        }
        return triangleArray;
    }

    public void rebuildFormerTriangles(float[] formerTriangles){ //falls es in einer früheren Activity schon Dreiecke gab
        for(int i=0;i<formerTriangles.length;i++){
            P3D p0 = new P3D(formerTriangles[i++], formerTriangles[i++],formerTriangles[i++]);
            P3D p1 = new P3D(formerTriangles[i++], formerTriangles[i++],formerTriangles[i++]);
            P3D p2 = new P3D(formerTriangles[i++], formerTriangles[i++],formerTriangles[i]);
            this.addTri2(p0, p1, p2);
        }
        invalidate();
    }

    public void rebuildFormerTriangles(float[] formerTriangles, float scale, float verschX, float verschY){ //falls es in einer früheren Activity schon Dreiecke gab
        for(int i=0;i<formerTriangles.length;i++){
            P3D p0 = new P3D(formerTriangles[i++]*scale+verschX, (formerTriangles[i++]-verschY)*scale,formerTriangles[i++]);
            P3D p1 = new P3D(formerTriangles[i++]*scale+verschX, (formerTriangles[i++]-verschY)*scale,formerTriangles[i++]);
            P3D p2 = new P3D(formerTriangles[i++]*scale+verschX, (formerTriangles[i++]-verschY)*scale,formerTriangles[i]);
            this.addTri2(p0, p1, p2);
        }
        invalidate();
    }


    /*
    //HEREAFTER BE ENTZERRUNG
    //entzerrung wird erst beim finalen übergeben aufgerufen (beim verbinden der 2-perspektiv-koordinaten)
    //determine I via intersect(AB, VP)
    //linear eq.: y=m*x+b
    private PointF intersect(float m1, float b1, float m2, float b2){
        float x = (b2-b1)/(m1-m2);
        float y = m1 * x + b1;
        return new PointF(x,y);
    }
    //m
    private float detPitch(PointF U, PointF V){
        if (U == V){
            return 0;
        }
        return (V.y - U.y)/(V.x - U.x);
    }
    //b
    private float detYIntersect(PointF U, PointF V){
        if (U == V){
            return 0;
        }
        return U.y - U.x * ((V.y - U.y)/(V.x - U.x));
    }
    //p1,p2,p3 are the 3 points to base the adjustment on
    private void calcOptimizedPoints(PointF p1, PointF p2, PointF p3){
        //find point with biggest Y (is peak C)
        peak = p1;
        //init leftBase|rightBase generically
        //leftBase = p2;
        //rightBase = p3;
        //det peak
        if(p2.y > peak.y){
            peak = p2;
        }
        if(p3.y > peak.y){
            peak = p3;
        }
        //find left-most remaining point (is fix. A)
        //remaining point is B
        if(peak == p1){
            //A,B are p2,p3
            if(p2.x < p3.x){
                leftBase = p2;
                rightBase = p3;
            }else{
                leftBase = p3;
                rightBase = p2;
            }
        }
        if(peak == p2){
            //A,B are p1,p3
            if(p1.x < p3.x){
                leftBase = p1;
                rightBase = p3;
            }else{
                leftBase = p3;
                rightBase = p1;
            }
        }
        if(peak == p3){
            //A,B are p1,p2
            if(p1.x < p2.x){
                leftBase = p1;
                rightBase = p2;
            }else{
                leftBase = p2;
                rightBase = p1;
            }
        }
        //calculate coords for optPeak, optRightBase, (optLeftBase)
        optLeftBase = leftBase;  //leftBase ist fixpunkt
        optRightBase = new PointF(rightBase.x, leftBase.y);
        optPeak = new PointF((leftBase.x+rightBase.x)/2, leftBase.y+(rightBase.x-leftBase.x)/2);
    }
    //V verschobener referenz-eckpunkt (p0->p1), A,B fixe eckpunkte (p2, p3), P bildpunkt
    public void recalcPointP(PointF p0, PointF p1, PointF p2, PointF p3, PointF P){
        //arbeite mit kopien, um originale eckpunkte für berechnung anderer bildpunkte gebrauchen zu können
        //NUR P WIRD TATSÄCHLICH VERSCHOBEN!
        PointF V = new PointF(p0.x, p0.y);
        //PointF V1 = new PointF(p1);
        PointF A = new PointF(p2.x, p2.y);
        PointF B = new PointF(p3.x, p3.y);
        //m1,b1 for ray VP
        float m1 = detPitch(V,P);
        float b1 = detYIntersect(V,P);
        //m2,b2 for opposing side of triangle (OSOT)
        float m2 = detPitch(A,B);
        float b2 = detYIntersect(A,B);
        //I lies on OSOT
        PointF I = intersect(m1, b1, m2, b2);
        //vect(VI)=k*vect(VP)
        double k = abstand(V,I)/abstand(V,P);
        //ADJUSTING V TO V' GOES HERE!
        V.set(p1);
        //vec(V'I) (V is now on pos V')
        float dX = I.x - V.x;
        float dY = I.y - V.y;
        //adjust P to P' (according to V->V' adjust)
        double adjustedX = V.x + (dX/k);
        double adjustedY = V.y + (dY/k);
        P.set((float)adjustedX, (float)adjustedY);
    }
    //methode: PointF P für gesamtes referenzdreieck entzerren
    public void overallAdjustPoint(PointF P){
        //methods to adjust each PointF P
        //recalcForPeak|leftBase|rightBase
        recalcPointP(peak,optPeak,leftBase,rightBase,P);
        //recalcForRightBase|leftBase|optPeak
        //since adjustment was made for peak->optPeak previously, optPeak replaces peak here
        recalcPointP(rightBase,optRightBase,leftBase,optPeak,P);
    }
    */
}
