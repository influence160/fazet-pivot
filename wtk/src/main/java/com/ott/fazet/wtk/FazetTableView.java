package com.ott.fazet.wtk;

import org.apache.pivot.collections.ArrayList;
import org.apache.pivot.collections.List;
import org.apache.pivot.collections.Sequence;
import org.apache.pivot.wtk.Point;
import org.apache.pivot.wtk.RangeSelection;
import org.apache.pivot.wtk.Span;

import com.ott.fazet.wtk.TableView.SelectMode;

public class FazetTableView extends TableView {
	
    public FazetTableView() {
		this(new ArrayList<Object>());
	}

	public FazetTableView(List<?> tableData) {
        super(tableData);
		installSkin(FazetTableView.class);
	}
    
    /**
     * Returns the selection state of a given index.
     *
     * @param index
     * The index whose selection state is to be tested.
     *
     * @return <tt>true</tt> if the index is selected; <tt>false</tt>,
     * otherwise.
     */
    public boolean isCellSelected(int columnIndex, int rowIndex) {
        indexBoundsCheck("row index", rowIndex, 0, getTableData().getLength() - 1);
        indexBoundsCheck("column index", columnIndex, 0, getColumns().getLength() - 1);

        return perimeterSelection.containsPoint(columnIndex, rowIndex);
    }


	@Override
	public void setSelectedCell(int columnIndex, int rowIndex) {
		if(columnIndex == -1) {
			perimeterSelection.clear();
		}
		setSelectedPerimeter(columnIndex, columnIndex, rowIndex, rowIndex);
	}

	@Override
	public void setSelectedPerimeter(Span x, Span y) {
        ArrayList<Rectangle> selectedPerimeters = new ArrayList<Rectangle>();
        selectedPerimeters.add(new Rectangle(x, y));
        setSelectedPerimeters(selectedPerimeters);
	}

    public Sequence<Span> setSelectedColumnsRanges(Sequence<Span> selectedRanges) {
        if (selectedRanges == null) {
            throw new IllegalArgumentException("selectedRanges is null.");
        }

        // When we're in mode NONE, the only thing we can do is to clear the selection
        if (selectMode == SelectMode.NONE
            && selectedRanges.getLength() > 0) {
            throw new IllegalArgumentException("Selection is not enabled.");
        }
        
        if (selectMode != SelectMode.SINGLECOLUMN
            && selectMode != SelectMode.MULTICOLUMNS) {
            throw new IllegalArgumentException("Selection mode is not a column selection mode.");
        }
        
        // Update the selection
        Sequence<Span> previousSelectedRanges = this.columnsSelection.getSelectedRanges();
        Object previousSelectedRow = (selectMode == SelectMode.SINGLE) ? getSelectedRow() : null;

        RangeSelection listSelection = new RangeSelection();
        for (int i = 0, n = selectedRanges.getLength(); i < n; i++) {
            Span range = selectedRanges.get(i);

            if (range == null) {
                throw new IllegalArgumentException("range is null.");
            }

            if (range.start < 0) {
                throw new IndexOutOfBoundsException("range.start < 0, " + range.start);
            }
            if (range.end >= columnSequence.getLength()) {
                throw new IndexOutOfBoundsException("range.end >= tableData length, "
                            + range.end + " >= " + columnSequence.getLength());
            }

            listSelection.addRange(range.start, range.end);
        }

        this.columnsSelection = listSelection;

        // Notify listeners
        tableViewSelectionListeners.selectedColumnsRangesChanged(this, previousSelectedRanges);

        if (selectMode == SelectMode.SINGLE) {
            tableViewSelectionListeners.selectedColumnChanged(this, previousSelectedRow);
        }

        return getSelectedColumnsRanges();
    }
    
