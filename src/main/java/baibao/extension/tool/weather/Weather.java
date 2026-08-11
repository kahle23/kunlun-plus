/*
 * Copyright (c) 2019. the original author or authors.
 * BaiBao is licensed under the "LICENSE" file in the project's root directory.
 */

package baibao.extension.tool.weather;

import java.io.Serializable;

/**
 * 天气的数据对象.
 * @since 2025-04-23
 * @author Kahle
 */
public class Weather implements Serializable {

    // -------- 位置信息 --------
    /**
     * 位置名称
     */
    private String location;
    /**
     * 位置编码
     */
    private String locationCode;
    // -------- 位置信息 --------


    // -------- 天气信息 --------
    /**
     * 主要的天气现象的文字（比如白天天气、上午天气等）
     */
    private String mainWeatherText;
    /**
     * 主要的天气现象的编码
     */
    private String mainWeatherCode;
    /**
     * 次要的天气现象的文字（比如晚上天气、下午天气等）
     */
    private String minorWeatherText;
    /**
     * 次要的天气现象的编码
     */
    private String minorWeatherCode;
    // -------- 天气信息 --------


    // -------- 温度信息 --------
    /**
     * 最低温度
     */
    private String minTemperature;
    /**
     * 最高温度
     */
    private String maxTemperature;
    /**
     * 当前温度
     */
    private String temperature;
    /**
     * 体感温度
     */
    private String feelsLikeTemperature;
    // -------- 温度信息 --------


    // -------- 风向风速 --------
    /**
     * 风向的文字
     */
    private String windDirection;
    /**
     * 风速
     */
    private String windSpeed;
    /**
     * 风力等级
     */
    private String windScale;
    // -------- 风向风速 --------


    // -------- 时间信息 --------
    /**
     * 天气的时间
     */
    private String weatherTime;
    /**
     * 天气的格式化时间
     */
    private String formattedTime;
    // -------- 时间信息 --------

    public String getLocation() {

        return location;
    }

    public void setLocation(String location) {

        this.location = location;
    }

    public String getLocationCode() {

        return locationCode;
    }

    public void setLocationCode(String locationCode) {

        this.locationCode = locationCode;
    }

    public String getMainWeatherText() {

        return mainWeatherText;
    }

    public void setMainWeatherText(String mainWeatherText) {

        this.mainWeatherText = mainWeatherText;
    }

    public String getMainWeatherCode() {

        return mainWeatherCode;
    }

    public void setMainWeatherCode(String mainWeatherCode) {

        this.mainWeatherCode = mainWeatherCode;
    }

    public String getMinorWeatherText() {

        return minorWeatherText;
    }

    public void setMinorWeatherText(String minorWeatherText) {

        this.minorWeatherText = minorWeatherText;
    }

    public String getMinorWeatherCode() {

        return minorWeatherCode;
    }

    public void setMinorWeatherCode(String minorWeatherCode) {

        this.minorWeatherCode = minorWeatherCode;
    }

    public String getMinTemperature() {

        return minTemperature;
    }

    public void setMinTemperature(String minTemperature) {

        this.minTemperature = minTemperature;
    }

    public String getMaxTemperature() {

        return maxTemperature;
    }

    public void setMaxTemperature(String maxTemperature) {

        this.maxTemperature = maxTemperature;
    }

    public String getTemperature() {

        return temperature;
    }

    public void setTemperature(String temperature) {

        this.temperature = temperature;
    }

    public String getFeelsLikeTemperature() {

        return feelsLikeTemperature;
    }

    public void setFeelsLikeTemperature(String feelsLikeTemperature) {

        this.feelsLikeTemperature = feelsLikeTemperature;
    }

    public String getWindDirection() {

        return windDirection;
    }

    public void setWindDirection(String windDirection) {

        this.windDirection = windDirection;
    }

    public String getWindSpeed() {

        return windSpeed;
    }

    public void setWindSpeed(String windSpeed) {

        this.windSpeed = windSpeed;
    }

    public String getWindScale() {

        return windScale;
    }

    public void setWindScale(String windScale) {

        this.windScale = windScale;
    }

    public String getWeatherTime() {

        return weatherTime;
    }

    public void setWeatherTime(String weatherTime) {

        this.weatherTime = weatherTime;
    }

    public String getFormattedTime() {

        return formattedTime;
    }

    public void setFormattedTime(String formattedTime) {

        this.formattedTime = formattedTime;
    }
}
