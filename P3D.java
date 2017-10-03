package com.example.core.peyepeliner;

import android.graphics.PointF;

import java.io.Serializable;

/**
 * Created by Core on 17.09.2017.
 */

class P3D{
    //private PointF pointF;
	public float x;
	public float y;
    public float z;

    public P3D(PointF p){
        this.x=p.x;
		this.y=p.y;
    }

    public P3D(float a){
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

}
