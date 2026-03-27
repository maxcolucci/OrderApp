package com.orderapp.ordering.multitenant;

import java.io.IOException;

import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.orderapp.ordering.entity.Tenant;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class TenantResolutionFilter extends OncePerRequestFilter {
	private final TenantResolverService tenantResolverService;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
		throws ServletException, IOException {

		try {
			String host = request.getHeader(HttpHeaders.HOST);
			String subdomain = extractSubdomain(host);
			if (subdomain == null || subdomain.isBlank()) {
				throw new TenantNotResolvedException("Missing tenant subdomain in Host header");
			}

			Tenant tenant = tenantResolverService.resolveBySubdomainOrThrow(subdomain);
			TenantContext.setTenant(tenant.getId(), tenant.getSubdomain());

			filterChain.doFilter(request, response);
		} finally {
			TenantContext.clear();
		}
	}

	/**
	 * Expected formats:
	 * - tenant-slug.domain.com
	 * - tenant-slug.localhost:4200 (dev)
	 */
	static String extractSubdomain(String hostHeader) {
		if (hostHeader == null || hostHeader.isBlank()) {
			return null;
		}
		String host = hostHeader.trim().toLowerCase();
		int portIdx = host.indexOf(':');
		if (portIdx >= 0) {
			host = host.substring(0, portIdx);
		}

		// dev shortcut: tenant.localhost
		if (host.endsWith(".localhost")) {
			return host.substring(0, host.length() - ".localhost".length());
		}

		String[] parts = host.split("\\.");
		if (parts.length < 3) { // subdomain + domain + tld
			return null;
		}
		return parts[0];
	}
}

