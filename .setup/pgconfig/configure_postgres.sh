#!/bin/bash

# Добавление настройки
add_config() {
  local config="$1"
  local file="$2"
  echo "$config" >> "$file"
}

# Функция для удаления строки из файла
remove_config() {
  local config="$1"
  local file="$2"
  sed -i "/^$config/d" "$file"
}

# ================================= #
# === Настройка postgresql.conf === #
# ================================= #

PG_CONF="/var/lib/postgresql/data/postgresql.conf"

# Удаляем текущие настройки репликации
remove_config "wal_level" $PG_CONF
remove_config "max_replication_slots" $PG_CONF
remove_config "max_wal_senders" $PG_CONF

add_config "wal_level = logical" $PG_CONF
add_config "max_replication_slots = 4" $PG_CONF
add_config "max_wal_senders = 4" $PG_CONF

# ================================= #
# ===== Настройка pg_hba.conf ===== #
# ================================= #

PG_HBA="/var/lib/postgresql/data/pg_hba.conf"

# Удаляем текущие настройки
remove_config "host replication" $PG_HBA

# Добавляем правило в pg_hba.conf
add_config "host replication postgres 127.0.0.1/32 md5" $PG_HBA

# ================================= #
# ===== Создание базы данных ====== #
# ================================= #

# Создаем базу данных, если она еще не существует
DB_NAME="pgexrep"
DB_USER="postgres"
DB_PASS="postgres"

db_exists() {
  local db_name="$1"
  local result=$(psql -U postgres -tAc "SELECT 1 FROM pg_database WHERE datname='$db_name'")
  if [[ "$result" = "1" ]]; then
    return 0  # База данных существует
  else
    return 1  # База данных не существует
  fi
}

if db_exists $DB_NAME; then
  echo "База данных уже существует: $DB_NAME"
else
  echo "Создаем базу данных: $DB_NAME"
  # Создаем базу данных, если она еще не существует
  create_db() {
    local db_name="$1"
    local db_user="$2"
    local db_pass="$3"
    psql -U postgres -c "CREATE DATABASE $db_name"
    psql -U postgres -c "CREATE USER $db_user WITH ENCRYPTED PASSWORD '$db_pass'"
    psql -U postgres -c "GRANT ALL PRIVILEGES ON DATABASE $db_name TO $db_user"
  }

  create_db $DB_NAME $DB_USER $DB_PASS
fi