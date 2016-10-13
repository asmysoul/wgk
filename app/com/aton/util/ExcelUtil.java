package com.aton.util;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Workbook;

import vos.OrderExpress;

import com.aton.config.Config;
import com.google.common.collect.Maps;

import net.sf.jxls.exception.ParsePropertyException;
import net.sf.jxls.reader.ReaderBuilder;
import net.sf.jxls.reader.XLSReader;
import net.sf.jxls.transformer.XLSTransformer;

public class ExcelUtil {

    /**
     * Parses an excel file into a list of beans.
     * 
     * @param <T> the type of the bean
     * @param xlsFile the excel data file to parse
     * @param jxlsConfigFile the jxls config file describing how to map rows to beans
     * @return the list of beans or an empty list there are none
     * @throws Exception if there is a problem parsing the file
     */
    public static <T> List<T> parseExcelFileToBeans(File xlsFile,  File jxlsConfigFile) throws Exception {
        final XLSReader xlsReader = ReaderBuilder.buildFromXML(jxlsConfigFile);
        final List<T> result = new ArrayList();
        final Map<String, Object> beans = new HashMap();
        beans.put("results", result);
        InputStream inputStream = new BufferedInputStream(new FileInputStream(xlsFile));
        xlsReader.read(inputStream, beans);
        inputStream.close();
        return result;
    }
    
    /**
     * 
     * 按照模板配置构造Excle\csv格式的数据流.
     *
     * @param templateXls
     * @param data
     * @return
     * @throws IOException
     * @throws ParsePropertyException
     * @throws InvalidFormatException
     * @since  v1.4
     * @author youblade
     * @created 2014年11月21日 上午9:23:08
     */
    public static <T> ByteArrayInputStream buildExportFile(File templateXls, List<T> data){
        try {
            //excel模板加载
            InputStream is = new BufferedInputStream(new FileInputStream(templateXls));
            
            //将数据按照excel模板格式写入Workbook
            Map<String, List<T>> beanParams = Maps.newHashMap();  
            beanParams.put("items", data);  
            Workbook workbook = new XLSTransformer().transformXLS(is, beanParams);
            
            //Workbook写入inputStream
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            ByteArrayInputStream bais = new ByteArrayInputStream(out.toByteArray());
            
            return bais;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

}
