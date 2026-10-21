package com.bankofcli.backend.rest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bankofcli.backend.persistence.AccountWriteDTO;
import com.bankofcli.backend.persistence.AuthResponseDTO;
import com.bankofcli.backend.persistence.LoginRequestDTO;
import com.bankofcli.backend.service.AuthService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;


@RestController
@RequestMapping("/api/auth")
public class AuthController {

	private final AuthService authService;

	public AuthController(AuthService authService) {
		this.authService = authService;
	}

	@PostMapping("/register")
	public ResponseEntity<AuthResponseDTO> register(
			@Valid @RequestBody AccountWriteDTO request) {
		return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(request));
	}

	@PostMapping("/login")
	public AuthResponseDTO login(
			@Valid @RequestBody LoginRequestDTO request,
			HttpServletRequest servletRequest,
			HttpServletResponse servletResponse) {
		return authService.login(request, servletRequest, servletResponse);
	}

	@PostMapping("/logout")
	public ResponseEntity<Void> logout(
			HttpServletRequest request,
			HttpServletResponse response,
			Authentication authentication) {
		authService.logout(request, response, authentication);
		return ResponseEntity.noContent().build();
	}

	@GetMapping("/me")
	public AuthResponseDTO currentUser(Authentication authentication) {
		return authService.toResponse(authentication);
	}


	@GetMapping("/csrf")
	public CsrfToken csrfToken(CsrfToken csrfToken) {
		return csrfToken;
	}
}
