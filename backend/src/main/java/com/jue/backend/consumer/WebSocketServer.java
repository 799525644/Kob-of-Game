package com.jue.backend.consumer;

import com.alibaba.fastjson.JSONObject;
import com.jue.backend.consumer.utils.Game;
import com.jue.backend.consumer.utils.JwtAuthentication;
import com.jue.backend.mapper.UserMapper;
import com.jue.backend.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

@Component
@ServerEndpoint("/websocket/{token}") // 声明当前Bean 接受的Websocket URL,原理类似Controller的映射，但是注意这边不要以`/`结尾
public class WebSocketServer {
    // 1.1 需要维护每个user链接，而且可以被每个ws实例调用
    // 使用static修饰，可以理解为定义公共变量，对所有实例可见。可以从外面用类名访问，而不用实例访问。
    // final即常量
    // 定义一个线程安全的哈希表ConcurrentHashMap，存储所有的user链接，名为users
    final private static ConcurrentHashMap<Integer, WebSocketServer> users = new ConcurrentHashMap<>();
    // 定义一个线程安全的匹配池
    final private static CopyOnWriteArraySet<User> matchpool = new CopyOnWriteArraySet<>();

    // 1.2 需要维护每个ws链接，并存储该连接的user信息（从共有的userMapper中找）
    // 1.2.1 websocket是非单例的，其多个实例对象需要互相独立、属性互相分开，而且是开多线程的，要留意 不同线程访问 同源 但不同的实例对象带来的数据错乱问题。；
    // 因此对于需要注入的变量确保使用static；如果不用static，那么会造成内存浪费并且线程数据也不安全。
    // 1.2.2 除此之外，要注意Spring的自动装配机制仅支持实例变量或方法，而不支持静态变量；因此当static的属性直接注入，将会是null
    // 要通过手动注入，即 注入setUserMapper方法，在其中手动赋值给静态变量userMapper
    private User user;
    private Session session = null;
    // 在这边 不能直接注入@Autowired
    private static UserMapper userMapper;
    @Autowired
    public void setUserMapper(UserMapper userMapper) {
        WebSocketServer.userMapper = userMapper;
    }

    // 2.1

    /**
     * ws的onOpen事件代表ws连接建立后的的操作，一般要做鉴权操作
     * @param session
     * @param token
     * @throws IOException
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("token") String token) throws IOException {
        this.session = session;
        System.out.println("connected!");
        // 通过userId从userMapper中获取user，并存储
        // #1 先传入ID来调式
//        Integer userId = Integer.parseInt(token);
        // #2 传入jwt token
        Integer userId = JwtAuthentication.getUserId(token);
        this.user = userMapper.selectById(userId);
        if (this.user != null) {
            users.put(userId, this);
        } else {
            this.session.close();
        }
        System.out.println("id"+users);
    }

    /**
     * ws的onClose事件代表ws关闭后的的操作
     */
    @OnClose
    public void onClose() {
        System.out.println("disconnected!");
        if (this.user != null) {
            users.remove(this.user.getId());
            matchpool.remove(this.user);
        }
    }

    private void startMatching() {
        System.out.println("start matching!");
        matchpool.add(this.user);
        // #1 多于两个人匹配，调试用
        while (matchpool.size() >= 2) {
            // 枚举前两个人，取出删掉
            Iterator<User> it = matchpool.iterator();
            User a = it.next(), b = it.next();
            matchpool.remove(a);
            matchpool.remove(b);

            // #1 暂时先用局部变量、不存储到连接中
            Game game = new Game(13, 14, 20);
            game.createMap();

            JSONObject respA = new JSONObject();
            respA.put("event", "start-matching"); // 在前端接收处，对应起来

            respA.put("opponent_username", b.getUsername());
            respA.put("opponent_photo", b.getPhoto());
            respA.put("gamemap", game.getG()); // 传入地图
            // 取出A的连接.传递信息sendMessage()
            users.get(a.getId()).sendMessage(respA.toJSONString());

            // 同理给B传
            JSONObject respB = new JSONObject();
            respB.put("event", "start-matching");
            respB.put("opponent_username", a.getUsername());
            respB.put("opponent_photo", a.getPhoto());
            respB.put("gamemap", game.getG()); // 传入地图
            users.get(b.getId()).sendMessage(respB.toJSONString());
        }
    }

    private void stopMatching() {
        System.out.println("stop matching");
        matchpool.remove(this.user);
    }

    /**
     * ws的OnMessage事件，即接收到消息后调用的方法
     * 获取前端的ws.send过来的JSON数据
     * @param message
     * @param session
     */
    @OnMessage
    public void onMessage(String message, Session session) {  // ws中message实际上是被当做路由
        System.out.println("receive message!");
        JSONObject data = JSONObject.parseObject(message);
        String event = data.getString("event");
        if ("start-matching".equals(event)) {
            startMatching();
        } else if ("stop-matching".equals(event)) {
            stopMatching();
        }
    }

    /**
     * ws的onError事件，即发生错误时调用
     * @param session
     * @param error
     */
    @OnError
    public void onError(Session session, Throwable error) {
        error.printStackTrace();
    }
    public void sendMessage(String message) {
        synchronized (this.session) {
            try {
                this.session.getBasicRemote().sendText(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}
