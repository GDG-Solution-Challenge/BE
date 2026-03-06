# Cloud SQL PostgreSQL 인스턴스 생성
# cloud-proxy 연결
resource "google_sql_database_instance" "postgres_instance" {
  name             = "${local.common_project_name}-database"
  database_version = "POSTGRES_15"
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
      point_in_time_recovery_enabled = true
    }

    availability_type = "REGIONAL"
  }

  deletion_protection = false
}

# 데이터베이스 생성
resource "google_sql_database" "postgres_db" {
  name     = local.database_name
  instance = google_sql_database_instance.postgres_instance.name
}

# 데이터베이스 사용자 생성
resource "google_sql_user" "postgres_user" {
  name     = "mamatolmi_db" # TODO: 사용자 이름 입력
  instance = google_sql_database_instance.postgres_instance.name
  password = "1234" # TODO: 비밀번호 입력
}