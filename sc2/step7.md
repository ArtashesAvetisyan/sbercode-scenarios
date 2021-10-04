На данном шаге мы откроем исходящий трафик из service mesh для получения ответов из worldtimeapi.org на запросы из ServiceC.

Схема service mesh, в соотвесвтии с которой будем настраивать наш кластер:

![Mesh configuration](../assets/sc2-4.png)

Существует 3 подхода к открытию исходящего трафика в Istio:

1) Открытый доступ из любого пода на любой внешний хост по умолчанию - удобный подход для разработки, но не безопасный и не контролируемый, поэтому в промышленной эксплуатации применяется редко.

2) Отсутствие доступа на любой внешний хост исключая те, которые явно указаны в манифесте ServiceEntry.

3) Направление трафика на внешний хост через единый egress шлюз - позволяет обогатить весь исходящий трафик из кластера требуемой логикой (например обогатить заголовками для аутентификации запросов), мониторировать и контролировать его. Данный подход применяться в больших промышленных системах.

Реализуем третий подход.

Развернем egress-шлюз, выполнив команду авто-конфигруации Isto:

`istioctl -c /etc/rancher/k3s/k3s.yaml install -y --set components.egressGateways[0].name=istio-egressgateway --set components.egressGateways[0].enabled=true --set meshConfig.accessLogFile=/dev/stdout --set meshConfig.outboundTrafficPolicy.mode=REGISTRY_ONLY --set values.pilot.resources.requests.memory=128Mi --set values.pilot.resources.requests.cpu=50m --set values.global.proxy.resources.requests.cpu=10m --set values.global.proxy.resources.requests.memory=32Mi`{{execute}}

В случае успеха, в выводе вышеприведенной команды должны быть строки:
```
✔ Istio core installed                                                        
✔ Istiod installed                                                            
✔ Ingress gateways installed                                                  
✔ Egress gateways installed                                                   
✔ Installation complete
```

Создадим манифест Gateway для исходящего трафика:
`kubectl apply -f service-ext-outbound-gw.yml`{{execute}}

Рассмотрим новое правило маршрутизации:
```
apiVersion: networking.istio.io/v1alpha3
kind: VirtualService
metadata:
  name: outbound-srv-c-to-external-srv-vs
spec:
  hosts:
    - istio-ingressgateway.istio-system.svc.cluster.local
  gateways:
    - istio-egressgateway
    - mesh
  http:
    - match:
        - gateways:
            - mesh
          port: 80
      route:
        - destination:
            host: istio-egressgateway.istio-system.svc.cluster.local
            port:
              number: 80
    - match:
        - gateways:
            - istio-egressgateway
          port: 80
      route:
        - destination:
            host: istio-ingressgateway.istio-system.svc.cluster.local
            port:
              number: 80
```

В соответствии с этим манифестом новое правило будет работать при вызовах на хост istio-ingressgateway.istio-system.svc.cluster.local из шлюза istio-egressgateway, а также из любого envoy-прокси в неймспейсе. Если вызов прийдет из любого envoy-прокси в неймспейсе (кроме istio-egressgateway), произойдет его перенаправление на хост istio-egressgateway. Если поступит запрос из istio-egressgateway, то он будет направлен на хост istio-ingressgateway.istio-system.svc.cluster.local. Таким образом достигается сосредоточение всех исходящих вызовов в кластере на шлюз istio-egressgateway.

Применим это правило:
`kubectl apply -f outbound-srv-c-to-service-ext-vs.yml`{{execute}}

Теперь исходящий трафик направляется через egress-шлюз и достигает istio-ingressgateway.istio-system.svc.cluster.local.

Совершим несколько запросов на ingress-шлюз, напомню, запросы из ServiceA все также балансируются между ServiceB и ServiceC:

`curl -v http://$GATEWAY_URL/service-a`{{execute}}

На этом шаге все ответы должны быть успешные и иметь вид (если поступили из ServiceB):
`Hello from ServiceA! Calling Producer Service... Received response from Producer Service: Hello from ServiceB!`

Или из ServiceC:
```
Hello from ServiceA! Calling Producer Service... Received response from Producer Service: Hello from Service-EXT! Calling master system API... Received response from master system (http://istio-ingressgateway.istio-system.svc.cluster.local/service-ext): Hello from External Cluster Service!
```

Обратите внимание, что в части ответа из ServiceC присутвует ответ из кластера external-cluster по запросу `http://istio-ingressgateway.istio-system.svc.cluster.local/service-ext`

Если исходящий трафик планируется направить на хост, который не зарегистриован в сети (в другой сетевой контур, например, в открытом Интернете), то в том случае следует дополнительно создать манифест ServiceEntry с описанием ного хоста.

Перейдем далее.