package com.example.robotmachine.Model;

import com.example.robotmachine.Util.Point;
import com.example.robotmachine.Util.Util;

import java.nio.FloatBuffer;

public class Model {
    //三角面 个数
    private int facetCount;
    //顶点坐标数组
    private float[] verts;
    //每个顶点对应的法向量数组
    private float[] vnorms;
    //每个三角形的属性信息
    private short[] remarks;
    //顶点数组转换来的Buffer
    private FloatBuffer vertBuffer;
    //顶点数组对应的法向量转换而来的Buffer
    private FloatBuffer vnormBuffer;
    //以下分别保存所有点在x、y、z方向上的最大值、最小值
    float maxX;
    float minX;
    float maxY;
    float minY;
    float maxZ;
    float minZ;
    //返回模型的中心点
    public Point getCenterPoint(){
        float cx = minX + (maxX - minX) / 2;
        float cy = minY + (maxY - minY) / 2;
        float cz = minZ + (maxZ - minZ) / 2;
        return new Point(cx,cy,cz);
    }
    //包裹模型的最大半径
    public float getR(){
        float dx= (maxX - minX);
        float dy= (maxY - minY);
        float dz= (maxZ - minZ);
        float max = dx;
        if (dy > max){
            max = dy;
        }
        if (dz > max){
            max = dz;
        }
        return max;
    }

    //设置顶点数组的同时，设置对应的Buffer
    public void setVerts(float[] verts){
        this.verts = verts;
        vertBuffer = Util.floatToBuffer(verts);
    }
    //设置顶点数组法向量的同时，设置对应的 Buffer
    public void setVnorms(float[] vnorms){
        this.vnorms = vnorms;
        vnormBuffer = Util.floatToBuffer(vnorms);
    }

    public void setFaceCount(int faceCount) {
        this.facetCount = faceCount;
    }

    public int getFaceCount() {
        return facetCount;
    }

    public void setRemarks(short[] remarks) {
        this.remarks = remarks;
    }

    public FloatBuffer getVnormBuffer() {
        return vnormBuffer;
    }

    public FloatBuffer getVertBuffer() {
        return vertBuffer;
    }

    @Override
    public String toString() {
        return "maxX: "+ maxX + " maxY: "+ maxY + " maxZ: "+ maxZ;
    }
}
