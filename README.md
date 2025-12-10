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
- ✅ Шифрование и маскирование номеров карт (HashiCorp Vault)
- ✅ Миграции через Liquibase
- ✅ Документация Swagger/OpenAPI
- ✅ Docker Compose для dev-среды
- ✅ Юнит-тесты бизнес-логики
- ✅ Централизованное логирование (SLF4J + Logback)
- ✅ ELK Stack (Elasticsearch, Logstash, Kibana, Filebeat)
- ✅ JSON-логи для продакшена
- ✅ Трассировка запросов (requestId, userId)
- ✅ Мониторинг метрик (Prometheus + Grafana)
- ✅ Дашборды с визуализацией (JVM, HTTP, DB connections)

## Технологии
| Категория       | Технологии                                                                    |
|-----------------|-------------------------------------------------------------------------------|
| Backend         | Java 17, Spring Boot 3.5, Spring Security, Spring Data JPA                    |
| База данных     | PostgreSQL 12, Liquibase                                                      |
| Безопасность    | JWT, HashiCorp Vault                                                          |
| Логирование     | SLF4J, Logback, Logstash Encoder                                              |
| Мониторинг      | Prometheus, Grafana 10.2, Elasticsearch 8.11, Kibana 8.11, Logstash, Filebeat |
| Документация    | Swagger/OpenAPI 3.0                                                           |
| Контейнеризация | Docker, Docker Compose                                                        |
| Тестирование    | JUnit 5, Mockito                                                              |

## Структура проекта
```
src/
├── main/
│   ├── java/com/example/bankcards/
│   │   ├── aspect/           # AOP-аспекты (логирование)
│   │   ├── config/           # Конфигурация (Security, Beans)
│   │   ├── constant/         # Константы
│   │   ├── controller/       # REST-контроллеры
│   │   │   └── admin/        # Админские контроллеры
│   │   ├── dto/              # DTO для запросов/ответов
│   │   ├── entity/           # JPA-сущности
│   │   ├── exception/        # Обработка ошибок
│   │   │   └── errors/       # Enum ошибок
│   │   ├── filter/           # Фильтры (MDC для логирования)
│   │   ├── repository/       # JPA-репозитории
│   │   ├── security/         # JWT, фильтры безопасности
│   │   ├── service/          # Бизнес-логика
│   │   │   └── impl/         # Реализации сервисов
│   │   └── util/             # Утилиты (шифрование карт)
│   └── resources/
│       ├── db/migration/     # Миграции Liquibase
│       ├── static/docs/      # OpenAPI спецификация
│       ├── application.yml   # Конфигурация приложения
│       └── logback-spring.xml # Конфигурация логирования
├── test/                     # Юнит-тесты
├── elk/                      # Конфигурация ELK Stack
│   ├── filebeat/
│   ├── logstash/
│   └── kibana/
└── monitoring/               # Конфигурация Prometheus + Grafana
    ├── prometheus/
    └── grafana/
        ├── provisioning/
        └── dashboards/
```

## Запуск проекта

### Переменные окружения
Перед запуском заполните файл `credentials-dev.env` (локально) или `credentials-docker.env` (Docker):

```env
# Database
POSTGRES_URL=jdbc:postgresql://localhost:5432/bankcards
POSTGRES_USER=postgres
POSTGRES_PASSWORD=your_password
POSTGRES_DB=bankcards
POSTGRES_PORT=5432:5432

# Server
SERVER_PORT=8185
BACKEND_PORT=8185:8185

# JWT
JWT_SECRET=your_jwt_secret_key

# Vault
VAULT_ADDR=http://localhost:8200
VAULT_ROOT_TOKEN=root
VAULT_ENCRYPTION_KEY=your_32_char_encryption_key

# Mail (опционально)
SENDER_MAIL=your_email@yandex.ru
SENDER_PASSWORD=your_app_password

# pgAdmin
PGADMIN_DEFAULT_EMAIL=admin@admin.com
PGADMIN_DEFAULT_PASSWORD=admin
PGADMIN_PORT=5050:80
```

### Вариант 1: Полный запуск через Docker (с ELK)

