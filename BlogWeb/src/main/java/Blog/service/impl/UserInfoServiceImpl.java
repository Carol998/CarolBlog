package Blog.service.impl;

import Blog.dao.UserInfoMapper;
import Blog.entity.UserInfo;
import Blog.service.UserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserInfoServiceImpl implements UserInfoService {

    @Autowired
    private UserInfoMapper userInfoMapper;

    @Override
    public UserInfo findByUid(Long uid) {
        UserInfo userInfo = new UserInfo();
        userInfo.setuId(uid);
        return userInfoMapper.selectOne(userInfo);
    }

    @Override
    public void update(UserInfo userInfo) {
        userInfoMapper.updateByPrimaryKey(userInfo);
    }

    @Override
    public void addUInfo(UserInfo userInfo) {
        userInfoMapper.insert(userInfo);
    }
}
