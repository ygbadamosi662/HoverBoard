package com.multi_tenant.demo.Controllers;


import com.multi_tenant.demo.Dtos.LoginDto;
import com.multi_tenant.demo.Dtos.OriginDto;
import com.multi_tenant.demo.Dtos.ResponseDtos.DimeResponseDto;
import com.multi_tenant.demo.Dtos.ResponseDtos.ReceiptResponseDto;
import com.multi_tenant.demo.Dtos.ResponseDtos.ToolResponseDto;
import com.multi_tenant.demo.Dtos.ResponseDtos.UserResDto;
import com.multi_tenant.demo.Dtos.SubscribeDto;
import com.multi_tenant.demo.Dtos.UserDto;
import com.multi_tenant.demo.Enums.Status;
import com.multi_tenant.demo.Models.*;
import com.multi_tenant.demo.Repos.*;
import com.multi_tenant.demo.Services.JwtService;
import com.multi_tenant.demo.Services.PortalService;
import com.multi_tenant.demo.Utilities.Utility;
import jakarta.persistence.PersistenceException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.*;


@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/user")
public class UserController
{
    private final UserRepo userRepo;

    private final UserCorsRepo corsRepo;

    private final DimensionRepo dimeRepo;

    private final ToolRepo toolRepo;

    private final ToolReceiptRepo trexRepo;

    private final FeatureRepo featRepo;

    private final CombosRepo comboRepo;

    private final ContractRepo contractRepo;

    private final ToolReceiptRepo toolReceiptRepo;

    private final ReceiptRepo receiptRepo;

    private final JwtBlackListRepo blackRepo;

    private final JwtService jwtService;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;

    private final Utility util;

    private final PortalService portalService;

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
            return ResponseEntity.ok("Tenant-id: "+userRepo.save(dto.getUser()).getId().toString());
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

    @PostMapping("/register/origin")
    public ResponseEntity<?> reg_origin(@Valid @RequestBody OriginDto origin, HttpServletRequest req)
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

    @GetMapping("/get/dimes")
    public ResponseEntity<?> get_dimes(@Valid @RequestParam int page, HttpServletRequest req)
    {
        try
        {
            String jwt = jwtService.setJwt(req);
            if(jwtService.is_cancelled(jwt))
            {
                return new ResponseEntity<>("jwt blacklisted,user should login again",
                        HttpStatus.BAD_REQUEST);
            }

            Pageable pageable = PageRequest.of(page-1, 20);
            Slice<Dimension> dimes = dimeRepo.findByStatus(Status.ACTIVE, pageable);
            List<DimeResponseDto> res = new ArrayList<>();
            dimes.forEach((dime) -> {
                res.add(new DimeResponseDto(dime));
            });


            return new ResponseEntity<>(res, HttpStatus.OK);
        }
        catch (PersistenceException e)
        {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/get/tools")
    public ResponseEntity<?> get_tools(@Valid @RequestParam String dime_id,
                                       @Valid @RequestParam String type,
                                       @Valid @RequestParam int page, HttpServletRequest req)
    {
        try
        {
            String jwt = jwtService.setJwt(req);
            if(jwtService.is_cancelled(jwt))
            {
                return new ResponseEntity<>("jwt blacklisted,user should login again",
                        HttpStatus.BAD_REQUEST);
            }

            Optional<Dimension> byId = dimeRepo.findById(UUID.fromString(dime_id));
            if(byId.isEmpty())
            {
                return new ResponseEntity<>("Dime id is not valid", HttpStatus.BAD_REQUEST);
            }

            Pageable pageable = PageRequest.of(page-1, 20);
            Slice<Tool> tools = toolRepo.findByTypeAndDime(type, byId.get(), pageable);
            List<ToolResponseDto> res = new ArrayList<>();
            tools.forEach((tool) -> {
                res.add(new ToolResponseDto(tool));
            });


            return new ResponseEntity<>(res, HttpStatus.OK);
        }
        catch (PersistenceException e)
        {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/rent/dime")
    public ResponseEntity<?> rent_a_dime(@Valid @RequestBody SubscribeDto sub, HttpServletRequest req)
    {
        try
        {
            String jwt = jwtService.setJwt(req);
            if(jwtService.is_cancelled(jwt))
            {
                return new ResponseEntity<>("jwt blacklisted,user should login again",
                        HttpStatus.BAD_REQUEST);
            }
            User user = jwtService.giveUser();
            System.out.println(sub.getTerm());
            Optional<Dimension> byId = dimeRepo.findById(UUID.fromString(sub.getDime_or_tool_id()));
            System.out.println("hey hey");
            if(byId.isEmpty())
            {
                return new ResponseEntity<>("Dime cannot be found", HttpStatus.BAD_REQUEST);
            }
            System.out.println("hey hey");
//            alot of payment shenanigans will have to happen before the following happens
            Contract con = sub.getCon();
            con.setDime(byId.get());
            con.setUser(user);
            con.setStatus(Status.ACTIVE);
            con.setUsage_ends(util.get_tool_usage_end(con));
            return new ResponseEntity<>(new ReceiptResponseDto(contractRepo.save(con)), HttpStatus.OK);
        }
        catch (PersistenceException e)
        {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/sub/tool")
    public ResponseEntity<?> sub_a_tool(@Valid @RequestBody SubscribeDto sub, HttpServletRequest req)
    {
        try
        {
            String jwt = jwtService.setJwt(req);
            if(jwtService.is_cancelled(jwt))
            {
                return new ResponseEntity<>("jwt blacklisted,user should login again",
                        HttpStatus.BAD_REQUEST);
            }
            User user = jwtService.giveUser();

            Optional<Tool> byId = toolRepo.findById(UUID.fromString(sub.getDime_or_tool_id()));
            if(byId.isEmpty())
            {
                return new ResponseEntity<>("Dime cannot be found", HttpStatus.BAD_REQUEST);
            }
//            check if user has all dependencies of the tool
//            alot of payment shenanigans will have to happen before the following happens
            ToolReceipt tRex = sub.get_tool_receipt();
            tRex.setTool(byId.get());
            tRex.setUser(user);
            tRex.setStatus(Status.ACTIVE);
            tRex.setUsage_ends(util.get_tool_usage_end(tRex));
            return new ResponseEntity<>(new ReceiptResponseDto(trexRepo.save(tRex)), HttpStatus.OK);
        }
        catch (PersistenceException e)
        {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/get/print")
    public ResponseEntity<?> get_tools(@Valid String dime_id, HttpServletRequest req)
    {
        try
        {
            String jwt = jwtService.setJwt(req);
            if(jwtService.is_cancelled(jwt))
            {
                return new ResponseEntity<>("jwt blacklisted,user should login again",
                        HttpStatus.BAD_REQUEST);
            }
            User user = jwtService.giveUser();

            Optional<Dimension> byId = dimeRepo.findById(UUID.fromString(dime_id));
            if(byId.isEmpty())
            {
                return new ResponseEntity<>("Cant find Dime", HttpStatus.BAD_REQUEST);
            }

            String print = portalService.generatePortalPrint(user, byId.get());

            return new ResponseEntity<>(print, HttpStatus.OK);
        }
        catch (PersistenceException e)
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
