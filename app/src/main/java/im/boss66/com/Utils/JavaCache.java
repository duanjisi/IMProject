package im.boss66.com.Utils;

import java.util.WeakHashMap;

import de.tavendo.autobahn.WebSocket;
import de.tavendo.autobahn.WebSocketConnection;

/**
 * Created by Johnny on 2017/6/8.
 */
public class JavaCache {
    private static WeakHashMap<String, WebSocket> map = new WeakHashMap<>();

    public static WebSocket getWebSocket(String key) {
//先从缓存里面取值
        WebSocket obj = map.get(key);
//判断缓存里面是否有值
        if (obj == null) {
//如果没有，那么就去获取相应的数据，比如读取数据库或者文件
            obj = new WebSocketConnection();//这个方法是*Dao实现具体数据库查询的时候调用的方法
//把获取的值设置回到缓存里面
            map.put(key, obj);
        }
//如果有值了，就直接返回使用
        return obj;
    }
}
