package pl.signalbus.config;

import com.vaadin.flow.spring.security.VaadinSecurityConfigurer;
import com.vaadin.flow.server.auth.NavigationAccessControl;
import com.vaadin.flow.spring.security.UidlRedirectStrategy;
import com.vaadin.flow.spring.security.VaadinSavedRequestAwareAuthenticationSuccessHandler;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.context.annotation.Bean;
import org.springframework.boot.security.autoconfigure.SecurityProperties;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.jooq.DSLContext;

import java.lang.reflect.Proxy;
import java.util.Objects;
import java.util.UUID;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.web.access.AccessDeniedHandlerImpl;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.name;
import static org.jooq.impl.DSL.table;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;
import pl.signalbus.security.UserRole;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   NavigationAccessControl navigationAccessControl,
                                                   VaadinSavedRequestAwareAuthenticationSuccessHandler authenticationSuccessHandler,
                                                   DaoAuthenticationProvider authenticationProvider) {
        http.authenticationProvider(authenticationProvider);
        navigationAccessControl.setLoginView("/login");

        var logoutSuccessHandler = new SimpleUrlLogoutSuccessHandler();
        logoutSuccessHandler.setDefaultTargetUrl("/");
        logoutSuccessHandler.setRedirectStrategy(new UidlRedirectStrategy());

        http.with(VaadinSecurityConfigurer.vaadin(), vaadin -> vaadin
                .logoutSuccessHandler(logoutSuccessHandler)
        );

        http.authorizeHttpRequests(auth -> auth
                .requestMatchers("/",
                                 "/error",
                                 "/console-creator/**",
                                 "/web/**",
                                 "/css/**",
                                 "/js/**",
                                 "/styles.css",
                                 "/favicon.ico")
                .permitAll()
                .requestMatchers("/app/admin", "/app/admin/**").hasRole(UserRole.ADMIN)
                .requestMatchers("/app/console", "/app/console/**").hasRole(UserRole.CONSOLE)
                .requestMatchers("/**").permitAll()
        );

        http.exceptionHandling(ex -> {
            AccessDeniedHandlerImpl deniedHandler = new AccessDeniedHandlerImpl();
            deniedHandler.setErrorPage("/error");
            ex.accessDeniedHandler(deniedHandler);
        });

        http.formLogin(form -> form
                .loginPage("/login")
                .authenticationDetailsSource(PanelLoginDetails::new)
                .successHandler(authenticationSuccessHandler)
                .permitAll()
        );

        return http.build();
    }

    @Bean
    public VaadinSavedRequestAwareAuthenticationSuccessHandler authenticationSuccessHandler() {
        var handler = new VaadinSavedRequestAwareAuthenticationSuccessHandler() {
            @Override
            protected String determineTargetUrl(HttpServletRequest request,
                                                HttpServletResponse response,
                                                Authentication authentication) {

                if (authentication == null || authentication.getAuthorities().isEmpty()) {
                    return "/login?error";
                }
                
                boolean admin = authentication.getAuthorities().stream()
                                              .anyMatch(authority -> UserRole.ROLE_ADMIN.equals(authority.getAuthority()));

                return admin ? "/app/admin" : "/app/console";
            }
        };
        handler.setAlwaysUseDefaultTargetUrl(true);
        return handler;
    }

    @Bean
    public UserDetailsService panelUsers(SecurityProperties properties, DSLContext database) {
        return username -> {
            var admin = properties.getUser();
            if (admin.getName().equals(username)) {
                return User.withUsername(username).password("{noop}" + admin.getPassword())
                        .roles("ADMIN").build();
            }
            UUID consoleId;
            try {
                consoleId = UUID.fromString(username);
                if (!consoleId.toString().equalsIgnoreCase(username)) {
                    throw new IllegalArgumentException("Invalid UUID");
                }
            } catch (IllegalArgumentException exception) {
                throw new UsernameNotFoundException("Invalid console credentials");
            }
            String password = database.select(field(name("password"), String.class))
                    .from(table(name("console")))
                    .where(field(name("id"), UUID.class).eq(consoleId))
                    .fetchOne(field(name("password"), String.class));
            if (password == null) {
                throw new UsernameNotFoundException("Invalid console credentials");
            }
            return User.withUsername(consoleId.toString()).password("{noop}" + password)
                    .roles(UserRole.CONSOLE).build();
        };
    }

    @Bean
    public DaoAuthenticationProvider panelAuthenticationProvider(UserDetailsService panelUsers) {
        var provider = new DaoAuthenticationProvider(panelUsers) {
            @Override
            protected void additionalAuthenticationChecks(@NonNull UserDetails userDetails,
                                                          UsernamePasswordAuthenticationToken authentication) {
                if (authentication.getDetails() instanceof PanelLoginDetails details
                        && details.requiredRole != null
                        && userDetails.getAuthorities().stream().noneMatch(
                                authority -> Objects.equals(authority.getAuthority(), details.requiredRole))) {
                    throw new BadCredentialsException("Invalid panel credentials");
                }
                super.additionalAuthenticationChecks(userDetails, authentication);
            }
        };
        provider.setPasswordEncoder(PasswordEncoderFactories.createDelegatingPasswordEncoder());
        return provider;
    }

    private static class PanelLoginDetails extends WebAuthenticationDetails {
        private static final HttpServletResponse DUMMY_RESPONSE = (HttpServletResponse) Proxy.newProxyInstance(
                HttpServletResponse.class.getClassLoader(),
                new Class<?>[]{HttpServletResponse.class},
                (proxy, method, args) -> {
                    Class<?> returnType = method.getReturnType();
                    if (returnType.isPrimitive()) {
                        if (returnType == boolean.class) return false;
                        if (returnType == int.class) return 0;
                        if (returnType == long.class) return 0L;
                        if (returnType == byte.class) return (byte) 0;
                        if (returnType == short.class) return (short) 0;
                        if (returnType == float.class) return 0.0f;
                        if (returnType == double.class) return 0.0d;
                        if (returnType == char.class) return '\0';
                    }
                    return null;
                }
        );

        private final String requiredRole;

        PanelLoginDetails(HttpServletRequest request) {
            super(request);

            var saved = new HttpSessionRequestCache().getRequest(request, DUMMY_RESPONSE);
            String path = saved == null ? "" : java.net.URI.create(saved.getRedirectUrl()).getPath();
            String contextPath = request.getContextPath();
            if (!contextPath.isEmpty() && path.startsWith(contextPath + "/")) {
                path = path.substring(contextPath.length());
            }
            if (isPanelPath(path, "/app/console")) {
                requiredRole = UserRole.ROLE_CONSOLE;
            } else if (isPanelPath(path, "/app/admin")) {
                requiredRole = UserRole.ROLE_ADMIN;
            } else {
                requiredRole = null;
            }
        }

        private static boolean isPanelPath(String path, String panel) {
            return path.equals(panel) || path.startsWith(panel + "/");
        }
    }

}
