package com.ra.base_spring_boot.security.github;

import com.ra.base_spring_boot.exception.HttpNotFound;
import com.ra.base_spring_boot.model.Role;
import com.ra.base_spring_boot.model.User;
import com.ra.base_spring_boot.model.constants.AuthProvider;
import com.ra.base_spring_boot.model.constants.RoleName;
import com.ra.base_spring_boot.model.constants.UStatus;
import com.ra.base_spring_boot.repository.IRoleRepository;
import com.ra.base_spring_boot.repository.IUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final IUserRepository userRepository;
    private final IRoleRepository roleRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        AuthProvider provider = AuthProvider.GITHUB;
        String providerId = Objects.requireNonNull(oAuth2User.getAttribute("id")).toString();

        String email = oAuth2User.getAttribute("email");
        if (email == null || email.isEmpty()) {
            email = "github_" + providerId + "@no-email.com";
        }

        String avatarUrl = oAuth2User.getAttribute("avatar_url");

        String finalEmail = email;
        User user = userRepository.findByProviderAndProviderId(provider, providerId)
                .orElseGet(() -> {
                    Optional<User> existingUser = userRepository.findByEmail(finalEmail);
                    if (existingUser.isPresent()) {
                        User u = existingUser.get();
                        u.setProvider(provider);
                        u.setProviderId(providerId);
                        return userRepository.save(u);
                    }

                    User newUser = User.builder()
                            .lastName("GIT")
                            .firstName("HUB")
                            .email(finalEmail)
                            .provider(provider)
                            .providerId(providerId)
                            .password("{noop}" + UUID.randomUUID())
                            .profileImage(avatarUrl)
                            .status(UStatus.ACTIVE)
                            .build();

                    Set<Role> roles = new HashSet<>();
                    Role userRole = roleRepository.findByRoleName(RoleName.ROLE_USER)
                            .orElseThrow(() -> new HttpNotFound("Role USER not found"));
                    roles.add(userRole);
                    newUser.setRoles(roles);

                    return userRepository.save(newUser);
                });

        return new CustomOAuth2User(user, oAuth2User.getAttributes());
    }
}

