
Развернем egress-шлюз, выполнив команду авто-конфигруации Isto:

`istioctl -c /etc/rancher/k3s/k3s.yaml install -y --set components.egressGateways[0].name=istio-egressgateway --set components.egressGateways[0].enabled=true --set meshConfig.accessLogFile=/dev/stdout --set meshConfig.outboundTrafficPolicy.mode=REGISTRY_ONLY --set values.pilot.resources.requests.memory=128Mi --set values.pilot.resources.requests.cpu=50m --set values.global.proxy.resources.requests.cpu=10m --set values.global.proxy.resources.requests.memory=32Mi --set values.global.proxy.resources.limits.memory=64Mi --set values.pilot.resources.limits.memory=256Mi`{{execute}}

`kubectl apply -f outbound-srv-c-to-service-ext-vs.yml`{{execute}}
`kubectl apply -f service-ext-outbound-gw.yml`{{execute}}
Применим DestinationRule:
`kubectl apply -f external-cluster-dr.yml`{{execute}}




Совершим GET запрос по адресу ingress-шлюза:
`curl -v http://$GATEWAY_URL/service-c`{{execute}}

На данном шаге мы откроем исходящий HTTPS трафик из service mesh для получения ответов из oracle.com на запросы из ServiceG.

Рассмотрим манифест outbound-oracle-dr.yml:
```
apiVersion: networking.istio.io/v1alpha3
kind: DestinationRule
metadata:
  name: www-oracle-com-dr
spec:
  host: www.oracle.com
  trafficPolicy:
    portLevelSettings:
      - port:
          number: 80
        tls:
          mode: SIMPLE
```

Обратите внимание на ключ spec.trafficPolicy.portLevelSettings[0].tls со значением `mode: SIMPLE`. Такое значение позволит зашифровать HTTP трафик, поступивший на указанный порт 80 для хоста www.oracle.com.

Рассмотрим манифест oracle-host-se.yml:
```
apiVersion: networking.istio.io/v1alpha3
kind: ServiceEntry
metadata:
  name: www-oracle-com
spec:
  hosts:
    - www.oracle.com
  ports:
    - number: 80
      name: http-port
      protocol: HTTP
      targetPort: 443
    - number: 443
      name: https-port
      protocol: HTTPS
  resolution: DNS
```

Ключи spec.hosts, а также две пары ключей number и protocol обозначают допустимые протоколы для приведенного хоста, но обратите внимание на ключ spec.ports[0].targetPort (443). При налчии этого ключа, трафик, который поступит на порт в значении spec.ports[0].number (80), будет перенаправлен на порт в значении spec.ports[0].targetPort (443).

Таким образом мы достигнем перенаправления трафика при помощи envoy-прокси в поде с бизнес сервисом из порта 80, куда направляет запросы ServiceG, в порт 443.



Применим ServiceEntry:
`kubectl apply -f external-cluster-se.yml`{{execute}}



В ответе мы получим:
`Hello from ServiceG! Calling master system API... Received response from master system (http://www.oracle.com/index.html): <!DOCTYPE html><html lang="en"><head><link href="/product-navigator/main__product-navigator__1.29.44.css" as="style" rel="preload"/><meta charSet="utf-8"/><title>Oracle ...`

Как мы убедились ранее на шаге 2, данную страницу можно получить только при GET запросе с применением HTTPS протокола.

Кроме того, вся сетевая логика, связанная с созданием HTTPS соединения и шифрованием данных, осталась абсолютно прозрачной для бизнес сервиса, который продолжал совершать небезопасные HTTP запросы без TLS шифрования.

Таким образом мы зашифровали HTTP трафик и создали HTTPS соединение.


