package com.goosage.app.recovery.message.temporalshadow;

public class TemporalRoleResolverShadowCli {

    public static void main(String[] args) {
        String text = String.join(" ", args);

        TemporalRoleShadow role =
                new TemporalRoleResolverShadow().resolve(text);

        System.out.println("temporalRole=" + role);
    }
}
