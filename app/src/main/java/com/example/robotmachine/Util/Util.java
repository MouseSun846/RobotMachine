package com.example.robotmachine.Util;

import com.example.robotmachine.Model.Model;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.List;

public class Util {
    public static FloatBuffer floatToBuffer(float[] a){
        //先初始化buffer，数组的长度*4，因为一个float占4个字节
        ByteBuffer bb = ByteBuffer.allocateDirect(a.length * 4);
        //数组排序用nativeOrder
        bb.order(ByteOrder.nativeOrder());
        FloatBuffer buffer = bb.asFloatBuffer();
        buffer.put(a);
        buffer.position(0);
        return buffer;
    }
    public static int byte4ToInt(byte[] bytes,int offset){
        int b3 = bytes[offset + 3] & 0XFF;
        int b2 = bytes[offset + 2] & 0XFF;
        int b1 = bytes[offset + 1] & 0XFF;
        int b0 = bytes[offset + 0] & 0XFF;
        return (b3 << 24)|(b2 << 16)|(b1 << 8)|b0;

    }
    public static short byte2ToShort(byte[] bytes,int offset)
    {
        int b1 = bytes[offset + 1] & 0XFF;
        int b0 = bytes[offset + 0] & 0XFF;
        return (short) ((b1 << 8) | b0);
    }
    public static float byte4ToFloat(byte[] bytes,int offset){
        return Float.intBitsToFloat(byte4ToInt(bytes,offset));
    }
    private static Point modelsBorder(List<Model> models, boolean isMin) {
        Point p;
        if (isMin)
            p = new Point(Float.MAX_VALUE, Float.MAX_VALUE, Float.MAX_VALUE);
        else
            p = new Point(Float.MIN_VALUE, Float.MIN_VALUE, Float.MIN_VALUE);

        for (Model model : models) {
            if (isMin) {//如果取的是最小的xyz
                if (model.minX < p.x)
                    p.x = model.minX;
                if (model.minY < p.y)
                    p.y = model.minY;
                if (model.minZ < p.z)
                    p.z = model.minZ;
            } else {//如果取的是最大的xyz
                if (model.maxX > p.x)
                    p.x = model.minX;
                if (model.maxY > p.y)
                    p.y = model.minY;
                if (model.maxZ > p.z)
                    p.z = model.minZ;
            }
        }

        return p;
    }

    public static Point getCenter(List<Model> models) {
        Point min = modelsBorder(models, true);
        Point max = modelsBorder(models, false);
        float cx = min.x + (max.x - min.x) / 2;
        float cy = min.y + (max.y - min.y) / 2;
        float cz = min.z + (max.z - min.z) / 2;
        return new Point(cx, cy, cz);
    }

    public static float getR(List<Model> models) {
        Point minP = modelsBorder(models, true);
        Point maxP = modelsBorder(models, false);
        float rx = maxP.x - minP.x;
        float ry = maxP.y - minP.y;
        float rz = maxP.z - minP.z;
        float r = (float) (Math.sqrt(rx * rx + ry * ry + rz * rz) / 2);
        return r;
    }
}

