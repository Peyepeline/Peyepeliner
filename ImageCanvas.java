package com.example.core.peyepeliner;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.Rect;
import android.graphics.Region;
import android.graphics.Shader;
import android.graphics.drawable.shapes.PathShape;
import android.graphics.drawable.shapes.RectShape;
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

/* <activity android:name=".FourthActivity"
            android:screenOrientation="portrait"
            android:configChanges="keyboardHidden|orientation|screenSize"
            android:parentActivityName=".ThirdActivity" >
            <!-- The meta-data tag is required if you support API level 15 and lower -->
            <meta-data
                android:name="android.support.PARENT_ACTIVITY"
                android:value=".ThirdActivity" />
        </activity>*/

public class ImageCanvas extends ImageView {

    private Bitmap bitmap;
    private BitmapShader btpShader;
    private BitmapShader shader;
    //private BitmapShader shader2;
    private Paint shapaint;
    //private Paint shapaint2;
    public Bitmap topViewBitmap;
    public BitmapShader topViewShader;
    public Paint topViewPaint;
    public Bitmap sideViewBitmap;
    public BitmapShader sideViewShader;
    public Paint sideViewPaint;
    public boolean photoTexture=false;
    Bitmap hilfsBitmap;
    Bitmap hilfsBitmap2;
    Bitmap hilfsBitmap3;
    public boolean bodenZuerst;
    public Rect origTopRect;
    public Rect origSideRect;
    private Rect seitenRechteck;
    public Rect sideImageOnScreen;
    public Rect topImageOnScreen;
    //Bitmap hilfsBitmap=Bitmap.createBitmap(Resources.getSystem().getDisplayMetrics().widthPixels,Resources.getSystem().getDisplayMetrics().heightPixels-100,Bitmap.Config.ARGB_8888);;
    //Bitmap hilfsBitmap2 = Bitmap.createBitmap(Resources.getSystem().getDisplayMetrics().widthPixels,Resources.getSystem().getDisplayMetrics().heightPixels-100,Bitmap.Config.ARGB_8888);
    //Bitmap hilfsBitmap3 = Bitmap.createBitmap(Resources.getSystem().getDisplayMetrics().widthPixels,Resources.getSystem().getDisplayMetrics().heightPixels-100,Bitmap.Config.ARGB_8888);
    Canvas canvas2 = new Canvas();
    Canvas canvas3 = new Canvas();
    public ArrayList<Tri3D> Dreiecke = new ArrayList<>();
    public ArrayList<Tri3D> zuletztHinzugefuegt=new ArrayList<>();
    public Model3D model = new Model3D();
    public P3D extremL, extremR; // aus vorheriger Activity mit uebertragen -> vllt auch nur Indices??
    //ArrayList mit Punkten
    //enthält zuerst nur 2/3 der koordinaten, wird in 3rdactivity vervollständigt
    //public ArrayList<P3D> points = new ArrayList<P3D>();
    //boolean for type: TriCanvas = true, PoiCanvas = false
    //public Boolean canvasTypeTri = true;
    public ArrayList<PointF> pointsToDraw = new ArrayList<PointF>(); //2D-Koordinaten auf Bildschirm der 3-dimensionalen Punkte
    public boolean xy = true; //werden x und y Koordinaten gemalt oder x und z Koordinaten
    public boolean textures=false;

    public ArrayList<Tri3D> boden = new ArrayList<Tri3D>();
    public ArrayList<Tri3D> deckel = new ArrayList<Tri3D>();
    public ArrayList<Tri3D> seiten = new ArrayList<Tri3D>();

    public boolean isAxisObject = false;

    //public boolean fourthActivity = false;
    public int extremZ = 20;
    public P3D point1, point2, point3;
    public Tri3D firstNewTriangle; //erstes in FourthActivity hinzugefuegtes Dreieck
    public int newTriangles=0; // Anzahl der in Fourth Activity hinzugefuegten Dreiecke

    private Paint contourPaint = new Paint();   //for contours
    private Paint pointMarker = new Paint();
    private Paint selectMarker = new Paint();
    private Paint[] pointMarkers= new Paint[5];
    private Paint greyMarker = new Paint();
    public int anzahlPunkteInRing;
    public boolean isFilled = false; //Dreiecke wurden noch nicht aufgefuellt

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

    //public int rotatedZ=0;


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

    public void shader(int texture){
        this.textures=true;
        switch(texture){
            case 0:
                photoTexture=true;
                //bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.bild);
                //bitmap = sideViewBitmap;
                //break;
                //this.invalidate();
                break;
            case 1:
                bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.herz);
                photoTexture=false;
                btpShader = new BitmapShader(bitmap, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
                break;
            case 2:
                bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.stars);
                btpShader = new BitmapShader(bitmap, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
                photoTexture=false;
                break;
        }
        shapaint = new Paint();
        shapaint.setStyle(Paint.Style.FILL);
        shapaint.setAntiAlias(true);
        updateStuff();
        //shapaint.setShader(btpShader); //in andere Funktion?
        //this.invalidate();
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

        greyMarker.setStyle(Paint.Style.FILL);
        greyMarker.setColor(Color.GRAY);
        greyMarker.setStrokeWidth(10);
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
        updateStuff();
        //this.invalidate();
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
        updateStuff();
        //this.invalidate();
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
        //rotatedZ=rotatedZ+30;
        /*for(int i=0;i<model.points.size();i++){
            pointsToDraw.get(i).x=model.points.get(i).x;
            pointsToDraw.get(i).y=model.points.get(i).y;
        }*/
        updateStuff();
        //this.invalidate();
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
        //updateStuff(); //noetig???
        //this.invalidate();
    }

    public void up(){
        for(int i=0;i<this.model.points.size();i++){
            this.model.points.get(i).y=this.model.points.get(i).y-10;
        }
        changePointsToDraw();
        updateStuff();
        //invalidate();
    }

    public void down(){
        for(int i=0;i<this.model.points.size();i++){
            this.model.points.get(i).y=this.model.points.get(i).y+10;
        }
        changePointsToDraw();
        updateStuff();
        //invalidate();
    }

    public void upOrDown(float versch){
        for(int i=0;i<this.model.points.size();i++){
            this.model.points.get(i).y=this.model.points.get(i).y+versch;
        }
        changePointsToDraw();
        updateStuff();
        //invalidate();
    }

    public void leftOrRight(float versch){
        for(int i=0;i<this.model.points.size();i++){
            this.model.points.get(i).x=this.model.points.get(i).x+versch;
        }
        changePointsToDraw();
        updateStuff();
        //invalidate();
    }

    public void left(){
        for(int i=0;i<this.model.points.size();i++){
            this.model.points.get(i).x=this.model.points.get(i).x-10;
        }
        changePointsToDraw();
        updateStuff();
        //invalidate();
    }

    public void right(){
        for(int i=0;i<this.model.points.size();i++){
            this.model.points.get(i).x=this.model.points.get(i).x+10;
        }
        changePointsToDraw();
        updateStuff();
        //invalidate();
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
        updateStuff();
        //invalidate();
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
        updateStuff();
        //invalidate();
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
        updateStuff();
        //invalidate();
    }

