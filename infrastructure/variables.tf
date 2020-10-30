variable "environment" {
  description = "The Application environment (dev, uat, sit, prod, etc)"
}

variable "image_id" {
  description = "The image digest of the text enrichment app image in ECS"
  default = ""
}

variable "git_sha" {
  description = "SHA of the terraform scripts used to create the environment"
}

variable "classification-filter_service_async_cluster_size_min" {
  default = 1
}

variable "classification-filter_service_async_cluster_size_max" {
  default = 10
}

variable "classification-filter_service_async_cluster_size_desired" {
  default = 1
}

variable "classification-filter_service_sync_cluster_size_min" {
  default = 1
}

variable "classification-filter_service_sync_cluster_size_max" {
  default = 10
}

variable "classification-filter_service_sync_cluster_size_desired" {
  default = 1
}

variable "region" {
  default = "eu-west-1"
}

variable "account_number" {
}
variable "ecr_account_number" {
  default = "652291809580"
}
variable "account_name" {}
variable "kube_support" {}

#--------------------------------------------------------------
# SQSAS Variables
#--------------------------------------------------------------

variable "classification-filter_scale_up_alarm" {
  description = "Scale up alarm value for classification-filter queue"
  default = 1000
}

variable "classification-filter_scale_down_alarm" {
  description = "Scale down alarm value for classification-filter queue"
  default = 500
}

variable "sqs_actions_enabled" {
  description = "Enable alarm actions"
  default = "true"
}