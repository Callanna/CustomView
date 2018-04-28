package com.cvlib;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

import com.callanna.viewlibrary.R;

/**
 * Created by Callanna on 2018/1/4.
 */

public class BannerView extends View {
    //定义两个常量表示需要将这张图片划分成20*20=400个小方格,//定义两个常量,这两个常量指定该图片横向,纵向上都被划分为20格
    private  int WIDTH=40;//横向划分的方格数目
    private  int HEIGHT=40;//纵向划分的方格数目
    private float FREQUENCY=0.1f;//三角函数的频率大小
    private int AMPLITUDE=60;//三角函数的振幅大小
    //那么将会产生21*21=421个交叉点
    private  int POINT_COUNT=(WIDTH+1)*(HEIGHT+1);
    //由于，我要储存一个坐标信息，一个坐标包括x,y两个值的信息,相邻2个值储存为一个坐标点
    //其实大家应该都认为这样不好吧，还不如直接写一个类来直接保存一个点的信息，但是没办法
    // 但是在drawBitmapMesh方法中传入的是一个verts数组,该数组就是保存所有点的x,y坐标全都放在一起
    //所以,我就只能这样去控制定义orig和verts数组了,
    private Bitmap baseBitmap;
    private float[] orig=new float[POINT_COUNT*2];//乘以２是因为x,y值是一对的。
    private float[] verts=new float[POINT_COUNT*2];
    private float k;
    public BannerView(Context context, AttributeSet attrs,
                      int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //接收自定义属性值
        TypedArray array=context.obtainStyledAttributes(attrs, R.styleable.MikyouBannerView);
        for (int i = 0; i < array.getIndexCount(); i++) {
            int attr=array.getIndex(i);
            if (attr == R.styleable.MikyouBannerView_Src) {
                baseBitmap = BitmapFactory.decodeResource(getResources(), array.getResourceId(attr, R.drawable.ic_bg));

            } else if (attr == R.styleable.MikyouBannerView_ColumnNum) {
                HEIGHT = array.getInt(attr, 40);

            } else if (attr == R.styleable.MikyouBannerView_RowNum) {
                WIDTH = array.getInt(attr, 40);

            } else if (attr == R.styleable.MikyouBannerView_Amplitude) {
                AMPLITUDE = array.getInt(attr, 60);

            } else if (attr == R.styleable.MikyouBannerView_Frequency) {
                FREQUENCY = array.getFloat(attr, 0.1f);

            } else {
            }
        }
        array.recycle();
        initData();
    }
    public BannerView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }
    public BannerView(Context context) {
        this(context,null);
    }

    //set,gfanfg
    public int getWIDTH() {
        return WIDTH;
    }
    public void setWIDTH(int wIDTH) {
        WIDTH = wIDTH;
    }
    public int getHEIGHT() {
        return HEIGHT;
    }
    public void setHEIGHT(int hEIGHT) {
        HEIGHT = hEIGHT;
    }
    public float getFREQUENCY() {
        return FREQUENCY;
    }
    public void setFREQUENCY(float fREQUENCY) {
        FREQUENCY = fREQUENCY;
    }
    public int getAMPLITUDE() {
        return AMPLITUDE;
    }
    public void setAMPLITUDE(int aMPLITUDE) {
        AMPLITUDE = aMPLITUDE;
    }
    public Bitmap getBaseBitmap() {
        return baseBitmap;
    }
    public void setBaseBitmap(Bitmap baseBitmap) {
        this.baseBitmap = baseBitmap;
    }
    @Override
    protected void onDraw(Canvas canvas) {
        flagWave();
        k+=FREQUENCY;
        canvas.drawBitmapMesh(baseBitmap, WIDTH, HEIGHT, verts, 0, null, 0, null);
        invalidate();
    }
    private void initData() {
        float baseBitmapWidth=baseBitmap.getWidth();
        float baseBitmapHeight=baseBitmap.getHeight();
        int index=0;
        //通过遍历所有的划分后得到的像素块，得到原图中每个交叉点的坐标，并把它们保存在orig数组中
        for (int i = 0; i <= HEIGHT; i++) {//因为这个数组是采取行优先原则储存点的坐标，所以最外层为纵向的格子数,然后一行一行的遍历
            float fy=baseBitmapHeight*i/HEIGHT;//得到每行中每个交叉点的y坐标,同一行的y坐标一样
            for (int j = 0; j <= WIDTH; j++) {
                float fx=baseBitmapHeight*j/WIDTH;//得到每行中的每个交叉点的x坐标,同一列的x坐标一样
                orig[index*2+0]=verts[index*2+0]=fx;//存储每行中每个交叉点的x坐标，为什么是index*2+0作为数组的序号呢？？
                //因为我们之前也说过这个数组既存储x坐标也存储y坐标，所以每个点就占有２个单位数组空间
                orig[index*2+1]=verts[index*2+1]=fy+200;//存储每行中每个交叉点的y坐标.为什么需要+1呢?正好取x坐标相邻的下标的元素的值
                //+200是为了避免等下在正弦函数扭曲下，会把上部分给挡住所以下移200
                index++;
            }
        }
    }
    /**
     * @author mikyou
     * 加入三角函数正弦函数Sinx的算法，来修改原图数组保存的交叉点的坐标
     * 从而得到旗帜飘扬的效果，这里我们只修改y坐标,x坐标保持不变
     * */
    public void flagWave(){
        for (int i = 0; i <=HEIGHT ; i++) {
            for (int j = 0; j <WIDTH; j++) {
                verts[(i*(WIDTH+1)+j)*2+0]+=0;
                float offSetY=(float) Math.sin((float)j/WIDTH*2* Math.PI+ Math.PI*k);
                verts[(i*(WIDTH+1)+j)*2+1]=orig[(i*(WIDTH+1)+j)*2+1]+offSetY*AMPLITUDE;
            }
        }
    }
}
