
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


docker build -t learning/trade-statistics-webapp:v1 . 

 kubectl config set-context --current --namespace=learning-ks
 
 kubectl logs -l app=trade-statistics-aggregator  -c istio-proxy
```

http://trade-stats.local/api/trade-stats/ABBN

```shell
kubectl autoscale deployment trade-statistics-webapp-v2 --cpu-percent=50 --min=1 --max=10

kubectl run -i \
--tty load-generator --rm --image=busybox:1.28 \
 --restart=Never --overrides='{"kind":"Pod", "apiVersion":"v1", "spec": {"hostNetwork": true}}' \
 -- /bin/sh -c "while sleep 0.01; do wget -q -O- http://trade-stats-webapp.local/trades/ABBN; done"


```