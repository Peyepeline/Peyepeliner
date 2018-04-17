package com.example.core.peyepeliner;

/**
 * Created by Core on 17.09.2017.
 */

import android.graphics.*;

class Tri3D {

    //TODO - implement methods of Triangle-class

    public Region clip = new Region(0, 0, 1920, 1920);
    private int id;
    private Paint triPaint = new Paint();    //Farbe des Dreiecks (zum späteren malen des Dreiecks)
    private Tri3D nextTriangle;

    private P3D ep0;
    private P3D ep1;
    private P3D ep2;

    public Tri3D(P3D point0, P3D point1, P3D point2){
        this.setp0(point0);
        this.setp1(point1);
        this.setp2(point2);
        /*this.p0 = point0;
        this.p1 = point1;
        this.p2 = point2;*/
        this.setColour(android.graphics.Color.WHITE);
    }

    public Tri3D(P3D point0, P3D point1, P3D point2, int Id){
        this.setp0(point0);
        this.setp1(point1);
        this.setp2(point2);
        this.setId(Id);
        this.setColour(android.graphics.Color.WHITE);
    }

    public void setId(int id){
        this.id = id;
    }

    public int getId(){
        return this.id;
    }

    public void setColour(int colour){
        this.triPaint.setColor(colour);
        this.triPaint.setAlpha(128);   //partial see-through [0..255]: halfpoint-value
        this.triPaint.setStyle(Paint.Style.FILL);
    }

    public Paint getColour(){
        return this.triPaint;
    }

    public void setNextTriangle(Tri3D next){
        this.nextTriangle = next;
    }

    public Tri3D getNextTriangle(){
        return this.nextTriangle;
    }

    public P3D getp0(){
        return this.ep0;
    }
    public P3D getp1(){
        return this.ep1;
    }
    public P3D getp2(){
        return this.ep2;
    }
    public void setp0(P3D a){
        this.ep0 = a;
    }
    public void setp1(P3D a){
        this.ep1 = a;
    }
    public void setp2(P3D a){
        this.ep2 = a;
    }

    public Path pathify(){
        Path pathTriThis = new Path();
        pathTriThis.moveTo(this.getp0().x, this.getp0().y);
        pathTriThis.lineTo(this.getp1().x, this.getp1().y);
        pathTriThis.lineTo(this.getp2().x, this.getp2().y);
        pathTriThis.close();
        return pathTriThis;
    }

    public boolean intersects(Tri3D tri1, Tri3D tri2){
        Path pathTri1 = tri1.pathify();
        Path pathTri2 = tri2.pathify();

        Region regionTri1 = new Region();
        regionTri1.setPath(pathTri1, clip);
        Region regionTri2 = new Region();
        regionTri2.setPath(pathTri2, clip);

        if (!regionTri1.quickReject(regionTri2) && regionTri1.op(regionTri2, Region.Op.INTERSECT)) {
            return true;
        }else{
            return false;
        }
    }

    public double abstand(PointF p1, PointF p2){
        return Math.sqrt(((p1.x-p2.x)*(p1.x-p2.x))+((p1.y-p2.y)*(p1.y-p2.y)));
    }

    public double abstand(P3D p1, PointF p2){
        return Math.sqrt(((p1.x-p2.x)*(p1.x-p2.x))+((p1.y-p2.y)*(p1.y-p2.y)));
    }

    public double abstand(PointF p1, P3D p2){
        return Math.sqrt(((p1.x-p2.x)*(p1.x-p2.x))+((p1.y-p2.y)*(p1.y-p2.y)));
    }

    public double abstand(P3D p1, P3D p2){
        return Math.sqrt(((p1.x-p2.x)*(p1.x-p2.x))+((p1.y-p2.y)*(p1.y-p2.y)));
    }

    public P3D getPointToAdjust(PointF pos) {   //liefert, welcher der 3 Eckpunkte von "this" dem Punkt (pos) am nächsten liegt
        double abstand0 = abstand(pos, this.getp0());
        double abstand1 = abstand(pos, this.getp1());
        double abstand2 = abstand(pos, this.getp2());
        double minAbstand = Math.min(abstand0, Math.min(abstand1, abstand2));
        if(minAbstand == abstand0){
            return this.getp0();
        }
        if(minAbstand == abstand1){
            return this.getp1();
        }
        if(minAbstand == abstand2){
            return this.getp2();
        }
        return null;
    }

