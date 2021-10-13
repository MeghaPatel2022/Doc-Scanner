package com.otaliastudios.cameraview.filters;

import androidx.annotation.NonNull;

import com.otaliastudios.cameraview.filter.BaseFilter;

public class F13 extends BaseFilter {

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
            + "  float colorR = ((0.6279345635605994 * color.r) + (0.3202183420819367 * color.g) + (-0.03965408211312453 * color.b));\n"
            + "  float colorG = ((0.02578397704808868 * color.r) + (0.6441188644374771 * color.g) + (0.03259127616149294 * color.b));\n"
            + "  float colorB = ((0.0466055556782719 * color.r) + (-0.0851232987247891 * color.g) + (0.5241648018700465 * color.b));\n"
            + "  gl_FragColor = vec4(colorR, colorG, colorB, color.a);\n"
            + "}\n";

    public F13() { }

    @NonNull
    @Override
    public String getFragmentShader() {
        return FRAGMENT_SHADER;
    }
}
