
Развернем egress-шлюз, выполнив команду авто-конфигруации Isto:

`istioctl -c /etc/rancher/k3s/k3s.yaml install -y --set components.egressGateways[0].name=istio-egressgateway --set components.egressGateways[0].enabled=true --set meshConfig.accessLogFile=/dev/stdout --set meshConfig.outboundTrafficPolicy.mode=REGISTRY_ONLY --set values.pilot.resources.requests.memory=128Mi --set values.pilot.resources.requests.cpu=50m --set values.global.proxy.resources.requests.cpu=10m --set values.global.proxy.resources.requests.memory=32Mi --set values.global.proxy.resources.limits.memory=64Mi --set values.pilot.resources.limits.memory=256Mi`{{execute}}

`kubectl apply -f outbound-srv-c-to-service-ext-vs.yml`{{execute}}
`kubectl apply -f service-ext-outbound-gw.yml`{{execute}}
Применим DestinationRule:
`kubectl apply -f external-cluster-dr.yml`{{execute}}

Совершим GET запрос по адресу ingress-шлюза:
`curl -v http://$GATEWAY_URL/service-c`{{execute}}