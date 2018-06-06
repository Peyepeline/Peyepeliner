package com.example.core.peyepeliner;

/**
 * Created by CentralCore on 30.10.2017.
 */

import android.graphics.*;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;

class edge {
    private P3D p0;
    private P3D p1;

    public edge(P3D a, P3D b){  //new edge [a,b]
        this.p0 = a;
        this.p1 = b;
    }

    public edge(edge e){    //clone e
        this.p0 = e.getP0();
        this.p1 = e.getP1();
    }

    public void setP0(P3D p){
        this.p0 = p;
    }

    public P3D getP0(){
        return this.p0;
    }

    public void setP1(P3D p){
        this.p1 = p;
    }

    public P3D getP1(){
        return this.p1;
    }

    public boolean equals(edge e){
        if((this.p0 == e.getP0())&&(this.p1 == e.getP1())){
            return true;
        }
        if((this.p0 == e.getP1())&&(this.p1 == e.getP0())){
            return true;
        }
        return false;
    }

    public edge getReverseEdge(){
        return new edge(this.getP1(), this.getP0());
    }
}

public class Model3D {
    public ArrayList<P3D> points = new ArrayList<P3D>();
    public ArrayList<edge> edges = new ArrayList<edge>();
    public ArrayList<Tri3D> triangles = new ArrayList<Tri3D>();

    public P3D deckelspitze;
    public P3D bodenspitze;
    public boolean hasDeckelSpitze;
    public boolean hasBodenSpitze;

    //TODO - check existence: Arraylist for 2-|3-tupled objects - done. don't use, use (edge)s instead

    //TODO - implement: topCoverFaces, (getPolyRing), (scalePolyRing)
    //TODO - start: topCover (points, tris) + polyRings (points only) + bottomCover (points, tris)
    //TODO - ALL POINTS EXIST FROM START (points is filled @start)!
    //TODO - implement addTri(p1,p2,p3) - done.
    //TODO - polyRingPointCorrespondence: pX ^= pN mod #rings ? - CHECK THIS!

    //TODO - correct building sequence:
    //  empty model
    //  add originalRing
    //  successively add newRing(s)
    //  add leftOverPoints
    //  recreate triangles etc pp
    //this ensures that the corresponding points in each ring are located at calculatable positions in the pointList, while lone points (spikes top/bottom) are at the very end of the list

    public Model3D(){
        //ensure empty lists on init
        this.points.clear();
        this.edges.clear();
        this.triangles.clear();
    }

    public Model3D(Model3D m){
        this.points.clear();
        this.edges.clear();
        this.triangles.clear();
        //create clone of m
        for(P3D p : m.points){
            this.points.add(p);
        }
        for(Tri3D t : m.triangles){
            this.triangles.add(t);
        }
    }

    public void addPointToMesh(P3D p){
        if(!points.contains(p)){
            points.add(p);
        }
    }

    public void addPointListToMesh(ArrayList<P3D> list){
        for(P3D p : list){
            if(!points.contains(p)){
                points.add(p);
            }
        }
    }

    public void addEdgeToMesh(edge e){
        boolean alreadyContained = false;
        for(edge edgeInEdges : this.edges){
            if(edgeInEdges.equals(e)){
                alreadyContained = true;
            }
        }
        if(!alreadyContained){   //check for t and all point-wise equivalents!
            edges.add(e);
        }
    }

