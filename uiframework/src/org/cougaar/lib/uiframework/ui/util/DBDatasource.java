package org.cougaar.lib.uiframework.ui.util;

import java.sql.*;
import java.util.*;
import javax.swing.tree.*;

import org.cougaar.lib.uiframework.transducer.*;
import org.cougaar.lib.uiframework.transducer.configs.*;
import org.cougaar.lib.uiframework.transducer.elements.*;
//import org.cougaar.lib.uiframework.transducer.custom.JTreeInterpreter;

/**
 * This class is used to extract data from a database specified by system
 * properties.  It provides generic lookup methods for getting data out
 * of tables and wrapper methods for the use of the UI framework SQL
 * transducer.
 *
 * The following system properies must be set for proper configuration:<BR><BR>
 *
 * DBURL - JDBC url to use to access the database<BR>
 * DBDRIVER - JDBC driver classname to use to access the database<BR>
 * DBUSER - User account to use to access the database<BR>
 * DBPASSWORD - User password to use to access the database<BR>
 */
public class DBDatasource
{
    private static final String DBTYPE = System.getProperty("DBTYPE");

    /** JDBC url to use to access the database */
    public static final String DBURL = (DBTYPE.equalsIgnoreCase("access") ?
        "jdbc:odbc:":"jdbc:oracle:thin:@") + System.getProperty("DBURL");

    /** JDBC driver to use to access the database */
    public static final String DBDRIVER = (DBTYPE.equalsIgnoreCase("access") ?
        "sun.jdbc.odbc.JdbcOdbcDriver" : "oracle.jdbc.driver.OracleDriver");

    /** User account to use to access the database */
    public static final String DBUSER = System.getProperty("DBUSER");

    /** User password to use to access the database */
    public static final String DBPASSWORD = System.getProperty("DBPASSWORD");

    /**
     * Establish a new connection to the database using the configured system
     * property values.
     *
     * @return a new connection to the database.
     */
    public static Connection establishConnection() throws SQLException
    {
        Connection con = null;

        //
        // Load Database Driver
        //
        try
        {
            Class.forName(DBDRIVER);
        }
        catch(Exception e)
        {
            System.out.println("Failed to load driver");
        }

        // Connect to the database
        con = DriverManager.getConnection(DBURL, DBUSER, DBPASSWORD);

        return con;
    }

    /**
     * Get a existing connection to the database.  Create one if existing one
     * doesn't exist.
     *
     * @return a connection to the database.
     */
    public static Connection getConnection() throws SQLException
    {
        if (dbConnection == null)
        {
            dbConnection = establishConnection();
        }

        return dbConnection;
    }
    private static Connection dbConnection = null;

    /**
     * Recreates a structure based on data from the database.
     *
     * @param config configuration object for mapped transducer.
     * @return structure based on data from the database.
     */
    public static Structure restoreFromDb (SqlTableMap config)
    {
        MappedTransducer mt = makeTransducer(config);
        mt.openConnection();
        Structure s = mt.readFromDb(null);
        mt.closeConnection();
        return s;
    }

    /**
     * Creates a tree model (viewable by a JTree) based on the contents of the
     * given structure.
     *
     * @param s structure on which to base contents of tree model
     * @return tree model based on the contents of the given structure.
     */
    public static DefaultMutableTreeNode createTree(Structure s)
    {
        JTreeInterpreter jti = new JTreeInterpreter();
        DefaultMutableTreeNode dmtn =
            (DefaultMutableTreeNode)jti.generate(s).getModel().getRoot();
        trimUIDs(dmtn);
        return dmtn;
    }

    /**
     * Searches through a table for each of a list of search values.  Returns
     * a list of values from the result columns that are found.  One result
     * value is returned for each search value.
     *
     * @param table        name of table to search through
     * @param searchColumn name of column to use as search column
     * @param resultColumn name of column to use as result column
     * @param searchValues values to search for in search column
     * @return vector of result strings
     */
    public static Vector
        lookupValues(String table, String searchColumn, String resultColumn,
                     Enumeration searchValues)
    {
        Vector values = new Vector();
        Connection con = null;

        try
        {
            con = getConnection();
            while(searchValues.hasMoreElements())
            {
                values.add(lookupValue(con, table, searchColumn, resultColumn,
                                       searchValues.nextElement().toString()));
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                //if (con != null) con.close();
            }
            catch(Exception e){/*I tried*/}
        }

        return values;
    }

