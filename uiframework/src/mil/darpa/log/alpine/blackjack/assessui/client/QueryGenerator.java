/*
 * <copyright>
 * Copyright 1997-2001 Defense Advanced Research Projects
 * Agency (DARPA) and ALPINE (a BBN Technologies (BBN) and
 * Raytheon Systems Company (RSC) Consortium).
 * This software to be used only in accordance with the
 * COUGAAR licence agreement.
 * </copyright>
 */
package mil.darpa.log.alpine.blackjack.assessui.client;

import java.awt.Component;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import javax.swing.JComboBox;
import javax.swing.event.TableModelEvent;
import javax.swing.tree.*;

import org.cougaar.lib.uiframework.ui.models.DatabaseTableModel;
import org.cougaar.lib.uiframework.ui.models.RangeModel;
import org.cougaar.lib.uiframework.ui.models.VariableModel;
import org.cougaar.lib.uiframework.ui.util.VariableInterfaceManager;

/**
 * Used to feed a DatabaseTableModel with SQL queries based on the selected
 * variable values managed by a VariableInterfaceManager.  This code is domain
 * specific to blackjack applications.  Other versions of this class could
 * be created for other domains and database schemas.
 */
public class QueryGenerator
{
    private static final boolean debug = false;
    public static final String INV_SAF_METRIC = "Inventory Over Target Level";
    public static final String INV_CRITICAL_METRIC = "Inventory Over Critical Level";
    public static final String RES_DEM_METRIC = "Cumulative Resupply Over Cumulative Demand";
    private static final String DEMAND_METRIC = "Demand";
    private static final String DUEIN_METRIC = "DueIn";
    private static final String DUEOUT_METRIC = "DueOut";
    private static final String INVENTORY_METRIC = "Inventory";
    private static final String CRITICAL_LEVEL_METRIC = "Critical Level";
    private static final String TARGET_LEVEL_METRIC = "Target Level";
    private static final String NO_DATA = "No Data Available";

    private VariableInterfaceManager variableManagerKludgeHelper = null;

    /** the database table model to set queries on. */
    private DatabaseTableModel dbTableModel;

    private boolean aggregateItems = false;

    /**
     * Creates a new query generator.
     *
     * @param dbTableModel the database table model to set queries on
     */
    public QueryGenerator(DatabaseTableModel dbTableModel)
    {
        this.dbTableModel = dbTableModel;
    }

    /**
     * Set whether query generator should aggregate over items or not
     *
     * @param newValue true if query generator should aggregate over items.
     */
    public void setAggregateItems(boolean newValue)
    {
        aggregateItems = newValue;
    }

    /**
     * Returns whether query generator is aggregating over items or not
     *
     * @return true if query generator is aggregating over items.
     */
    public boolean getAggregateItems()
    {
        return aggregateItems;
    }

