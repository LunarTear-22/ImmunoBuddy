package com.example.immunobubby;

import com.google.android.gms.maps.model.LatLng;
import java.util.List;

public class RouteData {
    private List<LatLng> coordinates;
    private double distance;

    public RouteData(List<LatLng> coordinates, double distance) {
        this.coordinates = coordinates;
        this.distance = distance;
    }

    public List<LatLng> getCoordinates() {
        return coordinates;
    }

    public double getDistance() {
        return distance;
    }
}
