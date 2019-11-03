#!/bin/bash

#constants
docker_image="754427858209.dkr.ecr.us-east-2.amazonaws.com/jarvis-cpen-321:latest"
invalidresponse="Your Authorization Token has expired"

docker="$(sudo docker build -t $docker_image .)"
echo "$docker"

dockercommand="$(aws ecr get-login --no-include-email)"
eval "sudo $dockercommand"

sudo docker push $docker_image

#stop currently running task
regex1="^.*arn:aws:ecs:us-east-2:754427858209:task\\/([A-Za-z0-9-]+).*$"
taskjson="$(aws ecs list-tasks --cluster jarvis-cpen-321)"

[[ $taskjson =~ $regex1 ]]
taskid="${BASH_REMATCH[1]}"

eval "aws ecs stop-task --cluster jarvis-cpen-321 --task $taskid"

#start up a new task, this time picking up the new image
aws ecs update-service --force-new-deployment --service arn:aws:ecs:us-east-2:754427858209:service/jarvis-cpen-321-service --cluster jarvis-cpen-321


