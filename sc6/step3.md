В предыдущем шаге мы обращались к ServiceB через цепь взаимодействий: ingress-шлюз -> ServiceA -> ServiceB.

Ingress-шлюз развернут в пространстве имен istio-system, ServiceA - в dev-service-mesh.

Для демонстрации действия политики авторизации давайте запретим все вызовы ServiceA в пространстве имен dev-service-mesh из istio-system при помощи манифеста ниже:
```
apiVersion: security.istio.io/v1beta1
kind: AuthorizationPolicy
metadata:
  name: autho-policy
  namespace: dev-service-mesh
spec:
  selector:
    matchLabels:
      app: service-a-app
  action: DENY
  rules:
    - from:
        - source:
            namespaces: ["istio-system"]
```
Обратите внимание на ключ spec.selector, относящий данную политику к поду с селектором `app:service-a-app`, ключ spec.action, который содержит вид политики (в данном случае - запрещающая) и spec.rules - содержащий блок детализации правила, в данном случае правило будет относиться к запросам из пространства имен istio-system.

Применим данный манифест:
`kubectl apply -f https://raw.githubusercontent.com/avsinsight/katacoda-scenarios/main/sc6/src/autho-policy.yml`{{execute}}

Подождем 5-10 секунд для применения политики и совершим GET запрос в адрес ingress-шлюза, как ранее:
`curl -v http://$GATEWAY_URL/service-a`{{execute}}

Тела ответа не будет, а среди заголовков ответа:
```
< HTTP/1.1 403 Forbidden
< content-length: 19
< content-type: text/plain
< date: Sun, 22 Aug 2021 13:06:48 GMT
< server: istio-envoy
< x-envoy-upstream-service-time: 0
```

Цель достигнута.

Давайте обновим предыдущую политику манифестом ниже:
```
apiVersion: security.istio.io/v1beta1
kind: AuthorizationPolicy
metadata:
  name: autho-policy
  namespace: dev-service-mesh
spec:
  selector:
    matchLabels:
      app: service-a-app
  action: DENY
  rules:
    - from:
        - source:
            notNamespaces: ["istio-system"]
```

Обратите внимание на отличие от предыдущего манифеста: значение ключа spec.rules[0].from[0].source в данном случае `notNamespaces: ["istio-system"]`, что меняет эффект политики на запрет вызовов ServiceA из всех пространств имен, за исключением istio-system.

Обновим политику:
`kubectl apply -f https://raw.githubusercontent.com/avsinsight/katacoda-scenarios/main/sc6/src/autho-policy-non-istio-sestem.yml`{{execute}}

Совершим GET запрос в адрес ingress-шлюза:
`curl -v http://$GATEWAY_URL/service-a`{{execute}}

Теперь мы снова видим успешный ответ:
`Hello from ServiceA! Calling Producer Service... Received response from Producer Service: Hello from ServiceB!`
