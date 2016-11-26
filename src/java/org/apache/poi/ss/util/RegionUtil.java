/* ====================================================================
   Licensed to the Apache Software Foundation (ASF) under one or more
   contributor license agreements.  See the NOTICE file distributed with
   this work for additional information regarding copyright ownership.
   The ASF licenses this file to You under the Apache License, Version 2.0
   (the "License"); you may not use this file except in compliance with
   the License.  You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
==================================================================== */

package org.apache.poi.ss.util;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

/**
 * Various utility functions that make working with a region of cells easier.
 */
public final class RegionUtil {

    private RegionUtil() {
        // no instances of this class
    }

    /**
     * For setting the same property on many cells to the same value
     */
    private static final class CellPropertySetter {

        private final String _propertyName;
        private final Short _propertyValue;


        public CellPropertySetter(String propertyName, int value) {
            _propertyName = propertyName;
            _propertyValue = Short.valueOf((short) value);
        }


        public void setProperty(Row row, int column) {
            Cell cell = CellUtil.getCell(row, column);
            CellUtil.setCellStyleProperty(cell, _propertyName, _propertyValue);
        }
    }

    /**
     * Sets the left border for a region of cells by manipulating the cell style of the individual
     * cells on the left
     * 
     * @param border The new border
     * @param region The region that should have the border
     * @param workbook The workbook that the region is on.
     * @param sheet The sheet that the region is on.
     * @deprecated 3.15 beta 2. Use {@link #setBorderLeft(int, CellRangeAddress, Sheet)}.
     */
    public static void setBorderLeft(int border, CellRangeAddress region, Sheet sheet, Workbook workbook) {
        setBorderLeft(border, region, sheet);
    }
    /**
     * Sets the left border for a region of cells by manipulating the cell style of the individual
     * cells on the left
     * 
     * @param border The new border
     * @param region The region that should have the border
     * @param sheet The sheet that the region is on.
     */
    public static void setBorderLeft(int border, CellRangeAddress region, Sheet sheet) {
        int rowStart = region.getFirstRow();
        int rowEnd = region.getLastRow();
        int column = region.getFirstColumn();

        CellPropertySetter cps = new CellPropertySetter(CellUtil.BORDER_LEFT, border);
        for (int i = rowStart; i <= rowEnd; i++) {
            cps.setProperty(CellUtil.getRow(i, sheet), column);
        }
    }

    /**
     * Sets the left border color for a region of cells by manipulating the cell style of the individual
     * cells on the left
     * 
     * @param color The color of the border
     * @param region The region that should have the border
     * @param workbook The workbook that the region is on.
     * @param sheet The sheet that the region is on.
     * @deprecated 3.15 beta 2. Use {@link #setLeftBorderColor(int, CellRangeAddress, Sheet)}.
     */
    public static void setLeftBorderColor(int color, CellRangeAddress region, Sheet sheet, Workbook workbook) {
        setLeftBorderColor(color, region, sheet);
    }
    /**
     * Sets the left border color for a region of cells by manipulating the cell style of the individual
     * cells on the left
     * 
     * @param color The color of the border
     * @param region The region that should have the border
     * @param sheet The sheet that the region is on.
     */
    public static void setLeftBorderColor(int color, CellRangeAddress region, Sheet sheet) {
        int rowStart = region.getFirstRow();
        int rowEnd = region.getLastRow();
        int column = region.getFirstColumn();

        CellPropertySetter cps = new CellPropertySetter(CellUtil.LEFT_BORDER_COLOR, color);
        for (int i = rowStart; i <= rowEnd; i++) {
            cps.setProperty(CellUtil.getRow(i, sheet), column);
        }
    }

    /**
     * Sets the right border for a region of cells by manipulating the cell style of the individual
     * cells on the right
     * 
     * @param border The new border
     * @param region The region that should have the border
     * @param workbook The workbook that the region is on.
     * @param sheet The sheet that the region is on.
     * @deprecated 3.15 beta 2. Use {@link #setBorderRight(int, CellRangeAddress, Sheet)}.
     */
    public static void setBorderRight(int border, CellRangeAddress region, Sheet sheet, Workbook workbook) {
        setBorderRight(border, region, sheet);
    }
    /**
     * Sets the right border for a region of cells by manipulating the cell style of the individual
     * cells on the right
     * 
     * @param border The new border
     * @param region The region that should have the border
     * @param sheet The sheet that the region is on.
     */
    public static void setBorderRight(int border, CellRangeAddress region, Sheet sheet) {
        int rowStart = region.getFirstRow();
        int rowEnd = region.getLastRow();
        int column = region.getLastColumn();

        CellPropertySetter cps = new CellPropertySetter(CellUtil.BORDER_RIGHT, border);
        for (int i = rowStart; i <= rowEnd; i++) {
            cps.setProperty(CellUtil.getRow(i, sheet), column);
        }
    }

    /**
     * Sets the right border color for a region of cells by manipulating the cell style of the individual
     * cells on the right
     * 
     * @param color The color of the border
     * @param region The region that should have the border
     * @param workbook The workbook that the region is on.
     * @param sheet The sheet that the region is on.
     * @deprecated 3.15 beta 2. Use {@link #setRightBorderColor(int, CellRangeAddress, Sheet)}.
     */
    public static void setRightBorderColor(int color, CellRangeAddress region, Sheet sheet, Workbook workbook) {
        setRightBorderColor(color, region, sheet);
    }
    /**
     * Sets the right border color for a region of cells by manipulating the cell style of the individual
     * cells on the right
     * 
     * @param color The color of the border
     * @param region The region that should have the border
     * @param sheet The sheet that the region is on.
     */
    public static void setRightBorderColor(int color, CellRangeAddress region, Sheet sheet) {
        int rowStart = region.getFirstRow();
        int rowEnd = region.getLastRow();
        int column = region.getLastColumn();

        CellPropertySetter cps = new CellPropertySetter(CellUtil.RIGHT_BORDER_COLOR, color);
        for (int i = rowStart; i <= rowEnd; i++) {
            cps.setProperty(CellUtil.getRow(i, sheet), column);
        }
    }

