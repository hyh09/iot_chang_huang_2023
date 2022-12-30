package org.thingsboard.server.utils;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.stereotype.Service;
import org.thingsboard.server.excel.dto.ExportDto;

import javax.servlet.http.HttpServletResponse;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
public class ExcelUtil {

    /**
     * 导出excel
     *
     * @param dto
     * @param response
     * @throws Exception
     */
    public void downloadExcel(ExportDto dto, HttpServletResponse response) throws Exception {
        //标题
        String title = StringUtils.isBlank(dto.getTitle()) ? "导出数据" : dto.getTitle();
        //列数
        int cols = dto.getDataList().get(0).size();
        title = URLDecoder.decode(title, "UTF-8");
        HSSFWorkbook wb = this.getExcel(title, dto.getDataList(),dto.getPath(), cols);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String fileName = title + sdf.format(new Date()) + ".xls";
        this.responseExcel(wb, fileName, response);
    }

    public static HSSFWorkbook getExcel(String title, List<List<String>> list, String path, int colNums) throws IOException {
        HSSFWorkbook wb = new HSSFWorkbook();
        HSSFSheet sheet = wb.createSheet(title);
        HSSFRow row;
        HSSFCell cell;

        //标题
        HSSFCellStyle styleTitle = wb.createCellStyle();
        styleTitle.setAlignment(HorizontalAlignment.CENTER);
        HSSFFont fontTitle = wb.createFont();
        fontTitle.setFontHeightInPoints((short) 20);
        styleTitle.setFont(fontTitle);

        //表头
        HSSFCellStyle styleHead = wb.createCellStyle();
        HSSFFont fontHead = wb.createFont();
        fontHead.setFontHeightInPoints((short) 11);
        styleHead.setFont(fontHead);

        //表格
        HSSFCellStyle styleBody = wb.createCellStyle();
        HSSFFont fontBody = wb.createFont();
        fontBody.setFontHeightInPoints((short) 10);

        styleBody.setFont(fontBody);

        //尾注
        HSSFCellStyle styleFoot = wb.createCellStyle();
        HSSFFont fontFoot = wb.createFont();
        fontFoot.setFontHeightInPoints((short) 12);
        fontFoot.setColor(HSSFColor.HSSFColorPredefined.DARK_GREEN.getIndex());
        styleFoot.setFont(fontFoot);
        styleFoot.setFillForegroundColor(HSSFColor.HSSFColorPredefined.YELLOW.getIndex());

        //设置标题行
        row = sheet.createRow(0);
        cell = row.createCell(0);
        //行高
        row.setHeightInPoints((float) (10.75 * 3));
        //内容
        cell.setCellValue(title);
        //样式
        cell.setCellStyle(styleTitle);
        // 合并单元格 (始行，终行，始列，终列)
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, colNums));

        //设置表头
        row = sheet.createRow(1);
        //行高
        row.setHeightInPoints(15);
        //内容
        //1.表头
        List<String> fieldTitleList = list.get(0);
        fieldTitleList.add(0, "序号");
        for (int j = 0; j < fieldTitleList.size(); j++) {
            cell = row.createCell(j);
            cell.setCellValue(fieldTitleList.get(j));
            cell.setCellStyle(styleHead);
        }
        //2.设置表格内容
        for (int i = 2; i <= list.size(); i++) {
            //序号列
            row = sheet.createRow(i);
            cell = row.createCell(0);
            cell.setCellValue(i - 1);
            cell.setCellStyle(styleBody);

            //内容列
            List<String> dataTitleList = list.get(i - 1);
            for (int j = 1; j <= dataTitleList.size(); j++) {
                cell = row.createCell(j);
                cell.setCellValue(dataTitleList.get(j - 1));
                cell.setCellStyle(styleBody);
            }
        }

        //设置脚注
        int n = sheet.getLastRowNum();
        row = sheet.createRow(++n);
        row.setHeightInPoints((float) (12.75 * 2));
        cell = row.createCell(0);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        cell.setCellValue("数据生成时间：" + sdf.format(new Date()));
        cell.setCellStyle(styleFoot);
        sheet.addMergedRegion(new CellRangeAddress(n, n, 0, colNums));

        // 自动调整列宽
        for (int k = 0; k < colNums; k++) {
            sheet.autoSizeColumn((short) k, true);
        }
        //手动设置列宽
        //sheet.setColumnWidth(列号,宽度);

        if(StringUtils.isNotBlank(path)){
            FileOutputStream fileOut = new FileOutputStream(path+title+sdf.format(System.currentTimeMillis()) + ".xls");
            wb.write(fileOut);
            fileOut.close();
        }

        return wb;
    }

    //通过浏览器下载
    public void responseExcel(HSSFWorkbook wb, String fileName, HttpServletResponse response) {
        OutputStream out = null;
        try {
            out = response.getOutputStream();
            response.setContentType("application/x-msdownload");
            response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
            wb.write(out);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}

