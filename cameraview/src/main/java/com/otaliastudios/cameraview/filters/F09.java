package com.otaliastudios.cameraview.filters;

import androidx.annotation.NonNull;

import com.otaliastudios.cameraview.filter.BaseFilter;

public class F09  extends BaseFilter {

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
            + "  float colorR = ((1.1285582396593525 * color.r) + (-0.3967382283601348 * color.g) + (-0.03992559172921793 * color.b));\n"
            + "  float colorG = ((-0.16404339962244616 * color.r) + (1.0835251566291304 * color.g) + (-0.05498805115633132 * color.b));\n"
            + "  float colorB = ((-0.16786010706155763 * color.r) + (0.5603416277695248 * color.g) + (1.6014850761964943 * color.b));\n"
            + "  gl_FragColor = vec4(colorR, colorG, colorB, color.a);\n"
            + "}\n";

    public F09() { }

    @NonNull
    @Override
    public String getFragmentShader() {
        return FRAGMENT_SHADER;
    }
}
