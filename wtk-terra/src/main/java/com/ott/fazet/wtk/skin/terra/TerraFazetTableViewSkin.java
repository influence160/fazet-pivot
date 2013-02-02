package com.ott.fazet.wtk.skin.terra;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Transparency;

import org.apache.pivot.collections.ArrayList;
import org.apache.pivot.collections.Dictionary;
import org.apache.pivot.collections.List;
import org.apache.pivot.collections.Sequence;
import org.apache.pivot.util.Filter;
import org.apache.pivot.wtk.Bounds;
import org.apache.pivot.wtk.Component;
import org.apache.pivot.wtk.Dimensions;
import org.apache.pivot.wtk.GraphicsUtilities;
import org.apache.pivot.wtk.Keyboard;
import org.apache.pivot.wtk.Mouse;
import org.apache.pivot.wtk.Orientation;
import org.apache.pivot.wtk.Platform;
import org.apache.pivot.wtk.Point;
import org.apache.pivot.wtk.SortDirection;
import org.apache.pivot.wtk.Span;
import org.apache.pivot.wtk.Theme;
import org.apache.pivot.wtk.Keyboard.KeyCode;
import org.apache.pivot.wtk.Keyboard.Modifier;
import org.apache.pivot.wtk.skin.ComponentSkin;
import org.apache.pivot.wtk.skin.terra.TerraTheme;

import com.ott.fazet.wtk.FazetTableView;
import com.ott.fazet.wtk.FazetTableViewSelectionListener;
import com.ott.fazet.wtk.TableView;
import com.ott.fazet.wtk.TableViewColumnListener;
import com.ott.fazet.wtk.TableViewListener;
import com.ott.fazet.wtk.TableViewRowListener;
import com.ott.fazet.wtk.TableViewSelectionListener;
import com.ott.fazet.wtk.TableView.CellRenderer;
import com.ott.fazet.wtk.TableView.Column;
import com.ott.fazet.wtk.TableView.ColumnSequence;
import com.ott.fazet.wtk.TableView.HeaderDataRenderer;
import com.ott.fazet.wtk.TableView.RowEditor;
import com.ott.fazet.wtk.TableView.SelectMode;
import com.ott.fazet.wtk.TableView.Skin;

