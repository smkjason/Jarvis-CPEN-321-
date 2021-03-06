[![Codacy Badge](https://api.codacy.com/project/badge/Grade/70e28116f3904e65bdac3098c8683bc8)](https://www.codacy.com?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=smkjason/Jarvis-CPEN-321-&amp;utm_campaign=Badge_Grade)
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

#### Docker
Build Image:
- `sudo docker build -t 754427858209.dkr.ecr.us-east-2.amazonaws.com/jarvis-cpen-321:latest .`

List and Run:
- `sudo docker images`
- `docker run -p 80:3000 <image id>`

List and kill running images:
- `sudo docker container ls`
- `sudo docker kill <container id>`

Useful: https://docs.aws.amazon.com/cli/latest/reference/ecs/update-service.html

Run:
- `aws ecr get-login --no-include-email`
to get login

- `sudo docker push 754427858209.dkr.ecr.us-east-2.amazonaws.com/jarvis-cpen-321`
somehow find the task id (can do thru web ui, but preferably thru aws-cli)

- stop running task
`aws ecs stop-task --cluster jarvis-cpen-321 --task <taskid>`

- start new task
`aws ecs update-service --force-new-deployment --service arn:aws:ecs:us-east-2:754427858209:service/jarvis-cpen-321-service --cluster jarvis-cpen-321`


useful to describe service:
- `aws ecs describe-services --service arn:aws:ecs:us-east-2:754427858209:service/jarvis-cpen-321-service --cluster jarvis-cpen-321`
- `aws ecs list-tasks --cluster jarvis-cpen-321`


### Connect to Server
`ssh ec2-user@3.14.144.180`

if that does not work, contact Charles, with your ssh public key and he will add you

`docker container list`

this should print out two containers:
- `754427858209.dkr.ecr.us-east-2.amazonaws.com/jarvis-cpen-321` is our server image
- `amazon/amazon-ecs-agent:latest` is the gateway container based on the configs

to see logs, run `docker logs <container id>`, where `container id` is the id of the container

`exit` to close ssh connection


# API

## User: `/user/`
All of these commands needs to have the user's google api key, since that's how our server will check if it is actually the user
 - create new user
 - add friend
 - see friends

 - see admin events
    `GET /user/<id>/admin`
    returns (maybe shouldn't be as detailed, maybe user id should be returned instead):
    ```json
    {
        "events": [
            {
                "invitees": [
                    "jarviscpen321@gmail.com"
                ],
                "responses": [
                    {
                        "timeslots": [
                            {
                                "startTime": "11-30, 2019 04:49",
                                "endTime": "11-09, 2019 04:49"
                            }
                        ],
                        "email": "jarviscpen321@gmail.com",
                        "declined": null
                    }
                ],
                "_id": "5dd9dd91b49ea1179df8e795",
                "name": "Test 8",
                "deadline": "2019-10-26",
                "length": "2:0",
                "creatorEmail": "jarviscpen321.1@gmail.com",
                "id": "36db52100e5a11eabf5a49b660b53dd6",
                "__v": 1
            }
        ]
    }
    ```

 - user information

 - logout (invalidate token)

## Chat
 - send message
done through socket
 - receive message
done through socket
 - chat information
 -retrieve old chats:
http://localhost:3000/events/<id>/messages?before=<unix timestamp>


## Event
```json
{
    "start_time":"0:00",
    "end_time":"23:00",
    "description":"hello this is a description",
    "repeat_days": [0,3,5],
    "repeat_until": 1290801901,
    "name": "name of event",
    "single_event": true,
    "weight": 3,
    "owner": "emailofuser@gmail.com",
    "google": false
}
```
start_time, end_time can be either strings representating a clock time as shown above, or as a unix timestamp if single_event = true.
repeat_days and repeat_until represents time we want to repeat the event for, and which days of the week the events are repeating
weight is used for when the user enters in free slots, and our app will calculate when they are free

 - create new event
 - access information (people going, location, chat urls)
 - upload location information for event
 - request other user's location information
 - add people
 - remove people

## Event Creation

 - front end can retrieve all the events of a user here:
 `GET user/:email/events`

 returns:
 ```json
{
    "events":[
        {
            //event one
        },
        {
            //event two
        }
    ]
}
 ```

 - admin inputs event creation constraints
 `POST user/:email/events/`
 ```json
{
    "deadline": "YYYY-MM-DD",
    "length": "hh:mm",
    "invitees":["array of emails"],
    "name": "name" 
}
 ```
deadline is inclusive

return:
```json
{
    "id":"tentative uuid",
    "status":"success",
    "error":"some message"
}
```

 - backend provides an endpoint to view all invites
 `GET user/:email/invites`
 
 return:
 ```json
 {
     "events":[
         {
             "id": "uuid",
             "name": "event name"
             (all other information related to an event)
         }
     ]
 }
 ```

 - front end receives notification, calls backend to display contraints

 - user picks list of preferred times, sends to backend

 `PUT user/:email/events/:id(?declined=true)`
 ```json
 {
     "timeslots":[
         {
             "startTime":"YYYY-MM-DD hh:mm",
             "endTime":"YYYY-MM-DD hh:mm"
         },
         {
             "startTime":"YYYY-MM-DD hh:mm",
             "endTime":"YYYY-MM-DD hh:mm"
         }
         ...
     ]
 }
 ```
 return:
```json
{
    "status":"success",
    "error":"some message"
}
```

 - admin can view the status of the event

 `GET events/:id/` will return
  ```json
{
    "deadline": "YYYY-MM-DD",
    "length": "hh:mm",
    "invitees":["array of emails"],
    "name": "name",
    "responses":[
        {
            "email":"email",
            "timeslots":[...],
            "declined": true
        }
        ...
    ]
}
 ```
 - admin can look at backend - suggested timeslots

 `GET events/:id/preferred`
 Return:
 ```json
 {
    "timeslots":[
         {
             "startTime":"YYYY-MM-DD hh:mm",
             "endTime":"YYYY-MM-DD hh:mm"
         },
         {
             "startTime":"YYYY-MM-DD hh:mm",
             "endTime":"YYYY-MM-DD hh:mm"
         }
         ...
     ]
 }
 ```

 - admin can also create the event at any time

 `POST events/:id/activate`
 ```json
 {
    "startTime":"YYYY-MM-DD hh:mm",
    "endTime":"YYYY-MM-DD hh:mm"
 }
 ```
 returns:
 ```json
 {
    "creatorEmail":"email",
    "id":"uuid",
    (other stuff that the event schema has)
 }
 ```
- backend will create the event for every person, chat will be created for the event

Push notification to everyone that accepted the event

## Event Location

- front-end periodically updates location
`PUT user/:email/location`
```json
{
    "lat":"number",
    "lon":"number"
}
```
returns:
```json
{
    "success":true
}
```

`GET events/:id/locations`
```json
{
    "locations":[
        {
            "user":"email",
            "lat":"number",
            "lon":"number"
        }
    ]
}




***We can use refresh tokens to obtain more information