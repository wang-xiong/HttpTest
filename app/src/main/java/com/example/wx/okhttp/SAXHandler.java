package com.example.wx.okhttp;

import android.util.Log;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Created by wx on 2018/3/28.
 */

public class SAXHandler extends DefaultHandler {
    private String mNodeName;
    private StringBuilder id;
    private StringBuilder name;
    private StringBuilder version;
    @Override
    public void startDocument() throws SAXException {
        id = new StringBuilder();
        name = new StringBuilder();
        version = new StringBuilder();
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        mNodeName = localName;
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        if ("id".equals(mNodeName)) {
            id.append(ch, start, length);
        } else if("name".equals(mNodeName)) {
            name.append(ch,start,length);
        } else if("version".equals(mNodeName)) {
            version.append(ch, start,length);
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if("app".equals(localName)) {
            Log.d("wx", "id is " + id + "name" + name + "version" +version);
            id.setLength(0);
            name.setLength(0);
            version.setLength(0);
        }
    }

    @Override
    public void endDocument() throws SAXException {
        super.endDocument();
    }
}
