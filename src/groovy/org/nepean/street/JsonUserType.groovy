package org.nepean.street

import grails.converters.JSON
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.SQLException
import java.sql.Types
import org.apache.commons.lang.StringUtils
import org.codehaus.groovy.grails.web.json.JSONElement
import org.codehaus.groovy.grails.web.json.JSONException
import org.codehaus.groovy.grails.web.json.JSONObject
import org.hibernate.usertype.UserType

class JsonUserType implements UserType, Serializable {

  private static final SQL_TYPES = [Types.VARCHAR] as int[]

  @Override
  public Object nullSafeGet(ResultSet rs, String[] names, Object owner) throws SQLException {
    String value = rs.getString(names[0])
    if (!rs.wasNull() && StringUtils.isNotEmpty(value)) {
      try {
        return JSON.parse(value)
      } catch (JSONException e) {
        throw new RuntimeException(e)
      }
    }
    return new JSONObject()
  }

  @Override
  public void nullSafeSet(PreparedStatement st, Object value, int index) throws SQLException {
    if (value == null) {
      st.setNull(index, SQL_TYPES[0])
    } else {
      st.setString(index, ((JSONElement) value).toString())
    }
  }

  @Override
  public int[] sqlTypes() {
    return SQL_TYPES
  }

  @Override
  @SuppressWarnings("unchecked")
  public Class returnedClass() {
    return JSONElement.class
  }

  @Override
  public Object assemble(Serializable cached, Object owner){
    return deepCopy(cached)
  }

  @Override
  public Object deepCopy(Object value){
    if (value == null){
      return value
    }
    try {
      return JSON.parse(((JSONElement) value).toString())
    } catch (JSONException e) {
      throw new RuntimeException(e)
    }
  }

  @Override
  public Serializable disassemble(Object value)  {
    return ((JSONElement) value).toString()
  }

  @Override
  public boolean equals(Object x, Object y)  {
    if (x == null) {
      return (y != null)
    }
    return (x.equals(y))
  }

  @Override
  public int hashCode(Object x)  {
    return ((JSONElement) x).hashCode()
  }

  @Override
  public boolean isMutable() {
    return true
  }

  @Override
  public Object replace(Object original, Object target, Object owner)  {
    return deepCopy(original)
  }
}