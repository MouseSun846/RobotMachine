package com.example.robotmachine.Model;

import android.opengl.GLU;
import android.util.Log;

import com.example.robotmachine.Util.Point;

import javax.microedition.khronos.opengles.GL10;

public class ShowSTLModel {

    public void showModel(GL10 gl,Model model){

        //模型旋转
        int order = model.getOrder();

        if (order == 1) {
            gl.glTranslatef(0, -0.4f,0.3f);
        }else if (order == 2){
            gl.glTranslatef(0f, 0f,0.26f);
        }else if (order == 3){
            gl.glTranslatef(0f, 0f,0f);
        }else if (order == 4){
            gl.glTranslatef(0f, 0.35f,0f);
        }else if (order == 5){
            gl.glTranslatef(0.08f, 0f,0f);
        }else if (order == 6){
            gl.glTranslatef(0.27f, 0f,0f);
        }else if (order == 7){
            gl.glTranslatef(0.06f, 0f,0f);
        }


        if (order == 1){
            gl.glRotatef(-90f,1,0,0);
        }else if (order == 2){
            gl.glRotatef(90f,1,0,0);
        }
            gl.glRotatef(model.mDegree.x, 1, 0, 0);
            gl.glRotatef(model.mDegree.y, 0, 1, 0);
            gl.glRotatef(model.mDegree.z, 0, 0, 1);



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
