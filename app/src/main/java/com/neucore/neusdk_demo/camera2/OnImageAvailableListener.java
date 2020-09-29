package com.neucore.neusdk_demo.camera2;

import android.media.ImageReader;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.neucore.neusdk_demo.utils.Util;

/**
 * ImageAvailableListener
 * Created by Administrator on 2018/10/31.
 */

@RequiresApi(api = Build.VERSION_CODES.KITKAT)
public class OnImageAvailableListener implements ImageReader.OnImageAvailableListener {

    private int orientation = 0;

    @Override
    public void onImageAvailable(ImageReader reader) {
        //System.out.println("    OnImageAvailableListener");
        //Util.setPaintViewUI(reader);
    }

    public void setOrientation(int orientation) {
        this.orientation = orientation;
    }

    public int getOrientation() {
        return orientation;
    }
}
