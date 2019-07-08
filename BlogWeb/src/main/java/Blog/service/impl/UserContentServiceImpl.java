package Blog.service.impl;

import Blog.common.PageHelper;
import Blog.entity.Comment;
import Blog.entity.UserContent;
import Blog.service.UserContentService;
import org.springframework.stereotype.Service;

@Service
public class UserContentServiceImpl implements UserContentService {
    @Override
    public PageHelper.Page<UserContent> findAll(UserContent content, Integer pageNum, Integer pageSize) {
        return null;
    }

    @Override
    public PageHelper.Page<UserContent> findAll(UserContent content, Comment comment, Integer pageNum, Integer pageSize) {
        return null;
    }

    @Override
    public PageHelper.Page<UserContent> findAllByUpvote(UserContent content, Integer pageNum, Integer pageSize) {
        return null;
    }
}
