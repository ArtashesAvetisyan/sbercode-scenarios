До начала выполнения упражнения давайте подготови необходимую среду выполнения

## Запуск виртуальной машины

Перед началом запуска упражнения была развернута виртуальные машина, на которой был установлен Kubernetes и организован минимальный кластер из одного управляющего узла (control-plane), где был установлен Istio.

Давайте запустим Kubernetes и Istio виполнив скрипт:

`launch.sh`{{execute}}

До перехода к следующему шагу необходимо убедиться, что узел Kubernetes (node) находятся в состоянии Ready.

Для получения статуса, давайте выполним команду: `kubectl get nodes`{{execute}}

Необходимое состояние узла для перехода к следующему шагу приведено ниже:
```
NAME              STATUS   ROLES                  AGE     VERSION
*********         Ready    control-plane,master   8m54s   v1.21.2+k3s1
```

Если вы не наблюдаете подобного вывода, подождите 1-2 минуты и повторите попытку.

Также убедимся, что все существующие поды всех пространстве имен запущены и функционируют корректно: статус (STATUS) - Running, число рестартов (RESTARTS) - 0.

Выполним команду: `kubectl get pods --all-namespaces `{{execute}}

Дождитесь перехода статусов подов до Running.

## Конфигурация Istio

Выполним команду: `istioctl -c /etc/rancher/k3s/k3s.yaml install -y --set meshConfig.accessLogFile=/dev/stdout --set meshConfig.outboundTrafficPolicy.mode=REGISTRY_ONLY --set values.pilot.resources.requests.memory=128Mi --set values.pilot.resources.requests.cpu=50m --set values.global.proxy.resources.requests.cpu=10m --set values.global.proxy.resources.requests.memory=32Mi  --set values.global.proxy.resources.limits.memory=64Mi --set values.pilot.resources.limits.memory=256Mi`{{execute}}

В случае успеха, в выводе вышеприведенной команды должны быть строки:
```
✔ Istio core installed                                                        
✔ Istiod installed                                                            
✔ Ingress gateways installed                                                  
✔ Installation complete
```

## Создание и конфигурация пространства имен

Для создания нового пространства имен dev-service-mesh выполним команду: `kubectl create namespace dev-service-mesh`{{execute}}

Внесем новоепространство в контекс Kubernetes: `kubectl config set-context --current --namespace=dev-service-mesh`{{execute}}

Активируем автоматическое внедрение контейнера с прокси-сервером Envoy в каждый создаваемый под в dev-service-mesh:

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
