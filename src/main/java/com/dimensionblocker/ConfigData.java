package com.dimensionblocker;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ConfigData {
    private ArrayList<String> dimensions = new ArrayList<>();

    public synchronized ArrayList<String> getDimensions(){
        return dimensions;
    }

    public synchronized void setDimensions(String dimensions){
        this.dimensions = new ArrayList<>(Stream.of(dimensions.split(";")).toList());
    }

    public synchronized String toString(){
        return String.join(";", dimensions);
    }
}
