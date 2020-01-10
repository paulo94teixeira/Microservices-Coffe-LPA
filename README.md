# Microservices-Coffe-LPA Project MEI ESTG IPP MTADS

### Identificação do Intervenientes
```
• Letícia Ribeiro Nº8190011
• Paulo Teixeira Nº8150524
```

### Objetivos
```
• Conceber a arquitetura técnica de um projeto segundo o paradigma micro-serviços;
• Desenvolver e implementar uma solução tecnológica de acordo com as práticas e padrões
associados a aplicações orientadas a micro-serviços;
• Argumentar decisões de arquitetura e justificar opções de implementação no contexto de
desenvolvimento de arquitetura baseadas em micro-serviços.
```

### Recursos
```
• Minikube
• Kurbenetes
• Git Hub
• Google Cloud Platform
• Opções para o desenvolvimento dos microserviços (Vert.x)
```

### Procedimentos
```
De acordo com as instruções fornecidas nas aulas, deverá garantir a correta instalação e
configuração do ambiente de desenvolvimento usando as tecnologias:
o Vagrant
o Git
• Deverão conceber a aplicação com um serviço que forneça pelo menos um dataset para
reporting em JSON.
```

### Para poder executar o projeto deve ter instalado na sua máquina os seguintes softwares e adicionar as respetivas variaveis de ambiente:

```
- Docker
- Kubernetes
- Java 8
- MongoDB
- Maven Apache
- Git bash
```

## Run app local machine

```
./script_run to run
./script_stop to stop
```

## Run app kubernetes
   
Aceder à pasta kubernets-scripts e executar os seguintes comandos/scripts

### START

```
minikube start --cpus=3 --memory=4192
eval $(minikube docker-env)
./create_mongoDB.sh
./create_permissions.sh
./build_all_create_all.sh -v=v01 -b=true
minikube tunnel
```

### STOP

```
minikube stop
docker-machine stop
```

### Delete all services and deploymets
```
./deleteall.sh
```
