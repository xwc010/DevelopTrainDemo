package com.yifants.diskcachedemo;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.anupcowkur.reservoir.Reservoir;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.yifants.diskcachedemo.acache.ASimpleCache;
import com.yifants.diskcachedemo.cache.CacheUtils;
import com.yifants.diskcachedemo.cache.Utils;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * 小数据（10） - 平均速度
 * ACache put = 8; get = 7
 * CacheUtils put = 19; get = 9
 * Reservoir put = 10; get = 3
 * Editor put = 15; get = 14
 *
 * 大数据（100） - 平均速度
 * ACache put = 9; get = 11
 * CacheUtils put = 26; get = 12
 * Reservoir put = 30; get = 2
 * Editor put = 28; get = 21
 */
public class MainActivity extends AppCompatActivity {

    private long a_num = 0;
    private long a_total_p = 0;
    private long a_total_g = 0;

    private long c_total_p = 0;
    private long c_total_g = 0;

    private long e_total_p = 0;
    private long e_total_g = 0;

    private long r_total_p = 0;
    private long r_total_g = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Demo demo = new Demo();
        List<Demo.Item> list = new ArrayList<>();
        for (int i=0; i<100; i++){
            Demo.Item item = new Demo.Item();
            list.add(item);
        }
        demo.dd = list;

        List<Demo.ItemCa> lisct = new ArrayList<>();
        for (int i=0; i<100; i++){
            Demo.ItemCa item = new Demo.ItemCa();
            lisct.add(item);
        }
        demo.ca = lisct;

        final ASimpleCache aCache = ASimpleCache.get(this);

        Utils.init(this);
        final CacheUtils cacheUtils = CacheUtils.getInstance();

        final SharedPreferences sharedPreferences = getSharedPreferences("Demo", MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPreferences.edit();

        try {
            Reservoir.init(this, 1024*1024); //in bytes 1M
        } catch (Exception e) {
            //failure
        }

        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                a_num = 0;
                a_total_p = 0;
                a_total_g = 0;

                c_total_p = 0;
                c_total_g = 0;

                e_total_p = 0;
                e_total_g = 0;

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        boolean doThis = true;
                        while (doThis){

                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            a_num++;

                            long timeaa = System.currentTimeMillis();
                            aCache.put("dd", demo);
                            a_total_p += (System.currentTimeMillis() - timeaa);
                            Log.d("Cache", "ACache put -> " + a_total_p);

                            long timebb = System.currentTimeMillis();
                            cacheUtils.put("dd", demo);
                            c_total_p += (System.currentTimeMillis() - timebb);
                            Log.d("Cache", "CacheUtils put -> " + c_total_p);

                            long timebdb = System.currentTimeMillis();
                            try {
                                Reservoir.put("mKey", demo);
                            } catch (Exception e) {
                                //failure;
                            }
                            r_total_p += (System.currentTimeMillis() - timebdb);
                            Log.d("Cache", "Reservoir put -> " + r_total_p);


                            long timeMillis = System.currentTimeMillis();
                            editor.putString("dd", new Gson().toJson(demo));
                            editor.commit();
                            e_total_p += (System.currentTimeMillis() - timeMillis);
                            Log.d("Cache", "Editor put-> " + e_total_p);


                            long timeaa1 = System.currentTimeMillis();
                            Demo demo1 = (Demo)aCache.getAsObject("dd");
                            a_total_g += (System.currentTimeMillis() - timeaa1);
                            Log.d("Cache", "ACache get -> " + a_total_g);

                            long timebb1 = System.currentTimeMillis();
                            Demo demo2 = (Demo)cacheUtils.getSerializable("dd");
                            c_total_g += (System.currentTimeMillis() - timebb1);
                            Log.d("Cache", "CacheUtils get-> " + c_total_g);

                            long timer = System.currentTimeMillis();
                            Type resultType = new TypeToken<Demo>() {}.getType();
                            try {
                                Demo demor = Reservoir.get("myKey", resultType);
                            }
                            catch (Exception e) {
                                //failure}
                                e.printStackTrace();
                            }
                            r_total_g += (System.currentTimeMillis() - timer);
                            Log.d("Cache", "Reservoir get-> " + r_total_g);

                            try {
                                long timeMillis1 = System.currentTimeMillis();
                                Demo demo3 = new Gson().fromJson(sharedPreferences.getString("dd", ""), Demo.class);
                                e_total_g += (System.currentTimeMillis() - timeMillis1);
                                Log.d("Cache", "Editor gett-> " + e_total_g);
                            } catch (JsonSyntaxException e) {
                                e.printStackTrace();
                            }

                            if(a_num != 0 && a_num%30 == 0){
                                doThis = false;
                                Log.d("Cache", " ________________________ ");
                                Log.d("Cache", "ACache put = " + (a_total_p/a_num) + "; get = " + (a_total_g/a_num));
                                Log.d("Cache", "CacheUtils put = " + (c_total_p/a_num) + "; get = " + (c_total_g/a_num));
                                Log.d("Cache", "Reservoir put = " + (r_total_p/a_num) + "; get = " + (r_total_g/a_num));
                                Log.d("Cache", "Editor put = " + (e_total_p/a_num) + "; get = " + (e_total_g/a_num));
                            }
                        }
                    }
                }).start();

            }
        });

    }
}
