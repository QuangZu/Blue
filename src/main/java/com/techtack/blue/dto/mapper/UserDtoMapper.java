package com.techtack.blue.dto.mapper;

import com.techtack.blue.dto.UserDto;
import com.techtack.blue.model.User;

import java.util.ArrayList;
import java.util.List;

public class UserDtoMapper {
    public static UserDto toUserDto(User user) {
        UserDto userDto=new UserDto();
        userDto.setId(user.getId());
        userDto.setEmail(user.getEmail());
        userDto.setFullName(user.getFullName());
        userDto.setIdentification_card(user.getIdentification_card());
        userDto.setLocation(user.getLocation());
        userDto.setBirthDate(user.getBirthDate());

        return userDto;
    }

    public static List<UserDto> toUserDtos(List<User> followers) {
        List<UserDto> userDtos=new ArrayList<>();

        for (User user : followers) {
            UserDto userDto=new UserDto();
            userDto.setId(user.getId());
            userDto.setEmail(user.getEmail());
            userDto.setFullName(user.getFullName());
            userDto.setIdentification_card(user.getIdentification_card());
            userDtos.add(userDto);
        }

        return userDtos;
    }

}
