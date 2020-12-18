package ui;

import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.blog.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import model.Post;

public class PostRecyclerAdapter extends RecyclerView.Adapter<PostRecyclerAdapter.ViewHolder> {
    private Context context;
    private List<Post> postList;

    public PostRecyclerAdapter(Context context, List<Post> postList) {
        this.context = context;
        this.postList = postList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.post_row, parent, false);
        return new ViewHolder(view, context);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Post post = postList.get(position);
        String imageUrl;

        String timeAgo = (String) DateUtils.getRelativeTimeSpanString(post.getTimeAdd().getSeconds() * 1000);

        holder.postTitleView.setText(post.getTitle());
        holder.postDescrView.setText(post.getDescription());
        holder.postDateView.setText(timeAgo);


        imageUrl = post.getImageUrl();

        Picasso.get().load(imageUrl)
                .placeholder(R.drawable.common_google_signin_btn_text_light_normal_background)
                .fit().into(holder.postImgView);

    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView postTitleView, postDescrView, postDateView;
        public ImageView postImgView;
        String userId, username;


        public ViewHolder(@NonNull View itemView, Context ctx) {
            super(itemView);
            context = ctx;

            postTitleView = itemView.findViewById(R.id.post_row_title);
            postDescrView = itemView.findViewById(R.id.post_row_desc);
            postDateView = itemView.findViewById(R.id.post_row_timestamp);
            postImgView = itemView.findViewById(R.id.post_row_image);

        }
    }
}
