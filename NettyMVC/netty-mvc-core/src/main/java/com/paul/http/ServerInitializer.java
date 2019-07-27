package com.paul.http;

import com.paul.mvc.core.DispatcherHandler;
import com.paul.ioc.bean.AnnotationApplicationContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;

public class ServerInitializer extends ChannelInitializer<SocketChannel> {

    private AnnotationApplicationContext applicationContext;

    public ServerInitializer(AnnotationApplicationContext applicationContext){
        this.applicationContext = applicationContext;
    }
    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        //管道，管道里面可以有很多 handler，handler 可以理解为一层层过滤的网
        ChannelPipeline pipeline = socketChannel.pipeline();
        //HttpServerCodec 是 HttpRequestDecoder 和 HttpReponseEncoder 的组合，编码和解码的 handler
        pipeline.addLast("httpServerCodec", new HttpServerCodec());
        pipeline.addLast(new HttpObjectAggregator(10*1024*1024));
        pipeline.addLast("handler", new DispatcherHandler(applicationContext));
    }
}
