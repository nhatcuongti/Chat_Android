package com.example.meza.utilities;

/**
 * Created by reiko-lhnhat on 4/2/2022.
 */
public interface Packable {
    ByteBuf marshal(ByteBuf out);
}