```bash
# Запуск всех сервисов включая ELK Stack
docker-compose --env-file credentials-docker.env up -d

# Или без ELK (только основные сервисы)
docker-compose --env-file credentials-docker.env up -d backend db vault vault-init pgadmin
```

### Вариант 2: Backend через IDE + Docker для инфраструктуры

```bash
# 1. Запустите инфраструктуру
docker-compose --env-file credentials-dev.env up -d db vault vault-init pgadmin

# 2. Опционально: запустите ELK Stack
docker-compose --env-file credentials-dev.env up -d elasticsearch kibana logstash filebeat

docker-compose --env-file credentials-dev.env up -d elasticsearch kibana logstash filebeat db vault-init vault grafana prometheus

# 3. В application.yml укажите:
#    import: optional:file:credentials-dev.env[.properties]

# 4. Запустите Bank_RESTApplication.java из IDE
```

### Доступ к сервисам

| Сервис            | URL                                   | Описание             |
|-------------------|---------------------------------------|----------------------|
| **Backend API**   | http://localhost:8185                 | REST API             |
| **Swagger UI**    | http://localhost:8185/swagger-ui.html | Документация API     |
| **Grafana**       | http://localhost:3000                 | Дашборды и метрики   |
| **Prometheus**    | http://localhost:9090                 | Сбор метрик          |
| **Kibana**        | http://localhost:5601                 | Визуализация логов   |
| **Elasticsearch** | http://localhost:9200                 | Поиск по логам       |
| **pgAdmin**       | http://localhost:5050                 | Управление БД        |
| **Vault**         | http://localhost:8200                 | Управление секретами |

## Логирование

### Архитектура
```
┌─────────────┐     ┌─────────────┐     ┌─────────────┐     ┌─────────────┐
│  Spring App │ ──► │   stdout    │ ──► │  Filebeat   │ ──► │    ELK      │
│  (Logback)  │     │   (JSON)    │     │  Logstash   │     │   Stack     │
└─────────────┘     └─────────────┘     └─────────────┘     └─────────────┘
```

### Профили логирования
- **dev/default** — цветной вывод в консоль (человекочитаемый)
- **prod/docker/k8s** — JSON-формат для ELK
- **test** — минимальное логирование

### Пример JSON-лога (prod)
```json
{
  "timestamp": "2025-12-09T10:30:45.123Z",
  "level": "INFO",
  "logger": "CardServiceImpl",
  "message": "Карта успешно создана",
  "cardId": 123,
  "ownerEmail": "user@example.com",
  "requestId": "a1b2c3d4",
  "userId": "456",
  "clientIp": "192.168.1.1",
  "app": "bank-rest",
  "env": "prod"
}
```

### Настройка Kibana
1. Откройте http://localhost:5601
2. **Management** → **Stack Management** → **Data Views**
3. Создайте Data View: `bankcards-logs-*`
4. Выберите `@timestamp` как Time field
5. Перейдите в **Discover** для просмотра логов

### Полезные запросы в Kibana
```
# Все ошибки
level: "ERROR"

# Логи конкретного пользователя
userEmail: "user@example.com"

# Трассировка запроса
requestId: "a1b2c3d4"

# Медленные запросы
executionTimeMs > 1000

# Операции с картами
logger: "CardServiceImpl"
```

## Мониторинг (Prometheus + Grafana)

### Архитектура

```
┌─────────────┐     ┌─────────────┐     ┌─────────────┐
│  Spring App │ ──► │  Prometheus │ ──► │   Grafana   │
│  (Actuator) │     │   (Scrape)  │     │ (Dashboard) │
└─────────────┘     └─────────────┘     └─────────────┘
```

### Доступные метрики

- **JVM**: память (heap/non-heap), потоки, GC
- **HTTP**: запросы в секунду, время ответа, ошибки по endpoint
- **Database**: HikariCP connections (active/idle/pending)
- **System**: CPU usage, uptime

### Настройка Grafana

1. Откройте http://localhost:3000
2. Логин: `admin` / Пароль: `admin`
3. Дашборд `Bank Cards - Application Metrics` уже настроен

