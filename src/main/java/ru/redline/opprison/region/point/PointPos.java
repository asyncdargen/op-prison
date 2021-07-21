package ru.redline.opprison.region.point;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class PointPos {

    private int x, y, z;

}
