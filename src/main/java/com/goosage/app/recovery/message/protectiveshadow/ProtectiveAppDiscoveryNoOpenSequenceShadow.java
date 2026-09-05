package com.goosage.app.recovery.message.protectiveshadow;

import java.util.List;

import com.goosage.app.recovery.message.structuredshadow.StructuredEventShadow;

public class ProtectiveAppDiscoveryNoOpenSequenceShadow {

    public boolean resolve(
            List<StructuredEventShadow> events
    ) {
        boolean appDiscoverySeen = false;

        for (StructuredEventShadow structured : events) {

            String text = structured.text();

            if (hasAppDiscovery(text)) {
                appDiscoverySeen = true;
            }

            if (
                    appDiscoverySeen
                    && hasExplicitNoOpen(text)
            ) {
                return true;
            }
        }

        return false;
    }


    private boolean hasAppDiscovery(String text) {

        boolean appContext =
                text.contains("앱");

        boolean discovery =
                text.contains("찾아봤")
                || text.contains("찾아보")
                || text.contains("남아 있");

        return appContext && discovery;
    }


    private boolean hasExplicitNoOpen(String text) {

        return text.contains("열어보지는 않")
                || text.contains("열어보지 않");
    }
}
