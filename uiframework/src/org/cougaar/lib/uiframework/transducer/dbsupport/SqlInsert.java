
package org.cougaar.lib.uiframework.transducer.dbsupport;

import java.util.Date;
import java.text.*;

/**
 *  An SqlInsert is a helper for writing INSERT statements in SQL.  Field name
 *  and value pairs may be added to the statement using the methods of the
 *  superclass, SqlExecute.  When converted to String, the product is an INSERT
 *  statement in correctly formatted SQL.
 */
public class SqlInsert extends SqlExecute {
  private static final String headerPrefix = "INSERT into ";

  private ConjClause fields = null;
  private ConjClause values = null;

  /**
   *  Create a new SqlInsert instance for inserting rows into the database.
   *  A table name may be supplied at the time of creation.
   *  @param table the name of the table into which rows will be inserted.
   */
  public SqlInsert (String table) {
    super(headerPrefix + table, SPACE, false);
    fields = new ConjClause(null, COMMA, true);
    values = new ConjClause(null, COMMA, true);

    add(fields);
    add("values");
    add(values);
  }

  /**
   *  Specify the name of the table into which rows are to be inserted by this
   *  SqlInsert.
   *  @param t the table name
   */
  public void setTable (String t) {
    setHeader(headerPrefix + t);
  }

  /**
   *  Clear the SqlInsert in preparation for making another insertion.  Note
   *  that the table designation is not affected by this call, whereas all the
   *  fields and corresponding values are dropped.
   */
  public void clear () {
    fields.clear();
    values.clear();
  }

  /**
   *  Add a field and an exact String expression to the SQL INSERT statement
   *  @param field the name of the field being assigned a value
   *  @param val the SQL expression assigned to the given field
   */
  public void addLiteral (String field, String val) {
    fields.add(field);
    values.add(val);
  }
}
