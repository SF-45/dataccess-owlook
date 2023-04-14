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
        if (!dataMap.containsKey(field)) return false;
        dataMap.replace(field, value);
        return true;
    }

    public String getValue(Field field) {
        return dataMap.get(field);
    }
    public String getValue(String field) {
        for (Field thisField : dataMap.keySet()) {
            if (thisField.getFieldName().equals(field)) return dataMap.get(thisField);
        }
        return "";
    }

    public Set<Field> getFields(){
        return dataMap.keySet();
    }

    public boolean findLike(String value){
        for (String dataValue : dataMap.values()) {
            if (dataValue.toLowerCase().contains(value.toLowerCase())) return true;
        }
        return false;
    }

	
    
    


}
