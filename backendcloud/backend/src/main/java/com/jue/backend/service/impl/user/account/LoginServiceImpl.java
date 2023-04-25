package com.jue.backend.service.impl.user.account;

import com.jue.backend.pojo.User;
import com.jue.backend.service.impl.utils.UserDetailsImpl;
import com.jue.backend.service.user.account.LoginService;
import com.jue.backend.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class LoginServiceImpl implements LoginService {

    @Autowired
    private AuthenticationManager authenticationManager;


    @Override
    public Map<String, String> getToken(String username, String password) {
        // 将输入的名和密码，封装为加密字符串
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(username, password);
        // 验证该Token
        Authentication authenticate = authenticationManager.authenticate(authenticationToken);
        // 若验证失败，则抛出异常

        UserDetailsImpl loginUser = (UserDetailsImpl)  authenticate.getPrincipal();

        User user = loginUser.getUser();
        String jwt = JwtUtil.createJWT(user.getId().toString()); // JWT（）
        Map<String, String> map = new HashMap<>();
        map.put("error_message", "success");
        map.put("token", jwt); // 此处自定义名称，只要和前端取名对应起来
        System.out.println(map);
        return map;
    }
}
