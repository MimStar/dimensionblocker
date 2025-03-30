package com.dimensionblocker;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ConfigData {
    private ArrayList<String> dimensions;

    public ConfigData(){
        dimensions = new ArrayList<>();
    }

    public ConfigData(String dimensions){
        setDimensions(dimensions);
    }

    public synchronized ArrayList<String> getDimensions(){
        return dimensions;
    }

    public synchronized void setDimensions(String dimensions){
        this.dimensions = new ArrayList<>(Stream.of(dimensions.split(";")).toList());
    }

    public synchronized String toString(){
        return String.join(";", dimensions);
    }

    public static final Codec<ConfigData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("dimensions").forGetter(ConfigData::toString)
    ).apply(instance, ConfigData::new));
}
