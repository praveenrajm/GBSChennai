package com.identv.voxxy.dao.impl;

import com.identv.voxxy.dao.UserDao;
import com.identv.voxxy.dto.UserLoginInfo;
import com.mongodb.WriteResult;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

/**
 * Created by Praveen on 07-01-2016.
 */
public class UserDaoImpl implements UserDao {

    private static final String COLLECTION = "userlogininfo";

    MongoTemplate mongoTemplate;

    UserDaoImpl(MongoTemplate mongoTemplate){
        this.mongoTemplate = mongoTemplate;
    }

    public void create(UserLoginInfo user) {
        if (user != null) {
            this.mongoTemplate.insert(user, COLLECTION);
        }
    }

    @Override
    public UserLoginInfo findById(int id) {
        Query query = new Query(Criteria.where("_id").is(id));
        return this.mongoTemplate.findOne(query, UserLoginInfo.class, COLLECTION);
    }

    @Override
    public int deleteById(int id) {

        Query query = new Query(Criteria.where("_id").is(id));
        WriteResult result = this.mongoTemplate.remove(query, UserLoginInfo.class,
                COLLECTION);
        return result.getN();
    }

    @Override
    public void update(UserLoginInfo user) {
        if (user != null) {
            this.mongoTemplate.save(user, COLLECTION);
        }
    }

    @Override
    public List<UserLoginInfo> findAll() {
        return (List<UserLoginInfo>) mongoTemplate.findAll(UserLoginInfo.class,
                COLLECTION);
    }
}
