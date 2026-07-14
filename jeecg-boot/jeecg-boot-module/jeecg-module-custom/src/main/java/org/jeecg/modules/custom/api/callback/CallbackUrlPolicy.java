package org.jeecg.modules.custom.api.callback;

import org.jeecg.common.exception.JeecgBootException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.IDN;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class CallbackUrlPolicy {
    private static final int MAX_URL_LENGTH = 2048;

    private final boolean requireHttps;
    private final AddressResolver resolver;
    private final Set<String> allowlist;

    @Autowired
    public CallbackUrlPolicy(
            @Value("${custom.api.callback.require-https:true}") boolean requireHttps,
            @Value("${custom.api.callback.allowlist:}") String allowlist) {
        this(requireHttps, host -> Arrays.asList(InetAddress.getAllByName(host)), parseAllowlist(allowlist));
    }

    public CallbackUrlPolicy(boolean requireHttps, AddressResolver resolver, Set<String> allowlist) {
        this.requireHttps = requireHttps;
        this.resolver = resolver;
        this.allowlist = allowlist == null ? Set.of() : allowlist.stream()
                .filter(value -> value != null && !value.isBlank())
                .map(value -> value.trim().toLowerCase(Locale.ROOT))
                .collect(Collectors.toUnmodifiableSet());
    }

    public URI validate(String rawUrl) {
        if (rawUrl == null || rawUrl.isBlank() || rawUrl.length() > MAX_URL_LENGTH) {
            throw new JeecgBootException("callback URL is required and must be at most 2048 characters");
        }
        final URI uri;
        try {
            uri = URI.create(rawUrl.trim());
        } catch (IllegalArgumentException invalid) {
            throw new JeecgBootException("callback URL is invalid");
        }
        if (!uri.isAbsolute() || uri.getHost() == null || uri.getHost().isBlank()) {
            throw new JeecgBootException("callback URL must contain an absolute host");
        }
        if (requireHttps && !"https".equalsIgnoreCase(uri.getScheme())) {
            throw new JeecgBootException("callback URL must use HTTPS");
        }
        if (!List.of("http", "https").contains(uri.getScheme().toLowerCase(Locale.ROOT))) {
            throw new JeecgBootException("callback URL scheme is not supported");
        }
        if (uri.getRawUserInfo() != null) {
            throw new JeecgBootException("callback URL credentials are not allowed");
        }
        if (uri.getFragment() != null) {
            throw new JeecgBootException("callback URL fragments are not allowed");
        }

        String host = normalizeHost(uri.getHost());
        rejectMetadataHost(host);
        String authority = host + ":" + effectivePort(uri);
        if (!allowlist.isEmpty() && !allowlist.contains(authority) && !allowlist.contains(host)) {
            throw new JeecgBootException("callback host is not allowlisted");
        }

        final List<InetAddress> addresses;
        try {
            addresses = resolver.resolve(host);
        } catch (Exception unresolved) {
            throw new JeecgBootException("callback host cannot be resolved");
        }
        if (addresses == null || addresses.isEmpty()
                || addresses.stream().anyMatch(address -> !isPublic(address))) {
            throw new JeecgBootException("callback host must resolve only to public addresses");
        }
        return uri;
    }

    public URI validateRedirect(URI source, String location) {
        URI target = validate(location);
        if (source == null || !sameAuthority(source, target)) {
            throw new JeecgBootException("callback redirect host must match the original host");
        }
        return target;
    }

    private boolean sameAuthority(URI left, URI right) {
        return normalizeHost(left.getHost()).equals(normalizeHost(right.getHost()))
                && effectivePort(left) == effectivePort(right);
    }

    private int effectivePort(URI uri) {
        if (uri.getPort() >= 0) {
            return uri.getPort();
        }
        return "https".equalsIgnoreCase(uri.getScheme()) ? 443 : 80;
    }

    private String normalizeHost(String host) {
        String normalized = IDN.toASCII(host).toLowerCase(Locale.ROOT);
        while (normalized.endsWith(".")) {
            normalized = normalized.substring(0, normalized.length() - 1);
        }
        return normalized;
    }

    private void rejectMetadataHost(String host) {
        if (host.equals("localhost") || host.endsWith(".localhost")) {
            throw new JeecgBootException("callback metadata or localhost hosts are not allowed");
        }
        if (host.equals("metadata") || host.startsWith("metadata.")
                || host.contains(".metadata.") || host.endsWith(".internal")) {
            throw new JeecgBootException("callback metadata host is not allowed");
        }
    }

    private boolean isPublic(InetAddress address) {
        if (address == null || address.isAnyLocalAddress() || address.isLoopbackAddress()
                || address.isLinkLocalAddress() || address.isSiteLocalAddress()
                || address.isMulticastAddress()) {
            return false;
        }
        byte[] bytes = address.getAddress();
        if (address instanceof Inet4Address) {
            int first = Byte.toUnsignedInt(bytes[0]);
            int second = Byte.toUnsignedInt(bytes[1]);
            int third = Byte.toUnsignedInt(bytes[2]);
            return first != 0
                    && first != 10
                    && first != 127
                    && !(first == 100 && second >= 64 && second <= 127)
                    && !(first == 169 && second == 254)
                    && !(first == 172 && second >= 16 && second <= 31)
                    && !(first == 192 && second == 0 && third == 0)
                    && !(first == 192 && second == 0 && third == 2)
                    && !(first == 192 && second == 168)
                    && !(first == 198 && (second == 18 || second == 19))
                    && !(first == 198 && second == 51 && third == 100)
                    && !(first == 203 && second == 0 && third == 113)
                    && first < 224;
        }
        if (address instanceof Inet6Address) {
            int first = Byte.toUnsignedInt(bytes[0]);
            return (first & 0xfe) != 0xfc
                    && !(Byte.toUnsignedInt(bytes[0]) == 0x20
                    && Byte.toUnsignedInt(bytes[1]) == 0x01
                    && Byte.toUnsignedInt(bytes[2]) == 0x0d
                    && Byte.toUnsignedInt(bytes[3]) == 0xb8);
        }
        return false;
    }

    private static Set<String> parseAllowlist(String value) {
        if (value == null || value.isBlank()) {
            return Set.of();
        }
        return Arrays.stream(value.split(","))
                .map(String::trim)
                .filter(item -> !item.isBlank())
                .collect(Collectors.toUnmodifiableSet());
    }

    @FunctionalInterface
    public interface AddressResolver {
        List<InetAddress> resolve(String host) throws Exception;
    }
}