    public void buildTrianglesFromEdges(){
        ArrayList<edge> edgesOriginatingInP = new ArrayList<edge>();
        for(P3D p : this.points){
            //mk AL with edges containing p
            edgesOriginatingInP.clear();
            for(edge edgeInEdges : this.edges){
                if((edgeInEdges.getP0() == p) || (edgeInEdges.getP1() == p)){    //if 1 || x, x isn't evaluated
                    edgesOriginatingInP.add(new edge(edgeInEdges)); //duplicates SHOULD be impossible
                }
            }
            //foreach in AL check existence edge with (p1,p1)
            for(edge pa : edgesOriginatingInP){
                for(edge pb : edgesOriginatingInP){
                    edge ab = new edge(pa.getP1(), pb.getP1()); //p0 is each p
                    boolean edgeExists = false;
                    for(edge compareTo : this.edges){
                        if(compareTo.equals(ab)){
                            edgeExists = true;
                        }
                    }
                    if(edgeExists){
                        this.addTriangleToMesh(new Tri3D(p, pa.getP1(), pb.getP1()));
                    }
                    /*
                    if((this.edges.contains(ab)) || (this.edges.contains(ab.getReverseEdge()))){
                        //case: pa, pb, ab exist : new triangle(p,a,b)
                        //this.triangles.add(new Tri3D(p, pa.getP1(), pb.getP1()));
                        this.addTriangleToMesh(new Tri3D(p, pa.getP1(), pb.getP1()));
                    }
                    */
                }
            }
        }
    }

    public void addTriangleToMesh(Tri3D t){
        P3D op0 = t.getp0();
        P3D op1 = t.getp1();
        P3D op2 = t.getp2();
        boolean alreadyContained = false;
        for(Tri3D triangleInTrianglesList : this.triangles){
            if(triangleEqualsPointwise(triangleInTrianglesList, op0, op1, op2)){
                alreadyContained = true;
            }
        }
        if(!alreadyContained){   //check for t and all point-wise equivalents!
            triangles.add(t);
            addPointToMesh(op0);
            addPointToMesh(op1);
            addPointToMesh(op2);
        }
    }

    private boolean triangleEqualsPointwise(Tri3D t, P3D p0, P3D p1,P3D p2){
	//checks wether a Tri3D t has the 3 corners p0-p2, independant of point order
        if(t.getp0()==p0){
            if((t.getp1()==p1) && (t.getp2()==p2)){
                return true;
            }
            if((t.getp1()==p2) && (t.getp2()==p1)){
                return true;
            }
        }
        if(t.getp0()==p1){
            if((t.getp1()==p0) && (t.getp2()==p2)){
                return true;
            }
            if((t.getp1()==p2) && (t.getp2()==p0)){
                return true;
            }
        }
        if(t.getp0()==p2){
            if((t.getp1()==p0) && (t.getp2()==p1)){
                return true;
            }
            if((t.getp1()==p1) && (t.getp2()==p0)){
                return true;
            }
        }
        return false;
    }
	
	public ArrayList<P3D> getRing(ArrayList<P3D> topViewPoints){
        ArrayList<P3D> ring = new ArrayList<P3D>();
        ring.clear();
        P3D c = new P3D(0,0,0);
        //==================================================
        //calc centre c
        for(P3D pointInPointList : topViewPoints){
            c.x += pointInPointList.x;
            c.y += pointInPointList.y;
            c.z += pointInPointList.z;
        }
        c.x = c.x / topViewPoints.size();
        c.y = c.y / topViewPoints.size();
        c.z = c.z / topViewPoints.size();
        //==================================================
        //calc half-average-distance(c,p) delta (via vector?)
        //calc avg dist(c,pointInPointsList)
        float averageDistance = 0;
        vector CP = new vector();   //empty vector for subsequent calculations
        for(P3D pointInPointList : topViewPoints){
            CP.setVector(c, pointInPointList);
            averageDistance += CP.length();
        }
        averageDistance = averageDistance / topViewPoints.size();
        //TODO - half-factor might need adjusting, depending on phys. model...
        float delta = averageDistance / 2;
        //==================================================
        //foreach p where dist(c,p) > delta (ignore inner points, like spikes etc pp):
        //add point to ArrayList<P3D> ring (order conserved)
        //CAUTION: topView(x)=reality(x), topView(y)=reality(z), reality(y) not in topView
        for(P3D pointInPointList : topViewPoints){
            CP.setVector(c, pointInPointList);
            if((CP.length() > delta) && (!ring.contains(pointInPointList))){
                ring.add(new P3D(pointInPointList.x, 0, pointInPointList.y));
                //ring.add(pointInPointList);
            }
        }
        //==================================================
        //swap ring(0) with P3D where x smallest
        P3D smallest = ring.get(0);
        for(P3D point : ring){
            if(smallest.x > point.x){
                smallest = ring.get(ring.indexOf(point));
            }
        }
        //ring(0)->ring.indexOf(smallest)
        //smallest->ring(0)
        //switch elements @0 and @pos
        P3D r0 = ring.get(0);
        ring.remove(0);
        ring.add(0, smallest);
        int pos = ring.lastIndexOf(smallest);
        ring.remove(pos);
        ring.add(pos, r0);
        //==================================================
        //sort according to polar coordinates (respective to ring[0])
        sortRing(ring);
        //==================================================
		//TODO - implement! - done, see above.
		//receives topView-ptList, creates list with RingPoints (and edges?) CONSERVING ORDER!
		//calc centre c,
		//calc half-average-distance(c,p) delta (via vector?)
		//foreach p where dist(c,p) > delta (ignore inner points, like spikes etc pp)
		//add point to ArrayList<P3D> ring (order conserved)
		return ring;
	}


