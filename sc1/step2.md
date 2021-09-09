На этом шаге мы установим Istio

Давайте загрузим istioctl - утилиту для конфигурации Istio выполнив команду: `curl -sL https://istio.io/downloadIstioctl | sh -`{{execute}}

Для того что-бы работать с istioctl из командной строки давайте экспортируем путь к ней при помощи команды: `export PATH=$PATH:$HOME/.istioctl/bin`{{execute}}

Проверим готовность среды для установки Istio: `istioctl x precheck`{{execute}}

Запустим установку Istio: `istioctl install --set meshConfig.accessLogFile=/dev/stdout --set meshConfig.outboundTrafficPolicy.mode=REGISTRY_ONLY`{{execute}}

Во время установки следует подтвердить намерение указав в терминале символ `y`{{copy}}

Обратите внимание на параметры, применяемые в данной команде:

1) meshConfig.accessLogFile=/dev/stdout - активации записи логов доступа Envoy каждого контейнера с прокси-сервером

2) meshConfig.outboundTrafficPolicy.mode=REGISTRY_ONLY - разрешаем прокси-сервером перенаправлять запросы на внешние хосты, которые явно указаны во внутреннем реестре Istio. В ином случае, Istio позволит совершить исходящий запрос на любой хост.