package com.example.robotmachine.OpenGL;

import android.content.Context;
import android.opengl.GLES10;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.util.Log;

import com.example.robotmachine.Model.Model;
import com.example.robotmachine.Model.STLReader;
import com.example.robotmachine.Model.ShowSTLModel;
import com.example.robotmachine.R;
import com.example.robotmachine.Util.MouseRotate;
import com.example.robotmachine.Util.Point;
import com.example.robotmachine.Util.Util;

import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;



public class GLRenderer implements GLSurfaceView.Renderer {
//    private Point mCenterPoint;
    private int modelId = 0;
    private Point eye = new Point(0,0,-3);
    private Point up = new Point(0,1,0);
    private Point center = new Point(0,0,0);
    private float mScalef = 1;
    private MouseRotate mouseRotate = new MouseRotate(0,0);
    //纹理
    private float cnt=0;
    private Context context;
    private List<Model> models = new ArrayList<>();

    public GLRenderer(final Context context){

        initSixAxisesView(context);

        this.context = context;
    }
    private void initSixAxisesView(Context context){
        String[] link = context.getResources().getStringArray(R.array.link);
        try {
        for (int i = 0; i < link.length; i++) {
            Model model = new STLReader().parseBinStlInAssets(context,link[i]);
            model.setOrder(i+1);
            models.add(model);
            Log.i("mouse","i = "+i);
        }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        gl.glClearColor(0.0f,0.0f,0.0f,1.0f);
        //启用深度缓存
        gl.glEnable(GL10.GL_DEPTH_TEST);
        //设置深度缓存值
        gl.glClearDepthf(1.0f);
        //设置深度缓存比较函数
        gl.glDepthFunc(GLES10.GL_LEQUAL);
        //设置阴影模式
        gl.glShadeModel(GLES10.GL_SMOOTH);
        //所有的变换都针对于模型
        gl.glMatrixMode(GL10.GL_MODELVIEW);
        float r = Util.getR(models);
        //r 是半径，因此用0.5/r计算缩放比例
        mScalef = 0.5f / r;
        //开启光
        openLight(gl);
        //添加材质属性
        enableMaterial(gl);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        //设置视口
        gl.glViewport(0,0,width,height);
        //创建投影矩阵
        gl.glMatrixMode(GL10.GL_PROJECTION);
        //将投影矩阵设置单位矩阵
        gl.glLoadIdentity();
        //设置透视范围
        GLU.gluPerspective(gl,60.0f,((float)(width)/height),1f,100f);
        //声明所有的变换都是针对于模型
        gl.glMatrixMode(GL10.GL_MODELVIEW);
        gl.glLoadIdentity();
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        // 清除屏幕和深度缓存
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT|GL10.GL_DEPTH_BUFFER_BIT);
        gl.glMatrixMode(GL10.GL_MODELVIEW);
        //重置当前模型矩阵
        gl.glLoadIdentity();
        //眼睛对着原点看
        GLU.gluLookAt(gl,eye.x,eye.y,eye.z,center.x,center.y,center.z,up.x,up.y,up.z);
        //注意坐标轴也选择了（逆时针）
//        gl.glRotatef(90f, 1f, 0f, 0f);
        //将模型放缩到View刚好装下
        gl.glScalef(mScalef,mScalef,mScalef);
        ShowSTLModel showSTLModel = new ShowSTLModel();
        modelControlState();
        if (models.size() == 9 ) {
            for (Model model : models) {
                showSTLModel.showModel(gl, model);
            }
        }
    }
    float[] ambient = {0.0f, 0.0f, 0.0f, 1.0f,};
    float[] diffuse = {1.0f, 1.0f, 1.0f,1.0f,};
    float[] specular = {1.0f, 1.0f, 1.0f, 1.0f,};
    float[] lightPosition = {0.5f, 0.5f, 0.5f, 0.0f,};

    public void openLight(GL10 gl) {
        gl.glEnable(GL10.GL_LIGHTING);
        gl.glEnable(GL10.GL_LIGHT0);
        gl.glShadeModel(GL10.GL_SMOOTH);
        //RGBA模式下环境光
        gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_AMBIENT, Util.floatToBuffer(ambient));
        //RGBA模式下漫反射光
        gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_DIFFUSE, Util.floatToBuffer(diffuse));
        //RGBA模式下镜面光
        gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_SPECULAR, Util.floatToBuffer(specular));
        //光源位置齐次坐标
        gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_POSITION, Util.floatToBuffer(lightPosition));

    }

    float[] materialAmb = {1.0f, 0.6f, 0.3f, 1.0f};
    float[] materialDiff = {1.0f, 0.6f, 0.3f, 1.0f};//漫反射
    float[] materialSpec = {1.0f, 0.6f, 0.3f,  1.0f};
    //衰减系数，越小越亮
    float[] shiness = {0.8f};

    public void enableMaterial(GL10 gl) {

        //材料对环境光的反射情况
        gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_AMBIENT, Util.floatToBuffer(materialAmb));
        //散射光的反射情况
        gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_DIFFUSE, Util.floatToBuffer(materialDiff));
        //镜面光的反射情况
        gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_SPECULAR, Util.floatToBuffer(materialSpec));
        //光亮度
        gl.glMaterialfv(GL10.GL_FRONT_AND_BACK,GL10.GL_SHININESS,Util.floatToBuffer(shiness));
    }

    //按下时候坐标
    public void handleTouchPress(float normalizex, float normalizey) {
//        Log.i("mouse","---Press--- "+"X: "+normalizex+" Y: "+normalizey);
        mouseRotate.preX = normalizex;
        mouseRotate.preY = normalizey;
    }

    //拖拉坐标
    public void handleTouchDrag(float normalizex, float normalizey) {
//        Log.i("mouse","---Drag--- "+"X: "+normalizex+" Y: "+normalizey);
        mouseRotate.xDegree = (normalizex-mouseRotate.preX)*360;
        mouseRotate.yDegree = (normalizey-mouseRotate.preY)*360;
    }

    //RadioButton 监听接口
    public void rbOnCheckedChanged(int i) {
        this.modelId = i;
    }

    //根据监听的RadioButton接口实现模型控制
    public void modelControlState(){
        switch (modelId){
            case R.id.rbAxiseAll:
                models.get(0).mDegree.x=mouseRotate.xDegree;

                break;
            case R.id.rbAxiseOne:
                models.get(1).mDegree.y=mouseRotate.xDegree;

                break;
            case R.id.rbAxiseTwo:
                models.get(2).mDegree.z=mouseRotate.xDegree;

                break;
            case R.id.rbAxiseThree:
                models.get(3).mDegree.z=mouseRotate.xDegree;

                break;
            case R.id.rbAxiseFour:
                models.get(4).mDegree.x=mouseRotate.xDegree;
                break;
            case R.id.rbAxiseFive:
                models.get(5).mDegree.z=mouseRotate.xDegree;
                break;
            case R.id.rbAxiseSix:

                break;
        }
//        Log.i("mouse","xDegree  "+mouseRotate.xDegree);
    }

}
