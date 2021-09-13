На этом шаге мы осуществим конфигурацию Istio

Выполним команду: `istioctl -c /etc/rancher/k3s/k3s.yaml install --set meshConfig.accessLogFile=/dev/stdout --set meshConfig.outboundTrafficPolicy.mode=REGISTRY_ONLY`{{execute}}

Входе выполнения следует подтвердить намерение указав в терминале символ `y`

Обратите внимание на параметры, применяемые в данной команде:

1) meshConfig.accessLogFile=/dev/stdout - активации записи логов доступа Envoy каждого контейнера с прокси-сервером

2) meshConfig.outboundTrafficPolicy.mode=REGISTRY_ONLY - разрешаем прокси-сервером перенаправлять запросы на внешние хосты, которые явно указаны во внутреннем реестре Istio. В ином случае, Istio позволит совершить исходящий запрос на любой хост.