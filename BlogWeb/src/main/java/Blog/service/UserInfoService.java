package Blog.service;

import Blog.entity.UserInfo;

public interface UserInfoService {

    /**
     * 查找用户信息
     * @param uid
     * @return
     */
    UserInfo findByUid(Long uid);

    /**
     * 更新用户信息
     * @param userInfo
     */
    void update(UserInfo userInfo);

    /**
     * 更新用户信息
     * @param userInfo
     */
    void addUInfo(UserInfo userInfo);
}
