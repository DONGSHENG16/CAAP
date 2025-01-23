package com.bjfu.springboot.config.interceptor;

import cn.hutool.core.util.StrUtil;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.bjfu.springboot.common.Constants;
import com.bjfu.springboot.config.AuthAccess;
import com.bjfu.springboot.entity.User;
import com.bjfu.springboot.exception.ServiceException;
import com.bjfu.springboot.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class JwtInterceptor implements HandlerInterceptor {

    @Autowired
    private IUserService userService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String token = request.getHeader("token");

        // If the handler is not mapped to a method, pass through
        if (!(handler instanceof HandlerMethod)) {
            return true;
        } else {
            HandlerMethod h = (HandlerMethod) handler;
            AuthAccess authAccess = h.getMethodAnnotation(AuthAccess.class);
            if (authAccess != null) {
                return true;
            }
        }
        // Perform authentication
        if (StrUtil.isBlank(token)) {
            throw new ServiceException(Constants.CODE_401, "No token, please log in again");
        }
        // Get the user id from the token
        String userId;
        try {
            userId = JWT.decode(token).getAudience().get(0);
        } catch (JWTDecodeException j) {
            throw new ServiceException(Constants.CODE_401, "Token verification failed, please log in again");
        }
        // Query the database with the user id from the token
        User user = userService.getById(userId);
        if (user == null) {
            throw new ServiceException(Constants.CODE_401, "User does not exist, please log in again");
        }
        // Verify the token using the user's password as the signature
        JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256(user.getPassword())).build();
        try {
            jwtVerifier.verify(token); // Verify the token
        } catch (JWTVerificationException e) {
            throw new ServiceException(Constants.CODE_401, "Token verification failed, please log in again");
        }
        return true;
    }
}
