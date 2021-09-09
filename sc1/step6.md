Манифест Gateway из API Istio конфигурирует изолированный envoy-proxy, который управляет всем входящим (ingress-шлюз) или исходящим (egress-шлюз) трафиком сети.

На данном шаге мы будем конфигурировать ingres-шлюз представляющий собой под с контейнером envoy-proxy из пространства имен istio-system, где он был развернут автоматически при установке istio.

Рассмотрим манифест:
```
apiVersion: networking.istio.io/v1alpha3
kind: Gateway
metadata:
  name: service-b-gw
spec:
  selector:
    istio: ingressgateway
  servers:
    - port:
        number: 80
        name: http
        protocol: HTTP
      hosts:
        - "*"
```

Обратите внимание на значения ключей spec.selector.istio - содержит значение селектора istio, таким образом определяя свое действие на под, имеющий подобный селектор (istio=ingressgateway).

Ключ spec.servers[0].port.number содержит номер порта, который будет открыт у ingress-шлюза для приема входящих запросов, а ключ spec.servers[0].hosts - имя хостов, которые могут быть запрошены.

Рассмотрим детальное описание пода istio-ingressgateway, в том числе блок Labels, содержащий среди прочего - istio=ingressgateway:

`kubectl describe pod -l app=istio-ingressgateway -n istio-system`{{execute}}

Давайте применим service-b-gw.yml:

`kubectl apply -f https://raw.githubusercontent.com/avsinsight/katacoda-scenarios/main/sc1/src/service-b-gw.yml`{{execute}}

Получим детальное описание созданного ресурса:

`kubectl describe gateway.networking.istio.io service-b-gw`{{execute}}

Перейдем к следующему шагу.