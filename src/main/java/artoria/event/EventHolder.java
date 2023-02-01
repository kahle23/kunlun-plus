package artoria.event;

import artoria.convert.ConversionUtils;
import artoria.util.ArrayUtils;
import artoria.util.Assert;
import artoria.util.Current;
import artoria.util.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import static artoria.common.Constants.THIRTY;

/**
 * The event temporary management related tools.
 * @deprecated The event tools based on ThreadLocal should not be provided,
 * Because exception handling of ThreadLocal is a hassle (20220504).
 * @author Kahle
 */
@Deprecated // TODO: Deletable
public class EventHolder {
    private static final Logger log = LoggerFactory.getLogger(EventHolder.class);
    private static final String EVENT_MAP_NAME = "event_map";

    protected static Map<String, Event> getEventMap() {
        Map<String, Event> eventMap = ObjectUtils.cast(Current.get(EVENT_MAP_NAME));
        //Assert.notNull(eventMap, "The thread local container is not ready. ")
        if (eventMap == null) {
            eventMap = new HashMap<String, Event>(THIRTY);
            Current.put(EVENT_MAP_NAME, eventMap);
        }
        return eventMap;
    }

    protected static void submit(Event event) {
        Assert.notNull(event, "Parameter \"event\" must not null. ");
        EventUtils.track(event.getCode(),
                event.getTime(),
                event.getPrincipalId(),
                event.getProperties());
        boolean managed = event.getManaged();
        String alias = event.getAlias();
        if (managed) { getEventMap().remove(alias); }
    }

    public static Event alias(String alias) {

        return alias(alias, true);
    }

    public static Event alias(String alias, boolean managed) {
        // In some cases, the same event can occur more than once,
        // So aliases are more appropriate for event code.
        // For example, the integral obtain event, it's possible to have more than one at a time.
        alias = String.valueOf(alias);
        Map<String, Event> eventMap = getEventMap();
        Event event = eventMap.get(alias);
        if (event == null) {
            event = new Event(alias, managed);
            if (managed) { eventMap.put(alias, event); }
        }
        return event;
    }

    public static Event aliasAndCode(String aliasAndCode) {

        return alias(aliasAndCode).setCode(aliasAndCode);
    }

    public static Event aliasAndCode(String aliasAndCode, boolean managed) {

        return alias(aliasAndCode, managed).setCode(aliasAndCode);
    }

    public static void submit(String alias) {
        alias = String.valueOf(alias);
        Event event = getEventMap().get(alias);
        Assert.state(event != null,
                "The event corresponding to the alias does not exist. ");
        submit(event);
    }

    public static void cancel(String alias) {
        alias = String.valueOf(alias);
        getEventMap().remove(alias);
    }

    public static void clear() {
        getEventMap().clear();
        Current.remove(EVENT_MAP_NAME);
    }

    /**
     * The convenient event object.
     */
    public static class Event {
        private Map<Object, Object> properties = new LinkedHashMap<Object, Object>();
        private String principalId;
        private String anonymousId;
        private Long   time;
        private String code;
        private final boolean managed;
        private final String alias;

        Event(String alias, boolean managed) {
            this.managed = managed;
            this.alias = alias;
        }

        public String getAlias() {

            return alias;
        }

        public boolean getManaged() {

            return managed;
        }

        public String getCode() {

            return code;
        }

        public Event setCode(String code) {
            this.code = code;
            return this;
        }

        public Long getTime() {

            return time;
        }

        public Event setTime(Long time) {
            this.time = time;
            return this;
        }

        public String getAnonymousId() {

            return anonymousId;
        }

        public Event setAnonymousId(String anonymousId) {
            this.anonymousId = anonymousId;
            return this;
        }

        public String getPrincipalId() {

            return principalId;
        }

        public Event setPrincipalId(String principalId) {
            this.principalId = principalId;
            return this;
        }

        public Object get(Object key) {

            return properties.get(key);
        }

        public <T> T get(Object key, Class<T> clazz) {
            Object result = get(key);
            if (result == null) { return null; }
            result = ConversionUtils.convert(result, clazz);
            return ObjectUtils.cast(result, clazz);
        }

        public Integer getInteger(Object key) {

            return get(key, Integer.class);
        }

        public Long getLong(Object key) {

            return get(key, Long.class);
        }

        public Double getDouble(Object key) {

            return get(key, Double.class);
        }

        public Boolean getBoolean(Object key) {

            return get(key, Boolean.class);
        }

        public String getString(Object key) {

            return get(key, String.class);
        }

        public Event set(Object key, Object value) {
            properties.put(key, value);
            return this;
        }

        public Event remove(Object key) {
            properties.remove(key);
            return this;
        }

        public Event delete(Object... keys) {
            if (ArrayUtils.isEmpty(keys)) { return this; }
            for (Object key : keys) { remove(key); }
            return this;
        }

        public Map<Object, Object> getProperties() {

            return properties;
        }

        public Event setProperties(Map<Object, Object> properties) {
            this.properties = properties;
            return this;
        }

        public Event addProperties(Map<Object, Object> properties) {
            this.properties.putAll(properties);
            return this;
        }

        public void submit() {

            EventHolder.submit(this);
        }

        public void cancel() {
            if (managed) {
                EventHolder.cancel(alias);
            }
        }

    }

}
