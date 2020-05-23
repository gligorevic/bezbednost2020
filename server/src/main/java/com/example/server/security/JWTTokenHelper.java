package com.example.server.security;

import io.jsonwebtoken.*;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static com.example.server.security.SecurityConstants.SECRET;


@Component
public class JWTTokenHelper {

    public boolean validate(String token){
        try{
            Jwts.parser().setSigningKey(SECRET).parseClaimsJws(token);
            return true;
        }catch (SignatureException ex){
            System.out.println("Invalid JWT Signature");
        }catch (MalformedJwtException ex){
            System.out.println("Invalid JWT Token");
        }catch (ExpiredJwtException ex){
            System.out.println("Expired JWT token");
        }catch (UnsupportedJwtException ex){
            System.out.println("Unsupported JWT token");
        }catch (IllegalArgumentException ex){
            System.out.println("JWT claims string is empty");
        }
        return false;
    }

    public String getUserUsernameFromJWT(String token){
        try {
            Claims claims = Jwts.parser().setSigningKey(SECRET).parseClaimsJws(token).getBody();
            String username = (String)claims.get("username");
            return username;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<String> getPrivilegesFromAccessToken(String jwt) {
        List<String> privileges = new ArrayList<>();

        Claims claims = Jwts.parser().setSigningKey(SECRET).parseClaimsJws(jwt).getBody();

        String privilegesString = claims.get("privileges").toString();

        privilegesString = privilegesString.replaceFirst("\\[", "");
        privilegesString = privilegesString.replace("\\]", "");

        String[] privilegesArr = privilegesString.split(", ");

        for(String privilege : privilegesArr) {
            privileges.add(privilege);
        }

        return privileges;
    }
}