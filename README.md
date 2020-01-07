# Microservices-Coffe-LPA Project MEI ESTG IPP MTADS

## Objetivos
```
• Conceber a arquitetura técnica de um projeto segundo o paradigma micro-serviços;
• Desenvolver e implementar uma solução tecnológica de acordo com as práticas e padrões
associados a aplicações orientadas a micro-serviços;
• Argumentar decisões de arquitetura e justificar opções de implementação no contexto de
desenvolvimento de arquitetura baseadas em micro-serviços.
```

## Recursos
```
• Minikube
• Kubernetes
• Git Hub
• Google Cloud Platform
• Opções para o desenvolvimento dos microserviços (Vert.x)
```

## Procedimentos
```
De acordo com as instruções fornecidas nas aulas, deverá garantir a correta instalação e
configuração do ambiente de desenvolvimento usando as tecnologias:
o Vagrant
o Git
• Deverão conceber a aplicação com um serviço que forneça pelo menos um dataset para
reporting em JSON.
```

# Para poder executar o projeto deve ter instalado na sua máquina os seguintes softwares 
# e adicionar as respetivas variaveis de ambiente:

```
- Docker
- Kubernetes
- Java 8
- MongoDB
- Maven Apache
```

# Run app local machine

```
./script_run to run
./script_stop to stop
```

# Run app kubernetes
   
Aceder à pasta kubernets-scripts e executar os seguintes comandos/scripts

```
./create_mongoDB.sh
./create_hazelcast-management-center.sh
./build_all_create_all.sh -v=v01 -b=true
```
