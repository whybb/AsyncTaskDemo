package com.example.why.asynctask;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by why on 2017/3/20.
 */

public class NewsAdapter extends BaseAdapter implements AbsListView.OnScrollListener
{
    private List<NewsBean> mList;
    private LayoutInflater mInfalater;
    private ImageLoader mimageLoader;
    private int mSart;
    private int mend;
    public  static  String [] URLS;
    private boolean mfirst;


    public NewsAdapter(Context context, List<NewsBean> data, ListView listView)
    {
        this.mList=data;
        this.mInfalater=LayoutInflater.from(context);
         mimageLoader=new ImageLoader(listView);
        URLS=new String[data.size()];
        mfirst=true;
        for(int i=0;i<data.size();i++)
        {
            URLS[i]=data.get(i).newIconURL;
        }
        //记得注册事件
       listView.setOnScrollListener(this);
    }




    public int getCount()
    {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        ViewHolder viewHolder=null;
        if(convertView==null)
        {
            viewHolder=new ViewHolder();
            convertView=mInfalater.inflate(R.layout.item_layout,null);
            viewHolder.icon=(ImageView)convertView.findViewById(R.id.icon);
            viewHolder.title=(TextView)convertView.findViewById(R.id.textView);
            viewHolder.content=(TextView)convertView.findViewById(R.id.content);
            convertView.setTag(viewHolder);
        }
        else{
            viewHolder=(ViewHolder)convertView.getTag();
        }
        viewHolder.icon.setImageResource(R.mipmap.ic_launcher);
        String url=mList.get(position).newIconURL;
        viewHolder.icon.setTag(url);
       mimageLoader.showImageByAsyncTask(viewHolder.icon,url);//---use AsyncTask
       // mimageLoader.showImageByThread(viewHolder.icon,url);//--use handle---thread
        //new ImageLoader().showImageByThread(viewHolder.icon,mList.get(position).newIconURL);
        viewHolder.title.setText(mList.get(position).newsTitle);
        viewHolder.content.setText(mList.get(position).newContent);

        return convertView;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
    if(scrollState==SCROLL_STATE_IDLE)
    {
        mimageLoader.loadImages(mSart,mend);
        //加载可见项
    }
        else{
        //停止可见项
        mimageLoader.cancelAllTask();



    }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount)
    {
       mSart=firstVisibleItem;
        mend=firstVisibleItem+visibleItemCount;
        if(mfirst&&visibleItemCount>0)
        {
            mimageLoader.loadImages(mSart,mend);
            mfirst=false;
        }
    }

    class ViewHolder{
        public TextView title,content;
        public ImageView icon;
    }
}
