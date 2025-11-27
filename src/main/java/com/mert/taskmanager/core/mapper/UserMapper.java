package com.mert.taskmanager.core.mapper;

import com.mert.taskmanager.dto.request.User.UserLoginRequest;
import com.mert.taskmanager.dto.request.User.UserSignupRequest;
import com.mert.taskmanager.dto.response.UserAuthResponse;
import com.mert.taskmanager.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {


    @Mapping(target = "id", ignore = true)
    @Mapping(target = "hashedPassword", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "lastUpdateDate", ignore = true)
    @Mapping(target = "projectList", ignore = true)
    User toEntity(UserSignupRequest userSignupRequest);



    @Mapping(target = "token", ignore = true)
    @Mapping(source = "name", target = "username")
    UserAuthResponse toAuthResponse(User user);
}
