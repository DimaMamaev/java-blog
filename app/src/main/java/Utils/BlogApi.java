package Utils;

import android.app.Application;

public class BlogApi extends Application {
    private  String username;
    private  String userId;
    private static BlogApi instance;

    public static  BlogApi getInstance() {
        if (instance == null) instance = new BlogApi();
        return instance;
    };

    public BlogApi () {}

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
