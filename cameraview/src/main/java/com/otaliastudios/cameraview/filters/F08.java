package com.otaliastudios.cameraview.filters;

import androidx.annotation.NonNull;

import com.otaliastudios.cameraview.filter.BaseFilter;

public class F08 extends BaseFilter {

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
            + "  vec4 color = texture2D(sTexture, vTextureCoord);\n"
            + "  float colorR = ((1.438 * color.r) + (-0.062 * color.g) + (-0.062 * color.b));\n"
            + "  float colorG = ((-0.122 * color.r) + (1.378 * color.g) + (-0.122 * color.b));\n"
            + "  float colorB = ((-0.016 * color.r) + (-0.016 * color.g) + (1.483 * color.b));\n"
            + "  gl_FragColor = vec4(colorR, colorG, colorB, color.a);\n"
            + "}\n";


    public F08() { }

    @NonNull
    @Override
    public String getFragmentShader() {
        return FRAGMENT_SHADER;
    }
}
