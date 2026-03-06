# provider.tf

terraform {
  required_providers {
    google = {
      source  = "hashicorp/google"
      version = "4.51.0"
    }
  }
}

# gcp console에서 gcloud auth application-default login
provider "google" {
  project = "mamatolmi" # TODO: change to your project id
  region  = local.region
  credentials = "./application_default_credentials.json"
}