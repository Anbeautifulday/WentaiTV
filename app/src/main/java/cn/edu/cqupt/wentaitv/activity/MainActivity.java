package cn.edu.cqupt.wentaitv.activity;


import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;

import com.example.servicebestpractice.DownloadService;

import cn.edu.cqupt.wentaitv.R;
import cn.edu.cqupt.wentaitv.adapter.ShowInfoRecAdapter;
import cn.edu.cqupt.wentaitv.database.MyDatabaseHelper;
import cn.edu.cqupt.wentaitv.json.MyJsonUtil;
import cn.edu.cqupt.wentaitv.json.VideoInfo;
import cn.edu.cqupt.wentaitv.permission.CheckPermissionsActivity;
import cn.edu.cqupt.wentaitv.util.MyHttpUtil;

public class MainActivity extends CheckPermissionsActivity {

    private DownloadService.DownloadBinder downloadBinder;
    ServiceConnection connection = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            downloadBinder = (DownloadService.DownloadBinder) service;
        }

    };

    private MyDatabaseHelper dbHelper;
    private RecyclerView videosRecycler;
    private ShowInfoRecAdapter adapter;
    private SwipeRefreshLayout refreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //dbHelper = new MyDatabaseHelper(MainActivity.this, "VideoMsg.db", null, 2);
        //dbHelper.getWritableDatabase();
        Intent intent = new Intent(this, DownloadService.class);
        startService(intent); // 启动服务
        bindService(intent, connection, BIND_AUTO_CREATE); // 绑定服务
        try {
            MyHttpUtil.get(setParams(), new MyHttpUtil.Callback() {
                @Override
                public void onResponse(String response) {
                    VideoInfo info = MyJsonUtil.parseJsonWithGson(response, VideoInfo.class);
                    adapter = new ShowInfoRecAdapter(MainActivity.this, downloadBinder, info.getShowapi_res_body().getPageBean().getContentlist());
                    videosRecycler = (RecyclerView) findViewById(R.id.showVideoInfo);
                    videosRecycler.setLayoutManager(new LinearLayoutManager(MainActivity.this));
                    videosRecycler.setAdapter(adapter);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.refresh);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                refreshTest();
            }
        });

    }

    private void refreshTest() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // TODO: 17-5-21 刷新待实现 ////
                        refreshLayout.setRefreshing(false);
                    }
                });
            }
        }).start();
    }

    public String setParams() {
        String appid = "&showapi_appid=38563";
        String secret = "&showapi_sign=38fec45c9235493db3084f1269a34974";
        return "http://route.showapi.com/255-1?&showapi_appid=38563&showapi_sign=38fec45c9235493db3084f1269a34974&type=41&page=1";
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, HistoryActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(connection);
    }


}
