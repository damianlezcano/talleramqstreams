[<< back](../README.md)

# 07 - Contract [FULL_SCAN]

# Procedimiento armado ambiente local

## KAFKA

### RUN

```sh
cd /Users/damianlezcano/rh/kafka_2.13-2.8.0
bin/zookeeper-server-start.sh config/zookeeper.properties
bin/kafka-server-start.sh config/server.properties
```

### Crear Topic

```bash
$KAFKA_ROOT_FOLDER/bin/kafka-topics.sh --bootstrap-server localhost:9092 --topic contract \
  --partitions 3 \
  --replication-factor 1 \
  --create
```

### Verificamos stream

```sh
./kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic contract --from-beginning --max-messages 10
```
## APICURIO REGISTRY

### RUN DOCKER

```sh
docker run -it -p 8081:8080 apicurio/apicurio-registry-mem:2.0.1.Final
```

### Crear un schema en apicurio registry

```sh
curl -v -X POST -H "Content-type: application/json; artifactType=AVRO" \
    -H "X-Registry-ArtifactId: integra.publisher.model.Contract" \
    --data-binary "@src/main/avro/schemas/Contract.avsc" \
    localhost:8081/api/artifacts
```

** Si existe problema en ejecutarlo, hacerlo con la UI **


## BASE DE DATOS

### RUN DOCKER

```
docker run --user=root -e 'ACCEPT_EULA=Y' -e 'SA_PASSWORD=yourStrong(!)Password' -e 'MSSQL_PID=Express' -p 1433:1433 -d mcr.microsoft.com/mssql/server:2019-latest 
```

### CREAR TABLA
```sql
CREATE TABLE master.dbo.contract (
  contract numeric(8,0),
  internal varchar(200),
  client numeric(8,0)
)

CREATE TABLE master.dbo.client (
  client numeric(8,0),
  description varchar(200)
)
```

### INSERT REGISTRO

```sql
INSERT INTO master.dbo.client (client,description) VALUES (5,'BANCO SANTANDER RIO SA-BANCO SANTANDER RIO SA');
INSERT INTO master.dbo.contract (contract, internal, client) VALUES (9644,'300000286',5);
```

## Run application

```bash
cd $PROJECT_NAME
mvn clean package && java -jar target/*.jar --spring.config.location="/Users/damianlezcano/rh/workspaces/andreani/andreani-producers/kafka-batch-producer-fullscan/contract-producer/application-local.yaml"
mvn --spring.config.location="/Users/damianlezcano/rh/workspaces/andreani/andreani-producers/kafka-batch-producer-fullscan/contract-producer/application-local.yaml" spring-boot:run
mvn -Dexec.args=--spring.config.location="/Users/damianlezcano/rh/workspaces/andreani/andreani-producers/kafka-batch-producer-fullscan/contract-producer/application-local.yaml" spring-boot:run
```


# Despliegue en openshift

Login a OCP y nos posicionamos soble el namespace
```sh
oc login --token=<token> --server=https://api.aro.andreani.com.ar:6443
oc project mvp-fuse-producer
```

Creamos secret con las credenciales para acceder a github
```sh
oc create secret generic basicsecret --from-literal=password=645c90d55b68bbccb0eea638803896e3b3e8ad44 --type=kubernetes.io/basic-auth
```

Creamos ImageStream y BuildConfig
```sh
oc create configmap contract-producer-config --from-file=application.yaml=./application-dev.yaml

oc create -f <(echo '
apiVersion: image.openshift.io/v1
kind: ImageStream
metadata:
  name: contract-producer
  labels:
    app: contract-producer
    app.kubernetes.io/name: contract-producer
')

oc create -f <(echo '
apiVersion: build.openshift.io/v1
kind: BuildConfig
metadata:
  name: contract-producer
  labels:
    app: contract-producer
    app.kubernetes.io/name: contract-producer
spec:
  source:
    git:
      ref: confluent
      uri: 'https://github.com/architecture-it/andreani-producers.git'
    type: Git
    sourceSecret:
      name: "basicsecret"
    contextDir: "kafka-batch-producer-fullscan/contract-producer"
  output:
    to:
      kind: ImageStreamTag
      name: 'contract-producer:latest'
  strategy:
    type: Source
    sourceStrategy:
      from:
        kind: ImageStreamTag
        name: 'java:8'
        namespace: openshift
      env: []
  triggers:
    - type: ImageChange
      imageChange: {}
    - type: ConfigChange
')

oc start-build contract-producer

oc get builds
```

Desplegamos la aplicación
```sh
oc describe istag/contract-producer:latest | grep -e "Docker Image:" | awk '{print $3}'

oc apply -f <(echo '
apiVersion: apps/v1
kind: Deployment
metadata:
  name: contract-producer
  labels:
    app: contract-producer
    app.kubernetes.io/name: contract-producer
spec:
  selector:
    matchLabels:
      app: contract-producer
  replicas: 1
  template:
    metadata:
      labels:
        app: contract-producer
        app.kubernetes.io/name: contract-producer
    spec:
      containers:
      - name: contract-producer
        env:
          - name: TZ
            value: America/Argentina/Buenos_Aires
          - name: --spring.config.location
            value: /opt/config/application.yaml
          - name: JAVA_OPTS
            value: -Xms2048m -Xmx2048m -XX:+UseParallelGC -XX:ParallelGCThreads=4 -XX:MaxGCPauseMillis=100
        image: image-registry.openshift-image-registry.svc:5000/mvp-fuse-producer/contract-producer@sha256:c563fcd307767b0546f46d5a5fde5c35c8795595b509ae14222ef409a5046e25
        ports:
         - containerPort: 8080
        volumeMounts:
         - name: contract-producer-config-vol
           mountPath: /opt/config
        resources:
          limits:
            cpu: '1'
            memory: 2048Mi
          requests:
            cpu: 100m
            memory: 600Mi
      volumes:
      - configMap:
          defaultMode: 420
          name: contract-producer-config
        name: contract-producer-config-vol
')

```