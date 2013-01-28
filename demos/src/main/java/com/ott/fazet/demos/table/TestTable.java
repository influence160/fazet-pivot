package com.ott.fazet.demos.table;

import org.apache.pivot.beans.BXMLSerializer;
import org.apache.pivot.collections.Map;
import org.apache.pivot.wtk.Application;
import org.apache.pivot.wtk.Display;
import org.apache.pivot.wtk.Theme;
import org.apache.pivot.wtk.Window;

import com.ott.fazet.wtk.FazetTableView;
import com.ott.fazet.wtk.TableViewHeader;
import com.ott.fazet.wtk.skin.terra.TerraFazetTableViewSkin;

public class TestTable extends Application.Adapter{
    private Window window = null;
 
    
    @Override
    public void startup(Display display, Map<String, String> properties)
        throws Exception {
    	Theme.getTheme().set(FazetTableView.class, TerraFazetTableViewSkin.class);
    	Theme.getTheme().set(TableViewHeader.class, com.ott.fazet.wtk.skin.terra.TerraTableViewHeaderSkin.class);
        BXMLSerializer bxmlSerializer = new BXMLSerializer();
        window = (Window)bxmlSerializer.readObject(TestTable.class, "test_table.bxml");
        window.open(display);
    }

    @Override
    public boolean shutdown(boolean optional) {
        if (window != null) {
            window.close();
        }
 
        return false;
    }
 
    @Override
    public void suspend() {
    }
 
    @Override
    public void resume() {
    }
}