	@Override
	public Sequence<Rectangle> setSelectedPerimeters(ArrayList<Rectangle> selectedPerimeters) {
		// TODO Auto-generated method stub
        if (selectedPerimeters == null) {
            throw new IllegalArgumentException("selectedRanges is null.");
        }

        // When we're in mode NONE, the only thing we can do is to clear the selection
        if (selectMode == SelectMode.NONE
            && selectedPerimeters.getLength() > 0) {
            throw new IllegalArgumentException("Selection is not enabled.");
        }
        
        if (selectMode != SelectMode.SINGLECELL
            && selectMode != SelectMode.MULTICELLS) {
            throw new IllegalArgumentException("Selection mode is not a cell selection mode.");
        }

        // Update the selection
        Sequence<Rectangle> previousSelectedRanges = this.perimeterSelection.getSelectedPerimeters();
        Object previousSelectedCellValue = (selectMode == SelectMode.SINGLECELL) ? getSelectedCellValue() : null;

        PerimeterSelection listSelection = new PerimeterSelection();
        for (int i = 0, n = selectedPerimeters.getLength(); i < n; i++) {
            Rectangle perimeter = selectedPerimeters.get(i);

            if (perimeter == null) {
                throw new IllegalArgumentException("range is null.");
            }

            if (perimeter.x.start < 0) {
                throw new IndexOutOfBoundsException("perimeter.x.start < 0, " + perimeter.x.start);
            }
            if (perimeter.x.end >= columnSequence.getLength()) {
                throw new IndexOutOfBoundsException("perimeter.x.end >= columnSequence length, "
                            + perimeter.x.end + " >= " + columnSequence.getLength());
            }

            if (perimeter.y.start < 0) {
                throw new IndexOutOfBoundsException("perimeter.y.start < 0, " + perimeter.y.start);
            }
            if (perimeter.y.end >= tableData.getLength()) {
                throw new IndexOutOfBoundsException("perimeter.y.end >= tableData length, "
                            + perimeter.y.end + " >= " + tableData.getLength());
            }

            listSelection.addPerimeter(perimeter.x, perimeter.y);
        }

        this.perimeterSelection = listSelection;

        // Notify listeners
        tableViewSelectionListeners.selectedPerimetersChanged(this, previousSelectedRanges);

        if (selectMode == SelectMode.SINGLECELL) {
            tableViewSelectionListeners.selectedCellChanged(this, previousSelectedCellValue);
        }

        return getSelectedPerimeters();
	}


	@Override
	public boolean isCellDisabled(int columnIndex, int rowIndex) {
    	//TODO column, cell disabling
    	return isRowDisabled(rowIndex);
	}


	@Override
	public String getSelectedCellValue() {
        Point point = getSelectedPoint();
        String cell = null;
        if (point != null) {
        	Object row = tableData.get(point.y);
        	Column column = getColumns().get(point.x);
        	cell = column.getCellRenderer().toString(row, column.getName());
        }
        
        return cell;
	}

	@Override
	public boolean removeSelectedCell(int columntIndex, int rowIndex) {
        Sequence<Rectangle> removedPerimeters = removeSelectedPerimeter(new Span(columntIndex, columntIndex)
        	, new Span(rowIndex, rowIndex));
        return (removedPerimeters.getLength() > 0);
	}
	
	@Override
	public void setSelectedPerimeter(int startX, int endX, int startY, int endY) {
    	Span x = new Span(startX, endX);
    	Span y = new Span(startY, endY);
    	setSelectedPerimeter(x, y);
    }
	
	@Override
    //TODO correction
    public Point getFirstSelectedPoint() {
    	if (perimeterSelection.getLength() > 0) {
    		int x = perimeterSelection.get(0).x.start;
    		int y = perimeterSelection.get(0).y.start;
    		return new Point(x, y);
    	}
        return null;
    }

	@Override
    //TODO correction
    public Point getLastSelectedPoint() {
        if (perimeterSelection.getLength() > 0) {
        	Rectangle lastSelectedRange = perimeterSelection.get(perimeterSelection.getLength() - 1);
    		int x = lastSelectedRange.x.end;
    		int y = lastSelectedRange.y.end;
    		return new Point(x, y);
        }
        return null;
    }

    public Sequence<Span> removeSelectedColumnsRange(int start, int end) {
        if (selectMode != SelectMode.MULTICOLUMNS) {
            throw new IllegalStateException("Table view is not in multi-columns-select mode.");
        }

        if (start < 0) {
            throw new IndexOutOfBoundsException("start < 0, " + start);
        }
        if (end >= columnSequence.getLength()) {
            throw new IndexOutOfBoundsException("end >= columnSequence.getLength(), "
                  + end + " >= " + columnSequence.getLength());
        }

        Sequence<Span> removedRanges = columnsSelection.removeRange(start, end);

        int n = removedRanges.getLength();
        for (int i = 0; i < n; i++) {
            Span removedRange = removedRanges.get(i);
            tableViewSelectionListeners.selectedColumnsRangeRemoved(this, removedRange.start, removedRange.end);
        }

        if (n > 0) {
            tableViewSelectionListeners.selectedColumnsRangesChanged(this, null);
        }

        return removedRanges;
    }

