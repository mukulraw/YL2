package com.yl.youthlive;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import com.yl.youthlive.INTERFACE.AllAPIs;
import com.yl.youthlive.timelinePOJO.Datum;
import com.yl.youthlive.timelinePOJO.timelineBean;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import tcking.github.com.giraffeplayer2.VideoInfo;
import tcking.github.com.giraffeplayer2.VideoView;


public class Timeline extends Fragment {

    //RecyclerView grid;
    RecyclerView grid2;
    LinearLayoutManager manager;
    LiveAdapter2 adapter2;
    GridLayoutManager manager2;
    List<Datum> list;
    ProgressBar progress;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.timeline , container , false);


        list = new ArrayList<>();

        //grid = (RecyclerView)view.findViewById(R.id.grid);
        grid2 = (RecyclerView)view.findViewById(R.id.grid2);
        progress = (ProgressBar)view.findViewById(R.id.progress);


        manager = new LinearLayoutManager(getContext() , LinearLayoutManager.HORIZONTAL , false);
        manager2 = new GridLayoutManager(getContext() , 1);

        adapter2 = new LiveAdapter2(getContext() , list);

        //grid.setAdapter(adapter);
        //grid.setLayoutManager(manager);

        grid2.setAdapter(adapter2);
        grid2.setLayoutManager(manager2);



        return view;
    }

    @Override
    public void onResume() {
        super.onResume();


        loadData();


    }


    public void loadData()
    {
        progress.setVisibility(View.VISIBLE);

        final bean b = (bean) getContext().getApplicationContext();

        final Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(b.BASE_URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        final AllAPIs cr = retrofit.create(AllAPIs.class);


        Call<timelineBean> call = cr.getTimeline(b.userId);

Log.d("userId" , b.userId);

        call.enqueue(new Callback<timelineBean>() {
            @Override
            public void onResponse(Call<timelineBean> call, Response<timelineBean> response) {

                adapter2.setGridData(response.body().getData());

                progress.setVisibility(View.GONE);

            }

            @Override
            public void onFailure(Call<timelineBean> call, Throwable throwable) {
                progress.setVisibility(View.GONE);
            }
        });
    }


    public class LiveAdapter2 extends RecyclerView.Adapter<LiveAdapter2.ViewHolder>
    {

        Context context;
        List<Datum> list = new ArrayList<>();
        String TAG = "asdas";
        private int mSeekPosition;

        public LiveAdapter2(Context context , List<Datum> list)
        {
            this.context = context;
            this.list = list;
        }

        public void setGridData(List<Datum> list)
        {
            this.list = list;
            notifyDataSetChanged();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.timeline_model , parent , false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {

            holder.setIsRecyclable(false);

            final Datum item = list.get(position);

            DisplayImageOptions options = new DisplayImageOptions.Builder().resetViewBeforeLoading(false).cacheInMemory(true).cacheOnDisk(true).build();

            ImageLoader loader = ImageLoader.getInstance();
            loader.displayImage(item.getTimelineProfileImage() , holder.image , options);

            holder.name.setText(item.getTimelineName());

            holder.likes.setText(item.getLikesCount());
            holder.views.setText(item.getViewsCount());
            holder.comments.setText(item.getCommentCount());

            holder.time.setText(item.getUploadTime());


/*
            try {

                MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                //give YourVideoUrl below
                retriever.setDataSource(item.getVideoURL() , new HashMap<String, String>());
// this gets frame at 2nd second
                Bitmap image = retriever.getFrameAtTime(2000000, MediaMetadataRetriever.OPTION_CLOSEST_SYNC);

            }catch (Exception e)
            {
                e.printStackTrace();
            }
*/



            if (Objects.equals(item.getIsLiked(), "1"))
            {

                holder.likes.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_like , 0 , 0 , 0);

            }
            else
            {
                holder.likes.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.black_heart , 0 , 0 , 0);
            }


            holder.video.getVideoInfo().setBgColor(Color.BLACK).setAspectRatio(VideoInfo.AR_MATCH_PARENT).setCurrentVideoAsCover(true);//config player
            holder.video.setVideoPath(item.getVideoURL()).setFingerprint(position);


            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent intent = new Intent(context ,SingleVideoActivity.class);
                    intent.putExtra("videoId" , item.getVideoId());
                    intent.putExtra("url" , item.getVideoURL());
                    intent.putExtra("thumb", item.getThumbURL());
                    context.startActivity(intent);

                }
            });




            /*holder.video.setMediaController(holder.controller);

            holder.play.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    holder.video.setVideoPath(item.getVideoURL());
                    holder.video.requestFocus();

                    holder.play.setVisibility(View.GONE);
                    holder.controller.setVisibility(View.VISIBLE);

                    if (mSeekPosition > 0) {
                        holder.video.seekTo(mSeekPosition);
                    }
                    holder.video.start();
                    //holder.controller.setTitle("Big Buck Bunny");
                }
            });



            holder.video.setVideoViewCallback(new UniversalVideoView.VideoViewCallback() {
                @Override
                public void onScaleChange(boolean isFullscreen) {
                }

                @Override
                public void onPause(MediaPlayer mediaPlayer) { // Video pause
                    Log.d(TAG, "onPause UniversalVideoView callback");
                }

                @Override
                public void onStart(MediaPlayer mediaPlayer) { // Video start/resume to play
                    Log.d(TAG, "onStart UniversalVideoView callback");
                }

                @Override
                public void onBufferingStart(MediaPlayer mediaPlayer) {// steam start loading
                    Log.d(TAG, "onBufferingStart UniversalVideoView callback");
                }

                @Override
                public void onBufferingEnd(MediaPlayer mediaPlayer) {// steam end loading
                    Log.d(TAG, "onBufferingEnd UniversalVideoView callback");
                }

            });
*/
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder
        {

            CircleImageView image;
            TextView name , time;
            TextView likes , views , comments;
            VideoView video;
            ImageButton play;

            public ViewHolder(View itemView) {
                super(itemView);

                image = (CircleImageView)itemView.findViewById(R.id.image);
                name = (TextView)itemView.findViewById(R.id.name);
                time = (TextView)itemView.findViewById(R.id.time);
                video = (VideoView) itemView.findViewById(R.id.video);

                play = (ImageButton)itemView.findViewById(R.id.play);


                likes = (TextView)itemView.findViewById(R.id.likes);
                views = (TextView)itemView.findViewById(R.id.views);
                comments = (TextView)itemView.findViewById(R.id.comments);

                /*itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent=new Intent(getActivity(),CommentS.class);
                        startActivity(intent);
                    }
                });*/


            }
        }
    }


}
