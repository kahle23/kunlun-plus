/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package kunlun.data.xml.support;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.converters.reflection.PureJavaReflectionProvider;
import com.thoughtworks.xstream.core.util.QuickWriter;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.naming.NameCoder;
import com.thoughtworks.xstream.io.xml.PrettyPrintWriter;
import com.thoughtworks.xstream.io.xml.XppDriver;
import kunlun.data.xml.XmlClassAlias;
import kunlun.data.xml.XmlFieldAlias;
import kunlun.util.ClassLoaderUtils;
import kunlun.util.ObjectUtils;

import java.io.Writer;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import static kunlun.common.constant.Numbers.TWENTY;

/**
 * The xml conversion handler based on XStream.
 * @author Kahle
 */
public class XStreamXmlHandler extends AbstractXmlHandler {
    private static final XppDriver XPP_DRIVER = new InternalXppDriver();

    private XStream createXStream() {
        XStream xStream = new XStream(new PureJavaReflectionProvider(), XPP_DRIVER);
        xStream.ignoreUnknownElements();
        xStream.setMode(XStream.NO_REFERENCES);
        XStream.setupDefaultSecurity(xStream);
        String[] patterns = new String[]{"kunlun.**"};
        xStream.allowTypesByWildcard(patterns);
        ClassLoader classLoader = ClassLoaderUtils.getDefaultClassLoader();
        xStream.setClassLoader(classLoader);
        // Register converter.
        xStream.registerConverter(new MapEntryConverter());
        return xStream;
    }

    private void registerAlias(XStream xStream, Object... arguments) {
        for (Object arg : arguments) {
            if (arg instanceof XmlClassAlias) {
                XmlClassAlias alias = (XmlClassAlias) arg;
                xStream.alias(alias.getAlias(), alias.getType());
            }
            else if (arg instanceof XmlFieldAlias) {
                XmlFieldAlias alias = (XmlFieldAlias) arg;
                xStream.aliasField(
                        alias.getAlias(), alias.getType(), alias.getField()
                );
            }
            //else {
            //}
        }
    }

    @Override
    public String toXmlString(Object object, Object... arguments) {
        XStream xStream = createXStream();
        Class<?> clazz = object.getClass();
        xStream.processAnnotations(clazz);
        registerAlias(xStream, arguments);
        return xStream.toXML(object);
    }

    @Override
    public <T> T parseObject(String xmlString, Type type, Object... arguments) {
        XStream xStream = createXStream();
        xStream.processAnnotations((Class) type);
        registerAlias(xStream, arguments);
        Object fromXml = xStream.fromXML(xmlString);
        return ObjectUtils.cast(fromXml);
    }

    private static class MapEntryConverter implements Converter {

        @Override
        public boolean canConvert(Class clazz) {

            return Map.class.isAssignableFrom(clazz);
        }

        @Override
        public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
            Map map = (Map) source;
            for (Object obj : map.entrySet()) {
                Map.Entry entry = (Map.Entry) obj;
                writer.startNode(entry.getKey().toString());
                Object val = entry.getValue();
                if ( null != val ) {
                    writer.setValue(val.toString());
                }
                writer.endNode();
            }

        }


        @Override
        public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {

            Map<String, String> map = new HashMap<String, String>(TWENTY);

            while(reader.hasMoreChildren()) {
                reader.moveDown();
                // nodeName aka element's name
                String key = reader.getNodeName();
                String value = reader.getValue();
                map.put(key, value);

                reader.moveUp();
            }

            return map;
        }

    }

    private static class InternalXppDriver extends XppDriver {
        @Override
        public HierarchicalStreamWriter createWriter(Writer out) {

            return new InternalPrettyPrintWriter(out, getNameCoder());
        }
    }

    private static class InternalPrettyPrintWriter extends PrettyPrintWriter {
        private static final String PREFIX_CDATA = "<![CDATA[";
        private static final String SUFFIX_CDATA = "]]>";
        private static final String PREFIX_MEDIA_ID = "<MediaId>";
        private static final String SUFFIX_MEDIA_ID = "</MediaId>";

        public InternalPrettyPrintWriter(Writer writer, NameCoder nameCoder) {

            super(writer, nameCoder);
        }

        @Override
        protected void writeText(QuickWriter writer, String text) {
            if (text.startsWith(PREFIX_CDATA) && text.endsWith(SUFFIX_CDATA)) {
                writer.write(text);
            }
            else if (text.startsWith(PREFIX_MEDIA_ID) && text.endsWith(SUFFIX_MEDIA_ID)) {
                writer.write(text);
            }
            else {
                super.writeText(writer, text);
            }
        }

        @Override
        public String encodeNode(String name) {

            return name;
        }

    }

}
