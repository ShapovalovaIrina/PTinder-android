#!/bin/bash

docker swarm init
docker stack deploy -c docker/docker-compose.yml app
echo "Wait 30 seconds for container to start up"
sleep 30
docker stack ps app
#curl localhost:8080/ptinder/pets/types