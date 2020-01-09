## Minikube
instead of pushing yout Docker image to a registry, you can build the image using the same Docker host as the minikube VM, so that the images are automatically present.

# To use Minikube Docker daemon:
```
eval $(minikube docker-env)
```

To stop using:
```
eval$(minikube docker-env -u)
```


# Remover tudo do cluster Minikube:
```
minikube delete
```

# Criar um novo cluster Minikube:
```
minikube start
```

## Run Scripts

After build: mvn clean install

# Permission execution:
```
chmod +x create_mongoDB.sh \
chmod +x create_Kubernets_cluster.sh \
chmod +x build_all_create_all.sh \
chmod +x create_hazelcast-management-center.sh \
chmod +x delete_all_ReplicationControllers.sh \
chmod +x build_Scripts/*.sh \
chmod +x create_Scripts/*.sh \
```

# Execute in folder kubernets-scripts

```
eval $(minikube docker-env)
./create_mongoDB.sh \
./create_hazelcast-management-center.sh \
./build_all_create_all.sh -v=v01 -b=true
minikube tunnel
```

Delete all Replication Controllers and Build all again with new forced version:
```
./delete_all_ReplicationControllers.sh \
./build_all_create_all.sh -v=v02 -b=true
```

















