package com.dimensionblocker;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ConfigData {
    private List<String> dimensions = new ArrayList<>();

    public synchronized List<String> getDimensions(){
        return dimensions;
    }

    public synchronized void setDimensions(String dimensions){
        this.dimensions = Stream.of(dimensions.split(";")).toList();
    }

    public synchronized String toString(){
        return String.join(";", dimensions);
    }
}