    /**
     * Generate a SQL query based on the selected variable values referenced
     * by the given variable interface manager.  Set this query in the
     * query generator's database table model.  Further manipulate the
     * result set as required through the database table model transformation
     * methods.
     *
     * @param vim the variable interface manager from which the variable
     *            value selections will be gathered.
     */
    public void generateQuery(VariableInterfaceManager vim)
    {
        // demo kluge
        variableManagerKludgeHelper = vim;
        System.out.println(vim);

        //
        // build sql query from general query data
        //
        VariableModel xAxis = (VariableModel)vim.
                    getDescriptors(VariableModel.X_AXIS).nextElement();
        VariableModel yAxis = (VariableModel)vim.
                    getDescriptors(VariableModel.Y_AXIS).nextElement();

        // Create query(s) that aggregate over organizations
        VariableModel orgDesc = vim.getDescriptor("Org");
        VariableModel metricDesc = vim.getDescriptor("Metric");
        String metricString = metricDesc.getValue().toString();
        TreeNode selectedOrgNode = (TreeNode)orgDesc.getValue();
        if ((orgDesc.getState() == VariableModel.FIXED) ||
            (selectedOrgNode.isLeaf()))
        {
            String query =
                generateSingleQuery(vim, selectedOrgNode, metricString);
            System.out.println(query);
            dbTableModel.setDBQuery(query, 4, 1);
        }
        else
        {
            for (int i = 0; i < selectedOrgNode.getChildCount(); i++)
            {
                String query =
                    generateSingleQuery(vim, selectedOrgNode.getChildAt(i),
                                        metricString);

                System.out.println("query #" + i + ": " + query);
                if (i == 0)
                {
                    dbTableModel.setDBQuery(query, 4, 1);
                }
                else
                {
                    dbTableModel.appendDBQuery(query, 4, 1);
                }
            }
        }

        // aggregation across a time range must be done at the client (here)
        if (debug) System.out.println("Aggregating across time range");
        VariableModel timeDescriptor = vim.getDescriptor("Time");
        if (timeDescriptor.getState() == VariableModel.FIXED)
        {
            RangeModel timeRange = (RangeModel)timeDescriptor.getValue();
            int timeHeaderColumn = dbTableModel.getColumnIndex("unitsOfTime");
            int[] significantColumns = {dbTableModel.getColumnIndex("org"),
                                        dbTableModel.getColumnIndex("item"),
                                        dbTableModel.getColumnIndex("metric")};
            DatabaseTableModel.Combiner timeCombiner =
                (metricString.equals(DEMAND_METRIC) ||
                 metricString.equals(DUEOUT_METRIC) ||
                 metricString.equals(DUEIN_METRIC)) ?
                new AdditiveCombiner() : new AverageCombiner();
            dbTableModel.aggregateRows(significantColumns,
                                       timeRange.toString(),
                                       timeHeaderColumn,
                                       timeCombiner);
        }

        // transform based on needed X and Y variables
        if (debug) System.out.println("Transforming based on needed X/Y");
        String xDescName = xAxis.getName();
        String yDescName = yAxis.getName();
        String xColumnName = DBInterface.getColumnName(xDescName);
        String yColumnName = DBInterface.getColumnName(yDescName);
        String itemColumnName = DBInterface.getColumnName("Item");
        int[] yColumns =
            (vim.getDescriptor("Item").getState() == VariableModel.FIXED) ?
                new int[]{dbTableModel.getColumnIndex(yColumnName),
                          dbTableModel.getColumnIndex(itemColumnName)} :
                new int[]{dbTableModel.getColumnIndex(yColumnName)};
        dbTableModel.setXY(dbTableModel.getColumnIndex(xColumnName),
                           yColumns,
                           dbTableModel.getColumnIndex("assessmentValue"));

        // convert column and row header ids to names
        if (debug) System.out.println("Converting column headers to names");
        convertColumnHeaderIDsToNames(xAxis, dbTableModel, yColumns.length);
        if (debug) System.out.println("Converting row headers to names");
        convertRowHeaderIDsToNames(yAxis, dbTableModel, 0);
        if (yColumns.length > 1)
        {
            convertRowHeaderIDsToNames(vim.getDescriptor("Item"), dbTableModel,
                                       1);
        }

        // weighted aggregation across items
        VariableModel itemDescriptor = vim.getDescriptor("Item");
        if (aggregateItems)
        {
            if (debug) System.out.println("Aggregating across items");
            DefaultMutableTreeNode selectedItemNode =
                (DefaultMutableTreeNode)itemDescriptor.getValue();

            if (itemDescriptor.getState() == VariableModel.FIXED)
            {
                aggregateItems(selectedItemNode, 1);
            }
            else
            {
                if (itemDescriptor.getState() == VariableModel.X_AXIS)
                {
                    dbTableModel.transpose();
                }
                for (int i = 0; i < selectedItemNode.getChildCount(); i++)
                {
                    aggregateItems((DefaultMutableTreeNode)
                        selectedItemNode.getChildAt(i), 0);
                }
                if (itemDescriptor.getState() == VariableModel.X_AXIS)
                {
                    dbTableModel.transpose();
                }
            }
        }
        // remove item column if it is there and not needed
        if ((itemDescriptor.getState() == VariableModel.FIXED) &&
            (aggregateItems))
        {
            dbTableModel.removeColumn(1);
        }

        // sort columns and rows (sort items by NSN)
        Comparator itemCompare = new Comparator() {
                public int compare(Object o1, Object o2)
                {
                    Vector v1 = (Vector)o1;
                    Vector v2 = (Vector)o2;
                    String s1 = convert(v1.elementAt(0), "ITEM_ID");
                    String s2 = convert(v2.elementAt(0), "ITEM_ID");

                    // comp. for bad NSNs for Class I
                    if (s1.startsWith("NSN/89") && s2.startsWith("NSN/89"))
                    {
                        s1 = convert(v1.elementAt(0), "UID");
                        s2 = convert(v2.elementAt(0), "UID");
                    }

                    return s1.compareTo(s2);
                }

                private String convert(Object o, String key)
                {
                    try
                    {
                        DefaultMutableTreeNode tn = (DefaultMutableTreeNode)o;
                        String s = ((Hashtable)
                            tn.getUserObject()).get(key).toString();
                        return s;
                    } catch (Exception e) {}

                    return o.toString();
                }
            };

        if (debug) System.out.println("Sorting columns");
        dbTableModel.transpose();
        if (itemDescriptor.getState() == VariableModel.X_AXIS)
        {
            dbTableModel.sortRows(itemCompare);
        }
        else
        {
            dbTableModel.sortRows(0);
        }
        dbTableModel.transpose();

        if (debug) System.out.println("Sorting rows");
        if (itemDescriptor.getState() == VariableModel.Y_AXIS)
        {
            dbTableModel.sortRows(itemCompare);
        }
        else
        {
            dbTableModel.sortRows(0);
        }

        // derive unit column if needed
        String metric = vim.getDescriptor("Metric").getValue().toString();
        if (yDescName.equals("Item") &&
            !metric.equals(INV_SAF_METRIC) && !metric.equals(RES_DEM_METRIC) &&
            !metric.equals(INV_CRITICAL_METRIC))
        {
            if (debug) System.out.println("Adding unit of issue column");
            dbTableModel.insertColumn(1);
            dbTableModel.setColumnName(1, "UI");
            boolean uifound = false;
            for (int row = 0; row < dbTableModel.getRowCount(); row++)
            {
                if (!(dbTableModel.getValueAt(row, 0) instanceof
                      DefaultMutableTreeNode)) continue;

                DefaultMutableTreeNode tn =
                    (DefaultMutableTreeNode)dbTableModel.getValueAt(row, 0);
                Hashtable ht = (Hashtable)tn.getUserObject();
                Object units = ht.get("UNITS");
                if (units != null)
                {
                    uifound = true;
                    dbTableModel.setValueAt(units, row, 1);
                }
                else
                {
                    dbTableModel.setValueAt("", row, 1);
                }
            }
            if (!uifound)
            {
                dbTableModel.removeColumn(1);
            }
        }

        if (debug) System.out.println("Done.  Firing table change event");
        dbTableModel.fireTableChangedEvent(
            new TableModelEvent(dbTableModel, TableModelEvent.HEADER_ROW));
    }

