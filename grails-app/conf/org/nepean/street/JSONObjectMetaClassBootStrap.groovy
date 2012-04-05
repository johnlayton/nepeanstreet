package org.nepean.street

import com.jayway.jsonpath.JsonPath
import grails.converters.JSON
import org.apache.commons.lang.StringUtils
import org.codehaus.groovy.grails.web.json.JSONElement
import org.codehaus.groovy.grails.web.json.JSONObject

class JSONObjectMetaClassBootStrap {
  def init = { ctx ->

    JSONObject.metaClass.mask = { ->
      new JSONObject(delegate.collectEntries({ entry ->
        [(entry.key) : JSONElement.isAssignableFrom(entry.value.getClass()) ? entry.value.mask() : new JSONObject() ]
      }))
    }

    JSONObject.metaClass.minus = { JSONObject other ->
      new JSONObject(other).inject(JSON.parse(delegate.toString()), { result, prop ->
        if (result[prop.key].equals(prop.value))
          result.remove(prop.key)
        else if (result[prop.key] && JSONElement.isAssignableFrom(result[prop.key]?.getClass()))
          result[prop.key] = result[prop.key] - prop.value
        else
          log.info String.format("Unable to remove [%s] with value [%s]", prop.key, prop.value)
        result
      })
    }

    JSONObject.metaClass.minus = { Map other ->
      delegate - JSON.parse(other)
    }

    JSONObject.metaClass.plus = { JSONObject other ->
      new JSONObject(other).inject(JSON.parse(delegate.toString()), { result, prop ->
        if (result[prop.key] && JSONElement.isAssignableFrom(result[prop.key]?.getClass()))
          result[prop.key] = result[prop.key] + prop.value
        else
          result[prop.key] = prop.value
        result
      })
    }

    JSONObject.metaClass.plus = { Map other ->
      delegate + JSON.parse(other)
    }

    JSONObject.metaClass.leftShift = { JSONObject other ->
      new JSONObject(other).inject(delegate, { result, prop ->
        if (result[prop.key] && JSONElement.isAssignableFrom(result[prop.key]?.getClass()))
          result[prop.key] = result[prop.key] << prop.value
        else
          result[prop.key] = prop.value
        result
      })
    }

    JSONObject.metaClass.leftShift = { Map other ->
      delegate << JSON.parse(other)
    }

    JSONObject.metaClass.isBlank = { String key ->
      delegate.isNull(key) || StringUtils.isBlank(delegate.getString(key))
    }

    JSONObject.metaClass.isNotBlank = { String key -> !delegate.isBlank(key) }

    JSONObject.metaClass.read = { path ->
      JsonPath.read(delegate, path);
    }

  }
}
