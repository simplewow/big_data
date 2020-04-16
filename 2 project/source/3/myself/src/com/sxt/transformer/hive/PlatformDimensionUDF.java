package com.sxt.transformer.hive;


import java.io.IOException;
import java.util.List;

import org.apache.hadoop.hive.ql.exec.UDF;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;

import com.sxt.common.DateEnum;
import com.sxt.transformer.model.dim.base.DateDimension;
import com.sxt.transformer.model.dim.base.PlatformDimension;
import com.sxt.transformer.service.IDimensionConverter;
import com.sxt.transformer.service.impl.DimensionConverterImpl;
import com.sxt.util.TimeUtil;

/**
 * 
 * 
 * @author root
 *
 */
public class PlatformDimensionUDF extends UDF {
    private IDimensionConverter converter = new DimensionConverterImpl();

   

    /**
     
     * @param 
     * @return
     */
    public IntWritable evaluate(Text pl) {
        PlatformDimension dimension = new PlatformDimension(pl.toString());
        try {
            int id = this.converter.getDimensionIdByValue(dimension);
            return new IntWritable(id);
        } catch (IOException e) {
            throw new RuntimeException("获取id异常");
        }
    }
}
