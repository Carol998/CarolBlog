package Blog.service;

import Blog.common.PageHelper;
import Blog.entity.Comment;
import Blog.entity.UserContent;

public interface UserContentService {
    PageHelper.Page<UserContent> findAll(UserContent content, Integer pageNum, Integer pageSize);
    PageHelper.Page<UserContent> findAll(UserContent content, Comment comment, Integer pageNum, Integer pageSize);
    PageHelper.Page<UserContent> findAllByUpvote(UserContent content, Integer pageNum, Integer pageSize);
}
