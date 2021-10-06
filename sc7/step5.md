
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