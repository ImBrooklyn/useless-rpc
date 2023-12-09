package org.example.useless.remote.client;

import io.netty.channel.Channel;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author ImBrooklyn
 * @since 20/08/2023
 */
public class ChannelProvider {
    private ChannelProvider() {

    }

    private static final ChannelProvider INSTANCE = new ChannelProvider();

    public static ChannelProvider getInstance() {
        return INSTANCE;
    }

    private final Map<String, Channel> channelMap = new ConcurrentHashMap<>();

    public Channel getChannel(InetSocketAddress remoteAddress) {
        String key = remoteAddress.toString();
        if (channelMap.containsKey(key)) {
            Channel channel = channelMap.get(key);
            // if so, determine if the connection is available, and if so, get it directly
            if (channel != null && channel.isActive()) {
                return channel;
            } else {
                channelMap.remove(key);
            }
        }
        return null;
    }

    public void setChannel(Channel channel) {
        channelMap.put(channel.remoteAddress().toString(), channel);
    }
}