    private String generateSingleQuery(VariableInterfaceManager vim,
                                       TreeNode orgNode, String metricString)
    {
        String query = null;

        if (metricString.equals(INV_SAF_METRIC))
        {
            int metricInt = Integer.parseInt(DBInterface.lookupValue(
              DBInterface.getTableName("Metric"), "name", "id", metricString));

            query = generateRatioQuery(vim, "Org", orgNode,
                                       INVENTORY_METRIC, TARGET_LEVEL_METRIC,
                                       metricInt);
        }
        else if (metricString.equals(INV_CRITICAL_METRIC))
        {
            int metricInt = Integer.parseInt(DBInterface.lookupValue(
              DBInterface.getTableName("Metric"), "name", "id", metricString));

            query = generateRatioQuery(vim, "Org", orgNode,
                                       INVENTORY_METRIC, CRITICAL_LEVEL_METRIC,
                                       metricInt);
        }
        else if (metricString.equals(RES_DEM_METRIC))
        {
            int metricInt = Integer.parseInt(DBInterface.lookupValue(
              DBInterface.getTableName("Metric"), "name", "id", metricString));

            // Cumulative Resupply Over Cumulative Demand
            String numQuery = generateCumulativeSumQuery(
                generateQueryUsingRootNode(vim, "Org", orgNode,DUEIN_METRIC));
            String denQuery = generateCumulativeSumQuery(
                generateQueryUsingRootNode(vim, "Org", orgNode,DEMAND_METRIC));
            query = generateRatioQuery(numQuery, denQuery, metricInt);
        }
        else
        {
            query = generateQueryUsingRootNode(vim, "Org", orgNode,
                                               metricString);
        }

        return query;
    }

    private String generateRatioQuery(VariableInterfaceManager vim,
                                   String varName, TreeNode tn,
                                   String numMetric, String denMetric,
                                   int metricInt)
    {
        String numQuery =
            generateQueryUsingRootNode(vim, varName, tn, numMetric);
        String denQuery =
            generateQueryUsingRootNode(vim, "Org", tn, denMetric);

        return generateRatioQuery(numQuery, denQuery, metricInt);
    }

