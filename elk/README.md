# ELK Stack для Bank REST API

## Компоненты

| Сервис | Порт | Описание |
|--------|------|----------|
| Elasticsearch | 9200 | Хранение и поиск логов |
| Kibana | 5601 | Визуализация и дашборды |
| Logstash | 5044, 5000 | Обработка и трансформация логов |
| Filebeat | - | Сбор логов из Docker контейнеров |

## Запуск

```bash
# Запуск всего стека
docker-compose up -d

# Только ELK (без приложения)
docker-compose up -d elasticsearch kibana logstash filebeat
```

## Доступ

- **Kibana**: http://localhost:5601
- **Elasticsearch**: http://localhost:9200

## Настройка Kibana

1. Откройте http://localhost:5601
2. Перейдите в **Management** → **Stack Management** → **Index Patterns**
3. Создайте index pattern: `bankcards-logs-*`
4. Выберите `@timestamp` как Time field
5. Перейдите в **Discover** для просмотра логов

## Полезные запросы в Kibana

### Все ошибки
```
log_level: "ERROR"
```

### Логи конкретного пользователя
```
user_email: "user@example.com"
```

### Трассировка запроса
```
request_id: "a1b2c3d4"
```

### Медленные запросы
```
executionTimeMs: >1000
```

### Операции с картами
```
logger_name: "CardServiceImpl" AND log_level: "INFO"
```

## Структура логов

```json
{
  "@timestamp": "2025-12-08T15:30:45.123Z",
  "log_level": "INFO",
  "logger_name": "CardServiceImpl",
  "message": "Карта успешно создана",
  "request_id": "a1b2c3d4",
  "user_id": "456",
  "user_email": "user@example.com",
  "cardId": 123,
  "app": "bank-rest",
  "env": "prod"
}
```

## Мониторинг

### Проверка здоровья Elasticsearch
```bash
curl http://localhost:9200/_cluster/health?pretty
```

### Просмотр индексов
```bash
curl http://localhost:9200/_cat/indices?v
```

### Количество документов
```bash
curl http://localhost:9200/bankcards-logs-*/_count
```

## Очистка данных

```bash
# Удалить все индексы логов
curl -X DELETE http://localhost:9200/bankcards-logs-*

# Удалить данные Elasticsearch (полная очистка)
docker-compose down -v
```

## Ресурсы

- Elasticsearch использует 512MB heap
- Logstash использует 256MB heap
- Рекомендуется минимум 4GB RAM для всего стека

