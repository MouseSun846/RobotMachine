package com.example.robotmachine.OpenGL;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import static android.opengl.GLES20.GL_LINEAR;
import static android.opengl.GLES20.GL_LINEAR_MIPMAP_LINEAR;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.GL_TEXTURE_MAG_FILTER;
import static android.opengl.GLES20.GL_TEXTURE_MIN_FILTER;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glDeleteTextures;
import static android.opengl.GLES20.glGenTextures;
import static android.opengl.GLES20.glGenerateMipmap;
import static android.opengl.GLES20.glTexParameteri;
import static android.opengl.GLUtils.texImage2D;

public class TextureHelper {
    private static final String TAG = "TextureHelper";
    public static int loadTexture(Context context, int resourceId){
        final int[] textureObjectIds = new int[1];
        //创建纹理对象
        glGenTextures(1,textureObjectIds,0);
        if (textureObjectIds[0] == 0){
            Log.w("mouse","Could  not generate a new OpenGL texture  object.");
            return 0;
        }
        final BitmapFactory.Options options = new BitmapFactory.Options();
        //不缩放图片
        options.inScaled = false;
        final Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(),resourceId,options);
        if (bitmap == null){
            Log.w(TAG,"Resource Id "+resourceId+" could not be decode.");
            glDeleteTextures(1,textureObjectIds,0);
            return 0;
        }
        //绑定二维纹理对象
        glBindTexture(GL_TEXTURE_2D,textureObjectIds[0]);
        //缩小情况下的，三线性过滤
        glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_MIN_FILTER,GL_LINEAR_MIPMAP_LINEAR);
        //放大情况，双线性过滤
        glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_MAG_FILTER,GL_LINEAR);
        //加载纹理到OpenGL
        texImage2D(GL_TEXTURE_2D,0,bitmap,0);
        //释放掉
        bitmap.recycle();
        //生成MIP贴图
        glGenerateMipmap(GL_TEXTURE_2D);
        //解除与这个纹理绑定
        glBindTexture(GL_TEXTURE_2D,0);
        //返回纹理对象
        return textureObjectIds[0];
    }
}
