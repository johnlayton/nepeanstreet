package org.nepean.street

import com.jayway.jsonpath.JsonPath
import org.codehaus.groovy.grails.web.json.JSONArray
import org.codehaus.groovy.grails.web.json.JSONElement
import org.codehaus.groovy.grails.web.json.JSONObject

class JSONArrayMetaClassBootStrap {
  def init = { ctx ->

    JSONArray.metaClass.getCount = { delegate.size() }
    JSONArray.metaClass.setCount = {  }

    JSONArray.metaClass.mask = { ->
      new JSONArray(delegate.collect({ entry ->
        JSONElement.isAssignableFrom(entry.getClass()) ? entry.mask() : new JSONObject()
      }))
    }

    JSONArray.metaClass.read = { path ->
      JsonPath.read(delegate, path);
    }

    JSONArray.metaClass.findAll = { config ->
      delegate.findAll({ field ->
        field?.value && !field?.isNull('value') && !config['excluded'].contains(field?.label)
      })
    }

  }

}
