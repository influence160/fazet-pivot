package com.ott.fazet.demos.table;

import java.net.URL;

import org.apache.pivot.beans.BXML;
import org.apache.pivot.beans.Bindable;
import org.apache.pivot.collections.HashMap;
import org.apache.pivot.collections.List;
import org.apache.pivot.collections.Map;
import org.apache.pivot.collections.Sequence;
import org.apache.pivot.util.Resources;
import org.apache.pivot.wtk.ButtonGroup;
import org.apache.pivot.wtk.ButtonGroupListener;
import org.apache.pivot.wtk.Component;
import org.apache.pivot.wtk.ComponentMouseButtonListener;
import org.apache.pivot.wtk.Mouse.Button;
import org.apache.pivot.wtk.ButtonPressListener;
import org.apache.pivot.wtk.PushButton;
import org.apache.pivot.wtk.Span;
import org.apache.pivot.wtk.TextArea;
import org.apache.pivot.wtk.TextInput;
import org.apache.pivot.wtk.Window;

import com.ott.fazet.wtk.FazetTableView;
import com.ott.fazet.wtk.FazetTableViewSelectionListener;
import com.ott.fazet.wtk.Rectangle;
import com.ott.fazet.wtk.TableView;
import com.ott.fazet.wtk.TableView.SelectMode;

public class TheWindow extends Window implements Bindable {

    @BXML private TableView tableView;
    @BXML private org.apache.pivot.wtk.TableView tableView2;
    @BXML private TextArea textArea;
    @BXML private ButtonGroup fgroup;
    @BXML private ButtonGroup sgroup;
    
//    @BXML private TextInput rowIndexInput;
//    @BXML private PushButton removeRowButton;
//    @BXML private PushButton addRowButton;
//    @BXML private TextInput rowIndexInput2;
//    @BXML private PushButton removeRowButton2;
//    @BXML private PushButton addRowButton2;
    
    private int nbrClicks = 0;
    
    @Override
    public void initialize(Map<String, Object> namespace, URL location, Resources resources)
    {
    	
    	//disable row 5 and 14
//    	tableView.setDisabledRowFilter(new Filter<HashMap<Object, Object>>() {
//
//			@Override
//			public boolean include(HashMap<Object, Object> item) {
//				String index = (String) item.get("i");
//				return index.equals("5") || index.equals("14");
//			}
//    		
//		});
    	
    	initializeRadioButton();
    	//initializePushButtons();
    	//initializePushButtons2();
    	
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
					.append(previousSelectedRow)
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
			public void selectedPerimeterAdded(FazetTableView tableView, Span rangeX,
					Span rangeY) {
				StringBuilder sb = new StringBuilder("selectedPerimeterAdded ")
					.append("perimeter : ")
					.append(new Rectangle(rangeX, rangeY));
				insertText(sb.toString());
			}

			@Override
			public void selectedPerimeterRemoved(FazetTableView tableView, Span rangeX,
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

	private void initializeRadioButton() {

    	fgroup.getButtonGroupListeners().add(new ButtonGroupListener() {
			
			@Override
			public void selectionChanged(ButtonGroup buttonGroup,
					org.apache.pivot.wtk.Button previousSelection) {
				String selectedMode = (String) buttonGroup.getSelection().getButtonData();
				tableView.setSelectMode(SelectMode.valueOf(selectedMode));
			}
			
			@Override
			public void buttonRemoved(ButtonGroup buttonGroup,
					org.apache.pivot.wtk.Button button) {
				//no thing
			}
			
			@Override
			public void buttonAdded(ButtonGroup buttonGroup,
					org.apache.pivot.wtk.Button button) {
				//no thing
			}
		});
    	
    	sgroup.getButtonGroupListeners().add(new ButtonGroupListener() {
			
			@Override
			public void selectionChanged(ButtonGroup buttonGroup,
					org.apache.pivot.wtk.Button previousSelection) {
				String selectedMode = (String) buttonGroup.getSelection().getButtonData();
				tableView2.setSelectMode(org.apache.pivot.wtk.TableView.SelectMode.valueOf(selectedMode));
			}
			
			@Override
			public void buttonRemoved(ButtonGroup buttonGroup,
					org.apache.pivot.wtk.Button button) {
				//nothing
			}
			
			@Override
			public void buttonAdded(ButtonGroup buttonGroup,
					org.apache.pivot.wtk.Button button) {
				//nothing
			}
		});
	}

	private void insertText(String text) {
    	if(textArea.getText() != null && !textArea.getText().isEmpty()) {
    		textArea.insertText("\n", 0);
    	}
		textArea.insertText(text, 0);
    }
//    
//    private void initializePushButtons() {
//    	removeRowButton.getButtonPressListeners().add(new ButtonPressListener() {
//			@Override
//			public void buttonPressed(org.apache.pivot.wtk.Button button) {
//				String rowString = rowIndexInput.getText();
//				try {
//					int rowIndex = Integer.parseInt(rowString);
//					tableView.getTableData().remove(rowIndex, 1);
//				} catch (Exception e) {
//					System.out.println(e);
//					rowIndexInput.clear();
//				}
//			}
//		});
//    	addRowButton.getButtonPressListeners().add(new ButtonPressListener() {
//			@Override
//			public void buttonPressed(org.apache.pivot.wtk.Button button) {
//				HashMap<String, String> map = new HashMap<String, String>();
//				map.put("i", String.valueOf(tableView.getTableData().getLength()));
//				map.put("a", randomValue(100));
//				map.put("b", randomValue(1000));
//				map.put("c", randomValue(10000));
//				
//				String rowString = rowIndexInput.getText();
//				try {
//					int rowIndex = Integer.parseInt(rowString);
//					((List<HashMap>)tableView.getTableData()).insert(map, rowIndex);
//				} catch (Exception e) {
//					System.out.println(e);
//					((List<HashMap>)tableView.getTableData()).add(map);
//				}
//			}
//		});
//	}
//
//    
//    private void initializePushButtons2() {
//    	removeRowButton2.getButtonPressListeners().add(new ButtonPressListener() {
//			@Override
//			public void buttonPressed(org.apache.pivot.wtk.Button button) {
//				String rowString = rowIndexInput2.getText();
//				try {
//					int rowIndex = Integer.parseInt(rowString);
//					tableView2.getTableData().remove(rowIndex, 1);
//				} catch (Exception e) {
//					System.out.println(e);
//					rowIndexInput2.clear();
//				}
//			}
//		});
//    	addRowButton2.getButtonPressListeners().add(new ButtonPressListener() {
//			@Override
//			public void buttonPressed(org.apache.pivot.wtk.Button button) {
//				HashMap<String, String> map = new HashMap<String, String>();
//				map.put("i", String.valueOf(tableView2.getTableData().getLength()));
//				map.put("a", randomValue(100));
//				map.put("b", randomValue(1000));
//				map.put("c", randomValue(10000));
//				
//				String rowString = rowIndexInput2.getText();
//				try {
//					int rowIndex = Integer.parseInt(rowString);
//					((List<HashMap>)tableView2.getTableData()).insert(map, rowIndex);
//				} catch (Exception e) {
//					System.out.println(e);
//					((List<HashMap>)tableView2.getTableData()).add(map);
//				}
//				
//			}
//		});
//	}
//    
//    private String randomValue(int max){
//    	int rand = ((Double) Math.floor(Math.random() * max)).intValue();
//    	return String.valueOf(rand);
//    }
 
	
}
