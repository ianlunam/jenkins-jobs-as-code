#!/usr/bin/env bash

# ParameterNames: List of valid parameters for template. Example:
# export ParameterNames="""AlertLogicHost AlertLogicKey AmiId CreateCrons CreateScaling
#         DesiredCapacity DockerVolumeSize InstanceType MaxSize MinSize
#         NewRelicLicenseKey RootDomain RootVolumeSize SshKeyName SslCertificateId"""


# Optional variables
: ${AWS_DEFAULT_REGION:=eu-west-1}
: ${ParameterNames:=""}

# Mandatory variables
: ${StackName:?Variable Not Set. StackName is Mandatory} || exit 1
: ${S3Bucket:?Variable Not Set. S3Bucket is Mandatory} || exit 1
: ${TemplateFile:?Variable Not Set. TemplateFile is Mandatory} || exit 1

# Convert any supplied parameters into Key/Value blocks
cfn_params=""
for pname in ${ParameterNames}; do
    if [[ ! -z "$(eval echo \$$pname)" ]]; then
        cfn_params="$cfn_params ParameterKey=$pname,ParameterValue='$(eval echo \$$pname)'"
    fi
done

# If there are parameters, use --parameters parameters
parameter_command="--parameters"
if [[ -z "${cfn_params}" ]]; then
    parameter_command=""
fi

# Upload the cfn template json file to s3
cfn_file="${BUILD_TAG}.json"
aws s3 mb s3://${S3Bucket} 2>/dev/null || true
aws s3 cp ${TemplateFile} s3://${S3Bucket}/${cfn_file} || exit 1
S3Template=https://s3-eu-west-1.amazonaws.com/${S3Bucket}/${cfn_file}

# Determine if we are going to create or update
create_command="create-stack"
aws cloudformation describe-stacks --stack-name ${StackName} 1>/dev/null 2>&1
if [[ $? == 0 ]]; then
    create_command="update-stack"
fi

# Execute command to create/update stack
echo "Running ${create_command} for stack ${StackName}"
echo "Template Parameters :${cfn_params}"
StackId=$(aws cloudformation ${create_command} --stack-name ${StackName} --capabilities CAPABILITY_IAM \
        ${parameter_command} ${cfn_params} --template-url ${S3Template} \
        --query StackId --output text)

if [[ -z "${StackId}" ]]; then
    # Failure.
    echo "Failed. See above."
    exit 3
else
    # Wait for create / update to complete
    while true; do
        stack_status=$(aws cloudformation describe-stacks --stack-name ${StackName} \
                            --query 'Stacks[*].StackStatus' --output text)
        if [[ "${stack_status}" =~ "_COMPLETE" ]]
        then
            # It's complete. Describe and exit
            aws cloudformation describe-stacks --stack-name ${StackName}
            exit 0
        else
            if [[ "${stack_status}" =~ "_IN_PROGRESS" ]]
            then
                # Still waiting. Sleep and check again
                echo "waiting ..."
                sleep 10
                continue
            else
                # Failure. Print state and exit
                echo "Failed. Status is ${stack_status}. See above"
                exit 2
            fi
        fi
    done
fi

# Should never get to here
exit 0
