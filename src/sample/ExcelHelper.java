package sample;


import jxl.Cell;
import jxl.Hyperlink;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Author: wujian
 * Time: 2018/03/29
 * Desc:
 */
public class ExcelHelper {
    private String filePath;
    private ArrayList<String[]> file1List;
    private ArrayList<String[]> file2List;

    ExcelHelper(String filePath) {
        this.filePath = filePath;
        file1List = new ArrayList<>();
        file2List = new ArrayList<>();
    }

    public ArrayList<String[]> readExcelType1() throws IOException, BiffException {
        //创建输入流
        InputStream stream = new FileInputStream(filePath);
        //获取Excel文件对象
        Workbook rwb = Workbook.getWorkbook(stream);
        //获取文件的指定工作表 默认的第一个
        Sheet sheet = rwb.getSheet(0);
        Hyperlink[] links = sheet.getHyperlinks();
        int k = 0;
        //行数(表头的目录不需要，从1开始)
        for (int i = 0; i < sheet.getRows(); i++) {
            //创建一个数组 用来存储每一列的值
            String[] str = new String[sheet.getColumns()];
            Cell cell = null;
            //列数
            for (int j = 0; j < sheet.getColumns(); j++) {
                //获取第i行，第j列的值
                cell = sheet.getCell(j, i);
                str[j] = cell.getContents();
                if (str[j].equals("图")) {
                    str[j] = links[k].getURL().toString().split("jpg")[0] + "jpg";
                    k++;
                }
            }
            //把刚获取的列存入list
            file1List.add(str);
        }
        return file1List;
    }

    public ArrayList<String[]> readExcelType2() throws IOException, BiffException {
        InputStream stream = new FileInputStream(filePath);
        Workbook rwb = Workbook.getWorkbook(stream);
        Sheet sheet = rwb.getSheet(0);
        for (int i = 0; i < sheet.getRows(); i++) {
            ArrayList<String> columnList = new ArrayList<>();
            Cell cell = null;
            for (int j = 0; j < sheet.getColumns(); j++) {
                cell = sheet.getCell(j, i);
                String cellString = cell.getContents();

                if (cellString.startsWith("http")) {
                    String[] urlList = cellString.split(";|；");
                    Collections.addAll(columnList, urlList);
                } else {
                    columnList.add(cellString);
                }
            }
            String[] columnStringList = new String[columnList.size()];
            for(int k =0; k< columnList.size(); k++) {
                columnStringList[k] = columnList.get(k);
            }
            file2List.add(columnStringList);
        }

        return file2List;
    }

}
