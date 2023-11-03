package artoria.data.statistic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.net.URL;
import java.util.Enumeration;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

//@Configuration
public class JarInfoRecordAutoConfiguration {
    private static Logger log = LoggerFactory.getLogger(JarInfoRecordAutoConfiguration.class);

    @Autowired
    public JarInfoRecordAutoConfiguration() {
    }

    private void info() {
        try {
            Enumeration<URL> resources = getClass().getClassLoader().getResources("META-INF/MANIFEST.MF");
            while (resources.hasMoreElements()) {
                URL url = resources.nextElement();
                Manifest manifest = new Manifest(url.openStream());
                Attributes attrs = manifest.getMainAttributes();
                if (attrs == null) {
                    continue;
                }
                String watched = attrs.getValue("Watched");

                String implTitle = attrs.getValue("Implementation-Title");
                String implVersion = attrs.getValue("Implementation-Version");
                String implVendorId = attrs.getValue("Implementation-Vendor-Id");
                String implVendor = attrs.getValue("Implementation-Vendor");

                String bundleName = attrs.getValue("Bundle-Name");
                String bundleVersion = attrs.getValue("Bundle-Version");
                String bundleVendor = attrs.getValue("Bundle-Vendor");
                String bundleDescription = attrs.getValue("Bundle-Description");

                if ("true".equalsIgnoreCase(watched)) {
                    log.info("implTitle={}, implVersion={}, implVendorId={}, implVendor={}"
                            , implTitle, implVersion, implVendorId, implVendor);
                    log.info("bundleName={}, bundleVersion={}, bundleVendor={}, bundleDescription={}"
                            , bundleName, bundleVersion, bundleVendor, bundleDescription);
                }
            }
        } catch (Exception e) {
            // skip it
        }
    }

    private JarInfoImpl createJarInfo(Attributes attributes) {
        if (attributes == null) { return null; }
        // Create object.
        JarInfoImpl jarInfo = new JarInfoImpl();
        jarInfo.setWatched(attributes.getValue("Watched"));
        // Bundle info.
        jarInfo.setBundleName(attributes.getValue("Bundle-Name"));
        jarInfo.setBundleVersion(attributes.getValue("Bundle-Version"));
        jarInfo.setBundleVendor(attributes.getValue("Bundle-Vendor"));
        jarInfo.setBundleDescription(attributes.getValue("Bundle-Description"));
        // Implementation info.
        jarInfo.setImplementationTitle(attributes.getValue("Implementation-Title"));
        jarInfo.setImplementationUrl(attributes.getValue("Implementation-URL"));
        jarInfo.setImplementationVersion(attributes.getValue("Implementation-Version"));
        jarInfo.setImplementationVendorId(attributes.getValue("Implementation-Vendor-Id"));
        jarInfo.setImplementationVendor(attributes.getValue("Implementation-Vendor"));
        // Return result.
        return jarInfo;
    }

}
