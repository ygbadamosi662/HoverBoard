package com.multi_tenant.demo.Controllers;


import com.multi_tenant.demo.Dtos.*;
import com.multi_tenant.demo.Dtos.ResponseDtos.*;
import com.multi_tenant.demo.Enums.DbTestStatus;
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
import java.util.stream.Collectors;


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

    private final PortalPrintRepo printRepo;

    private final ContractRepo contractRepo;

    private final JwtBlackListRepo blackRepo;

    private final TenantStorageInfoRepo storageRepo;

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
        try
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

            List<Receipt> receipts = portalService.update_tenant_portfolio(user);
            if(!receipts.isEmpty())
            {
//                when notification is implemented,
//                notify the user for the update on each of the receipts

            }
            System.out.println(receipts.isEmpty());

            return ResponseEntity.ok(res);
        } catch (PersistenceException e)
        {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

    }

    @GetMapping("/signout")
    public ResponseEntity<?> signout(HttpServletRequest req)
    {
        try
        {
            String jwt = jwtService.setJwt(req);
            JwtBlackList a_nigga = new JwtBlackList(jwt);
            blackRepo.save(a_nigga);

            return new ResponseEntity<>("See you later", HttpStatus.OK);

        } catch (PersistenceException e)
        {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

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

            Optional<Dimension> byId = dimeRepo.findById(UUID.fromString(sub.getDime_or_tool_id()));

            if(byId.isEmpty())
            {
                return new ResponseEntity<>("Dime cannot be found", HttpStatus.BAD_REQUEST);
            }
            Dimension dime = byId.get();
//            checks if user have an active contract for the dime
            if(user.if_i_have_dime(dime)) return new
                        ResponseEntity<>("You have an active contract with us in this dimension, want to upgrade",
                        HttpStatus.BAD_REQUEST);

//            alot of payment shenanigans will have to happen before the following happens
            Contract con = sub.getCon();
            con.setDime(byId.get());
            con.setUser(user);
            con.setStatus(Status.ACTIVE);
            con.setUsage_ends(util.get_tool_usage_end(con));
            con = contractRepo.save(con);
//            generates a portal print
            PortalPrint print = portalService.get_new_persited_print(user, con);

            return new ResponseEntity<>(new ReceiptResponseDto(con), HttpStatus.OK);
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
            Tool tool = byId.get();

//            checks if user does mot have an active contract with the appropriate dime
            if(user.if_i_have_dime(tool.getDime()) == false) return new
                    ResponseEntity<>("You dont have an active contract with the associated dime",
                    HttpStatus.BAD_REQUEST);

//            checks if user have an active sub for the tool
            if(user.if_i_have_tool(tool)) return
                new ResponseEntity<>("You have an active subscription for this tool",
                        HttpStatus.BAD_REQUEST);

//            check if user has all dependencies of the tool
//            alot of payment shenanigans will have to happen before the following happens
            ToolReceipt tRex = sub.get_tool_receipt();
            tRex.setTool(byId.get());
            tRex.setUser(user);
            tRex.setStatus(Status.ACTIVE);
            tRex.setUsage_ends(util.get_tool_usage_end(tRex));
            tRex = trexRepo.save(tRex);

//            updating portal print
            PortalPrint print = printRepo
                    .findByContractAndTenant(user.get_contractByDime(tool.getDime()), user);
            print.getReceipts().add(tRex);
            print.setPrint(portalService.generate_portal_print(print));
            printRepo.save(print);

            return new ResponseEntity<>(new ReceiptResponseDto(tRex), HttpStatus.OK);
        }
        catch (PersistenceException e)
        {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/get/prints")
    public ResponseEntity<?> get_prints(@Valid HttpServletRequest req)
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

            List<PortalPrint> prints = printRepo.findByTenant(user);
            if(prints == null) return new ResponseEntity<>("You have no portal print",
                    HttpStatus.BAD_REQUEST);

            List<PrintResponseDto> res = prints
                    .stream()
                    .map((p) -> {
                        PrintResponseDto resp = new PrintResponseDto(p);
                        resp.set_receipts(p.getReceipts());
                        return resp;
                    })
                    .collect(Collectors.toList());

            return new ResponseEntity<>(res, HttpStatus.OK);
        }
        catch (PersistenceException e)
        {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/get/print")
    public ResponseEntity<?> get_print(@Valid @RequestParam String con_id, HttpServletRequest req)
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

//            get contract
            Optional<Contract> byId = contractRepo.findById(UUID.fromString(con_id));
            if(byId.isEmpty())
            {
                return new ResponseEntity<>("Contract does not exist", HttpStatus.BAD_REQUEST);
            }

//            get print
            Optional<PortalPrint> by_tenAndCon = printRepo.findByTenantAndContract(user, byId.get());
            if(by_tenAndCon.isEmpty()) return new ResponseEntity<>("You have no portal print",
                    HttpStatus.BAD_REQUEST);

            PrintResponseDto res = new PrintResponseDto(by_tenAndCon.get());
            return new ResponseEntity<>(res, HttpStatus.OK);
        }
        catch (PersistenceException e)
        {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/auth/validate/print")
    public ResponseEntity<?> validate_print(@Valid @RequestParam String dime_id,
                                            @Valid @RequestParam String print_id)
    {
        try
        {
//            get dime
            Optional<Dimension> byId = dimeRepo.findById(UUID.fromString(dime_id));
            if(byId.isEmpty())
            {
                return new ResponseEntity<>("Dime does not exist", HttpStatus.BAD_REQUEST);
            }

//            get print
            Optional<PortalPrint> by_print_id = printRepo.findById(UUID.fromString(print_id));
            if(by_print_id.isEmpty())
            {
                return new ResponseEntity<>("Print does not exist",
                        HttpStatus.BAD_REQUEST);
            }
            PortalPrint print = by_print_id.get();

//            checks if the print provided is for the provided dimension
            if(!print.getContract().getDime().equals(byId.get()))
            {
                return new ResponseEntity<>("You are in the wrong dimension pal",
                        HttpStatus.BAD_REQUEST);
            }

//            validate print
            if(!print.if_contract_active())
            {
//                when notifcation, however its done is implemented, notify the tenant here
                return new ResponseEntity<>("Your Provider does not have an active contract for " +
                        "this dimension",
                        HttpStatus.BAD_REQUEST);
            }

            if(print.if_any_inactive_tool())
            {
//                updates the print accordinly
//                when notification is implemented, notify the tenants what tools has expred
                List<ToolReceipt> expired_toolReceipts = print.update_tools();

                print.setPrint(portalService.generate_portal_print(print));
//                saving the print
                print = printRepo.save(print);
            }
            return new ResponseEntity<>(new DimeApiResponseDto(print), HttpStatus.OK);
        }
        catch (PersistenceException e)
        {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

//    this 👇 endpoint still in doubt , we should manage their data storage for consistency
    @PostMapping("/register/storage")
    public ResponseEntity<?> sub_a_tool(@Valid @RequestBody StorageDto std, HttpServletRequest req)
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

//            get dime
            Optional<Dimension> byId = dimeRepo.findById(UUID.fromString(std.getDime_id()));
            if(byId.isEmpty())
            {
                return new ResponseEntity<>("Dime does not exist", HttpStatus.BAD_REQUEST);
            }

//            checks if user have an active contract in the dimension
            if(!user.if_i_have_dime(byId.get()))
            {
                return new ResponseEntity<>("You dont have a contract with this dime",
                        HttpStatus.BAD_REQUEST);
            }

//            get Storage info
            TenantStorageInfo storage = std.getStorageInfo();
            storage.setPassword(passwordEncoder.encode(std.getPassword()));
//            implement testing the storage connection before going to the following code executes
//            if test is succesful, set testStatus to DbTestStatus.PASS else set to DbTestStatus.FAILED
//            for mow im going to assume it passed
            storage.setUser(user);
            storage.setDime(byId.get());
            storage.setTestStatus(DbTestStatus.PASS);

//            save storage
            storageRepo.save(storage);

            return new ResponseEntity<>("Storage registered succesfully", HttpStatus.OK);
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
