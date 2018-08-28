package com.turkfyp.tarcomm2.MapObjects;

/**
 * Created by User-PC on 29/6/2017.
 */

import java.util.List;

public interface DirectionFinderListener {
    void onDirectionFinderStart();
    void onDirectionFinderSuccess(List<Route> route);
}
