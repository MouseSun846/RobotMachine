package com.example.robotmachine.Model;

import android.content.Context;

import com.example.robotmachine.Util.Util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class STLReader {
    private StlLoadListener stlLoadListener;
    //从外部 SD卡加载
    private Model parseBinStlInSDCard(String path) throws IOException{
        File file = new File(path);
        FileInputStream fileInputStream = new FileInputStream(file);
        return parseBinStl(fileInputStream);
    }
    //从Assets中加载
    public Model parseBinStlInAssets(Context context,String fileName) throws IOException{
        InputStream inputStream = context.getAssets().open(fileName);
        return parseBinStl(inputStream);
    }
    //解析二进制STL文件
    public Model parseBinStl(InputStream in) throws IOException{
        if (stlLoadListener != null){
            stlLoadListener.onStart();
        }
        Model model = new Model();
        //前面80个字节是文件头，存储是文件名
        in.skip(80);
        //紧接着用4个字节的整数来描述模型的三角形片面的个数
        byte[] bytes = new byte[4];
        in.read(bytes);
        int faceCount = Util.byte4ToInt(bytes,0);
        model.setFaceCount(faceCount);
        if (faceCount == 0){
            in.close();
            return model;
        }
        //每个三角形片面占用固定的50个字节
        byte[] faceBytes = new byte[50 * faceCount];
        //将所有的三角形片面读取到字节数组
        in.read(faceBytes);
        in.close();
        parseModel(model,faceBytes);
        if (stlLoadListener != null){
            stlLoadListener.onFinished();
        }
        return model;
    }

    //解析模型数据，包括顶点数据、法向量数据、所占空间范围
    private void parseModel(Model model, byte[] faceBytes) {
        int facetCount = model.getFaceCount();
        /**
         * 每个三角面片占用固定的50个字节，50字节当中：
         * 三角片的法向量：（1个向量相当于一个点）*（3维/点）*（4字节浮点数/维）=12字节
         * 三角片的三个点坐标：（3个点）*（3维/点）*（4字节浮点数/维）=36字节 （顶点坐标 1 + 2 + 3）
         * 最后2字节用来三角面片的属性信息
         */
        //保存所有顶点坐标 信息，一个三角形3个顶点,一个顶点3个坐标
        float[] verts = new float[facetCount * 3 * 3];
        /**
         * 保存所有三角形对应法向量的位置
         * 一个三角形对应一个法向量，一个法向量对应3个点
         * 而绘制模型时候，是针对需要每个顶点对应的法向量，因此存储长度 * 3
         * 又同一个三角面的三个顶点的法量是相同的
         * 因此后面写入的法向量数据的时候，只需要连续写入3个相同的 法向量即可
         */
        float[] vnroms = new float[facetCount * 3 * 3];
        //保存三角面的属性信息
        short[] remarks = new short[facetCount];
        int stlOffset = 0;
        try {
            for (int i = 0; i < facetCount; i++) {
                if (stlLoadListener  != null){
                    stlLoadListener.onLoading(i,facetCount);
                }
                //处理48个字节 信息
                for (int j = 0; j < 4; j++) {
                    float x = Util.byte4ToFloat(faceBytes,stlOffset);
                    float y = Util.byte4ToFloat(faceBytes,stlOffset + 4);
                    float z = Util.byte4ToFloat(faceBytes,stlOffset + 8);
                    stlOffset += 12;
                    //法向量三个坐标
                    if ( 0 == j){
                        vnroms[i*9 + 0] = x;
                        vnroms[i*9 + 1] = y;
                        vnroms[i*9 + 2] = z;
                        vnroms[i*9 + 3] = x;
                        vnroms[i*9 + 4] = y;
                        vnroms[i*9 + 5] = z;
                        vnroms[i*9 + 6] = x;
                        vnroms[i*9 + 7] = y;
                        vnroms[i*9 + 8] = z;
                    }else {
                        //三个顶点
                        verts[i*9 +(j - 1)*3 + 0] = x;
                        verts[i*9 +(j - 1)*3 + 1] = y;
                        verts[i*9 +(j - 1)*3 + 2] = z;
                        //记录模型中三个坐标轴方向的最大最小值
                        if (i == 0 && j == 1){
                            model.minX = model.maxX = x;
                            model.minY = model.maxY = y;
                            model.minZ = model.maxZ = z;
                        }else {
                            model.minX = Math.min(model.minX,x);
                            model.minY = Math.min(model.minY,y);
                            model.minZ = Math.min(model.minZ,z);
                            model.maxX = Math.max(model.maxX,x);
                            model.maxY = Math.max(model.maxY,y);
                            model.maxZ = Math.max(model.maxZ,z);
                        }
                    }
                }
                short r = Util.byte2ToShort(faceBytes,stlOffset);
                stlOffset = stlOffset + 2;
                remarks[i]=r;
            }
        }catch (Exception e){
            if (stlLoadListener !=null){
                stlLoadListener.onFailure(e);
            }else {
                e.printStackTrace();
            }
        }
        //将读取的数据设置到Model对象中
        model.setVerts(verts);
        model.setVnorms(vnroms);
        model.setRemarks(remarks);

    }
    private void parseTexture(Model model, byte[] textureBytes) {
        int facetCount = model.getFaceCount();
        // 三角面个数有三个顶点，一个顶点对应纹理二维坐标
        float[] textures = new float[facetCount * 3 * 2];
        int textureOffset = 0;
        for (int i = 0; i < facetCount * 3; i++) {
            //第i个顶点对应的纹理坐标
            //tx和ty的取值范围为[0,1],表示的坐标位置是在纹理图片上的对应比例
            float tx = Util.byte4ToFloat(textureBytes, textureOffset);
            float ty = Util.byte4ToFloat(textureBytes, textureOffset + 4);

            textures[i * 2] = tx;
            //我们的pxy文件原点是在左下角，因此需要用1减去y坐标值
            textures[i * 2 + 1] = 1 - ty;

            textureOffset += 8;
        }
//        model.setTextures(textures);
    }

    public static interface StlLoadListener{
        void onStart();
        void onLoading(int cur,int total);
        void onFinished();
        void onFailure(Exception e);
    }

}
