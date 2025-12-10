# Monitoring Stack для Bank REST API

## Компоненты

| Сервис     | Порт | Описание                |
|------------|------|-------------------------|
| Prometheus | 9090 | Сбор и хранение метрик  |
| Grafana    | 3000 | Визуализация и дашборды |

## Запуск

```bash
# Запуск мониторинга вместе с основными сервисами
docker-compose --env-file credentials-dev.env up -d prometheus grafana

# Или всё вместе (включая ELK)
docker-compose --env-file credentials-dev.env up -d
```

## Доступ

- **Grafana**: http://localhost:3000
    - Логин: `admin`
    - Пароль: `admin` (или из переменной `GRAFANA_ADMIN_PASSWORD`)
- **Prometheus**: http://localhost:9090

## Настройка Grafana

При первом запуске Grafana автоматически настраивается:

1. **Data Sources** (автоматически):
    - Prometheus — метрики приложения
    - Elasticsearch — логи
    - PostgreSQL — данные БД

2. **Dashboards** (автоматически):
    - `Bank Cards - Application Metrics` — основной дашборд

### Готовые панели дашборда:

- CPU Usage (gauge)
- JVM Heap Usage (gauge)
- Active HTTP Sessions
- Uptime
- HTTP Requests Rate (по эндпоинтам)
- HTTP Response Time
- JVM Memory (heap/non-heap)
- JVM Threads
- HTTP Errors (4xx, 5xx)
- HikariCP Connections (active/idle/pending)
- GC Pauses

## Prometheus Endpoints

Spring Boot Actuator предоставляет метрики:

```bash
# Health check
curl http://localhost:8185/actuator/health

# All metrics
curl http://localhost:8185/actuator/metrics

# Prometheus format
curl http://localhost:8185/actuator/prometheus

# Specific metric
curl http://localhost:8185/actuator/metrics/jvm.memory.used
```

## Полезные PromQL запросы

```promql
# CPU usage
system_cpu_usage * 100

# Memory usage %
jvm_memory_used_bytes{area="heap"} / jvm_memory_max_bytes{area="heap"} * 100

# Request rate per endpoint
rate(http_server_requests_seconds_count[1m])

# Average response time
rate(http_server_requests_seconds_sum[1m]) / rate(http_server_requests_seconds_count[1m])

# Error rate (5xx)
sum(rate(http_server_requests_seconds_count{status=~"5.."}[5m]))

# Active DB connections
hikaricp_connections_active

# GC pause time
rate(jvm_gc_pause_seconds_sum[1m])
```

## Алерты (опционально)

Примеры правил для Alertmanager:

```yaml
groups:
  - name: bankcards
    rules:
      - alert: HighErrorRate
        expr: sum(rate(http_server_requests_seconds_count{status=~"5.."}[5m])) > 0.1
        for: 5m
        labels:
          severity: critical
        annotations:
          summary: "High error rate detected"

      - alert: HighMemoryUsage
        expr: jvm_memory_used_bytes{area="heap"} / jvm_memory_max_bytes{area="heap"} > 0.9
        for: 5m
        labels:
          severity: warning
        annotations:
          summary: "JVM heap usage > 90%"
```

## Импорт готовых дашбордов

Grafana Labs предоставляет готовые дашборды:

- **JVM (Micrometer)**: ID `4701`
- **Spring Boot Statistics**: ID `6756`
- **HikariCP**: ID `6083`

Импорт: Grafana → Dashboards → Import → Введите ID

## Переменные окружения

```env
GRAFANA_ADMIN_USER=admin
GRAFANA_ADMIN_PASSWORD=admin
```

## Структура файлов

```
monitoring/
├── prometheus/
│   └── prometheus.yml          # Конфигурация Prometheus
└── grafana/
    ├── provisioning/
    │   ├── datasources/
    │   │   └── datasources.yml # Источники данных
    │   └── dashboards/
    │       └── dashboards.yml  # Провайдер дашбордов
    └── dashboards/
        └── bankcards-app.json  # Дашборд приложения
```

