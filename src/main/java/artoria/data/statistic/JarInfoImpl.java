package artoria.data.statistic;

import artoria.data.JarInfo;
import artoria.util.Assert;

import java.util.jar.Attributes;

/**
 * The jar information implementation class.
 * @author Kahle
 */
public class JarInfoImpl extends JarInfo {
    private String watched;

    private String implementationTitle;
    private String implementationUrl;
    private String implementationVersion;
    private String implementationVendorId;
    private String implementationVendor;

    private void fillInfo(JarInfoImpl jarInfo, Attributes attributes) {
        Assert.notNull(attributes, "Parameter \"attributes\" must not null. ");
        Assert.notNull(jarInfo, "Parameter \"jarInfo\" must not null. ");
        // The watched information.
        jarInfo.setWatched(attributes.getValue("Watched"));
        // The bundle information.
        jarInfo.setBundleName(attributes.getValue("Bundle-Name"));
        jarInfo.setBundleVersion(attributes.getValue("Bundle-Version"));
        jarInfo.setBundleVendor(attributes.getValue("Bundle-Vendor"));
        jarInfo.setBundleDescription(attributes.getValue("Bundle-Description"));
        // The implementation information.
        jarInfo.setImplementationTitle(attributes.getValue("Implementation-Title"));
        jarInfo.setImplementationUrl(attributes.getValue("Implementation-URL"));
        jarInfo.setImplementationVersion(attributes.getValue("Implementation-Version"));
        jarInfo.setImplementationVendorId(attributes.getValue("Implementation-Vendor-Id"));
        jarInfo.setImplementationVendor(attributes.getValue("Implementation-Vendor"));
    }

    public JarInfoImpl() {

    }

    public JarInfoImpl(Attributes attributes) {

        fillInfo(this, attributes);
    }

    public String getWatched() {

        return watched;
    }

    public void setWatched(String watched) {

        this.watched = watched;
    }

    public String getImplementationTitle() {

        return implementationTitle;
    }

    public void setImplementationTitle(String implementationTitle) {

        this.implementationTitle = implementationTitle;
    }

    public String getImplementationUrl() {

        return implementationUrl;
    }

    public void setImplementationUrl(String implementationUrl) {

        this.implementationUrl = implementationUrl;
    }

    public String getImplementationVersion() {

        return implementationVersion;
    }

    public void setImplementationVersion(String implementationVersion) {

        this.implementationVersion = implementationVersion;
    }

    public String getImplementationVendorId() {

        return implementationVendorId;
    }

    public void setImplementationVendorId(String implementationVendorId) {

        this.implementationVendorId = implementationVendorId;
    }

    public String getImplementationVendor() {

        return implementationVendor;
    }

    public void setImplementationVendor(String implementationVendor) {

        this.implementationVendor = implementationVendor;
    }

    @Override
    public String toString() {
        return "JarInfo{" +
                "bundleName='" + getBundleName() + '\'' +
                ", bundleVersion='" + getBundleVersion() + '\'' +
                ", bundleVendor='" + getBundleVendor() + '\'' +
                ", bundleDescription='" + getBundleDescription() + '\'' +
                ", watched='" + watched + '\'' +
                ", implementationTitle='" + implementationTitle + '\'' +
                ", implementationUrl='" + implementationUrl + '\'' +
                ", implementationVersion='" + implementationVersion + '\'' +
                ", implementationVendorId='" + implementationVendorId + '\'' +
                ", implementationVendor='" + implementationVendor + '\'' +
                '}';
    }

}
