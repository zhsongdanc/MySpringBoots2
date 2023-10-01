package org.example.test;

import com.aspose.cells.*;

/*
 * @Author: demussong
 * @Description:
 * @Date: 2023/9/26 15:58
 */
public class SimpleReadTest {

    public static void main(String[] args) throws Exception {
        Workbook workbook = new Workbook("input.xlsx");
        Worksheet worksheet = workbook.getWorksheets().get(0);

        Cells cells = worksheet.getCells();
        int rowCount = cells.getMaxDataRow() + 1;
        int columnCount = cells.getMaxDataColumn() + 1;

        for (int row = 0; row < rowCount; row++) {
            for (int column = 0; column < columnCount; column++) {
                Cell cell = cells.get(row, column);
//                CellValueType.IS_BOOL
                if (cell.getType() == CellValueType.IS_NUMERIC) {
                    String formula = cell.getFormula();
                    System.out.println("Row: " + (row + 1) + ", Column: " + (column + 1) + ", Formula: " + formula);
                }
            }
        }

        workbook.dispose();
    }
}

