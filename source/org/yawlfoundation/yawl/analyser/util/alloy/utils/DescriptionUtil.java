package org.yawlfoundation.yawl.analyser.util.alloy.utils;

public class DescriptionUtil {
    public static GatewayType getGatewayType(int gatewayTypeCode) {
        return switch (gatewayTypeCode) {
            case 95 -> GatewayType.and;
            case 103 -> GatewayType.or;
            case 126 -> GatewayType.xor;
            default -> GatewayType.None;
        };
    }

    public static String getShowPredPart(int stateCount, int taskCount, int flowsCount) {
        return String.format("""
                 pred show{}
                 run show for %d but %d task, %d Flows\s
                """, stateCount, taskCount, flowsCount);
    }
}
