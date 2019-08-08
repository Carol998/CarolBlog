package Blog.service;

import Blog.entity.Upvote;

public interface UpvoteService {

    Upvote findByup(Upvote upvote);

    void updateUpvote(Upvote upvote);

    void addUpvote(Upvote upvote);

    /**
     * 根据文章id删除upvote信息
     *
     * @param cid
     */
    void deleteUpvoteByCid(Long cid);
}
