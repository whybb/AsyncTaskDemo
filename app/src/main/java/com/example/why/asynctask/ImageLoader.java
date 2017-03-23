package com.example.why.asynctask;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.LruCache;
import android.widget.ImageView;
import android.widget.ListView;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by why on 2017/3/20.
 */

public class ImageLoader
{
   private ImageView mimageView;
   private  String murl;


    private LruCache<String,Bitmap> mCaches;
    private ListView listView;
    private Set<NewAsyncTask> mtask;

    public ImageLoader(ListView listView)
    {
       this.listView=listView;
       mtask=new HashSet<>();
        int maxMemory=(int)Runtime.getRuntime().maxMemory();
        int cachesize=maxMemory/4;
        mCaches=new LruCache<String,Bitmap>(cachesize)
        {

            protected int sizeOf(String key, Bitmap value) {
                //return super.sizeOf(key, value);
                //在每次存入缓存时调用
                return  value.getByteCount();

            }
        };


    }

    //增加到缓存
    public void addBitmapToCache(String url,Bitmap bitmap)
    {
         if(getBitmapFromcache(url)==null)
         {
             mCaches.put(url,bitmap);
         }
    }

    //从缓存中获取数据
    public Bitmap getBitmapFromcache(String url)
    {
        return mCaches.get(url);
    }



    private Handler handler=new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(mimageView.getTag().equals(murl))
            {
                mimageView.setImageBitmap((Bitmap) msg.obj);
            }

        }
    };

    //用来加载从start到end的所有图片
    public  void  loadImages(int start ,int end)
    {
        for(int i=start;i<end;i++)
        {
            String url=NewsAdapter.URLS[i];
            Bitmap bitmap=getBitmapFromcache(url);
            if(bitmap==null)
            {
                NewAsyncTask task=new NewAsyncTask(url);
                task.execute(url);
                mtask.add(task);
                //new ImageLoader.NewAsyncTask(imageView,url).execute(url);

            }else
            {
                ImageView imageView=(ImageView)listView.findViewWithTag(url);
                imageView.setImageBitmap(bitmap);
            }

        }

    }

 public  void cancelAllTask()
 {
     if(mtask!=null)
     {
         for(NewAsyncTask task:mtask)
         {
             task.cancel(false);
         }
     }


 }


    public void showImageByThread(ImageView imageView,final String url){

        mimageView=imageView;
        murl=url;

        new Thread(){

            public void run()
            {
                super.run();
                Bitmap bitmap=getBitmap(url);
                Message message= Message.obtain();
                message.obj=bitmap;
                handler.sendMessage(message);
            }
        }.start();

    }

    public Bitmap getBitmap(String urlstring)
    {
        Bitmap bitmap;
        InputStream is=null;
        try{
            URL url =new URL(urlstring);
            HttpURLConnection connection=( HttpURLConnection)url.openConnection();
            is=new BufferedInputStream(connection.getInputStream());
            bitmap= BitmapFactory.decodeStream(is);
            connection.disconnect();
            return bitmap;

        }catch (java.io.IOException e)
        {
            e.printStackTrace();
        }
        finally {
            try {
                is.close();
            } catch (IOException e) {
               e.printStackTrace();
            }
        }


        return null;
    }

    public void showImageByAsyncTask(ImageView imageView,String url)
    {
        //从缓存中取出对应的图片
        Bitmap bitmap=getBitmapFromcache(url);
        if(bitmap==null)
        {
            imageView.setImageResource(R.mipmap.ic_launcher);

        }else
        {
            imageView.setImageBitmap(bitmap);
        }

    }

    private class NewAsyncTask extends AsyncTask<String,Void,Bitmap>
    {
        //private ImageView imageView;
        private String murl;
        public NewAsyncTask(String url)
        {
           // this.imageView=imageView;
            murl=url;

        }

        @Override
        protected Bitmap doInBackground(String... params) {
           //获取图片从网络上
            Bitmap bitmap =getBitmapFromcache(params[0]);
            String url=params[0];
            if(bitmap!=null)
            {
                //加入缓存
                addBitmapToCache(url,bitmap);
            }
            return getBitmap(params[0]);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);

            ImageView imageView=(ImageView)listView.findViewWithTag(murl);
          if(imageView!=null&&bitmap!=null)
          {
              imageView.setImageBitmap(bitmap);

          }
           /* if(mimageView.getTag().equals(murl))
            {
                imageView.setImageBitmap(bitmap);
            }*/

        }
    }


}
