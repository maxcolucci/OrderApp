package com.orderapp.ordering.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.orderapp.ordering.entity.Tenant;

public interface TenantRepository extends JpaRepository<Tenant, Long> {
	Optional<Tenant> findBySubdomainIgnoreCase(String subdomain);
}

