package baibao.extension.xxljob;

import baibao.db.vector.support.pinecone.AbstractPineconeVectorDbHandler;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.log.XxlJobLogger;
import kunlun.action.ActionUtil;
import kunlun.data.Event;
import kunlun.data.json.JsonUtil;
import kunlun.exception.ExceptionUtil;
import kunlun.spring.ApplicationContextUtils;
import kunlun.time.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;

import static kunlun.common.constant.Env.HOST_NAME;

public abstract class AbstractXxlJobHandler extends IJobHandler {
    private static final Logger log = LoggerFactory.getLogger(AbstractPineconeVectorDbHandler.class);

    public void log(String format, Object... arguments) {
        log.info(format, arguments);
        XxlJobLogger.log(format, arguments);
        if (arguments != null && arguments.length > 0) {
            Object argument = arguments[arguments.length - 1];
            if (argument instanceof Throwable) {
                XxlJobLogger.log((Throwable) argument);
            }
        }
    }

    protected void log(Throwable e) {

        log("An error has occurred! ", e);
    }

    public void start(String jobName, Object jobParam, Object... arguments) {

        log("The job \"{}\" is start. And input is \"{}\". ", jobName, jobParam);
    }

    public void finish(String jobName, Object jobParam, Object... arguments) {

        log("The job \"{}\" is finish. ", jobName);
    }

    /**
     * 定时任务出错通知
     * @param jobName 定时任务的名称（类全名）
     * @param jobParam 定时任务的参数
     * @param ex 定时任务的异常对象
     */
    public void error(String jobName, Object jobParam, Throwable ex, Object... arguments) {
        log("An error has occurred for job \"{}\". ", jobName, ex);
        Class<? extends Throwable> exClass = ex!=null?ex.getClass():null;
        String exClassName = exClass!=null?exClass.getName():null;
        String exMessage = ex!=null?ex.getMessage():null;
        String paramStr = null;
        if (jobParam != null) {
            paramStr = jobParam instanceof String ? (String) jobParam : JsonUtil.toJsonString(jobParam);
        }
        Environment environment = ApplicationContextUtils.getContext().getEnvironment();
        String appName = environment.getProperty("spring.application.name");
        String active = environment.getProperty("spring.profiles.active");
        String msg = String.format("主机名称：%s\n" +
                        "应用名称：%s\n" +
                        "环境名称：%s\n" +
                        "定时任务：%s\n" +
                        "错误时间：%s\n" +
                        "查询参数：%s\n" +
                        "异常类型：%s\n" +
                        "错误信息：%s",
                HOST_NAME, appName, active, jobName, DateUtil.format(), paramStr, exClassName, exMessage);
        ActionUtil.execute(Event.ofOperationLog()
                .setLevel(Event.Level.ERROR)
                .appendMessage(msg)
                .appendError(ex != null ? ExceptionUtil.toString(ex) : ""));
    }

}
