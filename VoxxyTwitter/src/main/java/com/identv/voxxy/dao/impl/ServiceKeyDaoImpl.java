package com.identv.voxxy.dao.impl;

import com.identv.voxxy.dao.ServiceKeyDao;
import com.identv.voxxy.dto.ServiceKeyInfo;
import com.mongodb.WriteResult;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

/**
 * Created by Praveen on 07-01-2016.
 */
public class ServiceKeyDaoImpl implements ServiceKeyDao {
    private static final String COLLECTION = "servicekeyinfo";

    MongoTemplate mongoTemplate;

    ServiceKeyDaoImpl(MongoTemplate mongoTemplate){
        this.mongoTemplate = mongoTemplate;
    }

    public void create(ServiceKeyInfo serviceKey) {
        if (serviceKey != null) {
            this.mongoTemplate.insert(serviceKey, COLLECTION);
        }
    }

    @Override
    public ServiceKeyInfo findById(int id) {
        Query query = new Query(Criteria.where("_id").is(id));
        return this.mongoTemplate.findOne(query, ServiceKeyInfo.class, COLLECTION);
    }

    @Override
    public int deleteById(int id) {

        Query query = new Query(Criteria.where("_id").is(id));
        WriteResult result = this.mongoTemplate.remove(query, ServiceKeyInfo.class,
                COLLECTION);
        return result.getN();
    }

    @Override
    public void update(ServiceKeyInfo keyInfo) {
        if (keyInfo != null) {
            this.mongoTemplate.save(keyInfo, COLLECTION);
        }
    }

    @Override
    public List<ServiceKeyInfo> findAll() {
        return (List<ServiceKeyInfo>) mongoTemplate.findAll(ServiceKeyInfo.class,
                COLLECTION);
    }
}
