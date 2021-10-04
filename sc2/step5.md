На этом шаге мы настроим балансиоровку исходящего трафика из ServiceA на два сервсиса-поставщика данных - ServiceB и ServiceC.

Схема service mesh, в соотвесвтии с которой будем настраивать наш кластер:

![Mesh configuration](../assets/sc2-3.png)

Установим ServiceC:
`kubectl apply -f service-c-deployment.yml`{{execute}}

Применим манифест Service для деплоймента ServiceC:
`kubectl apply -f service-c-srv.yml`{{execute}}

Россмотрим новую версию правила маршрутизации producer-internal-host-vs:
```
apiVersion: networking.istio.io/v1alpha3
kind: VirtualService
metadata:
  name: producer-internal-host-vs
spec:
  hosts:
    - producer-internal-host
  gateways:
    - mesh
  http:
    - route:
        - destination:
            host: producer-internal-host
            port:
              number: 80
          weight: 50
        - destination:
            host: service-c-srv
            port:
              number: 80
          weight: 50
```

Блок spec.http[0].route содержит два вложенных блока destination с хостами producer-internal-host и service-c-srv, а также с ключами weight, содержашими значания процентных долей для расщепления трафика и перенаправления всех поступивших на хост producer-internal-host (ключ spec.hosts) запросов.

Обновим вирутальный сервис producer-internal-host-vs, созданный на предидущем шаге, новым манифестом producer-internal-host-50-c-vs.yml:
`kubectl apply -f producer-internal-host-50-c-vs.yml`{{execute}}

Теперь, приблизительно 50% запросов будут направлены на Service C, оставшиейся как и ранее - на Service B. Совершите 5-6 запрсоов и убедитесь, что в отве присутсвуют данные из разных сервисов.
`curl -v http://$GATEWAY_URL/service-a`{{execute}}

Теперь среди ответов мы увидим уже известный нам вариант:
`Hello from ServiceA! Calling Producer Service... Received response from Producer Service: Hello from ServiceB!`

Но будет также новый вариант:

`Hello from ServiceA! Calling Producer Service... Received response from Producer Service: Hello from Service-C! Calling master system API... 404 Not Found: [no body]`

Такой ответ - результат направления запроса из ServiceA в ServiceC, который пытается получить данные из своего поставщика http://istio-ingressgateway.istio-system.svc.cluster.local/service-ext.

При последующих вызовах ответы продрожат чередоваться, так как мы расщепили трафик на два сервиса, как отражено на схеме.

`curl -v http://$GATEWAY_URL/service-a`{{execute}}

Перейдем далее.