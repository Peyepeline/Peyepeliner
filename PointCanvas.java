package com.example.core.peyepeliner;

/**
 * Created by Isabell on 12.09.2017.
 */

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

import java.util.ArrayList;

//customisierte View-Klasse, integriert canvasToDrawUpon und backgroudBitmap in einem

public class PointCanvas extends ImageView {  //extends android.support.v7.widget.AppCompatImageView

    public ArrayList<PointF> points = new ArrayList<PointF>();

    private Paint pointMarker = new Paint();
    private Paint selectMarker = new Paint();

    private int operationID = 0;    //vgl. TriangleOperations.java
    public int selectedPoint = -1; //Position von selectedPoint in ArrayList

    public PointCanvas(Context context) {
        super(context);
    }

    public PointCanvas(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PointCanvas(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public int getOperationID(){
        return this.operationID;
    }

    public void setOperationID(int x){
        //mit test auf validität
        if (x<=3 && x>=0){
            this.operationID = x;
        }else{
            this.operationID = 0;
        }
    }

    public void initCPaint() {
        pointMarker.setStyle(Paint.Style.FILL);
        pointMarker.setColor(Color.MAGENTA);
        pointMarker.setStrokeWidth(2);
        selectMarker.setStyle(Paint.Style.STROKE);
        selectMarker.setColor(Color.YELLOW);
        selectMarker.setStrokeWidth(10);
    }

    public void deletePoint(){
        if(this.selectedPoint!=-1) {
            this.points.remove(this.selectedPoint);
        }
    }

    public void deleteEverything(){
        this.points.clear();
    }

    public void setSelectedPoint(int x, int y){    //methode zum setten selectedPoint
        PointF checkPos = new PointF(x, y);
        for(int i=0;i<this.points.size();i++){
            if(abstand(checkPos, this.points.get(i))<10){
                this.selectedPoint = i;
            }
        }
        this.selectedPoint = -1;
    }

    public int getSelectedPoint(){
        return this.selectedPoint;
    }

    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        //TODO - switch(operationID) for ACTION_DOWN
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                switch (this.operationID){
                    case 1: // Punkte hinzufuegen
                        this.points.add(new PointF(x,y));
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
        return true;
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);
        for(int i=0;i<points.size();i++){
            if(selectedPoint==i){
                canvas.drawCircle(points.get(i).x,points.get(i).y,20,selectMarker);
            }else {
                canvas.drawCircle(points.get(i).x, points.get(i).y, 20, pointMarker);
            }
        }
    }

    public double abstand(PointF p1, PointF p2){
        return Math.sqrt(((p1.x-p2.x)*(p1.x-p2.x))+((p1.y-p2.y)*(p1.y-p2.y)));
    }


}