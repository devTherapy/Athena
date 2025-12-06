package co.retaila.athena.modules.revocation.services.impl;

import co.retaila.athena.common.entities.Authority;
import co.retaila.athena.common.entities.BaseEntity;
import co.retaila.athena.common.entities.Revocation;
import co.retaila.athena.common.entities.User;
import co.retaila.athena.common.repositories.RevocationRepository;
import co.retaila.athena.modules.revocation.services.RevocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RevocationServiceImpl implements RevocationService {

    private final RevocationRepository revocationRepository;

    @Override
    public List<Authority> getUserRevocations(User user) {
        return revocationRepository.findByUser(user).stream()
                .map(Revocation::getAuthority)
                .collect(Collectors.toList());
    }

    @Override
    public void createRevocations(User user, List<Authority> authorities) {
        List<Revocation> revocations = new ArrayList<>(authorities.size());

        for (Authority authority : authorities) {
            Revocation revocation= Revocation.builder().authority(authority).user(user).build();
            revocations.add(revocation);
        }

        revocationRepository.saveAll(revocations);
    }

    @Override
    public List<Authority> getAuthoritiesToRevoke(User user, List<Authority> authorities) {
        return authorities.stream()
                .filter(authority -> !revocationRepository.existsByUserAndAuthority(user, authority))
                .collect(Collectors.toList());
    }

    @Override
    public void deleteRevocations(User user, List<Authority> authorities) {
        var authorityIds = authorities.stream().map(BaseEntity::getId).collect(Collectors.toList());
        revocationRepository.deleteByUserAndAuthorityIn(user.getId(), authorityIds);
    }

    @Override
    public List<Authority> getAuthoritiesToPermit(User user, List<Authority> authorities) {
        return authorities.stream()
                .filter(authority -> revocationRepository.existsByUserAndAuthority(user, authority))
                .collect(Collectors.toList());
    }

}
