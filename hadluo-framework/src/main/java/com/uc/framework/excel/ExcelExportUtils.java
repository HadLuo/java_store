package com.uc.framework.excel;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFDataFormatter;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFDataFormat;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.util.CollectionUtils;

import com.alibaba.excel.EasyExcelFactory;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.metadata.BaseRowModel;
import com.alibaba.excel.metadata.Sheet;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.alibaba.excel.util.StringUtils;

/**
 * 
 * 导出excel工具
 * 
 * @author 罗政
 * @since JDK1.7
 * @history 2017年2月24日 新建
 */
public abstract class ExcelExportUtils {
    private final static String excel2003L = ".xls"; // 2003- 版本的excel
    private final static String excel2007U = ".xlsx"; // 2007+ 版本的excel

    public static class SheetData {

        Sheet sheet;
        /**
         * 二选择一 sheet List<Object> 内容
         */
        List<List<Object>> data;
        /**
         * 二选择一 sheet Model 内容
         */
        List<? extends BaseRowModel> modeldata;
        /**
         * 合并单元格位置集合
         */
        List<Merge> merge;

        public SheetData() {
            super();
        }

        public SheetData(Sheet sheet, List<List<Object>> data, List<Merge> merge) {
            super();
            this.sheet = sheet;
            this.data = data;
            this.merge = merge;
        }

        public SheetData(Sheet sheet, List<List<Object>> data, List<? extends BaseRowModel> modeldata,
                List<Merge> merge) {
            super();
            this.sheet = sheet;
            this.data = data;
            this.modeldata = modeldata;
            this.merge = merge;
        }

        public List<List<Object>> getData() {
            return data;
        }

        public void setData(List<List<Object>> data) {
            this.data = data;
        }

        public Sheet getSheet() {
            return sheet;
        }

        public void setSheet(Sheet sheet) {
            this.sheet = sheet;
        }

        public List<Merge> getMerge() {
            return merge;
        }

        public void setMerge(List<Merge> merge) {
            this.merge = merge;
        }

        public List<? extends BaseRowModel> getModeldata() {
            return modeldata;
        }

        public void setModeldata(List<? extends BaseRowModel> modeldata) {
            this.modeldata = modeldata;
        }

    }

    public static class Merge {
        /** 开始行 默认0 */
        int firstRow;
        /** 结束行 默认0 */
        int lastRow;
        /** 开始列 默认0 */
        int firstCol;
        /** 结束列 默认0 */
        int lastCol;

        public Merge(int firstRow, int lastRow, int firstCol, int lastCol) {
            super();
            this.firstRow = firstRow;
            this.lastRow = lastRow;
            this.firstCol = firstCol;
            this.lastCol = lastCol;
        }

    }

    /***
     * 合并单元格信息
     * 
     * @author HadLuo
     * @since JDK1.7
     * @history 2018年2月6日 新建
     */
    public static class AssemblyRowEntry {
        List<Integer> horizontalFrom = new ArrayList<Integer>();
        List<Integer> horizontalTo = new ArrayList<Integer>();

        // int verticalFrom, verticalTo;

        /***
         * 设置 要合并 的 横向 的 第 from 到 to 个单元格 [from从0开始 ]
         * 
         * @param from
         * @param to
         * @author HadLuo 2018年2月7日 新建
         */
        public AssemblyRowEntry setHorizontal(int from, int to) {
            horizontalFrom.add(from);
            horizontalTo.add(to);
            return this;
        }
        // /***
        // * 设置 要合并 的 纵向 的 第 from 到 to 个单元格 [from从0开始 ]
        // *
        // * @param from
        // * @param to
        // * @author HadLuo 2018年2月7日 新建
        // */
        // public void setVertical(int from, int to) {
        // verticalFrom = from;
        // verticalTo = to;
        // }
    }

