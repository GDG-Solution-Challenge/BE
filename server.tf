resource "google_cloud_run_service" "application_server" {
  name     = "${local.common_project_name}-server"
  location = local.region

  template {
    spec {
      containers {
        image = local.server_image

        ports {
          container_port = 8080
        }

        resources {
          limits = {
            memory = "2Gi"
            cpu    = "2"
          }
        }
      }
    }

    metadata {
      annotations = {
        "autoscaling.knative.dev/maxScale"      = "2"
        "run.googleapis.com/cloudsql-instances" = google_sql_database_instance.postgres_instance.connection_name
        "run.googleapis.com/client-name"        = "terraform"
      }
    }
  }

  traffic {
    percent         = 100
    latest_revision = true
  }

  autogenerate_revision_name = true
}

#resource "google_cloud_run_domain_mapping" "api_debate_com" {
#  name     = "api.debate.com"
#  location = google_cloud_run_service.application_server.location
#  service  = google_cloud_run_service.application_server.name
#
#  depends_on = [google_cloud_run_service.application_server]
#}
#
#resource "google_dns_record_set" "api_debate_com" {
#  name         = "api.debate.com."
#  managed_zone = "debate-zone"
#  type         = "CNAME"
#  ttl          = 300
#
#  rrdatas = [
#    google_cloud_run_service.application_server.status[0].url,
#  ]
#}