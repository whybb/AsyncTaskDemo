package com.example.why.asynctask;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ListView listView;
    private static String url= "http://www.imooc.com/api/teacher?type=4&num=30";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView=(ListView)findViewById(R.id.listview);
        myAsyncTask mTask= new myAsyncTask();
        mTask.execute(url);
    }
    private List<NewsBean>getJsonData(String url)
    {
        List<NewsBean> newsBeanList=new ArrayList<>();
         try
         {

            String jason=read(new URL(url).openStream());
             JSONObject jsonObject;
             NewsBean newsBean;
             try {
                 jsonObject = new JSONObject(jason);
                 JSONArray jsonArray = jsonObject.getJSONArray("data");
                 for (int i = 0; i < jsonArray.length(); i++) {
                     jsonObject = jsonArray.getJSONObject(i);
                     newsBean=new NewsBean();
                     newsBean.newIconURL=jsonObject.getString("picSmall");
                     newsBean.newsTitle=jsonObject.getString("name");
                     newsBean.newContent=jsonObject.getString("description");
                     newsBeanList.add(newsBean);

                 }
             }
                 catch(JSONException e){
                     e.printStackTrace();
                 }

             Log.d("sys",jason);
         }  catch (IOException e)
         {
             e.printStackTrace();
         }
       return newsBeanList;

    }

    private String read(InputStream is)
    {
        InputStreamReader isr;
        String result="";

        try{
            String line="";
            isr=new InputStreamReader(is,"utf-8");
            BufferedReader br=new BufferedReader(isr);
            while((line=br.readLine())!=null)
            {
                result+=line;
            }

        }catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }catch (IOException e)
        {
            e.printStackTrace();
        }
        return result;

    }




    class myAsyncTask extends AsyncTask<String,Void,List<NewsBean>>
    {

        protected List<NewsBean> doInBackground(String ...params)
        {
            return getJsonData(params[0]);
        }


       // @Override
        protected void onPostExecute( List<NewsBean> newsBeen) {

            super.onPostExecute(newsBeen);
            NewsAdapter adapter=new NewsAdapter(MainActivity.this,newsBeen,listView);
            listView.setAdapter(adapter);
        }
    }
}
