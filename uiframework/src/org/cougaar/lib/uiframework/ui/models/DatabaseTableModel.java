package org.cougaar.lib.uiframework.ui.models;

import java.util.Enumeration;
import java.util.Vector;
import javax.swing.table.TableModel;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import java.sql.*;

import org.cougaar.lib.uiframework.ui.util.DBDatasource;

/**
 * The database table model is used to represent the result set of a SQL
 * query as a table model that can be used to populate either a graph or
 * table view of the data.
 *
 * Additional methods are included to manipulate the modeled result set after
 * the query (e.g. transpose, setXY, aggregate).
 */
public class DatabaseTableModel implements TableModel
{
    /** Vector of table model listeners. */
    private Vector tableModelListeners = new Vector();

    /** Vector of rows modeled.  Each row is a vector of data */
    private Vector dataRows = new Vector();

    /** Default constructor; model will contain no data until DBQuery is set */
    public DatabaseTableModel() {}

    /**
     * Create new model of result set of specified SQL query
     *
     * @param     sqlQuery  the SQL query to make of database.
     */
    public DatabaseTableModel(String sqlQuery)
    {
        setDBQuery(sqlQuery);
    }

    /**
     * Returns the number of rows in the model
     *
     * @return the number of rows in the model
     */
    public synchronized int getRowCount()
    {
        return dataRows.size() - 1;
    }

    /**
     * Returns the number of columns in the model
     *
     * @return the number of columns in the model
     */
    public synchronized int getColumnCount()
    {
        return (dataRows.size() > 0) ?
               ((Vector)dataRows.elementAt(0)).size() : 0;
    }

    /**
     * Returns the name of a column given column index.
     *
     * @return the name of the column
     */
    public String getColumnName(int columnIndex)
    {
        String name =
            ((Vector)dataRows.elementAt(0)).elementAt(columnIndex).toString();
        return name;
    }

    /**
     * Set the name of a column given column index.
     *
     * @param columnIndex the index of the column to set
     * @param name        the name of the column
     */
    public void setColumnName(int columnIndex, String name)
    {
        setValueAt(name, -1, columnIndex);
    }

    /**
     * Gets the index of a column given column name.
     *
     * @param columnName  the name of the column
     * @return the index of the column
     */
    public int getColumnIndex(String columnName)
    {
        int columnIndex = -1;
        Vector columnHeaders = (Vector)dataRows.elementAt(0);
        for (int i=0; i<columnHeaders.size(); i++)
        {
            if (columnName.
                equalsIgnoreCase((String)columnHeaders.elementAt(i)))
            {
                columnIndex = i;
                break;
            }
        }
        return columnIndex;
    }

    /**
     * Gets the class of a column given column index.
     *
     * @return the class of the column
     */
    public Class getColumnClass(int columnIndex)
    {
        return String.class;
    }

    /**
     * Returns true if the view should allow editing of the cell.
     *
     * @param rowIndex    the row index of cell
     * @param columnIndex the column index of cell
     * @return whether the cell is editable
     */
    public boolean isCellEditable(int rowIndex, int columnIndex)
    {
        return false;
    }

    /**
     * Returns the contents of the cell.
     *
     * @param rowIndex    the row index of cell
     * @param columnIndex the column index of cell
     * @return the cell contents
     */
    public synchronized Object getValueAt(int rowIndex, int columnIndex)
    {
        try
        {
            return ((Vector)dataRows.elementAt(rowIndex + 1)).
                    elementAt(columnIndex);
        }
        catch (Exception e)
        {
            return new Float(99999); // look into this
        }
    }

    /**
     * Sets the contents of the cell.
     * (Does not trigger a table model change event)
     *
     * @param aValue      new value for the cell
     * @param rowIndex    the row index of cell
     * @param columnIndex the column index of cell
     */
    public synchronized void
        setValueAt(Object aValue, int rowIndex, int columnIndex)
    {
        ((Vector)dataRows.elementAt(rowIndex + 1)).
            setElementAt(aValue, columnIndex);
    }

    /**
     * Adds a new table model listener to the table.
     *
     * @param tml the table model listener to add
     */
    public void addTableModelListener(TableModelListener tml)
    {
        tableModelListeners.add(tml);
    }

    /**
     * Removes a table model listener from the table.
     *
     * @param tml the table model listener to remove
     */
    public void removeTableModelListener(TableModelListener tml)
    {
        tableModelListeners.remove(tml);
    }

