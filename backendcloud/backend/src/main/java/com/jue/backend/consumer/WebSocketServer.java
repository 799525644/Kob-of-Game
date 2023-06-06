package com.jue.backend.consumer;

import com.alibaba.fastjson.JSONObject;
import com.jue.backend.consumer.utils.Game;
import com.jue.backend.consumer.utils.JwtAuthentication;
import com.jue.backend.mapper.BotMapper;
import com.jue.backend.mapper.RecordMapper;
import com.jue.backend.mapper.UserMapper;
import com.jue.backend.pojo.Bot;
import com.jue.backend.pojo.Record;
import com.jue.backend.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

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
    // final即常量
    // 由于需要外面调用采用public
    // 使用static修饰，可以理解为定义公共变量，对所有实例可见。可以从外面用类名访问，而不用实例访问。
    // 定义一个线程安全的哈希表ConcurrentHashMap，存储所有的user链接，名为users；
    final public static ConcurrentHashMap<Integer, WebSocketServer> users = new ConcurrentHashMap<>();
    // 定义一个线程安全的匹配池
//TODO    final private static CopyOnWriteArraySet<User> matchpool = new CopyOnWriteArraySet<>();

    // 1.2 需要维护每个ws链接，并存储该连接的user信息（从共有的userMapper中找）
    // 1.2.1 websocket是非单例的，其多个实例对象需要互相独立、属性互相分开，而且是开多线程的，要留意 不同线程访问 同源 但不同的实例对象带来的数据错乱问题。；
    // 因此对于需要注入的变量确保使用static；如果不用static，那么会造成内存浪费并且线程数据也不安全。
    // 1.2.2 除此之外，要注意Spring的自动装配机制仅支持实例变量或方法，而不支持静态变量；因此当static的属性直接注入，将会是null
    // 要通过手动注入，即 注入setUserMapper方法，在其中手动赋值给静态变量userMapper
    private User user;
    private Session session = null;
    // 在这边 不能直接注入@Autowired
    public static UserMapper userMapper;
    public static RecordMapper recordMapper;
    public static BotMapper botMapper;
    public Game game = null;
    //TODO 把下面的URL写到配置文件中去
    private final static String addPlayerUrl = "http://127.0.0.1:4001/player/add/";
    private final static String removePlayerUrl = "http://127.0.0.1:4001/player/remove/";
    public static RestTemplate restTemplate; // 用于两个进程之间通，外面也需要借Websocket打restTemplate，故用public
    @Autowired
    public void setUserMapper(UserMapper userMapper) {
        WebSocketServer.userMapper = userMapper;
    }
    @Autowired
    public void setRecordMapper(RecordMapper recordMapper) {
        WebSocketServer.recordMapper = recordMapper;
    }
    @Autowired
    public void setBotMapper(BotMapper botMapper) {
        WebSocketServer.botMapper = botMapper;
    }
    @Autowired
    public void setRestTemplate(RestTemplate restTemplate){
        WebSocketServer.restTemplate = restTemplate;
    }
    // 2.1 websocket的钩子函数、onOpen、onCLose、onError等，最重要的是onMessage业务相关处理接收
    /**
     * Method: onOpen
     * @Description: ws对象内置的onOpen事件，代表ws连接建立后的的操作；一般要做鉴权操作
     * @Author: Juemuel
     * @CreateTime: 2023/4/14
     * @Param: [session, token]
     * @Return: void
     * @Throws: IOException
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("token") String token) throws IOException {
        this.session = session;
        System.out.println("connected in onOpen");
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
     * Method: onClose
     * @Description: ws对象的内置onClose事件代表ws关闭后的的操作
     * @Author: Juemuel
     * @CreateTime: 2023/4/14
     * @Param: []
     * @Return: void
     */
    @OnClose
    public void onClose() {
        System.out.println("disconnected! in onClose");
        if (this.user != null) {
            users.remove(this.user.getId());
        }
    }

    /**
     * Method: onMessage
     * @Description: ws对象内置的OnMessage事件，接收到消息后调用的方法；用于获取前端的ws.send过来的JSON数据
     * @Author: Juemuel
     * @CreateTime: 2023/4/14
     * @Param: [message, session]
     * @Return: void
     */
    @OnMessage
    public void onMessage(String message, Session session) {  // ws中message实际上是被当做路由
        JSONObject data = JSONObject.parseObject(message);
        System.out.println("receive message! in onMessage"+data);
        String event = data.getString("event");
        if ("start-matching".equals(event)) {
            startMatching(data.getInteger("bot_id"));
        } else if ("stop-matching".equals(event)) {
            stopMatching();
        }else if("move".equals(event)){
            move(data.getInteger("direction"));
        }
    }


    /**
     * Method: onError
     * @Description: ws对象内置的onError事件，即发生错误时调用
     * @Author: Juemuel
     * @CreateTime: 2023/4/14
     * @Param: [session, error]
     * @Return: void
     */
    @OnError
    public void onError(Session session, Throwable error) {
        System.out.println("error in onError~");
        error.printStackTrace();
    }


    /**
     * Method: startGame
     * @Description: startGame
     * @Author: Juemuel
     * @CreateTime: 2023/4/18
     * @Param: [aId, bId]
     * @Return: void
     */
    public static void startGame(Integer aId,Integer aBotId, Integer bId, Integer bBotId){
        User a = userMapper.selectById(aId);
        User b = userMapper.selectById(bId);
        Bot botA = botMapper.selectById(aBotId), botB = botMapper.selectById(bBotId);

        // #1 暂时先用局部变量、不存储到连接中
        Game game = new Game(
                13,
                14,
                20,
                a.getId(),
                botA,
                b.getId(),
                botB
        );
        game.createMap();
        game.start(); // 另起一个线程
        // 注意：要考虑到玩家不存在，意外退出的情况，因此要判空
        users.get(a.getId()).game = game;
        users.get(b.getId()).game = game;


        // 把相关信息封装在一起
        JSONObject respGame = new JSONObject();
        respGame.put("a_id", game.getPlayerA().getId());
        respGame.put("a_sx", game.getPlayerA().getSx());
        respGame.put("a_sy", game.getPlayerA().getSy());
        respGame.put("b_id", game.getPlayerB().getId());
        respGame.put("b_sx", game.getPlayerB().getSx());
        respGame.put("b_sy", game.getPlayerB().getSy());
        respGame.put("map", game.getG());

        JSONObject respA = new JSONObject();
        respA.put("event", "start-matching"); // 在前端接收处，对应起来
        respA.put("opponent_username", b.getUsername());
        respA.put("opponent_photo", b.getPhoto());
        respA.put("game", respGame); // 包括A的Game信息（id、坐标、map）
        System.out.println("A"+respA);
        // 注意判空
        if(users.get(a.getId()) != null)
            users.get(a.getId()).sendMessage(respA.toJSONString());

        // 同理给B传
        JSONObject respB = new JSONObject();
        respB.put("event", "start-matching");
        respB.put("opponent_username", a.getUsername());
        respB.put("opponent_photo", a.getPhoto());
        respB.put("game", respGame); // 包括A的Game信息（id、坐标、map）
        System.out.println("B"+respB);
        if(users.get(b.getId()) != null)
            users.get(b.getId()).sendMessage(respB.toJSONString());

    }


    /**
     * Method: 开始匹配：startMatching
     * @Description:
     * 方案1:微服务中通过RestTemplate类与匹配服务通信，使用MultiValueMap<String,String>的data传递
     * 方案2:单服务则自己建一个匹配池，将匹配逻辑写在同一个服务中
     * 将user放入匹配服务中匹配：需要userid、rating（此处基于rating来匹配）
     * @Author: Juemuel
     * @CreateTime: 2023/4/14
     * @Param: []
     * @Return: void
     */
    private void startMatching(Integer botId) {
        System.out.println("start matching!");
        // 注意ws中的map相当于缓存，需要注意一致性问题！
        MultiValueMap<String, String> data = new LinkedMultiValueMap<>();
        data.add("user_id",this.user.getId().toString());
        data.add("rating",this.user.getRating().toString());
        data.add("bot_id",botId.toString());
        restTemplate.postForObject(addPlayerUrl,data,String.class);//String.class是java反射机制

    }

    /**
     * Method: 终止匹配 stopMatching
     * @Description:
     * 方案1:微服务中通过RestTemplate类与匹配服务通信，使用MultiValueMap<String,String>的data传递
     * 方案2:单服务则自己建一个匹配池，将匹配逻辑写在同一个服务中
     * 将user从匹配服务中移除：需要userid
     * @Author: Juemuel
     * @CreateTime: 2023/4/14
     * @Param: []
     * @Return: void
     */
    private void stopMatching() {
        System.out.println("stop matching!");
        MultiValueMap<String, String> data = new LinkedMultiValueMap<>();
        data.add("user_id",this.user.getId().toString());
        restTemplate.postForObject(removePlayerUrl,data,String.class);
    }

    /**
     * Method: move
     * @Description: move中，先判断是蛇A还是蛇B的行为，再做下一步操作；EXP：写代码要保守一些多做严谨的判断避免bug
     * 补充：如果是人的操作才...Bot则屏蔽掉
     * @Author: Juemuel
     * @CreateTime: 2023/4/15
     * @Param: [direction]
     * @Return: void
     */
    private void move(int direction){
        if(game.getPlayerA().getId().equals(user.getId())){
            if(game.getPlayerA().getBotId().equals(-1))
                game.setNextStepA(direction);
        }else if(game.getPlayerB().getId().equals(user.getId())){
            if(game.getPlayerB().getBotId().equals(-1))
                game.setNextStepB(direction);
        }
    }

    /**
     * Method: sendMessage
     * @Description: sendMessage，用于发送信息到前端（匹配信息、结果信息）
     * @Author: Juemuel
     * @CreateTime: 2023/4/14
     * @Param: [message]
     * @Return: void
     */
    public void sendMessage(String message) {
        synchronized (this.session) {
            try {
                System.out.println("send"+message);
                this.session.getBasicRemote().sendText(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
