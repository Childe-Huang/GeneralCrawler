package config;

/**
 * Created by Bin on 2015/3/2.
 */
public class JedisConfig {
    //public static String address = "192.168.159.128";
    public static String address = "127.0.0.1";

    public static int port = 6379;

    public final static String CATALOG_SEED = "0";

    public final static String COMMON_SEED = "1";

    public final static String UN_GRABBED = "2";

    public final static String BEEN_GRABBED = "3";

    public static void setAddress(String address) {
        JedisConfig.address = address;
    }

    public static void setPort(int port) {
        JedisConfig.port = port;
    }
}
