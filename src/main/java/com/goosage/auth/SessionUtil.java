package com.goosage.auth;

import jakarta.servlet.http.HttpSession;

public class SessionUtil {
    private SessionUtil() {}

    public static long requireUserId(HttpSession session) {
        Object v = session.getAttribute(SessionConst.LOGIN_USER_ID);
        if (v == null) throw new IllegalArgumentException("UNAUTHORIZED");
        if (v instanceof Long l) return l;
        if (v instanceof Integer i) return i.longValue();
        throw new IllegalArgumentException("UNAUTHORIZED");
    }
}
