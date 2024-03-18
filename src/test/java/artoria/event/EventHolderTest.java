package artoria.event;

import artoria.identifier.IdentifierUtils;
import artoria.track.SimpleTrackProvider;
import org.junit.Test;

import java.util.Arrays;

public class EventHolderTest {

    @Test
    public void test1() {
        ((SimpleTrackProvider) EventUtils.getEventProvider())
                .setShowPropertyNames(Arrays.asList("appId", "serverId"));
        EventHolder.alias("alias1")
                .setCode("TEST_EVENT_1")
                .setTime(System.currentTimeMillis())
                .setAnonymousId(IdentifierUtils.nextStringIdentifier())
                .setPrincipalId("u10000001")
                .set("serverId", "SER1001")
                .set("appId", "APP101812")
                .submit();
    }

    @Test
    public void test2() {
        EventHolder.alias("alias2")
                .setCode("TEST_EVENT_2")
                .setPrincipalId("u10000001")
                .set("serverId", "SER1009")
                .set("appId", "APP101812");
        // Other code.
        EventHolder.alias("alias2_1")
                .set("ip", "192.168.1.1")
                .set("geo", "121.59")
                .remove("appId");
        // Other code.
        System.out.println(EventHolder.alias("alias2_1").get("ip"));
        EventHolder.submit("alias2");
        EventHolder.clear();
    }

    @Test
    public void test3() {
        EventHolder.alias("alias3").setCode("TEST_EVENT_3").setPrincipalId("u10000001");
        // Other code.
        EventHolder.alias("alias3_1").setCode("TEST_EVENT_3").setPrincipalId("u10000002").submit();
        // Other code.
        EventHolder.alias("alias3", false).set("ip", "192.168.1.1").submit();
    }

    @Test
    public void test4() {
        EventHolder.alias("alias4")
                .setCode("TEST_EVENT_4")
                .setPrincipalId("u10000001")
                .set("ip", "192.168.1.1")
                .set("summary", "Hello, event holder! ")
                .submit();
    }

}
