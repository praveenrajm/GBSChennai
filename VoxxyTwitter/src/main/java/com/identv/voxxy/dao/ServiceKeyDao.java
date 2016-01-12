package com.identv.voxxy.dao;

import com.identv.voxxy.dto.ServiceKeyInfo;

import java.util.List;

/**
 * Created by Praveen on 07-01-2016.
 */
public interface ServiceKeyDao {
    public void create(ServiceKeyInfo serviceKey);

    public void update(ServiceKeyInfo serviceKey);

    public int deleteById(int id);

    public ServiceKeyInfo findById(int id);

    public List<ServiceKeyInfo> findAll();
}
