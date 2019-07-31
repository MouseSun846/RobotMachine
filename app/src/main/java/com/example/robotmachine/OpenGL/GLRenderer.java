package com.example.robotmachine.OpenGL;

import android.content.Context;
import android.opengl.GLES10;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.util.Log;

import com.example.robotmachine.Model.Model;
import com.example.robotmachine.Model.STLReader;
import com.example.robotmachine.Util.Point;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;



public class GLRenderer implements GLSurfaceView.Renderer {
    private Model model;
    private Point mCenterPoint;
    private Point eye = new Point(0,0,-3);
    private Point up = new Point(0,1,0);
    private Point center = new Point(0,0,0);
    private float mScalef = 1;
    private float mDegree = 0;
    public GLRenderer(Context context){
        try {
            model = new STLReader().parseBinStlInAssets(context,"Link1.STL");
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public void rotate(float degree){
        mDegree = degree;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        //启用深度缓存
        gl.glEnable(GL10.GL_DEPTH_TEST);
        //设置深度缓存值
        gl.glClearDepthf(1.0f);
        //设置深度缓存比较函数
        gl.glDepthFunc(GLES10.GL_LEQUAL);
        //设置阴影模式
        gl.glShadeModel(GLES10.GL_SMOOTH);
        float r = model.getR();
        //r 是半径，因此用0.5/r计算缩放比例
        mScalef = 0.5f / r;
        mCenterPoint = model.getCenterPoint();
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
        GLU.gluPerspective(gl,45.0f,((float)(width)/height),1f,100f);
        //声明所有的变换都是针对于模型
        gl.glMatrixMode(GL10.GL_MODELVIEW);
        gl.glLoadIdentity();
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        // 清除屏幕和深度缓存
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT|GL10.GL_DEPTH_BUFFER_BIT);
        //重置当前模型矩阵
        gl.glLoadIdentity();
        //眼睛对着原点看
        GLU.gluLookAt(gl,eye.x,eye.y,eye.z,center.x,center.y,center.z,up.x,up.y,up.z);
        //注意坐标轴也选择了（逆时针）
        gl.glRotatef(-100f, 1f, 0f, 0f);
        //模型旋转
        gl.glRotatef(mDegree,0,1,0);
        //将模型放缩到View刚好装下
        gl.glScalef(mScalef,mScalef,mScalef);
        //将模型移动到原点
        gl.glTranslatef(-mCenterPoint.x,-mCenterPoint.y,-mCenterPoint.z);
        //***************begin***************//
        //允许给每个顶点设置法向量
        gl.glEnableClientState(GL10.GL_NORMAL_ARRAY);
        //允许设置顶点
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        //允许设置法向量数据源
        gl.glNormalPointer(GL10.GL_FLOAT,0,model.getVnormBuffer());
        //设置三角形顶点数据源
        gl. glVertexPointer(3,GL10.GL_FLOAT,0,model.getVertBuffer());
        //绘制三角形
        gl.glDrawArrays(GL10.GL_TRIANGLES,0,model.getFaceCount()*3);
        //取消顶点设置
        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
        //取消法向量设置
        gl.glDisableClientState(GL10.GL_NORMAL_ARRAY);
        //***************end***************//
        gl.glFinish();
        if (mDegree > 360){
            mDegree = 0;
        }else {
            mDegree += 1;
        }

    }
}
