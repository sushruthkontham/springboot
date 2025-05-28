package com.ezc.service.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import com.ezc.entity.User;
import com.ezc.repository.UserRepository;

@Slf4j
@Service
public class AddUserService {

  @Autowired
  private UserRepository userRepository;

  public void handle(User user) throws Exception {
    userRepository.save(user);
  }
}
