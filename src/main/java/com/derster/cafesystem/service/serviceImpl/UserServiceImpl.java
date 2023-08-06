package com.derster.cafesystem.service.serviceImpl;

import com.derster.cafesystem.constants.CafeConstants;
import com.derster.cafesystem.dao.UserDao;
import com.derster.cafesystem.jwt.CustomerUsersDatailsService;
import com.derster.cafesystem.jwt.JwtFilter;
import com.derster.cafesystem.jwt.JwtUtil;
import com.derster.cafesystem.pojo.User;
import com.derster.cafesystem.service.UserService;
import com.derster.cafesystem.utils.CafeUtils;
import com.derster.cafesystem.utils.EmailUtils;
import com.derster.cafesystem.wrapper.UserWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
public class UserServiceImpl implements UserService {
    @Autowired
    UserDao userDao;
    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    CustomerUsersDatailsService customerUsersDatailsService;
    @Autowired
    JwtUtil jwtUtil;
    @Autowired
    JwtFilter jwtFilter;
    @Autowired
    EmailUtils emailUtils;
    @Override
    public ResponseEntity<String> signUp(Map<String, String> requestMap) {
        log.info("Inside signup {}", requestMap);
        try {
            if (validateSignUpMap(requestMap)){
                User user = userDao.findByEmailId(requestMap.get("email"));
                if (Objects.isNull(user)){
                    userDao.save(getUserFromMap(requestMap));
                    return CafeUtils.getResponseEntity(CafeConstants.SUCCESSFULLY_REGISTRERED, HttpStatus.OK);
                }else{
                    return CafeUtils.getResponseEntity(CafeConstants.EMAIL_ALREADY_EXIST, HttpStatus.BAD_REQUEST);
                }
            }else{
                return CafeUtils.getResponseEntity(CafeConstants.INVALID_DATA, HttpStatus.BAD_REQUEST);
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<String> login(Map<String, String> requestMap) {
        log.info("Inside login");

        try{
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(requestMap.get("email"), requestMap.get("password"))
            );

            if (auth.isAuthenticated()){
                if (customerUsersDatailsService.getUserDetail().isStatus()){
                    return new ResponseEntity<String>("{\"token\":\""+jwtUtil.generateToken(customerUsersDatailsService.getUserDetail().getEmail(), customerUsersDatailsService.getUserDetail().getRole())+"\"}", HttpStatus.OK);
                }else {
                    return CafeUtils.getResponseEntity(CafeConstants.WAIT_FOR_ADMIN_APPROVAL, HttpStatus.BAD_REQUEST);
                }
            }
        }catch (Exception ex){
            log.info("{}", ex);
        }
        return CafeUtils.getResponseEntity(CafeConstants.BAD_CREDENTIAL, HttpStatus.BAD_REQUEST);
    }

    @Override
    public ResponseEntity<List<UserWrapper>> getAllUsers() {
        try{
            if (jwtFilter.isAdmin()){
                return new ResponseEntity<>(userDao.getAllUsers(), HttpStatus.OK);
            }else{
                return new ResponseEntity<>(new ArrayList<>(), HttpStatus.UNAUTHORIZED);
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }

        return new ResponseEntity<>(new ArrayList<>(), HttpStatus.BAD_REQUEST);
    }

    @Override
    public ResponseEntity<String> update(Map<String, String> requestMap) {
        log.info("Inside login");

        try{
            if (jwtFilter.isAdmin()){
                Optional<User> optional = userDao.findById(Integer.parseInt(requestMap.get("id")));

                if (!optional.isEmpty()){
                    userDao.updateStatus(Boolean.parseBoolean(requestMap.get("status")), Integer.parseInt(requestMap.get("id")));
                    sendMailToAllAdmin(Boolean.parseBoolean(requestMap.get("status")), optional.get().getEmail(), userDao.getAllAdmins());
                    return CafeUtils.getResponseEntity("User status updated successfully", HttpStatus.OK);
                }else{
                    return CafeUtils.getResponseEntity("User not exist", HttpStatus.OK);
                }
            }else{
                return CafeUtils.getResponseEntity(CafeConstants.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
            }
        }catch (Exception ex){
            log.info("{}", ex);
        }
        return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> checkToken() {
        return CafeUtils.getResponseEntity("true", HttpStatus.OK);
    }

    @Override
    public ResponseEntity<String> changePassword(Map<String, String> requestMap) {
        try{
            User userObj = userDao.findByEmail(jwtFilter.getCurrentUser());

            if (!userObj.getPassword().equals(requestMap.get("oldPassword"))) {
                return CafeUtils.getResponseEntity("Incorrect old password", HttpStatus.BAD_REQUEST);
            }else if(userObj.getPassword().equals(requestMap.get("newPassword"))){
                return CafeUtils.getResponseEntity("The new password must be different to old password", HttpStatus.BAD_REQUEST);
            }
            userObj.setPassword(requestMap.get("newPassword"));
            userDao.save(userObj);
            return CafeUtils.getResponseEntity("Password updated successfully", HttpStatus.OK);

        }catch (Exception ex){
            log.info("{}", ex);
        }
        return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> forgotPassword(Map<String, String> requestMap) {
        try {
            User user = userDao.findByEmail(requestMap.get("email"));
            log.info("{} ----email", user.getEmail());
            if (Strings.isNotEmpty(user.getEmail())){
                emailUtils.forgotMail(user.getEmail(), "Credential by Cafe Management System", user.getPassword());
                return CafeUtils.getResponseEntity("Check your mail for Credentials", HttpStatus.OK);
            }

        }catch (Exception ex){
            ex.printStackTrace();
        }
        return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private void sendMailToAllAdmin(Boolean status, String user, List<String> allAdmins) {
        allAdmins.remove(jwtFilter.getCurrentUser());

        if (status){
            emailUtils.sendSimpleMessage(jwtFilter.getCurrentUser(), "Account Approved", "USER:-"+user+"\n is approved by \nADMIN: - "+jwtFilter.getCurrentUser(), allAdmins);
        }else {
            emailUtils.sendSimpleMessage(jwtFilter.getCurrentUser(), "Account disabled", "USER:-"+user+"\n is disabled by \nADMIN: - "+jwtFilter.getCurrentUser(), allAdmins);
        }
    }
    private boolean validateSignUpMap(Map<String, String> requestMap){
        return requestMap.containsKey("name") && requestMap.containsKey("contactNumber") && requestMap.containsKey("email") && requestMap.containsKey("password");
    }

    private User getUserFromMap(Map<String, String> requestMap){
        User user = new User();
        user.setName(requestMap.get("name"));
        user.setContactNumber(requestMap.get("contactNumber"));
        user.setEmail(requestMap.get("email"));
        user.setPassword(requestMap.get("password"));
        user.setStatus(false);
        user.setRole("user");
        return user;
    }
}
