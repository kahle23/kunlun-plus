/*
 * Copyright (c) 2019. the original author or authors.
 * BaiBao is licensed under the "LICENSE" file in the project's root directory.
 */

package baibao.extension.tool.weather.support.weathercomcn;

import baibao.extension.tool.weather.AbstractWeatherAction;
import baibao.extension.tool.weather.Weather;
import baibao.extension.tool.weather.WeatherQuery;
import kunlun.data.Dict;
import kunlun.data.json.JsonUtil;
import kunlun.exception.ExceptionUtil;
import kunlun.net.http.HttpMethod;
import kunlun.net.http.HttpUtil;
import kunlun.net.http.support.SimpleRequest;
import kunlun.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

import static kunlun.common.constant.Charsets.STR_UTF_8;
import static kunlun.common.constant.Numbers.ONE;
import static kunlun.common.constant.Numbers.ZERO;
import static kunlun.common.constant.Symbols.EMPTY_STRING;

/**
 * weather.com.cn
 * @see <a href="https://www.weather.com.cn/">天气网</a>
 * @author Kahle
 */
public class WeatherComCnAction extends AbstractWeatherAction {
    private static final Logger log = LoggerFactory.getLogger(WeatherComCnAction.class);

    public String getLocationCode(String cityName) {
        String methodName = "jQuery18204609393285561594_1745462881962";
        cityName = Assert.notBlank(cityName).trim();
        try {
            // 这个接口支持有后缀的名称，所以需要切掉
            if (cityName.endsWith("市") || cityName.endsWith("省")
                    || cityName.endsWith("区") || cityName.endsWith("县") || cityName.endsWith("镇")) {
                cityName = cityName.substring(ZERO, cityName.length() - ONE);
            }
            // 构建请求，并调用接口
            cityName = URLEncoder.encode(cityName, STR_UTF_8);
            String url = "https://toy1.weather.com.cn/search?cityname=" + cityName + "&callback=" + methodName;
            SimpleRequest request = SimpleRequest.of(HttpMethod.GET, url);
            request.addHeader("referer", "https://www.weather.com.cn/");
            String bodyAsString = HttpUtil.execute(request).getBodyAsString();
            if (StrUtil.isBlank(bodyAsString)) { return null; }
            // 结果处理
            bodyAsString = bodyAsString.replace(methodName + "(", EMPTY_STRING);
            bodyAsString = bodyAsString.substring(ZERO, bodyAsString.length() - ONE);
            bodyAsString = bodyAsString.trim();
            List<Map<String, Object>> list = JsonUtil.parseObject(bodyAsString, List.class);
            if (CollUtil.isEmpty(list)) { return null; }
            Map<String, Object> first = CollUtil.getFirst(list);
            if (MapUtil.isEmpty(first)) { return null; }
            if (ObjUtil.isEmpty(first.get("ref"))) { return null; }
            String ref = String.valueOf(first.get("ref"));
            log.info("The full location information: {}", ref);
            int indexOf = ref.indexOf("~");
            if (indexOf < ZERO) { return null; }
            return ref.substring(ZERO, indexOf);
        } catch (Exception e) { throw ExceptionUtil.wrap(e); }
    }

    @Override
    protected Weather doQuery(WeatherQuery weatherQuery) {
        // 参数预处理（+获取位置编码）
        String locationCode = weatherQuery.getLocationCode();
        String location = weatherQuery.getLocation();
        if (StrUtil.isBlank(locationCode)) {
            if (StrUtil.isBlank(location)) { return null; }
            locationCode = getLocationCode(location);
        }
        if (StrUtil.isBlank(locationCode)) { return null; }
        locationCode = locationCode.trim();
        // 构建请求，并调用接口
        String url = "https://d1.weather.com.cn/dingzhi/"+locationCode+".html";
        SimpleRequest request = SimpleRequest.of(HttpMethod.GET, url);
        request.addHeader("referer", "https://www.weather.com.cn/");
        String bodyAsString = HttpUtil.execute(request).getBodyAsString();
        log.info("The raw weather information: {}", bodyAsString);
        if (StrUtil.isBlank(bodyAsString)) { return null; }
        // 切除 JS 相关内容，提取出 JSON 字符串
        bodyAsString = bodyAsString.replace("var cityDZ"+locationCode+" ={\"weatherinfo\":", EMPTY_STRING);
        int indexOf = bodyAsString.indexOf("};");
        if (indexOf < ZERO) { return null; }
        bodyAsString = bodyAsString.substring(ZERO, indexOf);
        if (StrUtil.isBlank(bodyAsString)) { return null; }
        bodyAsString = bodyAsString.trim();
        // 构建结果对象
        Map<String, Object> map = JsonUtil.parseObject(bodyAsString, Map.class);
        Dict dict = Dict.of(map);
        Weather weather = new Weather();
        weather.setLocation(dict.getString("city"));
        weather.setLocationCode(dict.getString("cityname"));
        weather.setMainWeatherText(dict.getString("weather"));
        weather.setMainWeatherCode(dict.getString("weathercode"));
        weather.setMinorWeatherCode(dict.getString("weathercoden"));
        weather.setMinTemperature(dict.getString("tempn"));
        weather.setMaxTemperature(dict.getString("temp"));
        weather.setWindDirection(dict.getString("wd"));
        weather.setWindSpeed(dict.getString("ws"));
        weather.setWeatherTime(dict.getString("fctime"));
        return weather;
    }

}
