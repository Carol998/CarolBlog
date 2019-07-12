package Blog.service.impl;

import Blog.common.PageHelper;
import Blog.entity.Comment;
import Blog.entity.UserContent;
import Blog.service.UserContentService;
import org.springframework.stereotype.Service;

import java.util.List;

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

    @Override
    public void addContent(UserContent content) {

    }

    @Override
    public List<UserContent> findByUserId(Long uid) {
        return null;
    }

    @Override
    public List<UserContent> findAll() {
        return null;
    }

    @Override
    public UserContent findById(long id) {
        return null;
    }

    @Override
    public void updateById(UserContent content) {

    }
}
