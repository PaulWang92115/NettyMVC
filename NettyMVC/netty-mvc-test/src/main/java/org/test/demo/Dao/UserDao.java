package org.test.demo.Dao;

import com.paul.ioc.annotation.Repository;

@Repository
public class UserDao {

    public String get(int id){
        if(id == 1){
            return "paul";
        }else{
            return "wang";
        }
    }
}