    private String generateRatioQuery(String numQuery, String denQuery,
                                      int metricInt)
    {
        String query = null;

        if (DBInterface.DBTYPE.equalsIgnoreCase("oracle"))
        {
            // if numerator data does not exist, assume 0
            query = "select t2.org, t2.item, t2.unitsOfTime, " + metricInt +
                " as METRIC, (NVL(t1.assessmentValue, 0)/t2.assessmentValue)" +
                " as \"ASSESSMENTVALUE\" from (" + numQuery + ") t1, (" +
                denQuery + ") t2 WHERE (t1.ORG (+) = t2.ORG and" +
                " t1.UnitsOfTime (+) = t2.UnitsOfTime and " +
                "t1.item (+) = t2.item and t2.assessmentValue <> 0)";
        }
        else
        {
            // if numerator data does not exist, NULL will be returned
            // (could not get Access to assume 0 as with Oracle)
            query = "select t2.org, t2.item, t2.unitsOfTime, " + metricInt +
                " as METRIC, (t1.assessmentValue/t2.assessmentValue) as " +
                "\"ASSESSMENTVALUE\" from (" + numQuery + ") t1 " +
                "RIGHT OUTER JOIN (" +denQuery+ ") t2 ON (t1.ORG=t2.ORG and" +
                " t1.UnitsOfTime=t2.UnitsOfTime and t1.item=t2.item and" +
                " t2.assessmentValue<>0)";
        }

        return query;
    }

    private String generateCumulativeSumQuery(String baseQuery)
    {
        String query = null;

        //Base query must be modified to get values for all time up to max day.
        int startIndex = baseQuery.indexOf("unitsOfTime >=");
        int endIndex = baseQuery.indexOf("AND ", startIndex) + 4;
        String minTimeConstraint = baseQuery.substring(startIndex, endIndex);
        baseQuery = baseQuery.substring(0, startIndex) +
                    baseQuery.substring(endIndex);

        query = "select t1.org, t1.item, t1.unitsOfTime, t1.metric, " +
                "sum(t2.assessmentValue) as " +
                "\"ASSESSMENTVALUE\" from (" + baseQuery + ") t1, (" +
                baseQuery + ") t2 where (t1." + minTimeConstraint +
                "t1.ORG=t2.ORG and t1.UnitsOfTime>=t2.UnitsOfTime and " +
                "t1.item=t2.item and t1.metric=t2.metric) " +
                "group by t1.unitsOfTime, t1.item, t1.org, t1.metric";

        return query;
    }

    private String
        generateQueryUsingRootNode(VariableInterfaceManager vim,
                                   String varName, TreeNode tn, String metric)
    {
        StringBuffer query = new StringBuffer();

        // determine whether single or multiple metric
        if (metric.startsWith("Group"))
        {
            DefaultMutableTreeNode metricNode =
                (DefaultMutableTreeNode)vim.getDescriptor("Metric").getValue();
            Enumeration metrics = metricNode.children();
            while (metrics.hasMoreElements())
            {
                String childMetric = ((DefaultMutableTreeNode)
                    metrics.nextElement()).getUserObject().toString();
                query.append(
                    generateSingleQuery(vim, tn, childMetric));
                if (metrics.hasMoreElements())
                {
                    query.append(" UNION ALL ");
                }
            }
        }
        else
        {
            String org_id = ((Hashtable)
                             ((DefaultMutableTreeNode)
                                tn).getUserObject()).get("ID").toString();

            String metric_catalog_table = DBInterface.getTableName("Metric");
            String metric_id =  DBInterface.lookupValue(metric_catalog_table,
                                                        "name", "id", metric);
            String metric_table =
                DBInterface.lookupValue(metric_catalog_table,
                                        "name", "table_name", metric);

            query.append("SELECT ");
            query.append(org_id);
            query.append(" AS \"ORG\", item, unitsOfTime, ");
            query.append(metric_id);
            query.append(" as \"METRIC\", sum(");
            query.append(metric_table);
            query.append(".assessmentValue) AS \"ASSESSMENTVALUE\" FROM ");
            query.append(metric_table);
            query.append(" WHERE (");

            // filter data needed based on org, item, metric, time
            if (debug) System.out.println("Generating Org where clause");
            query.append(generateWhereClause("Org", getLeafList(tn).elements()));
            if (debug) System.out.println("Generating Item where clause");
            query.append(" AND "+generateWhereClause(vim.getDescriptor("Item")));
            if (debug) System.out.println("Generating Time where clause");
            query.append(" AND "+generateWhereClause(vim.getDescriptor("Time")));
            query.append(")");

            query.append(" GROUP BY item, unitsOfTime");
        }

        return query.toString();
    }

