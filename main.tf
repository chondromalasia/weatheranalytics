terraform {
  required_providers {
    confluent = {
      source  = "confluentinc/confluent"
      version = "1.55.0"
    }
  }
}

provider "aws" {
  region = "eu-central-1"
}

provider "confluent" {
}

resource "aws_instance" "vps" {
  ami           = "ami-07c27171ff332e277"
  instance_type = "t4g.micro"

  tags = {
    Name = "weather-vps"
  }

  vpc_security_group_ids = [aws_security_group.ingress-ec2.id]

  key_name = "weather-api-connect"
}

resource "aws_s3_bucket" "terraform_state" {
  bucket = "weather-state"

  lifecycle {
    prevent_destroy = true
  }
}

resource "aws_s3_bucket_versioning" "enabled" {
  bucket = aws_s3_bucket.terraform_state.id
  versioning_configuration {
    status = "Enabled"
  }
}

resource "aws_s3_bucket_server_side_encryption_configuration" "default" {
  bucket = aws_s3_bucket.terraform_state.id

  rule {
    apply_server_side_encryption_by_default {
      sse_algorithm = "AES256"
    }
  }
}

resource "aws_s3_bucket_public_access_block" "public_access" {
  bucket                  = aws_s3_bucket.terraform_state.id
  block_public_acls       = true
  block_public_policy     = true
  ignore_public_acls      = true
  restrict_public_buckets = true
}

resource "aws_dynamodb_table" "terraform_locks" {
  name         = "weather_locks"
  billing_mode = "PAY_PER_REQUEST"
  hash_key     = "LockID"
  attribute {
    name = "LockID"
    type = "S"
  }
}

terraform {
  backend "s3" {
    bucket = "weather-state"
    key    = "global/s3/terraform.tfstate"
    region = "eu-central-1"

    dynamodb_table = "weather_locks"
    encrypt        = true
  }
}

resource "aws_security_group" "ingress-ec2" {
  name = "ssh-rules"

  ingress {
    from_port   = 22
    to_port     = 22
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
}

############
# CONFLUENT
############

resource "confluent_environment" "weather_analytics" {
  display_name = "weather_analytics"
}

resource "confluent_kafka_cluster" "weather_forecasts" {
  display_name = "weather_forecasts"
  availability = "SINGLE_ZONE"
  cloud        = "AWS"
  region       = "eu-central-1"
  basic {}
  environment {
    id = confluent_environment.weather_analytics.id
  }
}

resource "confluent_service_account" "admin" {
  display_name = "admin"
  description  = "Cluster management service account"
}

resource "confluent_role_binding" "admin-kafka-cluster-admin" {
  principal   = "User:${confluent_service_account.admin.id}"
  role_name   = "CloudClusterAdmin"
  crn_pattern = confluent_kafka_cluster.weather_forecasts.rbac_crn
}

resource "confluent_api_key" "admin-kafka-api-key" {
  display_name = "admin-kafka-api-key"
  description  = "Kafka API Key thta is owned by 'admin' service account"
  owner {
    id          = confluent_service_account.admin.id
    api_version = confluent_service_account.admin.api_version
    kind        = confluent_service_account.admin.kind
  }

  managed_resource {
    id          = confluent_kafka_cluster.weather_forecasts.id
    api_version = confluent_kafka_cluster.weather_forecasts.api_version
    kind        = confluent_kafka_cluster.weather_forecasts.kind

    environment {
      id = confluent_environment.weather_analytics.id
    }
  }

  depends_on = [
    confluent_role_binding.admin-kafka-cluster-admin
  ]
}

resource "confluent_kafka_topic" "forecasts" {
  kafka_cluster {
    id = confluent_kafka_cluster.weather_forecasts.id
  }
  topic_name    = "forecasts"
  rest_endpoint = confluent_kafka_cluster.weather_forecasts.rest_endpoint
  credentials {
    key    = confluent_api_key.admin-kafka-api-key.id
    secret = confluent_api_key.admin-kafka-api-key.secret
  }

  config = {
    "retention.ms" = "3888000000"
  }
}

resource "confluent_kafka_acl" "app-producer-write-on-topic" {
  kafka_cluster {
    id = confluent_kafka_cluster.weather_forecasts.id
  }
  resource_type = "TOPIC"
  resource_name = confluent_kafka_topic.forecasts.topic_name
  pattern_type  = "LITERAL"
  principal     = "User:${confluent_service_account.app-producer.id}"
  host          = "*"
  operation     = "WRITE"
  permission    = "ALLOW"
  rest_endpoint = confluent_kafka_cluster.weather_forecasts.rest_endpoint
  credentials {
    key    = confluent_api_key.admin-kafka-api-key.id
    secret = confluent_api_key.admin-kafka-api-key.secret
  }
}

resource "confluent_service_account" "app-producer" {
  display_name = "app-producer"
  description  = "Service account to produce to topics"
}


resource "confluent_api_key" "app-producer-kafka-api-key" {
  display_name = "app-producer-kafka-api-key"
  description  = "Kafka API Key that is owned by 'app-producer' service account"
  owner {
    id          = confluent_service_account.app-producer.id
    api_version = confluent_service_account.app-producer.api_version
    kind        = confluent_service_account.app-producer.kind
  }

  managed_resource {
    id          = confluent_kafka_cluster.weather_forecasts.id
    api_version = confluent_kafka_cluster.weather_forecasts.api_version
    kind        = confluent_kafka_cluster.weather_forecasts.kind

    environment {
      id = confluent_environment.weather_analytics.id
    }
  }
}

