package org.test.demo.Controller;


import org.test.demo.Service.UserService;
import com.paul.http.HttpUtil;
import com.paul.ioc.annotation.Autowired;
import com.paul.ioc.annotation.Controller;
import com.paul.mvc.annotation.RequestMapping;
import com.paul.mvc.annotation.RequestParam;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @RequestMapping("/getUser")
    public FullHttpResponse getUserById(FullHttpRequest request,@RequestParam("userId") int id,@RequestParam("name") String name){

        System.out.println(request.uri());
        System.out.println(request);
        System.out.println(id);
        System.out.println(name);
        String res = userService.getUser(id);
        return HttpUtil.constructText(res);
    }

}
