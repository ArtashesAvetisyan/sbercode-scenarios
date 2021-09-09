До начала выполнения упражнения давайте подготови необходимую среду выполнения

## Запуск Kubernetes

Давайте запустим Kubernetes: `launch.sh`{{execute}}

Дождитесь запуска Kubernetes.

Убедимся, что все узлы находятся в состоянии Ready:`kubectl get nodes`{{execute}}

Необходимое состояние узлов для перехода к следующему шагу приведено ниже:
```
NAME           STATUS   ROLES    AGE     VERSION
controlplane   Ready    master   6m4s    v1.18.0
node01         Ready    <none>   5m33s   v1.18.0
```

Если вы не наблюдаете подобного вывода, подождите 1-2 минуты и повторите попытку.

Также убедимся, что все существующие поды всех пространстве имен запущены и функционируют корректно: 

`kubectl get pods --all-namespaces `{{execute}}

Допустимы отличные статусы от приведенных выше значений только для пода katacoda-cloud-provider.

## Установка Istio

Давайте загрузим istioctl: `curl -sL https://istio.io/downloadIstioctl | sh -`{{execute}}

Экспортируем путь istioctl: `export PATH=$PATH:$HOME/.istioctl/bin`{{execute}}

Запустим установку Istio: `istioctl install --set meshConfig.accessLogFile=/dev/stdout --set meshConfig.outboundTrafficPolicy.mode=REGISTRY_ONLY`{{execute}}

Во время установки следует подтвердить намерение указав в терминале символ `y`{{copy}}

## Создание и конфигурация пространства имен

Для создания нового пространства имен dev-service-mesh выполним команду: `kubectl create namespace dev-service-mesh`{{execute}}

Внесем новоепространство в контекс Kubernetes: `kubectl config set-context --current --namespace=dev-service-mesh`{{execute}}

Активирум автоматическое внедрение контейнера с прокси-сервером Envoy в каждый создаваемый под в dev-service-mesh:

`kubectl label namespace dev-service-mesh istio-injection=enabled`{{execute}}

## Доступ к ingress-шлюзу Istio

Выполним команду:
`kubectl get svc istio-ingressgateway -n istio-system`{{execute}}

Экспортируем IP-адрес из этого ресурса в переменную:
`export INGRESS_HOST=$(kubectl -n istio-system get service istio-ingressgateway -o jsonpath='{.status.loadBalancer.ingress[0].ip}')`{{execute}}

То же самое сделаем для номера порта ingress-шлюза:
`export INGRESS_PORT=$(kubectl -n istio-system get service istio-ingressgateway -o jsonpath='{.spec.ports[?(@.name=="http2")].port}')`{{execute}}

Создадим переменную, содержащую извлеченные ранее данные:
`export GATEWAY_URL=$INGRESS_HOST:$INGRESS_PORT`{{execute}}

Таким образом, переменная GATEWAY_URL содержит адрес по которому можно совершить запрос в ingress-шлюз:
`echo $GATEWAY_URL`{{execute}}
