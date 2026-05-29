package com.educode.apps.calculadoraaritmetica;

import com.educode.apps.calculadoraaritmetica.clients.EmailClient;
import com.educode.apps.calculadoraaritmetica.controllers.HandlerExceptionController;
import com.educode.apps.calculadoraaritmetica.controllers.OperationController;
import com.educode.apps.calculadoraaritmetica.exceptions.EmailInvalidException;
import com.educode.apps.calculadoraaritmetica.exceptions.MailBoxConnectionException;
import com.educode.apps.calculadoraaritmetica.exceptions.OperationNotFoundException;
import com.educode.apps.calculadoraaritmetica.models.enums.OperationEnum;
import com.educode.apps.calculadoraaritmetica.models.dtos.CalculationDTO;
import com.educode.apps.calculadoraaritmetica.models.dtos.EmailValidationResponse;
import com.educode.apps.calculadoraaritmetica.models.dtos.ErrorDTO;
import com.educode.apps.calculadoraaritmetica.models.dtos.auth.AuthenticationRequest;
import com.educode.apps.calculadoraaritmetica.models.dtos.auth.AuthenticationResponse;
import com.educode.apps.calculadoraaritmetica.models.dtos.auth.RegisterRequest;
import com.educode.apps.calculadoraaritmetica.models.dtos.auth.RegisterResponse;
import com.educode.apps.calculadoraaritmetica.models.entities.Operation;
import com.educode.apps.calculadoraaritmetica.models.entities.Usuario;
import com.educode.apps.calculadoraaritmetica.repositories.OperationRepository;
import com.educode.apps.calculadoraaritmetica.repositories.UsuarioRepository;
import com.educode.apps.calculadoraaritmetica.security.JwtProvider;
import com.educode.apps.calculadoraaritmetica.security.SecurityProperties;
import com.educode.apps.calculadoraaritmetica.security.services.AuthServiceImpl;
import com.educode.apps.calculadoraaritmetica.security.services.UserDetailsServiceImpl;
import com.educode.apps.calculadoraaritmetica.services.EmailValidationService;
import com.educode.apps.calculadoraaritmetica.services.OperationServiceImpl;
import com.educode.apps.calculadoraaritmetica.services.UsuarioServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class CalculadoraAritmeticaApplicationTests {

    // Mocks for OperationController tests
    @Mock
    private com.educode.apps.calculadoraaritmetica.services.OperationService operationServiceMock;
    @Mock
    private com.educode.apps.calculadoraaritmetica.services.UsuarioService usuarioServiceMock;
    @Mock
    private SecurityContext securityContext;
    @Mock
    private Authentication authentication;
    @InjectMocks
    private OperationController operationController;
    @InjectMocks
    private HandlerExceptionController handlerExceptionController;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private JwtProvider jwtProvider;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private EmailValidationService emailValidationServiceMock;
    @InjectMocks
    private AuthServiceImpl authService;
    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;
    @Mock
    private OperationRepository operationRepository;
    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private EmailClient emailClient;
    @InjectMocks
    private OperationServiceImpl operationServiceImpl;
    @InjectMocks
    private UsuarioServiceImpl usuarioServiceImpl;
    @InjectMocks
    private EmailValidationService emailValidationService;

    private Usuario mockUsuario;

    @BeforeEach
    void setUp() {
        mockUsuario = new Usuario();
        mockUsuario.setId(1L);
        mockUsuario.setEmail("test@example.com");
        mockUsuario.setPassword("encodedPassword");

        SecurityContextHolder.setContext(securityContext);
        lenient().when(securityContext.getAuthentication()).thenReturn(authentication);
        lenient().when(authentication.getName()).thenReturn("test@example.com");
        lenient().when(usuarioServiceMock.getUsuarioByEmail(anyString())).thenReturn(mockUsuario);
        
        ReflectionTestUtils.setField(emailValidationService, "apiKey", "test-key");
        ReflectionTestUtils.setField(authService, "defaultPassword", "secret");
    }

    @Test
    void contextLoads() {
    }


    @Test
    void testAuthService_Authenticate() {
        AuthenticationRequest request = new AuthenticationRequest();
        request.setEmail("test@example.com");
        request.setPassword("password");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(jwtProvider.generateToken(any(Authentication.class))).thenReturn("mock-token");

        AuthenticationResponse response = authService.authenticate(request);

        assertNotNull(response);
        assertEquals("mock-token", response.getToken());
    }

    @Test
    void testAuthService_Register_Success() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("test@example.com");
        request.setUsername("testuser");
        request.setPassword("password");

        when(emailValidationServiceMock.isEmailValid(anyString())).thenReturn(true);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(mockUsuario);

        RegisterResponse response = authService.register(request);

        assertNotNull(response);
        assertEquals("You have successfully registered!", response.getMessage());
        assertEquals("test@example.com", response.getUsuarioDTO().getEmail());
    }

    @Test
    void testAuthService_Register_EmailInvalid() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("invalid@example.com");
        when(emailValidationServiceMock.isEmailValid(anyString())).thenReturn(false);
        assertThrows(EmailInvalidException.class, () -> authService.register(request));
    }

    @Test
    void testUserDetails_LoadSuccess() {
        when(usuarioRepository.findByEmail("test@example.com")).thenReturn(Optional.of(mockUsuario));
        UserDetails userDetails = userDetailsService.loadUserByUsername("test@example.com");
        assertEquals("test@example.com", userDetails.getUsername());
    }

    @Test
    void testUserDetails_LoadNotFound() {
        when(usuarioRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        assertThrows(UsernameNotFoundException.class, () -> userDetailsService.loadUserByUsername("none@test.com"));
    }


    @Test
    void testJwtProvider_GenerateAndValidate() {
        SecurityProperties props = new SecurityProperties();
        props.setSecretKey("v9y$B&E)H@McQfTjWnZr4u7x!A%C*F-JaNdRgUkXp2s5v8y/B?E(G+KbPeShVmYq");
        props.setExpiration(3600000);

        JwtProvider provider = new JwtProvider(props);
        when(authentication.getName()).thenReturn("test@example.com");

        String token = provider.generateToken(authentication);
        assertNotNull(token);
        assertTrue(provider.validateToken(token));
        assertEquals("test@example.com", provider.getUsernameFromToken(token));
    }

    @Test
    void testJwtProvider_InvalidToken() {
        SecurityProperties props = new SecurityProperties();
        props.setSecretKey("v9y$B&E)H@McQfTjWnZr4u7x!A%C*F-JaNdRgUkXp2s5v8y/B?E(G+KbPeShVmYq");
        JwtProvider provider = new JwtProvider(props);
        assertFalse(provider.validateToken("invalid.token.here"));
    }

    @Test
    void testSecurityProperties() {
        SecurityProperties props = new SecurityProperties();
        props.setSecretKey("key");
        props.setExpiration(100L);
        assertEquals("key", props.getSecretKey());
        assertEquals(100L, props.getExpiration());
        assertNotNull(props.toString());
    }


    @Test
    void testControllerCalculate() {
        CalculationDTO calculationDTO = new CalculationDTO();
        calculationDTO.setOperationEnum(OperationEnum.ADD);
        calculationDTO.setOperandA(new BigDecimal("10"));
        calculationDTO.setOperandB(new BigDecimal("5"));
        Operation mockOperation = new Operation();
        mockOperation.setResult(new BigDecimal("15"));
        when(operationServiceMock.doOperation(any(Usuario.class), any(Operation.class))).thenReturn(mockOperation);
        ResponseEntity<Operation> response = operationController.calculate(calculationDTO);
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    void testControllerHistory() {
        Operation mockOperation = new Operation();
        mockOperation.setId(1L);
        mockOperation.setOperationEnum(OperationEnum.ADD);
        mockOperation.setOperandA(new BigDecimal("10"));
        mockOperation.setOperandB(new BigDecimal("5"));
        mockOperation.setResult(new BigDecimal("15"));
        Page<Operation> page = new PageImpl<>(Collections.singletonList(mockOperation));
        when(operationServiceMock.history(anyLong(), any(Pageable.class))).thenReturn(page);
        ResponseEntity<List<Operation>> response = operationController.getHistory(0, 10, "timestamp", "desc");
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
    }


    @Test
    void testOperationService_Add() {
        Operation op = new Operation();
        op.setOperationEnum(OperationEnum.ADD);
        op.setOperandA(new BigDecimal("10"));
        op.setOperandB(new BigDecimal("5"));
        when(operationRepository.save(any(Operation.class))).thenAnswer(i -> i.getArguments()[0]);
        Operation result = operationServiceImpl.doOperation(mockUsuario, op);
        assertEquals(new BigDecimal("15"), result.getResult());
    }

    @Test
    void testOperationService_Divide() {
        Operation op = new Operation();
        op.setOperationEnum(OperationEnum.DIVIDE);
        op.setOperandA(new BigDecimal("10"));
        op.setOperandB(new BigDecimal("2"));
        when(operationRepository.save(any(Operation.class))).thenAnswer(i -> i.getArguments()[0]);
        Operation result = operationServiceImpl.doOperation(mockUsuario, op);
        assertEquals(new BigDecimal("5.00"), result.getResult());
    }

    @Test
    void testOperationService_DivideByZero() {
        Operation op = new Operation();
        op.setOperationEnum(OperationEnum.DIVIDE);
        op.setOperandA(new BigDecimal("10"));
        op.setOperandB(BigDecimal.ZERO);
        assertThrows(ArithmeticException.class, () -> operationServiceImpl.doOperation(mockUsuario, op));
    }

    @Test
    void testOperationService_HistoryPaged() {
        Page<Operation> page = new PageImpl<>(Collections.emptyList());
        when(operationRepository.findByUsuarioId(anyLong(), any(Pageable.class))).thenReturn(page);
        Page<Operation> result = operationServiceImpl.history(1L, PageRequest.of(0, 10));
        assertNotNull(result);
    }

    @Test
    void testOperationService_Detail() {
        Operation mockOp = new Operation();
        mockOp.setId(1L);
        when(operationRepository.findById(1L)).thenReturn(Optional.of(mockOp));
        Operation result = operationServiceImpl.operationDetail(1L);
        assertEquals(1L, result.getId());
    }

    @Test
    void testOperationService_DetailNotFound() {
        when(operationRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(OperationNotFoundException.class, () -> operationServiceImpl.operationDetail(1L));
    }

    @Test
    void testOperationService_Delete() {
        doNothing().when(operationRepository).deleteById(anyLong());
        operationServiceImpl.deleteOperation(1L);
        verify(operationRepository).deleteById(1L);
    }

    @Test
    void testOperationService_ValidateRange() {
        Operation op = new Operation();
        op.setOperationEnum(OperationEnum.ADD);
        op.setOperandA(new BigDecimal("2000000"));
        op.setOperandB(new BigDecimal("5"));
        assertThrows(IllegalArgumentException.class, () -> operationServiceImpl.doOperation(mockUsuario, op));
    }


    @Test
    void testUsuarioService_GetByEmail_Success() {
        when(usuarioRepository.findByEmail("test@example.com")).thenReturn(Optional.of(mockUsuario));
        Usuario result = usuarioServiceImpl.getUsuarioByEmail("test@example.com");
        assertEquals("test@example.com", result.getEmail());
    }

    @Test
    void testUsuarioService_GetByEmail_NotFound() {
        when(usuarioRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        assertThrows(UsernameNotFoundException.class, () -> usuarioServiceImpl.getUsuarioByEmail("none@test.com"));
    }


    @Test
    void testEmailValidation_Valid() {
        EmailValidationResponse response = new EmailValidationResponse();
        response.setFormatValid(true);
        response.setMxFound(true);
        response.setDisposable(false);
        when(emailClient.checkEmail(anyString(), anyString())).thenReturn(response);
        assertTrue(emailValidationService.isEmailValid("test@example.com"));
    }

    @Test
    void testEmailValidation_Exception() {
        when(emailClient.checkEmail(anyString(), anyString())).thenThrow(new MailBoxConnectionException("Error"));
        assertThrows(MailBoxConnectionException.class, () -> emailValidationService.isEmailValid("test@example.com"));
    }

    @Test
    void testHandler_EmailInvalid() {
        ResponseEntity<ErrorDTO> response = handlerExceptionController.emailInvalid(new EmailInvalidException("Invalid email"));
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void testHandler_BadCredentials() {
        ResponseEntity<ErrorDTO> response = handlerExceptionController.badCredentials(new BadCredentialsException("Bad creds"));
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    void testHandler_OperationNotFound() {
        ResponseEntity<ErrorDTO> response = handlerExceptionController.operationNotFound(new OperationNotFoundException("Not found"));
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}
