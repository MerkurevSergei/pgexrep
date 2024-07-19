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


SELECT * FROM pg_create_logical_replication_slot('pgexrep_public', 'pgoutput');
CREATE PUBLICATION pgexrep_public FOR TABLES IN SCHEMA public;
DROP publication pgexrep_public;

SELECT pg_terminate_backend(39);
SELECT pg_drop_replication_slot('pgexrep_public');

SELECT pid, backend_start, state, query
FROM pg_stat_activity;

SELECT * FROM pg_replication_slots WHERE slot_name = 'pgexrep_public';

SELECT * from pg_drop_replication_slot('pgexrep_public');
SELECT * FROM pg_logical_slot_get_changes('pgexrep_public', NULL, NULL);
SELECT * FROM pg_logical_slot_get_binary_changes('pgexrep_public', NULL, NULL, 'proto_version', '1', 'publication_names', 'pgexrep_public');

select * from pg_stat_replication;

DROP SUBSCRIPTION pgexrep_public;







SELECT pg_create_logical_replication_slot('pgexrep_public', 'pgoutput');
SELECT pg_drop_replication_slot ('pgexrep_public')
CREATE PUBLICATION pgexrep_public FOR all TABLES;

SELECT * FROM pg_replication_slots;
select * from pg_stat_replication;
SELECT * FROM pg_stat_activity;

SELECT * FROM pg_subscription;
select * from pg_stat_subscription;
drop subscription pgexrep_public;


SELECT * FROM pg_publication;
DROP PUBLICATION pgexrep_public;