package org.jeecg.modules.custom.api.callback;

import java.net.InetAddress;
import java.net.URI;
import java.util.List;

public record ValidatedCallbackTarget(URI uri, List<InetAddress> addresses) {
    public ValidatedCallbackTarget {
        if (uri == null || addresses == null || addresses.isEmpty()) {
            throw new IllegalArgumentException("callback target URI and addresses are required");
        }
        addresses = List.copyOf(addresses);
    }
}
