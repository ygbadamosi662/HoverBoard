package com.multi_tenant.demo.Controllers;


import com.multi_tenant.demo.Dtos.LoginDto;
import com.multi_tenant.demo.Dtos.OriginDto;
import com.multi_tenant.demo.Dtos.ResponseDtos.UserResDto;
import com.multi_tenant.demo.Dtos.UserDto;
import com.multi_tenant.demo.Enums.Status;
import com.multi_tenant.demo.Models.JwtBlackList;
import com.multi_tenant.demo.Models.User;
import com.multi_tenant.demo.Models.UserCors;
import com.multi_tenant.demo.Repos.JwtBlackListRepo;
import com.multi_tenant.demo.Repos.UserCorsRepo;
import com.multi_tenant.demo.Repos.UserRepo;
import com.multi_tenant.demo.Services.JwtService;
import com.multi_tenant.demo.Utilities.Utility;
import jakarta.persistence.PersistenceException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/user")
public class UserController
{
    private final UserRepo userRepo;

    private final UserCorsRepo corsRepo;

    private final JwtBlackListRepo blackRepo;

    private final JwtService jwtService;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;

    private final Utility util;

    @GetMapping("/auth/home")
    public ResponseEntity<?> home()
    {
        return ResponseEntity.ok("Welcome Home");
    }

    @PostMapping("/auth/register")
    public ResponseEntity<?> register(@Valid @RequestBody UserDto dto)
    {
        Optional<User> chk_chk = userRepo.findByEmail( dto.getEmail());

        if(chk_chk.isPresent())
        {
            return new ResponseEntity<>("user already exists", HttpStatus.BAD_REQUEST);
        }

        dto.setPassword(passwordEncoder.encode(dto.getPassword()));
        try
        {
            return ResponseEntity.ok(userRepo.save(dto.getUser()));
        } catch (PersistenceException e)
        {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/auth/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginDto login)
    {
        Optional<User> chk_chk = userRepo.findByEmail(login.getEmail());

        if(!chk_chk.isPresent())
        {
            return new ResponseEntity<>("email or password incorrect", HttpStatus.BAD_REQUEST);
        }

        User user = chk_chk.get();

        UsernamePasswordAuthenticationToken token =new UsernamePasswordAuthenticationToken(
                login.getEmail(),
                login.getPassword(),
                user.getAuthorities()
        );

        authenticationManager.authenticate(token);
        String jwt = jwtService.generateJwt(user);

        Map<String, String> res = new HashMap<>();
        res.put("Message", "Login successfull");
        res.put("Jwt", jwt);

        return ResponseEntity.ok(res);
    }

    @PostMapping("/origin")
    public ResponseEntity<?> reg_origin(@Valid @RequestBody OriginDto origin, @Valid HttpServletRequest req)
    {
        String jwt = jwtService.setJwt(req);

        if(jwtService.is_cancelled(jwt))
        {
            return new ResponseEntity<>("jwt blacklisted,user should login again",
                    HttpStatus.BAD_REQUEST);
        }
        User user = jwtService.giveUser();

        if (util.checkValidUrl(origin.getOrigin()) == false)
        {
            return new ResponseEntity<>("url format is not valid", HttpStatus.BAD_REQUEST);
        }

        UserCors userC = origin.getUserCors();
//        when set test the connection to the url before setting status and saving userC
        userC.setStatus(Status.ACTIVE);
        userC.setUser(user);
        try
        {
            return ResponseEntity.ok(corsRepo.save(userC).getOrigin());
        } catch (PersistenceException e)
        {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }



//    private static Map<String, Object> setExtraClaims(User user)
//    {
//        Map <String, Object> extraClaims = new HashMap<>();
//        extraClaims.put("fname",user.getFname());
//        extraClaims.put("lname",user.getLname());
//        extraClaims.put("email",user.getEmail());
//
//        return extraClaims;
//    }
}
