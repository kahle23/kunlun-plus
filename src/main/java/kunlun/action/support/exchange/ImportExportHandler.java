/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package kunlun.action.support.exchange;

import kunlun.core.handler.AsyncSupportedHandler;

import java.util.Date;
import java.util.List;

/**
 * The import and export generic handler.
 * @param <P> The type of the import or export parameter
 * @param <D> The type of data fetched or parsed
 * @author Kahle
 */
public interface ImportExportHandler<P, D> extends AsyncSupportedHandler {

    /**
     * Push import or export task information.
     * @param context The import or export context
     */
    void pushTask(ImportExportContext<P> context);

    /**
     * Before write data
     * @param context The import or export context
     */
    void preHandle(ImportExportContext<P> context);

    /**
     * Handle the fetched or parsed data.
     * For export, this method simply writes data to the file object to be exported.
     * For import, this method will contain the logic for data validation, data writing, and logging.
     * @param context The import or export context
     * @param cursor The cursor for this data ("pageNum" or "scrollId" or "index" or null)
     * @param data The fetched or parsed data
     * @return The handle result or null
     */
    Object handle(ImportExportContext<P> context, Object cursor, List<D> data);

    /**
     * Save the data (biased to IO write).
     * @param context The import or export context
     * @return The save result or null
     */
    Object save(ImportExportContext<P> context);

    /**
     * The import and export generic context.
     * @param <P> The type of the import or export parameter
     * @author Kahle
     */
    class ImportExportContext<P> extends AsyncSupportContext {
        /* The common parameters */
        /**
         * The parameter for import or export.
         */
        private P param;
        /**
         * The import or export configuration.
         */
        private Object config;
        /**
         * An error occurred while importing or exporting.
         */
        private Exception error;
        /**
         * The class of the result returned by this function.
         */
        private Class<?> resultClass;
        /* The common parameters */


        /* The task information */
        /**
         * The identifier of the import or export task.
         */
        private String taskId;
        /**
         * The module identifier of the import or export function.
         */
        private String module;
        /**
         * The import status: 0 unknown, 1 will import, 2 importing, 3 processing, 4 timeout(dead), 5 failure, 6 success
         * The export status: 0 unknown, 1 will export, 2 exporting, 3 processing, 4 timeout(dead), 5 failure, 6 success
         */
        private Integer status;
        /**
         * The begin time of the task.
         */
        private Date beginTime;
        /**
         * The end time of the task.
         */
        private Date endTime;
        /**
         * The total number of items to be imported or exported.
         */
        private Long totalCount;
        /**
         * The number of successful imports or exports.
         */
        private Long successCount;
        /**
         * The number of import or export failures.
         */
        private Long failureCount;
        /**
         * Import or export the result address
         */
        private String resultAddress;
        /**
         * Import or export the result message
         */
        private String resultMessage;
        /* The task information */

        public P getParam() {

            return param;
        }

        public void setParam(P param) {

            this.param = param;
        }

        public Object getConfig() {

            return config;
        }

        public void setConfig(Object config) {

            this.config = config;
        }

        public Exception getError() {

            return error;
        }

        public void setError(Exception error) {

            this.error = error;
        }

        public Class<?> getResultClass() {

            return resultClass;
        }

        public void setResultClass(Class<?> resultClass) {

            this.resultClass = resultClass;
        }

        public String getTaskId() {

            return taskId;
        }

        public void setTaskId(String taskId) {

            this.taskId = taskId;
        }

        public String getModule() {

            return module;
        }

        public void setModule(String module) {

            this.module = module;
        }

        public Integer getStatus() {

            return status;
        }

        public void setStatus(Integer status) {

            this.status = status;
        }

        public Date getBeginTime() {

            return beginTime;
        }

        public void setBeginTime(Date beginTime) {

            this.beginTime = beginTime;
        }

        public Date getEndTime() {

            return endTime;
        }

        public void setEndTime(Date endTime) {

            this.endTime = endTime;
        }

        public Long getTotalCount() {

            return totalCount;
        }

        public void setTotalCount(Long totalCount) {

            this.totalCount = totalCount;
        }

        public Long getSuccessCount() {

            return successCount;
        }

        public void setSuccessCount(Long successCount) {

            this.successCount = successCount;
        }

        public Long getFailureCount() {

            return failureCount;
        }

        public void setFailureCount(Long failureCount) {

            this.failureCount = failureCount;
        }

        public String getResultAddress() {

            return resultAddress;
        }

        public void setResultAddress(String resultAddress) {

            this.resultAddress = resultAddress;
        }

        public String getResultMessage() {

            return resultMessage;
        }

        public void setResultMessage(String resultMessage) {

            this.resultMessage = resultMessage;
        }

    }

}