    public PointF schwerpunkt(int ring){ //Quelle Wikipedia
        float flaeche=0;
        PointF schwerpunkt = new PointF(0,0); //x und z Koordinate
        P3D currentPoint1, currentPoint2;
        for(int i=ring*anzahlPunkteInRing;i<(ring+1)*anzahlPunkteInRing;i++){
            currentPoint1=this.model.points.get(i);
            if(i+1==(ring+1)*anzahlPunkteInRing){
                currentPoint2= this.model.points.get(ring*anzahlPunkteInRing);
            }else{
                currentPoint2= this.model.points.get(i+1);
            }
            flaeche=flaeche+((currentPoint1.x*currentPoint2.z)-(currentPoint2.x*currentPoint1.z));
        }
        flaeche=flaeche/2;
        for(int i=ring*anzahlPunkteInRing;i<(ring+1)*anzahlPunkteInRing;i++){
            currentPoint1=this.model.points.get(i);
            if(i+1==(ring+1)*anzahlPunkteInRing){
                currentPoint2= this.model.points.get(ring*anzahlPunkteInRing);
            }else{
                currentPoint2= this.model.points.get(i+1);
            }
            schwerpunkt.x=schwerpunkt.x+(currentPoint1.x+currentPoint2.x)*((currentPoint1.x*currentPoint2.z)-(currentPoint2.x*currentPoint1.z));
            schwerpunkt.y=schwerpunkt.y+(currentPoint1.z+currentPoint2.z)*((currentPoint1.x*currentPoint2.z)-(currentPoint2.x*currentPoint1.z));
        }
        schwerpunkt.x=schwerpunkt.x/(6*flaeche);
        schwerpunkt.y=schwerpunkt.y/(6*flaeche);
        return schwerpunkt;
    }

    public void undo(){
        switch(pointsNb){
            case 0:
                Tri3D toDelete;
                if(newTriangles>0){
                    if(this.isFilled){
                        int letztes=this.model.triangles.size()-1;
                        int autoNeue=this.model.triangles.size()-newTriangles+zuletztHinzugefuegt.size();
                        for(int i=letztes;i>=autoNeue;i--){
                            toDelete = this.model.triangles.get(i);
                            this.model.triangles.remove(toDelete);
                            newTriangles--;
                        }
                        this.isFilled=false;
                        this.textures=false;
                        this.setOperationID(5);
                    }else {
                        toDelete = zuletztHinzugefuegt.get(zuletztHinzugefuegt.size() - 1);
                        this.model.triangles.remove(toDelete);
                        zuletztHinzugefuegt.remove(toDelete);
                        //this.model.triangles.remove(this.model.triangles.size()-1);
                        newTriangles--;
                    }
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
                    if(Math.abs((this.model.points.indexOf(point2)%anzahlPunkteInRing)-(this.model.points.indexOf(point1)%anzahlPunkteInRing))>1){
                        point2=null;
                    }else {
                        this.pointsNb = 2;
                    }
                }
            }
            return;
        }
        if(this.pointsNb==2){
            point3 = getClosestPoint4(new PointF(x,y));
            if(point3.compare(point1)||point3.compare(point2)){ // dritter Punkt muss unterschiedlich zu ersten und zweitem Punkt sein
                point3=null;
                return;
            }
            if(Math.abs((this.model.points.indexOf(point3)%anzahlPunkteInRing)-(this.model.points.indexOf(point1)%anzahlPunkteInRing))>1){
                point3=null;
                return;
            }
            if(Math.abs((this.model.points.indexOf(point3)%anzahlPunkteInRing)-(this.model.points.indexOf(point2)%anzahlPunkteInRing))>1){
                point3=null;
                return;
            }
            if(point3!=null) {
                this.pointsNb = 0;
                int alt = this.model.triangles.size();
                Tri3D neu=new Tri3D(point1, point2, point3);
                for(int i=0;i<this.model.triangles.size();i++){
                    if(neu.intersects(neu,this.model.triangles.get(i))){
                        return;
                    }
                }
                this.model.addTriangleToMesh(neu);
                if(alt<this.model.triangles.size()) {
                    ++this.newTriangles;
                    zuletztHinzugefuegt.add(neu); // fuer "undo"-Funktion
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
        updateStuff();
        //invalidate();
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
                            updateStuff();
                            //invalidate();;
                            break;

                        case 3: // alle Punkte loeschen
                            //Anfrage, ob man wirklich alles loeschen will?
                            //deleteEverything();
                            updateStuff();
                            //invalidate();
                            break;

                        //NEED THIS FOR ACTIVITY 3!
                        case 4:
                            setSelectedPoint4(Math.round(x),Math.round(y));
                            updateStuff();
                            //invalidate();
                            break;

                        default:
                            capturePoints4(Math.round(x),Math.round(y));
                            updateStuff();
                            //invalidate();
                            break;
                    }
                    updateStuff();
                    //invalidate();
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
                    updateStuff();
                    //invalidate();
                    break;

                case MotionEvent.ACTION_CANCEL: //~eq. above.
                    //point = null;
                    updateStuff();
                    //invalidate();
                    break;
            }
            return true;
    }

    public Rect getTopRechteck(){
        float links=this.getWidth();float oben=this.getHeight(); float rechts=0; float unten=0;
        P3D currentPoint;
        for(int i=0;i<anzahlPunkteInRing;i++){
            currentPoint=this.model.points.get(i);
            if(currentPoint.x<links){
                links=currentPoint.x;
            }
            if(currentPoint.y<oben){
                oben=currentPoint.y;
            }
            if(currentPoint.x>rechts){
                rechts=currentPoint.x;
            }
            if(currentPoint.y>unten){
                unten=currentPoint.y;
            }
        }
        return new Rect((int)links, (int)oben, (int)rechts,(int) unten);
    }

    public Rect getBottomRechteck(){
        float links=this.getWidth();float oben=this.getHeight(); float rechts=0; float unten=0;
        P3D currentPoint;
        for(int i=this.model.points.size()-anzahlPunkteInRing;i<this.model.points.size();i++){ //letzter Ring
            currentPoint=this.model.points.get(i);
            if(currentPoint.x<links){
                links=currentPoint.x;
            }
            if(currentPoint.y<oben){
                oben=currentPoint.y;
            }
            if(currentPoint.x>rechts){
                rechts=currentPoint.x;
            }
            if(currentPoint.y>unten){
                unten=currentPoint.y;
            }
        }
        return new Rect((int)links, (int)oben, (int)rechts,(int) unten);
    }

    public Rect getSeitenRechteck(){
        float links=this.getWidth();float oben=this.getHeight(); float rechts=0; float unten=0;
        P3D currentPoint;
        for(int i=0;i<this.model.points.size();i++){
            currentPoint=this.model.points.get(i);
            if(currentPoint.x<links){
                links=currentPoint.x;
            }
            if(currentPoint.y<oben){
                oben=currentPoint.y;
            }
            if(currentPoint.x>rechts){
                rechts=currentPoint.x;
            }
            if(currentPoint.y>unten){
                unten=currentPoint.y;
            }
        }
        return new Rect((int)links, (int)oben, (int)rechts,(int) unten);
    }

