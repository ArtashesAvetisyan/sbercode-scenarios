На этом шаге мы осуществим конфигурацию Istio

Выполним команду: `istioctl -c /etc/rancher/k3s/k3s.yaml install -y --set meshConfig.accessLogFile=/dev/stdout --set meshConfig.outboundTrafficPolicy.mode=REGISTRY_ONLY --set values.pilot.resources.requests.memory=128Mi --set values.pilot.resources.requests.cpu=50m --set values.global.proxy.resources.requests.cpu=10m --set values.global.proxy.resources.requests.memory=32Mi`{{execute}}

В случае успеха, в выводе вышеприведенной команды должны быть строки:
```
✔ Istio core installed                                                        
✔ Istiod installed                                                            
✔ Ingress gateways installed                                                  
✔ Installation complete
```

Обратите внимание на параметры, применяемые в данной команде:

1) meshConfig.accessLogFile=/dev/stdout - активации записи логов доступа Envoy каждого контейнера с прокси-сервером

2) meshConfig.outboundTrafficPolicy.mode=REGISTRY_ONLY - разрешаем прокси-сервером перенаправлять запросы на внешние хосты, которые явно указаны во внутреннем реестре Istio. В ином случае, Istio позволит совершить исходящий запрос на любой хост.