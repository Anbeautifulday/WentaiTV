package cn.edu.cqupt.wentaitv.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.servicebestpractice.DownloadService;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import cn.edu.cqupt.wentaitv.R;
import cn.edu.cqupt.wentaitv.activity.SurfaceActivity;
import cn.edu.cqupt.wentaitv.json.VideoInfo;
import cn.edu.cqupt.wentaitv.util.CircleImageUtil;
import cn.edu.cqupt.wentaitv.util.MyFirstFrameUtil;
import cn.edu.cqupt.wentaitv.util.MyImageUtil;
import cn.edu.cqupt.wentaitv.util.ToastUtil;

public class ShowInfoRecAdapter extends RecyclerView.Adapter<ShowInfoRecAdapter.ViewHolder> {

    private static final String TAG = "ShowInfoRecAdapter";

    private Context context;
    private DownloadService.DownloadBinder downloadBinder;
    private List<VideoInfo.ShowBody.PageBean.BodyInfo> videos = new ArrayList<>();

    public ShowInfoRecAdapter(Context context, DownloadService.DownloadBinder downloadBinder, List<VideoInfo.ShowBody.PageBean.BodyInfo> videos) {
        this.context = context;
        this.videos = videos;
        this.downloadBinder = downloadBinder;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.video_item, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, SurfaceActivity.class);
                intent.putExtra("videoUri", videos.get(viewType).getVideo_uri());
                intent.putExtra("videos", (Serializable) videos);
                intent.putExtra("index", viewType);
                context.startActivity(intent);
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        VideoInfo.ShowBody.PageBean.BodyInfo bodyInfo = videos.get(position);

        MyImageUtil.get(bodyInfo.getProfile_image(), new MyImageUtil.Callback_img() {
            @Override
            public void onResponse(Bitmap response) {
                holder.authorImg.setImageBitmap(response);
            }
        });

        MyFirstFrameUtil.get(bodyInfo.getVideo_uri(), new MyImageUtil.Callback_img() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onResponse(Bitmap response) {
                final Drawable drawable = new BitmapDrawable(response);
                holder.firstFrame.setBackground(drawable);
                //holder.firstFrame.setImageBitmap(response);
            }
        });
        //String strDelSpace = str.replaceAll(regStartSpace, "").replaceAll(regEndSpace, "");
        holder.name.setText(bodyInfo.getName());
        holder.detail.setText(bodyInfo.getText().replace(" ", ""));
        holder.time.setText(bodyInfo.getCreate_time());

        holder.download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastUtil.show(context, R.string.beginDownload);
                downloadBinder.startDownload(videos.get(position).getVideo_uri());
            }
        });
    }

    @Override
    public int getItemCount() {
        return videos.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        View view;
        CircleImageUtil authorImg;
        TextView name;
        TextView detail;
        TextView time;
        ImageView firstFrame;
        ImageButton download;

        public ViewHolder(View itemView) {
            super(itemView);
            this.view = itemView;
            authorImg = (CircleImageUtil) itemView.findViewById(R.id.authorImg);
            name = (TextView) itemView.findViewById(R.id.authorName);
            detail = (TextView) itemView.findViewById(R.id.detail);
            time = (TextView) itemView.findViewById(R.id.showTime);
            firstFrame = (ImageView) itemView.findViewById(R.id.firstFrame);
            download = (ImageButton) itemView.findViewById(R.id.download);
        }
    }

}
