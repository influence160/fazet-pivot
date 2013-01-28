/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ott.fazet.wtk.content;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.pivot.json.JSON;
import org.apache.pivot.util.CalendarDate;

/**
 * Default renderer for table view cells that contain date data. Renders
 * cell contents as a formatted date.
 */
public class TableViewDateCellRenderer extends TableViewCellRenderer {
    private DateFormat dateFormat = DateFormat.getDateInstance();

    public DateFormat getDateFormat() {
        return dateFormat;
    }

    public void setDateFormat(DateFormat dateFormat) {
        if (dateFormat == null) {
            throw new IllegalArgumentException("dateFormat is null.");
        }

        this.dateFormat = dateFormat;
    }

    public void setDateFormat(String dateFormat) {
        setDateFormat(new SimpleDateFormat(dateFormat));
    }

    @Override
    public String toString(Object row, String columnName) {
        Object cellData = JSON.get(row, columnName);

        String string;
        if (cellData instanceof Date) {
            string = dateFormat.format((Date)cellData);
        } else if (cellData instanceof Long) {
            string = dateFormat.format(new Date((Long)cellData));
        } else if (cellData instanceof Calendar) {
            string = dateFormat.format(((Calendar)cellData).getTime());
        } else if (cellData instanceof CalendarDate) {
            string = dateFormat.format(((CalendarDate)cellData).toCalendar().getTime());
        } else {
            string = (cellData == null) ? null : cellData.toString();
        }

        return string;
    }
}
