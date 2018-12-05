package artoria.document.common;

import artoria.util.Assert;
import org.apache.commons.io.IOUtils;

import java.io.*;

/**
 * Default image extractor.
 * @author Kahle
 */
public class DefaultImageExtractor implements ImageExtractor {
    private final File baseDir;

    public DefaultImageExtractor(File baseDir) {
        Assert.notNull(baseDir, "Parameter \"baseDir\" must not null. ");
        this.baseDir = baseDir;
    }

    @Override
    public void extract(String imagePath, byte[] imageData) throws IOException {
        File imageFile = new File(this.baseDir, imagePath);
        File parentFile = imageFile.getParentFile();
        if (!parentFile.exists() && !parentFile.mkdirs()) {
            throw new IOException("Create directory \"" + parentFile + "\" failure. ");
        }
        InputStream in = null;
        OutputStream out = null;
        try {
            in = new ByteArrayInputStream(imageData);
            out = new FileOutputStream(imageFile);
            IOUtils.copyLarge(in, out);
        }
        finally {
            IOUtils.closeQuietly(in);
            IOUtils.closeQuietly(out);
        }
    }

}
