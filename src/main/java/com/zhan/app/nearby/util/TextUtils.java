package com.zhan.app.nearby.util;

import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public final class TextUtils {

	public static boolean isEmpty(final CharSequence s) {
		if (s == null) {
			return true;
		}
		return s.length() == 0;
	}

	public static boolean isNotEmpty(final CharSequence s) {
		return !isEmpty(s);
	}

	public static boolean isBlank(final CharSequence s) {
		if (s == null) {
			return true;
		}
		for (int i = 0; i < s.length(); i++) {
			if (!Character.isWhitespace(s.charAt(i))) {
				return false;
			}
		}
		return true;
	}

	public static boolean isNotBlank(String userName) {
		// TODO Auto-generated method stub
		return !isBlank(userName);
	}

	
	public void test() throws InterruptedException{
		 EventLoopGroup bossGroup = new NioEventLoopGroup(); // (1)
	        EventLoopGroup workerGroup = new NioEventLoopGroup();
	        try {
	            ServerBootstrap b = new ServerBootstrap(); // (2)
	            b.group(bossGroup, workerGroup)
	             .channel(NioServerSocketChannel.class) // (3)
	             .childHandler(new ChannelInitializer<SocketChannel>() { // (4)
	                 @Override
	                 public void initChannel(SocketChannel ch) throws Exception {
	                     ch.pipeline().addLast(null);
	                 }
	             })
	             .option(ChannelOption.SO_BACKLOG, 128)          // (5)
	             .childOption(ChannelOption.SO_KEEPALIVE, true); // (6)
	    
	            // Bind and start to accept incoming connections.
	            ChannelFuture f = b.bind(5645).sync(); // (7)
	    
	            // Wait until the server socket is closed.
	            // In this example, this does not happen, but you can do that to gracefully
	            // shut down your server.
	            f.channel().closeFuture().sync();
	        } finally {
	            workerGroup.shutdownGracefully();
	            bossGroup.shutdownGracefully();
	        }
	}
	
	
	 public static void main(String[] args) {
	        Callable<Integer> callable = new Callable<Integer>() {
	            public Integer call() throws Exception {
	                return new Random().nextInt(100);
	            }
	        };
	        FutureTask<Integer> future = new FutureTask<Integer>(callable);
	        new Thread(future).start();
	        try {
	            Thread.sleep(5000);// 可能做一些事情
	            System.out.println(future.get());
	        } catch (InterruptedException e) {
	            e.printStackTrace();
	        } catch (ExecutionException e) {
	            e.printStackTrace();
	        }
	    }
}