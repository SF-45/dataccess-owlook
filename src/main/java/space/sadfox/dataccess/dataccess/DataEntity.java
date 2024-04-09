package space.sadfox.dataccess.dataccess;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DataEntity {

  private Map<Field, String> dataMap = new LinkedHashMap<>();

  public DataEntity(List<Field> fields) {
    fields.forEach(field -> {
      dataMap.put(field, "");
    });
  }

  public boolean setValue(Field field, String value) {
    String out = dataMap.replace(field, value);
    return out != "";
  }

  public boolean setValue(String field, String value) {
    return dataMap.replace(getField(field), value) != "";
  }

  public String getValue(Field field) {
    return dataMap.get(field);
  }

  public String getValue(String field) {
    Field findField = getField(field);
    if (findField != null) {
      return dataMap.get(findField);
    }
    return "";
  }

  public Set<Field> getFields() {
    return dataMap.keySet();
  }

  public Field getField(String fieldName) {
    for (Field thisField : getFields()) {
      if (thisField.getFieldName().equals(fieldName))
        return thisField;
    }
    return null;
  }

  public boolean findLike(String value) {
    for (String dataValue : dataMap.values()) {
      if (dataValue.toLowerCase().contains(value.toLowerCase()))
        return true;
    }
    return false;
  }

  public boolean validateValue(String fieldName, String value) {
    Field field = getField(fieldName);
    if (field == null)
      return false;

    boolean ind = field.getParserFilters().isEmpty();
    mark: for (ParserFilter filter : field.getParserFilters()) {

      switch (filter.getComparison()) {
        case EQUAL:
          if (value.equalsIgnoreCase(filter.getValue())) {
            ind = true;
            break mark;
          }
          break;
        case NOT_EQUAL:
          if (!value.equalsIgnoreCase(filter.getValue())) {
            ind = true;
            break mark;
          }
          break;
        case LIKE:
          if (value.toLowerCase().contains(filter.getValue().toLowerCase())) {
            ind = true;
            break mark;
          }
          break;
      }

    }
    return ind;
  }
}
