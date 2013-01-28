package com.ott.fazet.wtk;

import org.apache.pivot.collections.ArrayList;
import org.apache.pivot.collections.List;
import org.apache.pivot.collections.Sequence;
import org.apache.pivot.wtk.Point;
import org.apache.pivot.wtk.Span;

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
		setSelectedPerimeter(columnIndex, columnIndex, rowIndex, rowIndex);
	}

	@Override
	public void setSelectedPerimeter(Span x, Span y) {
        ArrayList<Rectangle> selectedPerimeters = new ArrayList<Rectangle>();
        selectedPerimeters.add(new Rectangle(x, y));
        setSelectedPerimeters(selectedPerimeters);
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

}