	@Override
	public Sequence<Rectangle> removeSelectedPerimeter(Span x, Span y) {
        if (selectMode != SelectMode.MULTICELLS) {
            throw new IllegalStateException("Table view is not in multi-select-rows mode.");
        }

        if (x.start < 0) {
            throw new IndexOutOfBoundsException("x.start < 0, " + x.start);
        }
        if (y.start < 0) {
            throw new IndexOutOfBoundsException("y.start < 0, " + y.start);
        }
        if (x.end >= columnSequence.getLength()) {
            throw new IndexOutOfBoundsException("x.end >= tableData.getLength(), "
                  + x.end + " >= " + tableData.getLength());
        }
        if (y.end >= tableData.getLength()) {
            throw new IndexOutOfBoundsException("y.end >= tableData.getLength(), "
                  + y.end + " >= " + tableData.getLength());
        }

        Sequence<Rectangle> removedPerimeters = perimeterSelection.removePerimeter(x, y);

        int n = removedPerimeters.getLength();
        for (int i = 0; i < n; i++) {
        	Rectangle removedPerimeter = removedPerimeters.get(i);
            tableViewSelectionListeners.selectedPerimeterRemoved(this, removedPerimeter.x, removedPerimeter.y);
        }

        if (n > 0) {
            tableViewSelectionListeners.selectedPerimetersChanged(this, null);
        }

        return removedPerimeters;
	}

    public boolean addSelectedColumnIndex(int index) {
        Sequence<Span> addedRanges = addSelectedColumnsRange(index, index);
        return (addedRanges.getLength() > 0);
    }
    
	@Override
	public boolean addSelectedCell(int columnIndex, int rowIndex) {
        Sequence<Rectangle> addedPerimeters = addSelectedPerimeter(new Span(columnIndex, columnIndex)
        	, new Span(rowIndex, rowIndex));
        return (addedPerimeters.getLength() > 0);
	}

    public Sequence<Span> addSelectedColumnsRange(int start, int end) {
        if (selectMode != SelectMode.MULTICOLUMNS) {
            throw new IllegalStateException("Table view is not in multi-columns-select mode.");
        }

        if (start < 0) {
            throw new IndexOutOfBoundsException("start < 0, " + start);
        }
        if (end >= columnSequence.getLength()) {
            throw new IndexOutOfBoundsException("end >= tableData.getLength(), "
                  + end + " >= " + columnSequence.getLength());
        }

        Sequence<Span> addedRanges = columnsSelection.addRange(start, end);

        int n = addedRanges.getLength();
        for (int i = 0; i < n; i++) {
            Span addedRange = addedRanges.get(i);
            tableViewSelectionListeners.selectedColumnsRangeAdded(this, addedRange.start, addedRange.end);
        }

        if (n > 0) {
            tableViewSelectionListeners.selectedColumnsRangesChanged(this, null);
        }

        return addedRanges;
    }
    
	@Override
	public Sequence<Rectangle> addSelectedPerimeter(Span x, Span y) {
        if (selectMode != SelectMode.MULTICELLS) {
            throw new IllegalStateException("Table view is not in multi-select-cells mode.");
        }

        if (x.start < 0) {
            throw new IndexOutOfBoundsException("x.start < 0, " + x.start);
        }
        if (y.start < 0) {
            throw new IndexOutOfBoundsException("y.start < 0, " + y.start);
        }
        if (x.end >= columnSequence.getLength()) {
            throw new IndexOutOfBoundsException("x.end >= tableData.getLength(), "
                  + x.end + " >= " + tableData.getLength());
        }
        if (y.end >= tableData.getLength()) {
            throw new IndexOutOfBoundsException("y.end >= tableData.getLength(), "
                  + y.end + " >= " + tableData.getLength());
        }

        Sequence<Rectangle> addedPerimeters= perimeterSelection.addPerimeter(x, y);

        int n = addedPerimeters.getLength();
        for (int i = 0; i < n; i++) {
        	Rectangle addedPerimeter = addedPerimeters.get(i);
            tableViewSelectionListeners.selectedPerimeterAdded(this, addedPerimeter.x, addedPerimeter.y);
        }

        if (n > 0) {
            tableViewSelectionListeners.selectedPerimetersChanged(this, null);
        }

        return addedPerimeters;
	}

}