[2021-10-06T22:41:57.885Z] "GET /service-ext HTTP/2" 503 NC cluster_not_found - "-" 0 0 0 - "10.42.0.9" "Java/11.0.12" "eb65ea17-1b51-4e34-aee1-0446a3b68118" "istio-ingressgateway.istio-system.svc.cluster.local" "-" - - 10.42.0.11:8080 10.42.0.9:41952 - -
[2021-10-06T22:41:58.557Z] "GET /service-ext HTTP/2" 503 NC cluster_not_found - "-" 0 0 0 - "10.42.0.9" "Java/11.0.12" "701215ce-620e-452d-af79-e7cc1daadb49" "istio-ingressgateway.istio-system.svc.cluster.local" "-" - - 10.42.0.11:8080 10.42.0.9:41952 - -
[2021-10-06T22:41:58.558Z] "GET /service-ext HTTP/2" 503 NC cluster_not_found - "-" 0 0 0 - "10.42.0.9" "Java/11.0.12" "701215ce-620e-452d-af79-e7cc1daadb49" "istio-ingressgateway.istio-system.svc.cluster.local" "-" - - 10.42.0.11:8080 10.42.0.9:41952 - -
[2021-10-06T22:41:58.581Z] "GET /service-ext HTTP/2" 503 NC cluster_not_found - "-" 0 0 0 - "10.42.0.9" "Java/11.0.12" "701215ce-620e-452d-af79-e7cc1daadb49" "istio-ingressgateway.istio-system.svc.cluster.local" "-" - - 10.42.0.11:8080 10.42.0.9:41952 - -
[2021-10-06T22:41:59.238Z] "GET /service-ext HTTP/2" 503 NC cluster_not_found - "-" 0 0 0 - "10.42.0.9" "Java/11.0.12" "c4e26db0-b2b1-446d-ad0f-714bb6378385" "istio-ingressgateway.istio-system.svc.cluster.local" "-" - - 10.42.0.11:8080 10.42.0.9:41952 - -
[2021-10-06T22:41:59.262Z] "GET /service-ext HTTP/2" 503 NC cluster_not_found - "-" 0 0 0 - "10.42.0.9" "Java/11.0.12" "c4e26db0-b2b1-446d-ad0f-714bb6378385" "istio-ingressgateway.istio-system.svc.cluster.local" "-" - - 10.42.0.11:8080 10.42.0.9:41952 - -
[2021-10-06T22:41:59.301Z] "GET /service-ext HTTP/2" 503 NC cluster_not_found - "-" 0 0 0 - "10.42.0.9" "Java/11.0.12" "c4e26db0-b2b1-446d-ad0f-714bb6378385" "istio-ingressgateway.istio-system.svc.cluster.local" "-" - - 10.42.0.11:8080 10.42.0.9:41952 - -
[2021-10-06T22:42:00.557Z] "GET /service-ext HTTP/2" 503 NC cluster_not_found - "-" 0 0 0 - "10.42.0.9" "Java/11.0.12" "55d8da92-4b6f-4b2c-9bbd-5a6d81916618" "istio-ingressgateway.istio-system.svc.cluster.local" "-" - - 10.42.0.11:8080 10.42.0.9:41952 - -
[2021-10-06T22:42:00.573Z] "GET /service-ext HTTP/2" 503 NC cluster_not_found - "-" 0 0 0 - "10.42.0.9" "Java/11.0.12" "55d8da92-4b6f-4b2c-9bbd-5a6d81916618" "istio-ingressgateway.istio-system.svc.cluster.local" "-" - - 10.42.0.11:8080 10.42.0.9:41952 - -
[2021-10-06T22:42:00.601Z] "GET /service-ext HTTP/2" 503 NC cluster_not_found - "-" 0 0 0 - "10.42.0.9" "Java/11.0.12" "55d8da92-4b6f-4b2c-9bbd-5a6d81916618" "istio-ingressgateway.istio-system.svc.cluster.local" "-" - - 10.42.0.11:8080 10.42.0.9:41952 - -
[2021-10-06T22:42:00.573Z] "GET /service-ext HTTP/2" 503 NC cluster_not_found - "-" 0 0 0 - "10.42.0.9" "Java/11.0.12" "55d8da92-4b6f-4b2c-9bbd-5a6d81916618" "istio-ingressgateway.istio-system.svc.cluster.local" "-" - - 10.42.0.11:8080 10.42.0.9:41952 - -
[2021-10-06T22:42:00.601Z] "GET /service-ext HTTP/2" 503 NC cluster_not_found - "-" 0 0 0 - "10.42.0.9" "Java/11.0.12" "55d8da92-4b6f-4b2c-9bbd-5a6d81916618" "istio-ingressgateway.istio-system.svc.cluster.local" "-" - - 10.42.0.11:8080 10.42.0.9:41952 - -
[2021-10-06T22:44:29.608Z] "GET /service-ext HTTP/2" 503 UC upstream_reset_before_response_started{connection_termination} - "-" 0 95 0 - "10.42.0.9" "Java/11.0.12" "0e931e89-5f3e-4098-a6b4-f8ab95b557c9" "istio-ingressgateway.istio-system.svc.cluster.local" "10.42.0.3:8443" outbound|443||istio-ingressgateway.istio-system.svc.cluster.local 10.42.0.11:38560 10.42.0.11:8080 10.42.0.9:41800 - -
[2021-10-06T22:44:29.617Z] "GET /service-ext HTTP/2" 503 UC upstream_reset_before_response_started{connection_termination} - "-" 0 95 0 - "10.42.0.9" "Java/11.0.12" "0e931e89-5f3e-4098-a6b4-f8ab95b557c9" "istio-ingressgateway.istio-system.svc.cluster.local" "10.42.0.3:8443" outbound|443||istio-ingressgateway.istio-system.svc.cluster.local 10.42.0.11:38562 10.42.0.11:8080 10.42.0.9:41800 - -
[2021-10-06T22:44:29.665Z] "GET /service-ext HTTP/2" 503 UC upstream_reset_before_response_started{connection_termination} - "-" 0 95 0 - "10.42.0.9" "Java/11.0.12" "0e931e89-5f3e-4098-a6b4-f8ab95b557c9" "istio-ingressgateway.istio-system.svc.cluster.local" "10.42.0.3:8443" outbound|443||istio-ingressgateway.istio-system.svc.cluster.local 10.42.0.11:38564 10.42.0.11:8080 10.42.0.9:41800 - -