    /**
     * Fire a new table change event on table.  This will cause all views to
     * resync with model.
     *
     * @param e the table model event to associate with the table change event
     */
    public void fireTableChangedEvent(TableModelEvent e)
    {
        for (int i = 0; i < tableModelListeners.size(); i++)
        {
            TableModelListener tml =
                (TableModelListener)tableModelListeners.elementAt(i);
            tml.tableChanged(e);
        }
    }

    /**
     * Set the database query to which this model should model result set.
     * This result set will replace any existing data in model.
     *
     * @param sqlQuery the SQL query to send to database
     */
    public void setDBQuery(String sqlQuery)
    {
        setDBQuery(sqlQuery, 1, 1);
    }


    /**
     * Set the database query to which this model should model result set.
     * This result set will replace any existing data in model.
     *
     * @param sqlQuery the SQL query to send to database
     * @param rowHeaders the number of leading columns that should be considered
     *                   row headers in the result set.
     * @param columnHeaders the number of leading rows that should be
     *                      considered column headers in the result set.
     */
    public synchronized void
        setDBQuery(String sqlQuery, int rowHeaders, int columnHeaders)
    {
        dataRows = new Vector();

        Connection con = null;
        Statement stmt = null;
        ResultSet rs = null;
        try
        {
            //con = DBDatasource.establishConnection();
            con = DBDatasource.getConnection();
            stmt = con.createStatement();
            rs = stmt.executeQuery(sqlQuery);

            // get/use meta data from table
            Vector columnRow = new Vector();
            dataRows.add(columnRow);
            ResultSetMetaData rsmd = rs.getMetaData();
            int numColumns = rsmd.getColumnCount();
            for (int i = 1; i <= numColumns; i++)
            {
                columnRow.add(rsmd.getColumnName(i));
            }

            // get the data
            int rowCount = 1;
            while(rs.next())
            {
                rowCount++;
                Vector dataRow = new Vector();
                for (int i = 1; i <= numColumns; i++)
                {
                    if ((i <= rowHeaders) || (rowCount <= columnHeaders))
                    {
                        dataRow.add(rs.getString(i));
                    }
                    else
                    {
                        String assessmentValue;
                        if ((assessmentValue = rs.getString(i)) == null)
                        {
                            dataRow.add("N/A");
                        }
                        else
                        {
                            dataRow.add(Float.valueOf(assessmentValue));
                        }
                    }
                }
                dataRows.add(dataRow);
            }
        }
        catch(SQLException e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                //if (con != null) con.close();
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }
        }

