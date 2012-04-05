package org.nepean.street

import grails.converters.JSON
import org.codehaus.groovy.grails.web.json.JSONArray
import org.codehaus.groovy.grails.web.json.JSONObject

class JSONMetaClassBootStrap {
  def init = { ctx ->

    JSON.metaClass.static.parse = { List other ->
      new JSONArray(other.collect({ entry ->
        (Map.isAssignableFrom(entry.getClass()) || List.isAssignableFrom(entry.getClass())) ? JSON.parse(entry) : entry
      }))
    }

    JSON.metaClass.static.parse = { Map other ->
      new JSONObject(other).each({ entry ->
        entry.value = (Map.isAssignableFrom(entry.value.getClass()) || List.isAssignableFrom(entry.value.getClass())) ? JSON.parse(entry.value) : entry.value
      })
    }

  }
}