    /**
     * Generate a SQL where clause to constrain a result set based on the
     * contents of the given variable descriptor.
     *
     * @param v variable descriptor to used to formulate the where clause
     * @return the generated where clause
     */
    private String generateWhereClause(VariableModel v)
    {
        String whereClause = null;
        String varName = v.getName();

        if (varName.equals("Time")) // time is a special case
        {
            RangeModel timeRange = (RangeModel)v.getValue();
            StringBuffer timeWhereClause = new StringBuffer();
            timeWhereClause.append("(unitsOfTime >= ");
            timeWhereClause.append(timeRange.getMin());
            timeWhereClause.append(" AND unitsOfTime <= ");
            timeWhereClause.append(timeRange.getMax());
            timeWhereClause.append(")");
            whereClause = timeWhereClause.toString();
        }
        else if (v.getValue() instanceof DefaultMutableTreeNode)
        {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode)v.getValue();

            //get all required leaf nodes
            //would need to be modified to use aggregated values in database
            if (/*(v.getState() == v.FIXED) || */(node.isLeaf()))
            {
                whereClause = generateWhereClause(varName, node);
            }
            else
            {
                //Enumeration children = node.children(); for Agg. values in db
                Enumeration children = getLeafList(node).elements();
                whereClause = generateWhereClause(varName, children);
            }
        }
        else
        {
            whereClause =generateWhereClause(varName, v.getValue().toString());
        }
        return whereClause;
    }

    private String generateWhereClause(String varName, Enumeration neededValues)
    {
        StringBuffer whereClause =  new StringBuffer();
        String columnName = DBInterface.getColumnName(varName);

        whereClause.append("(");

        Vector neededIDsVector = new Vector();
        while (neededValues.hasMoreElements())
        {
            DefaultMutableTreeNode n =
                (DefaultMutableTreeNode)neededValues.nextElement();
            int id = Integer.parseInt(
                ((Hashtable)n.getUserObject()).get("ID").toString());
            neededIDsVector.add(new Integer(id));
        }

        Collections.sort(neededIDsVector);
        for (int i = 0; i < neededIDsVector.size(); i++)
        {
            int id = ((Integer)neededIDsVector.elementAt(i)).intValue();
            neededIDsVector.setElementAt(new RangeModel(id, id), i);
        }

        // demo kludge
        String itemString =
            ((Hashtable)((DefaultMutableTreeNode)variableManagerKludgeHelper.
                getDescriptor("Item").getValue()).getUserObject()).get("UID").
                toString();
        if (varName.equals("Item") && itemString.equals("All Items"))
        {
            whereClause.append("0=0");
        }
        else
        {
            // find ranges of ids to compare against
            int runCount = 1;
            for (int run = 0; run < runCount; run++)
            {
                for (int i1 = 0; i1 < neededIDsVector.size(); i1++)
                {
                    for (int i2 = i1; i2 < neededIDsVector.size(); i2++)
                    {
                        RangeModel r1 = (RangeModel)neededIDsVector.elementAt(i1);
                        RangeModel r2 = (RangeModel)neededIDsVector.elementAt(i2);

                        if (r1.getMin() - 1 == r2.getMax())
                        {
                            r1.setMin(r2.getMin());
                            neededIDsVector.remove(i2);
                            i2--;
                        }
                        else if (r1.getMax() + 1 == r2.getMin())
                        {
                            r1.setMax(r2.getMax());
                            neededIDsVector.remove(i2);
                            i2--;
                        }
                    }
                }
            }
            for (int i = 0; i < neededIDsVector.size(); i++)
            {
                RangeModel r = (RangeModel)neededIDsVector.elementAt(i);

                if (r.getMin() == r.getMax())
                {
                    whereClause.append(columnName + " = " + r.getMin());
                }
                else
                {
                    whereClause.append("(");
                    whereClause.append(columnName + " >= " + r.getMin());
                    whereClause.append(" AND ");
                    whereClause.append(columnName + " <= " + r.getMax());
                    whereClause.append(")");
                }

                if (i < neededIDsVector.size() - 1)
                {
                    whereClause.append(" OR ");
                }
            }
        }
        whereClause.append(")");

        return whereClause.toString();
    }

    private static String generateWhereClause(String varName,
                                              String neededValue)
    {
        StringBuffer whereClause = new StringBuffer();
        String columnName = DBInterface.getColumnName(varName);

        whereClause.append(columnName);
        whereClause.append(" = ");
        if (varName.equalsIgnoreCase("time"))
        {
            whereClause.append(neededValue);
        }
        else
        {
            whereClause.append(
                DBInterface.lookupValue(DBInterface.getTableName(varName),
                                       "name", "id", neededValue));
        }

        return whereClause.toString();
    }

    private static String generateWhereClause(String varName,
                                              DefaultMutableTreeNode neededValue)
    {
        StringBuffer whereClause = new StringBuffer();
        String columnName = DBInterface.getColumnName(varName);

        whereClause.append(columnName);
        whereClause.append(" = ");
        whereClause.append(((Hashtable)neededValue.getUserObject()).get("ID"));
        return whereClause.toString();
    }

    private static void convertColumnHeaderIDsToNames(VariableModel vm,
                                                      DatabaseTableModel tm,
                                                      int columnStart)
    {
        String varName = vm.getName();

        if (!varName.equalsIgnoreCase("time"))
        {
            Vector oldColumnHeaders = new Vector();
            for (int column = columnStart; column < tm.getColumnCount(); column++)
            {
                oldColumnHeaders.add(tm.getColumnName(column));
            }

            Enumeration newColumnHeaders =
                convertHeaderIDsToNames(vm, oldColumnHeaders.elements());

            int columnCount = columnStart;
            while (newColumnHeaders.hasMoreElements())
            {
                Object name = newColumnHeaders.nextElement();
                if (tm.getColumnCount() <= columnCount)
                {
                    if (tm.getRowCount() == 0)
                    {
                        tm.insertRow(0);
                    }
                    tm.insertColumn(columnCount);
                    tm.setColumnName(columnCount, NO_DATA);
                }
                tm.setColumnName(columnCount++, name);
            }
        }
    }

    private static void convertRowHeaderIDsToNames(VariableModel vm,
                                                   DatabaseTableModel tm,
                                                   int rowHeaderColumn)
    {
        String varName = vm.getName();

        if (!varName.equalsIgnoreCase("Time"))
        {
            Vector oldRowHeaders = new Vector();
            for (int row = 0; row < tm.getRowCount(); row++)
            {
                oldRowHeaders.add(tm.getValueAt(row, rowHeaderColumn));
            }

            Enumeration newRowHeaders =
                convertHeaderIDsToNames(vm, oldRowHeaders.elements());

            int rowCount = 0;
            while (newRowHeaders.hasMoreElements())
            {
                if (tm.getRowCount() <= rowCount)
                {
                    tm.insertRow(rowCount);
                    if (tm.getColumnCount() == 1)
                    {
                        tm.insertColumn(1);
                        tm.setColumnName(1, NO_DATA);
                    }
                }
                Object header = newRowHeaders.nextElement();
                tm.setValueAt(header, rowCount++, rowHeaderColumn);
            }
        }
    }

    private static Enumeration
        convertHeaderIDsToNames(VariableModel vm, Enumeration oldHeaders)
    {
        Enumeration newHeaders = null;
        if (vm.getValue() instanceof DefaultMutableTreeNode)
        {
            Vector newHeaderObjects = new Vector();
            DefaultMutableTreeNode tn = (DefaultMutableTreeNode)vm.getValue();
            if (tn.isLeaf())
            {
                while (oldHeaders.hasMoreElements())
                {
                    String oldHeader = oldHeaders.nextElement().toString();
                    newHeaderObjects.add(tn);
                }
            }
            else
            {
                // demo kludge
                Vector allLeaves = getLeafList(tn);

                while (oldHeaders.hasMoreElements())
                {
                    String oldHeader = oldHeaders.nextElement().toString();

                    // demo kludge
                    if (vm.getName().equals("Item"))
                    {
                        for (int i = 0; i < allLeaves.size(); i++)
                        {
                            DefaultMutableTreeNode child =
                                (DefaultMutableTreeNode)allLeaves.elementAt(i);
                            Hashtable childHT = (Hashtable)child.getUserObject();
                            if (oldHeader.equals(childHT.get("ID")))
                            {
                                newHeaderObjects.add(child);
                                break;
                            }
                        }
                    }
                    else
                    {
                        for (int ci = 0; ci < tn.getChildCount(); ci++)
                        {
                            DefaultMutableTreeNode child =
                                (DefaultMutableTreeNode)tn.getChildAt(ci);
                            Hashtable childHT = (Hashtable)child.getUserObject();
                            if (oldHeader.equals(childHT.get("ID")))
                            {
                                newHeaderObjects.add(child);
                                break;
                            }
                        }
                    }
                }

                /*
                // fill with N/As if data does not exist
                if (newHeaderObjects.size() != tn.getChildCount())
                {
                    for (int ci = 0; ci < tn.getChildCount(); ci++)
                    {
                        DefaultMutableTreeNode child =
                            (DefaultMutableTreeNode)tn.getChildAt(ci);
                        if (!newHeaderObjects.contains(child))
                        {
                            newHeaderObjects.add(child);
                        }
                    }
                }
                */
            }
            newHeaders = newHeaderObjects.elements();
        }
        else
        {
            newHeaders = DBInterface.
                    lookupValues(DBInterface.getTableName(vm.getName()), "id",
                    "name", oldHeaders).elements();
        }

        return newHeaders;
    }

    /**
     * Aggregate the rows of the table model based on the child list of the
     * given tree node.
     *
     * @param node         node under which to aggregate
     * @param headerColumn index of column that contains row headers to match
     *                     with tree elements.
     * @param combiner     the object used to combine two values into one.
     */
    /*
    private void aggregateTreeRows(DefaultMutableTreeNode node,
                                   int headerColumn,
                                   DatabaseTableModel.Combiner combiner)
    {
        for (int i = 0; i < node.getChildCount(); i++)
        {
            DefaultMutableTreeNode tn =
                (DefaultMutableTreeNode)node.getChildAt(i);
            dbTableModel.aggregateRows(getLeafList(tn).elements(),
                                       tn.getUserObject().toString(),
                                       headerColumn, combiner);
        }
    }
    */

    private void aggregateItems(DefaultMutableTreeNode node, int itemColumn)
    {
        if (!node.isLeaf())
        {
            Vector childVector = new Vector();
            for (int i = 0; i < node.getChildCount(); i++)
            {
                DefaultMutableTreeNode tn =
                    (DefaultMutableTreeNode)node.getChildAt(i);
                childVector.add(tn);
                aggregateItems(tn, itemColumn);
            }
            dbTableModel.aggregateRows(childVector, node, itemColumn,
                                       new WeightedAverageCombiner(true));
        }
    }

    /**
     * Creates a delimited list string based on given parameters.
     *
     * @param items objects that will be converted to strings as the
     *              elements of the new delimited list
     * @param prefix prefix to include before each element in the list
     * @param postfix postfix to include after each element in the list
     * @param delimiter delimiter to include between the elements of the list
     * @return the generated delimited list string
     */
    private static String createDelimitedList(Enumeration items, String prefix,
                                              String postfix, String delimiter)
    {
        StringBuffer list = new StringBuffer();
        while (items.hasMoreElements())
        {
            Object item = items.nextElement();
            list.append(prefix + item.toString() + postfix);
            if (items.hasMoreElements()) list.append(delimiter);
        }
        return list.toString();
    }

    /**
     * Returns a vector of all the leaf nodes under the given tree node.
     *
     * @param tn tree node to traverse
     * @return vector of all the leaf nodes under the given tree node
     */
    private static Vector getLeafList(TreeNode tn)
    {
        Vector leafList = new Vector();

        if (tn.isLeaf())
        {
            leafList.add(tn);
        }
        else
        {
            Enumeration children = tn.children();
            while (children.hasMoreElements())
            {
                TreeNode node = (TreeNode)children.nextElement();
                leafList.addAll(getLeafList(node));
            }
        }
        return leafList;
    }

    private class FurthestFromOneCombiner
        implements DatabaseTableModel.Combiner
    {
        public Vector prepare(Vector row, int headerColumn)
        {
            return row;
        }

        public Object combine(Object obj1, Object obj2)
        {
            if ((obj1 == null) ||
                (obj1.toString().equals(DatabaseTableModel.NO_VALUE)))
                return obj2;
            Object combinedObject = null;
            if ((obj1 instanceof Float) && (obj2 instanceof Float))
            {
                float f1 = ((Float)obj1).floatValue();
                float f2 = ((Float)obj2).floatValue();
                float f1Badness = Math.abs(f1 - 1);
                float f2Badness = Math.abs(f2 - 1);
                combinedObject = (f1Badness > f2Badness) ? obj1 : obj2;
            }
            else
            {
                combinedObject = obj1;
            }

            return combinedObject;
        }

        public Vector finalize(Vector row, int headerColumn)
        {
            return row;
        }
    };

    private class AdditiveCombiner implements DatabaseTableModel.Combiner
    {
        public Vector prepare(Vector row, int headerColumn)
        {
            return row;
        }

        public Object combine(Object obj1, Object obj2)
        {
            if ((obj1 == null) ||
                (obj1.toString().equals(DatabaseTableModel.NO_VALUE)))
                return obj2;
            Object combinedObject = null;
            if ((obj1 instanceof Float) && (obj2 instanceof Float))
            {
                float f1 = ((Float)obj1).floatValue();
                float f2 = ((Float)obj2).floatValue();
                combinedObject = new Float(f1 + f2);
            }
            else
            {
                combinedObject = obj1;
            }

            return combinedObject;
        }

        public Vector finalize(Vector row, int headerColumn)
        {
            return row;
        }
    };

    private class AverageCombiner extends AdditiveCombiner
    {
        private int numRowsCombined = 0;

        public Vector prepare(Vector row, int headerColumn)
        {
            numRowsCombined++;
            return row;
        }

        public Vector finalize(Vector row, int headerColumn)
        {
            for (int i = 0; i < row.size(); i++)
            {
                Object obj = row.elementAt(i);
                if (obj instanceof Float)
                {
                    row.setElementAt(
                       new Float((((Number)obj).floatValue())/numRowsCombined),
                       i);
                }
            }
            numRowsCombined = 0;
            return row;
        }
    }

    private class WeightedAverageCombiner extends AdditiveCombiner
    {
        private float totalWeight = 0;
        private boolean assumeNullEqZero = false;

        public WeightedAverageCombiner(boolean assumeNullEqZero)
        {
            this.assumeNullEqZero = assumeNullEqZero;
        }

        public Vector prepare(Vector row, int headerColumn)
        {
            DefaultMutableTreeNode headerNode =
                (DefaultMutableTreeNode)row.elementAt(headerColumn);
            String weightString =
              ((Hashtable)headerNode.getUserObject()).get("WEIGHT").toString();
            float weight = Float.parseFloat(weightString);
            totalWeight += weight;
            for (int i = 0; i < row.size(); i++)
            {
                Object obj = row.elementAt(i);
                if (obj instanceof Float)
                {
                    row.setElementAt(
                       new Float((((Number)obj).floatValue())*weight), i);
                }
            }

            return row;
        }

        public Vector finalize(Vector row, int headerColumn)
        {
            if (assumeNullEqZero)
            {
                totalWeight = 0;

                // find total weight under branch
                DefaultMutableTreeNode headerNode =
                    (DefaultMutableTreeNode)row.elementAt(headerColumn);
                Enumeration branchChildren = headerNode.children();
                while (branchChildren.hasMoreElements())
                {
                    DefaultMutableTreeNode node =
                        (DefaultMutableTreeNode)branchChildren.nextElement();
                    String weightString = ((Hashtable)
                        node.getUserObject()).get("WEIGHT").toString();
                    float weight = Float.parseFloat(weightString);
                    totalWeight += weight;
                }
            }

            for (int i = 0; i < row.size(); i++)
            {
                Object obj = row.elementAt(i);
                if (obj instanceof Float)
                {
                    row.setElementAt(
                       new Float((((Number)obj).floatValue())/totalWeight),
                       i);
                }
            }

            totalWeight = 0;
            return row;
        }
    }

    /**
     * for debug
     */
    private static void printHashtable(Hashtable ht)
    {
        System.out.println("----------");
        Enumeration htKeys = ht.keys();
        while (htKeys.hasMoreElements())
        {
            Object key = htKeys.nextElement();
            System.out.println(key + ": " + ht.get(key));
        }
        System.out.println("----------");
    }
}