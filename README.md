# SkillSwap - платформа продажи услуг

Сервис где можно предлагать свои услуги. Публикуешь что умеешь, договариваешься о сессиях и оставляешь отзывы. Цены автоматически конвертируются в разные валюты через внешний API

# Стек
бэкенд: Spring Boot 3.x, Spring MVC, Spring Security 6, Spring Data JPA

база: PostgreSQL + миграции на Flyway

кэш: Redis (через Spring Cache)

HTTP-клиент: OkHttp (для дергания API валют)

дока API: springdoc-openapi (Swagger UI)

сборка: Maven

# Как запустить

1. поднимаем базу и Redis

docker-compose up -d

поднимется Postgres на 5432. база: skillswap, пользователь: skillswap, пароль: skillswap и Redis на 6379

2. стартуем приложение

mvn spring-boot:run

3. открываем в браузере
сайт http://localhost:8080
свагер http://localhost:8080/swagger-ui.html

# Переменные окружения

DB_URL=jdbc:postgresql://localhost:5432/skillswap
DB_USER=skillswap
DB_PASS=skillswap
REDIS_HOST=localhost
REDIS_PORT=6379

# Структура проекта

config/        конфиги (Security, Redis, Web, OpenAPI, Thymeleaf)

controller/    MVC-контроллеры (Auth, Offer, Exchange, Profile, Admin)

api/           REST-контроллеры (OfferRest, Currency, Exchange)

service/       бизнес-логика

repository/    репозитории Spring Data JPA

entity/        JPA-сущности (User, Role, Skill, SkillOffer и т.д.)

dto/           DTO

form/          классы для биндинга форм с валидацией

request/       тела запросов для REST API

converter/     конвертеры типов Spring

exception/     кастомные экзэпшены
handler/       глобальный обработчик ошибок
security/      UserDetailsService, rate limiter, хендлеры авторизации
external/      OkHttp-клиент для API валют
dialect/       отрисовка рейтинга

# Тесты API в src/test/http/offers-api.http
