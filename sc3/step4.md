На этом шаге мы создадим новое пространство имен или виртуальный кластер, который изолирован от текущего пространства имен dev-service-mesh и будет содержать прикладной сервис, поставляющий данные для Service C.

Весь исходящий трафик из dev-service-mesh будут направляться на egress-шлюз, который в свою очередь будет проксировать все запросы в пространство external-cluster.

Новый кластер мы будем создавать аналогично тому, как это подробно представлено в упражнении  [Конфигурация окружения и запуск прикладного сервиса в sevice mesh](https://sbercode.pcbltools.ru/ui/ArtashesAvetisyan/sc1/)

Давайте создадим новое пространство имен (виртуальный кластер):

`kubectl create namespace external-cluster`{{execute}}

Настроим авто-внедрение envoy-прокси в данном пространстве имен:

`kubectl label namespace external-cluster istio-injection=enabled`{{execute}}

Обратите внимание, в отличие от аналогичного шага в упражнении  [Маршрутизация трафика внутри service mesh](https://sbercode.pcbltools.ru/ui/ArtashesAvetisyan/sc2/) ... 

```
kind: Gateway
metadata:
  name: service-ext-gw
spec:
  selector:
    istio: ingressgateway
  servers:
    - port:
        number: 443
        name: https
        protocol: HTTPS
      tls:
        mode: MUTUAL
      hosts:
        - "*"
```
Развернем приведенный выше манифест:

`kubectl apply -f service-ext-deployment.yml -n external-cluster`{{execute}}

Создадим для него Service:

`kubectl apply -f service-ext-srv.yml -n external-cluster`{{execute}}

Откроем доступ к хосту данного сервиса через ingress-шлюз:

`kubectl apply -f service-ext-gw.yml -n external-cluster`{{execute}}

Настроем внутрикластерную маршрутизацию трафика из ingress-шлюза в прикладной сервис:

`kubectl apply -f inbound-to-service-ext-vs.yml -n external-cluster`{{execute}}

Убедимся, что все поды работают:

`kubectl get pods --all-namespaces`{{execute}}

Проверим новый сервис обратившись к нему:

`curl -v http://$GATEWAY_URL/service-ext`{{execute}}

Перейдем далее.