package com.example.photoeditor.helpers;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.SystemClock;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Created by Андрей on 27.11.2014.
 */
public class FirstRenderer implements GLSurfaceView.Renderer {
    private final FloatBuffer mFirstTriangle;
    private final int mBytesForFloat = 4;
    private final int mViewMatrixCount = 16;
    private float[]mViewMatrix = new float[mViewMatrixCount];
    private float[]mProjectionMatrix = new float[mViewMatrixCount];
    private  float[]mModelMatrix = new float[mViewMatrixCount];
    private float[]mMVPMatrix = new float[mViewMatrixCount];
    private int mMatrixHandle,mPositionHandle,mColorHandle;
    private final int mStrideBytes = 7 * mBytesForFloat;
    private final int mPositionOffset = 0;
    private final int mPositionDataSize = 3;
    private final int mColorOffset = 3;
    private final int mColorDataSize = 4;
    public FirstRenderer()
    {
        final float[] firstTriangle = new float[]
                {
                        -0.5f,-0.25f,0.0f,
                        1.0f,0.0f,0.0f,1.0f,

                        0.5f,-0.25f,0.0f,
                        0.0f,0.0f,1.0f,1.0f,

                        0.0f,0,5f,0.0f,
                        0.0f,1.0f,0.0f,1.0f
                };
        mFirstTriangle = ByteBuffer.allocateDirect(firstTriangle.length * mBytesForFloat)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        mFirstTriangle.put(firstTriangle).position(0);

    }

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        GLES20.glClearColor(0.5f,0.5f,0.5f,0.5f);
        final float eyeX = 0.0f,
                eyeY = 0.0f,
                eyeZ = 1.5f;
        final float lookX = 0.0f,
                lookY= 0.0f,
                lookZ = -5.0f;
        final float upX = 0.0f,
                upY = 1.0f,
                upZ = 0.0f;
        Matrix.setLookAtM(mViewMatrix,0,eyeX,eyeY,eyeZ,lookX,lookY,lookZ,upX,upY,upZ);
        final String vertexShader =
                "uniform mat4 u_MVPMatrix;      \n"     // Константа отвечающая за комбинацию матриц МОДЕЛЬ/ВИД/ПРОЕКЦИЯ.
                        + "attribute vec4 a_Position;     \n"     // Информация о положении вершин.
                        + "attribute vec4 a_Color;        \n"     // Информация о цвете вершин.
                        + "varying vec4 v_Color;          \n"     // Это будет передано в фрагментный шейдер.
                        + "void main()                    \n"     // Начало программы вершинного шейдера.
                        + "{                              \n"
                        + "   v_Color = a_Color;          \n"     // Передаем цвет для фрагментного шейдера.
                        // Он будет интерполирован для всего треугольника.
                        + "   gl_Position = u_MVPMatrix   \n"     // gl_Position специальные переменные используемые для хранения конечного положения.
                        + "               * a_Position;   \n"     // Умножаем вершины на матрицу для получения конечного положения
                        + "}                              \n";    // в нормированных координатах экрана.
        final String fragmentShader =
                "precision mediump float;       \n"     // Устанавливаем по умолчанию среднюю точность для переменных. Максимальная точность
                        // в фрагментном шейдере не нужна.
                        + "varying vec4 v_Color;          \n"     // Цвет вершинного шейдера преобразованного
                        // для фрагмента треугольников.
                        + "void main()                    \n"     // Точка входа для фрагментного шейдера.
                        + "{                              \n"
                        + "   gl_FragColor = v_Color;     \n"     // Передаем значения цветов.
                        + "}";
        int vertexShaderHandler = GLES20.glCreateShader(GLES20.GL_VERTEX_SHADER);
        if(vertexShaderHandler!=0)
        {
            GLES20.glShaderSource(vertexShaderHandler,vertexShader);
            GLES20.glCompileShader(vertexShaderHandler);
            final int[] compilerStatus = new int[1];
            GLES20.glGetShaderiv(vertexShaderHandler,GLES20.GL_COMPILE_STATUS,compilerStatus,0);
            if(compilerStatus[0]==0)
            {
                GLES20.glDeleteShader(vertexShaderHandler);
            }
        }
        if(vertexShaderHandler==0)
        {
            throw new RuntimeException("There is error in create shader");
        }
        int fragmentShaderHandler = GLES20.glCreateShader(GLES20.GL_FRAGMENT_SHADER);
        if(fragmentShaderHandler!=0)
        {
            GLES20.glShaderSource(fragmentShaderHandler,fragmentShader);
            GLES20.glCompileShader(fragmentShaderHandler);
            int[] fragmentCompilerStatus = new int[1];
            GLES20.glGetShaderiv(fragmentShaderHandler,GLES20.GL_COMPILE_STATUS,fragmentCompilerStatus,0);
            if(fragmentCompilerStatus[0]==0)
            {
                GLES20.glDeleteShader(fragmentShaderHandler);
            }
        }
        else
        {
            throw new RuntimeException("Error in create fragment shader");
        }
        int programHandler = GLES20.glCreateProgram();
        if(programHandler!=0)
        {
            GLES20.glAttachShader(programHandler,vertexShaderHandler);
            GLES20.glAttachShader(programHandler,fragmentShaderHandler);
            GLES20.glBindAttribLocation(programHandler, 0, "a_Position");
            GLES20.glBindAttribLocation(programHandler, 1, "a_Color");
            GLES20.glLinkProgram(programHandler);
            final int[] linkStatus = new int[1];
            GLES20.glGetProgramiv(programHandler,GLES20.GL_LINK_STATUS,linkStatus,0);
            if(linkStatus[0]==0)
            {
                GLES20.glDeleteProgram(programHandler);
            }
        }else
        {
            throw new RuntimeException("There is error in create program");
        }
        mMatrixHandle = GLES20.glGetUniformLocation(programHandler,"u_MVPMatrix");
        mPositionHandle = GLES20.glGetAttribLocation(programHandler,"a_Position");
        mColorHandle = GLES20.glGetAttribLocation(programHandler,"a_Color");
        GLES20.glUseProgram(programHandler);
    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int width, int height) {
        GLES20.glViewport(0,0,width,height);
        final float ratio = (float)width/height,
                left = -ratio,
                right = ratio,
                bottom = -1.0f,
                top = 1.0f,
                near = 1.0f,
                far = 10.0f;
        Matrix.frustumM(mProjectionMatrix,0,left,right,bottom,top,near,far);
        drawTriangle(mFirstTriangle);
    }

    @Override
    public void onDrawFrame(GL10 gl10) {
        GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT|GLES20.GL_COLOR_BUFFER_BIT);
        long time = SystemClock.uptimeMillis() % 10000L;
        float angleInDegrees = (360.0f / 10000.0f) * ((int) time);
        Matrix.setIdentityM(mModelMatrix, 0);
    }
    private void drawTriangle(final FloatBuffer aTriagnleBuffer)
    {
        aTriagnleBuffer.position(mPositionOffset);
        GLES20.glVertexAttribPointer(mPositionHandle, mPositionDataSize, GLES20.GL_FLOAT, false,
                mStrideBytes, aTriagnleBuffer);
        GLES20.glEnableVertexAttribArray(mPositionHandle);
        aTriagnleBuffer.position(mColorOffset);
        GLES20.glVertexAttribPointer(mColorHandle, mColorDataSize, GLES20.GL_FLOAT, false,
                mStrideBytes, aTriagnleBuffer);
        GLES20.glEnableVertexAttribArray(mColorHandle);
        Matrix.multiplyMM(mMVPMatrix,0,mViewMatrix,0,mModelMatrix,0);
        Matrix.multiplyMM(mMVPMatrix,0,mProjectionMatrix,0,mMVPMatrix,0);
        GLES20.glUniformMatrix4fv(mMatrixHandle, 1, false, mMVPMatrix, 0);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 3);
    }
}
