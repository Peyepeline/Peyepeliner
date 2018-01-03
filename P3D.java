package com.example.core.peyepeliner;

import android.graphics.PointF;

/**
 * Created by Core on 17.09.2017.
 */

class P3D {
    //private PointF pointF;
	public float x;
	public float y;
    public float z;

    public P3D(PointF p){
        this.x=p.x;
		this.y=p.y;
        this.z=0;
    }

    public P3D(float a){
        this.x=0;
        this.y=0;
        this.z=a;
    }

	public P3D(P3D p){
		this.x=p.x;
		this.y=p.y;
		this.z=p.z;
    }

    public P3D(PointF p, float x){
        this.x=p.x;
		this.y=p.y;
        this.z=x;
    }

    public P3D(float a, float b, float c){
		this.x=a;
		this.y=b;
		this.z=c;
    }

    public P3D(float a, float b){
        this.x=a;
		this.y=b;
        this.z=0;
    }

/*    public void setX(float a){
        this.z = a;
    }
    public void setY(float a){
        this.z = a;
    }
    public void setZ(float a){
        this.z = a;
    }

    public float getZ(){
        return this.z;
    }


    public void setPointF(PointF p){
        this.pointF = p;
    }
*/
    public PointF getPointF(){
        return new PointF(this.x, this.y);
    }

	public void set(float a, float b){
		this.x = a;
		this.y = b;
	}
	
	public void set(PointF p){
		this.x = p.x;
		this.y = p.y;
	}

	//adds vector (move by vector)
	public void aV(vector v){
        this.x += v.x;
        this.y += v.y;
        this.z += v.z;
    }

    public boolean compare(P3D p){
        if(this.x==p.x||this.y==p.y||this.z==p.z){
            return true;
        }else{
            return false;
        }
    }

}

class vector{
    public float x;
    public float y;
    public float z;

    //constructor for 0-3 floats
    public vector(){
        this.x = 0;
        this.y = 0;
        this.z = 0;
    }
    public vector(float a){
        this.x = a;
        this.y = 0;
        this.z = 0;
    }
    public vector(float a, float b){
        this.x = a;
        this.y = b;
        this.z = 0;
    }
    public vector(float a, float b, float c){
        this.x = a;
        this.y = b;
        this.z = c;
    }
    //constructor for 2 P3D
    public vector(P3D p0, P3D p1){
        this.x = p1.x-p0.x;
        this.y = p1.y-p0.y;
        this.z = p1.z-p0.z;
    }

    //vector p0->p1
    public void setVector(P3D p0, P3D p1){
        this.x = p1.x-p0.x;
        this.y = p1.y-p0.y;
        this.z = p1.z-p0.z;
    }

    public void scaleVector(float k){
        this.x *= k;
        this.y *= k;
        this.z *= k;
    }

    //EVEN IN 3D, |v| is sqrt(v.x^2 + v.y^2 + v.z^2) !!!
    public double length(){
        return Math.abs(Math.sqrt(this.x*this.x+this.y*this.y+this.z*this.z));
    }

    //calculates angle between vectors this and v
    public double angle(vector v){
        float prodSum = this.x*v.x+this.y*v.y+this.z*v.z;
        double lengthProd = this.length()*v.length();
        if(lengthProd != 0){
            return Math.acos(prodSum/lengthProd);
        }
        return 0;   //in case one vector has length 0.
    }
    //calculates angle between vectors u and v
    public double angle(vector u, vector v){
        float prodSum = u.x*v.x+u.y*v.y+u.z*v.z;
        double lengthProd = u.length()*v.length();
        if(lengthProd != 0){
            return Math.acos(prodSum/lengthProd);
        }
        return 0;   //in case one vector has length 0.
    }

    //sk.prod 2D
    //public float angle2D(){
    //    return 0;
    //}
    /*
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
    */
}

