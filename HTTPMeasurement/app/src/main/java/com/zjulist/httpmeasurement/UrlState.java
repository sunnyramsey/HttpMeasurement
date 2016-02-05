package com.zjulist.httpmeasurement;

/**
 * Created by dell on 2016/1/30.
 */
public class UrlState {

    private String url;
    private boolean isFinished;

    public UrlState(String url)
    {
        this.url = url;
        isFinished = false;
    }


    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isFinished() {
        return isFinished;
    }

    public void setIsFinished(boolean isFinished) {
        this.isFinished = isFinished;
    }
}
