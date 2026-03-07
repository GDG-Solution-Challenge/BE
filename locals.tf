# locals.tf


locals {
  common_project_name = "mamatolmi" # TODO: Change this to your project name
  common_project_id = "mamatolmi" # TODO: Change this to your project id

  # CIDR blocks
  vpc_cidr_block      = "10.0.0.0/16"
  public_a_cidr_block = "10.0.1.0/24"
  public_c_cidr_block = "10.0.2.0/24"

  server_private_a_cidr_block = "10.0.3.0/24"
  server_private_c_cidr_block = "10.0.4.0/24"

  database_private_a_cidr_block = "10.0.5.0/24"
  database_private_c_cidr_block = "10.0.6.0/24"

  # Base configuration
  region              = "asia-northeast3"
  availability_zone_a = "${local.region}-a"
  availability_zone_c = "${local.region}-c"

  server_image = "us-docker.pkg.dev/cloudrun/container/hello"
  database_name = "mamatolmi-db" # TODO: Change this to your database name
}