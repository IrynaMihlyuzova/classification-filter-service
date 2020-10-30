#!/usr/bin/env bash
set -e

interactiveMode="interactive"
nonInteractiveMode="non-interactive"
mode="$interactiveMode"

callingArguments="$@"
firstArgument="$1"
echo "calling arguments are $callingArguments"
while [[ ${1} ]]; do
	case "${1}" in
		--action)
			action=${2}
			mode=${nonInteractiveMode}
			shift
			;;
		--environment)
			environment=${2}
			mode=${nonInteractiveMode}
			shift
			;;
		--tfvars)
			tfvars=${2}
			mode=${nonInteractiveMode}
			shift
			;;
		*)
			;;
	esac
	if ! shift; then
		echo 'Missing parameter argument.' >&2
		exit 1
	fi
done

validate_action(){
	if [ -z ${action+x} ] || ! [[ "$action" =~ ^(plan|apply|destroy)$ ]]; then
		echo "action '$action' is not supported, valid actions are [plan|apply|destroy]";
		exit 1
	fi
}

validate_environment(){
	if [ -z ${environment+x} ] || ! [[ "${environment}" =~ ^[0-9a-zA-Z\_\.-]+$ ]]; then
		echo "environment value '$environment' is not allowed, environments must only contain [0-9a-zA-Z_.-]";
		exit 1
	fi
}

validate_image_id(){
	check_exists=$(aws ecr describe-images --region eu-west-1 --registry-id 652291809580 --repository-name  entellect/classification-filter-service  --query 'imageDetails[*].imageTags[?contains(@, `'$TF_VAR_image_id'`) == `true`]' --output text)
	if [[ -z $check_exists ]] && [[ $TF_VAR_image_id != 'latest' ]]; then
		echo "Image ID does not exist in entellect/${serviceNamePrefix}-service. Exiting..."
		exit 1
	fi
}

validate_kubenetes_support() {
	if [[ $TF_VAR_kube_support != "true" ]] && [[ $TF_VAR_kube_support != "false" ]]; then
		echo "incorrect input. You must use 'true' or 'false'. Exiting ..."
		exit 1
	fi
}

setup_profile() {
	PROFILE_PATH=~/.cef_terraform_profile_entellect
	if [ -f $PROFILE_PATH ]; then
		. $PROFILE_PATH
	else
		touch $PROFILE_PATH
	fi
}

prompt_user_for_variable() {
	promptValue=$1
	varName=$2
	varValue=${!varName}
	hideOutput=$3
	if [ -z "$varValue" ]; then
		read $hideOutput -p "$promptValue : " newValue
	if [ -z "$hideOutput" ]; then
		echo "export $varName=$newValue" >> $PROFILE_PATH
	fi
		. $PROFILE_PATH
	else
		read -p "$promptValue (Default: $varValue): " newValue
		if [ ! -z "$newValue" ]; then
			echo "$varName changed to $newValue"
			sed -ie "s/$varName=.*/$varName=$newValue/g" $PROFILE_PATH
		fi
	fi
	. $PROFILE_PATH
}

read_git_sha() {
	export TF_VAR_git_sha=$(git rev-parse HEAD)
}

select_workspace() {
	# Select workspace or create new one.
	# Avoid selecting or creating if workspace already selected.
	if [ -e .terraform/environment ]; then
		# If switching to new workspace, check if it exists and if not then create it.
		local selected_workspace=$(cat .terraform/environment)
		if [ "${selected_workspace}" != "${TF_VAR_environment}" ]; then
			select_or_create_new_workspace
		fi
	else
		# Nothing setup locally, initialize from remote or create new
		select_or_create_new_workspace
	fi
}

select_or_create_new_workspace() {
	# If select fails then create a new workspace
	if ! (terraform workspace select ${TF_VAR_environment} 2>/dev/null); then
		terraform workspace new ${TF_VAR_environment}
	fi
}

init_terraform() {
	SERVICE="classification-filter-service"
	if [ "${TF_VAR_account_number}" = "511696228681" ] || [ "${TF_VAR_account_number}" = "290244732740" ]; then
		BUCKET="elsevier-tio-aws-h-entellect-${TF_VAR_account_name}${TF_VAR_account_number}"
		KEY="tfstate/aws-h-entellect-${TF_VAR_account_name}-enrichment-services/${SERVICE}-terraform.tfstate"
		BUCKET_REGION="us-east-1"
	else
		BUCKET="elsevier-cef-${TF_VAR_account_number}"
		KEY="entellect/${SERVICE}-terraform.tfstate"
		BUCKET_REGION="${TF_VAR_region}"
	fi;
	terraform init -force-copy -backend-config="bucket=${BUCKET}" -backend-config="key=${KEY}" -backend-config="region=${BUCKET_REGION}" -input=false
}

init_aws_region() {
	if [[ "${TF_VAR_environment}" = "enrichment-services-prod-"* ]]; then
		export TF_VAR_region="us-east-2"
	elif [[ "${TF_VAR_environment}" = "enrichment-services-np-"* ]]; then
		export TF_VAR_region="eu-west-1"
	else
		export TF_VAR_region="eu-west-1"
	fi
	export AWS_DEFAULT_REGION="${TF_VAR_region}"
}

init_aws_account() {
	if [[ "${TF_VAR_environment}" = "enrichment-services-prod-"* ]]; then
		export TF_VAR_account_name="prod"
		export TF_VAR_account_number="511696228681"
	elif [[ "${TF_VAR_environment}" = "enrichment-services-np-"* ]]; then
		export TF_VAR_account_name="nonprod"
		export TF_VAR_account_number="290244732740"
	else
		export TF_VAR_account_name="nonprod"
		export TF_VAR_account_number="652291809580"
	fi
}

setup_terraform_arguments() {
	if [ ${mode} == ${interactiveMode} ] ; then
		terraformArguments=${callingArguments}
	else
		terraformArguments="${action}"
		if [ ${action} == "destroy" ]; then
			terraformArguments="${terraformArguments} -force"
		fi
	if [ ${action} == "apply" ]; then
			terraformArguments="${terraformArguments} -auto-approve"
		fi
	fi
}

read_git_sha

if [ ${mode} == ${nonInteractiveMode} ] ; then
	echo "Non-interactive mode detected, validate calling arguments"
	validate_action
	validate_environment
	validate_image_id

	export TF_VAR_environment="${environment}"
else
	echo "Interactive mode detected. Prompting..."
	action="${firstArgument}"
	setup_profile
	prompt_user_for_variable "Which Environment?" TF_VAR_environment
	prompt_user_for_variable "Which Image ID? " TF_VAR_image_id
	prompt_user_for_variable "with kubernetes? (true/false) " TF_VAR_kube_support
fi

validate_image_id
validate_kubenetes_support
init_aws_region
init_aws_account
init_terraform
select_workspace
init_terraform

setup_terraform_arguments

terraform ${terraformArguments} ${ARGS}