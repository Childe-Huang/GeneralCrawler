package fetcher;

/**
 * Created by Bin on 2015/3/2.
 */
public class FetchQueueItem {
    //网页URL地址
    private String url;
    //网页位置相对种子层级
    private int layer;

    public FetchQueueItem() {

    }

    public FetchQueueItem(String url) {
        this.setUrl(url);
    }

    public FetchQueueItem(String url, int layer) {
        this.setUrl(url);
        this.setLayer(layer);
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getLayer() {
        return layer;
    }

    public void setLayer(int layer) {
        this.layer = layer;
    }
}
