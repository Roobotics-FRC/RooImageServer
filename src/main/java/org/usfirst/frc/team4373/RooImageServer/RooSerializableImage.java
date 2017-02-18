package org.usfirst.frc.team4373.RooImageServer;

import org.opencv.core.Mat;
import org.usfirst.frc.team4373.roosight.RooColorImage;

import java.io.Serializable;

/**
 * Created by derros on 2/18/17.
 */
public class RooSerializableImage extends RooColorImage implements Serializable {

    public RooSerializableImage(Mat m) {
        super(m);
    }

}
