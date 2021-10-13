package com.otaliastudios.cameraview.filter;

import androidx.annotation.NonNull;

import com.otaliastudios.cameraview.filters.AutoFixFilter;
import com.otaliastudios.cameraview.filters.BW01;
import com.otaliastudios.cameraview.filters.BW02;
import com.otaliastudios.cameraview.filters.BW03;
import com.otaliastudios.cameraview.filters.BlackAndWhiteFilter;
import com.otaliastudios.cameraview.filters.BrightnessFilter;
import com.otaliastudios.cameraview.filters.ContrastFilter;
import com.otaliastudios.cameraview.filters.CrossProcessFilter;
import com.otaliastudios.cameraview.filters.DocumentaryFilter;
import com.otaliastudios.cameraview.filters.DuotoneFilter;
import com.otaliastudios.cameraview.filters.F01;
import com.otaliastudios.cameraview.filters.F02;
import com.otaliastudios.cameraview.filters.F03;
import com.otaliastudios.cameraview.filters.F04;
import com.otaliastudios.cameraview.filters.F05;
import com.otaliastudios.cameraview.filters.F06;
import com.otaliastudios.cameraview.filters.F07;
import com.otaliastudios.cameraview.filters.F08;
import com.otaliastudios.cameraview.filters.F09;
import com.otaliastudios.cameraview.filters.F10;
import com.otaliastudios.cameraview.filters.F11;
import com.otaliastudios.cameraview.filters.F13;
import com.otaliastudios.cameraview.filters.FillLightFilter;
import com.otaliastudios.cameraview.filters.GammaFilter;
import com.otaliastudios.cameraview.filters.GrainFilter;
import com.otaliastudios.cameraview.filters.GrayscaleFilter;
import com.otaliastudios.cameraview.filters.HueFilter;
import com.otaliastudios.cameraview.filters.InvertColorsFilter;
import com.otaliastudios.cameraview.filters.LomoishFilter;
import com.otaliastudios.cameraview.filters.PosterizeFilter;
import com.otaliastudios.cameraview.filters.SaturationFilter;
import com.otaliastudios.cameraview.filters.SepiaFilter;
import com.otaliastudios.cameraview.filters.SharpnessFilter;
import com.otaliastudios.cameraview.filters.TemperatureFilter;
import com.otaliastudios.cameraview.filters.TintFilter;
import com.otaliastudios.cameraview.filters.VignetteFilter;

/**
 * Contains commonly used {@link Filter}s.
 * <p>
 * You can use {@link #newInstance()} to create a new instance and
 * pass it to {@link com.otaliastudios.cameraview.CameraView#setFilter(Filter)}.
 */
public enum Filters {

    /** @see NoFilter *//*
    NONE(NoFilter.class),

    *//** @see AutoFixFilter *//*
    AUTO_FIX(AutoFixFilter.class),

    *//** @see BlackAndWhiteFilter *//*
    BLACK_AND_WHITE(BlackAndWhiteFilter.class),

    *//** @see BrightnessFilter *//*
    BRIGHTNESS(BrightnessFilter.class),

    *//** @see ContrastFilter *//*
    CONTRAST(ContrastFilter.class),

    *//** @see CrossProcessFilter *//*
    CROSS_PROCESS(CrossProcessFilter.class),

    *//** @see DocumentaryFilter *//*
    DOCUMENTARY(DocumentaryFilter.class),

    *//** @see DuotoneFilter *//*
    DUOTONE(DuotoneFilter.class),

    *//** @see FillLightFilter *//*
    FILL_LIGHT(FillLightFilter.class),

    *//** @see GammaFilter *//*
    GAMMA(GammaFilter.class),

    *//** @see GrainFilter *//*
    GRAIN(GrainFilter.class),

    *//** @see GrayscaleFilter *//*
    GRAYSCALE(GrayscaleFilter.class),

    *//** @see HueFilter *//*
    HUE(HueFilter.class),

    *//** @see InvertColorsFilter *//*
    INVERT_COLORS(InvertColorsFilter.class),

    *//** @see LomoishFilter *//*
    LOMOISH(LomoishFilter.class),

    *//** @see PosterizeFilter *//*
    POSTERIZE(PosterizeFilter.class),

    *//** @see SaturationFilter *//*
    SATURATION(SaturationFilter.class),

    *//** @see SepiaFilter *//*
    SEPIA(SepiaFilter.class),

    *//** @see SharpnessFilter *//*
    SHARPNESS(SharpnessFilter.class),

    *//** @see TemperatureFilter *//*
    TEMPERATURE(TemperatureFilter.class),

    *//** @see TintFilter *//*
    TINT(TintFilter.class),

    *//**
     * @see VignetteFilter
     *//*
    VIGNETTE(VignetteFilter.class);*/
    NONE(NoFilter.class),
    F_01(F01.class),
    F_02(F02.class),
    //F_03(F03.class),
    F_04(F04.class),
    F_05(F05.class),
    F_06(F06.class),
    F_07(F07.class),
    F_08(F08.class),
    F_09(F09.class),
    F_10(F10.class),
    F_11(F11.class),
    F_13(F13.class),
    BW_01(BW01.class),
    BW_02(BW02.class),
    BW_03(BW03.class);

    private Class<? extends Filter> filterClass;

    Filters(@NonNull Class<? extends Filter> filterClass) {
        this.filterClass = filterClass;
    }

    /**
     * Returns a new instance of the given filter.
     *
     * @return a new instance
     */
    @NonNull
    public Filter newInstance() {
        try {
            return filterClass.newInstance();
        } catch (IllegalAccessException e) {
            return new NoFilter();
        } catch (InstantiationException e) {
            return new NoFilter();
        }
    }
}