    /*public Rect getSeitenRechteck(){
        return this.seitenRechteck;
    }*/

    public Path topPath(){
        Path path = new Path();
        P3D currentPoint=this.model.points.get(0);
        path.moveTo(currentPoint.x,currentPoint.y);
        for(int i=1;i<anzahlPunkteInRing;i++){
            currentPoint=this.model.points.get(i);
            path.lineTo(currentPoint.x,currentPoint.y);
        }
        path.close();
        return path;
    }

    public void topOld(){
        //Bitmap hilfsBitmap = Bitmap.createBitmap(Resources.getSystem().getDisplayMetrics().widthPixels,Resources.getSystem().getDisplayMetrics().heightPixels-100,Bitmap.Config.ARGB_8888);
        canvas2.setBitmap(hilfsBitmap);
        shader(1);
        RectShape path;
        path = new RectShape();
        //PathShape path = new PathShape(topPath(),this.getWidth(),this.getHeight());
        path.resize(this.getWidth(), this.getHeight());
        path.draw(canvas2, shapaint);
        //this.setImageBitmap(hilfsBitmap);
        //Bitmap hilfsBitmap2 = Bitmap.createBitmap(Resources.getSystem().getDisplayMetrics().widthPixels,Resources.getSystem().getDisplayMetrics().heightPixels-100,Bitmap.Config.ARGB_8888);
        //hilfsBitmap2.eraseColor(Color.TRANSPARENT);

        canvas2.setBitmap(hilfsBitmap2);

        canvas2.drawBitmap(hilfsBitmap, new Rect(0,0,this.getWidth(),this.getHeight()), getTopRechteck(), null);
        //bitmap=hilfsBitmap2;
        //this.setImageBitmap(hilfsBitmap2);
        shader = new BitmapShader(hilfsBitmap2, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
        /*shapaint = new Paint();
        shapaint.setStyle(Paint.Style.FILL);
        shapaint.setAntiAlias(true);*/
        shapaint.setShader(shader);
    }

    public void top(){
        if(photoTexture) {
            canvas2.setBitmap(hilfsBitmap);
            canvas2.drawBitmap(topViewBitmap,null,topImageOnScreen,null);
            canvas2.setBitmap(hilfsBitmap2);
            canvas2.drawBitmap(hilfsBitmap,origTopRect,getTopRechteck(),null);
            //canvas2.setBitmap(hilfsBitmap2);
            //canvas2.drawBitmap(topViewBitmap, origTopRect, getTopRechteck(), null); //new Rect durch "richtiges" Rechteck ersetzen
            //canvas3.rotate(rotatedZ,getTopRechteck().exactCenterX(),getTopRechteck().exactCenterY());
        }else{
            shapaint.setShader(btpShader);
            canvas2.setBitmap(hilfsBitmap);
            RectShape path;
            path = new RectShape();
            path.resize(this.getWidth(), this.getHeight());
            path.draw(canvas2, shapaint);
            canvas2.setBitmap(hilfsBitmap2);
            canvas2.drawBitmap(hilfsBitmap, new Rect(0, 0, this.getWidth(), this.getHeight()), getTopRechteck(), null);
        }
        shader = new BitmapShader(hilfsBitmap2, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
        shapaint.setShader(shader);
    }

    public void bottom(){
        if(photoTexture){
            return;
        }else {
            shapaint.setShader(btpShader);
            canvas2.setBitmap(hilfsBitmap);
            RectShape path;
            path = new RectShape();
            path.resize(this.getWidth(), this.getHeight());
            path.draw(canvas2, shapaint);
            canvas2.setBitmap(hilfsBitmap2);
            canvas2.drawBitmap(hilfsBitmap, new Rect(0, 0, this.getWidth(), this.getHeight()), getBottomRechteck(), null);
            shader = new BitmapShader(hilfsBitmap2, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
            shapaint.setShader(shader);
        }
    }

    public void side(){
        if(photoTexture) {
            canvas2.setBitmap(hilfsBitmap);
            canvas2.drawBitmap(sideViewBitmap,null,sideImageOnScreen,null);
            canvas2.setBitmap(hilfsBitmap2);
            canvas2.drawBitmap(hilfsBitmap,origSideRect,getSeitenRechteck(),null);
            /*float verhaeltnis = getSeitenRechteck().width() / getSeitenRechteck().height();
            float gewollteBreite = origSideRect.height() * verhaeltnis;
            int versch;
            if (gewollteBreite > origSideRect.width()) { //anders loesen
                versch=(int)(gewollteBreite - origSideRect.width());
                Rect neu = new Rect(origSideRect);
                neu.offset(versch, 0);
                canvas2.drawBitmap(sideViewBitmap, neu, getSeitenRechteck(), null);
            } else {
                //canvas2.rotate(rotatedZ,origSideRect.exactCenterX(),origSideRect.exactCenterY());*/
                //canvas2.drawBitmap(sideViewBitmap, origSideRect, getSeitenRechteck(), null);//new Rect durch "richtiges" Rechteck ersetzen
            //}
            //canvas2.rotate(rotatedZ,getSeitenRechteck().exactCenterX(),getSeitenRechteck().exactCenterY());
        }else {
            shapaint.setShader(btpShader);
            canvas2.setBitmap(hilfsBitmap);
            RectShape path;
            path = new RectShape();
            path.resize(hilfsBitmap.getWidth(), hilfsBitmap.getHeight());
            path.draw(canvas2, shapaint);
            canvas2.setBitmap(hilfsBitmap2);
            //canvas2.rotate(rotatedZ,this.getWidth()/2,this.getHeight()/2);
            //rotatedZ=0;
            canvas2.drawBitmap(hilfsBitmap, new Rect(0, 0, this.getWidth(), this.getHeight()), getSeitenRechteck(), null);
            /*canvas2.rotate(rotatedZ,getSeitenRechteck().exactCenterX(),getSeitenRechteck().exactCenterY());
            rotatedZ=0;*/
        }
        shader = new BitmapShader(hilfsBitmap2, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
        shapaint.setShader(shader);
    }

    public void sideOld(){
        //Bitmap hilfsBitmap = Bitmap.createBitmap(Resources.getSystem().getDisplayMetrics().widthPixels,Resources.getSystem().getDisplayMetrics().heightPixels-100,Bitmap.Config.ARGB_8888);
        canvas2.setBitmap(hilfsBitmap);
        shader(1);
        RectShape path;
        path = new RectShape();
        path.resize(hilfsBitmap.getWidth(), hilfsBitmap.getHeight());
        path.draw(canvas2, shapaint);
        //hilfsBitmap2.eraseColor(Color.TRANSPARENT);

        canvas2.setBitmap(hilfsBitmap2);
        //hilfsBitmap2.eraseColor(Color.TRANSPARENT);
        canvas2.drawBitmap(hilfsBitmap, new Rect(0,0,this.getWidth(),this.getHeight()), getSeitenRechteck(), null);
        //bitmap=hilfsBitmap2;
        //this.setImageBitmap(hilfsBitmap2);
        shader = new BitmapShader(hilfsBitmap2, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
        /*shapaint = new Paint();
        shapaint.setStyle(Paint.Style.FILL);
        shapaint.setAntiAlias(true);*/
        shapaint.setShader(shader);
    }

    /*public void drawOnBitmapTest(){ //Textur von "Deckel" bzw. "Boden" geht in die Seitenansicht, deshalb "ausschneiden" der Dreiecke notwendig!
        //RectShape path=new RectShape();
        if(!textures){
            return;
        }
        Canvas canvas3=new Canvas(hilfsBitmap3);
        if(Dreiecke.size()>(deckel.size()+boden.size())) { //wenn Modell nicht nur aus Boden und Deckel besteht, sondern auch Seiten hat
            side();
            canvas3.drawBitmap(hilfsBitmap2, getSeitenRechteck(), new Rect(0,0,this.getWidth(),this.getHeight()), null);
        }
        if(!bodenZuerst){
            bottom();
            canvas3.drawBitmap(hilfsBitmap2, getTopRechteck(), new Rect(0,0,this.getWidth(),this.getHeight()), null);
        }else{
            top();
            canvas3.drawBitmap(hilfsBitmap2, getTopRechteck(), new Rect(0,0,this.getWidth(),this.getHeight()), null);
        }
    }*/

    public void drawOnBitmap(){
        if(!textures){
            return;
        }
        Canvas canvas3=new Canvas(hilfsBitmap3);
        PathShape shapath;
        Tri3D currentTri;
        if(bodenZuerst){
            for(int j=0;j<boden.size();j++){
                currentTri = boden.get(j);
                //if(textures){
                    //if(photoTexture){
                            /*shapath = new PathShape(pathify(currentTri), this.getWidth(), this.getHeight());
                            shapath.resize(this.getWidth(), this.getHeight());
                            shapath.draw(canvas, sideViewPaint);*/
                        //canvas3.drawPath(pathify(currentTri), currentTri.getColour());
                    //}else {
                        shapath = new PathShape(pathify(currentTri), this.getWidth(), this.getHeight());
                        shapath.resize(this.getWidth(), this.getHeight());
                        shapath.draw(canvas3, shapaint);
                    //}
                /*}else {
                    canvas3.drawPath(pathify(currentTri), currentTri.getColour());   //draw filled Tri
                }*/
                //canvas3.drawPath(pathify(currentTri), contourPaint); //draw Tri borders
            }
        }else{
            for(int j=0;j<deckel.size();j++){
                currentTri = deckel.get(j);
                //if(textures){
                    /*if(photoTexture){
                        shapath = new PathShape(pathify(currentTri), this.getWidth(), this.getHeight());
                        shapath.resize(this.getWidth(), this.getHeight());
                        shapath.draw(canvas3, topViewPaint);*/
                    //}else {
                        shapath = new PathShape(pathify(currentTri), this.getWidth(), this.getHeight());
                        shapath.resize(this.getWidth(), this.getHeight());
                        shapath.draw(canvas3, shapaint);
                    //}
                /*}else {
                    canvas3.drawPath(pathify(currentTri), currentTri.getColour());   //draw filled Tri
                }*/
                //canvas3.drawPath(pathify(currentTri), contourPaint); //draw Tri borders
            }
        }
        if(Dreiecke.size()>(deckel.size()+boden.size())) { //wenn Modell nicht nur aus Boden und Deckel besteht, sondern auch Seiten hat
            //canvas2.save();
            side();
            for (int j = boden.size(); j < Dreiecke.size() - boden.size(); j++) {
                currentTri = Dreiecke.get(j);
                //if (textures) {
                    /*if (photoTexture) {
                        shapath = new PathShape(pathify(currentTri), this.getWidth(), this.getHeight());
                        shapath.resize(this.getWidth(), this.getHeight());
                        shapath.draw(canvas3, sideViewPaint);
                    } else {*/
                        shapath = new PathShape(pathify(currentTri), this.getWidth(), this.getHeight());
                        shapath.resize(this.getWidth(), this.getHeight());
                        shapath.draw(canvas3, shapaint);
                    //}
                /*} else {
                    canvas3.drawPath(pathify(currentTri), currentTri.getColour());   //draw filled Tri
                }*/
                //canvas3.drawPath(pathify(currentTri), contourPaint);
            }
        }
        /*try {
            canvas2.restore();
            canvas2.save();
        }catch (IllegalStateException e){
            //nicht abstuerzen
        }*/
        if(!bodenZuerst){
            bottom();
            for(int j=0;j<boden.size();j++){
                currentTri = boden.get(j);
                //if(textures){
                    if(photoTexture) {
                        canvas3.drawPath(pathify(currentTri), greyMarker);
                    }else {
                        shapath = new PathShape(pathify(currentTri), this.getWidth(), this.getHeight());
                        shapath.resize(this.getWidth(), this.getHeight());
                        shapath.draw(canvas3, shapaint);
                    }
                /*}else {
                    canvas3.drawPath(pathify(currentTri), currentTri.getColour());   //draw filled Tri
                }*/
                //canvas3.drawPath(pathify(currentTri), contourPaint); //draw Tri borders
            }
        }else{
            top();
            for(int j=0;j<deckel.size();j++){
                currentTri = deckel.get(j);
                //if(textures){
                    /*if(photoTexture){
                        shapath = new PathShape(pathify(currentTri), this.getWidth(), this.getHeight());
                        shapath.resize(this.getWidth(), this.getHeight());
                        shapath.draw(canvas3, topViewPaint);
                    }else {*/
                        shapath = new PathShape(pathify(currentTri), this.getWidth(), this.getHeight());
                        shapath.resize(this.getWidth(), this.getHeight());
                        shapath.draw(canvas3, shapaint);
                    //}
                /*}else {
                    canvas3.drawPath(pathify(currentTri), currentTri.getColour());   //draw filled Tri
                }*/
                //canvas3.drawPath(pathify(currentTri), contourPaint); //draw Tri borders
            }
        }
        /*try {
            canvas2.restore();
        }catch (IllegalStateException e){
            //nicht abstuerzen
        }*/
        shader = new BitmapShader(hilfsBitmap3, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
        shapaint.setShader(shader);
    }

    public void updateStuff(){
        reihenfolge();
        drawOnBitmap();
        this.invalidate();
        //rotatedZ=0;
    }

    public void reihenfolgeOld(){
        Canvas canvas3=new Canvas(hilfsBitmap3);
        Dreiecke.clear();
        int i=0;
        Tri3D currentTri;
        PathShape shapath;
        float durchschnittDeckel = this.model.points.get(0).z; //points=Ring
        float maxDeckel = this.model.points.get(0).z; //points=Ring
        //float durchschnittBoden = this.model.points.get(this.model.points.size()-anzahlPunkteInRing).z;
        float durchschnittBoden = this.model.points.get(anzahlPunkteInRing).z;
        int maxPos=0;
        for(int j=1;j<anzahlPunkteInRing;j++){
            if(maxDeckel<this.model.points.get(j).z){
                maxPos=j;
                maxDeckel=this.model.points.get(j).z;
            }
            //if(maxBoden<this.model.points.get(this.model.points.size()-anzahlPunkteInRing+j).z){
            durchschnittBoden=durchschnittBoden+this.model.points.get(anzahlPunkteInRing+j).z;
            durchschnittDeckel=durchschnittDeckel+this.model.points.get(j).z;
            //}
        }
        durchschnittBoden=durchschnittBoden/anzahlPunkteInRing;
        durchschnittDeckel=durchschnittDeckel/anzahlPunkteInRing;
        if(durchschnittBoden>durchschnittDeckel){
            for(int j=0;j<boden.size();j++){
                currentTri = boden.get(j);
                if(textures){
                    if(photoTexture){
                            /*shapath = new PathShape(pathify(currentTri), this.getWidth(), this.getHeight());
                            shapath.resize(this.getWidth(), this.getHeight());
                            shapath.draw(canvas, sideViewPaint);*/
                        canvas3.drawPath(pathify(currentTri), currentTri.getColour());
                    }else {
                        shapath = new PathShape(pathify(currentTri), this.getWidth(), this.getHeight());
                        shapath.resize(this.getWidth(), this.getHeight());
                        shapath.draw(canvas3, shapaint);
                    }
                }else {
                    canvas3.drawPath(pathify(currentTri), currentTri.getColour());   //draw filled Tri
                }
                canvas3.drawPath(pathify(currentTri), contourPaint); //draw Tri borders
            }
        }else{
            //top();
            for(int j=0;j<deckel.size();j++){
                currentTri = deckel.get(j);
                if(textures){
                    if(photoTexture){
                        shapath = new PathShape(pathify(currentTri), this.getWidth(), this.getHeight());
                        shapath.resize(this.getWidth(), this.getHeight());
                        shapath.draw(canvas3, topViewPaint);
                    }else {
                        shapath = new PathShape(pathify(currentTri), this.getWidth(), this.getHeight());
                        shapath.resize(this.getWidth(), this.getHeight());
                        shapath.draw(canvas3, shapaint);
                    }
                }else {
                    canvas3.drawPath(pathify(currentTri), currentTri.getColour());   //draw filled Tri
                }
                canvas3.drawPath(pathify(currentTri), contourPaint); //draw Tri borders
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
            side();
            //canvas.drawRect(getSeitenRechteck(),new Paint(Color.CYAN));
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
                if(textures){
                    if(photoTexture){
                        shapath = new PathShape(pathify(currentTri), this.getWidth(), this.getHeight());
                        shapath.resize(this.getWidth(), this.getHeight());
                        shapath.draw(canvas3, sideViewPaint);
                    }else {
                        shapath = new PathShape(pathify(currentTri), this.getWidth(), this.getHeight());
                        shapath.resize(this.getWidth(), this.getHeight());
                        shapath.draw(canvas3, shapaint);
                    }
                }else {
                    canvas3.drawPath(pathify(currentTri), currentTri.getColour());   //draw filled Tri
                }
                canvas3.drawPath(pathify(currentTri), contourPaint);
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
                        //canvas.drawPath(pathify(currentTri), currentTri.getColour());
                        if(textures){
                            if(photoTexture){
                                shapath = new PathShape(pathify(currentTri), this.getWidth(), this.getHeight());
                                shapath.resize(this.getWidth(), this.getHeight());
                                shapath.draw(canvas3, sideViewPaint);
                            }else {
                                shapath = new PathShape(pathify(currentTri), this.getWidth(), this.getHeight());
                                shapath.resize(this.getWidth(), this.getHeight());
                                shapath.draw(canvas3, shapaint);
                            }
                        }else {
                            canvas3.drawPath(pathify(currentTri), currentTri.getColour());   //draw filled Tri
                        }
                        canvas3.drawPath(pathify(currentTri), contourPaint);
                        i++;
                        if (i == seiten.size()) {
                            break;
                        }
                    }
                    next = weiter2 + (j * anzahlPunkteInRing * 2);
                    if(next<seiten.size()&&dreiecke[next]==false) {
                        currentTri = seiten.get(next);
                        dreiecke[next]=true;
                        if(textures){
                            if(photoTexture){
                                shapath = new PathShape(pathify(currentTri), this.getWidth(), this.getHeight());
                                shapath.resize(this.getWidth(), this.getHeight());
                                shapath.draw(canvas3, sideViewPaint);
                            }else {
                                shapath = new PathShape(pathify(currentTri), this.getWidth(), this.getHeight());
                                shapath.resize(this.getWidth(), this.getHeight());
                                shapath.draw(canvas3, shapaint);
                            }
                        }else {
                            canvas3.drawPath(pathify(currentTri), currentTri.getColour());   //draw filled Tri
                        }
                        canvas3.drawPath(pathify(currentTri), contourPaint);
                        i++;
                        if (i == seiten.size()) {
                            break;
                        }
                    }
                }
            }
        }

        if(durchschnittBoden<=durchschnittDeckel){
            for(int j=0;j<boden.size();j++){
                currentTri = boden.get(j);
                if(textures){
                    if(photoTexture) {
                        canvas3.drawPath(pathify(currentTri), currentTri.getColour());
                    }else {
                        shapath = new PathShape(pathify(currentTri), this.getWidth(), this.getHeight());
                        shapath.resize(this.getWidth(), this.getHeight());
                        shapath.draw(canvas3, shapaint);
                    }
                }else {
                    canvas3.drawPath(pathify(currentTri), currentTri.getColour());   //draw filled Tri
                }
                canvas3.drawPath(pathify(currentTri), contourPaint); //draw Tri borders
            }
        }else{
            top();
            for(int j=0;j<deckel.size();j++){
                currentTri = deckel.get(j);
                if(textures){
                    if(photoTexture){
                        shapath = new PathShape(pathify(currentTri), this.getWidth(), this.getHeight());
                        shapath.resize(this.getWidth(), this.getHeight());
                        shapath.draw(canvas3, topViewPaint);
                    }else {
                        shapath = new PathShape(pathify(currentTri), this.getWidth(), this.getHeight());
                        shapath.resize(this.getWidth(), this.getHeight());
                        shapath.draw(canvas3, shapaint);
                    }
                }else {
                    canvas3.drawPath(pathify(currentTri), currentTri.getColour());   //draw filled Tri
                }
                canvas3.drawPath(pathify(currentTri), contourPaint); //draw Tri borders
            }
        }
        shader = new BitmapShader(hilfsBitmap3, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
        /*shapaint = new Paint();
        shapaint.setStyle(Paint.Style.FILL);
        shapaint.setAntiAlias(true);*/
        shapaint.setShader(shader);
    }

    public void reihenfolge(){
        Dreiecke.clear();
        int i=0;
        Tri3D currentTri;
        float durchschnittDeckel = this.model.points.get(0).z; //points=Ring
        float maxDeckel = this.model.points.get(0).z; //points=Ring
        //float durchschnittBoden = this.model.points.get(this.model.points.size()-anzahlPunkteInRing).z;
        float durchschnittBoden = this.model.points.get(anzahlPunkteInRing).z;
        int maxPos=0;
        for(int j=1;j<anzahlPunkteInRing;j++){
            if(maxDeckel<this.model.points.get(j).z){
                maxPos=j;
                maxDeckel=this.model.points.get(j).z;
            }
            //if(maxBoden<this.model.points.get(this.model.points.size()-anzahlPunkteInRing+j).z){
            durchschnittBoden=durchschnittBoden+this.model.points.get(anzahlPunkteInRing+j).z;
            durchschnittDeckel=durchschnittDeckel+this.model.points.get(j).z;
            //}
        }
        durchschnittBoden=durchschnittBoden/anzahlPunkteInRing;
        durchschnittDeckel=durchschnittDeckel/anzahlPunkteInRing;
        if(durchschnittBoden>durchschnittDeckel){
            bodenZuerst=true;
            for(int j=0;j<boden.size();j++){
                Dreiecke.add(boden.get(j));
            }
        }else{
            bodenZuerst=false;
            for(int j=0;j<deckel.size();j++) {
                Dreiecke.add(deckel.get(j));
            }
        }
        seiten.clear();
        for(int h=2*boden.size();h<model.triangles.size();h++){
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
                Dreiecke.add(seiten.get(next));
                dreiecke[next]=true;
                i++;
            }
            int weiter1 = hinten, weiter2 = hinten;
            while(i<seiten.size()){
                if (--weiter1 < 0) {
                    weiter1 = (anzahlPunkteInRing * 2)-1;
                }
                if (++weiter2 >= anzahlPunkteInRing * 2) {
                    weiter2 = 0;
                }
                for(int j = 0; j < ebenen; j++){
                    next = weiter1 + (j * anzahlPunkteInRing * 2);
                    if(next<seiten.size()&&dreiecke[next]==false) {
                        Dreiecke.add(seiten.get(next));
                        dreiecke[next]=true;
                        i++;
                        if (i == seiten.size()) {
                            break;
                        }
                    }
                    next = weiter2 + (j * anzahlPunkteInRing * 2);
                    if(next<seiten.size()&&dreiecke[next]==false) {
                        Dreiecke.add(seiten.get(next));
                        dreiecke[next]=true;
                        i++;
                        if (i == seiten.size()) {
                            break;
                        }
                    }
                }
            }
        }

        if(durchschnittBoden<=durchschnittDeckel){
            for(int j=0;j<boden.size();j++){
                Dreiecke.add(boden.get(j));
            }
        }else{
            for(int j=0;j<deckel.size();j++){
                Dreiecke.add(deckel.get(j));
            }
        }
    }

    //done - both.
    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        //reihenfolge();
        //updateStuff();
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

        //canvas.drawRect(getTopRechteck(), null);
        /*Bitmap testBitmap=Bitmap.createBitmap(this.getWidth(),this.getHeight(),Bitmap.Config.ARGB_8888);
        Bitmap herz = BitmapFactory.decodeResource(getResources(),R.drawable.herz);
        Canvas canvas2 = new Canvas(testBitmap);
        canvas2.rotate(90,herz.getWidth()/2,herz.getHeight()/2);
        canvas2.drawBitmap(herz,0,0,null);
        canvas2.drawPoint(0,0,greyMarker);
        canvas.drawBitmap(testBitmap,new Rect(0,0,herz.getWidth(),herz.getHeight()),new RectF(0,0,this.getWidth(),this.getHeight()),null);*/
        int punkte=0;
        int farbe=0;
        /*canvas.drawCircle(this.getWidth()/2, 0, 20, greyMarker);
        canvas.drawCircle(this.getWidth(), 0, 20, greyMarker);
        canvas.drawCircle((model.points.get(0).x+extremR.x)/2, 0, 20, selectMarker);*/
        if (!this.isFilled) {
            for (int i = 0; i < model.points.size(); i++) {
                if (i % anzahlPunkteInRing > this.model.points.indexOf(extremR) || i >= anzahlPunkteInRing * 2) {
                    canvas.drawCircle(pointsToDraw.get(i).x, pointsToDraw.get(i).y, 20, greyMarker);
                }
            }
        }
            for (int i = 0; i < model.points.size(); i++) {
                //if(points.get(i).z<=extremZ) {
                    /*if (selectedPointIndex == i) {
                        canvas.drawCircle(pointsToDraw.get(i).x, pointsToDraw.get(i).y, 20, selectMarker);
                    } else {*/
                    if(!this.isFilled){
                        if(!(i%anzahlPunkteInRing>this.model.points.indexOf(extremR)||i>=anzahlPunkteInRing*2)){
                            canvas.drawCircle(pointsToDraw.get(i).x, pointsToDraw.get(i).y, 20, pointMarkers[farbe]);
                        }
                    }else {
                        canvas.drawCircle(pointsToDraw.get(i).x, pointsToDraw.get(i).y, 20, pointMarkers[farbe]);
                    }
                    if(i==this.model.points.indexOf(this.extremR)){
                        canvas.drawCircle(pointsToDraw.get(i).x, pointsToDraw.get(i).y, 20, pointMarkers[4]);
                    }

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
            for(int i = 0; i < model.points.size(); i++){
                canvas.drawText(""+i, pointsToDraw.get(i).x,pointsToDraw.get(i).y, new Paint(Color.CYAN));
            }
            /*if(true){
                return;
            }*/

            int i=0;
            Tri3D currentTri;
            PathShape shapath;
            /*if(!xy){
                boolean[] dreiecke = dreiecke();
                for(int h=0;h<model.triangles.size();h++){
                    shapath = new PathShape(pathify(model.triangles.get(h)), this.getWidth(), this.getHeight());
                    shapath.resize(this.getWidth(), this.getHeight());
                    shapath.draw(canvas, shapaint);
                }
                for(int h=0;h<model.triangles.size();h++){
                    if(dreiecke[h]==true) {
                        currentTri = model.triangles.get(h);
                        //canvas.drawPath(pathify(currentTri), currentTri.getColour());   //draw filled Tri
                        canvas.drawPath(pathify(currentTri), contourPaint); //draw Tri borders
                        // currentTri = currentTri.getNextTriangle();
                    }
                }
                //textures(dreiecke);
                return;
            }*/
            try{
                for(int j=0;j<Dreiecke.size();j++){
                    currentTri = Dreiecke.get(j);
                    if(textures){
                        shapath = new PathShape(pathify(currentTri), this.getWidth(), this.getHeight());
                        shapath.resize(this.getWidth(), this.getHeight());
                        shapath.draw(canvas, shapaint);
                    }else{
                        canvas.drawPath(pathify(currentTri), currentTri.getColour());
                    }
                    canvas.drawPath(pathify(currentTri), contourPaint);
                }
                /*float durchschnittDeckel = this.model.points.get(0).z; //points=Ring
                float maxDeckel = this.model.points.get(0).z; //points=Ring
                //float durchschnittBoden = this.model.points.get(this.model.points.size()-anzahlPunkteInRing).z;
                float durchschnittBoden = this.model.points.get(anzahlPunkteInRing).z;
                int maxPos=0;
                for(int j=1;j<anzahlPunkteInRing;j++){
                    if(maxDeckel<this.model.points.get(j).z){
                        maxPos=j;
                        maxDeckel=this.model.points.get(j).z;
                    }
                    //if(maxBoden<this.model.points.get(this.model.points.size()-anzahlPunkteInRing+j).z){
                    durchschnittBoden=durchschnittBoden+this.model.points.get(anzahlPunkteInRing+j).z;
                    durchschnittDeckel=durchschnittDeckel+this.model.points.get(j).z;
                    //}
                }
                durchschnittBoden=durchschnittBoden/anzahlPunkteInRing;
                durchschnittDeckel=durchschnittDeckel/anzahlPunkteInRing;
                if(durchschnittBoden>durchschnittDeckel){
                    for(int j=0;j<boden.size();j++){
                        currentTri = boden.get(j);
                        if(textures){
                            if(photoTexture){
                                /*shapath = new PathShape(pathify(currentTri), this.getWidth(), this.getHeight());
                                shapath.resize(this.getWidth(), this.getHeight());
                                shapath.draw(canvas, sideViewPaint);*/
                                /*canvas.drawPath(pathify(currentTri), currentTri.getColour());
                            }else {
                                shapath = new PathShape(pathify(currentTri), this.getWidth(), this.getHeight());
                                shapath.resize(this.getWidth(), this.getHeight());
                                shapath.draw(canvas, shapaint);
                            }
                        }else {
                            canvas.drawPath(pathify(currentTri), currentTri.getColour());   //draw filled Tri
                        }
                        canvas.drawPath(pathify(currentTri), contourPaint); //draw Tri borders
                    }
                }else{
                    //top();
                    for(int j=0;j<deckel.size();j++){
                        currentTri = deckel.get(j);
                        if(textures){
                            if(photoTexture){
                                shapath = new PathShape(pathify(currentTri), this.getWidth(), this.getHeight());
                                shapath.resize(this.getWidth(), this.getHeight());
                                shapath.draw(canvas, topViewPaint);
                            }else {
                                shapath = new PathShape(pathify(currentTri), this.getWidth(), this.getHeight());
                                shapath.resize(this.getWidth(), this.getHeight());
                                shapath.draw(canvas, shapaint);
                            }
                        }else {
                            canvas.drawPath(pathify(currentTri), currentTri.getColour());   //draw filled Tri
                        }
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
                    /*seiten.add(model.triangles.get(h)); //nur zu Testzwecken, dauerndes Umaendern der Liste unnoetig!
                }
                boolean[] dreiecke = new boolean[seiten.size()];
                for(int h=0;h<dreiecke.length;h++){
                    dreiecke[h]=false;
                }
                if(seiten.size()>0) {
                    //side();
                    //canvas.drawRect(getSeitenRechteck(),new Paint(Color.CYAN));
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
                        if(textures){
                            if(photoTexture){
                                shapath = new PathShape(pathify(currentTri), this.getWidth(), this.getHeight());
                                shapath.resize(this.getWidth(), this.getHeight());
                                shapath.draw(canvas, sideViewPaint);
                            }else {
                                shapath = new PathShape(pathify(currentTri), this.getWidth(), this.getHeight());
                                shapath.resize(this.getWidth(), this.getHeight());
                                shapath.draw(canvas, shapaint);
                            }
                        }else {
                            canvas.drawPath(pathify(currentTri), currentTri.getColour());   //draw filled Tri
                        }
                        canvas.drawPath(pathify(currentTri), contourPaint);
                        i++;
                    }
                    int weiter1 = hinten, weiter2 = hinten;
                    //for (int h=0;h<2*anzahlPunkteInRing;h++) {
                    while(i<seiten.size()){
                        /*if(i>=seiten.size()){
                            break;
                        }*/
                        /*if (--weiter1 < 0) {
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
                                //canvas.drawPath(pathify(currentTri), currentTri.getColour());
                                if(textures){
                                    if(photoTexture){
                                        shapath = new PathShape(pathify(currentTri), this.getWidth(), this.getHeight());
                                        shapath.resize(this.getWidth(), this.getHeight());
                                        shapath.draw(canvas, sideViewPaint);
                                    }else {
                                        shapath = new PathShape(pathify(currentTri), this.getWidth(), this.getHeight());
                                        shapath.resize(this.getWidth(), this.getHeight());
                                        shapath.draw(canvas, shapaint);
                                    }
                                }else {
                                    canvas.drawPath(pathify(currentTri), currentTri.getColour());   //draw filled Tri
                                }
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
                                if(textures){
                                    if(photoTexture){
                                        shapath = new PathShape(pathify(currentTri), this.getWidth(), this.getHeight());
                                        shapath.resize(this.getWidth(), this.getHeight());
                                        shapath.draw(canvas, sideViewPaint);
                                    }else {
                                        shapath = new PathShape(pathify(currentTri), this.getWidth(), this.getHeight());
                                        shapath.resize(this.getWidth(), this.getHeight());
                                        shapath.draw(canvas, shapaint);
                                    }
                                }else {
                                    canvas.drawPath(pathify(currentTri), currentTri.getColour());   //draw filled Tri
                                }
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

                if(durchschnittBoden<=durchschnittDeckel){
                    for(int j=0;j<boden.size();j++){
                        currentTri = boden.get(j);
                        if(textures){
                            if(photoTexture) {
                                canvas.drawPath(pathify(currentTri), currentTri.getColour());
                            }else {
                                shapath = new PathShape(pathify(currentTri), this.getWidth(), this.getHeight());
                                shapath.resize(this.getWidth(), this.getHeight());
                                shapath.draw(canvas, shapaint);
                            }
                        }else {
                            canvas.drawPath(pathify(currentTri), currentTri.getColour());   //draw filled Tri
                        }
                        canvas.drawPath(pathify(currentTri), contourPaint); //draw Tri borders
                    }
                }else{
                    //top();
                    for(int j=0;j<deckel.size();j++){
                        currentTri = deckel.get(j);
                        if(textures){
                            if(photoTexture){
                                shapath = new PathShape(pathify(currentTri), this.getWidth(), this.getHeight());
                                shapath.resize(this.getWidth(), this.getHeight());
                                shapath.draw(canvas, topViewPaint);
                            }else {
                                shapath = new PathShape(pathify(currentTri), this.getWidth(), this.getHeight());
                                shapath.resize(this.getWidth(), this.getHeight());
                                shapath.draw(canvas, shapaint);
                            }
                        }else {
                            canvas.drawPath(pathify(currentTri), currentTri.getColour());   //draw filled Tri
                        }
                        canvas.drawPath(pathify(currentTri), contourPaint); //draw Tri borders
                    }
                }
            */
            }catch (Exception e) {
                contourPaint.setColor(Color.RED);
                int h=0;
                while (h < model.triangles.size()) {
                    currentTri = model.triangles.get(h++);
                    canvas.drawPath(pathify(currentTri), currentTri.getColour());   //draw filled Tri
                    canvas.drawPath(pathify(currentTri), contourPaint); //draw Tri borders
                    // currentTri = currentTri.getNextTriangle();
                }
                contourPaint.setColor(Color.BLACK);
            }

            //textures();
        /*currentTri=model.triangles.get(0);
        canvas.drawRect(origSideRect,currentTri.getColour());
        canvas.drawRect(getSeitenRechteck(),currentTri.getColour());
        canvas.drawPoint((float)sideViewBitmap.getWidth(),(float)sideViewBitmap.getHeight(),selectMarker);*/
        /*int weite1=this.getWidth();
        int hoehe1=this.getHeight();
        int weite2=sideViewBitmap.getWidth();
        int hoehe2=sideViewBitmap.getHeight();
        int weite3=sideViewBitmap.getScaledWidth(Resources.getSystem().getDisplayMetrics());
        int hoehe3=sideViewBitmap.getScaledHeight(Resources.getSystem().getDisplayMetrics());
        int diff=weite1-weite2;*/

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

    public boolean[] dreiecke(){
        if(model.triangles.isEmpty()){
            return null;
        }
        boolean[] dreiecke = new boolean[model.triangles.size()];
        for(int i=0;i<model.triangles.size();i++){
            dreiecke[i]=true;
        }

        Tri3D currentTri1, currentTri2;
        float zCurrent1, zCurrent2;
        float zVector;
        for(int i=0;i<dreiecke.length-1;i++) {
            if(dreiecke[i]==true) {
                currentTri1 = model.triangles.get(i);
                for (int j = 1; j < dreiecke.length; j++) {
                    if(dreiecke[j]==true&&i!=j) {
                        currentTri2 = model.triangles.get(j);
                        if (currentTri1.intersects(currentTri1, currentTri2)) {
                            zCurrent1 = currentTri1.getp0().z + currentTri1.getp1().z + currentTri1.getp2().z;
                            zCurrent2 = currentTri2.getp0().z + currentTri2.getp1().z + currentTri2.getp2().z;
                            zVector=zCurrent1-zCurrent2;
                            /*if(zVector==0){
                                zVector=currentTri1.getp0().z-currentTri2.getp1().z;
                                if(zVector==0){
                                    zVector=currentTri1.getp0().z-currentTri2.getp2().z;
                                }
                            }*/
                            if (zVector<0) {
                                dreiecke[j] = false;
                            } else {
                                dreiecke[i] = false;
                                break;
                            }
                        }
                    }
                }
            }
        }
        return dreiecke;
    }

    public void textures(boolean[] dreiecke){
        if(seiten.isEmpty()){
            return;
        }
        Bitmap bitmap2 = Bitmap.createBitmap(this.getWidth(),this.getHeight(),Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap2);
        PathShape shapath;

        /*for(int i=0;i<seiten.size();i++) {
            shapath = new PathShape(pathify(seiten.get(i)), this.getWidth(), this.getHeight());
            shapath.resize(this.getWidth(), this.getHeight());
            shapath.draw(canvas, shapaint);
        }*/

        /*for(int i=0;i<model.triangles.size();i++) {
            if(dreiecke[i]) {
                shapath = new PathShape(pathify(model.triangles.get(i)), this.getWidth(), this.getHeight());
                shapath.resize(this.getWidth(), this.getHeight());
                shapath.draw(canvas, shapaint);
            }
        }*/

        for(int i=0;i<model.triangles.size();i++) {
                shapath = new PathShape(pathify(model.triangles.get(i)), this.getWidth(), this.getHeight());
                shapath.resize(this.getWidth(), this.getHeight());
                shapath.draw(canvas, shapaint);
        }

        this.setImageBitmap(bitmap2);
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
        /*pathTriThis.moveTo(pointsToDraw.get(p0).x, pointsToDraw.get(p0).y);
        pathTriThis.lineTo(pointsToDraw.get(p1).x, pointsToDraw.get(p1).y);
        pathTriThis.lineTo(pointsToDraw.get(p2).x, pointsToDraw.get(p2).y);*/
        pathTriThis.close();
        return pathTriThis;
    }

    public void rebuildFormerModel(){

    }

    public void setOriginalTopRect(float[] punkte){
        float links=this.getWidth();float oben=this.getHeight(); float rechts=0; float unten=0;
        for(int i=0;i<punkte.length;i++){
            if(links>punkte[i]){
                links=punkte[i];
            }
            if(rechts<punkte[i]){
                rechts=punkte[i];
            }
            i++;
            if(oben>punkte[i]){
                oben=punkte[i];
            }
            if(unten<punkte[i]){
                unten=punkte[i];
            }
            i++;
        }
        this.origTopRect=new Rect((int)links,(int)oben,(int)rechts,(int)unten);
    }

    public void setOriginalSideRect(float[] xPunkte, float[] yPunkte){
        float links=this.getWidth();float oben=this.getHeight(); float rechts=0; float unten=0;
        for(int i=0;i<xPunkte.length;i++){
            if(links>xPunkte[i]){
                links=xPunkte[i];
            }
            if(rechts<xPunkte[i]){
                rechts=xPunkte[i];
            }
            if(oben>yPunkte[i]){
                oben=yPunkte[i];
            }
            if(unten<yPunkte[i]){
                unten=yPunkte[i];
            }
        }
        this.origSideRect=new Rect((int)links,(int)oben,(int)rechts,(int)unten);
        /*float breite = (float) getSeitenRechteck().width();
        float hoehe = (float) getSeitenRechteck().height();
        float verhaeltnis = breite/hoehe;
        float gewollteBreite = ((float)origSideRect.height()) * verhaeltnis;
        int neuRechts;
        if (gewollteBreite > origSideRect.width()) { //anders loesen
            neuRechts = (int) (gewollteBreite - origSideRect.width());
            //Rect neu = new Rect(origSideRect);
            origSideRect.set(origSideRect.left,origSideRect.top,origSideRect.right+neuRechts,origSideRect.bottom);
        }*/
        /*for(int i=0;i<XPoints.length;i++){
            this.points.add(new P3D(XPoints[i],YPoints[i],ZPoints[i]));
        }*/
        /*rebuildFormerPoints(getIntent().getFloatArrayExtra("XPunkte"),getIntent().getFloatArrayExtra("YPunkte"),
                    getIntent().getFloatArrayExtra("ZPunkte"), scalefactor, verschiebeFactorX, verschiebeFactorY);*/
    }

}
