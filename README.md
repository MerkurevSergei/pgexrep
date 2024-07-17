# pgexrep

## Локальный запуск приложения

Запустить инфраструктурные сервисы с помощью docker-compose
```shell
docker-compose -p pgexrep -f .setup/docker-compose-local.yaml up
```

Создать БД и настроить репликацию
```shell
# Копируем скрипт в контейнер
docker cp .setup/pgconfig/configure_postgres.sh postgres-pgexrep:configure_postgres.sh
# Даем права на выполнение скрипта
docker exec postgres-pgexrep chmod +x /configure_postgres.sh
# Выполняем скрипт и удаляем его из контейнера
docker exec postgres-pgexrep /configure_postgres.sh
docker exec postgres-pgexrep rm configure_postgres.sh
# Перезапускаем контейнер
docker restart postgres-pgexrep
```
Запустить приложение с профилем local

Для отображения доступных endpoint запустить приложение и перейти по url:
http://host:port//swagger-ui.html (host - имя хоста, port - порт на котором поднято приложение)


Пример создания слота
SELECT * FROM pg_create_logical_replication_slot('pgexrep_public', 'pgoutput');
Создайте публикацию
CREATE PUBLICATION pgexrep_public FOR ALL TABLES;
CREATE PUBLICATION pgexrep_public FOR TABLES IN SCHEMA public;