package Blog.service;

import Blog.entity.Upvote;

public interface UpvoteService {

    Upvote findByup(Upvote upvote);

    void updateUpvote(Upvote upvote);

    void addUpvote(Upvote upvote);
}
