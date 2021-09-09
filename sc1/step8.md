На данный момент мы завершили создание манифестов, необходимых для достижения цели открытия входящего трафика в service mesh.

Для того чтобы апробировать путь входящих запросов в ServiceB, следует совершить запрос на адрес ingress-шлюза, который должен перенаправить поступивший запрос в адрес envoy-прокси в поде с бизнес сервисом, который в свою очередь направит запрос в ServecB. Этот сервис получив запрос, сформирует и отправит ответ по обратному пути.

Для начала запросим краткое описание манифеста Service ingress-шлюза, созданного при установке Istio:

`kubectl get svc istio-ingressgateway -n istio-system`{{execute}}

Экспортируем IP-адрес из этого ресурса в переменную:

`export INGRESS_HOST=$(kubectl -n istio-system get service istio-ingressgateway -o jsonpath='{.status.loadBalancer.ingress[0].ip}')`{{execute}}

То же самое сделаем для номера порта ingress-шлюза:

`export INGRESS_PORT=$(kubectl -n istio-system get service istio-ingressgateway -o jsonpath='{.spec.ports[?(@.name=="http2")].port}')`{{execute}}

Создадим переменную, содержащую извлеченные ранее данные:

`export GATEWAY_URL=$INGRESS_HOST:$INGRESS_PORT`{{execute}}

Таким образом, переменная GATEWAY_URL содержит адрес по которому можно совершить запрос в ingress-шлюз:

`echo $GATEWAY_URL`{{execute}}

Перед запросом убедимся в корректности статусов всех подов всех пространств имен:

`kubectl get pods —all-namespaces`{{execute}}

И наконец совершим GET запрос по адресу ingress-шлюза:

`curl -v http://$GATEWAY_URL/service-b`{{execute}}

В случае успеха в теле ответа мы должны видеть сообщение: `Hello from ServiceB!`

Проверим логи доступа Envoy ingress-шлюза:

`kubectl logs -l app=istio-ingressgateway -n istio-system -c istio-proxy`{{execute}}

Проверим логи доступа Envoy в поде с бизнес сервисом:

`kubectl logs -l app=service-b-app -c istio-proxy`{{execute}}