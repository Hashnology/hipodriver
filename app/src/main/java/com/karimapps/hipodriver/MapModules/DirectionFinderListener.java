package com.karimapps.hipodriver.MapModules;

import java.util.List;

/**
 * Created by Mai Thanh Hiep on 4/3/2016.
 */
public interface DirectionFinderListener {
    void onDirectionFinderStart(String operationName);
    void onDirectionFinderSuccess(List<Route> route, String operationName);
}
