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
`aws ecs describe-services --service arn:aws:ecs:us-east-2:754427858209:service/jarvis-cpen-321-service --cluster jarvis-cpen-321`


# API

## User: `/user/`
All of these commands needs to have the user's google api key, since that's how our server will check if it is actually the user
 - create new user
    `POST /user`
    expects:
    ```json
    {
        "email":"<email",
        "jwt_token":"<token>",
        "username":"<username>"
    }
    ```
    returns:
    ```json
    {
        "response":{
            "id":"<id>"
        }
    }
    ```

 - upload location information
    `POST /user/<id>/location`
    expects: 
    ```json
    {
        "latitude":"string float",
        "longitude":"string float"
    }
    ```

 - add friend
    `POST /user/<id>/friend_request
    expects:
    ```json
    {
        "from":"<user_id>"
    }
    ```
    backend needs to validate from person
    returns 
    ```json
    {
        "response": "pending"
    }
    ```

 - see friends
    `GET /user/<id>/friends`
    returns
    ```json
    {
        "response":[
            "<id>","<id>"...
        ]
    }
    ```

 - see events
    `GET /user/<id>/events`
    returns (maybe shouldn't be as detailed, maybe user id should be returned instead):
    ```json
    {
        "response":[
            {
                "id":"<id>",
                "name":"",
                "start_time":"<unix timestamp>",
                "duration":"<duration in seconds>",
                "status": "pending, active, passed",
                "owner":"<email>",
                "accepted":["<email>"...],
                "invited": ["<email>", "<email>", ...],
                "chat_url":"<url>"
            },
            ...
        ]
    }

 - user information
    `GET /user/<id>` OR `GET /user/<id>/schedule`
    ```json
    {
        "response":{
            "id":"<id>",
            "username":"<name>",
            "schedule":[
                {
                    (a bunch of events)
                }
            ]
        }
    }
    ```
    returns generic user information

 - logout (invalidate token)
    `PUT /user/<id>/logout`
    ```json
    {
        "response": "success"
    }
    ```

## Chat
 - send message
 - receive message
 - chat information

## Event
 - create new event
 - access information (people going, location, chat urls)
 - upload location information for event
 - request other user's location information
 - add people
 - remove people
