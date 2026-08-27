package com.genesys.controller;

import com.genesys.dto.ResetPasswordRequest;
import com.genesys.dto.UserCreationRequest;
import com.genesys.dto.UserDtoResponse;
import com.genesys.service.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UsersService usersService;


    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/fetch")
    public ResponseEntity<List<UserDtoResponse>> fetchUsers(){

        return ResponseEntity.ok(usersService.fetchUsers());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/create")
    public ResponseEntity<?> createUser(@RequestBody UserCreationRequest request){

        usersService.createUser(request);
        return ResponseEntity.status(201).build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/reset-password")
    public ResponseEntity<?> resetPassword(@PathVariable(value = "id") UUID id,
                                           @RequestBody ResetPasswordRequest request){

        usersService.resetPassword(request,id);
         return ResponseEntity.status(204).build();
    }


    @DeleteMapping("/{id}/deactivate")
    public ResponseEntity<?> deactivate(@PathVariable(value = "id") UUID id){


        usersService.deactivate(id);
        return ResponseEntity.status(204).build();
    }

}
