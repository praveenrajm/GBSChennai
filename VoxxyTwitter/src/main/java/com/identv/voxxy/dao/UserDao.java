package com.identv.voxxy.dao;

import com.identv.voxxy.dto.UserLoginInfo;

import java.util.List;

/**
 * Created by Praveen on 07-01-2016.
 */

public interface UserDao {
    public void create(UserLoginInfo employee);

    public void update(UserLoginInfo employee);

    public int deleteById(int id);

    public UserLoginInfo findById(int id);

    public List<UserLoginInfo> findAll();
}
