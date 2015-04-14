package net;

import org.apache.http.HttpHost;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Bin on 2015/3/25.
 */
public class Proxys extends ArrayList<HttpHost> {
    //IP代理日志
    public static final Logger LOG= LoggerFactory.getLogger(Proxys.class);

    private int randomCount;

    private Random random;

    public void add(String ip, int port) {
        HttpHost proxy = new HttpHost(new HttpHost(ip, port));
        this.add(proxy);
    }

    /**
     * 随机从代理列表中获取代理ip
     * @return
     */
    public HttpHost getRandomProxy() {
        HttpHost proxy = null;
        random = new Random();
        if (this.size() != 0) {
            randomCount = Math.abs(random.nextInt()) % this.size();
            proxy = this.get(randomCount);
        }
        return proxy;
    }
}
