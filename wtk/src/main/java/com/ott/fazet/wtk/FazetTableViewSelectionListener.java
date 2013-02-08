package com.ott.fazet.wtk;

import org.apache.pivot.collections.Sequence;
import org.apache.pivot.wtk.Span;


/**
 * Table view selection listener interface.
 */
public interface FazetTableViewSelectionListener extends TableViewSelectionListener{
    /**
     * Table view selection listener adapter.
     */
    public static class Adapter implements FazetTableViewSelectionListener {
        @Override
        public void selectedRangeAdded(TableView tableView, int rangeStart, int rangeEnd) {
            // empty block
        }

        @Override
        public void selectedRangeRemoved(TableView tableView, int rangeStart, int rangeEnd) {
            // empty block
        }

        @Override
        public void selectedRangesChanged(TableView tableView, Sequence<Span> previousSelectedRanges) {
            // empty block
        }

        @Override
        public void selectedRowChanged(TableView tableView, Object previousSelectedRow) {
            // empty block
        }

		@Override
		public void selectedPerimeterAdded(FazetTableView tableView, Span rangeX,
				Span rangeY) {
            // empty block
		}

		@Override
		public void selectedPerimeterRemoved(FazetTableView tableView, Span rangeX,
				Span rangeY) {
            // empty block
		}

		@Override
		public void selectedCellChanged(FazetTableView tableView,
				Object previousSelectedRanges) {
            // empty block
		}

		@Override
		public void selectedPerimetersChanged(FazetTableView tableView,
				Sequence<Rectangle> previousSelectedRanges) {
            // empty block
		}

		@Override
		public void selectedColumnsRangeAdded(TableView tableView,
				int rangeStart, int rangeEnd) {
            // empty block
		}

		@Override
		public void selectedColumnsRangeRemoved(TableView tableView,
				int rangeStart, int rangeEnd) {
            // empty block
		}

		@Override
		public void selectedColumnsRangesChanged(TableView tableView,
				Sequence<Span> previousSelectedRanges) {
            // empty block
		}

		@Override
		public void selectedColumnChanged(TableView tableView,
				Object previousSelectedRow) {
            // empty block
		}
    }
    
    /**
     * Called when a range has been added to a table view's selection.
     *
     * @param tableView
     * The source of the event.
     *
     * @param rangeStart
     * The start index of the range that was added, inclusive.
     *
     * @param rangeEnd
     * The end index of the range that was added, inclusive.
     */
    public void selectedColumnsRangeAdded(TableView tableView, int rangeStart, int rangeEnd);

    /**
     * Called when a range has been removed from a table view's selection.
     *
     * @param tableView
     * The source of the event.
     *
     * @param rangeStart
     * The start index of the range that was removed, inclusive.
     *
     * @param rangeEnd
     * The end index of the range that was removed, inclusive.
     */
    public void selectedColumnsRangeRemoved(TableView tableView, int rangeStart, int rangeEnd);

    /**
     * Called when a table view's selection state has been reset.
     *
     * @param tableView
     * The source of the event.
     *
     * @param previousSelectedRanges
     * If the selection changed directly, contains the ranges that were previously
     * selected. If the selection changed indirectly as a result of a model change,
     * contains the current selection. Otherwise, contains <tt>null</tt>.
     */
    public void selectedColumnsRangesChanged(TableView tableView, Sequence<Span> previousSelectedRanges);

    /**
     * Called when a table view's selected item has changed.
     *
     * @param tableView
     * The source of the event.
     *
     * @param previousSelectedRow
     * The row that was previously selected.
     */
    public void selectedColumnChanged(TableView tableView, Object previousSelectedRow);

    /**
     * Called when a perimeter of cells has been added to a table view's selection.
     *
     * @param tableView
     * The source of the event.
     *
     * @param rangeX
     * A Span that represent the start and the end column indexes of the perimeter that was added, inclusive.
     *
     * @param rangeY
     * A Span that represent the start and the end row indexes of the perimeter that was added, inclusive.
     */
    public void selectedPerimeterAdded(FazetTableView tableView, Span rangeX, Span rangeY);

    /**
     * Called when a perimeter has been removed from a table view's selection.
     *
     * @param tableView
     * The source of the event.
     *
     * @param rangeX
     * A Span that represent the start and the end column indexes of the perimeter that was added, inclusive.
     *
     * @param rangeY
     * A Span that represent the start and the end row indexes of the perimeter that was added, inclusive.
     */
    public void selectedPerimeterRemoved(FazetTableView tableView, Span rangeX, Span rangeY);

    /**
     * Called when a table view's selected cell has been changed.
     *
     * @param tableView
     * The source of the event.
     *
     * @param previousSelectedRanges
     * If the selection changed directly, contains the ranges that were previously
     * selected. If the selection changed indirectly as a result of a model change,
     * contains the current selection. Otherwise, contains <tt>null</tt>.
     */
    public void selectedCellChanged(FazetTableView tableView, Object previousSelectedCell);
    
    /**
     * Called when a table view's selection state has been reset.
     *
     * @param tableView
     * The source of the event.
     *
     * @param previousSelectedRanges
     * If the selection changed directly, contains the ranges that were previously
     * selected. If the selection changed indirectly as a result of a model change,
     * contains the current selection. Otherwise, contains <tt>null</tt>.
     */
    public void selectedPerimetersChanged(FazetTableView tableView, Sequence<Rectangle> previousSelectedRanges);


}