        fireTableChangedEvent(
            new TableModelEvent(this, TableModelEvent.HEADER_ROW));
    }

    /**
     * This method takes three column indices and transforms the table such
     * that the values in the first column are used as column headers and the
     * values in the second column are used as row headers.  The values in the
     * third column are used to populate the body of the new table (indexed by
     * the other two columns).  The new table will have a single row of column
     * headers and a single column of row headers.
     *
     * @param xColumn the column index of which the values should be used as
     *                column headers after transformation
     * @param yColumn the column index of which the values should be used as
     *                row headers after transformation
     * @param valueColumn the column index of which the values should be used
     *                    as data cells after transformation
     */
    public synchronized void setXY(int xColumn, int yColumn, int valueColumn)
    {
        if (dataRows.size() > 0)
        {
            // determine new column headers and mapped data points
            Vector columnHeaders = new Vector();
            columnHeaders.addElement(" "); // column header for row headers
            Vector rowHeaders = new Vector();
            Vector dataPoints = new Vector();
            for (int rowIndex = 1; rowIndex < dataRows.size(); rowIndex++)
            {
                int transformedColumnIndex = 0;
                int transformedRowIndex = 0;
                Vector origRowVector = (Vector)dataRows.elementAt(rowIndex);

                Object newColumnHeader = origRowVector.elementAt(xColumn);
                transformedColumnIndex = columnHeaders.indexOf(newColumnHeader);
                if (transformedColumnIndex == -1)
                {
                    columnHeaders.addElement(newColumnHeader);
                    transformedColumnIndex = columnHeaders.size();
                }

                Object newRowHeader = origRowVector.elementAt(yColumn);
                transformedRowIndex = rowHeaders.indexOf(newRowHeader) + 1;
                if (transformedRowIndex == 0)
                {
                    rowHeaders.addElement(newRowHeader);
                    transformedRowIndex = rowHeaders.size();
                }

                dataPoints.addElement(
                    new DataPoint(transformedRowIndex, transformedColumnIndex,
                                  origRowVector.elementAt(valueColumn)));
            }

            // create new rows
            Vector newRows = new Vector();
            newRows.addElement(columnHeaders);
            for (int i = 0; i < dataPoints.size(); i++)
            {
                DataPoint dp = (DataPoint)dataPoints.elementAt(i);
                if (dp.rowIndex >= newRows.size())
                {
                    Vector newRow = new Vector();
                    newRow.addElement(rowHeaders.elementAt(dp.rowIndex-1));
                    newRows.addElement(newRow);
                }

                Vector targetRow = (Vector)newRows.elementAt(dp.rowIndex);
                if (dp.columnIndex >= targetRow.size())
                {
                    targetRow.add(dp.value);
                }
                else
                {
                    targetRow.setElementAt(dp.value, dp.columnIndex);
                }
            }

            dataRows = newRows;
            fireTableChangedEvent(
                new TableModelEvent(this, TableModelEvent.HEADER_ROW));
        }
    }

    /**
     * This class is used by setXY transformation for intermediate data
     * handling
     */
    private class DataPoint
    {
        public int rowIndex;
        public int columnIndex;
        public Object value;
        public DataPoint(int rowIndex, int columnIndex, Object value)
        {
            this.rowIndex = rowIndex;
            this.columnIndex= columnIndex;
            this.value = value;
        }
        public String toString()
        {
            return "row: " + rowIndex + ", column: " + columnIndex +
                   ", value: " + value;
        }
    }

    /**
     * transpose the contents of this model.
     * Rows become columns and columns become rows.
     */
    public synchronized void transpose()
    {
        if (dataRows.size() > 0)
        {
            Vector newRows = new Vector();

            for (int rowIndex = 0;
                 rowIndex < ((Vector)dataRows.elementAt(0)).size(); rowIndex++)
            {
                newRows.add(new Vector());
            }

            for (int rowIndex = 0; rowIndex < dataRows.size(); rowIndex++)
            {
                Vector rowVector = (Vector)dataRows.elementAt(rowIndex);
                for (int columnIndex = 0; columnIndex < rowVector.size();
                     columnIndex++)
                {
                    Object data = rowVector.elementAt(columnIndex);
                    ((Vector)newRows.elementAt(columnIndex)).add(data);
                }
            }

            dataRows = newRows;
        }

        fireTableChangedEvent(
            new TableModelEvent(this, TableModelEvent.HEADER_ROW));
    }

    /**
     * Returns the number of rows that have given id as a row header
     *
     * @param rowID the header to search for
     * @param headerColumn the index of the column to use as header column
     */
    public int getRowCount(String rowID, int headerColumn)
    {
        int count = 0;

        for (int i = 0; i < dataRows.size(); i++)
        {
            Vector row = (Vector)dataRows.elementAt(i);
            if (row.elementAt(headerColumn).toString().equals(rowID))
            {
                count++;
            }
        }

        return count;
    }

    /**
     * aggregates all rows in which all values in the significant columns
     * are equal between rows.  The agg header is the new header value placed
     * in the aggregatedHeaderColumn of the aggregated rows.
     *
     * @param significantColumns indices of columns that should be the same in
     *                           rows to aggregate
     * @param aggHeader          the new header to set in the new aggragated
     *                           row
     * @param aggregatedHeaderColumn the column index in which the new
     *                               aggregated header should be set
     */
    public void aggregateRows(int[] significantColumns, String aggHeader,
                              int aggregatedHeaderColumn)
    {
        if (getRowCount() > 1)
        {
            Vector modelRow;
            while (!(modelRow = (Vector)dataRows.elementAt(1)).
                   elementAt(aggregatedHeaderColumn).equals(aggHeader))
            {
                dataRows.removeElementAt(1);
                Vector aggregateRow =
                    extractAndAggregateRows(modelRow, significantColumns,
                                            aggHeader, aggregatedHeaderColumn);
                dataRows.addElement(aggregateRow);
            }
        }
    }

    /**
     * aggregates all rows in which all values in the significant columns
     * are equal to model row.  The agg header is the new header value placed
     * in the aggregatedHeaderColumn of the aggregated rows.
     *
     * @param modelRow           index of the row to use as model for
     *                           comparison
     * @param significantColumns indices of columns that should be the same in
     *                           rows to aggregate
     * @param aggHeader          the new header to set in the new aggragated
     *                           row
     * @param aggregatedHeaderColumn the column index in which the new
     *                               aggregated header should be set
     * @return the new aggregated row
     */
     private Vector
        extractAndAggregateRows(Vector modelRow, int[] significantColumns,
                                String aggHeader, int aggregatedHeaderColumn)
    {
        Vector aggregateRow = modelRow;

        for (int rowIndex = 1; rowIndex < dataRows.size(); rowIndex++)
        {
            Vector row = (Vector)dataRows.elementAt(rowIndex);
            if (row.elementAt(aggregatedHeaderColumn).equals(aggHeader)) break;
            boolean matches = true;
            for (int i = 0; i < significantColumns.length; i++)
            {
                int columnIndex = significantColumns[i];
                Object valueToMatch = modelRow.elementAt(columnIndex);
                Object value = row.elementAt(columnIndex);
                if (!value.equals(valueToMatch))
                {
                    matches = false;
                    break;
                }
            }
            if (matches)
            {
                dataRows.removeElementAt(rowIndex--);
                aggregateRow = combine(aggregateRow, row);
            }
         }
         aggregateRow.setElementAt(aggHeader, aggregatedHeaderColumn);

         return aggregateRow;
    }

    /**
     * Aggregates the given list of rows into a single row with new header
     *
     * @param rowList      list of row header strings to combine
     * @param aggHeader    the new header to set in the new aggragated
     *                     row
     * @param headerColumn the column index in which the new
     *                     aggregated header should be set
     */
    public void aggregateRows(Enumeration rowList, String aggHeader,
                              int headerColumn)
    {
        Vector aggregateRow = null;
        while (rowList.hasMoreElements())
        {
            String rowID = rowList.nextElement().toString();
            Vector row = findRow(rowID, headerColumn);
            dataRows.remove(row);
            if (aggregateRow == null)
            {
                aggregateRow = row;
            }
            else
            {
                aggregateRow = combine(aggregateRow, row);
            }
        }
        aggregateRow.set(headerColumn, aggHeader);
        dataRows.add(aggregateRow);

        fireTableChangedEvent(
            new TableModelEvent(this, TableModelEvent.HEADER_ROW));
    }

    /**
     * Returns list of rows the have the given row header
     *
     * @rowID        the row header to search for
     * @headerColumn the index of the column to use as row header column
     */
    private Vector findRow(String rowID, int headerColumn)
    {
        for (int i = 0; i < dataRows.size(); i++)
        {
            Vector row = (Vector)dataRows.elementAt(i);
            if (row.elementAt(headerColumn).toString().equals(rowID))
            {
                return row;
            }
        }
        return null;
    }

    /**
     * combines two rows into one row
     *
     * @param row1 first row to be combined
     * @param row2 second row to be combined
     * @return the combined row
     */
    private Vector combine(Vector row1, Vector row2)
    {
        Vector combinedRow = new Vector();
        for (int i = 0; i < row1.size(); i++)
        {
            combinedRow.add(combine(row1.elementAt(i), row2.elementAt(i)));
        }

        return combinedRow;
    }

    /**
     * combines values into one value
     *
     * This method currently uses hard coded combining method.
     * Will be extended in future to use a given method.
     *
     * @param obj1 first object to be combined
     * @param obj2 second object to be combined
     * @return the combined object
     */
    private Object combine(Object obj1, Object obj2)
    {
        Object combinedObject = null;
        if (obj1 instanceof Float)
        {
            float f1 = ((Float)obj1).floatValue();
            float f2 = ((Float)obj2).floatValue();
            float f1Badness = Math.abs(f1 - 1);
            float f2Badness = Math.abs(f2 - 1);
            combinedObject =  (f1Badness > f2Badness) ? obj1 : obj2;
        }
        else
        {
            combinedObject = obj1;
        }

        return combinedObject;
    }

    /**
     * For debug.  Prints out contents of model to standard out
     */
    private void printDataMatrix()
    {
        System.out.println(dataRows.size() + " rows, " +
                           ((Vector)dataRows.elementAt(0)).size() +" columns");
        for (int rowIndex = 0; rowIndex < dataRows.size(); rowIndex++)
        {
            System.out.println();
            Vector rowVector = (Vector)dataRows.elementAt(rowIndex);
            for (int columnIndex = 0; columnIndex < rowVector.size();
                 columnIndex++)
            {
                System.out.print(rowVector.elementAt(columnIndex) + " ");
            }
        }
    }
}