    /**
     * Returns a vector of values from a single column of a table
     *
     * @param table        name of table to search through
     * @param resultColumn name of column to get values from
     */
    public static Vector
        lookupValues(String table, String resultColumn)
    {
        return  lookupValues(table, null, resultColumn, (String)null);
    }

    /**
     * Searches through a table for a single search value in a specified
     * search column.  Returns a list of values from the result column of the
     * rows that are found.
     *
     * @param table        name of table to search through
     * @param searchColumn name of column to use as search column
     * @param resultColumn name of column to use as result column
     * @param searchValue  value to search for in search column
     * @return vector of result strings
     */
    public static Vector
        lookupValues(String table, String searchColumn, String resultColumn,
                     String searchValue)
    {
        Vector values = null;
        Connection con = null;

        try
        {
            con = getConnection();
            values = lookupValues(con, table, searchColumn,
                                  resultColumn, searchValue);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                //if (con != null) con.close();
            }
            catch(Exception e){/*I tried*/}
        }

        return values;
    }

    /**
     * Searches through a table for a single search value in a specified
     * search column.  Returns a single values from the result column of the
     * first row that is found.
     *
     * @param table        name of table to search through
     * @param searchColumn name of column to use as search column
     * @param resultColumn name of column to use as result column
     * @param searchValue  value to search for in search column
     * @return result data string
     */
    public static String lookupValue(String table, String searchColumn,
                                     String resultColumn, String searchValue)
    {
        return (String)lookupValues(table, searchColumn,
                                    resultColumn, searchValue).firstElement();
    }

    /**
     * Searches through a table for a single search value in a specified
     * search column.  Returns a single values from the result column of the
     * first row that is found.
     *
     * @param con          connection to database
     * @param table        name of table to search through
     * @param searchColumn name of column to use as search column
     * @param resultColumn name of column to use as result column
     * @param searchValue  value to search for in search column
     * @return result data string
     */
    private static String lookupValue(Connection con, String table,
                                      String searchColumn, String resultColumn,
                                      String searchValue)
    {
        return (String)lookupValues(con, table, searchColumn,
                                    resultColumn, searchValue).firstElement();
    }

    private static Vector
        lookupValues(Connection con, String table, String searchColumn,
                    String resultColumn, String searchValue)
    {
        Vector values = new Vector();
        Statement stmt = null;
        ResultSet rs = null;

        try
        {
            stmt = con.createStatement();
            StringBuffer query = new StringBuffer("SELECT ");
            query.append(resultColumn + " FROM " + table);
            if ((searchColumn != null) && (searchValue != null))
            {
                if (!searchColumn.equalsIgnoreCase("id"))
                {
                    searchValue = "'" + searchValue + "'";
                }

                query.append(" WHERE (" + searchColumn +
                             "=" + searchValue + ")");
            }
            //System.out.println("lookup query: " + query);
            rs = stmt.executeQuery(query.toString());
            while (rs.next())
            {
                values.add(rs.getString(resultColumn).trim());
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
            }
            catch(Exception e){/*I tried*/}
        }

        return values;
    }

    private static void trimUIDs(DefaultMutableTreeNode dmtn)
    {
        dmtn.setUserObject(dmtn.getUserObject().toString().trim());
        for (int i=0; i<dmtn.getChildCount(); i++)
        {
            trimUIDs((DefaultMutableTreeNode)dmtn.getChildAt(i));
        }
    }

    private static MappedTransducer makeTransducer (SqlTableMap config)
    {
        MappedTransducer mt = new MappedTransducer(DBDRIVER, config);
        mt.setDbParams(DBURL, DBUSER, DBPASSWORD);
        return mt;
    }
}