Манифест VirtualService из API Istio определяет список правил маршрутизации трафика внутри service-mesh в привязке к имени вызываемого хоста.

На данном шаге мы применим манифест определяющий те правила, которые позволят запросам из ingress-щлюза поступить в под с бизнес сервисом ServiceB.

Рассмотрим манифест:
```
apiVersion: networking.istio.io/v1alpha3
kind: VirtualService
metadata:
  name: inbound-to-service-b-vs
spec:
  hosts:
    - "*"
  gateways:
    - service-b-gw
  http:
    - match:
        - uri:
            exact: /service-b
      rewrite:
        uri: /
      route:
        - destination:
            host: producer-internal-host
            port:
              number: 80
```

Обратите внимание на значения ключей spec.hosts, spec.gateways. Первый содержит список хостов, которые охватывает данное правило, второй - список имен шлюзов, запросы из которых будут учтены данным правилом (здесь указано значение имени Gateway, созданного на предыдущем шаге).

Ключ spec.http[0].match[0].uri.exact содержит значение HTTP заголовка path в запросе, он же определяет запрошенный путь, в данном случае это - "/service-b".

Ключ spec.http[0].rewrite.uri содержит то значение, на которое следует заменить значение заголовка path поступившего запроса, в данном случае это "/", то есть запросы с путем "/service-b" будут направлены на корневой каталог ("/") хоста назначения.

Ключ spec.http[0].route[0].destination.host содержит имя хоста назначения, в данном случае producer-internal-host - имя в манифесте Service, созданном на шаге 5.

Ключ spec.http[0].route[0].destination.port.number содержит значение порта упомянутого сервиса.

Давайте применим inbound-to-service-b-vs.yml:
`kubectl apply -f https://raw.githubusercontent.com/avsinsight/katacoda-scenarios/main/sc1/src/inbound-to-service-b-vs.yml`{{execute}}

Получим детальное описание созданного ресурса:
`kubectl describe virtualservice.networking.istio.io inbound-to-service-b-vs`{{execute}}

Перейдем к следующему шагу.