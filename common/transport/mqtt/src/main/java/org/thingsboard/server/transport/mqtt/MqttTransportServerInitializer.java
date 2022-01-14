/**
 * Copyright © 2016-2021 The Thingsboard Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.thingsboard.server.transport.mqtt;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.mqtt.MqttDecoder;
import io.netty.handler.codec.mqtt.MqttEncoder;
import io.netty.handler.ssl.SslHandler;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.openjdk.jol.vm.VM;

/**
 * @author Andrew Shvayka
 */
@Slf4j
public class MqttTransportServerInitializer extends ChannelInitializer<SocketChannel> {

    private final MqttTransportContext context;
    private final boolean sslEnabled;
    @Getter
    private static MqttTransportHandler handler;


    public MqttTransportServerInitializer(MqttTransportContext context, boolean sslEnabled) {
        this.context = context;
        this.sslEnabled = sslEnabled;
    }

    @Override
    public void initChannel(SocketChannel ch) {
        ChannelPipeline pipeline = ch.pipeline();
        SslHandler sslHandler = null;
        if (sslEnabled && context.getSslHandlerProvider() != null) {
            sslHandler = context.getSslHandlerProvider().getSslHandler();
            pipeline.addLast(sslHandler);
        }
        pipeline.addLast("decoder", new MqttDecoder(context.getMaxPayloadSize()));
        pipeline.addLast("encoder", MqttEncoder.INSTANCE);

        handler = new MqttTransportHandler(context, sslHandler);
        long l = VM.current().addressOf(handler);
        log.info("handler初始化内存地址：" +VM.current().addressOf(handler));
        log.info("MqttTransportHandler被初始化："+handler.toString());
        log.info("context"+handler.toString());
        log.info("sslHandler"+handler.toString());

        pipeline.addLast(handler);
        ch.closeFuture().addListener(handler);
    }

}