    /***
     * 将 生产 的额excel 存入本地文件
     * 
     * @param savePath
     * @param fileName
     * @param sheetName
     * @param title
     * @param body
     */
    public static void exportLocalFile(String savePath, String fileName, String sheetName, String[] title,
            String[][] body, AssemblyRowEntry assemblyRowEntry) {
        if (null == fileName || null == sheetName || null == title || null == body) {
            return;
        }
        if (title.length == 0 || body.length == 0) {
            return;
        }
        List<SheetData> sheetDataList = new ArrayList<>();
        Sheet sheet = new Sheet(1, 1);
        sheet.setSheetName(sheetName);
        sheet.setHead(ExcelExportUtils.createListStringHead(title));
        // 设置 合并单元格 属性
        List<Merge> mergeList = new ArrayList<>();
        if (assemblyRowEntry != null
                && assemblyRowEntry.horizontalFrom.size() == assemblyRowEntry.horizontalTo.size()) {
            for (int i = 0; i < assemblyRowEntry.horizontalFrom.size(); i++) {
                mergeList.add(new Merge(0, 0, assemblyRowEntry.horizontalFrom.get(i),
                        assemblyRowEntry.horizontalTo.get(i)));
            }
        }
        sheetDataList.add(new SheetData(sheet, ExcelExportUtils.createListObjectByArray(body), mergeList));
        // 写入文件 默认2007版本
        if (!fileName.endsWith(excel2007U) && !fileName.endsWith(excel2003L)) {
            fileName += excel2007U;
        }
        ExcelTypeEnum excelType = fileName.endsWith(excel2003L) ? ExcelTypeEnum.XLS : ExcelTypeEnum.XLSX;
        FileOutputStream fos = null;
        String abPath;
        if (savePath == null) {
            abPath = fileName;
        } else {
            abPath = savePath + File.separator + fileName;
        }
        try {
            File file = new File(abPath);
            if (file.getParentFile() != null && !file.getParentFile().exists()) {
                file.mkdirs();
            }
            if (!file.exists()) {
                file.createNewFile();
            } else {
                file.delete();
            }
            fos = new FileOutputStream(file);
            writeExcelByEasyExcel(fos, sheetDataList, excelType);
            // 将excel写入流
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != fos) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void exportHttpResponse(String fileName, String sheetName, String[] title,
            List<String[]> body, HttpServletResponse response, AssemblyRowEntry assemblyRowEntry) {
        if (null == fileName || null == sheetName || null == title || null == body) {
            return;
        }
        if (body.size() > 0) {
            String[][] arrays = new String[body.size()][];
            for (int i = 0; i < body.size(); i++) {
                arrays[i] = body.get(i);
            }
            exportHttpResponse(fileName, sheetName, title, arrays, response, null, assemblyRowEntry);
        }
    }

    /***
     * 生成并导出excel 到 httpresponse
     * 
     * @param fileName 文件名
     * @param sheetName excel 的 sheet 名称
     * @param title 标题 数组 ， 必须与body对齐
     * @param body 内容
     * @param response httpresponse
     */
    public static void exportHttpResponse(String fileName, String sheetName, String[] title, String[][] body,
            HttpServletResponse response, File saveFile, AssemblyRowEntry assemblyRowEntry) {
        if (null == fileName || null == sheetName || null == title || null == body) {
            return;
        }
        if (title.length == 0 || body.length == 0) {
            return;
        }
        // sheetName 不允许有空格
        if (sheetName.contains(" ")) {
            sheetName = sheetName.replaceAll(" ", "");
        }
        List<SheetData> sheetDataList = new ArrayList<>();
        Sheet sheet = new Sheet(1, 1);
        sheet.setSheetName(sheetName);
        sheet.setHead(createListStringHead(title));
        // 设置 合并单元格 属性
        List<Merge> mergeList = new ArrayList<>();
        if (assemblyRowEntry != null
                && assemblyRowEntry.horizontalFrom.size() == assemblyRowEntry.horizontalTo.size()) {
            for (int i = 0; i < assemblyRowEntry.horizontalFrom.size(); i++) {
                mergeList.add(new Merge(0, 0, assemblyRowEntry.horizontalFrom.get(i),
                        assemblyRowEntry.horizontalTo.get(i)));
            }
        }
        sheetDataList.add(new SheetData(sheet, createListObjectByArray(body), mergeList));

        // 生成excel文件 并 回送到 response
        ByteArrayOutputStream out = null;
        try {
            out = new ByteArrayOutputStream();
            // 将excel写入流
            if (!fileName.endsWith(".xlsx")) {
                fileName += ".xlsx";
                writeExcelByEasyExcel(out, sheetDataList, ExcelTypeEnum.XLSX);
            } else {
                writeExcelByEasyExcel(out, sheetDataList, ExcelTypeEnum.XLS);
            }

            /** 回写http客户端 */
            if (null != response) {
                response.setContentType("application/vnd.ms-excel;charset=utf-8");
                fileName = response.encodeURL(new String(fileName.getBytes("utf-8"), "iso8859-1"));
                response.addHeader("Content-Disposition", "attachment;filename=" + fileName);
                response.setContentLength(out.size());
                ServletOutputStream outputstream = response.getOutputStream();
                out.writeTo(outputstream);
                outputstream.flush();
            }

            /** 写入本地文件 */
            if (saveFile != null) {
                if (!saveFile.getParentFile().exists()) {
                    saveFile.getParentFile().mkdirs();
                }
                if (!saveFile.exists()) {
                    saveFile.createNewFile();
                }
                FileOutputStream fos = new FileOutputStream(saveFile);
                out.writeTo(fos);
                fos.flush();
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != out) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 转化表头数据
     * 
     * @param title
     * @return
     * @author 卢家伟 2019年2月18日 新建
     */
    public static List<List<String>> createListStringHead(String[] title) {
        List<List<String>> date = new ArrayList<List<String>>();
        if (title == null) {
            return date;
        }
        for (String head : title) {
            List<String> headList = new ArrayList<String>();
            headList.add(head);
            date.add(headList);
        }
        return date;
    }

    /**
     * 转化数据
     * 
     * @param body
     * @return
     * @author 卢家伟 2019年2月18日 新建
     */
    public static List<List<Object>> createListObjectByArray(String[][] body) {
        List<List<Object>> date = new ArrayList<>();
        if (body == null) {
            return date;
        }
        // 填充 body内容
        for (String[] bodyRow : body) { // 遍历行
            List<Object> rowExcel = new ArrayList<>();
            for (int column = 0; column < bodyRow.length; column++) { // 遍历列
                rowExcel.add(bodyRow[column]);
            }
            date.add(rowExcel);
        }
        return date;
    }

    public static List<List<Object>> createListObjectByArray(List<String[]> saveExcel) {
        List<List<Object>> date = new ArrayList<>();
        if (saveExcel == null) {
            return date;
        }
        // 填充 body内容
        for (String[] bodyRow : saveExcel) { // 遍历行
            List<Object> rowExcel = new ArrayList<>();
            for (int column = 0; column < bodyRow.length; column++) { // 遍历列
                rowExcel.add(bodyRow[column]);
            }
            date.add(rowExcel);
        }
        return date;
    }

    /***
     * 
     * @param in
     * @param fileName
     * @return 返回 excel的数据结构
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    public static <T> List<List<T>> parserExcel(InputStream in, String fileName) throws Exception {
        if (null == in || null == fileName) {
            throw new NullPointerException();
        }
        List<List<T>> datas = new ArrayList<>();
        // 创建Excel工作薄

        Workbook work = getWorkbook(in, fileName);
        if (null == work) {
            throw new Exception("创建Excel工作薄为空！");
        }
        org.apache.poi.ss.usermodel.Sheet sheet = null;
        Row row = null;
        try {
            for (int s = 0; s < work.getNumberOfSheets(); s++) {// 遍历Excel中所有的sheet
                sheet = work.getSheetAt(s);
                for (int i = 0; i <= sheet.getLastRowNum(); i++) { // 遍历 行
                    if (i == 0)
                        continue;
                    row = sheet.getRow(i);
                    if (row != null) {
                        int lastCellNum = row.getLastCellNum();
                        Cell cell = null;
                        List<T> li = new ArrayList<T>();
                        for (int j = 0; j <= lastCellNum; j++) { // 遍历列
                            cell = row.getCell(j);
                            li.add((T) getCellValue(cell));
                        }
                        if (!isNull(li)) {
                            datas.add(li);
                        }
                    }
                }
            }
        } catch (Exception e) {
            throw e;
        }
        return datas;
    }

    private static <T> boolean isNull(List<T> li) {
        for (T t : li) {
            if (t != null) {
                return false;
            }
        }
        return true;
    }

    /**
     * 描述：对表格中数值进行格式化
     * 
     * @param cell
     * @return
     */
    private static Object getCellValue(Cell cell) {
        if (cell == null) {
            return null;
        }
        switch (cell.getCellType()) {
        case HSSFCell.CELL_TYPE_STRING:
            return cell.getStringCellValue();
        case HSSFCell.CELL_TYPE_FORMULA:
            return cell.getCellFormula();
        case HSSFCell.CELL_TYPE_NUMERIC:
            HSSFDataFormatter dataFormatter = new HSSFDataFormatter();
            return dataFormatter.formatCellValue(cell);
        case HSSFCell.CELL_TYPE_ERROR:
            return null;
        }
        return null;
    }

    /**
     * 描述：根据文件后缀，自适应上传文件的版本
     * 
     * @param inStr ,fileName
     * @return
     * @throws Exception
     */
    private static Workbook getWorkbook(InputStream inStr, String fileName) throws Exception {
        Workbook wb = null;
        try {
            String fileType = fileName.substring(fileName.lastIndexOf("."));
            if (excel2003L.equals(fileType)) {
                wb = new HSSFWorkbook(inStr); // 2003-
            } else if (excel2007U.equals(fileType)) {
                wb = new XSSFWorkbook(inStr); // 2007+
            } else {
                throw new Exception("解析的文件格式有误！");
            }
        } catch (Exception e) {
            throw e;
        }
        return wb;
    }

    /**
     * 将导出表格写到输出流
     * 
     * @param out
     * @param sheetDataList
     * @param excelType
     * @throws IOException
     * @author 卢家伟 2019年2月18日 新建
     */
    public static void writeExcelByEasyExcel(OutputStream out, List<SheetData> sheetDataList,
            ExcelTypeEnum excelType) throws IOException {
        ExcelWriter writer = EasyExcelFactory.getWriter(out, excelType, true);
        for (SheetData sheetData : sheetDataList) {
            Sheet sheet = sheetData.getSheet();
            writer.write1(sheetData.getData(), sheet);

            List<Merge> mergeList = sheetData.getMerge();
            if (CollectionUtils.isEmpty(mergeList)) {
                continue;
            }
            for (Merge merge : mergeList) {
                writer.merge(merge.firstRow, merge.lastRow, merge.firstCol, merge.lastCol);
            }
        }
        writer.finish();
        out.close();
    }

    /**
     * 创建 excel
     * 
     * @param sheetName
     * @param title
     * @param body
     * @return
     */
    public static XSSFWorkbook createExcel(String sheetName, String[] title, String[][] body,
            AssemblyRowEntry assemblyRowEntry) {
        // 1.创建一个Excel文件
        XSSFWorkbook workbook = new XSSFWorkbook();
        // 2.获取并设置默认样式 左距中
        XSSFCellStyle defaultStyle = workbook.createCellStyle();
        defaultStyle.setAlignment(HorizontalAlignment.LEFT);

        // 3、创建sheet为‘选品上架商品’的HSSFSheet，并填充数据
        XSSFSheet sheet = workbook.createSheet(sheetName);
        // 4. 设置标题样式
        XSSFCellStyle baseTitleStyle = builderTitleStyle(workbook);

        int rowPosition = 0; // 行号
        XSSFRow rowExcel = sheet.createRow(rowPosition++);
        XSSFCell nCell = null;
        // 填充标题 内容
        for (int i = 0; i < title.length; i++) {
            sheet.autoSizeColumn(i, true);// 自动设宽
            nCell = rowExcel.createCell(i);
            nCell.setCellStyle(baseTitleStyle);
            nCell.setCellValue(title[i]);
        }

        // 设置 合并单元格 属性
        if (assemblyRowEntry != null
                && assemblyRowEntry.horizontalFrom.size() == assemblyRowEntry.horizontalTo.size()) {
            for (int e = 0; e < assemblyRowEntry.horizontalFrom.size(); e++) {
                sheet.addMergedRegion(new CellRangeAddress(0, 0, assemblyRowEntry.horizontalFrom.get(e),
                        assemblyRowEntry.horizontalTo.get(e)));
            }
        }

        // 填充 body内容
        for (String[] bodyRow : body) { // 遍历行
            rowExcel = sheet.createRow(rowPosition++);
            for (int column = 0; column < bodyRow.length; column++) { // 遍历列
                setCellValue(rowExcel, workbook, defaultStyle, column, bodyRow[column]);
            }
        }
        return workbook;
    }

    /**
     * 标题样式设置
     * 
     * @param workbook
     */
    public static XSSFCellStyle builderTitleStyle(XSSFWorkbook workbook) {
        // 设置标题样式
        XSSFCellStyle baseTitleStyle = workbook.createCellStyle();
        baseTitleStyle.setAlignment(HorizontalAlignment.LEFT); // 左对齐
        Font font = workbook.createFont();
        font.setFontHeightInPoints((short) 13);
        // font.setBoldweight(Font.BOLDWEIGHT_BOLD); // 加粗
        baseTitleStyle.setFont(font);
        return baseTitleStyle;
    }

    /**
     * 设置String类型cell值
     * 
     * @param nRow 行对象
     * @param wb 工作book对象
     * @param colNo 列号
     * @param value String类型的值
     * @author 杨永亚 2016年12月15日 新建
     */
    private static void setCellValue(XSSFRow nRow, XSSFWorkbook wb, XSSFCellStyle defaultStyle, int colNo,
            String value) {
        if (StringUtils.isEmpty(value) || "null".equals(value)) {
            value = "";
        }
        XSSFCell nCell = nRow.createCell(colNo);
        // 新增的四句话，设置CELL格式为文本格式
        XSSFCellStyle cellStyle2 = wb.createCellStyle();
        XSSFDataFormat format = wb.createDataFormat();
        cellStyle2.setDataFormat(format.getFormat("@"));
        nCell.setCellStyle(cellStyle2);

        nCell.setCellType(HSSFCell.CELL_TYPE_STRING);
        nCell.setCellStyle(defaultStyle);
        nCell.setCellValue(value);
    }
}
