package com.chatr.ws;

import com.chatr.jwt.JwtService;
import com.chatr.jwt.TokenType;
import com.chatr.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import javax.net.ssl.HandshakeCompletedListener;
import java.util.Map;

import static com.chatr.util.RequestParser.HEADER;

@Component
@RequiredArgsConstructor
public class JwtHandshakeIntercepter implements HandshakeInterceptor {


    private final JwtService jwtService;
    private final UserService userService;


    private void authUser(String token){

        String username = jwtService.getUsername(token, TokenType.ACCESS);

        if(!username.isEmpty() && SecurityContextHolder.getContext().getAuthentication() ==  null){

            UserDetails userDetails = userService.userDetailsService().loadUserByUsername(username);

            // TODO add valid by email
            if(jwtService.validateToken(token, TokenType.ACCESS)){

                SecurityContext securityContext = SecurityContextHolder.createEmptyContext();

                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                securityContext.setAuthentication(authenticationToken);
                SecurityContextHolder.setContext(securityContext);

                return;

            }
        }
    }


    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {

        var headers = request.getHeaders();
        String bearerToken  = headers.get("Authorization").getFirst();

        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            authUser(bearerToken.substring(7));
            return true;
        }

        return false;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {

    }
}
