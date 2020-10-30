terraform {
  backend "s3" {
    key = "terraform.tfstate"
    //Config passed from terraform init - Reason: You cant have interpolations in this section of the script
  }
  required_version = "= 0.11.1"
}

provider "aws" {
  version = "~> 1.23"
  region = "${var.region}"
}

provider "archive" {
  version = "~> 1.0"
}

provider "null" {
  version = "~> 1.0"
}

provider "random" {
  version = "~> 1.3"
}

provider "template" {
  version = "~> 1.0"
}

module "classification-filter_service_main" {
  source = "git@github.com:elsevier-health/entellect-infrastructure-parent.git//infrastructure/service"

  #EC2, ELB, ASG Services
  account_number = "${var.account_number}"
  ecr_account_number = "${var.ecr_account_number}"
  account_name = "${var.account_name}"
  region = "${var.region}"
  health_check_url = "counters"
  environment = "${var.environment}"
  image_id = "${var.image_id}"
  sync_spring_profile = "sync"
  async_spring_profile = "async"
  app_function_sync = "classification-filter-sync"
  app_function_async = "classification-filter-async"
  docker_repository = "classification-filter-service"
  cef_entellect_async_instance_type = "t2.small,t2.medium"
  cef_entellect_sync_instance_type = "t2.small,t2.medium"
  cef_entellect_async_cluster_size_min = "${var.classification-filter_service_async_cluster_size_min}"
  cef_entellect_async_cluster_size_max = "${var.classification-filter_service_async_cluster_size_max}"
  cef_entellect_async_cluster_size_desired = "${var.classification-filter_service_async_cluster_size_desired}"
  cef_entellect_sync_cluster_size_min = "${var.classification-filter_service_sync_cluster_size_min}"
  cef_entellect_sync_cluster_size_max = "${var.classification-filter_service_sync_cluster_size_max}"
  cef_entellect_sync_cluster_size_desired = "${var.classification-filter_service_sync_cluster_size_desired}"
  developer_access = false
  git_sha = "${var.git_sha}"
  kube_support = "${var.kube_support}"

  #SQS Services
  service_name = "entellect-classification-filter-service"
  sqs_scale_up_alarm = "${var.classification-filter_scale_up_alarm}"
  sqs_scale_down_alarm = "${var.classification-filter_scale_down_alarm}"
  sqs_actions_enabled = "${var.sqs_actions_enabled}"
  alarm_actions = ""
  create_sqs_alarms = 0
  main_visibility_timeout_seconds = 600
  receive_wait_time_seconds = 20
  scale_down_value = -1
  scale_up_value = 1
}
