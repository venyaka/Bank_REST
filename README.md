
# Bank REST API

## Описание
Система управления банковскими картами. Позволяет создавать, просматривать, блокировать, активировать и удалять карты, а также выполнять переводы между своими картами. Реализована аутентификация и авторизация с ролями ADMIN и USER, шифрование и маскирование номеров карт, фильтрация и пагинация, обработка ошибок, покрытие тестами.

## Основные возможности
- ✅ Создание и управление картами (CRUD)
- ✅ Просмотр карт (поиск, пагинация)
- ✅ Переводы между своими картами
- ✅ Запрос блокировки карты
- ✅ Просмотр баланса
- ✅ Аутентификация и авторизация (Spring Security + JWT)
- ✅ Ролевой доступ (ADMIN, USER)
- ✅ Управление пользователями (ADMIN)
- ✅ Шифрование и маскирование номеров карт
- ✅ Миграции через Liquibase
- ✅ Документация Swagger/OpenAPI
- ✅ Docker Compose для dev-среды
- ✅ Юнит-тесты бизнес-логики

## Структура проекта
- `src/main/java/com/example/bankcards/controller` — REST-контроллеры
- `src/main/java/com/example/bankcards/service` — бизнес-логика
- `src/main/java/com/example/bankcards/repository` — доступ к данным
- `src/main/java/com/example/bankcards/entity` — сущности JPA
- `src/main/java/com/example/bankcards/dto` — DTO для запросов/ответов
- `src/main/resources/db/migration` — миграции Liquibase
- `src/main/resources/static/docs/openapi.yaml` — OpenAPI спецификация
- `src/test/java/com/example/bankcards` — юнит-тесты

## Запуск проекта

Перед запуском проекта обязательно заполните свои данные в файле `credentials-dev.env` (для локального запуска) или `credentials-docker.env` (для запуска через Docker) в корне проекта. В этих файлах указываются параметры для подключения к базе данных, pgAdmin, Vault и другие переменные окружения.

> **Важно:**
> - Для локального запуска используйте `credentials-dev.env`.
> - Для запуска через Docker используйте `credentials-docker.env`.
> - В файле `application.yml` в строке:
>   ```yaml
>   import: optional:file:credentials-dev.env[.properties]
>   ```
>   укажите нужный файл, например:
>   ```yaml
>   import: optional:file:credentials-docker.env[.properties]
>   ```
> - В `docker-compose.yml` для Docker-окружения используйте:
>   ```yaml
>   env_file:
>     - credentials-docker.env
>   ```
Заполните файл `credentials-docker.env`, затем выполните команду:
```bash
docker-compose --env-file credentials-dev.env up --build
```
- Swagger UI: http://localhost:8185/swagger-ui.html

### 2. Запуск backend через IDE, остальные сервисы — через Docker
1. Запустите БД через Docker:
   ```bash
   docker-compose --env-file credentials-dev.env up db pgadmin vault vault-init
   ```
2. Убедитесь, что переменные окружения backend соответствуют настройкам БД (см. `application.yml` и `.env`).
3. Запустите backend из IDE (`Bank_RESTApplication.java`).
4. В файле `application.yml` укажите:
   ```yaml
   import: optional:file:credentials-dev.env[.properties]
   ```
5. Запустите backend из IDE (`Bank_RESTApplication.java`).

## Swagger
- Swagger UI: http://localhost:8185/swagger-ui.html
- OpenAPI спецификация: `src/main/resources/static/docs/openapi.yaml`

## Основные эндпоинты

### Аутентификация и Регистрация (`/authorize`)
- `POST /authorize/login` — Вход в систему, получение JWT токенов.
- `POST /authorize/register` — Регистрация нового пользователя.
- `POST /authorize/verificateCode` — Повторная отправка кода верификации.
- `GET, POST /authorize/verification` — Верификация пользователя по email и токену.

### Управление текущим пользователем (`/users`)
- `GET /users/info` — Получение информации о текущем пользователе.
- `PATCH /users/update` — Обновление данных текущего пользователя.
- `POST /users/logout` — Выход из системы.

### Управление картами пользователя (`/cards`)
- `GET /cards` — Получить все свои карты.
- `GET /cards/{id}` — Получить карту по ID.
- `GET /cards/{id}/balance` — Получить баланс карты по ID.
- `POST /cards/transfer` — Перевод средств между своими картами.
- `POST /cards/{id}/block-request` — Создать запрос на блокировку своей карты.
- `GET /cards/block-requests` — Посмотреть свои запросы на блокировку.

### Администрирование: Карты (`/admin/cards`)
- `GET /admin/cards` — Получить все карты в системе.
- `POST /admin/cards` — Создать новую карту для любого пользователя.
- `DELETE /admin/cards/{id}` — Удалить карту.
- `PATCH /admin/cards/{id}/block` — Заблокировать карту.
- `PATCH /admin/cards/{id}/activate` — Активировать карту.

### Администрирование: Пользователи (`/admin/users`)
- `GET /admin/users` — Получить всех пользователей.
- `POST /admin/users` — Создать нового пользователя.
- `PATCH /admin/users/{id}` — Обновить данные пользователя.
- `DELETE /admin/users/{id}` — Удалить пользователя.

### Администрирование: Запросы на блокировку (`/admin/cards/block-requests`)
- `GET /admin/cards/block-requests` — Получить все запросы на блокировку.
- `POST /admin/cards/block-requests/{requestId}/approve` — Подтвердить запрос на блокировку.
- `POST /admin/cards/block-requests/{requestId}/reject` — Отклонить запрос на блокировку.

## Защита маршрутов
- Все маршруты защищены через Spring Security + JWT.
- Доступ к эндпоинтам:
    - ADMIN: полный доступ ко всем картам и пользователям
    - USER: доступ только к своим картам и переводам
- Маскирование номеров карт реализовано для всех ответов

## Переменные окружения
- `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USER`, `DB_PASSWORD` — настройки БД
- `JWT_SECRET` — секрет для токенов
- `VAULT_ADDR` — адрес для подключения к Vault
- `VAULT_TOKEN` — токен для аутентификации в Vault
- `VAULT_ROOT_TOKEN` — корневой токен для инициализации Vault в Docker
- `MAIL_HOST`, `MAIL_PORT`, `MAIL_USER`, `MAIL_PASSWORD` — настройки почты (если используется)
- Пример: `credentials.env`, `application.yml`

## Авторы
- Вениамин
