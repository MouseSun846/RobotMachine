package com.example.robotmachine.Model;

import android.opengl.GLU;
import android.util.Log;

import com.example.robotmachine.Util.Point;

import javax.microedition.khronos.opengles.GL10;

public class ShowSTLModel {
    private Point eye;
    private Point up;
    private Point center;
    private float scale;
    //模型平移位置
    private Point movPos = new Point(0f,0f,0f);
    public ShowSTLModel(Point eye,Point up,Point center,float scale){
        this.eye = eye;
        this.up = up;
        this.center = center;
        this.scale = scale;
    }
    public void showModel(GL10 gl,Model model){
        //重置当前模型矩阵
        gl.glLoadIdentity();
        //眼睛对着原点看
        GLU.gluLookAt(gl,eye.x,eye.y,eye.z,center.x,center.y,center.z,up.x,up.y,up.z);
        //注意坐标轴也选择了（逆时针）
        gl.glRotatef(180f, 1f, 0f, 0f);
        //模型旋转
        int order = model.getOrder();
        if (order == 1){
            gl.glRotatef(90f,1,0,0);
        }else if (order == 2){
            gl.glRotatef(180f,1,0,0);
        }
        gl.glRotatef(model.mDegree.x,1,0,0);

        //将模型放缩到View刚好装下
        gl.glScalef(scale,scale,scale);
        //获得模型的中心点
        Point centerPoint = model.getCenterPoint();
        movPos.x = movPos.x + centerPoint.x;
        movPos.y = movPos.y + centerPoint.y;
        movPos.z = movPos.z + centerPoint.z;

        //拼接模型
        if (order == 1) {
            gl.glTranslatef(0, 0,-0.4f);
        }else if(order == 2){
            gl.glTranslatef(0, -0.17f,0);
        } else if (order == 6) {
            gl.glTranslatef(0.3f, -movPos.y,0);
        } else if (order > 6) {
            gl.glTranslatef(0.38f, -0.15f, 0);
        } else {
            gl.glTranslatef(0, -movPos.y, 0);
        }

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
    }


}
