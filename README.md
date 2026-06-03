#SkillSwap - платформа продажи услуг

Сервис где можно предлагать свои услуги. Публикуешь что умеешь, договариваешься о сессиях и оставляешь отзывы. Цены автоматически конвертируются в разные валюты через внешний API

#Стек
бэкенд: Spring Boot 3.x, Spring MVC, Spring Security 6, Spring Data JPA
база: PostgreSQL + миграции на Flyway
кэш: Redis (через Spring Cache)
HTTP-клиент: OkHttp (для дергания API валют)
дока API: springdoc-openapi (Swagger UI)
сборка: Maven

#Как запустить

1. поднимаем базу и Redis

docker-compose up -d

поднимется Postgres на 5432. база: skillswap, пользователь: skillswap, пароль: skillswap и Redis на 6379

2. стартуем приложение

mvn spring-boot:run

3. открываем в браузере
сайт http://localhost:8080
свагер http://localhost:8080/swagger-ui.html

#Тестовые аккаунты

| логин | пароль | роль |
| admin | admin123 | ADMIN + USER |
| johndoe | user123 | USER |

#Переменные окружения

| Переменная | Дефолт | Описание |
| DB_URL | jdbc:postgresql://localhost:5432/skillswap | URL базы |
| DB_USER | skillswap | Юзер базы |
| DB_PASS | skillswap | Пароль базы |
| REDIS_HOST | localhost | Хост Redis |
| REDIS_PORT | 6379 | Порт Redis |

#Структура проекта

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

#Тесты API в src/test/http/offers-api.http

#Фичи
регистрация и авторизация с ролями (USER, ADMIN)
полный CRUD для объявлений (скиллов)
процесс обмена: заявка → принятие/отклонение → обмен → отзыв
живой поиск объявлений через AJAX (с debounce)
конвертация валют через внешний API (ExchangeRate API + кэш в Redis)
свой диалект Thymeleaf для вывода рейтинга звездочками
админка для управления юзерами
безопасность: защита от CSRF и XSS, rate limiting, защита от session fixation