package com.glj.user.musicdownload;

import android.content.Context;
import android.os.Environment;

import net.sf.json.JSONArray;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ParsePage1 {
    private String body;
    private ArrayList<Page1> list;
    private ParsePage2 parse2 = new ParsePage2();
    private Page2 page2;
    private Page1 page;

    public void connect(String name) throws IOException {
        OkHttpClient ok = new OkHttpClient();
        String url = "http://api.96iz.cc/api/search.php?kw=" + name + "&type=tencent&page=1";
        Request request = new Request.Builder().url(url).build();
        Response response = null;
        response = ok.newCall(request).execute();
        body = response.body().string();
        System.out.println("--------------"+body.length()+"----------------");
        int begin = body.indexOf("[");
        int end = body.lastIndexOf("]");
        body = (String) body.subSequence(begin, end + 1);
        parse();
    }

    public void selectauthor(Page1 page1) throws IOException {
        this.page = page1;
        parse2.connect(page1);
        page2 = parse2.getPage2();

    }

    public void save(int number, Context context) {
        String path;
        if (number == 1) {
            path = page2.getUrl128();
        } else if (number == 2) {
            path = page2.getUrl320();
        } else {
            path = page2.getUrl999();
        }
        try {
            //saveImg(page);
            saveMusic(page, page2, path, context);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void parse() {
        JSONArray arr = JSONArray.fromObject(body);
        list = new ArrayList<Page1>();
        for (int i = 0; i < arr.length(); i++) {
            Page1 page1 = new Page1();
            page1.setName(arr.getJSONObject(i).get("name").toString());
            page1.setAuthor(arr.getJSONObject(i).getString("author"));
            page1.setId(arr.getJSONObject(i).getString("id"));
            page1.setPic(arr.getJSONObject(i).getString("pic"));
            list.add(page1);
        }
    }

    private void saveImg(Page1 page1, Context con) throws Exception {
        URL url = new URL(page1.getPic());
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        if (conn.getResponseCode() == 200) {
            try (InputStream is = conn.getInputStream()) {
                BufferedInputStream bis = new BufferedInputStream(is);
                String imgName = page1.getAuthor() + " - " + page1.getName() + ".jpg";
                File IMG = new File("D://img", imgName);
                FileOutputStream fos = new FileOutputStream(IMG);
                BufferedOutputStream bos = new BufferedOutputStream(fos);
                byte[] b = new byte[1024];
                int len;
                while ((len = bis.read(b)) != -1) {
                    bos.write(b, 0, len);
                }
                System.out.println("保存成功:" + IMG);
                bos.close();
                bis.close();
                fos.close();
                is.close();
            }
        } else {
            System.out.println(conn.getResponseCode());
        }
    }

    private void saveMusic(Page1 page1, Page2 page2, String path, Context context) throws Exception {
        URL url = new URL(path);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        if (conn.getResponseCode() == 200) {
            InputStream is = conn.getInputStream();
            BufferedInputStream bis = new BufferedInputStream(is);

            String dirName = Environment.getExternalStorageDirectory() + "/MusicDownload/";
            File file = new File(dirName);
            if(!file.exists()) file.mkdir();
            String MusName = page1.getAuthor() + " - " + page1.getName() + ".mp3";
            File music = new File(dirName,MusName);
            FileOutputStream fos = new FileOutputStream(music);
            BufferedOutputStream bos = new BufferedOutputStream(fos);
            byte[] b = new byte[1024];
            int len;
            while ((len = bis.read(b)) != -1) {
                bos.write(b, 0, len);
            }
            System.out.println("保存成功:" + MusName);
            bos.close();
            bis.close();
            fos.close();
            is.close();
        } else {
            System.out.println(conn.getResponseCode());
        }
    }

    public ArrayList<Page1> getList() {
        return list;
    }

    public void setList(ArrayList<Page1> list) {
        this.list = list;
    }
}
