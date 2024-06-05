package com.ethan.common.url.component;

import com.ethan.common.extension.SPI;

import java.util.List;

@SPI
public interface DynamicParamSource {

    void init(List<String> keys, List<ParamValue> values);
    
}
