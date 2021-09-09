На этом шаге мы выполним конфигурацию Istio

Выпооним команду: `istioctl install --set meshConfig.accessLogFile=/dev/stdout --set meshConfig.outboundTrafficPolicy.mode=REGISTRY_ONLY`{{execute}}

Обратите внимание на параметры, применяемые в данной команде:

1) meshConfig.accessLogFile=/dev/stdout - активации записи логов доступа Envoy каждого контейнера с прокси-сервером

2) meshConfig.outboundTrafficPolicy.mode=REGISTRY_ONLY - разрешаем прокси-сервером перенаправлять запросы на внешние хосты, которые явно указаны во внутреннем реестре Istio. В ином случае, Istio позволит совершить исходящий запрос на любой хост.