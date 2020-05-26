package com.example.AuthService.service;

import com.example.AuthService.domain.Privilege;
import com.example.AuthService.domain.Role;
import com.example.AuthService.domain.User;
import com.example.AuthService.dto.LoginRequestDTO;
import com.example.AuthService.dto.UserDTO;
import com.example.AuthService.exception.CustomException;
import com.example.AuthService.repository.PrivilegeRepository;
import com.example.AuthService.repository.RoleRepository;
import com.example.AuthService.repository.UserRepository;
import com.example.AuthService.security.JWTTokenHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

import static com.example.AuthService.security.SecurityConstants.TOKEN_BEARER_PREFIX;

@Service
public class UserService {

    @Autowired
    private JWTTokenHelper tokenHelper;

    @Autowired
    private PrivilegeRepository privilegeRepository;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    public boolean changeUserPrivileges(List<Long> privilegeList, Long enduserId, boolean remove) throws Exception {
        User user = userRepository.getOne(enduserId);
        if(user == null) {
            throw new Exception("User not found");
        }

        if(remove) {
            List<Privilege> privileges =  privilegeRepository.findAllById(privilegeList);
            Set<Privilege> newBlockedPrivileges = new HashSet<>(privileges);
            newBlockedPrivileges.addAll(user.getBlockedPrivileges());
            user.setBlockedPrivileges(new ArrayList<>(newBlockedPrivileges));
        } else {
            List<Privilege> listOutput = user.getBlockedPrivileges().stream()
                            .filter(e -> !privilegeList.contains(e.getId()))
                            .collect(Collectors.toList());
            user.setBlockedPrivileges(listOutput);
        }
        userRepository.save(user);

        return true;
    }

    public String verifyUser(String bearerToken) {
        String jwt = tokenHelper.getJWTFromBearerToken(bearerToken);

        tokenHelper.validate(jwt);

        List<Long> rolesIdFromJWT = tokenHelper.getRolesIdFromJWT(jwt);

        List<Privilege> privilegesBoundWithRoles =  privilegeRepository.findByRolesIn(rolesIdFromJWT);
        User user = userRepository.findByEmail(tokenHelper.getUserUsernameFromJWT(jwt));

        List<Long> blockedPrivileges = user.getBlockedPrivileges().stream().map(p -> p.getId()).collect(Collectors.toList());

        List<Privilege> privileges = privilegesBoundWithRoles.stream()
                .filter(e -> !blockedPrivileges.contains(e.getId()))
                .collect(Collectors.toList());

        String accessToken = tokenHelper.generateAccessToken(privileges, jwt);

        return accessToken;
    }

    public String login(LoginRequestDTO loginRequestDTO) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequestDTO.getUsername(),
                        loginRequestDTO.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = TOKEN_BEARER_PREFIX +  tokenHelper.generate(authentication);

        return jwt;
    }


    public UserDTO register(UserDTO userDTO) throws Exception, CustomException {
        User user = userRepository.findByEmail(userDTO.getEmail());

        if(user != null) {
            throw new CustomException("User already exist", HttpStatus.BAD_REQUEST);
        }

        Role role = roleRepository.findByName(userDTO.getRoleName());

        if(role == null || role.getName().equals("ROLE_ADMIN")) {
            throw new CustomException("Role doesn't exist", HttpStatus.BAD_REQUEST);
        }

        User newUser = new User(userDTO);
        List<Role> roleList = new ArrayList<>();
        roleList.add(role);

        newUser.setRoles(roleList);
        newUser.setPassword(bCryptPasswordEncoder.encode(userDTO.getPassword()));

        userRepository.save(newUser);

        userDTO.setPassword(null);

        return userDTO;
    }

    public User getUser(String email, Authentication authentication) throws Exception {
        User userFromAuth = (User)authentication.getPrincipal();

        if(!userFromAuth.getEmail().equals(email)) {
            throw new Exception("Unauthorized");
        }

        return userRepository.findByEmail(email);
    }
}
