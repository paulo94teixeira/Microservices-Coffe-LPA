**Minikube**
instead of pushing yout Docker image to a registry, you can build the image using the same Docker host as the minikube VM, so that the images are automatically present.
 To use Minikube Docker daemon:
> eval $(minikube docker-env)

To stop using:
> eval$(minikube docker-env -u)

Remover todo o cluster Minikube:
> minikube delete

Criar um novo cluster Minikube:
< minikube start

**Run Scripts Docker**

After build: mvn clean install

Permission execution:
> chmod +x create_mongoDB.sh \
> chmod +x create_Kubernets_cluster.sh \
> chmod +x build_all_create_all.sh \
> chmod +x update_all_containers.sh \
> chmod +x create_hazelcast-management-center.sh \
> chmod +x dnsLookup.sh \                     
> chmod +x delete_all_ReplicationControllers.sh \
> chmod +x build_Scripts/*.sh \
> chmod +x create_Scripts/*.sh \
> chmod +x update_Scripts/*.sh


Execute in folder kubernets-scripts

Build & Create project to Minikube for the first time:
- v01 is the number of version tag

> eval $(minikube docker-env)
> ./create_mongoDB.sh \
> ./create_hazelcast-management-center.sh \
> ./build_all_create_all.sh -v=v01 -b=true

OR (Default build & deploy is true & version is v01)

> ./build_all_create_all.sh -v=v01

Update all pods of Deployment:
- v02 is the number of version tag
> ./update_all_containers.sh --version=v02 --build=true--update=true --replication=false

Update all pods of ReplicationController(Rolling update):
- v02 is the number of version tag
> ./update_all_containers.sh -v=v02 -b=true -u=true -r=true

Delete all Replication Controllers and Build all again with new forced version:
- v02 is the number of version tag
> ./delete_all_ReplicationControllers.sh \
> ./build_all_create_all.sh -v=v02 -b=true

To delete the entire cluster and network:

















