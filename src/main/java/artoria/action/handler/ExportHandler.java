package artoria.action.handler;

import artoria.action.ActionHandler;

import java.util.List;

/**
 * The export handler.
 * @param <P> The export parameter type
 * @param <D> The type of data obtained
 * @author Kahle
 */
public interface ExportHandler<P, D> extends ActionHandler, ImportExportHandler<P, D> {

    /**
     * Obtain the data to be exported by paging.
     * @param context The export context
     * @param pageId The page identifier ("pageNum" or "scrollId")
     * @return The queried data
     */
    List<D> getData(ExportContext<P> context, Object pageId);

    /**
     * The export context.
     * @param <P> The export parameter type
     * @author Kahle
     */
    class ExportContext<P> extends ImportExportContext<P> {
        /**
         * The page size at each time the data is fetched.
         */
        private Integer pageSize;

        public Integer getPageSize() {

            return pageSize;
        }

        public void setPageSize(Integer pageSize) {

            this.pageSize = pageSize;
        }

    }

}
