package com.glj.user.musicdownload;

import net.sf.json.JSONObject;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ParsePage2 {
    private String body;
    private Page2 page2;
    private int i = 0;

    public void connect(Page1 page1) throws IOException {
        OkHttpClient ok = new OkHttpClient();
        String url = "http://api.96iz.cc/api/post.php?id=" + page1.getId() + "&type=qq";
        Request request = new Request.Builder().url(url).build();
        Response response = ok.newCall(request).execute();

        body = response.body().string();
        JSONObject json = JSONObject.fromObject(body);
        page2 = new Page2();
        page2.setUrl128(json.getString("url128"));
        page2.setUrl320(json.getString("url320"));
        page2.setUrl999(json.getString("url999"));
        i = 1;
        //System.out.println(page2);

    }

    public Page2 getPage2() {
        return page2;
    }

    public void setPage2(Page2 page2) {
        this.page2 = page2;
    }

    public int getI() {
        return i;
    }

    public void setI(int i) {
        this.i = i;
    }
}
