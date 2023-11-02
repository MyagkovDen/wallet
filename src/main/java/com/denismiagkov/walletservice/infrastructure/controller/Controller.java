package com.denismiagkov.walletservice.infrastructure.controller;

import com.denismiagkov.walletservice.application.dto.AccountDto;
import com.denismiagkov.walletservice.application.dto.PlayerDto;
import com.denismiagkov.walletservice.application.dto.TransactionDto;
import com.denismiagkov.walletservice.application.service.Service;
import com.denismiagkov.walletservice.infrastructure.in.DataValidator;
import com.denismiagkov.walletservice.infrastructure.in.exceptions.InfoMessage;
import com.denismiagkov.walletservice.infrastructure.login_service.AuthService;
import com.denismiagkov.walletservice.infrastructure.login_service.JwtRequest;
import com.denismiagkov.walletservice.infrastructure.login_service.JwtResponse;
import com.denismiagkov.walletservice.init.WebInit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

//import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.List;

/**
 * Класс обрабатывает запросы, полученные от пользователя и управляет взаимодействием между внешним
 * и внутренними слоями приложения
 */
@RestController
public class Controller {
    /**
     * Cервис приложения
     */
    private Service service;
    /**
     * Сервис аутентификации
     */
    private AuthService authService;
    /**
     * Информационное сообщение для пользователя о статусе исполнения запроса
     */
    private InfoMessage message;

    /**
     * Конструктор класса
     */
    @Autowired
    public Controller(Service service) {
        this.service = service;
        this.authService = new AuthService();
        this.message = new InfoMessage();
    }

    /**
     * Init-метод создает подключение к базе данных и запускает Liquibase
     */
   // @PostConstruct
    public void init() {
        WebInit.start();
    }

    /**
     * Метод вызывает в сервисе метод регистрации нового игрока. Возвращает статус исполнения запроса
     *
     * @param playerDto ДТО нового игрока
     * @return статус исполнения запроса
     */
    @PostMapping("/registration")
    public ResponseEntity<InfoMessage> registerPlayer(@RequestBody PlayerDto playerDto) throws RuntimeException {
        DataValidator.checkRegistrationForm(playerDto);
        service.registerPlayer(playerDto);
        message.setInfo("Игрок " + playerDto.getName() + " " + playerDto.getSurname() + " зарегистрирован");
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(message);
    }

    /**
     * Метод реализует аутентентификацию пользователя и сообщает ему о результате исполнения запроса
     *
     * @param authRequest запрос на авторизацию игрока, включающий его логин и пароль
     * @return токен
     */
    @PostMapping("/authentication")
    public ResponseEntity<JwtResponse> authorizePlayer(@RequestBody JwtRequest authRequest) throws RuntimeException {
        JwtResponse authResponse = authService.login(authRequest);
        message.setInfo("Аутентификация пользователя выполнена успешно");
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(authResponse);
    }

    /**
     * Метод передает в сервис запрос о текущем состоянии баланса игрока и возвращает
     * результат обработки запроса пользователю.
     *
     * @param header Header "Authorization" HttpServletRequest, содержащий токен игрока
     * @return текущий баланс на счет игрока
     */
    @PostMapping("/players/balance")
    public ResponseEntity<AccountDto> getCurrentBalance(@RequestHeader("Authorization") String header) {
        String login = authService.validateAccessToken(header);
        AccountDto accountDto = service.getCurrentBalance(login);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(accountDto);
    }

    /**
     * Метод вызывает в сервисе историю дебетовых и кредитных операций по счету игрока
     * и возвращает пользователю результат обработки запроса.
     *
     * @param header Header "Authorization" HttpServletRequest, содержащий токен игрока
     * @return история транзакций на счете игрока
     */
    @PostMapping("/players/transactions")
    public ResponseEntity<List<TransactionDto>> getTransactionsHistory(@RequestHeader("Authorization") String header) {
        String login = authService.validateAccessToken(header);
        List<TransactionDto> transactionDtoList = service.getTransactionHistory(login);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(transactionDtoList);
    }

    /**
     * Метод вызывает в сервисе метод по пополнению денежного счета игрока
     * и возвращает пользователю результат обработки запроса
     *
     * @param header  Header "Authorization" HttpServletRequest, содержащий токен игрока
     * @param wrapper класс-обертка для получения значения типа BigDecimal из http-запроса
     * @return статус исполнения запроса
     */
    @PostMapping("/players/depositing")
    public ResponseEntity<InfoMessage> topUpAccount(@RequestHeader("Authorization") String header,
                                                    @RequestBody AmountWrapper wrapper) {
        BigDecimal amount = wrapper.getAmount();
        String login = authService.validateAccessToken(header);
        service.topUpAccount(login, amount);
        message.setInfo("Ваш баланс пополнен на сумму " + amount + " " + " денежных единиц");
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(message);
    }

    /**
     * Метод вызывает в сервисе метод по списанию денежных средств со счета игрока
     * и возвращает пользователю результат обработки запроса.
     *
     * @param header  Header "Authorization" HttpServletRequest, содержащий токен игрока
     * @param wrapper класс-обертка для получения значения типа BigDecimal из http-запроса
     * @return статус исполнения запроса
     */
    @PostMapping("/players/withdrawal")
    public ResponseEntity<InfoMessage> writeOffFunds(@RequestHeader("Authorization") String header,
                                                     @RequestBody AmountWrapper wrapper) throws RuntimeException {
        BigDecimal amount = wrapper.getAmount();
        String login = authService.validateAccessToken(header);
        service.writeOffFunds(login, amount);
        message.setInfo("С вашего счета списана сумма " + amount + " " + " денежных единиц");
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(message);
    }
}