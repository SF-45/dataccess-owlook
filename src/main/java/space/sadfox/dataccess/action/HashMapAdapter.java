package space.sadfox.dataccess.action;

import java.util.HashMap;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;
import javafx.beans.property.StringProperty;

public class HashMapAdapter extends XmlAdapter<HashMapType, HashMap<String, StringProperty>> {

    @Override
    public HashMap<String, StringProperty> unmarshal(HashMapType hashMapType) throws Exception {
        HashMap<String, StringProperty> hashMap = new HashMap<>();
        for (HashMapEntry entry : hashMapType.property) {
            hashMap.put(entry.getKey(), entry.valueProperty());
        }
        return hashMap;
    }

    @Override
    public HashMapType marshal(HashMap<String, StringProperty> stringHashMap) throws Exception {
        HashMapType hashMapType = new HashMapType();

        stringHashMap.forEach((key, value) -> {
            hashMapType.property.add(new HashMapEntry(key, value));
        });

        return hashMapType;
    }
}