    /**
     * Sets the bottom border for a region of cells by manipulating the cell style of the individual
     * cells on the bottom
     * 
     * @param border The new border
     * @param region The region that should have the border
     * @param workbook The workbook that the region is on.
     * @param sheet The sheet that the region is on.
     * @deprecated 3.15 beta 2. Use {@link #setBorderBottom(int, CellRangeAddress, Sheet)}.
     */
    public static void setBorderBottom(int border, CellRangeAddress region, Sheet sheet, Workbook workbook) {
        setBorderBottom(border, region, sheet);
    }
    /**
     * Sets the bottom border for a region of cells by manipulating the cell style of the individual
     * cells on the bottom
     * 
     * @param border The new border
     * @param region The region that should have the border
     * @param sheet The sheet that the region is on.
     */
    public static void setBorderBottom(int border, CellRangeAddress region, Sheet sheet) {
        int colStart = region.getFirstColumn();
        int colEnd = region.getLastColumn();
        int rowIndex = region.getLastRow();
        CellPropertySetter cps = new CellPropertySetter(CellUtil.BORDER_BOTTOM, border);
        Row row = CellUtil.getRow(rowIndex, sheet);
        for (int i = colStart; i <= colEnd; i++) {
            cps.setProperty(row, i);
        }
    }

    /**
     * Sets the bottom border color for a region of cells by manipulating the cell style of the individual
     * cells on the bottom
     * 
     * @param color The color of the border
     * @param region The region that should have the border
     * @param workbook The workbook that the region is on.
     * @param sheet The sheet that the region is on.
     * @deprecated 3.15 beta 2. Use {@link #setBottomBorderColor(int, CellRangeAddress, Sheet)}.
     */
    public static void setBottomBorderColor(int color, CellRangeAddress region, Sheet sheet, Workbook workbook) {
        setBottomBorderColor(color, region, sheet);
    }
    /**
     * Sets the bottom border color for a region of cells by manipulating the cell style of the individual
     * cells on the bottom
     * 
     * @param color The color of the border
     * @param region The region that should have the border
     * @param sheet The sheet that the region is on.
     */
    public static void setBottomBorderColor(int color, CellRangeAddress region, Sheet sheet) {
        int colStart = region.getFirstColumn();
        int colEnd = region.getLastColumn();
        int rowIndex = region.getLastRow();
        CellPropertySetter cps = new CellPropertySetter(CellUtil.BOTTOM_BORDER_COLOR, color);
        Row row = CellUtil.getRow(rowIndex, sheet);
        for (int i = colStart; i <= colEnd; i++) {
            cps.setProperty(row, i);
        }
    }

    /**
     * Sets the top border for a region of cells by manipulating the cell style of the individual
     * cells on the top
     * 
     * @param border The new border
     * @param region The region that should have the border
     * @param workbook The workbook that the region is on.
     * @param sheet The sheet that the region is on.
     * @deprecated 3.15 beta 2. Use {@link #setBorderTop(int, CellRangeAddress, Sheet)}.
     */
    public static void setBorderTop(int border, CellRangeAddress region, Sheet sheet, Workbook workbook) {
        setBorderTop(border, region, sheet);
    }
    /**
     * Sets the top border for a region of cells by manipulating the cell style of the individual
     * cells on the top
     * 
     * @param border The new border
     * @param region The region that should have the border
     * @param sheet The sheet that the region is on.
     */
    public static void setBorderTop(int border, CellRangeAddress region, Sheet sheet) {
        int colStart = region.getFirstColumn();
        int colEnd = region.getLastColumn();
        int rowIndex = region.getFirstRow();
        CellPropertySetter cps = new CellPropertySetter(CellUtil.BORDER_TOP, border);
        Row row = CellUtil.getRow(rowIndex, sheet);
        for (int i = colStart; i <= colEnd; i++) {
            cps.setProperty(row, i);
        }
    }

    /**
     * Sets the top border color for a region of cells by manipulating the cell style of the individual
     * cells on the top
     * 
     * @param color The color of the border
     * @param region The region that should have the border
     * @param workbook The workbook that the region is on.
     * @param sheet The sheet that the region is on.
     * @deprecated 3.15 beta 2. Use {@link #setTopBorderColor(int, CellRangeAddress, Sheet)}.
     */
    public static void setTopBorderColor(int color, CellRangeAddress region, Sheet sheet, Workbook workbook) {
        setTopBorderColor(color, region, sheet);
    }
    /**
     * Sets the top border color for a region of cells by manipulating the cell style of the individual
     * cells on the top
     * 
     * @param color The color of the border
     * @param region The region that should have the border
     * @param sheet The sheet that the region is on.
     */
    public static void setTopBorderColor(int color, CellRangeAddress region, Sheet sheet) {
        int colStart = region.getFirstColumn();
        int colEnd = region.getLastColumn();
        int rowIndex = region.getFirstRow();
        CellPropertySetter cps = new CellPropertySetter(CellUtil.TOP_BORDER_COLOR, color);
        Row row = CellUtil.getRow(rowIndex, sheet);
        for (int i = colStart; i <= colEnd; i++) {
            cps.setProperty(row, i);
        }
    }
}
