# Amazon AWS Summary

- EC2 -> server instances that hosts our container
- ECR -> repository where the docker images are pushed to
- ECS -> service that runs the docker containers
    - Clusters -> the EC2 server
        - where containers are run
    - Tasks -> definition to proxy the container
        - defines container image
        - volumes
        - network env variables
        - port mappings
    - Services -> runs a cluster (?)

### To pick up a new deploy
Assuming docker is set up with AWS (`aws ecr get-login --no-include-email --region us-east-2`)

Useful: https://docs.aws.amazon.com/cli/latest/reference/ecs/update-service.html

Run:
- `sudo docker push 754427858209.dkr.ecr.us-east-2.amazonaws.com/jarvis-cpen-321`
somehow find the task id (can do thru web ui, but preferably thru aws-cli)

- stop running task
`aws ecs stop-task --cluster jarvis-cpen-321 --task <taskid>`

- start new task
`aws ecs update-service --force-new-deployment --service arn:aws:ecs:us-east-2:754427858209:service/jarvis-cpen-321-service --cluster jarvis-cpen-321`


useful to describe service:
` aws ecs describe-services --service arn:aws:ecs:us-east-2:754427858209:service/jarvis-cpen-321-service --cluster jarvis-cpen-321`
