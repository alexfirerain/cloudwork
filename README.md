## CloudWork – облачное хранилище файлов

Программа **CloudWork** представляет собой файловый сервер, предназначенный для работы в составе программного комплекса из трёх компонентов: 
* передовое (лицевое) приложение **_NetologyDiplomFrontend_**, с которым пользователь работает в своём браузере (предоставлено как-есть в [ТЗ](./TT.md)),
* собственно тыловое (серверное) приложение **_CloudWork_**, разработанное для согласованной работы с передовым приложением согласно спецификации [CloudServiceSpecification](./CloudServiceSpecification.yaml),
* СУБД (в данной реализации **MySQL**) для персистентного хранения на сервере пользовательских файлов и данных о пользователях.
    

Приложение написано на _Java_ (v17) с использованием каркаса _Spring Boot_, взаимодействие с БД (MySQL) строится с помощью ORM _Hibernate_, в процессе компиляции используется библиотека аннотаций _Lombok_, а для управления состояниями базы данных _Liquibase_.

>* [Функционал приложения](#функционал-приложения)
>* [Запуск приложения](#запуск-приложения)
>* [Архитектура приложения](#архитектура-приложения)


## Функционал приложения
Приложение **Cloudwork** производит операции над файлами, принадлежащими зарегистрированным пользователям. Учётные данные зарегистрированных пользователей и их файлы хранятся в БД. Команды на операции считываются из http-запросов, поступающих от лицевого приложения на оконечные адреса (такназываемые "эндпойнты"). Совокупность допустимых для программы запросов и ожидаемых ответов на них называется АПИ и собрана в [спецификацию](./CloudServiceSpecification.yaml).

### Авторизация

**CloudWork** осуществляет авторизацию пользователей по логину и паролю. Он принимает неавторизованные запросы на эндпойнт `"/login"`, служащий для входа в систему, и авторизованные запросы на остальные эндпойнты. В ответ на корректные логин и пароль приложение генерирует и отсылает авторизационный токен. Затем авторизованный запрос должен содержать этот токен в определённом заголовке.  
Запрос к эндпойнту `"/logout"` соответствует выходу пользователя из системы.

| эндпойнт  | метод | принимает                           | возвращает              |
|-----------|-------|-------------------------------------|-------------------------|
| `/login`  | POST  | json-объект с _логином_ и _паролем_ | json-объект с _токеном_ |
| `/logout` | POST  | -                                   | -                       |

> Управление пользователями (помимо назначения им токенов доступа) не входит в функционал программного комплекса, приложение имеет дело с теми учётными записями, которые предоставляются БД. Для демонстрации работы программы используется тестовая база данных с двумя пользователями:
> 
>| "почта"    | "пароль" |
>|------------|----------|
>| `user`     | `0000`   |
>| `who_user` | `1111`   |
> 
> Контроль состояния БД в даном проекте поручен мигратору _Liquibase_.
>
> > Вместе с тем, в приложении присутствует дополнительный компонент для преддобавления в БД какого-то количества определённых в коде юзеров при запуске: [UserPreloader](./src/main/java/ru/netology/cloudwork/UserPreloader.java), он включается в [настройках](./src/main/resources/application.yaml) `application: user-preloader: enabled`.

### Работа с файлами

**CloudWork** работает с файлами пользователя, имя которого узнаёт по авторизационному токену из запроса. Поддерживаются запросы загрузить файл, скачать файл, переименовать файл или удалить его, а также запрос списка загруженных файлов (ограниченной длины), о каждом из которых сообщается имя и размер. 

| эндпойнт | метод  | принимает                                                                  | возвращает                                          |
|----------|--------|----------------------------------------------------------------------------|-----------------------------------------------------|
| `/list`  | GET    | _количество файлов в списке_ в параметре                                   | массив json-объектов с _именем_ и _размером_ файлов |
| `/file`  | POST   | _имя файла_ в параметре,</br> _сам файл_ в теле запроса                    | -                                                   |
| `/file`  | GET    | _имя файла_ в параметре                                                    | сам файл                                            |
| `/file`  | PUT    | _имя файла_ в параметре, </br> json-объект с _новым именем_ в теле запроса | -                                                   |
| `/file`  | DELETE | _имя файла_ в параметре                                                    | -                                                   |
> Фактическая реализация фронт-приложения всегда запрашивает список длиной в три файла.
> 
> Фактическая реализация переименования файла во фронт-приложении присылает в качестве нового имени случайное трёхзначное десятичное число.
> 
> Хотя фронт-приложение отображает сортировку файлов по дате, а CloudWork сохраняет дату загрузки и изменения файла, эти данные не передаются от сервера и лишь симулируются клиентом.


## Запуск приложения
**CloudWork** это не самодостаточное приложение, а _сервис_, предназначенный для работы в составе _программного комплекса_.  
_Сервис_ выполняет собственно операции с файлами, нуждаясь при этом в _СУБД_, где хранятся учётные записи пользователей и их материалы, и в _лицевом приложении_, через которое с комплексом взаимодействует пользователь.
В принципе каждая программа может быть запущена любым возможным способом, даже на разных ЭВМ, лишь бы они могли найти друг друга благодаря корректным настройкам.

Рекомендуется запускать программный комплекс с помощью средства Докер-Компоуз. Предлагаемый [сценарий запуска](./docker-compose.yml) гарантирует сборку докер-образов тылового и лицевого приложений из _исходников в данном репозитории_ и их согласованный старт и взаимодействие между собой и с БД "MySQL". Запуск сценария производится командой `docker-compose up -d` в рабочем каталоге проекта.

## Архитектура приложения
Приложение **CloudWork** построено на каркасе Спринг Бут, и большинство компонентов автоматически инициализируются в его рамках.
Приложение построено по стандартной слоистой архитектуре с обычным разделением функций, отражённым в структуре проекта:
* _**фильтры**_ осуществляют предварительный анализ запроса, в частности фильтры, реализующие систему безопасности CloudWork, авторизуют запрос, находя в нём идентифицирующую информацию, т.е. сопоставляет учётную запись пользователя потоку обработки этого запроса. Вот полная цепочка фильтров, присутствующих в приложении, выделены кастомные компоненты:
  > * **DisableEncodeUrlFilter**
  > * **WebAsyncManagerIntegrationFilter**
  > * **SecurityContextHolderFilter**
  > * **HeaderWriterFilter**
  > * **CorsFilter**
  > * **[ExceptionHandlerFilter](./src/main/java/ru/netology/cloudwork/filter/ExceptionHandlerFilter.java "Фильтр, размещённый непосредственно перед TokenFilter, призванный формировать правильные ответы об ошибках всякий раз, когда возникают исключения за пределами области действия, которую покрывает ErrorController. Если обработка входящего запроса где-то далее в цепи вызывает исключение, оно здесь перехватывается и записывается в ответ, обёрнутое в подобающее DTO, при этом ответу устанавливается статус 401,  если исключение было экземпляром AuthenticationException, и 500 в иных случаях.")** - _обработчик исключений авторизации CloudWork_
  > * **[TokenFilter](./src/main/java/ru/netology/cloudwork/filter/TokenFilter.java "Фильтр, проверяющий, содержат ли запросы нужный токен в том самом заголовке. Если запрос действительно содержит токен, назначенный какому-либо пользователю, он аутентифицируется, в противном случае отклоняется.")** - _фильтр, анализирующий входящие запросы на предмет наличия в них токена аутентификации и запускающий процедуру их аутентификации_
  > * **LogoutFilter** - _фильтр, завершающий сессию пользователя при его выходе из системы, он запускает специально определённый_ **[CloudworkLogoutHandler](./src/main/java/ru/netology/cloudwork/filter/CloudworkLogoutHandler.java "Обработчик выхода пользователя из CloudWork. Он запускается фильтром выхода из системы и подаёт команду службе авторизации на завершение текущего сеанса.")**_, который аннулирует сессию_
  > * **RequestCacheAwareFilter**
  > * **SecurityContextHolderAwareRequestFilter**
  > * **SessionManagementFilter**
  > * **ExceptionTranslationFilter**
  > * **AuthorizationFilter**

* _**контроллеры**_ считывают команду и данные из http-запроса, передают их на обработку сервисам и возвращают клиентскому приложению результат выполнения в формате http-ответа; в приложении присутствуют два контроллера:
  * **[EntranceController](./src/main/java/ru/netology/cloudwork/controller/EntranceController.java "Контроллер, отвечающий за вход и выход пользователя из системы.")** - _принимает POST-запрос на авторизацию, приходящий на эндпойнт `"/login"`, а также отрабатывает GET-перенаправление на этот адрес после того, как LogoutHandler отработал запрос на `"/logout"`._
  * **[FileController](./src/main/java/ru/netology/cloudwork/controller/FileController.java "Контроллер, отвечающий за связанные с файлами операции в соответствии со Спецификацией. Он определяет локальное имя пользователя и обращается к FileService с запросами, определяемыми как имя файла и владелец (клиент).")** - _обрабатывает все остальные запросы кроме логина и логаута, т.е. все запросы на эндпойнты `"/list"` и `"/file"` согласно [спецификации](./CloudServiceSpecification.yaml)._
* _**сервисы**_ отражают основной функционал приложения (такназыаемую "бизнес-логику"); присутствует сервис для работы с юзерами, сервис для операций с файлами и авторизационный сервис:
  * **[CloudworkAuthorizationService](./src/main/java/ru/netology/cloudwork/service/CloudworkAuthorizationService.java "Служба пользовательских токенов и сессий, а также реализация функционала AuthenticationManager в CloudWork. Для более быстрого ответа содержит также карту активных токенов, сопоставленных вошедшим в систему пользователями.")** - управляет вопросами пользовательских сессий CloudWork, содержит основную логику авторизации.
  * **[UserManager](./src/main/java/ru/netology/cloudwork/service/UserManager.java "Служба для управления и получения информации о пользователях, такой как UserDetails и назначенные токены сеансов CloudWork. Также реализует функционал UserDetailsService в рамках механизма аутентификации Спринг Секьюрити.")** - решает все вопросы, относящиеся к хранимым в БД данным о пользователях, в том числе управление сопоставленными токенами доступа и предоставление UserDetails для авторизации.
  * **[FileService](./src/main/java/ru/netology/cloudwork/service/FileService.java "Служба для выполнения всех необходимых по спецификации операций с файлами.")** - решает все вопросы, относящиеся к хранению файлов в БД.
* _**репозитории**_ реализуют взаимодействию с СУБД, т.е. запрашивают данные о файлах и пользователях из хранилища и сохраняют их в нём.

### Модель предметной области
Программа манипулирует с двумя основными видами сущностей: учётные записи пользователей (владельцев файлов) и собственно хранимые файлы. Данные о них хранятся в двух соответственных таблицах в БД.

О хранимом файле (_[FileEntity](./src/main/java/ru/netology/cloudwork/entity/FileEntity.java)_) в базе держится следующая информация:
* `file_id` - уникальный идентификатор (назначается СУБД);
* `file_name` - имя файла;
* `size` - размер файла;
* `owner_user_id` - идентификатор владельца файла (внешний ключ);
* `file_type` - тип содержимого (если файл может о нём сообщить);
* `body` - байтовый массив, составляющий файл как таковой;
* `upload_date` - дата загрузки файла;
* `update_date` - дата крайнего изменения (переименования) файла.

Управление пользователями CloudWork использует технологию Spring Security, поэтому помимо базовой информации для идентификации в базе юзеров (_[UserEntity](./src/main/java/ru/netology/cloudwork/entity/UserEntity.java)_) на всякий случай хранится информация об учётной записи, соответствующая дополнительным возможностям интерфейса _UserDetails_:
* `user_id` - уникальный идентификатор (назначается СУБД);
* `username` - логин, имя юзера;
* `password` - пароль (хранится в закодированном виде);
* `authorities` - полномочия пользователя, т.е. набор его ролей (в БД хранится как строка, где названия ролей соединены через запятую, т.е. в формате CSV);
* `files` - список файлов данного пользователя (отражение связи с таблицей файлов);
* `account_expired` - истёк ли срок действия учётной записи;
* `locked` - заблокирована ли учётная запись;
* `credentials_expired` - истёк ли срок действия авторизации;
* `enabled` - включена ли учётная запись; 
* `access_token` - токен доступа, означающий сопоставленную пользователю сессию доступа CloudWork.


### Аутентификация на основе Спринг Секьюрити
Запросы к сервису должны быть авторизованы, т.е. каждый запрос однозначно сопоставлен с каким-то зарегистрированным пользователем. Запрос на авторизацию заключается в том, что от клиента приходит пара логин-пароль. Если она корректна, открывается новая сессия: клиенту высылается токен доступа. Авторизованные запросы от лицевого приложения должны затем содержать этот токен в заголовке `auth-token`. Говорится, что "сессия пользователя открыта", когда ему сопоставлен токен доступа (т.е. сохранён в БД в строке соответствующего пользователя), закрытие сессии соответствует установлению значения колонки с токеном в нуль.

Приложение CloudWork использует модель аутентификации на основе токена, это не JWT-токен, а просто строка с именем пользователя и датой генерации, токен тождественен самому себе, является меткой пользовательской сессии и не имеет никаких дополнительных свойств безопасности, таких как срок действия или секретный ключ.

#### Получение доступа
Для получения авторизации от лицевого приложения на `"/login"` приходит джейсон-объект с логином и паролем (заявка от клиента на авторизацию). Сервис проверяет, что присланные учётные данные соответствуют активной учётной записи, и генерирует токен доступа, сохраняемый в сопоставление этому пользователю и высылаемый в ответ. Если для данного пользователя уже существует открытая сессия, то переиспользуется старый токен.  

#### Авторизованный доступ
Когда запрос приходит на CloudWork, он анализируется фильтром TokenFilter. Если адрес требует авторизации (т.е. для всех эндпойнтов кроме `"/login"`), то проверяется токен из заголовка `auth-token`. Если токена нужного формата не обнаружено, возвращается ответ 401. Если же корректный токен извлечён из заголовка, он отправляется в CloudworkAuthorizationService - тот находит по БД, есть



Совокупность используемых в приложении средств безопасности называется иногда в документации "моделью CloudWork", которая включает следующие классы:
* [_Role_](./src/main/java/ru/netology/cloudwork/model/Role.java), воплощение интерфейса _GrantedAuthority_ ("выданные полномочия") - константный список предусмотренных моделью ролей, состоит из элементов `USER`, который используется по умолчанию во всех случаях, и `SUPERUSER`, который предуготовляется для специальных админских процедур и в данной реализации никак не задействуется.
* [_UserInfo_](./src/main/java/ru/netology/cloudwork/model/UserInfo.java), воплощение интерфейса _UserDetails_ ("подробности пользователя") - представление данных об учётной записи, соответствующее хранимой в БД сущности, несёт в себе имя пользователя, пароль, набор полномочий и биты, означающие четыре причины, по которым учётная запись может быть не активна: `accountExpired`, `locked`, `credentialsExpired`, `enabled` (три первых у активного аккаунта имеют значение НЕТ, последнее ДА). При создании объект _UserInfo_ обычно заполняется данными на основе соответствующей _UserEntity_.
* [_CloudworkAuthorization_](./src/main/java/ru/netology/cloudwork/model/CloudworkAuthorization.java), воплощение интерфейса _Authentication_ ("аутентификация") - представление состояние аутентифицированности для пользователя CloudWork, несёт в себе имя пользователя, пароль, набор полномочий (_Role_, в данной реализации используется только `USER`) и бит аутентифицированности, соответствующий тому, что этот пользователь предоставил в запросе валидный токен доступа и получил через то авторизованный доступ к системе (как USER).


А также классы-фильтры, участвующие в Цепи Фильтров Безопасности - вот полный список используемых в проекте фильтров, большинство из которых являются стандартными инфраструктурными компонентами Spring Boot, а три относятся к приложению CloudWork:

> * DisableEncodeUrlFilter
> * WebAsyncManagerIntegrationFilter
> * SecurityContextHolderFilter
> * HeaderWriterFilter 
> * CorsFilter
> * **ExceptionHandlerFilter** - _обработчик исключений авторизации CloudWork_
> * **TokenFilter** - _фильтр, анализирующий входящие запросы на предмет наличия в них токена аутентификации и запускающий процедуру их аутентификации_
> * LogoutFilter - _фильтр, завершающий сессию пользователя при его выходе из системы, он запускает специально определённый_ **CloudworkLogoutHandler**_, который аннулирует сессию_
> * RequestCacheAwareFilter
> * SecurityContextHolderAwareRequestFilter
> * SessionManagementFilter
> * ExceptionTranslationFilter
> * AuthorizationFilter

* **[**TokenFilter**](./src/main/java/ru/netology/cloudwork/filter/TokenFilter.java)** (воплощает `OnePerRequestFilter`) - ищет во входящих запросах токен CloudWork. Имя заголовка, несущего токен, и префикс строки токена могут специфицироваться через настройки `application: token-header` и `application: token-prefix`'. Запросы на `"/login"` пропускаются, ибо авторизации не требуют. Найденный токен отправляется на авторизацию в `CloudworkAuthorizationService`, который или аутентицирует запрос, или, если что-то не так, отреагирует исключением.
* [**CloudworkLogoutHandler**](./src/main/java/ru/netology/cloudwork/filter/CloudworkLogoutHandler.java) (воплощает `LogoutHandler`) - обработчик выхода из системы, запускаемый `LogoutFilter`-ом согласно стандартной отработке логаута в Спринг Бут. Его работа заключается в том, что он распоряжается `CloudworkAuthorizationService`-у выполнить процедуру завершения сеанса пользователя.
* [**ExceptionHandlerFilter**](./src/main/java/ru/netology/cloudwork/filter/ExceptionHandlerFilter.java) (воплощает `OnePerRequestFilter`) - обрабатывает должным образом исключения, возникшие в фильтрах, т.е. находящиеся вне зоны ответственности `ErrorController`-а. Если перехватываемое исключение относится к подмножеству `AuthenticationException`, то ответ маркируется http-кодом 401, в ином случае - кодом 500.

Сервисы CloudWork также являются частью системы безопасности:
* [**UserManager**](./src/main/java/ru/netology/cloudwork/service/UserManager.java), воплощающий интерфейс `UserDetailsManager` - используется для всех операций, связанных с управлением пользователями, в том числе для получения учётных данных пользователя (UserDetails) по его имени, как то требуется при стандартной процедуре проверки учётных данных в Спринг Секьюрити.
* [**CloudworkAuthorizationService**](./src/main/java/ru/netology/cloudwork/service/CloudworkAuthorizationService.java), воплощающий интерфейс `AuthenticationManager` - сервис, реализующий основную логику системы управления пользователями и сессиями CloudWork. Он работает четырьмя методами:
1.  `public LoginResponse initializeSession(LoginRequest loginRequest)` - проверяет полученный из запроса логин и пароль и, в случае успеха, возвращает обёрнутый токен доступа, который либо генерируется, либо переиспользуется из уже открытой сессии:
     * принимает запрос на аутентификацию, содержащий переданный от веб-приложения логин и пароль;
     * просит `UserManager`-а по логину найти данные пользователя; если он не сможет этого сделать, выдаст `UsernameNotFoundException`;
     * сверяет полученный пароль с паролем из данных пользователя (с учётом зашифрованной кодировки его хранения в БД); если не совпадёт, выдаст `BadCredentialsException`;
     * интересуется у `UserManager`-а, не существует ли уже открытая сессия для этого пользователя (т.е. сопоставлен ли ему ненулевой токен), и, если нет, то генерирует новый токен и говорит `UserManager`-у записать его в БД в строку этого юзера;
     * возвращает, обёрнутым в `LoginResponse`, токен доступа для прошедшего проверку логин-запроса (новосгенерированный или переиспользуя существующий).
2. `public void authenticateByToken(String token)` - аутентицирует текущий запрос по строке токена, взятой из его заголовка:
     * спрашивает у `UserManager`-а, какому пользователю сопоставлен такой токен; если никакому, то выдаст `BadCredentialsExcepption`, так как это значит, что сессия, которой этот токен соответствует, видимо уже закончена;
     * ставит в `SecurityContextHolder` объект `CloudworkAuthorization`, соответствующий учётным данным найденного по базе пользователя, таким образом аутентицируя текущий поток; если аутентикация невозможна по одной из причин, предусмотренных в методе `.authenticate()`, бросается соответственное исключение.
3. `public void terminateSession(String username)` - завершает текущую сессию пользователя, распоряжаясь `UserManager`-у установить значение токена для указанного юзернейма в нуль.
4. `public Authentication authenticate(Authentication authentication)` - во исполнение должности `AuthenticationManager` принимает объект аутентификации, находит с помощью `UserManager`-а соответствующие ему данные пользователя и проверяет, не отключена ли запись, не заблокирована ли, и есть ли у этого пользователя сопоставленный ненулевой токен; если что-то из этого окажется не фактом, выдаст `DisabledException`, `LockedException` или `BadCredentialsException`, а если всё в порядке, то аутентицирует этот объект и вернёт его обратно.
5. `private String generateTokenFor(UserDetails authentication)` - внутренний метод создания токена для предложенного пользователя. Модель CloudWork использует очень простую строку из логина и текущей даты, как `"%s @ %s".formatted(имя_пользователя, new Date())`.












### Сервисы CloudWork
Основная логика работы приложения реализована в службах, т.е. сервисах:
* **CloudworkAuthorizationService** - управляет вопросами пользовательских сессий CloudWork.
* **UserManager** - решает все вопросы, относящиеся к хранимым в БД данным о пользователях, в том числе управление сопоставленными токенами доступа и предоставление UserDetails для авторизации.
* **FileService** - решает все вопросы, относящиеся к хранению файлов в БД.

### Контроллеры CloudWork
* **EntranceController** - принимает POST-запрос на авторизацию, приходящий на эндпойнт `"/login"`, а также отрабатывает GET-перенаправление на этот адрес после того, как LogoutHandler отработал запрос на `"/logout"`.
* **FileController** - обрабатывает все остальные запросы кроме логина и логаута, т.е. все запросы на эндпойнты `"/list"` и `"/file"` согласно [спецификации](./CloudServiceSpecification.yaml).  

### Репозитории CloudWork
* **UserRepository** - JpaRepository-интерфейс, производит все необходимые запросы к БД по таблице `users`.
* **FileRepository** - JpaRepository-интерфейс, производящий все необходимые запросы к БД по таблице `files`.
___