### Prometheus Endpoints

```bash
# Health check
curl http://localhost:8185/actuator/health

# Metrics в формате Prometheus
curl http://localhost:8185/actuator/prometheus

# Список всех метрик
curl http://localhost:8185/actuator/metrics
```

### Полезные PromQL запросы

```promql
# CPU usage (%)
system_cpu_usage * 100

# Memory usage (%)
jvm_memory_used_bytes{area="heap"} / jvm_memory_max_bytes{area="heap"} * 100

# Request rate
rate(http_server_requests_seconds_count[1m])

# Error rate (5xx)
sum(rate(http_server_requests_seconds_count{status=~"5.."}[5m]))
```

## API Эндпоинты

### Аутентификация (`/authorize`)
| Метод | Путь | Описание |
|-------|------|----------|
| POST | `/authorize/login` | Авторизация, получение JWT |
| POST | `/authorize/register` | Регистрация пользователя |
| POST | `/authorize/verificateCode` | Повторная отправка кода |
| POST | `/authorize/verification` | Верификация email |

### Пользователь (`/users`)
| Метод | Путь | Описание |
|-------|------|----------|
| GET | `/users/info` | Информация о текущем пользователе |
| PATCH | `/users/update` | Обновление данных |
| POST | `/users/logout` | Выход из системы |

### Карты (`/cards`)
| Метод | Путь | Описание |
|-------|------|----------|
| GET | `/cards` | Все карты пользователя |
| GET | `/cards/{id}` | Карта по ID |
| GET | `/cards/{id}/balance` | Баланс карты |
| GET | `/cards/search` | Поиск с пагинацией |
| POST | `/cards/transfer` | Перевод между картами |
| POST | `/cards/{id}/block-request` | Запрос на блокировку |
| GET | `/cards/block-requests` | Мои запросы на блокировку |

### Админ: Пользователи (`/admin/users`)
| Метод | Путь | Описание |
|-------|------|----------|
| GET | `/admin/users` | Все пользователи |
| POST | `/admin/users` | Создать пользователя |
| PATCH | `/admin/users/{id}` | Обновить пользователя |
| DELETE | `/admin/users/{id}` | Удалить пользователя |

### Админ: Карты (`/admin/cards`)
| Метод | Путь | Описание |
|-------|------|----------|
| GET | `/admin/cards` | Все карты |
| POST | `/admin/cards` | Создать карту |
| DELETE | `/admin/cards/{id}` | Удалить карту |
| PATCH | `/admin/cards/{id}/block` | Заблокировать карту |
| PATCH | `/admin/cards/{id}/activate` | Активировать карту |
| PATCH | `/admin/cards/{id}/test-balance` | Изменить баланс (тест) |

### Админ: Запросы на блокировку (`/admin/cards/block-requests`)
| Метод | Путь | Описание |
|-------|------|----------|
| GET | `/admin/cards/block-requests` | Все запросы |
| POST | `/admin/cards/block-requests/{id}/approve` | Одобрить |
| POST | `/admin/cards/block-requests/{id}/reject` | Отклонить |

## Безопасность
- Все маршруты защищены через Spring Security + JWT
- **ADMIN** — полный доступ ко всем ресурсам
- **USER** — доступ только к своим картам и данным
- Номера карт шифруются через HashiCorp Vault
- В ответах API номера карт маскируются (`**** **** **** 1234`)

## Тестирование

```bash
# Запуск всех тестов
./mvnw test

# Запуск с отчётом о покрытии
./mvnw test jacoco:report
```

## Полезные команды

```bash
# Проверка здоровья Elasticsearch
curl http://localhost:9200/_cluster/health?pretty

# Просмотр индексов логов
curl http://localhost:9200/_cat/indices?v

# Количество логов
curl http://localhost:9200/bankcards-logs-*/_count

# Очистка логов
curl -X DELETE http://localhost:9200/bankcards-logs-*

# Пересборка Docker-образа backend
docker-compose build backend

# Просмотр логов контейнера
docker logs -f bankcards_backend
```

## Автор
- Вениамин

