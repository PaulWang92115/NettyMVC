package org.test.demo.Service;

import org.test.demo.Dao.UserDao;
import com.paul.ioc.annotation.Autowired;
import com.paul.ioc.annotation.Service;

@Service("userService")
public class UserServiceImpl implements UserService {

    @Autowired("userDao")
    private UserDao userDao;

    @Override
    public String getUser(int id) {
        return userDao.get(id);
    }
}
