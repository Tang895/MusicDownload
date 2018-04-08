package com.glj.user.musicdownload;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private ListView listView;
    private Button button;
    private EditText editText;
    private ParsePage1 parse1;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            if (msg.arg1 == 1) {
                ParsePage1 parse1 = (ParsePage1) msg.obj;
                listView.setAdapter(new MyAdapter(getApplicationContext(), parse1.getList()));
            }
            if (msg.what == 1) {

                Toast.makeText(getApplicationContext(), "下载成功", Toast.LENGTH_SHORT).show();

            }
            if(msg.arg2 == 1){
                Toast.makeText(getApplicationContext(),"开始下载..",Toast.LENGTH_SHORT).show();
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = findViewById(R.id.lv_song);
        button = findViewById(R.id.btn_find);
        button.setOnClickListener(this);
        editText = findViewById(R.id.et_name);

    }

    public void find() {

        String name = editText.getText().toString();
        try {
            parse1 = new ParsePage1();
            parse1.connect(name);
            Message msg = new Message();
            msg.obj = parse1;
            msg.arg1 = 1;
            handler.sendMessage(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }


    @Override
    public void onClick(View v) {
        new Thread() {
            @Override
            public void run() {
                System.out.println("onClickfind" + Thread.currentThread().getName());
                find();
            }
        }.start();

    }


    private class MyAdapter extends BaseAdapter {
        private Context context;
        private List<Page1> list;

        public MyAdapter(Context context, List<Page1> list) {
            this.context = context;
            this.list = list;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            Page1 page1 = list.get(position);
            View item = convertView;
            final ViewHolder viewHolder;
            if (convertView == null) {
                item = View.inflate(getApplicationContext(), R.layout.item, null);
                viewHolder = new ViewHolder();
                viewHolder.tv_name = item.findViewById(R.id.tv_name);
                viewHolder.tv_author = item.findViewById(R.id.tv_author);
                viewHolder.spinner = item.findViewById(R.id.s_type);
                viewHolder.download = item.findViewById(R.id.btn_download);
                item.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            viewHolder.tv_name.setText(page1.getName());
            viewHolder.tv_name.setTextColor(Color.BLACK);

            viewHolder.tv_author.setText(page1.getAuthor());
            viewHolder.tv_author.setTextColor(Color.BLACK);

            viewHolder.spinner.setBackgroundColor(Color.GRAY);
            String[] types = {"普通音质", "高等音质", "无损音质"};
            viewHolder.spinner.setAdapter(new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, types));


            viewHolder.download.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Page1 page1 = list.get(position);
                    page1.setType(viewHolder.spinner.getSelectedItem().toString());
                    final Page1 page = page1;
                    new Thread() {
                        @Override
                        public void run() {
                            try {
                                parse1.selectauthor(page);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            Message msg1 = new Message();
                            msg1.arg2 = 1;
                            handler.sendMessage(msg1);
                            if (page.getType().equals("普通音质")) {
                                parse1.save(1, getApplicationContext());
                            } else if (page.getType().equals("高等音质")) {
                                parse1.save(2, getApplicationContext());
                            } else {
                                parse1.save(3, getApplicationContext());
                            }
                            Message msg = new Message();
                            msg.obj = page;
                            msg.what = 1;
                            handler.sendMessage(msg);
                        }
                    }.start();

                }
            });
            return item;
        }
    }

    class ViewHolder {
        TextView tv_name;
        TextView tv_author;
        Spinner spinner;
        Button download;
    }
}
