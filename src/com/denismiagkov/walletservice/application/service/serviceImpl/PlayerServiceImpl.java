package com.denismiagkov.walletservice.application.service.serviceImpl;

import com.denismiagkov.walletservice.application.service.Service;
import com.denismiagkov.walletservice.application.service.serviceImpl.exception.IncorrectLoginException;
import com.denismiagkov.walletservice.application.service.serviceImpl.exception.IncorrectPasswordException;
import com.denismiagkov.walletservice.application.service.serviceImpl.exception.LoginIsNotUniqueException;
import com.denismiagkov.walletservice.application.service.serviceImpl.exception.PlayerAlreadyExistsException;
import com.denismiagkov.walletservice.domain.model.Player;
import com.denismiagkov.walletservice.domain.service.PlayerService;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Низкоуровневый сервис, реализующий методы, связанные с <strong>обработкой и манипуляцией данных об игроке</strong>.
 * Описанные в классе методы вызываются высокоуровневым сервисом для выполнения конкретных специализированных операций,
 * соответствующих бизнес-логике.
 */
public class PlayerServiceImpl implements PlayerService {

    /**
     * Перечень всех игроков
     */
    public Set<Player> allPlayers;
    /**
     * Перечень всех комбинаций логин-пароль, необходимых для аутентификации игроков {@link Entry}
     */
    private Map<String, String> allEntries;
    /**
     * Перечень, устанавливающий соответствие уникальных идентификаторов (логинов) игрокам.
     */
    private Map<String, Player> loginsPerPlayers;


    /**
     * Конструктор класса
     * */
    public PlayerServiceImpl() {
        this.allPlayers = new HashSet<>();
        this.allEntries = new HashMap<>();
        this.loginsPerPlayers = new HashMap<>();
    }

    /**
     * Возвращает список игроков
     * */
    public Set<Player> getAllPlayers() {
        return allPlayers;
    }

    /**
     * Возвращает список комбинаций логин-пароль
     * */
    public Map<String, String> getAllEntries() {
        return allEntries;
    }

    /**
     * Возвращает список соответствия логинов инрокам
     * */
    public Map<String, Player> getLoginsPerPlayers() {
        return loginsPerPlayers;
    }


    /**
     * Метод создает игрока, уникальную комбинацию идентификатора (логина) и пароля, необходимую для проведения
     * аутентификации игрока при использовании приложения, "привязывает" данную комбинацию к игроку.
     *
     * @param firstName имя игрока
     * @param lastName  фамилия игрока
     * @param email     электронная почта игрока
     * @param login     уникальный идентификатор игрока (логин)
     * @param password  идентифицирующий признак игрока (пароль)
     * @return новый игрок
     * @throws PlayerAlreadyExistsException в случае, если у игрока уже имеется учетная запись в системе и он
     *                                      пытается зарегистрироваться повторно
     * @throws LoginIsNotUniqueException    в случае, если логин, предложенный пользователем в процессе регистрации,
     *                                      уже зарегистрирован в системе за другим игроком
     */
    @Override
    public Player registerPlayer(String firstName, String lastName, String email, String login, String password)
            throws RuntimeException {
        if (allPlayers.contains(new Player(firstName, lastName, email))) {
            throw new PlayerAlreadyExistsException(firstName, lastName, email);
        } else if (allEntries.containsKey(login)) {
            throw new LoginIsNotUniqueException(login);
        } else {
            Player player = new Player(firstName, lastName, email);
            Entry entry = new Entry(player, login, email);
            allPlayers.add(player);
            allEntries.put(login, password);
            loginsPerPlayers.put(login, player);
            return player;
        }
    }

    /**
     * Метод выполняет аутентификацию игрока путем сопоставления идентификатора (логина)
     * и идентифицирующего признака (пароля) для решения вопроса о доступе пользователя в систему
     * {@link Service#authorizePlayer(String, String)}.
     * Также применяется высокоуровневым сервисом для установления личности игрока при совершении им
     * определенных действий в системе {@link Service#getPlayer(String, String)}.
     *
     * @param login    идентификатор игрока (логин)
     * @param password идентифицирующий признак игрока (пароль)
     * @return игрок, пытающийся войти в систему или совершить в ней определенное действие
     * @throws IncorrectLoginException    в случае, если пользователем введен логин, не зарегистрированный в системе
     * @throws IncorrectPasswordException в случае, если пользователем введен неверный пароль
     */
    public Player authorizePlayer(String login, String password) throws RuntimeException {
        if (!allEntries.containsKey(login)) {
            throw new IncorrectLoginException(login);
        } else if (allEntries.get(login).equals(password)) {
            Player player = loginsPerPlayers.get(login);
            return player;
        } else {
            throw new IncorrectPasswordException();
        }
    }
}
