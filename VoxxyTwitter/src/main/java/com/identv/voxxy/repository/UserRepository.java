package com.identv.voxxy.repository;

import com.identv.voxxy.dto.UserLoginInfo;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Component;

/**
 * Created by Praveen on 06-01-2016.
 */

@Component
public interface UserRepository extends MongoRepository<UserLoginInfo, String> {


}
