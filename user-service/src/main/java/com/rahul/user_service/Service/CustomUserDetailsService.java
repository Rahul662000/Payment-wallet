//package com.rahul.user_service.Service;
//
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
//
//public class CustomUserDetailsService implements UserDetailsService {
//
//    private
//
//    @Override
//    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
//        return userRepo.findByEmail(email)
//                .orElseThrow(() -> new UsernameNotFoundException("User not found."));
//    }
//}
