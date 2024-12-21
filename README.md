
```shell
python3 register-schema.py http://localhost:8081 trades trade-statistics-aggregator/src/avro/trade.avsc 
```

View schema: http://localhost:8081/subjects/trades-value/versions/1

GET stats:
http://localhost:8080/trade-stats/ABBN

```shell

helm install  --namespace learning-ks kafka bitnami/kafka 

helm install -f k8s/schema-registry-config.yaml --namespace learning-ks schema-registry oci://registry-1.docker.io/bitnamicharts/schema-registry

```

```shell

docker save mynginx > myimage.tar
microk8s ctr image import myimage.tar

```

```shell
docker build -t learning/trade-statistics-aggregator:v1 .

 kubectl config set-context --current --namespace=learning-ks
```

http://trade-stats.local/api/trade-stats/ABBN