    private ArrayList<P3D> sortRing(ArrayList<P3D> r){
        //sort r via parallelly sorting its items polarCoordinates
        //use Array, since ArrayList can't handle primitive types
        //double[] polarRing = new double[r.size()];
        //fill polarRing with corresponding values
        //for (int i = 0; i < polarRing.length; i++){
        //    polarRing[i] = getPolarCoords(r.get(0), r.get(i));
        //}
        //quicksort both parallelly
        //^BLAH BLAH^ instead calculate and compare the angles, yet don't overwrite the elements with them.
        //don't start @0 to conserve first point equalling smallest
        ArrayList<P3D> sortedRing = new ArrayList<P3D>();
        sortedRing = quicksort(r, 1, r.size()-1);
        return sortedRing;
    }

    private double getPolarCoords(P3D c, P3D p){	//returns p in polar coordinates relative to center c
        //working on topView-coord.-system
        //CAUTION: topView(x)=reality(x), topView(y)=reality(z), reality(y) not in topView
        //TODO - OFFSET by pi()/4   !!!
        double ftheta = 0;
        //use p.x, p.z; y=0 because topView is P3D-source
        //coordinates relative to centre
        float relativeX = p.x-c.x;
        float relativeZ = p.z-c.z;
        /*
        //get distance (polar radius)
        double r = Math.sqrt(relativeX*relativeX + relativeZ*relativeZ);
        */
        //"false theta"
        ftheta = Math.atan(relativeZ/relativeX);

        //for sorting purposes, theta should be relative to pi()/2, increasing clockwise
        //so far: relative to 0, increasing CC-wise
        double ctheta = (450-ftheta)%360;

        return ctheta;
    }

    private ArrayList<P3D> quicksort(ArrayList<P3D> ring, int l, int r)
    {
        if (l >= r)
            return ring;

        //P3D pivot = ring.get(r);
        double pivot = getPolarCoords(ring.get(0), ring.get(r));

        int left = l;
        int right = r;

        //for each comparison: calculate polar angle and compare it
        while (left < right)
        {
            /*while(getPolarCoords(ring.get(0), ring.get(left)) < pivot)  //@left<pivot
                left++;

            while(getPolarCoords(ring.get(0), ring.get(right)) > pivot)  //@right>pivot
                right--;*/

            while(getPolarCoords(ring.get(0), ring.get(left)) > pivot)  //@left>pivot
                left++;

            while(getPolarCoords(ring.get(0), ring.get(right)) < pivot)  //@right<pivot
                right--;

            if (right > left);
            {
                Collections.swap(ring, left, right);
            }
        }

        quicksort(ring, l, right-1);
        quicksort(ring, right+1, r);

        return ring;
    }
	
	/*
	//deprecated, already in 3rdActivity
	//TODO - refactor into Model3D?
	public void scaleRing(ArrayList<P3D> rTBS, float skalar){	//ringToBeScaled
		//TODO - implement!
		//scale RingPointList-point-coords around centre
	}
	*/

}
