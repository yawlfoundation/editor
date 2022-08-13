package org.yawlfoundation.yawl.analyser.util.alloy.utils;

public class DescriptionUtil {
    public static GatewayType getGatewayType(int gatewayTypeCode) {
        switch (gatewayTypeCode) {
            case 95: return GatewayType.and;
            case 103: return GatewayType.or;
            case 126: return GatewayType.xor;
            default: return GatewayType.None;
        }
    }

    public static String getShowPredPart(int objectCount, int predicateCount) {
        return String.format("fact{#Boolean = %d}\nfact{#Object1 = %d}\n\n\n pred show{}\n run show for %d but %d " +
                "State ", predicateCount, objectCount, objectCount, objectCount);
    }
}
