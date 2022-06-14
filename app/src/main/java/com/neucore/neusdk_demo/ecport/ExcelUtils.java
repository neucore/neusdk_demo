package com.neucore.neusdk_demo.ecport;

import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import jxl.CellView;
import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

/**
 * Excel工具类 基于jxl.jar
 *
 * @author baiduo
 */
public class ExcelUtils {
    private static String TAG = "ExcelUtils";
    private static final int MIN_WIDTH = 9;
    private static final int CELL_INTERVAL = 1;
    private static volatile ExcelUtils instance;
    private WritableWorkbook writableWorkbook;
    private WritableSheet workbookSheet;

    private ExcelUtils() {
    }

    public static ExcelUtils getInstance() {
        if (instance == null) {
            synchronized (ExcelUtils.class) {
                if (instance == null) {
                    instance = new ExcelUtils();
                }
            }
        }
        return instance;
    }

    /**
     * 创建工作表
     *
     * @param filePath
     * @param filePath
     * @return
     */
    public ExcelUtils create(String filePath, String name) {
        //检测保存路劲是否存在，不存在则新建
        checkFilePathIsExist(filePath);
        File file = new File(filePath, name + ".xls");
        try {
            if(!file.exists())file.createNewFile();
            writableWorkbook = Workbook.createWorkbook(file);
        } catch (IOException e) {
            Log.e(TAG,"create",e);
        }

        return this;
    }

    public ExcelUtils createSheetFillData(String sheetName, String[] titles, List<Object> dataList, WritableCellFormat format) throws WriteException {
        checkWorkbookIsNull();
        workbookSheet = writableWorkbook.createSheet(sheetName, 0);
        Label label = null;
        //循环写入title
        for (int x = 0; x < titles.length; x++) {
            //设置单元格宽度为自动适配
            CellView cellView = new CellView();
            cellView.setAutosize(true);
            //设置第一行的列名
            label = new Label(x, 0, titles[x]);
            workbookSheet.setColumnView(x, cellView);
            workbookSheet.addCell(label);
        }
        fillData(dataList, format);

        return this;
    }

    /**
     * 只设置第一行的title
     *
     * @param sheetName
     * @param titles
     * @param format
     * @return
     * @throws WriteException
     */
    public ExcelUtils createSheetSetTitle(String sheetName, String[] titles, WritableCellFormat format) throws WriteException {
        checkWorkbookIsNull();

        workbookSheet = writableWorkbook.createSheet(sheetName, 0);
        Label label;
        //循环写入title
        for (int x = 0; x < titles.length; x++) {
            //设置单元格宽度为自动适配
            CellView cellView = new CellView();
            cellView.setSize(1000);
            //设置第一行的列名
            if (format == null) {
                label = new Label(x, 0, titles[x]);
            } else {
                label = new Label(x, 0, titles[x], format);
            }
            workbookSheet.setColumnView(x, getStringLength(titles[x]) + CELL_INTERVAL);
            workbookSheet.addCell(label);
        }


        return this;
    }

    /**
     * 填充字符串数据
     *
     * @param dataList
     * @param format
     */
    public void fillStringData(List<List<String>> dataList, WritableCellFormat format) {
        checkSheetIsNull();
        List<String> datas = null;
        Label label = null;
        for (int x = 1; x <= dataList.size(); x++) {

            //利用反射获取list中对象的属性值
            datas = dataList.get(x - 1);
            for (int y = 0; y < datas.size(); y++) {
                try {
                    if (format == null) {
                        label = new Label(y, x, datas.get(y));
                    } else {
                        label = new Label(y, x, datas.get(y), format);
                    }
                    workbookSheet.setColumnView(x, getStringLength(datas.get(y)) + CELL_INTERVAL);
                    workbookSheet.addCell(label);

                } catch (WriteException e) {
                    Log.e(TAG,"fillStringData",e);
                }
            }
        }
    }

    /**
     * 通过反射填充数据
     *
     * @param dataList
     * @param format
     */
    public ExcelUtils fillData(List<Object> dataList, WritableCellFormat format) {
        checkSheetIsNull();

        Label label = null;
        Object data = null;
        Class<?> aClass = null;
        String fieldName = null;
        String getMethodName = null;
        Method getMethod = null;
        Object value = null;
        Field field = null;
        //x=1是为了不覆盖title
        for (int x = 1; x <= dataList.size(); x++) {

            //利用反射获取list中对象的属性值
            data = dataList.get(x - 1);
            aClass = data.getClass();
            //获取字段数组
            Field[] declaredFields = aClass.getDeclaredFields();
            for (int y = 0; y < declaredFields.length; y++) {
                field = declaredFields[y];
                fieldName = field.getName();
                //获取getter方法名称
                getMethodName = "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);

                try {
                    getMethod = aClass.getMethod(getMethodName, new Class[]{});
                    value = getMethod.invoke(data, new Object[]{});
                    if (format == null) {
                        label = new Label(y, x, String.valueOf(value));
                    } else {
                        label = new Label(y, x, String.valueOf(value), format);
                    }
                    int width = getStringLength(String.valueOf(value));
                    workbookSheet.setColumnView(y, (width > MIN_WIDTH ? width : MIN_WIDTH));
                    workbookSheet.addCell(label);
                } catch (Exception e) {
                    Log.e(TAG,"fillData",e);
                }
            }
        }
        return this;
    }

    /**
     * 根据字符串返回字符数,解决中文显示不全的问题
     * @param str
     * @return
     */
    private int getStringLength(String str) {
        int count = 0;
        char[] c = str.toCharArray();
        for (int i = 0; i < c.length; i++) {
            String len = Integer.toBinaryString(c[i]);
            if (len.length() > 8) {
                count+=2;
            }else {
                count++;
            }
        }
        return count;
    }

    /**
     * 写入数据
     */
    public void close() {
        checkWorkbookIsNull();
        try {
            writableWorkbook.write();
            writableWorkbook.close();
        } catch (Exception e) {
            Log.e(TAG,"close",e);
        }
    }

    private void checkWorkbookIsNull() {
        if (writableWorkbook == null) {
            throw new NullPointerException("writableWorkbook is null");
        }
    }

    private void checkSheetIsNull() {
        if (workbookSheet == null) {
            throw new NullPointerException("workbookSheet is null");
        }
    }

    /**
     * 如果路径不存在就创建
     *
     * @param filePath
     */
    private void checkFilePathIsExist(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            file.mkdirs();
        }
    }
}