    public void connect(Tri3D tri2){
        //for each point in "this" find closest point in "tri2"
        //for this.p0 find closest point in tri2. if abstand is under 50, set this.p0 to tri2.closestpt
        //tri2.getPointToAdjust(this.p0) gets point in tri2 closest to p0
        if(abstand(this.getp0(), tri2.getPointToAdjust(this.getp0().getPointF()))<50){
            this.setp0(tri2.getPointToAdjust(this.getp0().getPointF()));
        }
        if(abstand(this.getp1(), tri2.getPointToAdjust(this.getp1().getPointF()))<50){
            this.setp1(tri2.getPointToAdjust(this.getp1().getPointF()));
        }
        if(abstand(this.getp2(), tri2.getPointToAdjust(this.getp2().getPointF()))<50){
            this.setp2(tri2.getPointToAdjust(this.getp2().getPointF()));
        }
    }

    public void connect2(Tri3D tri2){
        //for each point in "this" find closest point in "tri2"
        //for this.p0 find closest point in tri2. if abstand is under 50, set this.p0 to tri2.closestpt
        //tri2.getPointToAdjust(this.p0) gets point in tri2 closest to p0
        if(abstand(this.getp0(), tri2.getPointToAdjust(this.getp0().getPointF()))<1){
            this.setp0(tri2.getPointToAdjust(this.getp0().getPointF()));
        }
        if(abstand(this.getp1(), tri2.getPointToAdjust(this.getp1().getPointF()))<1){
            this.setp1(tri2.getPointToAdjust(this.getp1().getPointF()));
        }
        if(abstand(this.getp2(), tri2.getPointToAdjust(this.getp2().getPointF()))<1){
            this.setp2(tri2.getPointToAdjust(this.getp2().getPointF()));
        }
    }

    //////////////////////////////////////////////////
    /*
    public boolean checkMove(PointF pos, PointF next, Triangle tri2){
        float dX = next.x-pos.x;
        float dY = next.y-pos.y;
        PointF nP0 = new PointF();
        nP0.set(this.getp0());
        PointF nP1 = new PointF();
        nP1.set(this.getp1());
        PointF nP2 = new PointF();
        nP2.set(this.getp2());
        nP0.offset(dX, dY);
        nP1.offset(dX, dY);
        nP2.offset(dX, dY);

        if (!intersects(new Triangle(nP0,nP1,nP2), tri2)){
            return false;
        }else{
            return true;
        }
    }

    public boolean intersects(Triangle tri2){
        Path pathTriThis = this.pathify();
        Path pathTri2 = tri2.pathify();

        Region regionTriThis = new Region();
        regionTriThis.setPath(pathTriThis, clip);
        Region regionTri2 = new Region();
        regionTri2.setPath(pathTri2, clip);

        if (!regionTriThis.quickReject(regionTri2) && regionTriThis.op(regionTri2, Region.Op.INTERSECT)) {
            return true;
        }else{
            return false;
        }
    }

    public void move(PointF pos, PointF next){
        float dX = next.x-pos.x;
        float dY = next.y-pos.y;
        this.getp0().offset(dX, dY);
        this.getp1().offset(dX, dY);
        this.getp2().offset(dX, dY);
    }

    public boolean checkAdjustPoints(PointF pos, PointF next, Triangle tri2){  //kann man den punkt verschieben, ohne überschneidung?
        PointF point = new PointF();
        point.set(getPointToAdjust(pos));
        boolean intersects = false;
        if(point==null){
            return true;
        }
        float dX = next.x-pos.x;
        float dY = next.y-pos.y;
        point.offset(dX,dY);

        if(point.equals(this.getp0().x, this.getp0().y)){
            intersects = intersects(new Triangle(point,this.getp1(),this.getp2()),tri2);
        }else{
            if(point.equals(this.getp1().x, this.getp1().y)){
                intersects = intersects(new Triangle(this.getp0(),point,this.getp2()),tri2);
            }else{
                intersects = intersects(new Triangle(this.getp0(),this.getp1(),point),tri2);
            }
        }

        if(!intersects){
            return false;
            //this.getPointToAdjust(pos).offset(dX,dY);   //WAR DAS NICHT UNREACHABLE? weil return bricht methode ab, und es war NACH dem return-statement...
        }else{
            return true;
        }
    }

    public void adjustPoints(PointF pos, PointF next){ //Einzelpunkt-move()
        if(getPointToAdjust(pos)==null){
            return;
        }
        float dX = next.x-pos.x;
        float dY = next.y-pos.y;
        this.getPointToAdjust(pos).offset(dX,dY);
    }
    */
}