public class TerraFazetTableViewSkin extends ComponentSkin implements TableView.Skin,
	TableViewListener, TableViewColumnListener, TableViewRowListener,
	FazetTableViewSelectionListener {
	
	private Font font;
	private Color color;
	private Color disabledColor;
	private Color backgroundColor;
	private Color selectionColor;
	private Color selectionBackgroundColor;
	private Color inactiveSelectionColor;
	private Color inactiveSelectionBackgroundColor;
	private Color highlightBackgroundColor;
	private Color alternateRowBackgroundColor;
	private Color columnSelectionColor;
	private Color columnSelectionHorizontalGridColor;
	private Color horizontalGridColor;
	private Color verticalGridColor;
	private boolean showHighlight;
	private boolean showHorizontalGridLines;
	private boolean showVerticalGridLines;
	private boolean includeTrailingVerticalGridLine;
	private boolean includeTrailingHorizontalGridLine;
	private boolean variableRowHeight;
	
	private ArrayList<Integer> columnWidths = null;
	private ArrayList<Integer> rowBoundaries = null;
	private int fixedRowHeight = -1;
	private int defaultWidthColumnCount = 0;
	
	private int highlightIndex = -1;
	private int selectIndex = -1;
	
	private Point highlightPoint = null;
	
	private boolean validateSelection = false;
	
	public TerraFazetTableViewSkin() {
	    TerraTheme theme = (TerraTheme)Theme.getTheme();
	    font = theme.getFont();
	    color = theme.getColor(1);
	    disabledColor = theme.getColor(7);
	    backgroundColor = theme.getColor(4);
	    selectionColor = theme.getColor(4);
	    selectionBackgroundColor = theme.getColor(14);
	    inactiveSelectionColor = theme.getColor(1);
	    inactiveSelectionBackgroundColor = theme.getColor(9);
	    highlightBackgroundColor = theme.getColor(10);
	    alternateRowBackgroundColor = theme.getColor(11);
	    columnSelectionColor = null;
	    columnSelectionHorizontalGridColor = null;
	    horizontalGridColor = theme.getColor(11);
	    verticalGridColor = theme.getColor(11);
	
	    showHighlight = true;
	    showHorizontalGridLines = true;
	    showVerticalGridLines = true;
	    includeTrailingVerticalGridLine = false;
	    includeTrailingHorizontalGridLine = false;
	}
	
	@Override
	public void install(Component component) {
	    super.install(component);
	
	    TableView tableView = (TableView)component;
	    tableView.getTableViewListeners().add(this);
	    tableView.getTableViewColumnListeners().add(this);
	    tableView.getTableViewRowListeners().add(this);
	    tableView.getTableViewSelectionListeners().add(this);
	}
	
	@Override
	public int getPreferredWidth(int height) {
	    return getPreferredWidth((TableView)getComponent(), includeTrailingVerticalGridLine);
	}
	
	public static int getPreferredWidth(TableView tableView, boolean includeTrailingVerticalGridLine) {
	    int preferredWidth = 0;
	
	    TableView.ColumnSequence columns = tableView.getColumns();
	    List<?> tableData = tableView.getTableData();
	
	    int n = columns.getLength();
	    for (int i = 0; i < n; i++) {
	        TableView.Column column = columns.get(i);
	
	        if (!column.isRelative()) {
	            int columnWidth = column.getWidth();
	
	            if (columnWidth == -1) {
	                // Calculate the maximum cell width
	                columnWidth = 0;
	
	                TableView.CellRenderer cellRenderer = column.getCellRenderer();
	
	                int rowIndex = 0;
	                for (Object rowData : tableData) {
	                    cellRenderer.render(rowData, rowIndex++, i, tableView, column.getName(),
	                        false, false, false);
	                    columnWidth = Math.max(cellRenderer.getPreferredWidth(-1), columnWidth);
	                }
	            }
	
	            preferredWidth += Math.min(Math.max(columnWidth, column.getMinimumWidth()), column.getMaximumWidth());
	        }
	        else {
	            preferredWidth += column.getMinimumWidth();
	        }
	    }
	
	    // Include space for vertical gridlines; even if we are not painting them,
	    // the header does
	    preferredWidth += (n - 1);
	
	    if (includeTrailingVerticalGridLine) {
	        preferredWidth++;
	    }
	
	    return preferredWidth;
	}
	
	@Override
	public int getPreferredHeight(int width) {
	    int preferredHeight = 0;
	
	    TableView tableView = (TableView)getComponent();
	
	    int n = tableView.getTableData().getLength();
	
	    if (variableRowHeight) {
	        ArrayList<Integer> columnWidthsLocal = getColumnWidths(tableView, width);
	
	        for (int i = 0; i < n; i++) {
	            preferredHeight += getVariableRowHeight(i, columnWidthsLocal);
	        }
	    } else {
	        int fixedRowHeightLocal = calculateFixedRowHeight(tableView);
	        preferredHeight = fixedRowHeightLocal * n;
	    }
	
	    // Include space for horizontal grid lines
	    preferredHeight += (n - 1);
	
	    if (includeTrailingHorizontalGridLine) {
	        preferredHeight++;
	    }
	
	    return preferredHeight;
	}
	
	@Override
	public Dimensions getPreferredSize() {
	    return new Dimensions(getPreferredWidth(-1), getPreferredHeight(-1));
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public int getBaseline(int width, int height) {
	    TableView tableView = (TableView)getComponent();
	    List<Object> tableData = (List<Object>)tableView.getTableData();
	
	    int baseline = -1;
	
	    TableView.ColumnSequence columns = tableView.getColumns();
	    ArrayList<Integer> columnWidthsLocal = getColumnWidths(tableView, width);
	
	    if (variableRowHeight) {
	        int rowHeight = getVariableRowHeight(0, columnWidthsLocal);
	        Object rowData = tableData.get(0);
	
	        for (int i = 0, n = columns.getLength(); i < n; i++) {
	            TableView.Column column = columns.get(i);
	            TableView.CellRenderer cellRenderer = column.getCellRenderer();
	            cellRenderer.render(rowData, 0, i, tableView, column.getName(), false, false, false);
	            baseline = Math.max(baseline, cellRenderer.getBaseline(columnWidthsLocal.get(i), rowHeight));
	        }
	
	    } else {
	        int rowHeight = calculateFixedRowHeight(tableView);
	
	        for (int i = 0, n = columns.getLength(); i < n; i++) {
	            TableView.Column column = columns.get(i);
	            TableView.CellRenderer cellRenderer = column.getCellRenderer();
	            cellRenderer.render(null, -1, i, tableView, column.getName(), false, false, false);
	            baseline = Math.max(baseline, cellRenderer.getBaseline(columnWidthsLocal.get(i), rowHeight));
	        }
	    }
	
	    return baseline;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public void layout() {
	    columnWidths = getColumnWidths((TableView)getComponent(), getWidth());
	
	    TableView tableView = (TableView)getComponent();
	    TableView.ColumnSequence columns = tableView.getColumns();
	
	    if (variableRowHeight) {
	        List<Object> tableData = (List<Object>)tableView.getTableData();
	
	        int n = tableData.getLength();
	        rowBoundaries = new ArrayList<Integer>(n);
	
	        int rowY = 0;
	        for (int i = 0; i < n; i++) {
	            Object rowData = tableData.get(i);
	
	            int rowHeight = 0;
	            for (int columnIndex = 0, columnCount = columns.getLength();
	                columnIndex < columnCount; columnIndex++) {
	                TableView.Column column = columns.get(columnIndex);
	
	                TableView.CellRenderer cellRenderer = column.getCellRenderer();
	
	                int columnWidth = columnWidths.get(columnIndex);
	
	                cellRenderer.render(rowData, i, columnIndex, tableView, column.getName(),
	                    false, false, false);
	                rowHeight = Math.max(rowHeight, cellRenderer.getPreferredHeight(columnWidth));
	            }
	
	            rowY += rowHeight;
	            rowBoundaries.add(rowY);
	            rowY++;
	        }
	    } else {
	        fixedRowHeight = calculateFixedRowHeight(tableView);
	    }
	
	    if (validateSelection) {
	        // Ensure that the selection is visible
	        Sequence<Span> selectedRanges = tableView.getSelectedRanges();
	
	        if (selectedRanges.getLength() > 0) {
	            int rangeStart = selectedRanges.get(0).start;
	            int rangeEnd = selectedRanges.get(selectedRanges.getLength() - 1).end;
	
	            Bounds selectionBounds = getRowBounds(rangeStart);
	            selectionBounds = selectionBounds.union(getRowBounds(rangeEnd));
	
	            Bounds visibleSelectionBounds = tableView.getVisibleArea(selectionBounds);
	            if (visibleSelectionBounds != null
	                && visibleSelectionBounds.height < selectionBounds.height) {
	                tableView.scrollAreaToVisible(selectionBounds);
	            }
	        }
	    }
	
	    validateSelection = false;
	}
	
	/**
	 * Calculates the table row height, which is determined as the maximum
	 * preferred height of all cell renderers.
	 */
	private static int calculateFixedRowHeight(TableView tableView) {
	    int fixedRowHeight = 0;
	    TableView.ColumnSequence columns = tableView.getColumns();
	
	    for (int i = 0, n = columns.getLength(); i < n; i++) {
	        TableView.Column column = columns.get(i);
	        TableView.CellRenderer cellRenderer = column.getCellRenderer();
	        cellRenderer.render(null, -1, i, tableView, column.getName(), false, false, false);
	
	        fixedRowHeight = Math.max(fixedRowHeight, cellRenderer.getPreferredHeight(-1));
	    }
	
	    return fixedRowHeight;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public void paint(Graphics2D graphics) {
		FazetTableView tableView = (FazetTableView)getComponent();
	    List<Object> tableData = (List<Object>)tableView.getTableData();
	    TableView.ColumnSequence columns = tableView.getColumns();
	
	    int width = getWidth();
	    int height = getHeight();
	
	    // Paint the background
	    if (backgroundColor != null) {
	        graphics.setPaint(backgroundColor);
	        graphics.fillRect(0, 0, width, height);
	    }
	
	    // Ensure that we only paint items that are visible
	    int rowStart = 0;
	    int rowEnd = tableData.getLength() - 1;
	
	    Rectangle clipBounds = graphics.getClipBounds();
	    if (clipBounds != null) {
	        if (variableRowHeight) {
	            rowStart = getRowAt(clipBounds.y);
	            if (rowStart == -1) {
	                rowStart = tableData.getLength();
	            }
	
	            if (rowEnd != -1) {
	                int clipBottom = clipBounds.y + clipBounds.height - 1;
	                clipBottom = Math.min(clipBottom, rowBoundaries.get(rowEnd) - 1);
	                rowEnd = getRowAt(clipBottom);
	            }
	        } else {
	            rowStart = Math.max(rowStart, (int)Math.floor(clipBounds.y
	                / (double)(fixedRowHeight + 1)));
	            rowEnd = Math.min(rowEnd, (int)Math.ceil((clipBounds.y
	                + clipBounds.height) / (double)(fixedRowHeight + 1)) - 1);
	        }
	    }

	    // Paint the row background
	    if (alternateRowBackgroundColor != null) {
	        for (int rowIndex = rowStart; rowIndex <= rowEnd; rowIndex++) {
	            int rowY = getRowY(rowIndex);
	            int rowHeight = getRowHeight(rowIndex);
	            if (rowIndex % 2 > 0) {
	                graphics.setPaint(alternateRowBackgroundColor);
	                graphics.fillRect(0, rowY, width, rowHeight + 1);
	            }
	        }
	    }
	
	    // Paint the column backgrounds
	    int columnX = 0;
	    if (columnSelectionColor != null) {
	        graphics.setColor(columnSelectionColor);
	
	        columnX = 0;
	
	        for (int columnIndex = 0, columnCount = columns.getLength();
	            columnIndex < columnCount; columnIndex++) {
	            TableView.Column column = columns.get(columnIndex);
	            int columnWidth = columnWidths.get(columnIndex);
	
	            String columnName = column.getName();
	            SortDirection sortDirection = tableView.getSort().get(columnName);
	            if (sortDirection != null) {
	                graphics.fillRect(columnX, 0, columnWidth, height);
	            }
	
	            columnX += columnWidth + 1;
	        }
	    }
	
	    // Paint the row content
	    for (int rowIndex = rowStart; rowIndex <= rowEnd; rowIndex++) {
	        SelectMode selectMode = tableView.getSelectMode();
	        Object rowData = tableData.get(rowIndex);
	        int rowY = getRowY(rowIndex);
	        int rowHeight = getRowHeight(rowIndex);
	        boolean cellHighlighted = false,
	        		cellSelected = false, 
	        		cellDisabled = false;
	        
	        if(selectMode == SelectMode.NONE ||
	        		selectMode == SelectMode.SINGLE ||
	        		selectMode == SelectMode.MULTI) {
		        boolean rowHighlighted = (rowIndex == highlightIndex
			            && tableView.getSelectMode() != TableView.SelectMode.NONE);
		        boolean rowSelected = tableView.isRowSelected(rowIndex);
		        boolean rowDisabled = tableView.isRowDisabled(rowIndex);
		        cellHighlighted = rowHighlighted;
		        cellSelected = rowSelected;
		        cellDisabled = rowDisabled;
		
		        // Paint selection state
		        Color rowBackgroundColor = null;
		        if (rowSelected) {
		            rowBackgroundColor = (tableView.isFocused())
		                ? this.selectionBackgroundColor : inactiveSelectionBackgroundColor;
		        } else {
		            if (rowHighlighted && showHighlight && !rowDisabled) {
		                rowBackgroundColor = highlightBackgroundColor;
		            }
		        }
		        
		        if (rowBackgroundColor != null) {
		            graphics.setPaint(rowBackgroundColor);
		            graphics.fillRect(0, rowY, width, rowHeight);
		        }
	        }
	
	        // Paint the cells
	        columnX = 0;
	
	        for (int columnIndex = 0, columnCount = columns.getLength();
                columnIndex < columnCount; columnIndex++) {
                TableView.Column column = columns.get(columnIndex);
                TableView.CellRenderer cellRenderer = column.getCellRenderer();
                int columnWidth = columnWidths.get(columnIndex);

                if(selectMode == SelectMode.MULTICELLS ||
                		selectMode == SelectMode.SINGLECELL) {
                	cellHighlighted = (highlightPoint != null && columnIndex == highlightPoint.x 
                			&& rowIndex == highlightPoint.y);
                	cellSelected = tableView.isCellSelected(columnIndex, rowIndex);
                	cellDisabled = tableView.isRowDisabled(rowIndex);//TODO cell disabletion
                	Color cellBackgroundColor = rowIndex % 2 == 0 ? backgroundColor : alternateRowBackgroundColor;
                	if (cellSelected) {
			        	cellBackgroundColor = (tableView.isFocused())
			                ? this.selectionBackgroundColor : inactiveSelectionBackgroundColor;
			        } else {
			            if (cellHighlighted && showHighlight && !cellDisabled) {
	                		cellBackgroundColor = highlightBackgroundColor;
			            }
			        }
			        
		        	if(cellBackgroundColor != null){
			            graphics.setPaint(cellBackgroundColor);
			            graphics.fillRect(columnX, rowY, columnWidth, rowHeight);
		        	}
                } else {
                	//TODO selectionMode = colum
                }
	        	
                Graphics2D rendererGraphics = (Graphics2D)graphics.create(columnX, rowY,
                    columnWidth, rowHeight);

                cellRenderer.render(rowData, rowIndex, columnIndex, tableView, column.getName(),
            		cellSelected, cellHighlighted, cellDisabled);
                cellRenderer.setSize(columnWidth, rowHeight);
                cellRenderer.paint(rendererGraphics);

                rendererGraphics.dispose();

                columnX += columnWidth + 1;
	        }
	    }
	
	    // Paint the vertical grid lines
	    graphics.setPaint(verticalGridColor);
	
	    if (showVerticalGridLines) {
	        columnX = 0;
	
	        for (int columnIndex = 0, columnCount = columns.getLength();
	            columnIndex < columnCount; columnIndex++) {
	            columnX += columnWidths.get(columnIndex);
	
	            if (columnIndex < columnCount - 1
	                || includeTrailingVerticalGridLine) {
	                GraphicsUtilities.drawLine(graphics, columnX, 0, height, Orientation.VERTICAL);
	            }
	
	            columnX++;
	        }
	    }
	
	    // Paint the horizontal grid lines
	    graphics.setPaint(horizontalGridColor);
	
	    if (showHorizontalGridLines) {
	        int rowCount = tableData.getLength();
	
	        for (int rowIndex = rowStart; rowIndex <= rowEnd; rowIndex++) {
	            int gridY = getRowY(rowIndex + 1) - 1;
	
	            if (rowIndex < rowCount - 1
	                || includeTrailingHorizontalGridLine) {
	                GraphicsUtilities.drawLine(graphics, 0, gridY, width, Orientation.HORIZONTAL);
	            }
	        }
	
	        if (columnSelectionHorizontalGridColor != null) {
	            graphics.setColor(columnSelectionHorizontalGridColor);
	
	            columnX = 0;
	
	            for (int columnIndex = 0, columnCount = columns.getLength();
	                columnIndex < columnCount; columnIndex++) {
	                TableView.Column column = columns.get(columnIndex);
	                int columnWidth = columnWidths.get(columnIndex);
	
	                String columnName = column.getName();
	                SortDirection sortDirection = tableView.getSort().get(columnName);
	                if (sortDirection != null) {
	                    for (int rowIndex = rowStart; rowIndex <= rowEnd; rowIndex++) {
	                        int gridY = getRowY(rowIndex + 1) - 1;
	
	                        if (rowIndex < rowCount - 1
	                            || includeTrailingHorizontalGridLine) {
	                            GraphicsUtilities.drawLine(graphics, columnX, gridY, columnWidth,
	                                Orientation.HORIZONTAL);
	                        }
	                    }
	                }
	
	                columnX += columnWidth + 1;
	            }
	        }
	    }
	}
	
	private int getRowY(int rowIndex) {
	    int rowY;
	    if (variableRowHeight) {
	        if (rowIndex == 0) {
	            rowY = 0;
	        } else {
	            rowY = rowBoundaries.get(rowIndex - 1);
	        }
	    } else {
	        rowY = rowIndex * (fixedRowHeight + 1);
	    }
	    return rowY;
	}
	
	private int getRowHeight(int rowIndex) {
	    int rowHeight;
	    if (variableRowHeight) {
	        rowHeight = rowBoundaries.get(rowIndex);
	
	        if (rowIndex > 0) {
	            rowHeight -= rowBoundaries.get(rowIndex - 1);
	        }
	    } else {
	        rowHeight = fixedRowHeight;
	    }
	
	    return rowHeight;
	}
	
	@SuppressWarnings("unchecked")
	protected int getVariableRowHeight(int rowIndex, ArrayList<Integer> columnWidthsArgument) {
	    TableView tableView = (TableView)getComponent();
	    List<Object> tableData = (List<Object>)tableView.getTableData();
	
	    TableView.ColumnSequence columns = tableView.getColumns();
	    Object rowData = tableData.get(rowIndex);
	
	    int rowHeight = 0;
	    for (int i = 0, n = columns.getLength(); i < n; i++) {
	        TableView.Column column = columns.get(i);
	        TableView.CellRenderer cellRenderer = column.getCellRenderer();
	        cellRenderer.render(rowData, rowIndex, i, tableView, column.getName(), false, false, false);
	
	        rowHeight = Math.max(rowHeight, cellRenderer.getPreferredHeight(columnWidthsArgument.get(i)));
	    }
	
	    return rowHeight;
	}
	
	// Table view skin methods
	@Override
	@SuppressWarnings("unchecked")
	public int getRowAt(int y) {
	    if (y < 0) {
	        throw new IllegalArgumentException("y is negative");
	    }
	
	    TableView tableView = (TableView)getComponent();
	
	    int rowIndex;
	    if (variableRowHeight) {
	        if (y == 0) {
	            rowIndex = 0;
	        } else {
	            rowIndex = ArrayList.binarySearch(rowBoundaries, y);
	            if (rowIndex < 0) {
	                rowIndex = -(rowIndex + 1);
	            }
	        }
	    } else {
	        rowIndex = (y / (fixedRowHeight + 1));
	
	        List<Object> tableData = (List<Object>)tableView.getTableData();
	        if (rowIndex >= tableData.getLength()) {
	            rowIndex = -1;
	        }
	    }
	
	    return rowIndex;
	}
	
	@Override
	public int getColumnAt(int x) {
	    if (x < 0) {
	        throw new IllegalArgumentException("x is negative");
	    }
	
	    TableView tableView = (TableView)getComponent();
	
	    int columnIndex = -1;
	
	    int i = 0;
	    int n = tableView.getColumns().getLength();
	    int columnX = 0;
	    while (i < n
	        && x > columnX) {
	        columnX += (columnWidths.get(i) + 1);
	        i++;
	    }
	
	    if (x <= columnX) {
	        columnIndex = i - 1;
	    }
	
	    return columnIndex;
	}
	
	@Override
	public Bounds getRowBounds(int rowIndex) {
	    return new Bounds(0, getRowY(rowIndex), getWidth(), getRowHeight(rowIndex));
	}
	
	@Override
	public Bounds getColumnBounds(int columnIndex) {
	    int columnX = 0;
	    for (int i = 0; i < columnIndex; i++) {
	        columnX += (columnWidths.get(i) + 1);
	    }
	
	    return new Bounds(columnX, 0, columnWidths.get(columnIndex), getHeight());
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public Bounds getCellBounds(int rowIndex, int columnIndex) {
	    TableView tableView = (TableView)getComponent();
	    List<Object> tableData = (List<Object>)tableView.getTableData();
	
	    if (rowIndex < 0
	        || rowIndex >= tableData.getLength()) {
	        throw new IndexOutOfBoundsException();
	    }
	
	    int cellX = 0;
	    for (int i = 0; i < columnIndex; i++) {
	        cellX += (columnWidths.get(i) + 1);
	    }
	
	    int rowHeight = getRowHeight(rowIndex);
	
	    return new Bounds(cellX, rowIndex * (rowHeight + 1), columnWidths.get(columnIndex), rowHeight);
	}
	
	public static ArrayList<Integer> getColumnWidths(TableView tableView, int width) {
	    int fixedWidth = 0;
	    int relativeWidth = 0;
	
	    TableView.ColumnSequence columns = tableView.getColumns();
	    int n = columns.getLength();
	
	    ArrayList<Integer> columnWidths = new ArrayList<Integer>(n);
	
	    for (int i = 0; i < n; i++) {
	        TableView.Column column = columns.get(i);
	
	        if (column.isRelative()) {
	            columnWidths.add(0);
	            relativeWidth += column.getWidth();
	        } else {
	            int columnWidth = column.getWidth();
	
	            if (columnWidth == -1) {
	                // Calculate the maximum cell width
	                columnWidth = 0;
	
	                TableView.CellRenderer cellRenderer = column.getCellRenderer();
	                List<?> tableData = tableView.getTableData();
	
	                int rowIndex = 0;
	                for (Object rowData : tableData) {
	                    cellRenderer.render(rowData, rowIndex++, i, tableView, column.getName(),
	                        false, false, false);
	                    columnWidth = Math.max(cellRenderer.getPreferredWidth(-1), columnWidth);
	                }
	            }
	
	            columnWidth = Math.min(Math.max(columnWidth, column.getMinimumWidth()), column.getMaximumWidth());
	            columnWidths.add(columnWidth);
	            fixedWidth += columnWidth;
	        }
	    }
	
	    fixedWidth += n - 1;
	    int variableWidth = Math.max(width - fixedWidth, 0);
	
	    for (int i = 0; i < n; i++) {
	        TableView.Column column = columns.get(i);
	
	        if (column.isRelative()) {
	            int columnWidth = (int)Math.round((double)(column.getWidth()
	                * variableWidth) / (double)relativeWidth);
	            columnWidths.update(i, Math.min(Math.max(columnWidth, column.getMinimumWidth()),
	                column.getMaximumWidth()));
	        }
	    }
	
	    return columnWidths;
	}
	
	@Override
	public boolean isFocusable() {
	    TableView tableView = (TableView)getComponent();
	    return (tableView.getSelectMode() != TableView.SelectMode.NONE);
	}
	
	@Override
	public boolean isOpaque() {
	    return (backgroundColor != null
	        && backgroundColor.getTransparency() == Transparency.OPAQUE);
	}
	
	public Font getFont() {
	    return font;
	}
	
	public void setFont(Font font) {
	    if (font == null) {
	        throw new IllegalArgumentException("font is null.");
	    }
	
	    this.font = font;
	    invalidateComponent();
	}
	
	public final void setFont(String font) {
	    if (font == null) {
	        throw new IllegalArgumentException("font is null.");
	    }
	
	    setFont(decodeFont(font));
	}
	
	public final void setFont(Dictionary<String, ?> font) {
	    if (font == null) {
	        throw new IllegalArgumentException("font is null.");
	    }
	
	    setFont(Theme.deriveFont(font));
	}
	
	public Color getColor() {
	    return color;
	}
	
	public void setColor(Color color) {
	    if (color == null) {
	        throw new IllegalArgumentException("color is null.");
	    }
	
	    this.color = color;
	    repaintComponent();
	}
	
	public final void setColor(String color) {
	    if (color == null) {
	        throw new IllegalArgumentException("color is null.");
	    }
	
	    setColor(GraphicsUtilities.decodeColor(color));
	}
	
	public final void setColor(int color) {
	    TerraTheme theme = (TerraTheme)Theme.getTheme();
	    setColor(theme.getColor(color));
	}
	
	public Color getDisabledColor() {
	    return disabledColor;
	}
	
	public void setDisabledColor(Color disabledColor) {
	    if (disabledColor == null) {
	        throw new IllegalArgumentException("disabledColor is null.");
	    }
	
	    this.disabledColor = disabledColor;
	    repaintComponent();
	}
	
	public final void setDisabledColor(String disabledColor) {
	    if (disabledColor == null) {
	        throw new IllegalArgumentException("disabledColor is null.");
	    }
	
	    setDisabledColor(GraphicsUtilities.decodeColor(disabledColor));
	}
	
	public final void setDisabledColor(int disabledColor) {
	    TerraTheme theme = (TerraTheme)Theme.getTheme();
	    setDisabledColor(theme.getColor(disabledColor));
	}
	
	public Color getBackgroundColor() {
	    return backgroundColor;
	}
	
	public void setBackgroundColor(Color backgroundColor) {
	    this.backgroundColor = backgroundColor;
	    repaintComponent();
	}
	
	public final void setBackgroundColor(String backgroundColor) {
	    if (backgroundColor == null) {
	        throw new IllegalArgumentException("backgroundColor is null.");
	    }
	
	    setBackgroundColor(GraphicsUtilities.decodeColor(backgroundColor));
	}
	
	public final void setBackgroundColor(int backgroundColor) {
	    TerraTheme theme = (TerraTheme)Theme.getTheme();
	    setBackgroundColor(theme.getColor(backgroundColor));
	}
	
	public Color getSelectionColor() {
	    return selectionColor;
	}
	
	public void setSelectionColor(Color selectionColor) {
	    if (selectionColor == null) {
	        throw new IllegalArgumentException("selectionColor is null.");
	    }
	
	    this.selectionColor = selectionColor;
	    repaintComponent();
	}
	
	public final void setSelectionColor(String selectionColor) {
	    if (selectionColor == null) {
	        throw new IllegalArgumentException("selectionColor is null.");
	    }
	
	    setSelectionColor(GraphicsUtilities.decodeColor(selectionColor));
	}
	
	public final void setSelectionColor(int selectionColor) {
	    TerraTheme theme = (TerraTheme)Theme.getTheme();
	    setSelectionColor(theme.getColor(selectionColor));
	}
	
	public Color getSelectionBackgroundColor() {
	    return selectionBackgroundColor;
	}
	
	public void setSelectionBackgroundColor(Color selectionBackgroundColor) {
	    if (selectionBackgroundColor == null) {
	        throw new IllegalArgumentException("selectionBackgroundColor is null.");
	    }
	
	    this.selectionBackgroundColor = selectionBackgroundColor;
	    repaintComponent();
	}
	
	public final void setSelectionBackgroundColor(String selectionBackgroundColor) {
	    if (selectionBackgroundColor == null) {
	        throw new IllegalArgumentException("selectionBackgroundColor is null.");
	    }
	
	    setSelectionBackgroundColor(GraphicsUtilities.decodeColor(selectionBackgroundColor));
	}
	
	public final void setSelectionBackgroundColor(int selectionBackgroundColor) {
	    TerraTheme theme = (TerraTheme)Theme.getTheme();
	    setSelectionBackgroundColor(theme.getColor(selectionBackgroundColor));
	}
	
	public Color getInactiveSelectionColor() {
	    return inactiveSelectionColor;
	}
	
	public void setInactiveSelectionColor(Color inactiveSelectionColor) {
	    if (inactiveSelectionColor == null) {
	        throw new IllegalArgumentException("inactiveSelectionColor is null.");
	    }
	
	    this.inactiveSelectionColor = inactiveSelectionColor;
	    repaintComponent();
	}
	
	public final void setInactiveSelectionColor(String inactiveSelectionColor) {
	    if (inactiveSelectionColor == null) {
	        throw new IllegalArgumentException("inactiveSelectionColor is null.");
	    }
	
	    setInactiveSelectionColor(GraphicsUtilities.decodeColor(inactiveSelectionColor));
	}
	
	public final void setInactiveSelectionColor(int inactiveSelectionColor) {
	    TerraTheme theme = (TerraTheme)Theme.getTheme();
	    setInactiveSelectionColor(theme.getColor(inactiveSelectionColor));
	}
	
	public Color getInactiveSelectionBackgroundColor() {
	    return inactiveSelectionBackgroundColor;
	}
	
	public void setInactiveSelectionBackgroundColor(Color inactiveSelectionBackgroundColor) {
	    if (inactiveSelectionBackgroundColor == null) {
	        throw new IllegalArgumentException("inactiveSelectionBackgroundColor is null.");
	    }
	
	    this.inactiveSelectionBackgroundColor = inactiveSelectionBackgroundColor;
	    repaintComponent();
	}
	
	public final void setInactiveSelectionBackgroundColor(String inactiveSelectionBackgroundColor) {
	    if (inactiveSelectionBackgroundColor == null) {
	        throw new IllegalArgumentException("inactiveSelectionBackgroundColor is null.");
	    }
	
	    setInactiveSelectionBackgroundColor(GraphicsUtilities.decodeColor(inactiveSelectionBackgroundColor));
	}
	
	public final void setInactiveSelectionBackgroundColor(int inactiveSelectionBackgroundColor) {
	    TerraTheme theme = (TerraTheme)Theme.getTheme();
	    setInactiveSelectionBackgroundColor(theme.getColor(inactiveSelectionBackgroundColor));
	}
	
	public Color getHighlightBackgroundColor() {
	    return highlightBackgroundColor;
	}
	
	public void setHighlightBackgroundColor(Color highlightBackgroundColor) {
	    if (highlightBackgroundColor == null) {
	        throw new IllegalArgumentException("highlightBackgroundColor is null.");
	    }
	
	    this.highlightBackgroundColor = highlightBackgroundColor;
	    repaintComponent();
	}
	
	public final void setHighlightBackgroundColor(String highlightBackgroundColor) {
	    if (highlightBackgroundColor == null) {
	        throw new IllegalArgumentException("highlightBackgroundColor is null.");
	    }
	
	    setHighlightBackgroundColor(GraphicsUtilities.decodeColor(highlightBackgroundColor));
	}
	
	public final void setHighlightBackgroundColor(int highlightBackgroundColor) {
	    TerraTheme theme = (TerraTheme)Theme.getTheme();
	    setHighlightBackgroundColor(theme.getColor(highlightBackgroundColor));
	}
	
	public Color getAlternateRowBackgroundColor() {
	    return alternateRowBackgroundColor;
	}
	
	public void setAlternateRowBackgroundColor(Color alternateRowBackgroundColor) {
	    this.alternateRowBackgroundColor = alternateRowBackgroundColor;
	    repaintComponent();
	}
	
	public final void setAlternateRowBackgroundColor(String alternateRowBackgroundColor) {
	    if (alternateRowBackgroundColor == null) {
	        throw new IllegalArgumentException("alternateRowBackgroundColor is null.");
	    }
	
	    setAlternateRowBackgroundColor(GraphicsUtilities.decodeColor(alternateRowBackgroundColor));
	}
	
	public final void setAlternateRowBackgroundColor(int alternateRowBackgroundColor) {
	    TerraTheme theme = (TerraTheme)Theme.getTheme();
	    setAlternateRowBackgroundColor(theme.getColor(alternateRowBackgroundColor));
	}
	
	public Color getColumnSelectionColor() {
	    return columnSelectionColor;
	}
	
	public void setColumnSelectionColor(Color columnSelectionColor) {
	    this.columnSelectionColor = columnSelectionColor;
	    repaintComponent();
	}
	
	public final void setColumnSelectionColor(String columnSelectionColor) {
	    if (columnSelectionColor == null) {
	        throw new IllegalArgumentException("columnSelectionColor is null.");
	    }
	
	    setColumnSelectionColor(GraphicsUtilities.decodeColor(columnSelectionColor));
	}
	
	public final void setColumnSelectionColor(int columnSelectionColor) {
	    TerraTheme theme = (TerraTheme)Theme.getTheme();
	    setColumnSelectionColor(theme.getColor(columnSelectionColor));
	}
	
	public Color getColumnSelectionHorizontalGridColor() {
	    return columnSelectionHorizontalGridColor;
	}
	
	public void setColumnSelectionHorizontalGridColor(Color columnSelectionHorizontalGridColor) {
	    if (columnSelectionHorizontalGridColor == null) {
	        throw new IllegalArgumentException("columnSelectionHorizontalGridColor is null.");
	    }
	
	    this.columnSelectionHorizontalGridColor = columnSelectionHorizontalGridColor;
	    repaintComponent();
	}
	
	public final void setColumnSelectionHorizontalGridColor(String columnSelectionHorizontalGridColor) {
	    if (columnSelectionHorizontalGridColor == null) {
	        throw new IllegalArgumentException("columnSelectionHorizontalGridColor is null.");
	    }
	
	    setColumnSelectionHorizontalGridColor(GraphicsUtilities.decodeColor(columnSelectionHorizontalGridColor));
	}
	
	public final void setColumnSelectionHorizontalGridColor(int columnSelectionHorizontalGridColor) {
	    TerraTheme theme = (TerraTheme)Theme.getTheme();
	    setColumnSelectionHorizontalGridColor(theme.getColor(columnSelectionHorizontalGridColor));
	}
	
	public Color getHorizontalGridColor() {
	    return horizontalGridColor;
	}
	
	public void setHorizontalGridColor(Color horizontalGridColor) {
	    if (horizontalGridColor == null) {
	        throw new IllegalArgumentException("horizontalGridColor is null.");
	    }
	
	    this.horizontalGridColor = horizontalGridColor;
	    repaintComponent();
	}
	
	public final void setHorizontalGridColor(String horizontalGridColor) {
	    if (horizontalGridColor == null) {
	        throw new IllegalArgumentException("horizontalGridColor is null.");
	    }
	
	    setHorizontalGridColor(GraphicsUtilities.decodeColor(horizontalGridColor));
	}
	
	public final void setHorizontalGridColor(int horizontalGridColor) {
	    TerraTheme theme = (TerraTheme)Theme.getTheme();
	    setHorizontalGridColor(theme.getColor(horizontalGridColor));
	}
	
	public Color getVerticalGridColor() {
	    return verticalGridColor;
	}
	
	public void setVerticalGridColor(Color verticalGridColor) {
	    if (verticalGridColor == null) {
	        throw new IllegalArgumentException("verticalGridColor is null.");
	    }
	
	    this.verticalGridColor = verticalGridColor;
	    repaintComponent();
	}
	
	public final void setVerticalGridColor(String verticalGridColor) {
	    if (verticalGridColor == null) {
	        throw new IllegalArgumentException("verticalGridColor is null.");
	    }
	
	    setVerticalGridColor(GraphicsUtilities.decodeColor(verticalGridColor));
	}
	
	public final void setVerticalGridColor(int verticalGridColor) {
	    TerraTheme theme = (TerraTheme)Theme.getTheme();
	    setVerticalGridColor(theme.getColor(verticalGridColor));
	}
	
	public boolean getShowHighlight() {
	    return showHighlight;
	}
	
	public void setShowHighlight(boolean showHighlight) {
	    this.showHighlight = showHighlight;
	    repaintComponent();
	}
	
	public boolean getShowHorizontalGridLines() {
	    return showHorizontalGridLines;
	}
	
	public void setShowHorizontalGridLines(boolean showHorizontalGridLines) {
	    this.showHorizontalGridLines = showHorizontalGridLines;
	    repaintComponent();
	}
	
	public boolean getShowVerticalGridLines() {
	    return showVerticalGridLines;
	}
	
	public void setShowVerticalGridLines(boolean showVerticalGridLines) {
	    this.showVerticalGridLines = showVerticalGridLines;
	    repaintComponent();
	}
	
	public boolean getIncludeTrailingVerticalGridLine() {
	    return includeTrailingVerticalGridLine;
	}
	
	public void setIncludeTrailingVerticalGridLine(boolean includeTrailingVerticalGridLine) {
	    this.includeTrailingVerticalGridLine = includeTrailingVerticalGridLine;
	    invalidateComponent();
	}
	
	public boolean getIncludeTrailingHorizontalGridLine() {
	    return includeTrailingHorizontalGridLine;
	}
	
	public void setIncludeTrailingHorizontalGridLine(boolean includeTrailingHorizontalGridLine) {
	    this.includeTrailingHorizontalGridLine = includeTrailingHorizontalGridLine;
	    invalidateComponent();
	}
	
	public boolean isVariableRowHeight() {
	    return variableRowHeight;
	}
	
	public void setVariableRowHeight(boolean variableRowHeight) {
	    this.variableRowHeight = variableRowHeight;
	    this.rowBoundaries = null;
	    this.fixedRowHeight = -1;
	    invalidateComponent();
	}
	
	@Override
	public boolean mouseMove(Component component, int x, int y) {
	    boolean consumed = super.mouseMove(component, x, y);
	
	    TableView tableView = (TableView)getComponent();
	    SelectMode selectMode = tableView.getSelectMode();
	
	    if (selectMode != SelectMode.NONE && showHighlight) {
	    	if(selectMode == SelectMode.SINGLE || selectMode == SelectMode.MULTI) {
	    	    int previousHighlightIndex = this.highlightIndex;
	    	    highlightIndex = getRowAt(y);
	    	    
	    		if(previousHighlightIndex != highlightIndex) {
			        if (previousHighlightIndex != -1) {
			            repaintComponent(getRowBounds(previousHighlightIndex));
			        }
			
			        if (highlightIndex != -1) {
			            repaintComponent(getRowBounds(highlightIndex));
			        }
	    		}
	    	} else if (selectMode == SelectMode.SINGLECELL || selectMode == SelectMode.MULTICELLS) {
	    	    Point previousHighlightPoint = this.highlightPoint;
	    	    highlightPoint = new Point(getColumnAt(x), getRowAt(y));
	    	    
	    		if(previousHighlightPoint != null && !previousHighlightPoint.equals(highlightIndex)) {
		            repaintComponent(getCellBounds(previousHighlightPoint.y, previousHighlightPoint.x));
	    		}
		        if (highlightPoint != null && !highlightPoint.equals(previousHighlightPoint)) {
		            repaintComponent(getCellBounds(highlightPoint.y, highlightPoint.x));
		        }
	    	} else {
	    		//TODO selectMode = column repaint highlight column and previews highlight column
	    	}
	    }
	
	    return consumed;
	}
	
	@Override
	public void mouseOut(Component component) {
	    super.mouseOut(component);
	
	    TableView tableView = (TableView)getComponent();
	    SelectMode selectMode = tableView.getSelectMode();
	
	    if (selectMode != SelectMode.NONE
	        && showHighlight) {
	    	if (highlightIndex != -1 && (selectMode == SelectMode.SINGLE || 
	    			selectMode == SelectMode.MULTI)) {
	            repaintComponent(getRowBounds(highlightIndex));
	    	} else if (highlightPoint != null && (selectMode == SelectMode.SINGLECELL ||
    				selectMode == SelectMode.MULTICELLS)) {
	    		repaintComponent(getCellBounds(highlightPoint.y, highlightPoint.x));
	    	} else {
	    		//TODO selectMode = column repaint highlight column
	    	}
	    }
	
	    highlightIndex = -1;
	    highlightPoint = null;
	    selectIndex = -1;//TODO why ???
	}
	
	@Override
	public boolean mouseDown(Component component, Mouse.Button button, int x, int y) {
	    boolean consumed = super.mouseDown(component, button, x, y);
	
	    TableView tableView = (TableView)getComponent();
	    int rowIndex = getRowAt(y);
	    int columnIndex = getColumnAt(x);
        TableView.SelectMode selectMode = tableView.getSelectMode();
        
	    if(selectMode == TableView.SelectMode.NONE ||
	    		selectMode == TableView.SelectMode.SINGLE ||
	    		selectMode == TableView.SelectMode.MULTI ) {
		    if (rowIndex >= 0
		        && !tableView.isRowDisabled(rowIndex)) {
		
		        if (button == Mouse.Button.LEFT) {
		            Keyboard.Modifier commandModifier = Platform.getCommandModifier();
		
		            if (Keyboard.isPressed(Keyboard.Modifier.SHIFT)
		                && selectMode == TableView.SelectMode.MULTI) {
		                Filter<?> disabledRowFilter = tableView.getDisabledRowFilter();
		
		                if (disabledRowFilter == null) {
		                    // Select the range
		                    int startIndex = tableView.getFirstSelectedIndex();
		                    int endIndex = tableView.getLastSelectedIndex();
		                    // if there is nothing currently selected, selected the indicated row
		                    if (startIndex == -1) {
		                        tableView.addSelectedIndex(rowIndex);
		                    } else {
		                        // otherwise select the range of rows
		                        Span selectedRange = (rowIndex > startIndex) ?
		                            new Span(startIndex, rowIndex) : new Span(rowIndex, endIndex);
		
		                        ArrayList<Span> selectedRanges = new ArrayList<Span>();
		                        selectedRanges.add(selectedRange);
		
		                        tableView.setSelectedRanges(selectedRanges);
		                    }
		                }
		            } else if (Keyboard.isPressed(commandModifier)
		                && selectMode == TableView.SelectMode.MULTI) {
		                // Toggle the item's selection state
		                if (tableView.isRowSelected(rowIndex)) {
		                    tableView.removeSelectedIndex(rowIndex);
		                } else {
		                    tableView.addSelectedIndex(rowIndex);
		                }
		            } else if (Keyboard.isPressed(commandModifier)
		                && selectMode == TableView.SelectMode.SINGLE) {
		                // Toggle the item's selection state
		                if (tableView.isRowSelected(rowIndex)) {
		                    tableView.setSelectedIndex(-1);
		                } else {
		                    tableView.setSelectedIndex(rowIndex);
		                }
		            } else {
		                if (selectMode != TableView.SelectMode.NONE) {
		                    if (tableView.isRowSelected(rowIndex)) {
		                        selectIndex = rowIndex;
		                    } else {
		                        tableView.setSelectedIndex(rowIndex);
		                    }
		                }
		            }
		        }
		    }
	    }
	    else if (selectMode == TableView.SelectMode.SINGLECELL ||
	    		selectMode == TableView.SelectMode.MULTICELLS ) {

		    if (rowIndex >= 0 && columnIndex >= 0
		        && !tableView.isCellDisabled(columnIndex, rowIndex)) {
		
		        if (button == Mouse.Button.LEFT) {
		            Keyboard.Modifier commandModifier = Platform.getCommandModifier();
		
		            if (Keyboard.isPressed(Keyboard.Modifier.SHIFT)
		                && selectMode == TableView.SelectMode.MULTICELLS) {
		                Filter<?> disabledRowFilter = tableView.getDisabledRowFilter();
		
		                if (disabledRowFilter == null) {
		                    // Select the range
		                    Point startPoint = tableView.getFirstSelectedPoint();
		                    Point endPoint = tableView.getLastSelectedPoint();
		                    // if there is nothing currently selected, selected the indicated row
		                    if (endPoint == null) {
		                        tableView.addSelectedCell(columnIndex, rowIndex);
		                    } else {
		                        // otherwise select the range of rows
		                    	int xStart = endPoint.x;
		                    	int yStart = endPoint.y;
		                    	Span selectedPerimeterX = new Span(Math.min(xStart, columnIndex),
		                    			Math.max(xStart, columnIndex));
		                    	Span selectedPerimeterY = new Span(Math.min(yStart, rowIndex),
		                    			Math.max(yStart, rowIndex));
		                    	com.ott.fazet.wtk.Rectangle selectedPerimeter = new com.ott.fazet.wtk.Rectangle(selectedPerimeterX,
		                    			selectedPerimeterY);
		                        if (Keyboard.isPressed(commandModifier)) {
		                        	//if CTRL is pressed then add selectedPerimeter to the current selected perimeters
		                        	Sequence<com.ott.fazet.wtk.Rectangle> addedPerimeters = 
		                        			tableView.addSelectedPerimeter(selectedPerimeter.x, selectedPerimeter.y);
		                        	if (addedPerimeters == null || addedPerimeters.getLength() == 0) {
		                        		//if all the added perimeter is already selected thend do the inverse and deselect it
		                        		tableView.removeSelectedPerimeter(selectedPerimeter);
		                        	}
		                        } else {
		                        	//set selectedPerimeter as the current selected perimeters
			                        ArrayList<com.ott.fazet.wtk.Rectangle> selectedPerimeters = new ArrayList<com.ott.fazet.wtk.Rectangle>();
			                        selectedPerimeters.add(selectedPerimeter);
			                        tableView.setSelectedPerimeters(selectedPerimeters);
		                        }
		                    }
		                }
		            } else if (Keyboard.isPressed(commandModifier)
		                && selectMode == TableView.SelectMode.MULTICELLS) {
		                // Toggle the item's selection state
		                if (tableView.isCellSelected(columnIndex, rowIndex)) {
		                    tableView.removeSelectedCell(columnIndex, rowIndex);
		                } else {
		                    tableView.addSelectedCell(columnIndex, rowIndex);
		                }
		            } else if (Keyboard.isPressed(commandModifier)
		                && selectMode == TableView.SelectMode.SINGLECELL) {
		                // Toggle the item's selection state
		                if (tableView.isCellSelected(columnIndex, rowIndex)) {
		                    tableView.setSelectedCell(-1, -1);
		                } else {
		                    tableView.setSelectedCell(columnIndex, rowIndex);
		                }
		            } else {
	                    if (tableView.isCellSelected(columnIndex, rowIndex)) {
	                        selectIndex = rowIndex;
	                    } else {
	                        tableView.setSelectedCell(columnIndex, rowIndex);
	                    }
		            }
		        }
		    }
	    }
	
	    tableView.requestFocus();
	
	    return consumed;
	}
	
	@Override
	public boolean mouseUp(Component component, Mouse.Button button, int x, int y) {
	    boolean consumed = super.mouseUp(component, button, x, y);
	
	    TableView tableView = (TableView)getComponent();
	    if (selectIndex != -1
	        && tableView.getFirstSelectedIndex() != tableView.getLastSelectedIndex()) {
	        tableView.setSelectedIndex(selectIndex);
	        selectIndex = -1;
	    }
	
	    return consumed;
	}
	
	@Override
	public boolean mouseClick(Component component, Mouse.Button button, int x, int y, int count) {
	    boolean consumed = super.mouseClick(component, button, x, y, count);
	
	    TableView tableView = (TableView)getComponent();
	    if (selectIndex != -1
	        && count == 2
	        && button == Mouse.Button.LEFT) {
	        TableView.RowEditor rowEditor = tableView.getRowEditor();
	
	        if (rowEditor != null) {
	            if (rowEditor.isEditing()) {
	                rowEditor.endEdit(true);
	            }
	
	            rowEditor.beginEdit(tableView, selectIndex, getColumnAt(x));
	        }
	    }
	
	    selectIndex = -1;
	
	    return consumed;
	}
	
	@Override
	public boolean mouseWheel(Component component, Mouse.ScrollType scrollType, int scrollAmount,
	    int wheelRotation, int x, int y) {
	    TableView tableView = (TableView)getComponent();
	
	    if (highlightIndex != -1) {
	        Bounds rowBounds = getRowBounds(highlightIndex);
	
	        highlightIndex = -1;
	
	        if (tableView.getSelectMode() != TableView.SelectMode.NONE
	            && showHighlight) {
	            repaintComponent(rowBounds.x, rowBounds.y, rowBounds.width, rowBounds.height, true);
	        }
	    }
	
	    return super.mouseWheel(component, scrollType, scrollAmount, wheelRotation, x, y);
	}
	
	/**
	 * {@link KeyCode#UP UP} Selects the previous enabled row when select mode
	 * is not {@link SelectMode#NONE}<br>
	 * {@link KeyCode#DOWN DOWN} Selects the next enabled row when select mode
	 * is not {@link SelectMode#NONE}<p>
	 * {@link Modifier#SHIFT SHIFT} + {@link KeyCode#UP UP} Increases the
	 * selection size by including the previous enabled row when select mode
	 * is {@link SelectMode#MULTI}<br>
	 * {@link Modifier#SHIFT SHIFT} + {@link KeyCode#DOWN DOWN} Increases the
	 * selection size by including the next enabled row when select mode is
	 * {@link SelectMode#MULTI}
	 */
	@Override
	public boolean keyPressed(Component component, int keyCode, Keyboard.KeyLocation keyLocation) {
	    boolean consumed = super.keyPressed(component, keyCode, keyLocation);
	
	    TableView tableView = (TableView)getComponent();
	    TableView.SelectMode selectMode = tableView.getSelectMode();
	
	    switch (keyCode) {
	        case Keyboard.KeyCode.UP: {
	            if (selectMode != TableView.SelectMode.NONE) {
	                int index = tableView.getFirstSelectedIndex();
	
	                do {
	                    index--;
	                } while (index >= 0
	                    && tableView.isRowDisabled(index));
	
	                if (index >= 0) {
	                    if (Keyboard.isPressed(Keyboard.Modifier.SHIFT)
	                        && tableView.getSelectMode() == TableView.SelectMode.MULTI) {
	                        tableView.addSelectedIndex(index);
	                    } else {
	                        tableView.setSelectedIndex(index);
	                    }
	                }
	
	                consumed = true;
	            }
	
	            break;
	        }
	
	        case Keyboard.KeyCode.DOWN: {
	            if (selectMode != TableView.SelectMode.NONE) {
	                int index = tableView.getLastSelectedIndex();
	                int count = tableView.getTableData().getLength();
	
	                do {
	                    index++;
	                } while (index < count
	                    && tableView.isRowDisabled(index));
	
	                if (index < count) {
	                    if (Keyboard.isPressed(Keyboard.Modifier.SHIFT)
	                        && tableView.getSelectMode() == TableView.SelectMode.MULTI) {
	                        tableView.addSelectedIndex(index);
	                    } else {
	                        tableView.setSelectedIndex(index);
	                    }
	                }
	
	                consumed = true;
	            }
	
	            break;
	        }
	    }
	
	    // Clear the highlight
	    if (highlightIndex != -1
	        && tableView.getSelectMode() != TableView.SelectMode.NONE
	        && showHighlight
	        && consumed) {
	        repaintComponent(getRowBounds(highlightIndex));
	    }
	
	    highlightIndex = -1;
	
	    return consumed;
	}
	
	// Component state events
	@Override
	public void enabledChanged(Component component) {
	    super.enabledChanged(component);
	
	    repaintComponent();
	}
	
	@Override
	public void focusedChanged(Component component, Component obverseComponent) {
	    super.focusedChanged(component, obverseComponent);
	
	    repaintComponent();
	}
	
	// Table view events
	@Override
	public void tableDataChanged(TableView tableView, List<?> previousTableData) {
	    invalidateComponent();
	}
	
	@Override
	public void columnSourceChanged(TableView tableView, TableView previousColumnSource) {
	    if (previousColumnSource != null) {
	        previousColumnSource.getTableViewColumnListeners().remove(this);
	    }
	
	    TableView columnSource = tableView.getColumnSource();
	
	    if (columnSource != null) {
	        columnSource.getTableViewColumnListeners().add(this);
	    }
	
	    invalidateComponent();
	}
	
	@Override
	public void rowEditorChanged(TableView tableView, TableView.RowEditor previousRowEditor) {
	    // No-op
	}
	
	@Override
	public void selectModeChanged(TableView tableView, TableView.SelectMode previousSelectMode) {
	    repaintComponent();
	}
	
	@Override
	public void disabledRowFilterChanged(TableView tableView, Filter<?> previousDisabledRowFilter) {
	    repaintComponent();
	}
	
	// Table view column events
	@Override
	public void columnInserted(TableView tableView, int index) {
	    TableView.Column column = tableView.getColumns().get(index);
	
	    if (column.getWidth() == -1) {
	        defaultWidthColumnCount++;
	    }
	
	    invalidateComponent();
	}
	
	@Override
	public void columnsRemoved(TableView tableView, int index, Sequence<TableView.Column> columns) {
	    for (int i = 0, n = columns.getLength(); i < n; i++) {
	        TableView.Column column = columns.get(i);
	
	        if (column.getWidth() == -1) {
	            defaultWidthColumnCount--;
	        }
	    }
	
	    invalidateComponent();
	}
	
	@Override
	public void columnNameChanged(TableView.Column column, String previousName) {
	    invalidateComponent();
	}
	
	@Override
	public void columnHeaderDataChanged(TableView.Column column, Object previousHeaderData) {
	    // No-op
	}
	
	@Override
	public void columnHeaderDataRendererChanged(TableView.Column column,
	    TableView.HeaderDataRenderer previousHeaderDataRenderer) {
	    // No-op
	}
	
	@Override
	public void columnWidthChanged(TableView.Column column, int previousWidth, boolean previousRelative)  {
	    if (column.getWidth() == -1) {
	        defaultWidthColumnCount++;
	    } else {
	        defaultWidthColumnCount--;
	    }
	
	    invalidateComponent();
	}
	
	@Override
	public void columnWidthLimitsChanged(TableView.Column column, int previousMinimumWidth, int previousMaximumWidth) {
	    invalidateComponent();
	}
	
	@Override
	public void columnFilterChanged(TableView.Column column, Object previousFilter) {
	    // No-op
	}
	
	@Override
	public void columnCellRendererChanged(TableView.Column column, TableView.CellRenderer previousCellRenderer) {
	    invalidateComponent();
	}
	
	// Table view row events
	@Override
	public void rowInserted(TableView tableView, int index) {
	    invalidateComponent();
	}
	
	@Override
	public void rowsRemoved(TableView tableView, int index, int count) {
	    invalidateComponent();
	}
	
	@Override
	public void rowUpdated(TableView tableView, int index) {
	    if (variableRowHeight
	        || defaultWidthColumnCount > 0) {
	        invalidateComponent();
	    } else {
	        repaintComponent(getRowBounds(index));
	    }
	}
	
	@Override
	public void rowsCleared(TableView listView) {
	    invalidateComponent();
	}
	
	@Override
	public void rowsSorted(TableView tableView) {
	    if (variableRowHeight) {
	        invalidateComponent();
	    } else {
	        repaintComponent();
	    }
	}
	
	// Table view selection detail events
	@Override
	public void selectedRangeAdded(TableView tableView, int rangeStart, int rangeEnd) {
	    if (tableView.isValid()) {
	        Bounds selectionBounds = getRowBounds(rangeStart);
	        selectionBounds = selectionBounds.union(getRowBounds(rangeEnd));
	        repaintComponent(selectionBounds);
	
	        // Ensure that the selection is visible
	        Bounds visibleSelectionBounds = tableView.getVisibleArea(selectionBounds);
	        if (visibleSelectionBounds.height < selectionBounds.height) {
	            tableView.scrollAreaToVisible(selectionBounds);
	        }
	    } else {
	        validateSelection = true;
	    }
	}
	
	@Override
	public void selectedRangeRemoved(TableView tableView, int rangeStart, int rangeEnd) {
	    // Repaint the area containing the removed selection
	    if (tableView.isValid()) {
	        Bounds selectionBounds = getRowBounds(rangeStart);
	        selectionBounds = selectionBounds.union(getRowBounds(rangeEnd));
	        repaintComponent(selectionBounds);
	    }
	}
	
	@Override
	public void selectedRangesChanged(TableView tableView, Sequence<Span> previousSelectedRanges) {
	    if (previousSelectedRanges != null
	        && previousSelectedRanges != tableView.getSelectedRanges()) {
	        if (tableView.isValid()) {
	            // Repaint the area occupied by the previous selection
	            if (previousSelectedRanges.getLength() > 0) {
	                int rangeStart = previousSelectedRanges.get(0).start;
	                int rangeEnd = previousSelectedRanges.get(previousSelectedRanges.getLength() - 1).end;
	
	                Bounds previousSelectionBounds = getRowBounds(rangeStart);
	                previousSelectionBounds = previousSelectionBounds.union(getRowBounds(rangeEnd));
	
	                repaintComponent(previousSelectionBounds);
	            }
	
	            // Repaint the area occupied by the current selection
	            Sequence<Span> selectedRanges = tableView.getSelectedRanges();
	            if (selectedRanges.getLength() > 0) {
	                int rangeStart = selectedRanges.get(0).start;
	                int rangeEnd = selectedRanges.get(selectedRanges.getLength() - 1).end;
	
	                Bounds selectionBounds = getRowBounds(rangeStart);
	                selectionBounds = selectionBounds.union(getRowBounds(rangeEnd));
	
	                repaintComponent(selectionBounds);
	
	                // Ensure that the selection is visible
	                Bounds visibleSelectionBounds = tableView.getVisibleArea(selectionBounds);
	                if (visibleSelectionBounds != null
	                    && visibleSelectionBounds.height < selectionBounds.height) {
	                    // TODO Repainting the entire component is a workaround for PIVOT-490
	                    repaintComponent();
	
	                    tableView.scrollAreaToVisible(selectionBounds);
	                }
	            }
	        } else {
	            validateSelection = true;
	        }
	    }
	}
	
	@Override
	public void selectedRowChanged(TableView tableView, Object previousSelectedRow) {
	    // No-op
	}

	@Override
	public void selectedCellAdded(FazetTableView tableView, Span rangeX,
			Span rangeY) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void selectedCellRemoved(FazetTableView tableView, Span rangeX,
			Span rangeY) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void selectedCellChanged(FazetTableView tableView,
			Object previousSelectedCell) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void selectedPerimetersChanged(FazetTableView tableView,
			Sequence<com.ott.fazet.wtk.Rectangle> previousSelectedPerimeters) {
	    if (previousSelectedPerimeters != null
		        && previousSelectedPerimeters != tableView.getSelectedPerimeters()) {
		        if (tableView.isValid()) {
		            // Repaint the area occupied by the previous selection
		            if (previousSelectedPerimeters.getLength() > 0) {
		                int rangeStart = previousSelectedPerimeters.get(0).y.start;
		                int rangeEnd = previousSelectedPerimeters.get(previousSelectedPerimeters.getLength() - 1).y.end;
		
		                Bounds previousSelectionBounds = getRowBounds(rangeStart);
		                previousSelectionBounds = previousSelectionBounds.union(getRowBounds(rangeEnd));
		                repaintComponent(previousSelectionBounds);
		            }
		
		            // Repaint the area occupied by the current selection
		            Sequence<com.ott.fazet.wtk.Rectangle> selectedPerimeters = tableView.getSelectedPerimeters();
		            if (selectedPerimeters.getLength() > 0) {
		                int rangeStart = selectedPerimeters.get(0).y.start;
		                int rangeEnd = selectedPerimeters.get(selectedPerimeters.getLength() - 1).y.end;
		
		                Bounds selectionBounds = getRowBounds(rangeStart);
		                selectionBounds = selectionBounds.union(getRowBounds(rangeEnd));
		                repaintComponent(selectionBounds);
		
		                // Ensure that the selection is visible
		                Bounds visibleSelectionBounds = tableView.getVisibleArea(selectionBounds);
		                if (visibleSelectionBounds != null
		                    && visibleSelectionBounds.height < selectionBounds.height) {
		                    // TODO Repainting the entire component is a workaround for PIVOT-490
		                    repaintComponent();
		
		                    tableView.scrollAreaToVisible(selectionBounds);
		                }
		            }
		        } else {
		            validateSelection = true;
		        }
		    }
	}
}