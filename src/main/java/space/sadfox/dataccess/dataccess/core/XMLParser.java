package space.sadfox.dataccess.dataccess.core;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import jakarta.xml.bind.JAXBException;
import space.sadfox.dataccess.dataccess.DataEntity;
import space.sadfox.dataccess.dataccess.Field;
import space.sadfox.dataccess.dataccess.ParserFilter;
import space.sadfox.dataccess.dataccess.TableData;
import space.sadfox.dataccess.dataccess.TableDataDao;

public class XMLParser {
    private Document document;
    private TableDataDao tableDataDao;

    public XMLParser(Path path) throws IOException, SAXException, ParserConfigurationException {
        if (!Files.exists(path)) throw new IOException("Файл не найден" + path);
        if (Files.isDirectory(path)) throw new IOException("Файл не найден" + path);
        document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(path.toFile());
    }

    public List<DataEntity> parse(TableData tData) throws JAXBException {
    	tableDataDao = new TableDataDao(tData);
        List<DataEntity> rez = new ArrayList<>();
        NodeList nodeDetails = document.getDocumentElement().getElementsByTagName("Details");
        for (int i=0; i< nodeDetails.getLength(); i++) {
            Node detail = nodeDetails.item(i);
            if (checkParseProperty(detail, tData)) {
                rez.add(parseNode(detail, tData));
            }
        }

        return rez;
    }

    private boolean checkParseProperty(Node node, TableData tData) {
        if (node.getNodeType() != Document.ELEMENT_NODE) return false;
        NamedNodeMap attrs = node.getAttributes();

        for (ParserFilter parserProperty : tData.getPrefilters()) {
            Node attr = attrs.getNamedItem(parserProperty.getAttr());
            String attrValue;
            if (attr == null) {
                attrValue = "";
            } else {
                attrValue = attr.getNodeValue();
            }
            if (attrValue == null) continue;

            boolean ind = false;
            switch (parserProperty.getComparison()) {
                case EQUAL:
                    for (String str : parserProperty.getValue()) {
                        if (attrValue.equalsIgnoreCase(str)) {
                            ind = true;
                            break;
                        }
                    }
                    if (!ind) return false;
                    break;
                case NOT_EQUAL:
                    for (String str : parserProperty.getValue()) {
                        if (attrValue.equalsIgnoreCase(str)) {
                            ind = true;
                            break;
                        }
                    }
                    if (ind) return false;
                    break;
                case LIKE:
                    for (String str : parserProperty.getValue()) {
                        if (attrValue.toLowerCase().contains(str.toLowerCase())) {
                            ind = true;
                            break;
                        }
                    }
                    if (!ind) return false;
            }
        }
        return true;
    }

    private DataEntity parseNode(Node node, TableData tData) {
        DataEntity dataEntity = tableDataDao.createDataEntity();

        NamedNodeMap attrs = node.getAttributes();

        for (Field field : dataEntity.getFields()) {
            Node attr = attrs.getNamedItem(field.getParseAssociation());
            if (attr == null) continue;

            String attrValue = attr.getNodeValue();
            if (attrValue == null) continue;

            dataEntity.setValue(field, attrValue);
        }


        return dataEntity;
    }

}
