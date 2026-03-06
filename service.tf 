resource "google_project_service" "cloud_sql" {
  project = local.common_project_id
  service = "sqladmin.googleapis.com"
  disable_dependent_services = false
}

resource "google_project_service" "compute_engine" {
  project = local.common_project_id
  service = "compute.googleapis.com"
  disable_dependent_services = false
}

resource "google_project_service" "cloud_run" {
  project = local.common_project_id
  service = "run.googleapis.com"
  disable_dependent_services = false
}

resource "google_project_service" "artifact_registry" {
  project = local.common_project_id
  service = "artifactregistry.googleapis.com"
  disable_dependent_services = false
}