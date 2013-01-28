package com.ott.fazet.demos.table;

import java.net.URL;

import org.apache.pivot.beans.BXML;
import org.apache.pivot.beans.Bindable;
import org.apache.pivot.collections.HashMap;
import org.apache.pivot.collections.Map;
import org.apache.pivot.collections.Sequence;
import org.apache.pivot.util.Filter;
import org.apache.pivot.util.Resources;
import org.apache.pivot.wtk.Component;
import org.apache.pivot.wtk.ComponentMouseButtonListener;
import org.apache.pivot.wtk.Span;
import org.apache.pivot.wtk.TextArea;
import org.apache.pivot.wtk.Window;
import org.apache.pivot.wtk.Mouse.Button;

import com.ott.fazet.wtk.FazetTableView;
import com.ott.fazet.wtk.FazetTableViewSelectionListener;
import com.ott.fazet.wtk.Rectangle;
import com.ott.fazet.wtk.TableView;
import com.ott.fazet.wtk.TableViewSelectionListener;

public class TheWindow extends Window implements Bindable {

    @BXML private TableView tableView;
    @BXML private TextArea textArea;
    
    private int nbrClicks = 0;
    
    @Override
    public void initialize(Map<String, Object> namespace, URL location, Resources resources)
    {
    	//disable row 5 and 14
    	tableView.setDisabledRowFilter(new Filter<HashMap<Object, Object>>() {

			@Override
			public boolean include(HashMap<Object, Object> item) {
				String index = (String) item.get("i");
				return index.equals("5") || index.equals("14");
			}
    		
		});
    	
    	tableView.getComponentMouseButtonListeners().add(new ComponentMouseButtonListener() {
			
			@Override
			public boolean mouseUp(Component arg0, Button arg1, int arg2, int arg3) {
				return false;
			}
			
			@Override
			public boolean mouseDown(Component arg0, Button arg1, int arg2, int arg3) {
				nbrClicks++;
				insertText("click " + nbrClicks );
				return false;
			}
			
			@Override
			public boolean mouseClick(Component arg0, Button arg1, int arg2, int arg3,
					int arg4) {
				return false;
			}
		});

	    tableView.getTableViewSelectionListeners().add(new FazetTableViewSelectionListener() {
			
			@Override
			public void selectedRowChanged(TableView tableView,
					Object previousSelectedRow) {
				StringBuilder sb = new StringBuilder("selectedRowChanged ")
					.append("previousSelectedRow : ")
					.append(previousSelectedRow.toString())
					.append(" selectedRow : ")
					.append(tableView.getSelectedRow());
				insertText(sb.toString());
			}
			
			@Override
			public void selectedRangesChanged(TableView tableView,
					Sequence<Span> previousSelectedRanges) {
				// TODO Auto-generated method stub
				insertText("selectedRangesChanged");
			}
			
			@Override
			public void selectedRangeRemoved(TableView tableView, int rangeStart,
					int rangeEnd) {
				// TODO Auto-generated method stub
				insertText("selectedRangeRemoved");
			}
			
			@Override
			public void selectedRangeAdded(TableView tableView, int rangeStart,
					int rangeEnd) {
				// TODO Auto-generated method stub
				insertText("selectedRangeAdded");
			}

			@Override
			public void selectedCellAdded(FazetTableView tableView, Span rangeX,
					Span rangeY) {
				// TODO Auto-generated method stub
				StringBuilder sb = new StringBuilder("selectedPerimeterAdded ")
					.append("perimeter : ")
					.append(new Rectangle(rangeX, rangeY));
				insertText(sb.toString());
			}

			@Override
			public void selectedCellRemoved(FazetTableView tableView, Span rangeX,
					Span rangeY) {
				// TODO Auto-generated method stub
				insertText("selectedCellRemoved");
				
			}

			@Override
			public void selectedCellChanged(FazetTableView tableView,
					Object previousSelectedCellValue) {
				StringBuilder sb = new StringBuilder("selectedCellChanged ")
					.append("\n    previousSelectedCellValue : ")
					.append(previousSelectedCellValue)
					.append("\n    selectedCellValue : ")
					.append(tableView.getSelectedCellValue())
					.append("\n    selectedPoint : ")
					.append(tableView.getSelectedPoint());
				insertText(sb.toString());
			}

			@Override
			public void selectedPerimetersChanged(FazetTableView tableView,
					Sequence<Rectangle> previousSelectedPerimeters) {
				StringBuilder sb = new StringBuilder("selectedPerimetersChanged ")
					.append("\n    previousSelectedPerimeters : ")
					.append(previousSelectedPerimeters)
					.append("\n    selectedPerimeters : ")
					.append(tableView.getSelectedPerimeters());
				insertText(sb.toString());
			}
		});
	}
    
    private void insertText(String text) {
    	if(textArea.getText() != null && !textArea.getText().isEmpty()) {
    		textArea.insertText("\n", 0);
    	}
		textArea.insertText(text, 0);
    }
 
	
}
