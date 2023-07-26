package com.multi_tenant.demo.Controllers;

import com.multi_tenant.demo.Dtos.DimeDto;
import com.multi_tenant.demo.Dtos.ResponseDtos.ToolResponseDto;
import com.multi_tenant.demo.Dtos.ToolDto;
import com.multi_tenant.demo.Enums.Status;
import com.multi_tenant.demo.Models.Combos;
import com.multi_tenant.demo.Models.Dimension;
import com.multi_tenant.demo.Models.Feature;
import com.multi_tenant.demo.Models.User;
import com.multi_tenant.demo.Repos.*;
import com.multi_tenant.demo.Services.JwtService;
import com.multi_tenant.demo.Utilities.Utility;
import jakarta.persistence.PersistenceException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/admin")
public class AdminController
{
    private final UserRepo userRepo;

    private final DimensionRepo dimeRepo;

    private final FeatureRepo featRepo;

    private final CombosRepo combosRepo;

    private final JwtService jwtService;

    private final Utility util;

    @PostMapping("/register/dime")
    public ResponseEntity<?> registerDime(@Valid @RequestBody DimeDto dto, HttpServletRequest req)
    {
        try
        {
            String jwt = jwtService.setJwt(req);
            if(jwtService.is_cancelled(jwt))
            {
                return new ResponseEntity<>("jwt blacklisted,user should login again",
                        HttpStatus.BAD_REQUEST);
            }

            if(dto.validateVersion() == false)
            {
                return new ResponseEntity<>("wrong version format", HttpStatus.BAD_REQUEST);
            }
            Dimension dime = dto.getDime();
            dime.setStatus(Status.ACTIVE);

            return new ResponseEntity<>(dimeRepo.save(dime), HttpStatus.OK);
        }
        catch (PersistenceException e)
        {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/register/tool")
    public ResponseEntity<?> registerDime(@Valid @RequestBody ToolDto dto, HttpServletRequest req)
    {
        try
        {
            String jwt = jwtService.setJwt(req);
            if(jwtService.is_cancelled(jwt))
            {
                return new ResponseEntity<>("jwt blacklisted,user should login again",
                        HttpStatus.BAD_REQUEST);
            }

            Optional<Dimension> byId = dimeRepo.findById(UUID.fromString(dto.getDime_id()));
            if(byId.isEmpty())
            {
                return new ResponseEntity<>("Cant find Dime", HttpStatus.BAD_REQUEST);
            }

            if(dto.validateVersion() == false)
            {
                return new ResponseEntity<>("wrong version format", HttpStatus.BAD_REQUEST);
            }
            ToolResponseDto resDto = new ToolResponseDto();
            if(dto.getType().equals(Feature.class.getSimpleName()))
            {
                Feature feat = dto.getFeature();
                feat.setStatus(Status.ACTIVE);
                feat.setDime(byId.get());
                resDto =  new ToolResponseDto(featRepo.save(feat));
            }

            if(dto.getType().equals(Combos.class.getSimpleName()))
            {
                Combos combo = dto.getCombo();
                combo.setStatus(Status.ACTIVE);
                combo.setDime(byId.get());
                resDto = new ToolResponseDto(combosRepo.save(combo));
            }

            return new ResponseEntity<>(resDto, HttpStatus.OK);
        }
        catch (PersistenceException e)
        {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

}
