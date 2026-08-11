package baibao.extension.tool.weather.support.weathercomcn;

import baibao.extension.tool.weather.Weather;
import baibao.extension.tool.weather.WeatherQuery;
import kunlun.action.ActionUtil;
import kunlun.data.json.JsonFormat;
import kunlun.data.json.JsonUtil;
import kunlun.data.json.support.FastJsonProcessor;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Ignore
public class WeatherComCnActionTest {
    private static final Logger log = LoggerFactory.getLogger(WeatherComCnActionTest.class);
    private static final String WEATHER_NAME = "WeatherComCn";

    static {
        JsonUtil.registerProcessor(JsonUtil.getDefaultProcessorName(), new FastJsonProcessor());
        ActionUtil.registerAction(WEATHER_NAME, new WeatherComCnAction());
        ActionUtil.registerShortcut(WeatherQuery.class, WEATHER_NAME);
    }

    @Test
    public void testGetLocationCode() {
        log.info(new WeatherComCnAction().getLocationCode("上海市"));
        log.info(new WeatherComCnAction().getLocationCode("杨浦区"));
        log.info(new WeatherComCnAction().getLocationCode("江苏省"));
    }

    @Test
    public void testWeather() {
        Weather weather = ActionUtil.execute(WEATHER_NAME, new WeatherQuery("上海市"));
        log.info(JsonUtil.toJsonString(weather, JsonFormat.PRETTY_FORMAT));
    }

}
