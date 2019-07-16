package Blog.service.impl;

import Blog.dao.UpvoteMapper;
import Blog.entity.Upvote;
import Blog.service.UpvoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UpvoteServiceImpl implements UpvoteService {

    @Autowired
    public UpvoteMapper upvoteMapper;

    @Override
    public Upvote findByup(Upvote upvote) {
        return upvoteMapper.selectOne(upvote);
    }

    @Override
    public void updateUpvote(Upvote upvote) {
        upvoteMapper.updateByPrimaryKey(upvote);
    }

    @Override
    public void addUpvote(Upvote upvote) {
        upvoteMapper.insert(upvote);
    }
}
