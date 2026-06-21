# 로컬 쿠버네티스

## 클러스터 생성

```bash
kind create cluster --config kind-bookiverse.yaml
kubectl apply -f https://raw.githubusercontent.com/kubernetes/ingress-nginx/main/deploy/static/provider/kind/deploy.yaml
kubectl wait --namespace ingress-nginx --for=condition=ready pod --selector=app.kubernetes.io/component=controller --timeout=180s
```

호스트 항목은 한 번만 추가하면 된다.

```bash
sudo sh -c 'echo "127.0.0.1 bookiverse.local" >> /etc/hosts'
```

## 서비스 이미지 빌드와 적재

```bash
./gradlew build
docker build -t bookiverse/book:local book
docker build -t bookiverse/rental:local rental
docker build -t bookiverse/user:local user
kind load docker-image bookiverse/book:local --name bookiverse
kind load docker-image bookiverse/rental:local --name bookiverse
kind load docker-image bookiverse/user:local --name bookiverse
```

## 배포

```bash
kubectl apply -k k8s/local
kubectl get pods -n bookiverse
kubectl get ingress -n bookiverse
```

## Ingress 라우팅 테스트

```bash
curl -i http://bookiverse.local:8080/books/1
curl -i http://bookiverse.local:8080/users/1
curl -i -X POST http://bookiverse.local:8080/rentals/1/books/1
```
