resource "google_artifact_registry_repository" "docker_repo" {
  repository_id = local.common_project_name
  format   = "DOCKER"
  location = local.region
  description = "Docker repository for storing container images"
}