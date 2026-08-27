package com.genesys.service;

import com.genesys.dto.ResetPasswordRequest;
import com.genesys.dto.UserCreationRequest;
import com.genesys.dto.UserDtoResponse;
import com.genesys.entity.User;
import com.genesys.exception.BaseException;
import com.genesys.exception.ErrorMessage;
import com.genesys.exception.MessageType;
import com.genesys.repo.UserRepository;
import com.genesys.security.CustomUserDetails;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UsersService {


    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    public UsersService(UserRepository userRepository){
        this.userRepository=userRepository;
    }

    @Transactional
    public List<UserDtoResponse> fetchUsers(){

        List<User> users = userRepository.findAll();
        List<UserDtoResponse> responseList = new ArrayList<>();

        if(!users.isEmpty()){
            System.out.println("Here");
            for(User u : users){
                UserDtoResponse dto = new UserDtoResponse();
                dto.setId(u.getId().toString());
                dto.setUsername(u.getUsername());
                dto.setRole(u.getRole());
                dto.setActive(u.getActive());
                responseList.add(dto);

            }
        }

        return responseList;

    }

    @Transactional
    public void createUser(UserCreationRequest request){

        if(userRepository.findByUsername(request.getUsername()).isPresent()){
            throw new BaseException(new ErrorMessage(MessageType.ALREADY_EXIST," | "+request.getUsername()));
        }

        User user = new User(request.getUsername(), passwordEncoder.encode(request.getPassword()),
                request.getRole());

        userRepository.save(user);
    }

    @Transactional
    public void resetPassword(ResetPasswordRequest request, UUID id){


        String newPassword = request.getNewPassword();
        Optional<User> optional;
        if((optional=userRepository.findById(id)).isEmpty()){
            throw new BaseException(new ErrorMessage(MessageType.NO_RECORD_EXIST," | "+id.toString()));

        }
        User user = optional.get();
        user.setPassword(passwordEncoder.encode(newPassword));

    }

    @Transactional
    public void deactivate(UUID id){

        Optional<User> optional;
        if((optional=userRepository.findById(id)).isEmpty()){
            throw new BaseException(new ErrorMessage(MessageType.NO_RECORD_EXIST," | "+id.toString()));

        }

        User user = optional.get();
        user.deactivate();
    }


}
