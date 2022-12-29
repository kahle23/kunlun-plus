package artoria.action.handler;

import artoria.action.ActionHandler;

import java.util.List;

/**
 * The import handler.
 * @param <P> The import parameter type
 * @param <D> The type of data parsed
 * @author Kahle
 */
public interface ImportHandler<P, D> extends ActionHandler, ImportExportHandler<P, D> {

    List<D> parseData(ImportContext<P> context, Object cursor, Object rawData);

    class ImportContext<P> extends ImportExportContext<P> {
        private Object fileData;
        private String filename;
        private Long   fileSize;
        private String contentType;

        public Object getFileData() {

            return fileData;
        }

        public void setFileData(Object fileData) {

            this.fileData = fileData;
        }

        public String getFilename() {

            return filename;
        }

        public void setFilename(String filename) {

            this.filename = filename;
        }

        public Long getFileSize() {

            return fileSize;
        }

        public void setFileSize(Long fileSize) {

            this.fileSize = fileSize;
        }

        public String getContentType() {

            return contentType;
        }

        public void setContentType(String contentType) {

            this.contentType = contentType;
        }

    }

}
