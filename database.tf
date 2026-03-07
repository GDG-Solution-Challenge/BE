# Cloud SQL MySQL 인스턴스 생성
resource "google_sql_database_instance" "mysql_instance" {
  name             = "${local.common_project_name}-mysql-db" # 기존 이름과 겹치지 않게 변경
  database_version = "MYSQL_8_0"
  region           = local.region

  settings {
    tier = "db-f1-micro"
    disk_size = 100

    ip_configuration {
      ipv4_enabled    = true

      authorized_networks {
        name  = "all"
        value = "0.0.0.0/0"
      }
    }

    backup_configuration {
      enabled = true
      binary_log_enabled = true # MySQL은 백업 시 이 옵션이 켜져있어야 안전합니다.
    }

    availability_type = "REGIONAL"
  }

  deletion_protection = false
}

# 데이터베이스 생성 (mamatolmi 방 만들기)
resource "google_sql_database" "mysql_db" {
  name     = local.database_name
  instance = google_sql_database_instance.mysql_instance.name
}

# 데이터베이스 사용자 생성 (아이디/비번 설정)
resource "google_sql_user" "mysql_user" {
  name     = "root"
  instance = google_sql_database_instance.mysql_instance.name
  password = "1234"
}