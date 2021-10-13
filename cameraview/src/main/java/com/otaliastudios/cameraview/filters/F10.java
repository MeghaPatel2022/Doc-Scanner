package com.otaliastudios.cameraview.filters;

import androidx.annotation.NonNull;

import com.otaliastudios.cameraview.filter.BaseFilter;

public class F10  extends BaseFilter {

    /*private final static String FRAGMENT_SHADER = "#extension GL_OES_EGL_image_external : require\n"
            + "precision mediump float;\n"
            + "varying vec2 vTextureCoord;\n"
            + "uniform samplerExternalOES sTexture;\n" + "void main() {\n"
            + "  vec4 color = texture2D(sTexture, vTextureCoord);\n"
            + "  float colorR = (color.r + color.g + color.b)/3.0;\n"
            + "  float colorG = (color.r + color.g + color.b)/3.0 ;\n"
            + "  float colorB = (color.r + color.g + color.b)/3.0;\n"
            + "  gl_FragColor = vec4(colorR, colorG, colorB, color.a);\n"
            + "}\n";*/
    private final static String FRAGMENT_SHADER = "#extension GL_OES_EGL_image_external : require\n"
            + "precision mediump float;\n"
            + "varying vec2 vTextureCoord;\n"
            + "uniform samplerExternalOES sTexture;\n" + "void main() {\n"
            + "  vec4 color = texture2D(sTexture,vTextureCoord);\n"
            + "  float colorR = 2.0 * color.r - 0.4 * color.g + 0.5 * color.b;\n"
            + "  float colorG = -0.5 * color.r + 2.0 * color.g - 0.4 * color.b;\n"
            + "  float colorB = -0.4 * color.r - 0.5 * color.g + 3.0 * color.b;\n"
            + "  gl_FragColor = vec4(colorR, colorG, colorB, color.a);\n"
            + "}\n";

    public F10() { }

    @NonNull
    @Override
    public String getFragmentShader() {
        return FRAGMENT_SHADER;
    }
}