package com.denismiagkov.walletservice.infrastructure.controller;

import com.denismiagkov.walletservice.application.dto.AccountDto;
import com.denismiagkov.walletservice.application.dto.EntryDto;
import com.denismiagkov.walletservice.application.dto.PlayerDto;
import com.denismiagkov.walletservice.application.dto.TransactionDto;
import com.denismiagkov.walletservice.application.service.Service;
import com.denismiagkov.walletservice.aspects.annotations.Loggable;
import com.denismiagkov.walletservice.infrastructure.in.DataValidator;
import com.denismiagkov.walletservice.infrastructure.in.exceptions.IncorrectNameException;
import com.denismiagkov.walletservice.infrastructure.in.exceptions.InfoMessage;
import com.denismiagkov.walletservice.infrastructure.login_service.AuthService;
import com.denismiagkov.walletservice.infrastructure.login_service.JwtRequest;
import com.denismiagkov.walletservice.infrastructure.login_service.JwtResponse;
import com.denismiagkov.walletservice.init.WebInit;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;


import java.math.BigDecimal;
import java.util.List;

/**
 * Класс обрабатывает запросы, полученные от пользователя и управляет взаимодействием между внешним
 * и внутренними слоями приложения
 */
@RestController
@Component
//@RequestMapping("/api")
public class Controller {
    /**
     * Cервис приложения
     */
    private Service service;
    private AuthService authService;

    /**
     * Конструктор класса
     */
    @Autowired
    public Controller(Service service) {
        this.service = service;
        this.authService = new AuthService();
        WebInit.start();
    }

    /**
     * Метод вызывает в сервисе метод регистрации нового игрока. В зависимости от полученного результата
     * возвращает в консоль булевое значение.
     *
     * @param playerDto ДТО игрока
     * @return статус успеха регистрации
     */
    @Loggable
    @PostMapping("/registration")
    public ResponseEntity<PlayerDto> registerPlayer(@RequestBody PlayerDto playerDto) throws RuntimeException {
        DataValidator.checkRegistrationForm(playerDto);
        service.registerPlayer(playerDto);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(playerDto);
    }


    /**
     * Метод вызывает в сервисе метод аутентентификации пользователя.     *
     *
     * @param authRequest запрос на авторизацию игрока, включающий его логин и пароль
     */
    @Loggable
    @PostMapping("/authentication")
    public ResponseEntity<JwtResponse> authorizePlayer(@RequestBody JwtRequest authRequest) throws RuntimeException {
        JwtResponse authResponse = authService.login(authRequest);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(authResponse);
    }


    /**
     * Метод передает в сервис запрос о текущем состоянии баланса игрока.
     *
     * @param header Header "Authorization" HttpServletRequest, содержащий токен игрока
     */
    @Loggable
    @PostMapping("/players/balance")
    public ResponseEntity<AccountDto> getCurrentBalance(@RequestHeader("Authorization") String header) {
        String login = authService.validateAccessToken(header);
        AccountDto accountDto = service.getCurrentBalance(login);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(accountDto);
    }

    /**
     * Метод вызывает в сервисе историю дебетовых и кредитных операций по счету игрока.
     *
     * @param header Header "Authorization" HttpServletRequest, содержащий токен игрока
     */
    @Loggable
    @PostMapping("/players/transactions")
    public ResponseEntity<List<TransactionDto>> getTransactionsHistory(@RequestHeader("Authorization") String header) {
        String login = authService.validateAccessToken(header);
        List<TransactionDto> transactionDtoList = service.getTransactionHistory(login);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(transactionDtoList);
    }

    /**
     * Метод вызывает метод сервиса по пополнению денежного счета игрока.
     *
     * @param header  Header "Authorization" HttpServletRequest, содержащий токен игрока
     * @param wrapper класс-обертка для получения значения типа BigDecimal из http-запроса
     */
    @Loggable
    @PostMapping("/players/depositing")
    public ResponseEntity<InfoMessage> topUpAccount(@RequestHeader("Authorization") String header,
                                                    @RequestBody AmountWrapper wrapper) {
        BigDecimal amount = wrapper.getAmount();
        String login = authService.validateAccessToken(header);
        service.topUpAccount(login, amount);
        InfoMessage message = new InfoMessage();
        message.setInfo("Ваш баланс пополнен на сумму " + amount + " " + " денежных единиц");
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(message);
    }

    /**
     * Метод вызывает метод сервиса по списанию денежных средств со счета игрока.
     *
     * @param header  Header "Authorization" HttpServletRequest, содержащий токен игрока
     * @param wrapper класс-обертка для получения значения типа BigDecimal из http-запроса
     */
    @Loggable
    @PostMapping("/players/withdrawal")
    public ResponseEntity<InfoMessage> writeOffFunds(@RequestHeader("Authorization") String header,
                                                     @RequestBody AmountWrapper wrapper) throws RuntimeException {
        BigDecimal amount = wrapper.getAmount();
        String login = authService.validateAccessToken(header);
        service.writeOffFunds(login, amount);
        InfoMessage message = new InfoMessage();
        message.setInfo("С вашего счета списана сумма " + amount + " " + " денежных единиц");
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(message);
    }

    /**
     * Метод вызывает в сервисе метод по фиксации в журнале аудита действия игрока по выходу из приложения.
     *
     * @param login    идентификатор игрока (логин)
     * @param password идентифицирующий признак игрока (пароль)
     */
    @Loggable
    public void logExit(String login, String password) {
        service.logExit(login, password);
    }

}

